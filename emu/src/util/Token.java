package util;
import java.util.*;
public class Token {
    public String type, value;
    public static Map<Character, String> map = new HashMap<Character, String>();
    public Token(String t, String v) {
        type = t;
        value = v;
    }
    public Token(String t) {
        type = t;
        value = null;
    }
    public void repr() {
        System.out.print(String.format("<%s:%s> ", type, value));
    }
    public static void init() {
        Token.map.put(',', "COMMA");
        Token.map.put('+', "PLUS");
        Token.map.put('[', "BRACKET_OPEN");
        Token.map.put(']', "BRACKET_CLOSE");
    }
}
