package compiler.Lexer;

public class MyFloat extends Symbol{
    public MyFloat(String value, int line) {
        super("MyFloat", value, line);
        this.setValue(true);
    }
}
