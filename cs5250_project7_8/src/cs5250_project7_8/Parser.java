package cs5250_project7_8;

import java.util.Scanner;

// Reference: https://github.com/thomas-stockx/nand2tetris/blob/master/projects/07/VMTranslator/src/com/stockxit/nand2tetris/Parser.java

public class Parser {
    private Scanner scanner;
    private String currentCommand;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public boolean hasMoreCommands() {
        return scanner.hasNextLine();
    }

    public void advance() {
        if (hasMoreCommands()) {
            currentCommand = scanner.nextLine();

            // strip out comment
            int commentIndex = currentCommand.indexOf("/");
            if (commentIndex >= 0) {
                currentCommand = currentCommand.substring(0, commentIndex);
            }
            currentCommand = currentCommand.trim();

            if (currentCommand.isEmpty()) {
                advance();
            }
        }
    }

    public CommandType commandType() {
        String command = currentCommand.split(" ")[0];
        switch (command) {
            case "push":
                return CommandType.C_PUSH;
            case "pop":
                return CommandType.C_POP;
            case "label":
                return CommandType.C_LABEL;
            case "goto":
                return CommandType.C_GOTO;
            case "if-goto":
                return CommandType.C_IF;
            case "function":
                return CommandType.C_FUNCTION;
            case "call":
                return CommandType.C_CALL;
            case "return":
                return CommandType.C_RETURN;
            default:
                return CommandType.C_ARITHMETIC;
        }
    }

    public String arg1() {
        if (commandType() == CommandType.C_RETURN) {
            return null;
        }

        if (commandType() == CommandType.C_ARITHMETIC) {
            return currentCommand;
        }

        return currentCommand.split(" ")[1];
    }

    public int arg2() {
        if (commandType() == CommandType.C_PUSH
                || commandType() == CommandType.C_POP
                || commandType() == CommandType.C_FUNCTION
                || commandType() == CommandType.C_CALL) {
            return Integer.valueOf(currentCommand.split(" ")[2]);
        }

        return 0;
    }
}


