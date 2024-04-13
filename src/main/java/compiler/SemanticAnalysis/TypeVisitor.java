package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;
import compiler.Parser.*;

import java.util.ArrayList;
import java.util.Objects;

public class TypeVisitor {
    IdentifierTable table;
    IdentifierType returnType;

    public TypeVisitor() {
        table = new IdentifierTable();
    }

    public TypeVisitor(TypeVisitor parent) {
        this.table = new IdentifierTable(parent.getTable());
        this.returnType = parent.getReturnType();
    }

    public IdentifierTable getTable() {
        return table;
    }

    public void setReturnType(IdentifierType returnType) {
        this.returnType = returnType;
    }

    public IdentifierType getReturnType() {
        return returnType;
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

    public IdentifierType visit(Block block) throws Exception {
        Statements statements = block.getStatements();
        for (Statements.Statement statement : statements.statements) {
            statement.accept(this);
        }
        return null;
    }

    public IdentifierType visit(Declaration declaration) throws Exception {
        Type type = table.getType(declaration.getType().getValue());
        if (type == null) {
            String str = "Type not declared: ";
            str +=  "< " + declaration.getType().getValue() + " >";
            SemanticAnalysis.SemanticException("ScopeError",str, declaration);
        }
        IdentifierType identifierType = new IdentifierType(type, declaration.getType());
        if (declaration.getAssignment() != null) {
            IdentifierType assignment = declaration.getAssignment().accept(this);
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError","Assignment type is null", declaration);
            }
            if (!Objects.equals(identifierType, assignment)) {
                String str = "Assignment type does not match declaration type: ";
                str += identifierType + " != ";
                str += assignment;
                SemanticAnalysis.SemanticException("TypeError",str, declaration);
            }
        }

        if (!table.addIdentifier(declaration.getIdentifier(), identifierType)) {
            String str = "Identifier already declared: ";
            str += "< " + declaration.getIdentifier() + " >";
            SemanticAnalysis.SemanticException("TypeError", str, declaration);
        }

        return identifierType;
    }

