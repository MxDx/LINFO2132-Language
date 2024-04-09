package compiler.Lexer;

public class MyFloat extends Symbol{
    public MyFloat(String value, int line, int i) {
        super("float", value, line, i);
        this.setValue(true);
    }
}
