package compiler.Lexer;

public class Identifier extends Symbol {
    public  String Type = "Identifier";
    public  String Value = "default";
    public Identifier(String value, int line) {
        super("Identifier", value, line);
    }
}