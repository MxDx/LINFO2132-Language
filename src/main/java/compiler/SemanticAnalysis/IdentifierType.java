package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;

public class IdentifierType {
    protected Type type;
    private int vectorDepth;
    private boolean Final;
    public IdentifierType(Type type) {
        this.type = type;
        this.vectorDepth = 0;
        this.Final = false;
    }

    public IdentifierType(Type type, VarType varType) {
        this(type);
        this.vectorDepth = varType.getVectorDepth();
        this.Final = varType.isFinal();
    }

    private IdentifierType(IdentifierType identifierType) {
        this.type = identifierType.getType();
        this.vectorDepth = identifierType.getVectorDepth();
        this.Final = identifierType.isFinal();
    }

    public void setVarType(VarType varType) {
        this.vectorDepth = varType.getVectorDepth();
        this.Final = varType.isFinal();
    }

    public Type getType() {
        return type;
    }

    public int getVectorDepth() {
        return vectorDepth;
    }

    public void setVectorDepth(int vectorDepth) {
        this.vectorDepth = vectorDepth;
    }

    public IdentifierType vectorPass() {
        IdentifierType identifierType = new IdentifierType(this);
        identifierType.setVectorDepth(vectorDepth - 1);
        return identifierType;
    }

    public boolean isVector() {
        return vectorDepth > 0;
    }

    public boolean isFinal() {
        return Final;
    }

    public void setFinal(boolean Final) {
        this.Final = Final;
    }

    public boolean isVoid() {
        return type == null;
    }

    public IdentifierType getNext() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IdentifierType identifierType) {
            return type.equals(identifierType.getType()) && vectorDepth == identifierType.getVectorDepth();
        }
        return false;
    }

    @Override
    public String toString() {
        String str = "";
        if (isVector()) {
            str += "[]";
        }
        if (isFinal()) {
            str = ", final";
        }
        str += ' ';
        if (type != null) {
            str += type.toString();
        } else {
            str += "void";
        }
        return str;
    }
}
