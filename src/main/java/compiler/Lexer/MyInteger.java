package compiler.Lexer;

public class MyInteger implements Symbol{
    public  String Type = "MyInteger";
    public  String Value = "default";
    public MyInteger(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }
}
