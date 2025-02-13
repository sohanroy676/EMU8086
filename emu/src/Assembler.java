import java.util.ArrayList;
import util.*;

public class Assembler {
    private ArrayList<Token> tokens;
    int index, length;
    private Parameter getParams(int start, int end) throws Exception {
        Token temp;
        String type = null, var = null, offset = null;
        // System.out.println(start + " " + end);
        if (start == end) {
            temp = tokens.get(start);
            if (temp.type == "NUMBER") {
                type = "IMMEDIATE";
                var = temp.value;
                if (var.charAt(0) == '0') var = var.substring(1);
            } else if (temp.type == "WORD") {
                type = "REGISTER";
                var = temp.value;
            } else {
                throw new Exception("ASSEMBLER: Invalid reference type: Try '[__]' for Memory Reference");
            }
        } else {
            type = "MEMORY";
            start++; end--;
            if (end - start >= 3) throw new Exception("ASSEMBLER: Invalid Syntax for Memory Reference: Expected '[register/Address + ?offset]'");
            if (start != end)
                offset = tokens.get(end).value;
            var = tokens.get(start).value;
        }
        return new Parameter(type, var, offset);
    }
    private int numOfParams() {
        for (int i = 1; i < length; i++) {
            if (tokens.get(i).type == "COMMA") {
                return i;
            }
        }
        return length > 1 ? 0 : -1;
    }
    protected Instruction getInstruction(ArrayList<Token> t) throws Exception {
        tokens = t;
        length = tokens.size();
        Token keyword = tokens.get(0);
        Class<?> cls = Class.forName(keyword.value);
        Object instruction;
        int idx = numOfParams();
        Parameter p1 = null, p2 = null;
        if (idx < 0) {
            instruction = cls.getDeclaredConstructors()[0].newInstance();
        }
        else if (idx == 0) {
            p1 = getParams(1, length - 1);
            instruction = cls.getDeclaredConstructors()[0].newInstance(p1);
        } else {
            p1 = getParams(1, idx - 1);
            p2 = getParams(idx + 1, length - 1);
            instruction = cls.getDeclaredConstructors()[0].newInstance(p1, p2);
        }
        
        return (Instruction)instruction;
    }
}
