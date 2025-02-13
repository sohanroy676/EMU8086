import util.Parameter;
import io.IO;
import util.Memory;
// import io.*;

public class Instruction {
    public String keyword;
    Parameter p1, p2;

    Instruction(String keyword, Parameter p1, Parameter p2) throws Exception {
        this.keyword = keyword;
        this.p1 = p1;
        this.p2 = p2;
    }

    public void repr() throws Exception {
        System.out.println(
                keyword + ": " + (p1 != null ? p1.getValue() : "XX") + ", " + (p2 != null ? p2.getValue() : "XX"));
    }

    public void execute() throws Exception {
    }
}

class NOP extends Instruction {
    NOP() throws Exception {
        super("NOP", null, null);
    }

    public void execute() throws Exception {
    }
}

class MOV extends Instruction {
    MOV(Parameter p1, Parameter p2) throws Exception {
        if (p1.type.equals("IMMEDIATE"))
            throw new Exception(
                    "INSTRUCTION: Argument 1 of MOV cannot be an IMMEDIATE REFERENCE. Expected Register|Memory Reference");
        super("MOV", p1, p2);
    }

    public void execute() throws Exception {
        String val = p2.getValue();
        if (p2.type.equals("MEMORY"))
            val = Memory.get(Integer.parseInt(val), 1);
        if (p1.type.equals("REGISTER"))
            Memory.set(p1.var, val);
        else
            Memory.set(Integer.parseInt(p1.getValue()), 1, val);
    }
}

class OUT extends Instruction {
    OUT(Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: Argument 1 of OUT can only be REGISTER");
        if (!p2.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: Argument 2 of OUT can only be REGISTER");
        super("OUT", p1, p2);
    }

    public void execute() throws Exception {
        int portId = (int)(p1.getValue().charAt(2) - '2');
        assert portId >= 0 && portId <= 3: "INSTRUCTION: Invalid port ID";
        switch (p1.getValue().charAt(3)) {
            case '0':
                App.ports[portId].fromPortA(p2.getValue());
            case '2':
                App.ports[portId].fromPortB(p2.getValue());
            case '4':
                App.ports[portId].fromPortC(p2.getValue());
            case '6': break;
            default: throw new Exception("INSTRUCTION: INVALID PORT");
        }
    }
}
class ADD extends Instruction {
    ADD(Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("REGISTER")) throw new Exception("INSTRUCTION: ADD expected REGISTER");
        super("ADD", p1, p2);
    }
    public void execute() throws Exception {
        int val1 = Integer.parseInt(p1.getValue(), 16);
        int val2;
        if (p2.type.equals("MEMORY")) {
            int addr = Integer.parseInt(p2.getValue(), 16);
            val2 = Integer.parseInt(Memory.get(addr, 1), 16);
        } else val2 = Integer.parseInt(p2.getValue(), 16);
        int val = val1 + val2;
        int max = (int)Math.pow(2, Memory.getRegister(p1.var)[1]*8);
        if (val >= max) {
            val -= max;
            Memory.setFlag("CARRY", 1);
        }
        String v = Integer.toHexString(val);
        Memory.set(p1.var, v);
    }
}
class SUB extends Instruction {
    SUB(Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("REGISTER")) throw new Exception("INSTRUCTION: SUB expected REGISTER");
        super("SUB", p1, p2);
    }
    public void execute() throws Exception {
        int val1 = Integer.parseInt(p1.getValue(), 16);
        int val2;
        if (p2.type.equals("MEMORY")) {
            int addr = Integer.parseInt(p2.getValue(), 16);
            val2 = Integer.parseInt(Memory.get(addr, 1), 16);
        } else val2 = Integer.parseInt(p2.getValue(), 16);
        int val = val1 - val2;
        if (val == 0) Memory.setFlag("ZERO", 1);
        else if (val < 0) val = (int)Math.pow(2, Memory.getRegister(p1.var)[1]*8) + val;
        String v = Integer.toHexString(val);
        Memory.set(p1.var, v);
    }
}

