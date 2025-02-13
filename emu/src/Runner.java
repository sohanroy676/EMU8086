import java.util.ArrayList;
import java.util.Scanner;
import util.Memory;
public class Runner {
    private int length;
    public static int ip = -1;
    void run(ArrayList<Instruction> t) throws Exception {
        ip = 0;
        length = t.size();
        String bp = Integer.toHexString(Memory.maxBytes);
        Memory.set("BP", bp);
        Memory.set("SP", bp);
        while (ip < length && !Memory.getFlag("INTERRUPT")) {
            t.get(ip).execute();
            ip++;
        }
    }
    void run(ArrayList<Instruction> t, ArrayList<String> code) throws Exception {
        ip = 0;
        length = t.size();
        Scanner input = new Scanner(System.in);
        String bp = Integer.toHexString(Memory.maxBytes);
        Memory.set("BP", bp);
        Memory.set("SP", bp);
        while (ip < length && !Memory.getFlag("INTERRUPT")) {
            t.get(ip).repr();
            System.out.println(ip + " : " + code.get(ip));
            t.get(ip).execute();
            ip++;
            Memory.repr();
            System.out.print("\ncontinue -t");
            input.nextLine();
        }
        input.close();
    }
}
