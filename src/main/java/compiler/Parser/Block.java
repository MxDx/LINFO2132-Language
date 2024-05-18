package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

public class Block extends Node{
    public Statements statements;
    public Block(Parser parser) {
        super(parser);
    }
    public Block parse() throws Exception {
        parser.match(Parser.OPEN_BRACES);
        statements = new Statements(parser).setEOF(Parser.CLOSE_BRACES).parse();
        parser.match(Parser.CLOSE_BRACES);
        return this;
    }

    public Statements getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return statements.toString();
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }
    @Override
    public int accept(CodeGenerator generator) {
        return generator.generateCode(this.statements);
    }
}
