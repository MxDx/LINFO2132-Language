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

    public Type visit(Starting root) {
        IdentifierTable identifierTable = new IdentifierTable(null);
        Statements statements = root.getStatements();
        for (Statements.Statement statement : statements.statements) {
            statement.accept(this);
        }
        return null;
    }

    public Type visit(Statements.Statement statement) {
        return statement.getContent().accept(this);
    }

    public Type visit(Declaration declaration) throws Exception {
        Type type = table.getType(declaration.getType().getValue());
        if (type != null) {
            SemanticAnalysis.SemanticException("TypeError","Type already declared");
        }
        if (!table.addIdentifier(declaration.getIdentifier(), type)) {
            SemanticAnalysis.SemanticException("TypeError","Identifier already declared");
        }

        if (declaration.getAssignment() != null) {
            Type assignment = declaration.getAssignment().accept(this);
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null");
            }
            if (!Objects.equals(type, assignment)) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type does not match declaration type");
            }
        }

        return type;
    }

    public Type visit(Expression expression) {
        return null;
    }
}
