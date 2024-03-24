package compiler.Lexer;

public class MyInteger extends Symbol{
    public MyInteger(String value, int line, int i) {
        super("MyInteger", value, line, i);
        this.setValue(true);
    }
}
