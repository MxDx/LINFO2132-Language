package compiler.Lexer;

public class Keyword extends Symbol {
    public Keyword(String value, int line, int i) {
        super("Keyword", value, line, i);
    }
    public Keyword(String value) {
        super("Keyword", value, 0, 0);
    }
}