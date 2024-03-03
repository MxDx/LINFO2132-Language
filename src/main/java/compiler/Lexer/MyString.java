package compiler.Lexer;

public class MyString implements Symbol {
    public  String Type = "MyString";
    public  String Value = "default";
    public MyString(String value) {
        Value = value;
    }
    public String toString() {
        return "<" + this.Type + ","+ this.Value + ">";
    }
}
