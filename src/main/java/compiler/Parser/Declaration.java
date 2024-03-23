package compiler.Parser;

import compiler.Lexer.Keyword;
import compiler.Lexer.Symbol;
import compiler.Lexer.VarType;

import java.util.Objects;

public class Declaration extends Node {
    public static final Symbol FINAL = new Keyword("final");
    public VarType type;
    public String identifier;
    public Node assignment;

    public Declaration(Parser parser) throws Exception {
        super(parser);
        boolean isFinal = false;
        if (parser.currentToken.equals(FINAL)){
            isFinal = true;
            parser.getNext();
        }
        type = new VarType(parser.currentToken.getValue());
        type.setFinal(isFinal);
        parser.getNext();
        if (Objects.equals(parser.currentToken.getValue(), "[")){
            type.setVector();
            parser.getNext();
            parser.match(Parser.CLOSE_BRACKETS);
        }
        if (!Objects.equals(parser.currentToken.getType(), "Identifier")){
            parser.ParserException("Invalid Identifier");
        }
        identifier = parser.currentToken.getValue();
        parser.getNext();
    }
    public Declaration parse() throws Exception {
        if (parser.currentToken.getValue().equals("=")){
            parser.getNext();
            assignment = new Expression(parser).parse();
        }

        return this;
    }

    @Override
    public String toString() {
        String str =  "\"Declaration_Statement\": { \n" +
                "\"type\": \"" + type + '\"' + ",\n" +
                "\"identifier\": \"" + identifier + '\"';
        if (assignment != null) {
            str += ",\n\"assignment\": " + assignment;
        }
        str += "\n}";
        return str;
    }
}
