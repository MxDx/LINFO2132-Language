package compiler.Lexer;

public class VarType extends Symbol{
    private Boolean Vector = false;
    private Boolean Final = false;

    public VarType(String value, int line, int i) {
        super("VarType", value, line, i);
    }
    public VarType(String value) {
        super("VarType", value, 0, 0);
        Value = value;
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
