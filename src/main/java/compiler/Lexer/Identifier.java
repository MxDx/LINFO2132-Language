package compiler.Lexer;

public class Identifier implements Symbol {
    public  String Type = "identifier";
    public  String Value = "default";
    public Identifier(String value) {
        Value = value;
    }
    public String toString() {
        return "Identifier(" + Value + ")";
    }

}