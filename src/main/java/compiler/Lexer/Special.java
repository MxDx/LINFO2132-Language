package compiler.Lexer;

public class Special implements Symbol{
    public  String Type = "special";
    public  String Value = "default";
    public Special(String value) {
        Value = value;
    }
    public String toString() {
        return "Special(" + Value + ")";
    }
}
