package compiler.Lexer;

public class VarType extends Symbol{
    private String Type = "VarType";
    private String Value = "default";
    private Boolean Vector = false;
    private Boolean Final = false;

    public VarType(String value, int line) {
        Value = value;
        this.line = line;
    }
    public VarType(String value) {
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

    public String toString() {
        String str = "<" + this.Type + ","+ this.Value;
        if (Vector) {
            str += ",Vector";
        }
        if (Final) {
            str += ",Final";
        }
        str += ">";
        return str;
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
