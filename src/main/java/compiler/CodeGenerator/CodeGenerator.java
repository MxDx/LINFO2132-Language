package compiler.CodeGenerator;
import compiler.Parser.Starting;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import compiler.Parser.*;



public class CodeGenerator {
    StackTable stackTable;
    ClassWriter cw;
    MethodVisitor mw;
    public CodeGenerator() {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Main", null, "java/lang/Object", null);
        mw = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mw.visitCode();
        stackTable = new StackTable();
    }

    public CodeGenerator(CodeGenerator parent) {
        this.stackTable = new StackTable(parent.stackTable);
        this.cw = parent.cw;
        this.mw = parent.mw;
    }
    public CodeGenerator(CodeGenerator parent,MethodVisitor mw) {
        this.stackTable = new StackTable(parent.stackTable);
        this.cw = parent.cw;
        this.mw = mw;
    }


    public void generateCode(Starting root) {

        Statements statements = root.getStatements();
        statements.accept(this);
        mw.visitInsn(Opcodes.RETURN);
        mw.visitEnd();
        mw.visitMaxs(-1, -1);
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        try (FileOutputStream outFile = new FileOutputStream("Main.class")) {
            outFile.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void generateCode(Statements statements) {
        for (Statements.Statement statement : statements.statements) {
            statement.accept(this);
        }
    }
    public void generateCode(Statements.Statement statement) {
        statement.content.accept(this);
    }

    public void generateCode(Block block) {
        block.getStatements().accept(this);
    }

    public void generateCode(Node node){
        throw new RuntimeException("Node Not implemented");
    }

    public void generateCode(Expression expression) {
        throw new RuntimeException("Expression Not implemented");
    }
    public void generateCode(Node node, String identifier) {
        throw new RuntimeException("Node Not implemented");
    }

    public void generateCode(Assignment assignment, String identifier) {
        Expression expression = (Expression) assignment.getExpression();
        expression.accept(this);
        switch (assignment.getNodeType()) {
            case "int":
                mw.visitVarInsn(Opcodes.ISTORE, stackTable.getVariable(identifier));
                break;
            case "float":
                mw.visitVarInsn(Opcodes.FSTORE, stackTable.getVariable(identifier));
                break;
            case "string":
                mw.visitVarInsn(Opcodes.ASTORE, stackTable.getVariable(identifier));
                break;
            default:
                throw new RuntimeException("Invalid type of assignment");
        }
    }
    public void generateCode(Expression.Value value) {
        switch (value.getValue().getType()) {
            case "int":
                mw.visitLdcInsn(Integer.parseInt(value.getValue().getValue()));
                break;
            case "float":
                mw.visitLdcInsn(Float.parseFloat(value.getValue().getValue()));
                break;
            case "string":
                mw.visitLdcInsn(value.getValue().getValue());
                break;
        }
    }
    public void generateCode(Declaration declaration) {
        Expression assignment = (Expression) declaration.getAssignment();
        stackTable.addVariableType(declaration.getIdentifier(), declaration.getType().getValue());
        stackTable.addVariable(declaration.getIdentifier());
        if (assignment != null) {
            Assignment assignmentNode = new Assignment(assignment, assignment);
            assignmentNode.setType(declaration.getNodeType());
            assignmentNode.accept(this, declaration.getIdentifier());
        }
    }
    public void generateCode(Method method) {
        StringBuilder descriptor = new StringBuilder("(");
        for (Parameter parameter : method.getParameters()) {
            descriptor.append(getJavaType(parameter.getType().getValue()));
        }
        descriptor.append(")");
        descriptor.append(getJavaType(method.getReturnType().getValue()));
        MethodVisitor new_mw = cw.visitMethod(Opcodes.ACC_PUBLIC, method.getName().getValue(), descriptor.toString(), null, null);
        CodeGenerator codeGenerator = new CodeGenerator(this, new_mw);
        for (Parameter parameter : method.getParameters()) {
            stackTable.addVariableType(parameter.getIdentifier(), parameter.getType().getValue());
            stackTable.addVariable(parameter.getIdentifier());
        }
        if (method.getBlock() != null) {
            method.getBlock().accept(codeGenerator);
        }
        new_mw.visitInsn(Opcodes.RETURN);
        new_mw.visitEnd();
        new_mw.visitMaxs(-1, -1);
            
    }
    public void generateCode(Return returnStatement) {
        Expression expression = (Expression) returnStatement.getExpression();
        if (expression == null) {
            mw.visitInsn(Opcodes.RETURN);
            return;
        }
        expression.accept(this);
        switch (expression.getNodeType()) {
            case "int":
                mw.visitInsn(Opcodes.IRETURN);
                break;
            case "float":
                mw.visitInsn(Opcodes.FRETURN);
                break;
            case "string":
                mw.visitInsn(Opcodes.ARETURN);
                break;
        }
    }
    public void generateCode(IdentifierAccess.FunctionCall functionCall) {
        switch (functionCall.getIdentifier()) {
            case "writeString", "write", "writeln":
                mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                functionCall.getArguments().get(0).accept(this);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                break;
            case "writeInt":
                mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                functionCall.getArguments().get(0).accept(this);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
                break;
            case "writeFloat":
                mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                functionCall.getArguments().get(0).accept(this);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
                break;
            default:
                for (Node argument : functionCall.getArguments()) {
                    argument.accept(this);
                }
                mw.visitMethodInsn(Opcodes.INVOKESTATIC, "Main", functionCall.getIdentifier(), "()V", false);

        }
    }
    public void generateCode(IdentifierAccess identifierAccess) {
        if (identifierAccess.getNext() != null) {
            identifierAccess.getNext().accept(this);
        } else if (identifierAccess.getAssignment() != null) {
            identifierAccess.getAssignment().accept(this, identifierAccess.getIdentifier());
        } else {
            int slot = stackTable.getVariable(identifierAccess.getIdentifier());
            switch (stackTable.getType(identifierAccess.getIdentifier())) {
                case "int":
                    mw.visitVarInsn(Opcodes.ILOAD, slot);
                    break;
                case "float":
                    mw.visitVarInsn(Opcodes.FLOAD, slot);
                    break;
                case "string":
                    mw.visitVarInsn(Opcodes.ALOAD, slot);
                    break;
            }
        }
    }

    public void loadOnStack(Expression.Operation operation) {
        String leftType = operation.getTypeLeft();
        String rightType = operation.getTypeRight();
        if (leftType != rightType) {
            if (leftType.equals("int") && rightType.equals("float")) {
                operation.getLeft().accept(this);
                mw.visitInsn(Opcodes.I2F);
                operation.getRight().accept(this);
            } else if (leftType.equals("float") && rightType.equals("int")) {
                operation.getRight().accept(this);
                mw.visitInsn(Opcodes.I2F);
                operation.getLeft().accept(this);
            } else {
                throw new RuntimeException("Invalid casting");
            }
        } else {
            operation.getLeft().accept(this);
            operation.getRight().accept(this);
        }
    }

    public void generateCode(Expression.Operation operation) {
        // Check for casting
        String finalType = operation.getNodeType();
        loadOnStack(operation);

        switch (operation.getOperator()) {
            case "+":
                switch (finalType) {
                    case "int":
                        mw.visitInsn(Opcodes.IADD);
                        break;
                    case "float":
                        mw.visitInsn(Opcodes.FADD);
                        break;
                    default:
                        throw new RuntimeException("String addition not yet implemented");
                }
                break;
            case "-":
                switch (finalType) {
                    case "int":
                        mw.visitInsn(Opcodes.ISUB);
                        break;
                    case "float":
                        mw.visitInsn(Opcodes.FSUB);
                        break;
                }
                break;
            case "*":
                switch (finalType) {
                    case "int":
                        mw.visitInsn(Opcodes.IMUL);
                        break;
                    case "float":
                        mw.visitInsn(Opcodes.FMUL);
                        break;
                }
                break;
            case "/":
                switch (finalType) {
                    case "int":
                        mw.visitInsn(Opcodes.IDIV);
                        break;
                    case "float":
                        mw.visitInsn(Opcodes.FDIV);
                        break;
                }
                break;
            case "%":
                switch (finalType) {
                    case "int":
                        mw.visitInsn(Opcodes.IREM);
                        break;
                    case "float":
                        throw new RuntimeException("Modulo operation not supported for float");
                }
                break;
        }
    }

    public int generateCode(Expression.ComparisonOperation comparisonOperation) {
        // Check for casting
        String finalType = comparisonOperation.getNodeType();
        loadOnStack(comparisonOperation);

        switch (comparisonOperation.getOperator()) {
            case "==":
                switch (finalType) {
                    case "int":
                        return Opcodes.IF_ICMPNE;
                    case "float":
                        // If left - right = 0, then they are equal
                        mw.visitInsn(Opcodes.FSUB);
                        mw.visitInsn(Opcodes.F2I);
                        return Opcodes.IFNE;
                    case "string":
                        return Opcodes.IF_ACMPNE;
                    case "bool":
                        return Opcodes.IF_ICMPNE;
                }
                break;
            case "!=":
                switch (finalType) {
                    case "int":
                        return Opcodes.IF_ICMPEQ;
                    case "float":
                        return Opcodes.IF_ACMPEQ;
                    case "string":
                        return Opcodes.IF_ACMPEQ;
                    case "bool":
                        return Opcodes.IF_ICMPEQ;
                }
                break;
            case "<":
                switch (finalType) {
                    case "int":
                        return Opcodes.IF_ICMPGE;
                    case "float":
                        throw new RuntimeException("Float comparison not supported");
                }
                break;
            case ">":
                switch (finalType) {
                    case "int":
                        return Opcodes.IF_ICMPLE;
                    case "float":
                        throw new RuntimeException("Float comparison not supported");
                }
                break;
            case "<=":
                switch (finalType) {
                    case "int":
                        return Opcodes.IF_ICMPGT;
                    case "float":
                        throw new RuntimeException("Float comparison not supported");
                }
                break;
            case ">=":
                switch (finalType) {
                    case "int":
                        return Opcodes.IF_ICMPLT;
                    case "float":
                        throw new RuntimeException("Float comparison not supported");
                }
                break;
            default:
                throw new RuntimeException("Invalid comparison operator");
        }
        throw new RuntimeException("Invalid comparison operator");
    }

    public int generateCode(Expression.LogicalOperation logicalOperation) {
        logicalOperation.getLeft().accept(this);
        logicalOperation.getRight().accept(this);
        switch (logicalOperation.getOperator()) {
            case "&&":
                mw.visitInsn(Opcodes.IAND);
                break;
            case "||":
                mw.visitInsn(Opcodes.IOR);
                break;
        }
        return Opcodes.IFEQ;
    }

    public void generateCode(While whileStatement) {
        StackTable oldStackTable = stackTable;
        stackTable = new StackTable(oldStackTable);
        Label start = new Label();
        Label end = new Label();
        mw.visitLabel(start);
        int OpCode = generateCode((Expression.ComparisonOperation) whileStatement.getExpression());
        mw.visitJumpInsn(OpCode, end);
        whileStatement.getBlock().accept(this);
        mw.visitJumpInsn(Opcodes.GOTO, start);
        mw.visitLabel(end);
        stackTable = oldStackTable;
    }

    public void generateCode(If ifStatement) {
        Label end = new Label();
        Label elseLabel = new Label();
        int OpCode = generateCode((Expression.ComparisonOperation) ifStatement.getExpression());
        if (ifStatement.getElseStatement() != null) {
            mw.visitJumpInsn(OpCode, elseLabel);
            ifStatement.getBlock().accept(this);
            mw.visitJumpInsn(Opcodes.GOTO, end);
            mw.visitLabel(elseLabel);
            ifStatement.getElseStatement().accept(this);
            mw.visitLabel(end);
        } else {
            mw.visitJumpInsn(OpCode, end);
            ifStatement.getBlock().accept(this);
            mw.visitLabel(end);
        }
    }


    public String getJavaType(String type) {
        switch (type) {
            case "int":
                return "I";
            case "float":
                return "F";
            case "string":
                return "Ljava/lang/String;";
            case "void":
                return "V";
            case "bool":
                return "Z";
            default:
                throw new RuntimeException("Invalid Type");
        }
    }
}
