// C_PUSH constant 10
@10
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_POP local 0
// pop local 0 => M[base+i] = pop()
@LCL
D=M
@0
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// C_PUSH constant 21
@21
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_PUSH constant 22
@22
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_POP argument 2
// pop argument 2 => M[base+i] = pop()
@ARG
D=M
@2
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// C_POP argument 1
// pop argument 1 => M[base+i] = pop()
@ARG
D=M
@1
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// C_PUSH constant 36
@36
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_POP this 6
// pop this 6 => M[base+i] = pop()
@THIS
D=M
@6
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// C_PUSH constant 42
@42
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_PUSH constant 45
@45
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_POP that 5
// pop that 5 => M[base+i] = pop()
@THAT
D=M
@5
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// C_POP that 2
// pop that 2 => M[base+i] = pop()
@THAT
D=M
@2
D=D+A
@R13
M=D
@SP
AM=M-1
D=M
@R13
A=M
M=D
// C_PUSH constant 510
@510
D=A
@SP
A=M
M=D
@SP
M=M+1
// C_POP temp 6
@SP
AM=M-1
D=M
@11
M=D
// C_PUSH local 0
// push local 0 => D=M[base+i]
@LCL
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// C_PUSH that 5
// push that 5 => D=M[base+i]
@THAT
D=M
@5
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
AM=M-1
D=M
A=A-1
M=D+M
// C_PUSH argument 1
// push argument 1 => D=M[base+i]
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// C_PUSH this 6
// push this 6 => D=M[base+i]
@THIS
D=M
@6
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// C_PUSH this 6
// push this 6 => D=M[base+i]
@THIS
D=M
@6
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
AM=M-1
D=M
A=A-1
M=D+M
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// C_PUSH temp 6
@11
D=M
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
AM=M-1
D=M
A=A-1
M=D+M
