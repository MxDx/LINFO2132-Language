package compiler.Lexer;

public class MyFloat implements Symbol{
    public  String Type = "float";
    public  String Value = "default";
    public MyFloat(String value) {
        Value = value;
    }
    public String toString() {
        return "MyFloat(" + Value + ")";
    }
}
