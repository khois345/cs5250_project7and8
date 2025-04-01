package cs5250_project7_8;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    private static Code code;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== VM Translator ===");

        while (true) {
            System.out.println("Please enter the path to a .vm file or folder (or type '/exit' to quit): ");
            String inputPath = scanner.nextLine().trim();

            if (inputPath.equalsIgnoreCase("/exit")) {
                System.out.println("Exiting translator.");
                scanner.close();
                break;
            }

            File inputFile = new File(inputPath);
            if (!inputFile.isAbsolute()) {
                File projectRoot = new File(System.getProperty("user.dir"));
                inputPath = inputPath.startsWith("/") ? inputPath.substring(1) : inputPath;
                inputFile = new File(projectRoot, inputPath);
            }

            if (!inputFile.exists()) {
                System.out.println("File or folder does not exist.");
                continue;
            }

            try {
                code = new Code(getOutputFilePath(inputFile));

                if (inputFile.isDirectory()) {
                    for (File vmFile : inputFile.listFiles((dir, name) -> name.endsWith(".vm"))) {
                        processFile(vmFile);
                    }
                } else {
                    processFile(inputFile);
                }

                code.close();
                System.out.println("Translation successful! Output is written to: " + getOutputFilePath(inputFile));

            } catch (Exception e) {
                System.err.println("Error occurred:");
                e.printStackTrace();
            }
        }
    }

    private static void processFile(File file) throws FileNotFoundException {
        Scanner fileScanner = new Scanner(file);
        Parser parser = new Parser(fileScanner);
        code.setFileName(file.getName());

        while (parser.hasMoreCommands()) {
            parser.advance();
            CommandType type = parser.commandType();
            if (type == null) continue;

            switch (type) {
                case C_PUSH:
                case C_POP:
                    code.writePushPop(type, parser.arg1(), parser.arg2());
                    break;
                case C_ARITHMETIC:
                    code.writeArithmetic(parser.arg1());
                    break;
                case C_LABEL:
                    code.writeLabel(parser.arg1());
                    break;
                case C_GOTO:
                    code.writeGoto(parser.arg1());
                    break;
                case C_IF:
                    code.writeIf(parser.arg1());
                    break;
                case C_FUNCTION:
                    code.writeFunction(parser.arg1(), parser.arg2());
                    break;
                case C_CALL:
                    code.writeCall(parser.arg1(), parser.arg2());
                    break;
                case C_RETURN:
                    code.writeReturn();
                    break;
                default:
                    break;
            }
        }
    }

    private static String getOutputFilePath(File input) {
        if (input.isDirectory()) {
            return new File(input, input.getName() + ".asm").getAbsolutePath();
        } else {
            return input.getAbsolutePath().replace(".vm", ".asm");
        }
    }
}
