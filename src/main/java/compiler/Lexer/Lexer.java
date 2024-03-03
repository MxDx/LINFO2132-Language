package compiler.Lexer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedList;

public class Lexer {

    private char lastChar;
    private String currentToken;
    private final HashSet<Character> specialCharacteres = new HashSet<Character>() {
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
    private Reader input;

    public Lexer(Reader input) throws Exception {
        symbolList = new LinkedList<Symbol>();
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
                    case "String" -> new VarType("String");
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
                if (this.lastChar != ' ' && this.lastChar != '\t' && this.lastChar != '\n' && !specialCharacteres.contains(this.lastChar)){
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
                String s = readSpecialCharactere(input);
                return new Special(s);
            }
        } catch (EndOfFileException e) {
            System.out.println("End of file");
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

    private char nextUsefulChar(Reader input) throws IOException {
        int c;
        while (this.lastChar != -1) {
            if (this.lastChar == ' ' || this.lastChar == '\t' || this.lastChar == '\n' || this.lastChar == '\r') {
                this.lastChar = (char) input.read();
                continue;
            }
            if (this.lastChar == '/') {
                c = input.read();
                if (this.lastChar == -1) {
                    throw new EndOfFileException();
                }
                if (this.lastChar == '/') {
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
            }
            return this.lastChar;
        }
        throw new EndOfFileException();
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

    private String readInteger(Reader input) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(this.lastChar)) {
            sb.append(this.lastChar);
            this.lastChar = (char) input.read();
        }
        return sb.toString();
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
    private String readSpecialCharactere(Reader input) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(this.lastChar);
        if (this.specialCharacteres.contains(this.lastChar)) {
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
