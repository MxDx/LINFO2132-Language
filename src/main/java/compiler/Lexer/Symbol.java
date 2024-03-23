package compiler.Lexer;

import java.util.Objects;

public abstract class Symbol {
    public String Type;
    public String Value;
    public int line;

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

    public int getLine() {
        return line;
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
}