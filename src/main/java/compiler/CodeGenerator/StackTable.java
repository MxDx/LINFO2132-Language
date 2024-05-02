package compiler.CodeGenerator;

import java.util.HashMap;

public class StackTable {
    HashMap<String, Integer> variableMap = new HashMap<>();
    StackTable parent;
    Integer stackPointer = 0;
    public StackTable(StackTable parent) {
        this.parent = parent;
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
}
