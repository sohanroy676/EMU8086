package io;
import java.util.Arrays;

public class DISPLAY7SEG implements IO {
    short val;
    StringBuilder valStr;
    public DISPLAY7SEG() {
        valStr = new StringBuilder(" ___ \n|   |\n|___|\n|   |\n|___|@");
        val = 0;
    }

    protected void getValue(String hex) throws Exception {
        val = (short)Integer.parseInt(hex, 16);
        char[] c = new char[30];
        Arrays.fill(c, ' ');
        for (int i = 5; i < 24; i += 6) c[i] = '\n';
        StringBuilder str = new StringBuilder(new String(c));
        if ((val & 1) == 0) str.replace(1, 4, "___");
        if ((val & 2) == 0) {
            str.setCharAt(10, '|');
            str.setCharAt(16, '|');
        }
        if ((val & 4) == 0) {
            str.setCharAt(22, '|');
            str.setCharAt(28, '|');
        }
        if ((val & 8) == 0) str.replace(25, 28, "___");
        if ((val & 16) == 0) {
            str.setCharAt(18, '|');
            str.setCharAt(24, '|');
        }
        if ((val & 32) == 0) {
            str.setCharAt(6, '|');
            str.setCharAt(12, '|');
        }
        if ((val & 64) == 0) str.replace(13, 16, "___");
        if ((val & 128) == 0) str.setCharAt(29, '@');
        valStr = str;
    }
    public void fromPortA(String portVal) {}

    public void fromPortB(String portVal) throws Exception {
        getValue(portVal);
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        System.out.println(valStr);
    }

    public void fromPortC(String portVal) {}

    public void connect(int portNum) throws Exception {
        System.out.println(valStr);
    }
}
