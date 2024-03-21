package compiler.Parser;

import compiler.Lexer.*;


public class Parser {
    Symbol currentToken;
    Node root;
    Lexer lexer;
    Symbol lookahead;

    public Parser(Lexer lexer) throws Exception {
        this.lexer = lexer;
        currentToken = lexer.getNextSymbol();
        lookahead = lexer.getNextSymbol();
        root = new Starting(this).parse();
    }
    public void getNext() throws Exception {
        currentToken = lookahead;
        lookahead = lexer.getNextSymbol();
    }
    public Symbol match(Symbol token) throws Exception{
        if (token == null) {
            throw new Exception("Syntax Error");
        }
        if (currentToken == token) {
            this.getNext();
            return token;
        } else {
            throw new Exception("Syntax Error");
        }
    }

}
