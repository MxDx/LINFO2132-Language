package compiler.Lexer;

public class MyFloat extends Symbol{
    public MyFloat(String value, int line, int i) {
        super("MyFloat", value, line, i);
        this.setValue(true);
    }
}