class INC extends Instruction {
    INC(Parameter p) throws Exception {
        if (!p.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: Argument of INC can only be REGISTER");
        super("INC", p, null);
    }

    public void execute() throws Exception {
        int val = Integer.parseInt(p1.getValue(), 16);
        ++val;
        String newVal = Integer.toHexString(val);
        if (newVal.length() % 2 != 0)
            newVal = newVal.substring(1);
        Memory.set(p1.var, newVal);
    }
}

class DEC extends Instruction {
    DEC(Parameter p) throws Exception {
        if (!p.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: Argument of DEC can only be REGISTER");
        super("DEC", p, null);
    }

    public void execute() throws Exception {
        int val = Integer.parseInt(p1.getValue(), 16);
        --val;
        String newVal = Integer.toHexString(val);
        Memory.setFlag("ZERO", (val == 0) ? 1 : 0);
        if (val < 0) {
            int n = Memory.getRegister(p1.var)[1];
            newVal = new String(new char[2 * n]).replace('\0', 'f');
        }
        Memory.set(p1.var, newVal);
    }
}

class DIV extends Instruction {
    DIV(Parameter p) throws Exception {
        if (!p.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: DIV expected REGSITER");
        super("DIV", p, null);
    }
    public void execute() throws Exception {
        int nmr = Integer.parseInt(Memory.get("AX"), 16);
        int dnr = Integer.parseInt(p1.getValue(), 16);
        if (dnr == 0) throw new Exception("INSTRUCTION: Division by ZERO");
        int size = Memory.getRegister(p1.var)[1];
        String q = Integer.toHexString(nmr/dnr), r = Integer.toHexString(nmr%dnr);
        if (size == 1) {
            Memory.set("AL", q);
            Memory.set("AH", r);
        } else {
            Memory.set("AX", q);
            Memory.set("DX", r);
        }
    }
}

class JMP extends Instruction {
    JMP(Parameter p) throws Exception {
        if (!p.type.equals("IMMEDIATE"))
            throw new Exception("INSTRUCTION: JUMP address can only be IMMEDIATE");
        super("JMP", p, null);
    }

    public void execute() throws Exception {
        int addr = Integer.parseInt(p1.getValue());
        Runner.ip = addr - 2;
    }
}

class JNZ extends JMP {
    JNZ(Parameter p) throws Exception {
        super(p);
        this.keyword = "JNZ";
    }

    @Override
    public void execute() throws Exception {
        if (Memory.getFlag("ZERO"))
            return;
        super.execute();
    }
}

class JZ extends JMP {
    JZ(Parameter p) throws Exception {
        super(p);
        this.keyword = "JZ";
    }

    @Override
    public void execute() throws Exception {
        if (!Memory.getFlag("ZERO"))
            return;
        super.execute();
    }
}

class JNC extends JMP {
    JNC(Parameter p) throws Exception {
        super(p);
        this.keyword = "JNC";
    }

    @Override
    public void execute() throws Exception {
        if (Memory.getFlag("CARRY"))
            return;
        super.execute();
    }
}

class JC extends JMP {
    JC(Parameter p) throws Exception {
        super(p);
        this.keyword = "JC";
    }

    @Override
    public void execute() throws Exception {
        if (!Memory.getFlag("CARRY"))
            return;
        super.execute();
    }
}

class LOOP extends JNZ {
    private static DEC dec = null;

    LOOP(Parameter p) throws Exception {
        super(p);
        this.keyword = "LOOP";
        if (dec == null)
            dec = new DEC(new Parameter("REGISTER", "CX", null));
    }

    @Override
    public void execute() throws Exception {
        dec.execute();
        super.execute();
    }
}

class INT extends Instruction {
    INT(Parameter p) throws Exception {
        if (!p.type.equals("IMMEDIATE"))
            throw new Exception("INSTRUCTION: INT expected IMMEDIATE");
        super("INT", p, null);
    }

    public void execute() throws Exception {
        Memory.setFlag("INTERRUPT", 1);
    }
}

class PUSH extends Instruction {
    private static DEC dec = null;

    PUSH(Parameter p) throws Exception {
        if (!p.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: PUSH expected REGISTER");
        super("PUSH", p, null);
        if (dec == null)
            dec = new DEC(new Parameter("REGISTER", "SP", null));
    }

    public void execute() throws Exception {
        int size = Memory.getRegister(p1.var)[1];
        dec.execute();
        if (size == 2)
            dec.execute();
        int addr = Integer.parseInt(Memory.get("SP"), 16);
        int val = Integer.parseInt(p1.getValue(), 16);
        Memory.set(addr, size, Integer.toHexString(val));
    }
}

class POP extends Instruction {
    private static INC inc = null;

