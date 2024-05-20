package compiler.Parser;

import compiler.Compiler;
import compiler.Lexer.Lexer;
import compiler.Lexer.Special;

import java.util.Stack;

public class Import extends Node {
    public Import(Parser parser) {
        super(parser);
    }
    public Node parse() throws Exception {
        parser.getNext();
        String importName = parser.currentToken.getValue();
        String path = parser.getImportPath();
        if (importName.equals("libs")) {
            parser.getNext();
            parser.match(new Special("."));
            path = "src/main/java/compiler/libs/";
        }
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
