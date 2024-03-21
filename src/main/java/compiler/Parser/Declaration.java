package compiler.Parser;

import java.util.Objects;

public class Declaration extends Node{
    String type;
    String identifier;
    Node assignment;

    public Declaration(Parser parser) throws Exception {
        super(parser);
        type = parser.currentToken.getValue();
        parser.getNext();
        if (!Objects.equals(parser.currentToken.getType(), "Identifier")){
            throw new Exception("Invalid Identifier");
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
            throw new Exception("Invalid Declaration");
        }
        parser.getNext();
        return this;
    }
}
