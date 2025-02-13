package io;

public interface IO {
    static final String[] peripherals = new String[] {"DISPLAY7SEG", "DISPLAY7SEGX6"};
    public void connect(int portNum) throws Exception;
    public void fromPortA(String portVal) throws Exception;
    public void fromPortB(String portVal) throws Exception;
    public void fromPortC(String portVal) throws Exception;
}