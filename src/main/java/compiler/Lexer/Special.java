package compiler.Lexer;

public class Special extends Symbol{
    public  String Type = "Special";
    public  String Value = "default";
    public Special(String value) {
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
