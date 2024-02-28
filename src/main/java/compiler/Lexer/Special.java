package compiler.Lexer;

public class Special implements Symbol{
    public static String Type = "special";
    public static String Value = "default";
    public Special(String value) {
        Value = value;
    }
    public String toString() {
        return "special(" + Value + ")";
    }
}
