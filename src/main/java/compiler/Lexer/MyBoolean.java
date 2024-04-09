package compiler.Lexer;

public class MyBoolean extends Symbol{
    public MyBoolean(String value, int line, int i) {
        super("bool", value, line, i);
        this.setValue(true);
    }
}
