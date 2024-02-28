package compiler.Lexer;

public class VarType implements Symbol{
    public  String Type = "varType";
    public  String Value = "default";
    public VarType(String value) {
        Value = value;
    }
    public String toString() {
        return "VarType(" + Value + ")";
    }
}
