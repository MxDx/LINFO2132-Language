package compiler.Lexer;

public class MyString extends Symbol {
    public MyString(String value, int line, int i) {
        super("MyString", value, line, i);
        this.setValue(true);
    }
}
