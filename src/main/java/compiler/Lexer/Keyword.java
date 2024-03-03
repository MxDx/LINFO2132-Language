package compiler.Lexer;

public class Keyword implements Symbol {
    public  String Type = "Keyword";
    public  String Value = "default";
    public Keyword(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }
}