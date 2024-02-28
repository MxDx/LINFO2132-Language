package compiler.Lexer;

public interface Symbol {
    public  String Type = "default";
    public  String Value = "default";
    public String toString();
    public default Boolean equals(Symbol s){
        return this.Type.equals(s.Type) && this.Value.equals(s.Value);
    };
}
