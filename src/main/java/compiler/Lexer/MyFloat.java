package compiler.Lexer;

public class MyFloat implements Symbol{
public static String Type = "float";
    public static String Value = "default";
    public MyFloat(String value) {
        Value = value;
    }
    public String toString() {
        return "float(" + Value + ")";
    }
}
