package compiler.Lexer;

public class MyInteger extends Symbol{
    public MyInteger(String value, int line) {
        super("MyInteger", value, line);
        this.setValue(true);
    }
}
