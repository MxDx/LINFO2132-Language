package compiler.Parser;

import compiler.SemanticAnalysis.Type;
import compiler.SemanticAnalysis.TypeVisitor;

public class Starting extends Node{
    Statements statements;
    public Starting(Parser parser) {
        super(parser);
    }
    public Starting parse() throws Exception {
        statements = new Statements(parser).parse();
        return this;
    }

    public Statements getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        String str = "{\n\"Starting\": \n";
        if (statements != null) {
            str += statements + "\n";
        }
        str += "\n}";
        return str;
    }

    public Type accept(TypeVisitor visitor) {
        return visitor.visit(this);
    }
}
