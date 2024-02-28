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

    public Lexer(Reader input) throws IOException {
        symbolList = new LinkedList<Symbol>();
        int c = input.read();
        if (c == -1) {
            return;
        }
        this.lastChar = (char) c;
        while (true) {
            try {
                this.lastChar = nextUsefulChar(input);
                if (this.lastChar == '\uFFFF') {
                    throw new EndOfFileException();
                }
                if (Character.isLetter(this.lastChar)) {
                    String s = readToken(input);
                    switch (s) {
                        // Boolean
                        case "true":
                            symbolList.add(new MyBoolean("true"));
                            break;
                        case "false":
                            symbolList.add(new MyBoolean("false"));
                            break;
                        // VarType
                        case "int":
                            symbolList.add(new VarType("int"));
                            break;
                        case "float":
                            symbolList.add(new VarType("float"));
                            break;
                        case "char":
                            symbolList.add(new VarType("char"));
                            break;
                        case "String":
                            symbolList.add(new VarType("String"));
                            break;
                        case "bool":
                            symbolList.add(new VarType("bool"));
                            break;
                        // Keyword
                        case "final":
                            symbolList.add(new Keyword("final"));
                            break;
                        case "if":
                            symbolList.add(new Keyword("if"));
                            break;
                        case "else":
                            symbolList.add(new Keyword("else"));
                            break;
                        case "while":
                            symbolList.add(new Keyword("while"));
                            break;
                        case "for":
                            symbolList.add(new Keyword("for"));
                            break;
                        case "free":
                            symbolList.add(new Keyword("free"));
                            break;
                        case "return":
                            symbolList.add(new Keyword("return"));
                            break;
                        case "struct":
                            symbolList.add(new Keyword("struct"));
                            break;
                        case "def":
                            symbolList.add(new Keyword("def"));
                            break;
                        // Identifier
                        default:
                            symbolList.add(new Identifier(s));
                            break;
                    }
                } else if (Character.isDigit(this.lastChar)) {
                    String s = readNumber(input);
                    if (s.contains(".")) {
                        symbolList.add(new MyFloat(s));
                    } else {
                        symbolList.add(new MyInteger(s));
                    }
                    if (this.lastChar != ' ' && this.lastChar != '\t' && this.lastChar != '\n' && !specialCharacteres.contains(this.lastChar)){
                        throw new IOException("Invalid character: " + this.lastChar);
                    }
                }
                else if (this.lastChar == '"')
                {
                    String s = readString(input);
                    symbolList.add(new MyString(s));
                }
                else {
                    String s = readSpecialCharactere(input);
                    symbolList.add(new Special(s));
                }
            } catch (EndOfFileException e) {
                break;
            }
        }

    }
    
    public Symbol getNextSymbol() {
        if (symbolList.isEmpty()) {
            return null;
        }
        return this.symbolList.removeFirst();
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
    private String readString(Reader input) throws IOException {
        StringBuilder sb = new StringBuilder();
        this.lastChar = (char) input.read();

        while (this.lastChar != '"') {
            sb.append(this.lastChar);
            this.lastChar = (char) input.read();
        }
        this.lastChar = (char) input.read();
        return sb.toString();
    }
    private String readSpecialCharactere(Reader input) throws IOException {
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
            throw new IOException("Invalid character: " + this.lastChar);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        String input = "var x int=2 ahfdjhfjdhf;";
        StringReader reader = new StringReader(input);
        Lexer lexer = new Lexer(reader);
        System.out.println(lexer.symbolList);
    }
}
