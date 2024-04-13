package compiler.SemanticAnalysis.Type;

import compiler.Lexer.VarType;
import compiler.Parser.Declaration;
import compiler.Parser.Struct;
import compiler.SemanticAnalysis.IdentifierTable;
import compiler.SemanticAnalysis.SemanticAnalysis;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.HashMap;

public class StructType extends Type {

    private HashMap<String, IdentifierType> fields;
    private IdentifierTable table;

    public StructType(Struct struct, TypeVisitor visitor) throws Exception {
        super(null);
        table = visitor.getTable();
        fields = new HashMap<>();
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
            fields.put(identifier, identifierType);
        }
    }

    public HashMap<String, IdentifierType> getFields() {
        return fields;
    }

    public IdentifierType getField(String field) {
        return fields.get(field);
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
}