    public IdentifierType visit(Struct struct) throws Exception {
        StructType structType = new StructType(struct, this);
        if (!table.addType(struct.getIdentifier(), structType)) {
            String str = "Struct already declared: ";
            str += "< " + struct.getIdentifier() + " >";
            SemanticAnalysis.SemanticException("StructError", str, struct);
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
        Node block = ifStatement.getBlock();
        if (block != null) {
            TypeVisitor typeVisitor = new TypeVisitor(this);
            block.accept(typeVisitor);
        }
        Node elseStatement = ifStatement.getElseStatement();
        if (elseStatement != null) {
            TypeVisitor typeVisitor = new TypeVisitor(this);
            elseStatement.accept(typeVisitor);
        }
        return null;
    }

    public IdentifierType visit(While whileStatement) throws Exception {
        Node expression = whileStatement.getExpression();
        if (expression == null) {
            SemanticAnalysis.SemanticException("MissingConditionError","While expression is null", whileStatement);
        }
        assert expression != null;
        IdentifierType type = expression.accept(this);
        if (!Objects.equals(type.getType(), table.getType("bool"))) {
            SemanticAnalysis.SemanticException("TypeError","While expression is not boolean", expression);
        }
        Node block = whileStatement.getBlock();
        if (block != null) {
            TypeVisitor typeVisitor = new TypeVisitor(this);
            block.accept(typeVisitor);
        }
        return null;
    }

    public IdentifierType visit(For forStatement) throws Exception {
        Node firstAssignment = forStatement.getFirstAssignment();
        if (firstAssignment == null) {
            SemanticAnalysis.SemanticException("MissingConditionError","For first assignment is null", forStatement);
        }
        assert firstAssignment != null;
        IdentifierType firstAssignmentType = firstAssignment.accept(this);

        Node expression = forStatement.getExpression();
        if (expression == null) {
            SemanticAnalysis.SemanticException("MissingConditionError","For expression is null", forStatement);
        }
        assert expression != null;
        IdentifierType type = expression.accept(this);
        if (!Objects.equals(type.getType(), table.getType("bool"))) {
            SemanticAnalysis.SemanticException("TypeError","For expression is not boolean", expression);
        }

        Node secondAssignment = forStatement.getSecondAssignment();
        if (secondAssignment == null) {
            SemanticAnalysis.SemanticException("MissingConditionError","For second assignment is null", forStatement);
        }
        assert secondAssignment != null;
        IdentifierType secondAssignmentType = secondAssignment.accept(this);

        Node block = forStatement.getBlock();
        if (block != null) {
            TypeVisitor typeVisitor = new TypeVisitor(this);
            block.accept(typeVisitor);
        }
        return null;
    }

    public IdentifierType visit(Method function) throws Exception {
        VarType returnType = function.getReturnType();
        Type type;
        if (!Objects.equals(returnType.getValue(), "void")) {
            type = table.getType(returnType.getValue());
            if (type == null) {
                String str = "Type not declared: ";
                str += "< " + function.getReturnType().getValue() + " >";
                SemanticAnalysis.SemanticException("ScopeError",str, function);
            }
        } else {
            type = null;
        }

        IdentifierType identifierTypeReturn = new IdentifierType(type, returnType);
        TypeVisitor newVisitor = new TypeVisitor(this);

        ArrayList<Parameter> parameters = function.getParameters();
        ArrayList<IdentifierType> parametersTypes = new ArrayList<>(parameters.size());
        for (Parameter parameter : parameters) {
            Type parameterType = table.getType(parameter.getType().getValue());
            if (parameterType == null) {
                String str = "Type not declared: ";
                str += "< " + parameter.getType().getValue() + " >";
                SemanticAnalysis.SemanticException("ArgumentError",str, function);
            }
            IdentifierType identifierType = new IdentifierType(parameterType, parameter.getType());
            newVisitor.getTable().addIdentifier(parameter.getIdentifier(), identifierType);
            parametersTypes.add(identifierType);
        }

        FuncType funcType = new FuncType(identifierTypeReturn, parametersTypes);

        String identifier = function.getName().getValue();
        if (!table.addIdentifier(identifier, funcType)) {
            String str = "Identifier already declared: ";
            str += "< " + identifier + " >";
            SemanticAnalysis.SemanticException("TypeError", str, function);
        }

        Block block = function.getBlock();
        if (block != null) {
            newVisitor.setReturnType(identifierTypeReturn);
            block.accept(newVisitor);
        }

        return null;
    }

    public IdentifierType visit(Return returnStatement) throws Exception {
        Node expression = returnStatement.getExpression();
        if (returnType == null) {
            SemanticAnalysis.SemanticException("ReturnError","Return outside of a function", returnStatement);
        }
        if (expression == null) {
            if (returnType == null) {
                SemanticAnalysis.SemanticException("ReturnError","Return type is null", returnStatement);
            }
            if (!returnType.isVoid()) {
                SemanticAnalysis.SemanticException("ReturnError","Return type does not match function return type", returnStatement);
            }
            return null;
        }
        IdentifierType type = expression.accept(this);
        if (returnType.isVoid() && type != null) {
            String str = "Return type does not match function return type: ";
            str += type + " != ";
            str += "void";
            SemanticAnalysis.SemanticException("TypeError", str, returnStatement);
        }

        if (Objects.equals(returnType.getType(), table.getType("float")) && Objects.equals(type.getType(), table.getType("int"))) {
            return null;
        }

        if (!Objects.equals(returnType, type)) {
            String str = "Return type does not match function return type: ";
            str += returnType + " != ";
            str += type;
            SemanticAnalysis.SemanticException("TypeError",str, returnStatement);
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
            SemanticAnalysis.SemanticException("OperatorError","Logical operation types do not match", comparisonOperation);
        }
        return new IdentifierType(table.getType("bool"));
    }

    public IdentifierType visit(Expression.ArithmeticOperation arithmeticOperation) throws Exception {
        IdentifierType left = arithmeticOperation.getLeft().accept(this);
        IdentifierType right = arithmeticOperation.getRight().accept(this);
        if (Objects.equals(left.getType(), table.getType("int")) && Objects.equals(right.getType(), table.getType("int"))) {
            return new IdentifierType(table.getType("int"));
        }
        if (Objects.equals(left.getType(), table.getType("float")) && Objects.equals(right.getType(), table.getType("float"))) {
            return new IdentifierType(table.getType("float"));
        }
        if (Objects.equals(left.getType(), table.getType("int")) && Objects.equals(right.getType(), table.getType("float"))) {
            return new IdentifierType(table.getType("float"));
        }
        if (Objects.equals(left.getType(), table.getType("float")) && Objects.equals(right.getType(), table.getType("int"))) {
            return new IdentifierType(table.getType("float"));
        }
        SemanticAnalysis.SemanticException("OperatorError","Arithmetic operation types do not match", arithmeticOperation);
        return null;
    }

    public IdentifierType visit(Expression.LogicalOperation logicalOperation) throws Exception {
        IdentifierType left = logicalOperation.getLeft().accept(this);
        IdentifierType right = logicalOperation.getRight().accept(this);
        if (!Objects.equals(left.getType(), table.getType("bool")) || !Objects.equals(right.getType(), table.getType("bool"))) {
            SemanticAnalysis.SemanticException("OperatorError","Logical operation types are not boolean", logicalOperation);
        }
        return new IdentifierType(table.getType("bool"));
    }

    public IdentifierType visit(IdentifierAccess identifierAccess) throws Exception {
        String identifier = identifierAccess.getIdentifier();
        IdentifierType type = table.getIdentifier(identifier);
        if (type == null) {
            String str = "Identifier not declared: ";
            str += "< " + identifier + " >";
            SemanticAnalysis.SemanticException("ScopeError", str, identifierAccess);
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
                String str = "Assignment type does not match declaration type: ";
                str += type + " != ";
                str += assignment;
                SemanticAnalysis.SemanticException("TypeError", str, identifierAccess.getAssignment());
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
            SemanticAnalysis.SemanticException("ScopeError","Field not declared", structAccess);
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
                String str = "Assignment type does not match declaration type: ";
                str += type + " != ";
                str += assignmentType;
                SemanticAnalysis.SemanticException("TypeError",str, structAccess.getAssignment());
            }
        }

        return type;
    }

