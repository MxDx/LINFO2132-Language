package compiler.Lexer;

public class MyBoolean extends Symbol{
    public MyBoolean(String value, int line) {
        super("MyBoolean", value, line);
        this.setValue(true);
    }
}
