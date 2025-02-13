package io;

public class DISPLAY7SEGX6 implements IO {
    private DISPLAY7SEG[] displays = new DISPLAY7SEG[6];
    String selections;
    public DISPLAY7SEGX6() throws Exception {
        selections = "000000";
        for (int i = 0; i < 6; i++) displays[i] = new DISPLAY7SEG();
    }
    public void getValues(String hex) throws Exception {
        for (int i = 0; i < 6; i++) displays[i].getValue((selections.charAt(i) == '1') ? hex : "0");
        // for (int i = 0; i < 6; i++) if (selections.charAt(i) == '1') displays[i].getValue(hex);
    }
    public void fromPortA(String portVal) throws Exception {
        selections = Integer.toBinaryString(Integer.parseInt(portVal, 16));
        while (selections.length() < 6) selections = "0" + selections;
    }
    public void fromPortB(String portVal) throws Exception {
        getValues(portVal);
        display();
    }
    public void fromPortC(String portVal) throws Exception {}

    public void display() throws Exception {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        for (int i = 0; i < 6; i++) System.out.println(displays[i].valStr);
        System.out.println();
    }
    public void connect(int portNum) throws Exception {
        display();
    }
}
