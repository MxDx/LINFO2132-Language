package compiler.Parser;

import compiler.Lexer.VarType;

import java.util.Objects;

public class Declaration extends Node{
    VarType type;
    String identifier;
    Node assignment;

    public Declaration(Parser parser) throws Exception {
        super(parser);
        type = new VarType(parser.currentToken.getValue());
        parser.getNext();
        if (!Objects.equals(parser.currentToken.getType(), "Identifier")){
            parser.ParserException("Invalid Identifier");
        }
        identifier = parser.currentToken.getValue();
        parser.getNext();
    }
    public Node parse() throws Exception {
        if (parser.currentToken.getValue().equals("=")){
            parser.getNext();
            assignment = new Expression(parser).parse();
        }
        if(!parser.currentToken.getValue().equals(";")){
            parser.ParserException("Invalid Declaration");
        }
        parser.getNext();
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
