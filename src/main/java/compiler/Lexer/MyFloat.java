package compiler.Lexer;

public class MyFloat extends Symbol{
    public  String Type = "MyFloat";
    public  String Value = "default";
    public MyFloat(String value, int line) {
        Value = value;
        this.line = line;
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

    @Override
    public boolean isValue() {
        return true;
    }
}