    POP(Parameter p) throws Exception {
        if (!p.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: POP expected REGISTER");
        super("POP", p, null);
        if (inc == null)
            inc = new INC(new Parameter("REGISTER", "SP", null));
    }

    public void execute() throws Exception {
        int size = Memory.getRegister(p1.var)[1];
        int spAddr = Integer.parseInt(Memory.get("SP"), 16);
        int bpAddr = Integer.parseInt(Memory.get("BP"), 16);
        if (spAddr >= bpAddr)
            throw new Exception("INSTRUCTION: Stack underflow");
        else if (spAddr + size > bpAddr)
            throw new Exception("INSTRUCTION: Higher size than available");
        String val = Memory.get(spAddr, size);
        Memory.set(p1.var, val);
        inc.execute();
        if (size == 2)
            inc.execute();
    }
}

class CALL extends Instruction {
    private static PUSH push;
    CALL(Parameter p) throws Exception {
        if (!p.type.equals("IMMEDIATE")) throw new Exception("INSTRUCTION: CALL addr can only be IMMEDIATE");
        super("CALL", p, null);
        if (push == null) push = new PUSH(new Parameter("REGISTER", "SR", null));
    }
    public void execute() throws Exception {
        String val = Integer.toHexString(Runner.ip);
        Memory.set("SR", val);
        push.execute();
        Runner.ip = Integer.parseInt(p1.getValue()) - 2;
    }
}

class RET extends Instruction {
    private static POP pop;
    RET() throws Exception {
        super("RET", null, null);
        if (pop == null) pop = new POP(new Parameter("REGISTER", "SR", null));
    }
    public void execute() throws Exception {
        pop.execute();
        Runner.ip = Integer.parseInt(Memory.get("SR"), 16);
    }
}

class XOR extends Instruction {
    XOR(Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("REGISTER") || !p2.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: XOR requires REGISTER");
        super("XOR", p1, p2);
    }
    public void execute() throws Exception {
        int val1 = Integer.parseInt(p1.getValue(), 16);
        int val2 = Integer.parseInt(p2.getValue(), 16);
        int val = val1^val2;
        Memory.set(p1.var, Integer.toHexString(val));
    }
}
class CMP extends Instruction {
    CMP(Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("REGISTER"))
            throw new Exception("INSTRUCTION: Expected REGISTER as argument 1 of CMP");
        super("CMP", p1, p2);
    }

    public void execute() throws Exception {
        int val1 = Integer.parseInt(p1.getValue(), 16);
        int val2;
        if (p2.type.equals("MEMORY"))
            val2 = Integer.parseInt(Memory.get(Integer.parseInt(p2.getValue()), 1), 16);
        else val2 = Integer.parseInt(p2.getValue(), 16);
        int cf = 0, zf = 0, sf = 0;
        if (val1 < val2) {
            cf = 1;
            sf = 1;
        } else if (val1 == val2) zf = 1;
        Memory.setFlag("CARRY", cf);
        Memory.setFlag("ZERO", zf);
        Memory.setFlag("SIGN", sf);
    }
}
class XCHG extends Instruction {
    XCHG(Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("REGISTER")) throw new Exception("INSTRUCTION: Expected REGISTER as argument 1 of XCHG");
        if (p2.type.equals("IMMEDIATE")) throw new Exception("INSTRUCTION: Argument 2 of XCHG cannot be IMMEDIATE");
        super("XCHG", p1, p2);
    }
    public void execute() throws Exception {
        String val1 = p1.getValue();
        String val2 = p2.getValue();
        if (p2.type.equals("MEMORY")) {
            int addr = Integer.parseInt(val2);
            val2 = Memory.get(addr, 1);
            Memory.set(p1.var, val2);
            Memory.set(addr, 1, val1);
        } else {
            Memory.set(p1.var, val2);
            Memory.set(p2.var, val1);
        }
    }
}

class CONNECT extends Instruction {
    CONNECT(Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("IMMEDIATE")) throw new Exception("INSTRUCTION: CONNECT expected IMMDEIATE");
        if (!p2.type.equals("IMMEDIATE")) throw new Exception("INSTRUCTION: CONNECT expected IMMDEIATE");
        super("CONNECT", p1, p2);
    }
    public void execute() throws Exception {
        int portNum = Integer.parseInt(p1.getValue(), 16);
        if (portNum < 1 || portNum > 3) throw new Exception("INSTRUCTION: Invalid port number: " + portNum);
        int portID = Integer.parseInt(p2.getValue(), 16);
        if (portNum < 1 || portNum > 3) throw new Exception("INSTRUCTION: Invalid port ID: " + portID);
        Class<?> cls = Class.forName("io." + IO.peripherals[portID]);
        IO per = (IO)cls.getDeclaredConstructors()[0].newInstance();
        App.ports[portNum] = per;
        per.connect(portNum);
    }
}

class SHL extends Instruction {
    SHL (Parameter p1, Parameter p2) throws Exception {
        if (!p1.type.equals("REGISTER")) throw new Exception("INSTRUCTION: Expected REGISTER as first argument");
        if (p1.type.equals("MEMORY")) throw new Exception("INSTRUCTION: second argument cannot be MEMORY");
        super("SHL", p1, p2);
    }
}