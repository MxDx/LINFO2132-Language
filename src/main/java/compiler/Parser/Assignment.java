package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Special;
import compiler.Lexer.Symbol;
import compiler.SemanticAnalysis.IdentifierTable;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;

public class Assignment extends Node {
    public Node expression;
    ArrayList<Symbol> EOF = new ArrayList<>() {{
        add(new Special(";"));
    }};

    public Assignment(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
        parser.match(Parser.EQUALS);
    }

    public Assignment(Node node, Node expression) {
        super(node);
        this.expression = expression;
    }

    public Assignment parse() throws Exception {
        expression = new Expression(parser).setEOF(EOF).parse();
        return this;
    }

    public Assignment setEOF(ArrayList<Symbol> EOF) {
        this.EOF = EOF;
        return this;
    }

    @Override
    public String toString() {
        return "\"Assignment\": {\n"
                + "\"expression\": {" + expression.toString() + "}\n"
                + '}';
    }

    public Node getExpression() {
        return expression;
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        IdentifierType ret = expression.accept(visitor);
        this.setType(ret.getType().getType().getValue());
        return ret;
    }
    public void accept(CodeGenerator generator, String identifier) {
        generator.generateCode(this, identifier);
    }

}
