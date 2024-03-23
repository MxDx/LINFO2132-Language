package compiler.Lexer;

public class MyInteger extends Symbol{
    public  String Type = "MyInteger";
    public  String Value = "default";
    public MyInteger(String value, int line) {
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
