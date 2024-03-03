package compiler.Lexer;

public class MyFloat implements Symbol{
    public  String Type = "MyFloat";
    public  String Value = "default";
    public MyFloat(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }
}
