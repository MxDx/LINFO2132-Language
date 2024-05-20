package compiler.CodeGenerator;

import java.util.HashMap;

public class StackTable {
    HashMap<String, Integer> variableMap = new HashMap<>();
    HashMap<String, String> typeMap = new HashMap<>();
    StackTable parent;
    Integer stackPointer = 0;
    public StackTable(StackTable parent) {
        this.parent = parent;
        //this.stackPointer = parent.stackPointer;
        stackPointer = 0;
    }
    public StackTable() {
        this.parent = null;
    }
    public int getVariable(String identifier) {
        if (variableMap.containsKey(identifier)) {
            return variableMap.get(identifier);
        }
        if (parent != null) {
            return parent.getVariable(identifier);
        }
        return -1;
    }

    public HashMap<String, Integer> getVariableMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.putAll(variableMap);
        if (parent != null) {
            map.putAll(parent.getVariableMap());
        }
        return map;
    }

    public String getType(String identifier) {
        if (typeMap.containsKey(identifier)) {
            return typeMap.get(identifier);
        }
        if (parent != null) {
            return parent.getType(identifier);
        }
        return null;
    }
    public boolean addVariable(String identifier, int value) {
        if (variableMap.containsKey(identifier)) {
            return false;
        }
        variableMap.put(identifier, value);
        return true;
    }
    public boolean addVariable(String identifier) {
        if (variableMap.containsKey(identifier)) {
            return false;
        }
        variableMap.put(identifier, stackPointer++);
        return true;
    }
    public boolean addVariableType(String identifier, String type) {
        if (typeMap.containsKey(identifier)) {
            return false;
        }
        typeMap.put(identifier, type);
        return true;
    }
}
