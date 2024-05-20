package compiler.Parser;

import compiler.Compiler;
import compiler.Lexer.Lexer;

import java.util.Stack;

public class Import extends Node {
    public Import(Parser parser) {
        super(parser);
    }
    public Node parse() throws Exception {
        parser.getNext();
        String path = parser.getImportPath();
        path += parser.currentToken.getValue();
        path += ".pedro";
        Lexer lexer = Compiler.lexerGetter(path, false, true);
        Parser newParser = Compiler.parserGetter(lexer, false, path.substring(0, path.lastIndexOf("/") + 1));
        parser.getNext();
        parser.match(Parser.SEMICOLON);
        parser.isImport = true;
        return newParser.getRoot().getStatements();
    }
}
