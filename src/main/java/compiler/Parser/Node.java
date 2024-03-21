package compiler.Parser;

public abstract class Node {
    public Parser parser;
    public Node(Parser parser) {
        this.parser = parser;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
