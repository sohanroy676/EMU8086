import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import io.IO;
import util.*;

public class App {
    static ArrayList<String> code;
    static Lexer lexer = new Lexer();
    static Assembler parser = new Assembler();
    static Runner runner = new Runner();
    static ArrayList<Token> tokens;
    static ArrayList<Instruction> instructions = new ArrayList<Instruction>();
    static IO[] ports = new IO[] { null, null, null };
    static boolean debug = false;

    public static void main(String[] args) throws Exception {
        init();
        try {
            for (String text : code) {
                tokens = lexer.tokenize(text.toUpperCase());
                if (tokens.size() == 0)
                    tokens.add(new Token("WORD", "NOP"));
                instructions.add(parser.getInstruction(tokens));
            }
            if (debug) runner.run(instructions, code);
            else runner.run(instructions);
            Memory.repr();
        } catch (Exception e) {
            Memory.repr();
            if (Runner.ip >= 0)
                System.out.println("Exception at line - " + (Runner.ip + 1));
            System.out.println(e.getMessage());
            return;
        }
    }

    private static void init() throws Exception {
        String cwd = System.getProperty("user.dir");
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter file name: ");
        String file = scan.nextLine();
        readFile(cwd + "/emu/src/asm/" + file);
        System.out.print("Enter memory size: ");
        Memory.boot(scan.nextInt());
        scan.nextLine();
        System.out.print("Run in Debug mode (Y/n): ");
        debug = scan.nextLine().equalsIgnoreCase("y");
        scan.close();

        Token.init();
    }

    private static void readFile(String filePath) throws Exception {
        code = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null)
            code.add(line);
        br.close();
    }
}
