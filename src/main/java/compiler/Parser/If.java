package compiler.Parser;

import compiler.CodeGenerator.CodeGenerator;
import compiler.Lexer.Keyword;
import compiler.Lexer.Symbol;
import compiler.SemanticAnalysis.Type.IdentifierType;
import compiler.SemanticAnalysis.TypeVisitor;

import java.util.ArrayList;
import java.util.Objects;

public class If extends Node{
    final static Symbol ELSE = new Keyword("else");
    ArrayList<Symbol> EOF = new ArrayList<>(){{
        add(Parser.CLOSE_PARENTHESES);
    }};
    public Node expression;
    public Block block;
    Node elseStatement;
    public If(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Node getExpression() {
        return expression;
    }
    public Node parse() throws Exception {
        parser.match(Parser.OPEN_PARENTHESES);
        expression = new Expression(parser).setEOF(EOF).parse();
        parser.match(Parser.CLOSE_PARENTHESES);
        block = new Block(parser).parse();
        if (Objects.equals(parser.currentToken, ELSE)){
            elseStatement = new Else(parser).parse();
        }
        return this;
    }

    @Override
    public String toString() {
        String str =  "\"IF_Statement\": {\n"
                + "\"expression\": {\n"+ expression.toString() + "\n},\n"
                + "\"block\": " + block.toString() + "\n";
        if (elseStatement != null){
            str += ",\n" + elseStatement;
        }
        str += '}';
        return str;
    }

    @Override
    public IdentifierType accept(TypeVisitor visitor) throws Exception {
        return visitor.visit(this);
    }

    @Override
    public int accept(CodeGenerator generator) {
        return generator.generateCode(this);
    }

    public Node getBlock() {
        return block;
    }
    public Node getElseStatement() {
        return elseStatement;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    private static class Else extends Node {
        Block block;

        public Else(Parser parser) throws Exception {
            super(parser);
            parser.getNext();
        }
        public Node parse() throws Exception {
            block = new Block(parser).parse();
            return this;
        }

        public Node getBlock() {
            return block;
        }

        @Override
        public String toString() {
            return "\"ELSE_Statement\": {\n"
                    + "\"block\": " + block.toString() + "\n"
                    + '}';
        }

        @Override
        public IdentifierType accept(TypeVisitor visitor) throws Exception {
            return block.accept(visitor);
        }

        @Override
        public int accept(CodeGenerator generator) {
            return generator.generateCode(this.block);
        }
    }
}
