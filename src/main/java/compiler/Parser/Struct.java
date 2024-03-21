package compiler.Parser;

import java.util.ArrayList;

public class Struct extends  Node {
    String identifier;
    ArrayList<Declaration> declarations = new ArrayList<>();

    public Struct(Parser parser) throws Exception {
        super(parser);
        parser.getNext();
    }

    public Struct parse() throws Exception {
        if (!parser.currentToken.getType().equals("Identifier")) {
            parser.ParserException("Invalid Identifier");
        }
        identifier = parser.currentToken.getValue();
        parser.getNext();
        parser.match(Parser.OPEN_BRACES);
        while (!parser.currentToken.equals(Parser.CLOSE_BRACES)) {
            declarations.add(new Declaration(parser).parse());
            parser.match(Parser.SEMICOLON);
        }
        parser.match(Parser.CLOSE_BRACES);
        return this;
    }

    @Override
    public String toString() {
        String str = "\"Struct_Statement\": {\n"
                + "\"identifier\": \"" + identifier + "\",\n"
                + "\"declarations\": [\n";
        for (int i = 0; i < declarations.size(); i++) {
            str += "{\n" + declarations.get(i).toString() + "\n}";
            if (i != declarations.size() - 1) {
                str += ",\n";
            }
        }
        str += "\n]\n}";
        return str;
    }
}
