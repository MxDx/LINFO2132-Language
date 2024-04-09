package compiler.Lexer;

public class VarType extends Symbol {
    private Boolean Vector = false;
    private Boolean Final = false;

    public VarType(String value, int line, int i) {
        super("VarType", value, line, i);
    }
    public VarType(String value) {
        super("VarType", value, 0, 0);
        Value = value;
    }
    @Override
    public String toString() {
        if (isVector()) {
            return "<" + this.Type + ","+ this.Value + "[]>";
        }
        if (isFinal()) {
            return "<" + this.Type + ",final "+ this.Value + ">";
        }
        return "<" + this.Type + ","+ this.Value + ">";

    }

    public void setVector() {
        Vector = true;
    }
    public Boolean isVector() {
        return Vector;
    }
    public void setFinal(Boolean finalValue) {
        Final = finalValue;
    }
    public Boolean isFinal() {
        return Final;
    }
}
