package compiler.SemanticAnalysis;

import compiler.Parser.Parser;
import compiler.Parser.Node;

public class SemanticAnalysis {
    public SemanticAnalysis(Parser parser) throws Exception{
        TypeVisitor visitor = new TypeVisitor();
        parser.getRoot().accept(visitor);
        System.out.println("Semantic Analysis Complete");
        System.out.println("No errors found");
        System.out.println(visitor.table.toString());
    }

    public static void SemanticException(String typeError, String message, Node node) throws Exception {
       String str = "{" + typeError + "}" + " " + message;
       str += " at line " + node.getLine() + " token " + node.getTokenNumber();
       str += " with node type " + node.getClass().getSimpleName();

       throw new Exception(str);
    }
}
