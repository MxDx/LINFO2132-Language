package compiler.Lexer;

public class MyString implements Symbol {
    public  String Type = "string";
    public  String Value = "default";
    public MyString(String value) {
        Value = value;
    }
    public String toString() {
        return "MyString(" + Value + ")";
    }
}
