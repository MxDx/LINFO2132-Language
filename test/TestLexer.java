import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import compiler.Lexer.Lexer;

import static org.junit.Assert.*;

public class TestLexer {
    
    @Test
    public void test1() throws Exception {
        String input = "var x int = 2;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,2>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
    }
    @Test
    public void test2() throws Exception {
        String input = "final float pi = 3.14;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Keyword,final>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,float>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,pi>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyFloat,3.14>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
    }
    @Test
    public void test3() throws Exception {
        String input = "if (x > 0) { print(x); }";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Keyword,if>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,(>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,>>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,0>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,)>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,{>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,print>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,(>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,)>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,}>");
    }
    @Test
    public void test4() throws Exception {
        String input = "while (x > 0) { x =x-1; }";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Keyword,while>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,(>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,>>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,0>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,)>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,{>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,->");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,1>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,}>");
    }
    @Test
    public void TestSimple() throws Exception {
        String input = "var x int = 2; var y int = 3;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,2>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,y>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,3>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
    }
    @Test
    public void TestForLoop() throws Exception {
        String input = """
                for (int i = 0; i 
                < 10; i = i + 1)
                 { print(i); 
                 }""";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Keyword,for>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,(>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,i>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,0>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,i>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,<>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,10>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,i>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,i>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,+>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,1>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,)>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,{>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,print>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,(>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,i>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,)>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,}>");
    }
    @Test
    public void TestComment1() throws Exception {
        String input = "// This is a comment\n  \rvar x                                        int     =2         ;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,2>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
    }
    @Test
    public void TestComment2() throws Exception {
        String input = "// This is a comment";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertNull(lexer.getNextSymbol());
    }
    @Test
    public void TestString() throws Exception {
        String input = "String s = \"Hello, World!\";";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,String>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,s>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyString,Hello, World!>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
    }

    @Test
    public void TestNoEndString() throws Exception {
        String input = "String s = \"Hello, World!";
        StringReader reader = new StringReader(input);
        try {
            Lexer lexer = new Lexer(reader);
            assertEquals(lexer.getNextSymbol().toString(), "<VarType,String>");
            assertEquals(lexer.getNextSymbol().toString(), "<Identifier,s>");
            assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
            lexer.getNextSymbol();
            fail("Supposed to raise an exception");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "No end of string");
        }

        String input2 = "String s = \"";
        StringReader reader2 = new StringReader(input2);
        try {
            Lexer lexer = new Lexer(reader2);
            assertEquals(lexer.getNextSymbol().toString(), "<VarType,String>");
            assertEquals(lexer.getNextSymbol().toString(), "<Identifier,s>");
            assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
            lexer.getNextSymbol();
            fail("Supposed to raise an exception");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "No end of string");
        }
    }

    @Test
    public void TestFloat() throws Exception {
        String input = "var x float = 3.14;\ny =.14";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,float>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyFloat,3.14>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,y>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyFloat,0.14>");
    }

    @Test
    public void TestFalseNumber() throws Exception {
        // This is not supposed to cause issue with the lexer because the parser will catch the error
        String input = "var x int = 2..3; \n var y int = 3.14.15;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyFloat,2.>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyFloat,0.3>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
        assertEquals(lexer.getNextSymbol().toString(), "<Identifier,y>");
        assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyFloat,3.14>");
        assertEquals(lexer.getNextSymbol().toString(), "<MyFloat,0.15>");
        assertEquals(lexer.getNextSymbol().toString(), "<Special,;>");
    }

    @Test
    public void TestSpecialCharacters() throws Exception {
        // This is supposed to raise an error when it reaches the special character
        String input = "var x int = 2@3;";
        StringReader reader = new StringReader(input);
        try {
            Lexer lexer = new Lexer(reader);
            assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
            assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
            assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
            assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
            assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,2>");
            lexer.getNextSymbol();
            fail("Supposed to raise an exception");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Invalid character: @");
        }

        String input2 = "var x int = 2#3;";
        StringReader reader2 = new StringReader(input2);
        try {
            Lexer lexer = new Lexer(reader2);
            assertEquals(lexer.getNextSymbol().toString(), "<Identifier,var>");
            assertEquals(lexer.getNextSymbol().toString(), "<Identifier,x>");
            assertEquals(lexer.getNextSymbol().toString(), "<VarType,int>");
            assertEquals(lexer.getNextSymbol().toString(), "<Special,=>");
            assertEquals(lexer.getNextSymbol().toString(), "<MyInteger,2>");
            lexer.getNextSymbol();
            fail("Supposed to raise an exception");
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Invalid character: #");
        }
    }
}

