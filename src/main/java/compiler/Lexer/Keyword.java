package compiler.Lexer;

public class Keyword extends Symbol {
    public  String Type = "Keyword";
    public  String Value = "default";
    public Keyword(String value, int line) {
        Value = value;
        this.line = line;
    }
    public Keyword(String value) {
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