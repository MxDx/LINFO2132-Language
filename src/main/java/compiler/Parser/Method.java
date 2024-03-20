package compiler.Parser;


import compiler.Lexer.Identifier;
import compiler.Lexer.VarType;

import java.util.ArrayList;

public class Method extends Node{
    public Identifier name;
    public VarType returnType;
    ArrayList<Parameter> parameters;
    public Block block;

    public Method(Parser parser) {
        super(parser);
    }
    public Node parse() throws Exception {
        return null;
    }
}
