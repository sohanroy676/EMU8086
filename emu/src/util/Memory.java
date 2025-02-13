package util;
import java.util.HashMap;
import java.util.Arrays;
public class Memory {
    private static int maxCapacity, flagStart = 16;
    public static int maxBytes;
    private static StringBuilder ram;
    private static HashMap<String, Integer> flags = new HashMap<String, Integer>();
    public static String[] registers = new String[] {"AX", "AH", "AL", "BX", "BH", "BL", "CX", "CH", "CL", "DX", "DH", "DL", "SP", "BP", "SI", "DI", "SR"};
    public static void boot(int size) {
        maxBytes = size;
        maxCapacity = size*2;
        ram = new StringBuilder(maxCapacity);
        char[] zeros = new char[maxCapacity];
        java.util.Arrays.fill(zeros, '0');
        ram.append(zeros);
        flags.put("CARRY", 0);
        flags.put("ZERO", 6);
        flags.put("SIGN", 7);
        flags.put("INTERRUPT", 9);
        flags.put("DIRECTION", 10);
    }
    public static Boolean isRegister(String name) {
        return Arrays.asList(registers).contains(name);
    }
    public static String get(int addr, int bytes) throws Exception {
        if (addr + bytes > maxBytes) throw new Exception("MEMORY: Memory out of Bounds. maxBytes = " + maxBytes);
        return ram.substring(2*addr, 2*(addr + bytes));
    }
    public static int[] getRegister(String reg) throws Exception {
        // AX(AH+AL) BX(BH+BL) CX(CH+CL) DX(DH+DL) SP BP SI DI SR
        int start, size = 2;
        char c1 = reg.charAt(0), c2 = reg.charAt(1);
        if (c2 == 'P') {
            start = 8;
            if (c1 == 'B') start += size;
            else if (c1 != 'S') throw new Exception("MEMORY: Invalid Register");
        }
        else if (c2 == 'I') {
            start = 12;
            if (c1 == 'D') start += size;
            else if (c1 != 'S') throw new Exception("MEMORY: Invalid Register");
        }
        else if (c2 == 'R' && c1 == 'S') start = 16;
        else if (c1 < 'A' && c1 > 'D') throw new Exception("MEMORY: Invalid Register");
        else {
            start = (c1 - 'A')*2;
            if (c2 == 'X') size = 2;
            else if (c2 == 'H') size = 1;
            else if (c2 == 'L') {
                start += 1;
                size = 1;
            } else throw new Exception("MEMORY: Invalid Register");
        }
        int[] r = new int[2];
        r[0] = start; r[1] = size;
        return r;
    }
    public static Boolean getFlag(String flag) throws Exception {
        int i = flags.get(flag);
        flag = Memory.get(flagStart, 2);
        int val = Integer.parseInt(flag, 16) & (1 << i);
        return val != 0;
    }
    public static Boolean setFlag(String flag, int val, int test) throws Exception {
        int i = flags.get(flag);
        flag = Memory.get(flagStart, 2);
        if (val == 1) val = Integer.parseInt(flag, 16) ^ (0 << i);
        return val != 0;
    }
    public static void setFlag(String flag, int val) throws Exception {
        int i = flags.get(flag);
        flag = Memory.get(flagStart, 2);
        StringBuffer s = new StringBuffer(Integer.toBinaryString(Integer.parseInt(flag, 16)));
        while (s.length() < 16) s.insert(0, '0');
        s.setCharAt(15-i, (char)('0' + val));
        String newVal = Integer.toHexString(Integer.parseInt(s.toString(), 2));
        Memory.set(flagStart, 2, newVal);
    }
    public static String get(String reg) throws Exception {
        int[] r = getRegister(reg);
        return Memory.get(r[0], r[1]);
    }
    public static void set(int addr, int bytes, String value) throws Exception {
        if (addr + bytes > maxBytes) throw new Exception("MEMORY: Memory out of Bounds. maxBytes = " + maxBytes);
        if (value.length() > 2*bytes) throw new Exception("MEMORY: Incompatible value");
        while (value.length() < 2*bytes) value = "0" + value;
        ram.replace(2*addr, 2*(addr + bytes), value);
    }
    public static void set(String reg, String value) throws Exception {
        int[] r = getRegister(reg);
        set(r[0], r[1], value);
    }
    public static void repr() {
        int m = maxCapacity/24;
        for (int i = 0; i < m; i++) {
            System.out.println(ram.substring(i*24, (i+1)*24));
        }
        if (m*24 >= maxCapacity) return;
        System.out.println(ram.substring(m*24, maxCapacity));
    }
}

