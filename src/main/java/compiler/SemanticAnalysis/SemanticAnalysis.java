package compiler.SemanticAnalysis;

import compiler.Parser.Parser;
import compiler.Parser.Node;
import compiler.SemanticAnalysis.Errors.*;
import org.json.JSONObject;

public class SemanticAnalysis {
    public SemanticAnalysis(Parser parser) throws Exception{
        TypeVisitor visitor = new TypeVisitor();
        parser.getRoot().accept(visitor);

    }
    public SemanticAnalysis(Parser parser, Boolean show) throws Exception{
        TypeVisitor visitor = new TypeVisitor();
        parser.getRoot().accept(visitor);
        if (show) {
            System.out.println("Semantic Analysis Complete");
            System.out.println("No errors found");
            String json = visitor.getTable().toString();
            try {
                JSONObject jsonObject = new JSONObject(json);
                System.out.println(jsonObject.toString(4));
            } catch (Exception e) {
                System.out.println(json);
            }
        }
    }

    public static void SemanticException(String typeError, String message, Node node) throws Exception {
       String str = message;
       str += " at line " + node.getLine() + " token " + node.getTokenNumber();
       str += " with node type " + node.getClass().getSimpleName();

       switch (typeError) {
           case "TypeError":
                throw new TypeError(str);
           case "StructError":
                throw new StructError(str);
           case "OperatorError":
                throw new OperatorError(str);
           case "ArgumentError":
                throw new ArgumentError(str);
           case "MissingConditionError":
                throw new MissingConditionError(str);
           case "ReturnError":
                throw new ReturnError(str);
           case "ScopeError":
               throw new ScopeError(str);
           case "DeclarationError":
               throw new DeclarationError(str);
           default:
                throw new Exception(str);
       }

       //throw new Exception(str);
    }
}

