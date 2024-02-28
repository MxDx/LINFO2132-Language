import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;

import static org.junit.Assert.*;

public class TestLexer {
    
    @Test
    public void test1() throws IOException {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(var)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "VarType(int)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(2)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
    }
    @Test
    public void test2() throws IOException {
        String input = "final float pi = 3.14;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "Keyword(final)");
        assertEquals(lexer.getNextSymbol().toString(), "VarType(float)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(pi)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "MyFloat(3.14)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
    }
    @Test
    public void test3() throws IOException {
        String input = "if (x > 0) { print(x); }";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "Keyword(if)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(()");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(>)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(0)");
        assertEquals(lexer.getNextSymbol().toString(), "Special())");
        assertEquals(lexer.getNextSymbol().toString(), "Special({)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(print)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(()");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "Special())");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(})");
    }
    @Test
    public void test4() throws IOException {
        String input = "while (x > 0) { x =x-1; }";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "Keyword(while)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(()");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(>)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(0)");
        assertEquals(lexer.getNextSymbol().toString(), "Special())");
        assertEquals(lexer.getNextSymbol().toString(), "Special({)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(-)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(1)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(})");
    }
    @Test
    public void test5() throws IOException {
        String input = "var x int = 2; var y int = 3;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(var)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "VarType(int)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(2)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(var)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(y)");
        assertEquals(lexer.getNextSymbol().toString(), "VarType(int)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(3)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
    }
    @Test
    public void test6() throws IOException {
        String input = """
                for (int i = 0; i 
                < 10; i = i + 1)
                 { print(i); 
                 }""";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "Keyword(for)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(()");
        assertEquals(lexer.getNextSymbol().toString(), "VarType(int)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(i)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(0)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(i)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(<)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(10)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(i)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(i)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(+)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(1)");
        assertEquals(lexer.getNextSymbol().toString(), "Special())");
        assertEquals(lexer.getNextSymbol().toString(), "Special({)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(print)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(()");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(i)");
        assertEquals(lexer.getNextSymbol().toString(), "Special())");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(})");
    }
    @Test
    public void test7() throws IOException {
        String input = "// This is a comment\n  \rvar x                                        int     =2         ;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(var)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(x)");
        assertEquals(lexer.getNextSymbol().toString(), "VarType(int)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "MyInteger(2)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
    }
    @Test
    public void test8() throws IOException {
        String input = "// This is a comment";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNull(lexer.getNextSymbol());
    }
    @Test
    public void test9() throws IOException {
        String input = "String s = \"Hello, World!\";";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "VarType(String)");
        assertEquals(lexer.getNextSymbol().toString(), "Identifier(s)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(=)");
        assertEquals(lexer.getNextSymbol().toString(), "MyString(Hello, World!)");
        assertEquals(lexer.getNextSymbol().toString(), "Special(;)");
    }
}

