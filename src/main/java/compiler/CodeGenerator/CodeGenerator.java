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
    MethodVisitor mv;
    public CodeGenerator() {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Main", null, "java/lang/Object", null);
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        stackTable = new StackTable();
    }

    public CodeGenerator(CodeGenerator parent) {
        this.stackTable = new StackTable(parent.stackTable);
        this.cw = parent.cw;
        this.mv = parent.mv;
    }

    public void generateCode(Starting root) {

        Statements statements = root.getStatements();
        statements.accept(this);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitEnd();
        mv.visitMaxs(-1, -1);
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();
        try (FileOutputStream outFile = new FileOutputStream("Output.class")) {
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
    public void generateCode(Node node, String identifier){
        throw new RuntimeException("Node Not implemented");
    }
    public void generateCode(Expression expression, String identifier) {
        throw new RuntimeException("Expression Not implemented");
    }
    public void generateCode(Assignment assignment, String identifier) {
        Expression expression = (Expression) assignment.getExpression();
        expression.accept(this, identifier);
    }
    public void generateCode(Expression.Value value, String identifier) {
        switch (value.getValue().getType()) {
            case "int":
                stackTable.addVariable(identifier);
                mv.visitLdcInsn(Integer.parseInt(value.getValue().getValue()));
                mv.visitVarInsn(Opcodes.ISTORE, stackTable.getVariable(identifier));
                break;
            case "float":
                stackTable.addVariable(identifier);
                mv.visitLdcInsn(Float.parseFloat(value.getValue().getValue()));
                mv.visitVarInsn(Opcodes.FSTORE, stackTable.getVariable(identifier));
                break;
            case "string":
                stackTable.addVariable(identifier);
                mv.visitLdcInsn(value.getValue().getValue());
                mv.visitVarInsn(Opcodes.ASTORE, stackTable.getVariable(identifier));
                break;
        }
    }
    public void generateCode(Declaration declaration) {
        if (declaration.getAssignment() != null) {
            declaration.getAssignment().accept(this, declaration.getIdentifier());
        }
    }
}
