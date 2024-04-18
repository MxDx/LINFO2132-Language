/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package compiler;
import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;
import compiler.Parser.Parser;
import compiler.SemanticAnalysis.SemanticAnalysis;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

public class Compiler {

    private final static HashSet<String> keywords = new HashSet<>() {
        {
            add("void");
            add("final");
            add("if");
            add("else");
            add("while");
            add("for");
            add("return");
            add("true");
            add("false");
            add("struct");
        }
    };

    private final static HashSet<String> basicTypes = new HashSet<>() {
        {
            add("int");
            add("float");
            add("string");
            add("bool");
        }
    };

    public static void main(String[] args) {

        /*if (args.length < 1) {
            System.out.println("No input file");
            return;
        }

        boolean showLexer = args[0].equals("-lexer");
        boolean showParser = args[0].equals("-parser");
        boolean showSemantic = args[0].equals("-semantic");
        System.out.println("showLexer: " + showLexer);
        System.out.println("showParser: " + showParser);
        if ((showLexer ||showParser) && args.length < 2) {
            System.out.println("No input file");
            return;
        }*/

        //String inputPath = args[(showLexer||showParser ? 1 : 0)];
        String inputPath = "src/main/java/compiler/test.txt";
        boolean showLexer = false;
        boolean showParser = true;
        boolean showSemantic = true;
        System.out.println("inputPath: " + inputPath); //LOCAL: String inputPath = "src/main/java/compiler/test.txt";
        Lexer lex = lexerGetter(inputPath, showLexer);
        Parser parser = parserGetter(lex, showParser);
        semanticAnalysisGetter(parser, showSemantic);
    }

    public static Lexer lexerGetter(String inputPath,boolean showLexer) {
            LinkedList<Symbol> symbolList = new LinkedList<>();
            try {
                File myObj = new File(inputPath);
                Reader reader = new FileReader(myObj);
                Stack<Reader> std = getStdLib();
                Lexer lex = new Lexer(reader, std);
                if (showLexer) {
                    while (true) {
                        Symbol s = lex.getNextSymbol();
                        if (s == null) {
                            break;
                        }
                        symbolList.add(s);
                    }
                    System.out.println(symbolList);
                }
                return lex;
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred. File not found.");
                e.printStackTrace();
            }  catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
    }

    public static Parser parserGetter(Lexer lex,boolean showParser) {
        try {
            Parser parser = new Parser(lex);
            if (showParser) {
                System.out.println(parser);

                // Write to file
                BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/compiler/output.txt"));
                writer.write(parser.toString());
                writer.close();
            }
            return parser;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void semanticAnalysisGetter(Parser parser, boolean showSemantic) {
        try {
            SemanticAnalysis semanticAnalysis = new SemanticAnalysis(parser, showSemantic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HashSet<String> getKeywords() {
        return keywords;
    }

    public static HashSet<String> getBasicTypes() {
        return basicTypes;
    }

    private static Stack<Reader> getStdLib() {
        // Getting all the file from the stdlib folder with .pedro extension
        File folder = new File("src/main/java/compiler/std");
        File[] listOfFiles = folder.listFiles();
        Stack<Reader> stdLibs = new Stack<>();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".pedro")) {
                try {
                    stdLibs.push(new FileReader(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return stdLibs;
    }
}
