package compiler.Lexer;

public class Identifier extends Symbol {
    public  String Type = "Identifier";
    public  String Value = "default";
    public Identifier(String value) {
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