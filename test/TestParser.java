import compiler.Lexer.Lexer;
import compiler.Lexer.VarType;
import compiler.Parser.*;
import org.junit.Test;

import java.io.StringReader;

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
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        Declaration decl = (Declaration) stmt.content;
        assertEquals(new VarType("int"), decl.type);
        assertEquals("a", decl.identifier);

        assertNull(decl.assignment);

        input = "int a = 10;";
        root = parse(input);

        statement = (Statements) root;
        stmt = (Statements.Statement) statement.statements.get(0);
        decl = (Declaration) stmt.content;
        assertEquals(new VarType("int"), decl.type);
        assertEquals("a", decl.identifier);
        assertNotNull(decl.assignment);

        Expression expression = (Expression) decl.assignment;
        Expression.Value value = (Expression.Value) expression.corps;
        assertEquals("10", value.value.getValue());
    }

    @Test
    public void testComplexeDeclaration() throws Exception {
        String input = "int[] a;";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        Declaration decl = (Declaration) stmt.content;

        VarType expectedType = new VarType("int");
        expectedType.setVector();
        assertEquals(expectedType, decl.type);

        assertEquals("a", decl.identifier);
        assertNull(decl.assignment);

        input = "int[] a = 10 * 10;";
        root = parse(input);

        statement = (Statements) root;
        stmt = (Statements.Statement) statement.statements.get(0);
        decl = (Declaration) stmt.content;
        expectedType = new VarType("int");
        expectedType.setVector();
        assertEquals(expectedType, decl.type);
        assertEquals("a", decl.identifier);
        assertNotNull(decl.assignment);

        Expression expression = (Expression) decl.assignment;
        Expression.Operation operation = (Expression.Operation) expression;
        Expression.Value value = (Expression.Value) operation.left;
        assertEquals("10", value.value.getValue());
        value = (Expression.Value) operation.right;
        assertEquals("10", value.value.getValue());
        assertEquals("*", operation.operation);
        assertEquals("10", value.value.getValue());
    }

    @Test
    public void testWhile() throws Exception {
        String input = "while (a < 10) { int a = 10; }";
        Starting root = parse(input);

        Statements statement = (Statements) root;
        Statements.Statement stmt = (Statements.Statement) statement.statements.get(0);
        While wh = (While) stmt.content;

        Expression expression = (Expression) wh.expression;
        Expression.Operation operation = (Expression.Operation) expression;
        Expression.Value value = (Expression.Value) operation.left;
        assertEquals("a", value.value.getValue());
        value = (Expression.Value) operation.right;
        assertEquals("10", value.value.getValue());
        assertEquals("<", operation.operation);

        Block block = (Block) wh.block;
        assertEquals(1, block.statements.size());
        Statements.Statement innerStmt = (Statements.Statement) block.statements.get(0);
        Declaration decl = (Declaration) innerStmt.content;
        assertEquals(new VarType("int"), decl.type);
        assertEquals("a", decl.identifier);
        assertNotNull(decl.assignment);

        Expression innerExpression = (Expression) decl.assignment;
        Expression.Value innerValue = (Expression.Value) innerExpression.corps;
        assertEquals("10", innerValue.value.getValue());
    }

}
