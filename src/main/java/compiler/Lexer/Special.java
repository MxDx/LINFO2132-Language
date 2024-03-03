package compiler.Lexer;

public class Special implements Symbol{
    public  String Type = "Special";
    public  String Value = "default";
    public Special(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }
}
