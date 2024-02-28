/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package compiler;
import compiler.Lexer.Lexer;
import compiler.Lexer.Symbol;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No input file");
            return;
        }
        boolean lexer = args[0].equals("-lexer");
        if (lexer && args.length < 2) {
            System.out.println("No input file");
            return;
        }
        String inputPath = args[(lexer ? 1 : 0)];
        System.out.printf("Input file: %s\n", inputPath);
        System.out.printf("Lexer: %s\n", lexer ? "true" : "false");
        LinkedList<Symbol> symbolList = new LinkedList<Symbol>();
        try {
            File myObj = new File(inputPath);
            Scanner myReader = new Scanner(myObj);
            StringBuilder sb = new StringBuilder();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                StringReader reader = new StringReader(data);
                Lexer lex = new Lexer(reader);
                symbolList.addAll(lex.symbolList);
            }
            myReader.close();
            if (lexer) {
                System.out.println(symbolList);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred. File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
