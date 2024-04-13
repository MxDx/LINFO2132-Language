package compiler.SemanticAnalysis;

public class SemanticErrors {
}

class TypeError extends Exception {
    public TypeError(String message) {
        super(message);
    }
}

class StructError extends Exception {
    public StructError(String message) {
        super(message);
    }
}

class OperatorError extends Exception {
    public OperatorError(String message) {
        super(message);
    }
}

class ArgumentError extends Exception {
    public ArgumentError(String message) {
        super(message);
    }
}

class MissingConditionError extends Exception {
    public MissingConditionError(String message) {
        super(message);
    }
}

class ReturnError extends Exception {
    public ReturnError(String message) {
        super(message);
    }
}

class ScopeError extends Exception {
    public ScopeError(String message) {
        super(message);
    }
}