package compiler.SemanticAnalysis;

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

    public IdentifierTable getTable() {
        return table;
    }

    public IdentifierType visit(Node node) {
        return null;
    }

    public IdentifierType visit(Starting root) throws Exception {
        table = new IdentifierTable();
        Statements statements = root.getStatements();
        for (Statements.Statement statement : statements.statements) {
            statement.accept(this);
        }
        return null;
    }

    public IdentifierType visit(Statements.Statement statement) throws Exception {
        return statement.getContent().accept(this);
    }

    public IdentifierType visit(Declaration declaration) throws Exception {
        Type type = table.getType(declaration.getType().getValue());
        if (type == null) {
            SemanticAnalysis.SemanticException("TypeError","Type already declared", declaration);
        }
        IdentifierType identifierType = new IdentifierType(type, declaration.getType());
        if (!table.addIdentifier(declaration.getIdentifier(), identifierType)) {
            SemanticAnalysis.SemanticException("TypeError","Identifier already declared", declaration);
        }

        if (declaration.getAssignment() != null) {
            IdentifierType assignment = declaration.getAssignment().accept(this);
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", declaration);
            }
            if (!Objects.equals(identifierType, assignment)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", declaration);
            }
        }

        return identifierType;
    }

    public IdentifierType visit(Struct struct) throws Exception {
        StructType structType = new StructType(struct, this);
        if (!table.addType(struct.getIdentifier(), structType)) {
            SemanticAnalysis.SemanticException("TypeError","Struct already declared", struct);
        }
        return null;
    }

    public IdentifierType visit(If ifStatement) throws Exception {
        Node expression = ifStatement.getExpression();
        if (expression == null) {
            SemanticAnalysis.SemanticException("MissingConditionError","If expression is null", ifStatement);
        }
        assert expression != null;
        IdentifierType type = expression.accept(this);
        if (!Objects.equals(type.getType(), table.getType("bool"))) {
            SemanticAnalysis.SemanticException("TypeError","If expression is not boolean", expression);
        }
        return null;
    }

    public IdentifierType visit(Expression expression) {
        return null;
    }

    public IdentifierType visit(Expression.ComparisonOperation comparisonOperation) throws Exception {
        IdentifierType left = comparisonOperation.getLeft().accept(this);
        IdentifierType right = comparisonOperation.getRight().accept(this);
        if (Objects.equals(left.getType(), table.getType("int")) && Objects.equals(right.getType(), table.getType("float"))) {
            return new IdentifierType(table.getType("bool"));
        }
        if (Objects.equals(left.getType(), table.getType("float")) && Objects.equals(right.getType(), table.getType("int"))) {
            return new IdentifierType(table.getType("bool"));
        }
        if (!Objects.equals(left.getType(), right.getType())) {
            SemanticAnalysis.SemanticException("TypeError","Logical operation types do not match", comparisonOperation);
        }
        return new IdentifierType(table.getType("bool"));
    }

    public IdentifierType visit(Expression.LogicalOperation logicalOperation) throws Exception {
        IdentifierType left = logicalOperation.getLeft().accept(this);
        IdentifierType right = logicalOperation.getRight().accept(this);
        if (!Objects.equals(left.getType(), table.getType("bool")) || !Objects.equals(right.getType(), table.getType("bool"))) {
            SemanticAnalysis.SemanticException("TypeError","Logical operation types are not boolean", logicalOperation);
        }
        return new IdentifierType(table.getType("bool"));
    }

    public IdentifierType visit(IdentifierAccess identifierAccess) throws Exception {
        String identifier = identifierAccess.getIdentifier();
        IdentifierType type = table.getIdentifier(identifier);
        if (type == null) {
            SemanticAnalysis.SemanticException("TypeError","Identifier not declared", identifierAccess);
        }
        if (identifierAccess.getNext() != null) {
            type =  identifierAccess.getNext().accept(this, type);
        }
        if (identifierAccess.getAssignment() != null) {
            IdentifierType assignment = identifierAccess.getAssignment().accept(this);
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", identifierAccess.getAssignment());
            }
            if (!Objects.equals(type, assignment)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", identifierAccess.getAssignment());
            }
        }
        return type;
    }

    public IdentifierType visit(IdentifierAccess.ArrayAccess arrayAccess, IdentifierType type) throws Exception {
        if (!type.isVector()) {
            SemanticAnalysis.SemanticException("TypeError","Identifier is not a vector", arrayAccess);
        }
        IdentifierType indexType = arrayAccess.getIndex().accept(this);
        if (!Objects.equals(indexType.getType(), table.getType("int"))) {
            SemanticAnalysis.SemanticException("TypeError","Index type is not int", arrayAccess);
        }
        type = type.vectorPass();
        if (arrayAccess.getNext() != null) {
            return arrayAccess.getNext().accept(this, type);
        }
        if (arrayAccess.getAssignment() != null) {
            IdentifierType assignmentType = arrayAccess.getAssignment().accept(this);
            if (assignmentType == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", arrayAccess.getAssignment());
            }
            if (!Objects.equals(type, assignmentType)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", arrayAccess.getAssignment());
            }
        }

        return type;
    }

    public IdentifierType visit(IdentifierAccess.StructAccess structAccess, IdentifierType type) throws Exception {
        // a[0].b
        StructType structType = (StructType) type.getType();
        type = structType.getField(structAccess.getField());
        if (type == null) {
            SemanticAnalysis.SemanticException("TypeError","Field not declared", structAccess);
        }
        if (structAccess.getNext() != null) {
            return structAccess.getNext().accept(this, type);
        }
        if (structAccess.getAssignment() != null) {
            IdentifierType assignmentType = structAccess.getAssignment().accept(this);
            if (assignmentType == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", structAccess.getAssignment());
            }
            if (!Objects.equals(type, assignmentType)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type", structAccess.getAssignment());
            }
        }

        return type;
    }

    public IdentifierType visit(IdentifierAccess.FunctionCall functionCall, IdentifierType type) {
        return null;
    }
}
