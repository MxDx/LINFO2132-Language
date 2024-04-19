import compiler.Lexer.Lexer;
import compiler.SemanticAnalysis.Errors.TypeError;
import org.junit.Test;
import compiler.Parser.*;
import compiler.SemanticAnalysis.*;
import compiler.SemanticAnalysis.Errors.*;

import java.io.StringReader;

import static org.junit.Assert.*;

public class TestSemanticAnalysis {
    private static Parser getParser(String input) throws Exception {
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        Parser parser = new Parser(lexer);
        return parser;
    }

    ////////////////// Basic tests //////////////////
    @Test
    public void basicTypeError() throws Exception {
        String input = "int a = \"Hello World\";";
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (TypeError e){
            String expectedErrorMessage = "{ TypeError } : Assignment type does not match declaration type: \"<int>\" != \"<string>\" at line 1 token 1 with node type Declaration";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "int a = 10;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }

    @Test
    public void basicStructError() throws Exception {
        String input = """
                struct while{
                    int b;
                    int c;}
                """;
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (StructError e){
            String expectedErrorMessage = "{ StructError } : Struct already declared or reserved keyword: < while > at line 1 token 1 with node type Struct";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                struct a{
                    int b;
                    int c;}
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }

    @Test
    public void basicOperatoErrorArithmetic() throws Exception {
        String input = "int a = 10 + \"Hello World\";";
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (OperatorError e){
            String expectedErrorMessage = "{ OperatorError } : Arithmetic operation [ + ] cannot be applied to types: \"<int>\" and \"<string>\" at line 1 token 6 with node type ArithmeticOperation";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "int a = 10 + 10;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }
    @Test
    public void basicOperatoErrorBinary() throws Exception {
        String input = "bool a = 10 && true;";
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (OperatorError e){
            String expectedErrorMessage = "{ OperatorError } : Logical operation [ && ] cannot be applied to types: \"<int>\" and \"<bool>\" at line 1 token 5 with node type LogicalOperation";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "bool a = true && true;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }
    @Test
    public void basicOperatoErrorComparison() throws Exception {
        String input = "bool a = \"Hello World\" > 10;";
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (OperatorError e){
            String expectedErrorMessage = "{ OperatorError } : Comparison operation [ > ] cannot be applied to types: \"<string>\" and \"<int>\" at line 1 token 4 with node type ComparisonOperation";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "bool a = 10 == 10;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }
    @Test
    public void basicOperatoErrorUnary() throws Exception {
        String input = "bool a = !(10);";
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (TypeError e){
            String expectedErrorMessage = "{ TypeError } : Assignment type does not match declaration type: \"<bool>\" != \"<int>\" at line 1 token 1 with node type Declaration";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "bool a = !(true);";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }

    @Test
    public void basicArgumentError() throws Exception {
        String input = """
                def int a(int b){
                    return b*b;}
                a("Hello World");
                """;
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (ArgumentError e){
            String expectedErrorMessage = "{ ArgumentError } : Function call parameters do not match function declaration: \"<int>\" != \"<string>\" at line 3 token 1 with node type FunctionCall";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                def int a(int b){
                    return b*b;}
                a(10);
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }
    @Test
    public void basicMissingConditionError() throws Exception {
        String input = """
                int a = 10;
                if (a){
                    a = 20;}
                """;
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (MissingConditionError e){
            String expectedErrorMessage = "{ MissingConditionError } : If expression is not boolean at line 2 token 3 with node type Expression";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                int a = 10;
                if (a > 5){
                    a = 20;}
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }
    @Test
    public void basicReturnError() throws Exception {
        String input = """
                def int a(){
                    return "Hello World";}
                a();
                """;
        Parser parser = getParser(input);
        try{
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        }
        catch (ReturnError e){
            String expectedErrorMessage = "{ ReturnError } : Return type does not match function return type: \"<int>\" != \"<string>\" at line 2 token 1 with node type Return";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                def int a(){
                    return 10;}
                a();
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        }
        catch (Exception e){
            assert false;
        }

    }
    @Test
    public void basicScopeError() throws Exception {
        String input = """
                def int a(){
                    int b = 10;
                    return b;}
                    b = 20;
                    a();
                    """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (ScopeError e) {
            String expectedErrorMessage = "{ ScopeError } : Identifier not declared: < b > at line 4 token 1 with node type IdentifierAccess";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                def int a(){
                    int b = 10;
                    b = 20;
                    return b;}
                    a();
                    """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            assert false;
        }
    }
    ////////////////// Complex tests //////////////////

    @Test
    public void complexTestDoubleDeclaration() throws Exception {
        String input = """
                struct a{
                    int b;
                    int c;}
                def int a(){
                    a x;
                    x.b = 10;
                    x.c = 20;
                    return x.b + x.c;}
                a();
                """;

        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (DeclarationError e) {
            String expectedErrorMessage = "{ DeclarationError } : Identifier already declared: < a > at line 4 token 1 with node type Method";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                struct a{
                    int b;
                    int c;}
                def int d(){
                    a x;
                    x.b = 10;
                    x.c = 20;
                    return x.b + x.c;}
                d();
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            assert false;
        }
    }

    @Test
    public void complexTestForLoop() throws Exception {
        String input = """
                def int a(){
                    int b = 0;
                    int i;
                    for (i = 0, i = i + 10, i = i + 1){
                        b = b + 1;}
                    return b;}
                a();
                """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (MissingConditionError e) {
            String expectedErrorMessage = "{ MissingConditionError } : For expression is not boolean at line 4 token 7 with node type Expression";
            assertEquals(expectedErrorMessage, e.getMessage());
        }


        input = """
                def int a(){
                    int b = 0;
                    int i;
                    for (i = 0, i < 10, i = i + 1){
                        b = b + 1;}
                    return b;}
                a();
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            assert false;
        }

    }
    @Test
    public void complexTestWhileLoop() throws Exception {
        String input = """
                def int a(){
                    int b = 0;
                    int i = 0;
                    while (i){
                        b = b + 1;}
                    return b;}
                a();
                """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (MissingConditionError e) {
            String expectedErrorMessage = "{ MissingConditionError } : While expression is not boolean at line 4 token 3 with node type Expression";
            assertEquals(expectedErrorMessage, e.getMessage());
        }

        input = """
                def int a(){
                    int b = 0;
                    int i = 0;
                    while (i < 10){
                        b = b + 1;}
                    return b;}
                a();
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            assert false;
        }
    }
    @Test
    public void complexTestFunctionCall() throws Exception {
        String input = """
                def int a(int c){
                    return 10;}
                def int b(){
                    return 20;}
                a(b());
                """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            assert false;
        }

        input = """
                def int a(int c){
                    return 10;}
                def int b(){
                    return 20;}
                a(b() + 10.0);
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (ArgumentError e) {
            String expectedErrorMessage = "{ ArgumentError } : Function call parameters do not match function declaration: \"<int>\" != \"<float>\" at line 5 token 1 with node type FunctionCall";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }
    @Test
    public void complexTestFunctionDef() throws Exception {
        String input = """
                def float main(int b, int[] c) {
                    int value;
                    int i;
                    for (i=1, i<100, i = i+1) {
                        while (value!=3) {
                            if (i > 10){
                                // ....
                                return 1.0;
                            } else {
                                // ....
                                return 0;
                            }
                        }
                    }
                                
                    i = (i+2)*2;
                    return i;
                }
                """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            assert false;
        }

        input = """
                def float main(int b, int[] c) {
                    int value;
                    int i;
                    for (i=1, i<100, i = i+1) {
                        while (value!=3) {
                            if (i > 10){
                                // ....
                                return 1.0;
                            } else {
                                // ....
                                return 0;
                            }
                        }
                    }
                                
                    i = (i+2)*2;
                    return i;
                }
                main(1, 2);
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (ArgumentError e) {
            String expectedErrorMessage = "{ ArgumentError } : Function call parameters do not match function declaration: \"<int[]>\" != \"<int>\" at line 19 token 1 with node type FunctionCall";
            assertEquals(expectedErrorMessage, e.getMessage());
        }

        input = """
                def float main(int b, int[] c) {
                    int value;
                    int i;
                    for (i=1, i<100, i = i+1) {
                        while (value!=3) {
                            if (i > 10){
                                // ....
                                return 1.0;
                            } else {
                                // ....
                                return "Hello World";
                            }
                        }
                    }
                                
                    i = (i+2)*2;
                    return i;
                }
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (ReturnError e) {
            String expectedErrorMessage = "{ ReturnError } : Return type does not match function return type: \"<float>\" != \"<string>\" at line 11 token 1 with node type Return";
            assertEquals(expectedErrorMessage, e.getMessage());
        }

    }
    @Test
    public void complexTestMixedExpression() throws Exception {
        String input = """
                int a = 10;
                float b = 20.0;
                bool res = a > b;
                    """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            assert false;
        }

        input = """
                int a = 10;
                float b = 20.0;
                int res = a + b;
                    """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert false;
        } catch (TypeError e) {
            String expectedErrorMessage = "{ TypeError } : Assignment type does not match declaration type: \"<int>\" != \"<float>\" at line 3 token 1 with node type Declaration";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }
    @Test
    public void complexTestConstructor() throws Exception {
        String input = """
                struct a{
                    int b;
                    int c;}
                a x = a(10, 20);
                """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assert false;
        }
    }
    @Test
    public void complexTestOperation() throws Exception{
        String input = """
                int a = 10;
                int b = 20;
                int c = a + b;
                int d = a - b;
                int e = a * b;
                int f = a / b;
                int g = a % b;
                bool h = a > b;
                bool i = a < b;
                bool j = a >= b;
                bool k = a <= b;
                bool l = a == b;
                bool m = a != b;
                float x = 10.0;
                float y = 20.0;
                float z = x + y;
                float w = x - y;
                float v = x * y;
                float u = x / y;
                bool n = x > y;
                bool o = x < y;
                bool p = x >= y;
                bool q = x <= y;
                bool r = x == y;
                bool s = x != y;
                string str = "Hello";
                string str2 = "World";
                string str3 = str + str2;
                string str4 = str + " " + str2;
                bool str5 = str == str2;
                bool str6 = str != str2;
                """;
        Parser parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, false);
            assert true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assert false;
        }
    }

}
