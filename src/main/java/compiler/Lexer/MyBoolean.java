package compiler.Lexer;

public class MyBoolean implements Symbol{
    public  String Type = "MyBoolean";
    public  String Value = "default";
    public MyBoolean(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }
}
