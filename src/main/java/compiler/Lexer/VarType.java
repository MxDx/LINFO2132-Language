package compiler.Lexer;

public class VarType extends Symbol{
    public  String Type = "VarType";
    public  String Value = "default";
    public VarType(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }

    @Override
    public String getType() {
        return Type;
    }

    @Override
    public String getValue() {
        return Value;
    }
}
