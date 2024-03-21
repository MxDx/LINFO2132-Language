package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;

public class Lexer {

    private char lastChar;
    private final HashSet<Character> specialCharacters = new HashSet<>() {
        {
            add('+');
            add('-');
            add('*');
            add('/');
            add('%');
            add('<');
            add('>');
            add('=');
            add('!');
            add('&');
            add('|');
            add('(');
            add(')');
            add('{');
            add('}');
            add(';');
            add(',');
            add('[');
            add(']');
            add('.');
        }
    };
    //'+', '-', '*', '/', '%', '<', '>', '=', '!', '&', '|', '(', ')', '{', '}', ';', ',', '[', ']', '.'
    public LinkedList<Symbol> symbolList;
    private final Reader input;

    public Lexer(Reader input) throws Exception {
        symbolList = new LinkedList<>();
        this.input = input;
        int c = input.read();
        if (c == -1) {
            throw new IOException("Empty file");
        }
        this.lastChar = (char) c;
    }
    
    public Symbol getNextSymbol() throws Exception {
        try {
            this.lastChar = nextUsefulChar(input);
            if (this.lastChar == '\uFFFF') {
                throw new EndOfFileException();
            }
            if (Character.isLetter(this.lastChar)) {
                String s = readToken(input);
                return switch (s) {
                    // Boolean
                    case "true" -> new MyBoolean("true");
                    case "false" -> new MyBoolean("false");
                    // VarType
                    case "int" -> new VarType("int");
                    case "float" -> new VarType("float");
                    case "char" -> new VarType("char");
                    case "string" -> new VarType("string");
                    case "bool" -> new VarType("bool");
                    // Keyword
                    case "final" -> new Keyword("final");
                    case "if" -> new Keyword("if");
                    case "else" -> new Keyword("else");
                    case "while" -> new Keyword("while");
                    case "for" -> new Keyword("for");
                    case "free" -> new Keyword("free");
                    case "return" -> new Keyword("return");
                    case "struct" -> new Keyword("struct");
                    case "def" -> new Keyword("def");
                    // Identifier
                    default -> new Identifier(s);
                };
            } else if (Character.isDigit(this.lastChar)) {
                String s = readNumber(input);
                if (this.lastChar != ' ' && this.lastChar != '\t' && this.lastChar != '\n' && !specialCharacters.contains(this.lastChar)){
                    throw new IOException("Invalid character: " + this.lastChar);
                }
                if (s.contains(".")) {
                    return new MyFloat(s);
                } else {
                    return new MyInteger(s);
                }
            } else if (this.lastChar == '"') {
                String s = readString(input);
                return new MyString(s);
            } else if (this.lastChar == '.') {
                this.lastChar = (char) input.read();
                if (Character.isDigit(this.lastChar)) {
                    String s = readNumber(input);
                    return new MyFloat("0." + s);
                } else {
                    return new Special(".");
                }
            } else {
                String s = readSpecialCharacters(input);
                return new Special(s);
            }
        } catch (EndOfFileException e) {
            System.out.println("End of file");
        }
        return null;
    }

    private char nextUsefulChar(Reader input) throws IOException {
        int c;
        while (true) {
            if (this.lastChar == ' ' || this.lastChar == '\t' || this.lastChar == '\n' || this.lastChar == '\r') {
                this.lastChar = (char) input.read();
                continue;
            }
            if (this.lastChar == '/') {
                c = input.read();
                if (c == -1) {
                    throw new EndOfFileException();
                }
                this.lastChar = (char) c;
                if (this.lastChar != '/') {
                    return '/';
                }
                while (this.lastChar != '\n') {
                    c = input.read();
                    if (c == -1) {
                        throw new EndOfFileException();
                    }
                    this.lastChar = (char) c;
                }
                c = input.read();
                if (c == -1) {
                    throw new EndOfFileException();
                }
                this.lastChar = (char) c;
                this.nextUsefulChar(input);
            }
            return this.lastChar;
        }
    }

    private String readToken(Reader input) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lastChar);
        this.lastChar = (char) input.read();

        while (Character.isLetterOrDigit(this.lastChar) || this.lastChar == '_') {
            sb.append(this.lastChar);
            this.lastChar = (char) input.read();
        }
        return sb.toString();
    }

    private static class EndOfFileException extends IOException {
        public EndOfFileException() {
            super("End of file");
        }
    }

    private String readNumber(Reader input) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(this.lastChar)) {
            sb.append(this.lastChar);
            this.lastChar = (char) input.read();
            if(this.lastChar == '.'){
                sb.append(this.lastChar);
                this.lastChar = (char) input.read();
                while (Character.isDigit(this.lastChar)) {
                    sb.append(this.lastChar);
                    this.lastChar = (char) input.read();
                }
                return sb.toString();
            }
        }
        return sb.toString();
    }
    private String readString(Reader input) throws Exception {
        StringBuilder sb = new StringBuilder();
        int c = input.read();
        if (c == -1) {
            throw new Exception("No end of string");
        }
        this.lastChar = (char) c;

        while (this.lastChar != '"') {
            sb.append(this.lastChar);
            c = input.read();
            if (c == -1) {
                throw new Exception("No end of string");
            }
            this.lastChar = (char) c;
        }
        this.lastChar = (char) input.read();
        return sb.toString();
    }
    private String readSpecialCharacters(Reader input) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lastChar);
        if (this.specialCharacters.contains(this.lastChar)) {
            if (this.lastChar == '<' || this.lastChar == '>' || this.lastChar == '=' || this.lastChar == '!') {
                this.lastChar = (char) input.read();
                if (this.lastChar == '=') {
                    sb.append(this.lastChar);
                    this.lastChar = (char) input.read();
                }
            }
            else if (this.lastChar == '&' || this.lastChar == '|') {
                char prevChar = this.lastChar;
                this.lastChar = (char) input.read();
                if (prevChar == this.lastChar) {
                    sb.append(this.lastChar);
                    this.lastChar = (char) input.read();
                }
            }
            else {
                this.lastChar = (char) input.read();
            }
        }
        else {
            throw new Exception("Invalid character: " + this.lastChar);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String input = "var x int=2 ahfdjhfjdhf;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        System.out.println(lexer.symbolList);
    }
}
