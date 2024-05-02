package compiler.CodeGenerator;
import compiler.Parser.Starting;
import org.objectweb.asm.ClassWriter;
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
        stackTable.addVariable(identifier);
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
        if (assignment != null) {
            Assignment assignmentNode = new Assignment(assignment, assignment);
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
        method.getBlock().accept(codeGenerator);
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
        for (Node argument : functionCall.getArguments()) {
            argument.accept(this);
        }
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, "Main", functionCall.getIdentifier(), "()V", false);
    }

    public void println(String str) {
        mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mw.visitLdcInsn(str);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
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
