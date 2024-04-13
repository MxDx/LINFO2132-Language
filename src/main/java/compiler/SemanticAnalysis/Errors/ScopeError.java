package compiler.SemanticAnalysis.Errors;

public class ScopeError extends Exception {
    public ScopeError(String message) {
        super(message);
    }
}