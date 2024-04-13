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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (TypeError e){
            String expectedErrorMessage = "Assignment type does not match declaration type:  <VarType,int> !=  <VarType,string> at line 1 token 1 with node type Declaration";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "int a = 10;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (StructError e){
            String expectedErrorMessage = "Struct already declared or reserved keyword: < while > at line 1 token 1 with node type Struct";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                struct a{
                    int b;
                    int c;}
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (OperatorError e){
            String expectedErrorMessage = "Arithmetic operation [ + ] cannot be applied to types:  <VarType,int> and  <VarType,string> at line 1 token 6 with node type ArithmeticOperation";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "int a = 10 + 10;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (OperatorError e){
            String expectedErrorMessage = "Logical operation [ && ] cannot be applied to types:  <VarType,int> and  <VarType,bool> at line 1 token 5 with node type LogicalOperation";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "bool a = true && true;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (OperatorError e){
            String expectedErrorMessage = "Comparison operation [ > ] cannot be applied to types:  <VarType,string> and  <VarType,int> at line 1 token 4 with node type ComparisonOperation";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "bool a = 10 == 10;";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (TypeError e){
            String expectedErrorMessage = "Assignment type does not match declaration type:  <VarType,bool> !=  <VarType,int> at line 1 token 1 with node type Declaration";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = "bool a = !(true);";
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (ArgumentError e){
            String expectedErrorMessage = "Function call parameters do not match function declaration:  <VarType,int> !=  <VarType,string> at line 3 token 1 with node type FunctionCall";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                def int a(int b){
                    return b*b;}
                a(10);
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (MissingConditionError e){
            String expectedErrorMessage = "If expression is not boolean at line 2 token 3 with node type Expression";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                int a = 10;
                if (a > 5){
                    a = 20;}
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        }
        catch (ReturnError e){
            String expectedErrorMessage = "Return type does not match function return type:  <VarType,int> !=  <VarType,string> at line 2 token 1 with node type Return";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
        input = """
                def int a(){
                    return 10;}
                a();
                """;
        parser = getParser(input);
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        } catch (ScopeError e) {
            String expectedErrorMessage = "Identifier not declared: < b > at line 4 token 1 with node type IdentifierAccess";
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        } catch (DeclarationError e) {
            String expectedErrorMessage = "Identifier already declared: < a > at line 4 token 1 with node type Method";
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert false;
        } catch (MissingConditionError e) {
            String expectedErrorMessage = "For expression is not boolean at line 4 token 7 with node type Expression";
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
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, true);
            assert true;
        } catch (Exception e) {
            assert false;
        }

    }



}
