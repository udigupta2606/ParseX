package lr0;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import util.*;

public class LR0Parser extends LRParser {

    private ArrayList<LR0State> canonicalCollection;

    public LR0Parser(Grammar grammar) {
        super(grammar);
    }

    public boolean parserSLR1() {
        createStates();
        createGoToTable();
        return createActionTableForSLR1();
    }

    public boolean parserLR0() {
        createStates();
        createGoToTable();
        return createActionTableForLR0();
    }

    protected void createStates() {
        canonicalCollection = new ArrayList<>();
        HashSet<LR0Item> start = new HashSet<>();
        start.add(new LR0Item(grammar.getRules().get(0)));

        LR0State startState = new LR0State(grammar, start);
        canonicalCollection.add(startState);

        for (int i = 0; i < canonicalCollection.size(); i++) {
            HashSet<String> stringWithDot = new HashSet<>();
            for (LR0Item item : canonicalCollection.get(i).getItems()) {
                if (item.getCurrentTerminal() != null) {
                    stringWithDot.add(item.getCurrentTerminal());
                }
            }
            for (String str : stringWithDot) {
                HashSet<LR0Item> nextStateItems = new HashSet<>();
                for (LR0Item item : canonicalCollection.get(i).getItems()) {

                    if (item.getCurrentTerminal() != null && item.getCurrentTerminal().equals(str)) {
                        LR0Item temp = new LR0Item(item);
                        temp.goTo();
                        nextStateItems.add(temp);
                    }
                }
                LR0State nextState = new LR0State(grammar, nextStateItems);
                boolean isExist = false;
                for (int j = 0; j < canonicalCollection.size(); j++) {
                    if (canonicalCollection.get(j).getItems().containsAll(nextState.getItems())
                            && nextState.getItems().containsAll(canonicalCollection.get(j).getItems())) {
                        isExist = true;
                        canonicalCollection.get(i).addTransition(str, canonicalCollection.get(j));
                    }
                }
                if (!isExist) {
                    canonicalCollection.add(nextState);
                    canonicalCollection.get(i).addTransition(str, nextState);
                }
            }
        }

    }

