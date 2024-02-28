package compiler.Lexer;

public class VarType implements Symbol{
    public static String Type = "varType";
    public static String Value = "default";
    public VarType(String value) {
        Value = value;
    }
    public String toString() {
        return "varType(" + Value + ")";
    }
}
