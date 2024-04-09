package compiler.SemanticAnalysis;

import compiler.Lexer.VarType;
import compiler.Parser.Declaration;
import compiler.Parser.Struct;

import java.util.HashMap;

public class StructType extends Type {

    private HashMap<String, Type> fields;
    private IdentifierTable table;

    public StructType(StructType structType) {
        super(null);
        fields = (HashMap<String, Type>) structType.getFields().clone();
    }

    public StructType(Struct struct, TypeVisitor visitor) throws Exception {
        super(null);
        table = visitor.table;
        fields = new HashMap<>();
        for (Declaration declaration : struct.getDeclarations()) {
            String identifier = declaration.getIdentifier();
            VarType varType = declaration.getType();
            Type type = visitor.table.getType(varType.getValue());
            if (type == null) {
                SemanticAnalysis.SemanticException("TypeError", "Field type does not exist", declaration);
            }
            if (fields.containsKey(identifier)) {
                SemanticAnalysis.SemanticException("TypeError", "Field already declared", declaration);
            }
            type = new UnaryType(varType);
            fields.put(identifier, type);
        }
    }

    public HashMap<String, Type> getFields() {
        return fields;
    }

    public Type getField(String field) {
        Type type = fields.get(field);
        if (type.isVector()) {
            int vectorDepth = type.getVectorDepth();
            type = table.getType(type.getType().getValue()).clone();
            type.setVectorDepth(vectorDepth);
            return type;
        }
        return table.getType(type.getType().getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StructType) {
            StructType structType = (StructType) obj;
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
        String str = "StructType: {\n";
        for (String field : fields.keySet()) {
            str += "\t\t";
            str += field + ": " + fields.get(field) + "\n";
        }
        str += "\t}";
        return str;
    }

    @Override
    public Type clone() {
        return new StructType(this);
    }
}
