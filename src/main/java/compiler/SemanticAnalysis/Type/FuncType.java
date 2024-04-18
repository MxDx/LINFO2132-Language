package compiler.SemanticAnalysis.Type;

import java.util.ArrayList;

public class FuncType extends IdentifierType {
    private final IdentifierType returnType;
    private final ArrayList<IdentifierType> parameters;
    private final boolean isVoid;

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
        str.append("{\n");
        str.append("\"FuncType\": {\n");
        if (isVoid) {
            str.append("\"returnType\": ").append(" void").append(",\n");
        } else {
            str.append("\"returnType\": ").append(returnType.toString()).append(",\n");
        }
        str.append("\"parameters\": {\n");
        int i = 0;
        for (IdentifierType parameter : parameters) {
            str.append("\"parameter").append(i).append("\": ");
            str.append(parameter.toString()).append(",\n");
            i++;
        }
        if (!parameters.isEmpty()) {
            str.deleteCharAt(str.length() - 2);
        }
        str.append("}\n");
        str.append("}\n");
        str.append("}");
        return str.toString();
    }
}
