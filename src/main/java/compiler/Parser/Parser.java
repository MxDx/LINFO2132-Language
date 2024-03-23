package compiler.Parser;

import compiler.Lexer.*;

public class Parser {
    public final static Symbol CLOSE_PARENTHESES = new Special(")");
    public final static Symbol OPEN_PARENTHESES = new Special("(");
    public final static Symbol OPEN_BRACES = new Special("{");
    public final static Symbol CLOSE_BRACES = new Special("}");
    public final static Symbol OPEN_BRACKETS = new Special("[");
    public final static Symbol CLOSE_BRACKETS = new Special("]");
    public static final Symbol SEMICOLON = new Special(";");

    public final static Symbol COMMA = new Special(",");
    public static final Symbol EQUALS = new Special("=");
    Symbol currentToken;
    Starting root;
    Lexer lexer;
    Symbol lookahead;

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        currentToken = lexer.getNextSymbol();
        lookahead = lexer.getNextSymbol();
        root = new Starting(this).parse();
    }
    public Starting getAST() {
        return root;
    }
    public void getNext() throws Exception {
        currentToken = lookahead;
        lookahead = lexer.getNextSymbol();
    }
    public void match(Symbol token) throws Exception {
        if (token == null) {
            ParserException("Syntax Error");
        }
        if (!currentToken.equals(token)) {
            ParserException("Syntax Error");
        }
        getNext();
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public void ParserException(String message) throws Exception {
        throw new Exception(message + " With token: " + currentToken.getValue() + " at line: " + currentToken.getLine());
    }

}
