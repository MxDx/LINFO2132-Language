package compiler.SemanticAnalysis.Type;

import java.util.ArrayList;

public class FuncType extends IdentifierType {
    private IdentifierType returnType;
    private ArrayList<IdentifierType> parameters;
    private boolean isVoid;

    public FuncType(IdentifierType returnType, ArrayList<IdentifierType> parameters) {
        super(null);
        if (returnType.getType() == null ) {
            isVoid = true;
        }
        else {
            isVoid = false;
            type = returnType.getType();
            this.setVarType(returnType.getType().getType());
        }
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public IdentifierType getReturnType() {
        return returnType;
    }

    public ArrayList<IdentifierType> getParameters() {
        return parameters;
    }

    public boolean isVoid() {
        return isVoid;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("FuncType: {\n");
        str.append("\t\treturnType: ").append(returnType.toString()).append("\n");
        str.append("\t\tparameters: [\n");
        for (IdentifierType parameter : parameters) {
            str.append("\t\t\t");
            str.append(parameter.toString()).append("\n");
        }
        str.append("\t]\n");
        str.append("\t}");
        return str.toString();
    }
}
