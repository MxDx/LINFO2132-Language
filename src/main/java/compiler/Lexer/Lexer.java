package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;

public class Lexer {

    public static void main(String[] args) {
        String input = "var x int = 2 ahfdjhfjdhf;";
        StringReader reader = new StringReader(input);
        while (true) {
            try {
                int c = nextUsefulChar(reader);
                String s = readIdentifier(reader, (char) c);
                System.out.println(s);
            } catch (IOException e) {
                break;
            }
        }
    }
    LinkedList<Symbol> symbolList;

    public Lexer(Reader input) {

    }
    
    public Symbol getNextSymbol() {
        return null;
    }

    private static char nextUsefulChar(Reader input) throws IOException {
        int c = input.read();
        while (c != -1) {
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                c = input.read();
                continue;
            }
            if (c == '/') {
                c = input.read();
                if (c == -1) {
                    throw new EndOfFileException();
                }
                if (c == '/') {
                    while (c != '\n') {
                        c = input.read();
                    }
                }
            }
            return (char) c;
        }
        throw new EndOfFileException();
    }

    private static String readIdentifier(Reader input, char c) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(c);
        c = (char) input.read();
        while (Character.isLetterOrDigit(c) || c == '_') {
            sb.append(c);
            c = (char) input.read();
        }
        return sb.toString();
    }

    private static class EndOfFileException extends IOException {
        public EndOfFileException() {
            super("End of file");
        }
    }
}
