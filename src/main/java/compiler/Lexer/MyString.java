package compiler.Lexer;

public class MyString extends Symbol {
    public MyString(String value, int line) {
        super("MyString", value, line);
        this.setValue(true);
    }
}