    protected void createGoToTable() {
        goToTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            goToTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getVariables().contains(s)) {
                    goToTable[i].put(s, findStateIndex(canonicalCollection.get(i).getTransition().get(s)));
                }
            }
        }
    }

    private boolean createActionTableForSLR1() {
        actionTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            actionTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getTerminals().contains(s)) {
                    actionTable[i].put(s, new Action(ActionType.SHIFT,
                            findStateIndex(canonicalCollection.get(i).getTransition().get(s))));
                }
            }
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (LR0Item item : canonicalCollection.get(i).getItems()) {
                if (item.getDotPointer() == item.getRightSide().length) {
                    if (item.getLeftSide().equals("S'")) {
                        actionTable[i].put("$", new Action(ActionType.ACCEPT, 0));
                    } else {
                        HashSet<String> follow = grammar.getFallowSets().get(item.getLeftSide());
                        Rule rule = new Rule(item.getLeftSide(), item.getRightSide().clone());
                        int index = grammar.findRuleIndex(rule);
                        Action action = new Action(ActionType.REDUCE, index);
                        for (String str : follow) {
                            if (actionTable[i].get(str) != null) {
                                System.out.println("it has a REDUCE-" + actionTable[i].get(str).getType()
                                        + " confilct in state " + i);
                                return false;
                            } else {
                                actionTable[i].put(str, action);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean createActionTableForLR0() {
        actionTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            actionTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getTerminals().contains(s)) {
                    actionTable[i].put(s, new Action(ActionType.SHIFT,
                            findStateIndex(canonicalCollection.get(i).getTransition().get(s))));
                }
            }
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (LR0Item item : canonicalCollection.get(i).getItems()) {
                if (item.getDotPointer() == item.getRightSide().length) {
                    if (item.getLeftSide().equals("S'")) {
                        actionTable[i].put("$", new Action(ActionType.ACCEPT, 0));
                    } else {
                        HashSet<String> terminals = grammar.getTerminals();
                        terminals.add("$");
                        Rule rule = new Rule(item.getLeftSide(), item.getRightSide().clone());
                        int index = grammar.findRuleIndex(rule);
                        Action action = new Action(ActionType.REDUCE, index);
                        for (String str : terminals) {
                            if (actionTable[i].get(str) != null) {
                                System.out.println("it has a REDUCE-" + actionTable[i].get(str).getType()
                                        + " confilct in state " + i);
                                return false;
                            } else {
                                actionTable[i].put(str, action);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private int findStateIndex(LR0State state) {
        for (int i = 0; i < canonicalCollection.size(); i++) {
            if (canonicalCollection.get(i).equals(state)) {
                return i;
            }
        }
        return -1;
    }

    public String canonicalCollectionStr() {
        String str = "Canonical Collection : \n";
        for (int i = 0; i < canonicalCollection.size(); i++) {
            str += "State " + i + " : \n";
            str += canonicalCollection.get(i) + "\n";
        }
        return str;
    }

    public void printActionTable() {
        System.out.println("Action Table:");
        for (int i = 0; i < actionTable.length; i++) {
            System.out.print("State " + i + ": ");
            for (String terminal : actionTable[i].keySet()) {
                Action action = actionTable[i].get(terminal);
                System.out.print("[" + terminal + " -> " + action + "] ");
            }
            System.out.println();
        }
    }

    public void printGoToTable() {
        System.out.println("GoTo Table:");
        for (int i = 0; i < goToTable.length; i++) {
            System.out.print("State " + i + ": ");
            for (String variable : goToTable[i].keySet()) {
                int state = goToTable[i].get(variable);
                System.out.print("[" + variable + " -> " + state + "] ");
            }
            System.out.println();
        }
    }

    public HashMap<Integer, HashMap<String, String>> getCombinedTable() {
        HashMap<Integer, HashMap<String, String>> combined = new HashMap<>();

        // Merge Action Table
        for (int i = 0; i < actionTable.length; i++) {
            HashMap<String, String> row = new HashMap<>();
            for (String terminal : actionTable[i].keySet()) {
                Action action = actionTable[i].get(terminal);
                String entry = "";

                switch (action.getType()) {
                    case SHIFT:
                        entry = "s" + action.getOperand();
                        break;
                    case REDUCE:
                        entry = "r" + action.getOperand();
                        break;
                    case ACCEPT:
                        entry = "acc";
                        break;
                    default:
                        entry = "err"; // optional for errors
                }

                row.put(terminal, entry);
            }
            combined.put(i, row);
        }

        // Merge GoTo Table
        for (int i = 0; i < goToTable.length; i++) {
            combined.putIfAbsent(i, new HashMap<>());
            for (String nonTerminal : goToTable[i].keySet()) {
                int nextState = goToTable[i].get(nonTerminal);
                combined.get(i).put(nonTerminal, String.valueOf(nextState));
            }
        }

        return combined;
    }

    public String mergedTableStr() {
        StringBuilder sb = new StringBuilder();
        HashMap<Integer, HashMap<String, String>> combined = getCombinedTable();

        // Step 1: Collect all unique symbols (sorted)
        Set<String> symbolSet = new TreeSet<>();
        for (HashMap<String, String> row : combined.values()) {
            symbolSet.addAll(row.keySet());
        }

        List<String> symbols = new ArrayList<>(symbolSet);
        symbols.remove("$");
        symbols.add("$");

        int colWidth = 12;

        // Header
        sb.append("Merged Action + GoTo Table:\n\n");
        sb.append(String.format("%-6s", "State"));
        for (String sym : symbols) {
            sb.append(String.format("|%-" + colWidth + "s", sym));
        }
        sb.append("\n");

        // Horizontal separator
        int totalCols = 1 + symbols.size();
        int totalWidth = 6 + totalCols * (colWidth + 1);
        sb.append("-".repeat(totalWidth)).append("\n");

        // Table rows
        List<Integer> sortedStates = new ArrayList<>(combined.keySet());
        Collections.sort(sortedStates);

        for (Integer state : sortedStates) {
            sb.append(String.format("%-6d", state));
            HashMap<String, String> row = combined.get(state);
            for (String sym : symbols) {
                String val = row.getOrDefault(sym, "");
                if (val.startsWith("s")) {
                    val = "SHIFT " + val.substring(1);
                } else if (val.startsWith("r")) {
                    val = "REDUCE " + val.substring(1);
                } else if (val.equalsIgnoreCase("acc")) {
                    val = "ACCEPT";
                }
                sb.append(String.format("|%-" + colWidth + "s", val));
            }
            sb.append("\n");
            sb.append("-".repeat(totalWidth)).append("\n");
        }

        return sb.toString();
    }


    public List<String[]> getProductionList() {
        List<String[]> prodList = new ArrayList<>();

        for (Rule rule : grammar.getRules()) {
            String[] rhs = rule.getRightSide();
            String[] fullProd = new String[2 + rhs.length];

            fullProd[0] = rule.getLeftSide();
            fullProd[1] = "->";
            for (int i = 0; i < rhs.length; i++) {
                fullProd[2 + i] = rhs[i];
            }

            prodList.add(fullProd);
        }

        return prodList;
    }

    private String formatAction(String action, List<String[]> productionList) {
        if (action.startsWith("s")) {
            return "shift " + action.substring(1);
        } else if (action.startsWith("r")) {
            int ruleIndex = Integer.parseInt(action.substring(1));
            String[] prod = productionList.get(ruleIndex);
            return "reduce " + prod[0] + "->" + String.join(" ", Arrays.copyOfRange(prod, 2, prod.length));
        } else if (action.equals("acc") || action.startsWith("a")) {
            return "accept";
        }
        return action;
    }

    public String parseInputString(List<String> input, HashMap<Integer, HashMap<String, String>> table, List<String[]> productionList) {
        Stack<String> stack = new Stack<>();
        stack.push("0");

        StringBuilder trace = new StringBuilder();
        trace.append(String.format("%-30s %-30s %-10s\n", "STACK", "INPUT", "ACTION"));
        trace.append("\n");

        while (!input.isEmpty()) {
            int state = Integer.parseInt(stack.peek());
            String symbol = input.get(0);

            String action = table.get(state).get(symbol);
            if (action == null) {
                trace.append("ERROR: No action found for state ").append(state).append(" and symbol ").append(symbol).append("\n");
                break;
            }

            // Append current STACK, INPUT, and ACTION to trace
            trace.append(String.format("%-30s %-30s %-10s\n",
                    String.join(" ", stack),
                    String.join(" ", input),
                    formatAction(action, productionList)
            ));

            if (action.startsWith("s")) {
                // Shift operation
                stack.push(symbol);
                stack.push(action.substring(1));
                input.remove(0);

            } else if (action.startsWith("r")) {
                // Reduce operation
                int ruleIndex = Integer.parseInt(action.substring(1));
                String[] production = productionList.get(ruleIndex);
                int symbolsToPop = (production.length - 2) * 2;

                for (int i = 0; i < symbolsToPop; i++) {
                    stack.pop();
                }

                String topState = stack.peek();
                String lhs = production[0];
                String gotoState = table.get(Integer.parseInt(topState)).get(lhs);

                stack.push(lhs);
                stack.push(gotoState);

            } else if (action.equals("acc") || action.startsWith("a")) {
                trace.append("\nAccepted\n");
                return trace.toString(); // return full trace to be printed in GUI
            }
        }

        trace.append("\nRejected\n");
        return trace.toString();
    }
}
