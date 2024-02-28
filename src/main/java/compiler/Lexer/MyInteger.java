package compiler.Lexer;

public class MyInteger implements Symbol{
    public static String Type = "integer";
    public static String Value = "default";
    public MyInteger(String value) {
        Value = value;
    }
    public String toString() {
        return "Integer(" + Value + ")";
    }
}
