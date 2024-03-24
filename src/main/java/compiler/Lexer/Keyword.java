package compiler.Lexer;

public class Keyword extends Symbol {
    public Keyword(String value, int line) {
        super("Keyword", value, line);
    }
    public Keyword(String value) {
        super("Keyword", value, 0);
    }
}