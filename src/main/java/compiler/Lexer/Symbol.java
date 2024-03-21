package compiler.Lexer;

public abstract class Symbol {
    public String Type;
    public String Value;

    public String toString() {
        return super.toString();
    }

    public String getType() {
        return Type;
    }

    public String getValue() {
        return Value;
    }

    public boolean isValue() {
        return false;
    }
}