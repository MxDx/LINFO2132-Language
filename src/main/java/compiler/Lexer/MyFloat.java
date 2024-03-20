package compiler.Lexer;

public class MyFloat extends Symbol{
    public  String Type = "MyFloat";
    public  String Value = "default";
    public MyFloat(String value) {
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
