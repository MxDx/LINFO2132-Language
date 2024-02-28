package compiler.Lexer;

public class Keyword implements Symbol {
    public static String Type = "keyword";
    public static String Value = "default";
    public Keyword(String value) {
        Value = value;
    }
    public String toString() {
        return "keyword(" + Value + ")";
    }
}