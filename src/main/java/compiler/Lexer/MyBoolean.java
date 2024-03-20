package compiler.Lexer;

public class MyBoolean extends Symbol{
    public  String Type = "MyBoolean";
    public  String Value = "default";
    public MyBoolean(String value) {
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
