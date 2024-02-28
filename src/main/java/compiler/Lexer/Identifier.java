package compiler.Lexer;

public class Identifier implements Symbol {
    public static String Type = "identifier";
    public static String Value = "default";
    public Identifier(String value) {
        Value = value;
    }
    public String toString() {
        return "identifier(" + Value + ")";
    }

}