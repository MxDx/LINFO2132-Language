package compiler.Parser;

import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

public abstract class Node {
    public Parser parser;
    private final int line;
    private final int tokenNumber;
    public Node(Parser parser) {
        this.parser = parser;
        this.line = parser.currentToken.getLine();
        this.tokenNumber = parser.currentToken.getTokenNumber();
    }

    public int getLine() {
        return line;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit();
    }
}
