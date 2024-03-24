import compiler.Lexer.Lexer;
import compiler.Lexer.VarType;
import compiler.Parser.*;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestParser {
    private static Starting parse(String input) throws Exception {
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        return parser.getAST();
    }
    @Test
    public void testBasicDeclaration() throws Exception {
        String input = "int a;";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        assertEquals(1, statement.statements.size());
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an  instance of Declaration",stmt.content instanceof Declaration);
        Declaration decl = (Declaration) stmt.content;
        assertEquals( "The declaration has the wrong type",new VarType("int"), decl.type);
        assertEquals("The declaration has the wrong identifier","a", decl.identifier);

        assertNull("The declaration has an assignment", decl.assignment);

        input = "int a = 10;";
        root = parse(input);

        statement = (Statements) root;
        stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an instance of Declaration",stmt.content instanceof Declaration);
        decl = (Declaration) stmt.content;
        assertEquals("The declaration has the wrong type",new VarType("int"), decl.type);
        assertEquals("The declaration has the wrong identifier","a", decl.identifier);

        assertNotNull("The declaration has no assignment", decl.assignment);

        assertTrue("The assignment is not an instance of Expression",decl.assignment instanceof Expression);
        Expression expression = (Expression) decl.assignment;
        assertTrue("The expression is not an instance of Expression.Value", expression instanceof Expression.Value);
        Expression.Value value = (Expression.Value) expression;
        assertEquals("The value is not correct","10", value.value.getValue());
    }

    @Test
    public void testComplexeDeclaration() throws Exception {
        String input = "final int[] a;";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an instance of Declaration",stmt.content instanceof Declaration);
        Declaration decl = (Declaration) stmt.content;

        VarType expectedType = new VarType("int");
        expectedType.setVector();
        expectedType.setFinal(true);
        assertEquals("The type is not correct",expectedType, decl.type);
        assertEquals("The identifier is not correct","a", decl.identifier);
        assertTrue("The declaration is not final",decl.type.isFinal());
        assertTrue("The declaration is not a vector",decl.type.isVector());
        assertNull("The declaration has an assignment", decl.assignment);

        input = "int[] a = 10 * 10;";
        root = parse(input);

        statement = (Statements) root;
        stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an instance of Declaration",stmt.content instanceof Declaration);
        decl = (Declaration) stmt.content;
        expectedType = new VarType("int");
        expectedType.setVector();
        assertEquals("The type is not correct",expectedType, decl.type);
        assertEquals("The identifier is not correct","a", decl.identifier);
        assertNotNull("The declaration has no assignment", decl.assignment);

        assertTrue("The assignment is not an instance of Expression",decl.assignment instanceof Expression);
        Expression expression = (Expression) decl.assignment;
        assertTrue("The expression is not an instance of Operation",expression instanceof Expression.Operation);
        Expression.Operation operation = (Expression.Operation) expression;
        assertTrue("The left side of the operation is not an instance of Value",operation.left instanceof Expression.Value);
        Expression.Value value = (Expression.Value) operation.left;
        assertEquals("The value is not correct","10", value.value.getValue());
        value = (Expression.Value) operation.right;
        assertEquals("The value is not correct","10", value.value.getValue());
        assertEquals("The operation is not correct","*", operation.operation);
    }

    @Test
    public void testBasicWhile() throws Exception {
        String input = "while (a < 10) { int a = 10; }";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an instance of While",stmt.content instanceof While);
        While wh = (While) stmt.content;

        assertTrue("The expression is not an instance of Expression",wh.expression instanceof Expression);
        Expression expression = (Expression) wh.expression;
        assertTrue("The expression is not an instance of Operation",expression instanceof Expression.Operation);
        Expression.Operation operation = (Expression.Operation) expression;

        assertTrue("The left side of the operation is not an instance of IdentifierAccess",operation.left instanceof IdentifierAccess);
        IdentifierAccess left = (IdentifierAccess) operation.left;
        assertEquals("The identifier is wrong","a", left.identifier);
        assertTrue("The right side of the operation is not an instance of Value",operation.right instanceof Expression.Value);
        Expression.Value value = (Expression.Value) operation.right;
        assertEquals("The value is wrong","10", value.value.getValue());
        assertEquals("<", operation.operation);

        Block block = wh.block;
        assertNotNull("The inside of the while loop is null",block);
        assertEquals("The block has the wrong number of statements",1, block.statements.statements.size());
        Statements.Statement innerStmt = (Statements.Statement) block.statements.statements.get(0);
        Declaration decl = (Declaration) innerStmt.content;
        assertEquals("The declaration has the wrong type",new VarType("int"), decl.type);
        assertEquals("The declaration has the wrong identifier","a", decl.identifier);
        assertNotNull("The declaration has no assignment", decl.assignment);

        assertTrue("The assignment is not an instance of Expression.Value",decl.assignment instanceof Expression.Value);
        Expression.Value innerValue = (Expression.Value) decl.assignment;
        assertEquals("The value is not correct","10", innerValue.value.getValue());
    }

    @Test
    public void testComplexeWhile() throws Exception {
        String input = "while (a + 2 * 4 < (1 + 10) * 10) {}";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an instance of While",stmt.content instanceof While);
        While wh = (While) stmt.content;

        assertTrue("The expression is not an instance of Expression",wh.expression instanceof Expression);
        Expression expression = (Expression) wh.expression;
        assertTrue("The expression is not an instance of Operation",expression instanceof Expression.Operation);
        Expression.Operation operationComp = (Expression.Operation) expression;

        assertTrue("The left side of the operation is not an instance of Operation", operationComp.left instanceof Expression.Operation);
        Expression.Operation operationLeft = (Expression.Operation) operationComp.left;
        assertTrue("The left side of the left operation is not an instance of IdentifierAccess", operationLeft.left instanceof IdentifierAccess);
        IdentifierAccess identifer = (IdentifierAccess) operationLeft.left;
        assertEquals("The identifier is not correct","a", identifer.identifier);
        assertTrue("The right side of the left operation is not an instance of Operation", operationLeft.right instanceof Expression.Operation);
        Expression.Operation operationRight = (Expression.Operation) operationLeft.right;
        assertTrue("The left side of the right operation is not an instance of Value", operationRight.left instanceof Expression.Value);
        Expression.Value value = (Expression.Value) operationRight.left;
        assertEquals("The value is not correct","2", value.value.getValue());
        assertTrue("The right side of the right operation is not an instance of Value", operationRight.right instanceof Expression.Value);
        value = (Expression.Value) operationRight.right;
        assertEquals("The value is not correct","4", value.value.getValue());
        assertEquals("The operation is not correct","*", operationRight.operation);

        assertTrue("The right side of the operation is not an instance of Operation", operationComp.right instanceof Expression.Operation);
        Expression.Operation operationRightMul = (Expression.Operation) operationComp.right;
        assertTrue("The left side of the right operation is not an instance of Operation", operationRightMul.left instanceof Expression.Operation);
        Expression.Operation operationRightMulAdd = (Expression.Operation) operationRightMul.left;
        assertTrue("The left side of the right operation is not an instance of Value", operationRightMulAdd.left instanceof Expression.Value);
        value = (Expression.Value) operationRightMulAdd.left;
        assertEquals("The value is not correct","1", value.value.getValue());
        assertTrue("The right side of the right operation is not an instance of Value", operationRightMulAdd.right instanceof Expression.Value);
        value = (Expression.Value) operationRightMulAdd.right;
        assertEquals("The value is not correct","10", value.value.getValue());
        assertEquals("The operation is not correct","+", operationRightMulAdd.operation);
        assertTrue("The right side of the right operation is not an instance of Value", operationRightMul.right instanceof Expression.Value);
        value = (Expression.Value) operationRightMul.right;
        assertEquals("The value is not correct","10", value.value.getValue());
        assertEquals("The operation is not correct","*", operationRightMul.operation);
    }

    @Test
    public void testBasicIf() throws Exception {
        String input = "if (a < 10) { int a = 10; }";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an instance of If",stmt.content instanceof If);
        If ifTested = (If) stmt.content;

        assertTrue("The expression is not an instance of Expression",ifTested.expression instanceof Expression);
        Expression expression = (Expression) ifTested.expression;
        assertTrue("The expression is not an instance of Operation",expression instanceof Expression.Operation);
        Expression.Operation operation = (Expression.Operation) expression;

        assertTrue("The left side of the operation is not an instance of IdentifierAccess",operation.left instanceof IdentifierAccess);
        IdentifierAccess left = (IdentifierAccess) operation.left;
        assertEquals("The identifier is wrong","a", left.identifier);
        assertTrue("The right side of the operation is not an instance of Value",operation.right instanceof Expression.Value);
        Expression.Value value = (Expression.Value) operation.right;
        assertEquals("The value is wrong","10", value.value.getValue());
        assertEquals("<", operation.operation);

        Block block = ifTested.block;
        assertNotNull("The inside of the if block is null",block);
        assertEquals("The block has the wrong number of statements",1, block.statements.statements.size());
        Statements.Statement innerStmt = (Statements.Statement) block.statements.statements.get(0);
        Declaration decl = (Declaration) innerStmt.content;
        assertEquals("The declaration has the wrong type",new VarType("int"), decl.type);
        assertEquals("The declaration has the wrong identifier","a", decl.identifier);
        assertNotNull("The declaration has no assignment", decl.assignment);

        assertTrue("The assignment is not an instance of Expression.Value",decl.assignment instanceof Expression.Value);
        Expression.Value innerValue = (Expression.Value) decl.assignment;
        assertEquals("The value is not correct","10", innerValue.value.getValue());
    }

    @Test
    // expression in complexe if : a + readInt(3 + a) * 4 < (1 + z.y[4]) * 10
    public void testComplexeIf() throws Exception {
        String input = "if (a + readInt(3 + a) * 4 < (1 + z.y[4]) * 10) {}";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        assertTrue("The statement is not an instance of If", stmt.content instanceof If);
        If ifTested = (If) stmt.content;

        assertTrue("The expression is not an instance of Expression", ifTested.expression instanceof Expression);
        Expression expression = (Expression) ifTested.expression;
        assertTrue("The expression is not an instance of Operation", expression instanceof Expression.Operation);
        Expression.Operation operationComp = (Expression.Operation) expression;

        assertTrue("The left side of the operation is not an instance of Operation", operationComp.left instanceof Expression.Operation);
        Expression.Operation operationLeftAdd = (Expression.Operation) operationComp.left;
        assertTrue("The left side of the left operation is not an instance of IdentifierAccess", operationLeftAdd.left instanceof IdentifierAccess);
        IdentifierAccess identifier = (IdentifierAccess) operationLeftAdd.left;
        assertEquals("The identifier is not correct", "a", identifier.identifier);

        assertTrue("The right side of the left operation is not an instance of Operation", operationLeftAdd.right instanceof Expression.Operation);
        Expression.Operation operationLeftAddMul = (Expression.Operation) operationLeftAdd.right;
        assertTrue("The left side of the right operation is not an instance of IdentifierAccess", operationLeftAddMul.left instanceof IdentifierAccess);
        identifier = (IdentifierAccess) operationLeftAddMul.left;
        assertEquals("The identifier is not correct", "readInt", identifier.identifier);
        assertTrue("The next element of the left side of the right operation is not an instance of IdentifierAccess.FunctionCall", identifier.next instanceof IdentifierAccess.FunctionCall);
        IdentifierAccess.FunctionCall functionCall = (IdentifierAccess.FunctionCall) identifier.next;
        ArrayList<Node> arguments = functionCall.arguments;
        assertEquals("The number of arguments is not correct", 1, arguments.size());
        assertTrue("The argument is not an instance of Expression.Operation", arguments.get(0) instanceof Expression.Operation);
        Expression.Operation operationArg = (Expression.Operation) arguments.get(0);
        assertTrue("The left side of the argument is not an instance of Value", operationArg.left instanceof Expression.Value);
        Expression.Value value = (Expression.Value) operationArg.left;
        assertEquals("The value is not correct", "3", value.value.getValue());
        assertTrue("The right side of the argument is not an instance of IdentifierAccess", operationArg.right instanceof IdentifierAccess);
        identifier = (IdentifierAccess) operationArg.right;
        assertEquals("The identifier is not correct", "a", identifier.identifier);
        assertEquals("The operation is not correct", "+", operationArg.operation);

        assertTrue("The right side of the right operation is not an instance of Value", operationLeftAddMul.right instanceof Expression.Value);
        value = (Expression.Value) operationLeftAddMul.right;
        assertEquals("The value is not correct", "4", value.value.getValue());
        assertEquals("The operation is not correct", "*", operationLeftAddMul.operation);


        assertTrue("The right side of the operation is not an instance of Operation", operationComp.right instanceof Expression.Operation);
        Expression.Operation operationRightMul = (Expression.Operation) operationComp.right;
        assertTrue("The left side of the right operation is not an instance of Operation", operationRightMul.left instanceof Expression.Operation);
        Expression.Operation operationRightMulAdd = (Expression.Operation) operationRightMul.left;
        assertEquals("The operation is not correct", "+", operationRightMulAdd.operation);
        assertTrue("The left side of the right operation is not an instance of Value", operationRightMulAdd.left instanceof Expression.Value);
        value = (Expression.Value) operationRightMulAdd.left;
        assertEquals("The value is not correct", "1", value.value.getValue());

        assertTrue("The right side of the right operation is not an instance of IdentifierAccess", operationRightMulAdd.right instanceof IdentifierAccess);
        identifier = (IdentifierAccess) operationRightMulAdd.right;
        assertEquals("The identifier is not correct", "z", identifier.identifier);
        assertTrue("The next element of the right side of the right operation is not an instance of IdentifierAccess.StructAccess", identifier.next instanceof IdentifierAccess.StructAccess);
        IdentifierAccess.StructAccess structAccess = (IdentifierAccess.StructAccess) identifier.next;
        assertEquals("The field is not correct", "y", structAccess.field);
        assertTrue("The next element of the right side of the right operation is not an instance of IdentifierAccess.ArrayAccess", structAccess.next instanceof IdentifierAccess.ArrayAccess);
        IdentifierAccess.ArrayAccess arrayAccess = (IdentifierAccess.ArrayAccess) structAccess.next;
        assertEquals("The index is not correct", 4, (int) arrayAccess.index);

        assertTrue("The right side of the right operation is not an instance of Value", operationRightMul.right instanceof Expression.Value);
        value = (Expression.Value) operationRightMul.right;
        assertEquals("The value is not correct", "10", value.value.getValue());
        assertEquals("The operation is not correct", "*", operationRightMul.operation);
    }
}
