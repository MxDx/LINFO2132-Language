package compiler.Parser;

import compiler.Lexer.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONObject;

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

    private static final ArrayList<Symbol> EOF_COMMA = new ArrayList<>(){{
        add(Parser.COMMA);
    }};
    private static final ArrayList<Symbol> EOF_CLOSE_PARENTHESES = new ArrayList<>(){{
        add(Parser.CLOSE_PARENTHESES);
    }};


    Symbol currentToken;
    Starting root;
    Lexer lexer;
    Symbol lookahead;
    HashSet<String> Structs;



    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        currentToken = lexer.getNextSymbol();
        lookahead = lexer.getNextSymbol();
        root = new Starting(this);
        Structs = new HashSet<>();
        root.parse();
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

    public Starting getRoot() {
        return root;
    }

    public void addStruct(String struct) {
        Structs.add(struct);
    }

    public boolean isStruct(String struct) {
        return Structs.contains(struct);
    }

    @Override
    public String toString() {
        String uglyJson =  root.toString();
        try {
            JSONObject json = new JSONObject(uglyJson);
            return json.toString(4);
        } catch (Exception e) {
            System.out.println("Returning ugly string: \n" + e.getMessage());
            return uglyJson;
        }
    }

    public void ParserException(String message) throws Exception {
        String error = message + "\n";
        error += "\twith token: " + currentToken.getValue() + "\n";
        error += "\tat line: " + currentToken.getLine() + "\n";
        error += "\tat token number: " + currentToken.getTokenNumber() + "\n";
        throw new ParserException(error);
    }

    public static ArrayList<Symbol> EOF_COMMA() {
        return new ArrayList<>(EOF_COMMA);
    }

    public static ArrayList<Symbol> EOF_CLOSE_PARENTHESES() {
        return new ArrayList<>(EOF_CLOSE_PARENTHESES);
    }

}

class ParserException extends Exception {
    public ParserException(String message) {
        super(message);
    }
}
