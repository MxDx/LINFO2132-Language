package compiler.Lexer;

public class VarType implements Symbol{
    public  String Type = "VarType";
    public  String Value = "default";
    public VarType(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }
}
