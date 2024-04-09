package compiler.SemanticAnalysis;

import compiler.Parser.Parser;

public class SemanticAnalysis {
    public SemanticAnalysis(Parser parser) {
        parser.getRoot().accept(new TypeVisitor());
    }

    public static void SemanticException(String typeError, String message) throws Exception {
       String str = typeError + ":\n" + message;
    }
}
