package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;
import compiler.Parser.*;

import java.util.Objects;

public class TypeVisitor {
    IdentifierTable table;
    public TypeVisitor() {
        table = new IdentifierTable();
    }

    public TypeVisitor(IdentifierTable parent) {
        this.table = new IdentifierTable(parent);
    }

    public Type visit(Node node) {
        return null;
    }

    public Type visit(Starting root) throws Exception {
        table = new IdentifierTable();
        Statements statements = root.getStatements();
        for (Statements.Statement statement : statements.statements) {
            statement.accept(this);
        }
        return null;
    }

    public Type visit(Statements.Statement statement) throws Exception {
        return statement.getContent().accept(this);
    }

    public Type visit(Declaration declaration) throws Exception {
        Type type = table.getType(declaration.getType().getValue());
        if (type == null) {
            SemanticAnalysis.SemanticException("TypeError","Type already declared", declaration);
        }
        if (declaration.getType().isVector()) {
            VarType varType = new VarType(type.getType().getValue());
            varType.setVectorDepth(declaration.getType().getVectorDepth());
            type = new UnaryType(varType);
        }
        if (!table.addIdentifier(declaration.getIdentifier(), type)) {
            SemanticAnalysis.SemanticException("TypeError","Identifier already declared", declaration);
        }

        if (declaration.getAssignment() != null) {
            Type assignment = declaration.getAssignment().accept(this);
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", declaration);
            }
            if (!Objects.equals(type, assignment)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", declaration);
            }
        }

        return type;
    }

    public Type visit(Struct struct) throws Exception {
        StructType structType = new StructType(struct, this);
        if (!table.addType(struct.getIdentifier(), structType)) {
            SemanticAnalysis.SemanticException("TypeError","Struct already declared", struct);
        }
        return structType;
    }

    public Type visit(Expression expression) {
        return null;
    }

    public Type visit(IdentifierAccess identifierAccess) throws Exception {
        String identifier = identifierAccess.getIdentifier();
        Type type = table.getIdentifier(identifier);
        if (type == null) {
            SemanticAnalysis.SemanticException("TypeError","Identifier not declared", identifierAccess);
        }
        if (identifierAccess.getNext() != null) {
            type =  identifierAccess.getNext().accept(this, type);
        }
        if (identifierAccess.getAssignment() != null) {
            Type assignment = identifierAccess.getAssignment().accept(this);
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", identifierAccess.getAssignment());
            }
            if (!Objects.equals(type, assignment)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", identifierAccess.getAssignment());
            }
        }
        return type;
    }

    public Type visit(IdentifierAccess.ArrayAccess arrayAccess, Type type) throws Exception {
        if (!type.getType().isVector()) {
            SemanticAnalysis.SemanticException("TypeError","Identifier is not a vector", arrayAccess);
        }
        Type indexType = arrayAccess.getIndex().accept(this);
        if (!Objects.equals(indexType.getType().getValue(), "int")) {
            SemanticAnalysis.SemanticException("TypeError","Index type is not int", arrayAccess);
        }
        if (arrayAccess.getNext() != null) {
            return arrayAccess.getNext().accept(this, type);
        }
        VarType varType = new VarType(type.getType().getValue());
        varType.setVectorDepth(type.getType().getVectorDepth() - 1);
        type = new UnaryType(varType);

        if (arrayAccess.getAssignment() != null) {
            Type assignmentType = arrayAccess.getAssignment().accept(this);
            if (assignmentType == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", arrayAccess.getAssignment());
            }
            if (!Objects.equals(type, assignmentType)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", arrayAccess.getAssignment());
            }
        }

        return type;
    }

    public Type visit(IdentifierAccess.StructAccess structAccess, Type type) throws Exception {
        // a[0].b
        if (!(type instanceof StructType)) {
            SemanticAnalysis.SemanticException("TypeError","Identifier is not a struct", structAccess);
        }
        StructType structType = (StructType) type;
        type = structType.getField(structAccess.getField());
        if (type == null) {
            SemanticAnalysis.SemanticException("TypeError","Field not declared", structAccess);
        }
        if (structAccess.getNext() != null) {
            return structAccess.getNext().accept(this, type);
        }
        if (structAccess.getAssignment() != null) {
            Type assignmentType = structAccess.getAssignment().accept(this);
            if (assignmentType == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", structAccess.getAssignment());
            }
            if (!Objects.equals(type, assignmentType)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", structAccess.getAssignment());
            }
        }

        return type;
    }

    public Type visit(IdentifierAccess.FunctionCall functionCall, Type type) {
        return null;
    }
}
