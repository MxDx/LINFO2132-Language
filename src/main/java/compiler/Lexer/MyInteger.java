package compiler.Lexer;

public class MyInteger implements Symbol{
    public  String Type = "integer";
    public  String Value = "default";
    public MyInteger(String value) {
        Value = value;
    }
    public String toString() {
        return "MyInteger(" + Value + ")";
    }
}
