package compiler.Lexer;

public class Special extends Symbol{
    public Special(String value, int line, int i) {
        super("Special", value, line, i);
    }

    public Special(String value) {
        super("Special", value, 0, 0);
    }
}
