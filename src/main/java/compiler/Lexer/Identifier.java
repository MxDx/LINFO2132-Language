package compiler.Lexer;

public class Identifier extends Symbol {
    public Identifier(String value, int line, int i) {
        super("Identifier", value, line, i);
    }
}