package compiler.Lexer;

import java.util.Objects;

public abstract class Symbol {
    public String Type;
    public String Value;
    public int Line;
    private boolean isValue = false;

    public Symbol(String type, String value, int line) {
        Type = type;
        Value = value;
        this.Line = line;
    }

    public String getType() {
        return Type;
    }

    public String getValue() {
        return Value;
    }

    public boolean isValue() {
        return isValue;
    }

    public void setValue(boolean value) {
        isValue = value;
    }

    public int getLine() {
        return Line;
    }

    @Override
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol) {
            Symbol s = (Symbol) obj;
            boolean type = Objects.equals(this.getType(), s.getType());
            boolean value = Objects.equals(this.getValue(), s.getValue());
            return type && value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Type, Value);
    }
}