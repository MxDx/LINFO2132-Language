package compiler.Lexer;

public class MyBoolean extends Symbol{
    public MyBoolean(String value, int line, int i) {
        super("MyBoolean", value, line, i);
        this.setValue(true);
    }
}
