package compiler.SemanticAnalysis.Type;

import compiler.Lexer.VarType;
import compiler.Parser.Declaration;
import compiler.Parser.Struct;
import compiler.SemanticAnalysis.IdentifierTable;
import compiler.SemanticAnalysis.SemanticAnalysis;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;
import java.util.HashMap;

public class StructType extends Type {

    private final HashMap<String, IdentifierType> fields;
    private final ArrayList<IdentifierType> fieldsList;
    public StructType(Struct struct, TypeVisitor visitor) throws Exception {
        super(new VarType(struct.getIdentifier()));
        fields = new HashMap<>();
        fieldsList = new ArrayList<>();
        for (Declaration declaration : struct.getDeclarations()) {
            String identifier = declaration.getIdentifier();
            VarType varType = declaration.getType();
            Type type = visitor.getTable().getType(varType.getValue());
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError", "Field type does not exist", declaration);
            }
            if (fields.containsKey(identifier)) {
                SemanticAnalysis.SemanticException("TypeError", "Field already declared", declaration);
            }
            IdentifierType identifierType = new IdentifierType(type, varType);
            declaration.setType(identifierType.getType().getType().getValue());
            if (identifierType.isVector()) {
                declaration.setType(identifierType.getType().getType().getValue() + "[]".repeat(identifierType.getVectorDepth()));
            }
            fields.put(identifier, identifierType);
            fieldsList.add(identifierType);
        }
    }

    public HashMap<String, IdentifierType> getFields() {
        return fields;
    }

    public IdentifierType getField(String field) {
        return fields.get(field);
    }

    public ArrayList<IdentifierType> getFieldsList() {
        return fieldsList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StructType structType) {
            if (fields.size() != structType.fields.size()) {
                return false;
            }
            for (String field : fields.keySet()) {
                if (!structType.fields.containsKey(field)) {
                    return false;
                }
                if (!fields.get(field).equals(structType.fields.get(field))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("{\n\"StructType\": {\n");
        for (String field : fields.keySet()) {
            str.append("\"").append(field);
            str.append("\"").append(": ");
            str.append(fields.get(field)).append(",\n");
        }
        if (!fields.isEmpty()) {
            str.replace(str.length() - 2, str.length(), "\n"); // remove last ",\n
        }
        str.append("}\n");
        str.append("}");
        return str.toString();
    }
}