    public IdentifierType visit(IdentifierAccess.FunctionCall functionCall, IdentifierType type) throws Exception {
        FuncType funcType = (FuncType) type;
        ArrayList<IdentifierType> parameters = funcType.getParameters();
        ArrayList<Node> arguments = functionCall.getArguments();
        if (parameters.size() != arguments.size()) {
            SemanticAnalysis.SemanticException("ArgumentError","Function call parameters do not match function declaration", functionCall);
        }

        for (int i = 0; i < parameters.size(); i++) {
            IdentifierType parameter = parameters.get(i);
            IdentifierType argument = arguments.get(i).accept(this);
            if (!Objects.equals(parameter, argument)) {
                String str = "Function call parameters do not match function declaration: ";
                str += parameter + " != ";
                str += argument;
                SemanticAnalysis.SemanticException("ArgumentError",str, functionCall);
            }
        }
        return funcType.getReturnType();
    }

    public IdentifierType visit(ArrayInitialization arrayInitialization) throws Exception {
        Type type = table.getType(arrayInitialization.getType().getValue());
        if (type == null) {
            String str = "Type not declared: ";
            str += "< " + arrayInitialization.getType().getValue() + " >";
            SemanticAnalysis.SemanticException("ScopeError",str, arrayInitialization);
        }

        Expression index = (Expression) arrayInitialization.getIndex();
        IdentifierType indexType = index.accept(this);
        if (!Objects.equals(indexType.getType(), table.getType("int"))) {
            SemanticAnalysis.SemanticException("TypeError","Index type is not int", arrayInitialization);
        }

        IdentifierType identifierType = new IdentifierType(type, arrayInitialization.getType());
        identifierType.setVectorDepth(1);
        return identifierType;
    }
}
