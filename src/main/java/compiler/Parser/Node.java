package compiler.Parser;
import compiler.Lexer.*;

public abstract class Node {
    public Parser parser;
    public Node(Parser parser) {
        this.parser = parser;
    }
}
