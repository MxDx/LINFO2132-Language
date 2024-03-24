package compiler.Lexer;

public class Special extends Symbol{
    public Special(String value, int line) {
        super("Special", value, line);
    }

    public Special(String value) {
        super("Special", value, 0);
    }
}
