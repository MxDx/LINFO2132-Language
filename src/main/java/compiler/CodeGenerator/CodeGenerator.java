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
    HashMap<String,String> functionTable;
    ClassWriter cw;
    MethodVisitor mw;
    String className;
    String fileName;
    boolean firstOr = false;
    public CodeGenerator() {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Main", null, "java/lang/Object", null);
        mw = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mw.visitCode();
        stackTable = new StackTable();
        functionTable = new HashMap<>();
    }
    public CodeGenerator(String fileName){
        this.fileName = fileName;
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        this.className = fileName.substring(0, fileName.lastIndexOf('.'));
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, this.className, null, "java/lang/Object", null);
        mw = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mw.visitCode();
        stackTable = new StackTable();
        functionTable = new HashMap<>();
    }

    public CodeGenerator(CodeGenerator parent) {
        this.stackTable = new StackTable(parent.stackTable);
        functionTable = new HashMap<>(parent.functionTable);
        this.cw = parent.cw;
        this.mw = parent.mw;
    }
    public CodeGenerator(CodeGenerator parent,MethodVisitor mw) {
        this.stackTable = new StackTable(parent.stackTable);
        functionTable = new HashMap<>(parent.functionTable);
        this.cw = parent.cw;
        this.mw = mw;
    }


    public int generateCode(Starting root) {

        Statements statements = root.getStatements();
        statements.accept(this);
        mw.visitInsn(Opcodes.RETURN);
        mw.visitEnd();
        mw.visitMaxs(-1, -1);
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        try (FileOutputStream outFile = new FileOutputStream(fileName)) {
            outFile.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Opcodes.NOP;
    }
    public int generateCode(Statements statements) {
        for (Statements.Statement statement : statements.statements) {
            statement.accept(this);
        }
        return Opcodes.NOP;
    }
    public int generateCode(Statements.Statement statement) {
        return statement.content.accept(this);
    }

    public int generateCode(Block block) {
       return  block.getStatements().accept(this);
    }

    public int generateCode(Node node){
        throw new RuntimeException("Node Not implemented");
    }

    public int generateCode(Expression expression) {
        throw new RuntimeException("Expression Not implemented");
    }
    public int generateCode(Node node, String identifier) {
        throw new RuntimeException("Node Not implemented");
    }

    public int generateCode(Assignment assignment, String identifier) {
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
        return Opcodes.NOP;
    }
    public int generateCode(Expression.Value value) {
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
        return Opcodes.NOP;
    }
    public int generateCode(Declaration declaration) {
        Expression assignment = (Expression) declaration.getAssignment();
        stackTable.addVariableType(declaration.getIdentifier(), declaration.getType().getValue());
        stackTable.addVariable(declaration.getIdentifier());
        if (assignment != null) {
            Assignment assignmentNode = new Assignment(assignment, assignment);
            assignmentNode.setType(declaration.getNodeType());
            assignmentNode.accept(this, declaration.getIdentifier());
        }
        return Opcodes.NOP;
    }
    public int generateCode(Method method) {
        StringBuilder descriptor = new StringBuilder("(");
        for (Parameter parameter : method.getParameters()) {
            descriptor.append(getJavaType(parameter.getType().getValue()));
        }
        descriptor.append(")");
        descriptor.append(getJavaType(method.getReturnType().getValue()));
        MethodVisitor new_mw = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, method.getName().getValue(), descriptor.toString(), null, null);
        functionTable.put(method.getName().getValue(),descriptor.toString());
        CodeGenerator newCodeGenerator = new CodeGenerator(this, new_mw);
        int indexParameter = 0;
        for (Parameter parameter : method.getParameters()) {
            newCodeGenerator.stackTable.addVariableType(parameter.getIdentifier(), parameter.getType().getValue());
            newCodeGenerator.stackTable.addVariable(parameter.getIdentifier());
            int slot = newCodeGenerator.stackTable.getVariable(parameter.getIdentifier());
            switch (parameter.getType().getValue()) {
                case "int":
                    new_mw.visitVarInsn(Opcodes.ILOAD, indexParameter);
                    new_mw.visitVarInsn(Opcodes.ISTORE, slot);
                    break;
                case "float":
                    new_mw.visitVarInsn(Opcodes.FLOAD, indexParameter);
                    new_mw.visitVarInsn(Opcodes.FSTORE, slot);
                    break;
                case "string":
                    new_mw.visitVarInsn(Opcodes.ALOAD, indexParameter);
                    new_mw.visitVarInsn(Opcodes.ASTORE, slot);
                    break;
            }
            indexParameter++;
        }
        if (method.getBlock() != null) {
            method.getBlock().accept(newCodeGenerator);
        }
        new_mw.visitInsn(Opcodes.RETURN);
        new_mw.visitEnd();
        new_mw.visitMaxs(-1, -1);
        return Opcodes.NOP;
    }
    public int generateCode(Return returnStatement) {
        Expression expression = (Expression) returnStatement.getExpression();
        if (expression == null) {
            mw.visitInsn(Opcodes.RETURN);
            return Opcodes.NOP;
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
        return Opcodes.NOP;
    }

    public int generateCode(IdentifierAccess.FunctionCall functionCall) {
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
                mw.visitMethodInsn(Opcodes.INVOKESTATIC, this.className, functionCall.getIdentifier(), functionTable.get(functionCall.getIdentifier()), false);

        }
        return Opcodes.NOP;
    }
    public int generateCode(IdentifierAccess identifierAccess) {
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
        return Opcodes.NOP;
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

    public int generateCode(Expression.Operation operation, Label start, Label end) {
        return Opcodes.NOP;
    }

    public int generateCode(Expression.ArithmeticOperation operation, Label start , Label end) {
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
        return Opcodes.NOP;
    }

    public int generateCode(Expression.ComparisonOperation comparisonOperation, Label start , Label end) {
        // Check for casting
        String finalType = comparisonOperation.getNodeType();
        loadOnStack(comparisonOperation);

        switch (comparisonOperation.getOperator()) {
            case "==":
                switch (finalType) {
                    case "int":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ICMPEQ, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ICMPNE, end);
                        }
                        return Opcodes.NOP;
                    case "float":
                        // If left - right = 0, then they are equal
                        mw.visitInsn(Opcodes.FSUB);
                        mw.visitInsn(Opcodes.F2I);
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IFEQ, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IFNE, end);
                        }
                        return Opcodes.IFNE;
                    case "string":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ACMPEQ, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ACMPNE, end);
                        }
                        return Opcodes.IF_ACMPNE;
                    case "bool":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ICMPEQ, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ICMPNE, end);
                        }
                        return Opcodes.IF_ICMPNE;
                }
                break;
            case "!=":
                switch (finalType) {
                    case "int", "bool":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ICMPNE, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ICMPEQ, end);
                        }
                        return Opcodes.IF_ICMPEQ;
                    case "float":
                        mw.visitInsn(Opcodes.FSUB);
                        mw.visitInsn(Opcodes.F2I);
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IFNE, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IFEQ, end);
                        }
                        return Opcodes.IFEQ;
                    case "string":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ACMPNE, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ACMPEQ, end);
                        }
                        return Opcodes.IF_ACMPEQ;
                }
                break;
            case "<":
                switch (finalType) {
                    case "int":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ICMPLT, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ICMPGE, end);
                        }
                        return Opcodes.IF_ICMPGE;
                    case "float":
                        mw.visitInsn(Opcodes.FCMPG);
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IFLT, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IFGE, end);
                        }
                        return Opcodes.IFGE;
                }
                break;
            case ">":
                switch (finalType) {
                    case "int":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ICMPGT, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ICMPLE, end);
                        }
                        return Opcodes.IF_ICMPLE;
                    case "float":
                        mw.visitInsn(Opcodes.FCMPL);
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IFGT, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IFLE, end);
                        }
                        return Opcodes.IFLE;
                        //throw new RuntimeException("Float comparison not supported");
                }
                break;
            case "<=":
                switch (finalType) {
                    case "int":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ICMPLE, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ICMPGT, end);
                        }
                        return Opcodes.IF_ICMPGT;
                    case "float":
                        mw.visitInsn(Opcodes.FCMPG);
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IFLE, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IFGT, end);
                        }
                        return Opcodes.IFGT;
                }
                break;
            case ">=":
                switch (finalType) {
                    case "int":
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IF_ICMPGE, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IF_ICMPLT, end);
                        }
                        return Opcodes.IF_ICMPLT;
                    case "float":
                        mw.visitInsn(Opcodes.FCMPL);
                        if (firstOr) {
                            mw.visitJumpInsn(Opcodes.IFGE, start);
                        } else {
                            mw.visitJumpInsn(Opcodes.IFLT, end);
                        }
                        return Opcodes.IFLT;
                }
                break;
            default:
                throw new RuntimeException("Invalid comparison operator");
        }
        throw new RuntimeException("Invalid comparison operator");
    }

    public int generateCode(Expression.LogicalOperation logicalOperation, Label start , Label end) {
        Expression.Operation left;
        Expression.Operation right;
        switch (logicalOperation.getOperator()) {
            case "&&":
                Label new_start = new Label();
                left = (Expression.Operation) logicalOperation.getLeft();
                firstOr = false;
                left.accept(this, new_start, end);
                mw.visitLabel(new_start);
                right = (Expression.Operation) logicalOperation.getRight();
                firstOr = false;
                right.accept(this, start, end);
                break;
            case "||":
                Label new_end = new Label();
                left = (Expression.Operation) logicalOperation.getLeft();
                firstOr = true;
                left.accept(this, start, new_end);
                mw.visitLabel(new_end);
                right = (Expression.Operation) logicalOperation.getRight();
                firstOr = false;
                right.accept(this, start, end);
                break;
        }
        return Opcodes.NOP;
    }

    public int generateCode(While whileStatement) {
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
        return Opcodes.NOP;
    }
    public int generateCode(If ifStatement) {
        Label start = new Label();
        Label end = new Label();
        Label elseLabel = new Label();
        //int OpCode = ifStatement.getExpression().accept(this);
        if (ifStatement.getElseStatement() != null) {
            //mw.visitJumpInsn(OpCode, elseLabel);
            Expression expression = (Expression) ifStatement.getExpression();
            expression.accept(this, start, elseLabel);
            mw.visitLabel(start);
            ifStatement.getBlock().accept(this);
            mw.visitJumpInsn(Opcodes.GOTO, end);
            mw.visitLabel(elseLabel);
            ifStatement.getElseStatement().accept(this);
            mw.visitLabel(end);
        } else {
            //mw.visitJumpInsn(OpCode, end);
            Expression expression = (Expression) ifStatement.getExpression();
            expression.accept(this, start, end);
            mw.visitLabel(start);
            ifStatement.getBlock().accept(this);
            mw.visitLabel(end);
        }
        return Opcodes.NOP;
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
