package compiler.Parser;

import compiler.Lexer.*;

import java.util.ArrayList;

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

    private static ArrayList<Symbol> EOF_COMMA = new ArrayList<>(){{
        add(Parser.COMMA);
    }};
    private static ArrayList<Symbol> EOF_CLOSE_PARENTHESES = new ArrayList<>(){{
        add(Parser.CLOSE_PARENTHESES);
    }};


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
        String error = message + "\n";
        error += "\twith token: " + currentToken.getValue() + "\n";
        error += "\tat line: " + currentToken.getLine() + "\n";
        error += "\tat token number: " + currentToken.getTokenNumber() + "\n";
        throw new Exception(error);
    }

    public static ArrayList<Symbol> EOF_COMMA() {
        return new ArrayList<>(EOF_COMMA);
    }

    public static ArrayList<Symbol> EOF_CLOSE_PARENTHESES() {
        return new ArrayList<>(EOF_CLOSE_PARENTHESES);
    }

}
