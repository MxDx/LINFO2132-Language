package compiler.Lexer;

public class MyString implements Symbol {
    public static String Type = "string";
    public static String Value = "default";
    public MyString(String value) {
        Value = value;
    }
    public String toString() {
        return "string(" + Value + ")";
    }
}
