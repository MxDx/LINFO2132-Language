package compiler.Lexer;

public class MyString extends Symbol {
    public MyString(String value, int line, int i) {
        super("string", value, line, i);
        this.setValue(true);
    }
}
