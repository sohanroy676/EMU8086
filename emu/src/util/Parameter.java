package util;
public class Parameter {
    public String type, var, offset;
    public Parameter(String type, String var, String offset) {
        this.type = type;
        this.var = var;
        this.offset = offset;
    }
    public String getValue() throws Exception {
        String val = var;
        if (type == "REGISTER") {
            val = Memory.get(var);
        } else if (type == "MEMORY") {
            int val1 = Integer.parseInt(Memory.isRegister(var) ? Memory.get(var) : var, 16);
            val = String.valueOf(val1 + Integer.parseInt((offset != null ? offset : "0"), 16));
        }
        return val;
    }
}
