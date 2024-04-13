package compiler.SemanticAnalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import compiler.Compiler;
import compiler.Lexer.VarType;

public class IdentifierTable {
    private IdentifierTable parent;
    private HashMap<String, IdentifierType> tableIdentifier;
    private HashMap<String, Type> tableType;

    public IdentifierTable() {
        parent = null;
        tableIdentifier = new HashMap<>();
        tableType = new HashMap<>();
        HashSet<String> basicTypes = Compiler.getBasicTypes();
        for (String type : basicTypes) {
            tableType.put(type, new UnaryType(new VarType(type)));
        }
    }

    public IdentifierTable(IdentifierTable parent) {
        this.parent = parent;
        tableIdentifier = new HashMap<>();
        tableType = new HashMap<>();

    }

    public Type getType(String identifier) {
        if (tableType.containsKey(identifier)) {
            return tableType.get(identifier);
        }
        if (parent != null) {
            return parent.getType(identifier);
        }
        return null;
    }

    public IdentifierType getIdentifier(String identifier) {
        if (tableIdentifier.containsKey(identifier)) {
            return tableIdentifier.get(identifier);
        }
        if (parent != null) {
            return parent.getIdentifier(identifier);
        }
        return null;
    }

    public boolean addIdentifier(String identifier, IdentifierType type) {
        if (tableIdentifier.containsKey(identifier)) {
            return false;
        }
        if (tableType.containsKey(identifier)) {
            return false;
        }
        if (Compiler.getKeywords().contains(identifier)) {
            return false;
        }
        if (Compiler.getBasicTypes().contains(identifier)) {
            return false;
        }
        tableIdentifier.put(identifier, type);
        return true;
    }

    public boolean addType(String identifier, Type type) {
        if (tableType.containsKey(identifier)) {
            return false;
        }
        if (tableIdentifier.containsKey(identifier)) {
            return false;
        }
        if (Compiler.getKeywords().contains(identifier)) {
            return false;
        }
        if (Compiler.getBasicTypes().contains(identifier)) {
            return false;
        }
        tableType.put(identifier, type);
        return true;
    }

    public IdentifierTable getParent() {
        return parent;
    }

    @Override
    public String toString() {
        String str = "IdentifierTable: {\n";
        for (String identifier : tableIdentifier.keySet()) {
            str += "\t";
            str += identifier + ": " + tableIdentifier.get(identifier).toString() + ",\n";
        }
        str += "},\n";
        str += "TypeTable: {\n";
        for (String type : tableType.keySet()) {
            str += "\t";
            str += type + ": " + tableType.get(type) + "\n";
        }
        str += "}";
        return str;
    }

}
