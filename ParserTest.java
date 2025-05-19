import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lr0.LR0Parser;
import lr1.LR1Parser;
import util.Grammar;

public class ParserTest {
    public static void main(String[] args) {
        String grammarStr = "";

        try {
            grammarStr = new String(Files.readAllBytes(Paths.get("grammar.txt")));
        } catch (IOException e) {
            System.err.println("Error reading grammar file: " + e.getMessage());
            return;
        }

        Grammar grammar = new Grammar(grammarStr);

        System.out.println("Testing LR(0) Parser:");
        LR0Parser lr0Parser = new LR0Parser(grammar);
        boolean lr0Success = lr0Parser.parserLR0();

        if (lr0Success) {
            System.out.println("LR(0) parsing successful.\n");
            System.out.println(lr0Parser.canonicalCollectionStr());

            System.out.println("\nAction Table for LR(0):");
            lr0Parser.printActionTable();

            System.out.println("\nGoTo Table for LR(0):");
            lr0Parser.printGoToTable();

        } else {
            System.out.println("LR(0) parsing failed due to conflicts.\n");
        }

        System.out.println("Testing LR(1) Parser (CLR(1)):");
        LR1Parser lr1Parser = new LR1Parser(grammar);
        boolean lr1Success = lr1Parser.parseCLR1();

        if (lr1Success) {
            System.out.println("LR(1) parsing (CLR(1)) successful.\n");
            System.out.println(lr1Parser.canonicalCollectionStr());

            System.out.println("\nAction Table for CLR(1):");
            lr1Parser.printActionTable();

            System.out.println("\nGoTo Table for CLR(1):");
            lr1Parser.printGoToTable();

        } else {
            System.out.println("LR(1) parsing (CLR(1)) failed due to conflicts.\n");
        }

        System.out.println("Testing LR(1) Parser (LALR(1)):");
        boolean lalrSuccess = lr1Parser.parseLALR1();

        if (lalrSuccess) {
            System.out.println("LALR(1) parsing successful.\n");
            System.out.println(lr1Parser.canonicalCollectionStr());

            System.out.println("\nAction Table for LALR(1):");
            lr1Parser.printActionTable();

            System.out.println("\nGoTo Table for LALR(1):");
            lr1Parser.printGoToTable();

        } else {
            System.out.println("LALR(1) parsing failed due to conflicts.\n");
        }
    }
}
