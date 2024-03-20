package compiler.Lexer;

public class MyString extends Symbol {
    public  String Type = "MyString";
    public  String Value = "default";
    public MyString(String value) {
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
