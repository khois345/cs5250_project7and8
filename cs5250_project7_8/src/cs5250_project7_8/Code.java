package cs5250_project7_8;

import java.io.*;

// Reference: https://github.com/thomas-stockx/nand2tetris/blob/master/projects/07/VMTranslator/src/com/stockxit/nand2tetris/CodeWriter.java

public class Code {
    private PrintWriter writer;
    private String fileName;
    private int labelCounter = 0;

    public Code(String outputPath) {
        try {
            writer = new PrintWriter(new FileWriter(outputPath));
        } catch (IOException e) {
            System.err.println("Failed to open output file: " + outputPath);
            e.printStackTrace();
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName.substring(0, fileName.lastIndexOf("."));
    }

    public void writeArithmetic(String command) {
    	// Write comment for readability
        writer.println("// " + command);
        
        switch (command) {
            case "add" -> writeBinary("M=D+M");
            case "sub" -> writeBinary("M=M-D");
            case "neg" -> writeUnary("M=-M");
            case "eq" -> writeComparison("JEQ");
            case "gt" -> writeComparison("JGT");
            case "lt" -> writeComparison("JLT");
            case "and" -> writeBinary("M=D&M");
            case "or"  -> writeBinary("M=D|M");
            case "not" -> writeUnary("M=!M");
        }
    }

    public void writePushPop(CommandType type, String segment, int index) {
    	// Write comment for readability
        writer.println("// " + type + " " + segment + " " + index);
        
        if (type == CommandType.C_PUSH) {
            handlePush(segment, index);
        } else {
            handlePop(segment, index);
        }
    }

    private String getBaseLabel(String segment) {
        return switch (segment) {
            case "local"    -> "LCL";
            case "argument" -> "ARG";
            case "this"     -> "THIS";
            case "that"     -> "THAT";
            default         -> null;
        };
    }

    private void handlePush(String segment, int index) {
        switch (segment) {
            case "constant":
                // D = index
                writer.println("@" + index);
                writer.println("D=A");
                break;

            case "local":
            case "argument":
            case "this":
            case "that":
            	// Write comment for readability
                writer.println("// push " + segment + " " + index + " => D=M[base+i]");
                
                // base pointer -> D
                writer.println("@" + getBaseLabel(segment));
                writer.println("D=M");
                // add index
                writer.println("@" + index);
                writer.println("A=D+A");
                writer.println("D=M"); // D = RAM[base + i]
                break;

            case "temp":
                // R5 + index
                writer.println("@" + (5 + index));
                writer.println("D=M");
                break;

            case "pointer":
                // pointer 0 -> R3, pointer 1 -> R4
                writer.println("@" + (3 + index));
                writer.println("D=M");
                break;

            case "static":
                writer.println("@" + fileName + "." + index);
                writer.println("D=M");
                break;
        }

        // push -> *SP = D; 
        // SP++
        writer.println("@SP");
        writer.println("A=M");
        writer.println("M=D");
        writer.println("@SP");
        writer.println("M=M+1");
    }

    private void handlePop(String segment, int index) {
        switch (segment) {
            case "local":
            case "argument":
            case "this":
            case "that": {
            	// Write comment for readability
                writer.println("// pop " + segment + " " + index + " => M[base+i] = pop()");
                
                // base pointer -> D
                writer.println("@" + getBaseLabel(segment));
                writer.println("D=M");
                // add index
                writer.println("@" + index);
                writer.println("D=D+A");
                // store address in R13
                writer.println("@R13");
                writer.println("M=D");

                // pop => D
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");

                // RAM[R13] = D
                writer.println("@R13");
                writer.println("A=M");
                writer.println("M=D");
                break;
            }
            case "temp":
                // pop -> R5 + index
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@" + (5 + index));
                writer.println("M=D");
                break;

            case "pointer":
                // pointer -> R3 or R4
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@" + (3 + index));
                writer.println("M=D");
                break;

            case "static":
                // pop -> fileName.i
                writer.println("@SP");
                writer.println("AM=M-1");
                writer.println("D=M");
                writer.println("@" + fileName + "." + index);
                writer.println("M=D");
                break;

            case "constant":
                // no pop constant
                break;
        }
    }

    private void writeBinary(String operation) {
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
        writer.println("A=A-1");
        writer.println(operation);
    }

    private void writeUnary(String op) {
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println(op);
    }

    private void writeComparison(String jump) {
        String trueLabel = "LABEL_TRUE_" + labelCounter;
        String endLabel = "LABEL_END_" + labelCounter;
        labelCounter++;

        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
        writer.println("A=A-1");
        writer.println("D=M-D");
        writer.println("@" + trueLabel);
        writer.println("D;" + jump);
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=0");
        writer.println("@" + endLabel);
        writer.println("0;JMP");
        writer.println("(" + trueLabel + ")");
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=-1");
        writer.println("(" + endLabel + ")");
    }

    public void writeLabel(String label) {
        writer.println("(" + label + ")");
    }

    public void writeGoto(String label) {
        writer.println("@" + label);
        writer.println("0;JMP");
    }

    public void writeIf(String label) {
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
        writer.println("@" + label);
        writer.println("D;JNE");
    }

    public void writeFunction(String functionName, int numLocals) {
        writer.println("(" + functionName + ")");
        for (int i = 0; i < numLocals; i++) {
            writePushPop(CommandType.C_PUSH, "constant", 0);
        }
    }
    
    // Project 8
    public void writeCall(String functionName, int numArgs) {
        writer.println("// call " + functionName + " " + numArgs);

        // Generate return address label
        String returnLabel = "RETURN_LABEL_" + labelCounter++;
        
        // push return-address
        writer.println("@" + returnLabel);
        writer.println("D=A");
        pushDToStack();

        writer.println("@LCL");
        writer.println("D=M");
        pushDToStack();

        writer.println("@ARG");
        writer.println("D=M");
        pushDToStack();

        writer.println("@THIS");
        writer.println("D=M");
        pushDToStack();

        writer.println("@THAT");
        writer.println("D=M");
        pushDToStack();

        // ARG = SP - numArgs - 5
        writer.println("@SP");
        writer.println("D=M");
        writer.println("@" + (numArgs + 5));
        writer.println("D=D-A");
        writer.println("@ARG");
        writer.println("M=D");

        // LCL = SP
        writer.println("@SP");
        writer.println("D=M");
        writer.println("@LCL");
        writer.println("M=D");

        // goto functionName
        writer.println("@" + functionName);
        writer.println("0;JMP");

        writer.println("(" + returnLabel + ")");
    }

    public void writeReturn() {
        writer.println("// return");

        // endFrame = LCL
        writer.println("@LCL");
        writer.println("D=M");
        writer.println("@R13"); // endFrame => R13
        writer.println("M=D");

        // retAddr = *(endFrame - 5)
        writer.println("@5");
        writer.println("A=D-A");  // A = endFrame - 5
        writer.println("D=M");
        writer.println("@R14");   // retAddr => R14
        writer.println("M=D");

        // *ARG = pop -> reposition return value for caller
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
        writer.println("@ARG");
        writer.println("A=M");
        writer.println("M=D");

        // SP = ARG + 1
        writer.println("@ARG");
        writer.println("D=M+1");
        writer.println("@SP");
        writer.println("M=D");

        // THAT = *(endFrame - 1)
        restoreSegment("THAT", 1);

        // THIS = *(endFrame - 2)
        restoreSegment("THIS", 2);

        // ARG = *(endFrame - 3)
        restoreSegment("ARG", 3);

        // LCL = *(endFrame - 4)
        restoreSegment("LCL", 4);

        // goto retAddr
        writer.println("@R14");
        writer.println("A=M");
        writer.println("0;JMP");
    }
    
    // Helper functions
    private void pushDToStack() {
        writer.println("@SP");
        writer.println("A=M");
        writer.println("M=D");
        writer.println("@SP");
        writer.println("M=M+1");
    }
    
    private void restoreSegment(String segment, int offset) {
        writer.println("@R13");    // endFrame
        writer.println("D=M");
        writer.println("@" + offset);
        writer.println("A=D-A");   // A = endFrame - offset
        writer.println("D=M");     // D = RAM[endFrame - offset]
        writer.println("@" + segment);
        writer.println("M=D");
    }
    
    public void writeBootstrap() {
        // Sets SP = 256
        writer.println("// Bootstrap code");
        writer.println("@256");
        writer.println("D=A");
        writer.println("@SP");
        writer.println("M=D");

        // Call Sys.init with 0 arguments
        writeCall("Sys.init", 0);
    }

    public void close() {
        writer.close();
    }
}


