package compiler.Lexer;

public class VarType extends Symbol {
    private int vectorDepth = 0;
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

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof VarType s) {
            VarType varType = (VarType) obj;
            if (varType.isVector() == this.isVector() && varType.isFinal() == this.isFinal()) {
                return true;
            }
        }
        return false;
    }

    public void setVector() {
        vectorDepth++;
    }
    public Boolean isVector() {
        return vectorDepth > 0;
    }

    public void setVectorDepth(int vectorDepth) {
        this.vectorDepth = vectorDepth;
    }

    public int getVectorDepth() {
        return vectorDepth;
    }

    public void setFinal(Boolean finalValue) {
        Final = finalValue;
    }
    public Boolean isFinal() {
        return Final;
    }
}
