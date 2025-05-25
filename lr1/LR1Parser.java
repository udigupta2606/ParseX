package lr1;

import lr0.LR0Item;
import util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class LR1Parser extends LRParser {

    private ArrayList<LR1State> canonicalCollection;

    public LR1Parser(Grammar grammar) {
        super(grammar);
    }

    protected void createStatesForCLR1() {
        canonicalCollection = new ArrayList<>();
        HashSet<LR1Item> start = new HashSet<>();
        Rule startRule = grammar.getRules().get(0);
        HashSet<String> startLockahead = new HashSet<>();
        startLockahead.add("$");
        start.add(new LR1Item(startRule.getLeftSide(), startRule.getRightSide(), 0, startLockahead));

        LR1State startState = new LR1State(grammar, start);
        canonicalCollection.add(startState);

        for (int i = 0; i < canonicalCollection.size(); i++) {
            HashSet<String> stringWithDot = new HashSet<>();
            for (LR1Item item : canonicalCollection.get(i).getItems()) {
                if (item.getCurrent() != null) {
                    stringWithDot.add(item.getCurrent());
                }
            }
            for (String str : stringWithDot) {
                HashSet<LR1Item> nextStateItems = new HashSet<>();
                for (LR1Item item : canonicalCollection.get(i).getItems()) {

                    if (item.getCurrent() != null && item.getCurrent().equals(str)) {
                        LR1Item temp = new LR1Item(item.getLeftSide(), item.getRightSide(), item.getDotPointer() + 1,
                                item.getLookahead());
                        nextStateItems.add(temp);
                    }
                }
                LR1State nextState = new LR1State(grammar, nextStateItems);
                boolean isExist = false;
                for (int j = 0; j < canonicalCollection.size(); j++) {
                    if (canonicalCollection.get(j).getItems().containsAll(nextState.getItems())
                            && nextState.getItems().containsAll(canonicalCollection.get(j).getItems())) {
                        isExist = true;
                        canonicalCollection.get(i).getTransition().put(str, canonicalCollection.get(j));
                    }
                }
                if (!isExist) {
                    canonicalCollection.add(nextState);
                    canonicalCollection.get(i).getTransition().put(str, nextState);
                }
            }
        }

    }

    //CLR1 Parser
    public boolean parseCLR1() {
        createStatesForCLR1();
        createGoToTable();
        return createActionTable();
    }

    //LALR Parser
    public boolean parseLALR1() {
        createStatesForLALR1();
        createGoToTable();
        return createActionTable();
    }

    public void createStatesForLALR1() {
        createStatesForCLR1();
        ArrayList<LR1State> temp = new ArrayList<>();
        for (int i = 0; i < canonicalCollection.size(); i++) {
            HashSet<String> lookahead = new HashSet<>();
            HashSet<LR0Item> itemsi = new HashSet<>();
            for (LR1Item item : canonicalCollection.get(i).getItems()) {
                itemsi.add(new LR0Item(item.getLeftSide(), item.getRightSide(), item.getDotPointer()));
            }
            for (int j = i + 1; j < canonicalCollection.size(); j++) {
                HashSet<LR0Item> itemsj = new HashSet<>();
                for (LR1Item item : canonicalCollection.get(j).getItems()) {
                    itemsj.add(new LR0Item(item.getLeftSide(), item.getRightSide(), item.getDotPointer()));
                }
                if (itemsi.containsAll(itemsj) && itemsj.containsAll(itemsi)) {
                    for (LR1Item itemi : canonicalCollection.get(i).getItems()) {
                        for (LR1Item itemj : canonicalCollection.get(j).getItems()) {
                            if (itemi.equalLR0(itemj)) {
                                itemi.getLookahead().addAll(itemj.getLookahead());
                                break;
                            }
                        }
                    }
                    for (int k = 0; k < canonicalCollection.size(); k++) {
                        for (String s : canonicalCollection.get(k).getTransition().keySet()) {
                            if (canonicalCollection.get(k).getTransition().get(s).getItems()
                                    .containsAll(canonicalCollection.get(j).getItems()) &&
                                    canonicalCollection.get(j).getItems().containsAll(
                                            canonicalCollection.get(k).getTransition().get(s).getItems())) {
                                canonicalCollection.get(k).getTransition().put(s, canonicalCollection.get(i));
                            }
                        }
                    }
                    canonicalCollection.remove(j);
                    j--;

                }
            }
            temp.add(canonicalCollection.get(i));
        }
        canonicalCollection = temp;
    }

    protected void createGoToTable() {
        goToTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            goToTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.isVariable(s)) {
                    goToTable[i].put(s, findStateIndex(canonicalCollection.get(i).getTransition().get(s)));
                }
            }
        }
    }

    private int findStateIndex(LR1State state) {
        for (int i = 0; i < canonicalCollection.size(); i++) {
            if (canonicalCollection.get(i).equals(state)) {
                return i;
            }
        }
        return -1;
    }

    private boolean createActionTable() {
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
            for (LR1Item item : canonicalCollection.get(i).getItems()) {
                if (item.getDotPointer() == item.getRightSide().length) {
                    if (item.getLeftSide().equals("S'")) {
                        actionTable[i].put("$", new Action(ActionType.ACCEPT, 0));
                    } else {
                        Rule rule = new Rule(item.getLeftSide(), item.getRightSide().clone());
                        int index = grammar.findRuleIndex(rule);
                        Action action = new Action(ActionType.REDUCE, index);
                        for (String str : item.getLookahead()) {
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

    //Stack implementation starts
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
                        entry = "acc"; // or "a" if your parser uses "a"
                        break;
                    default:
                        entry = "err"; // optional, for error handling
                }

                row.put(terminal, entry);
            }
            combined.put(i, row);
        }

        // Merge GOTO Table
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

        // Step 1: Collect and sort symbols (ensure $ is last)
        Set<String> allSymbols = new TreeSet<>();
        for (HashMap<String, String> row : combined.values()) {
            allSymbols.addAll(row.keySet());
        }

        List<String> symbols = new ArrayList<>(allSymbols);
        symbols.remove("$");
        symbols.add("$");

        int colWidth = 12; // enough space for REDUCE 10, etc.

        // Step 2: Header
        sb.append(String.format("|%-6s|", "State"));
        for (String sym : symbols) {
            sb.append(String.format(" %-" + (colWidth - 1) + "s|", sym));
        }
        sb.append("\n");

        // Step 3: Header separator (no '|')
        int totalCols = 1 + symbols.size();
        for (int i = 0; i < totalCols; i++) {
            sb.append("-".repeat(colWidth));
        }
        sb.append("\n");

        // Step 4: Print each row
        List<Integer> sortedStates = new ArrayList<>(combined.keySet());
        Collections.sort(sortedStates);

        for (Integer state : sortedStates) {
            sb.append(String.format("|%-6d|", state));
            HashMap<String, String> row = combined.get(state);

            for (String sym : symbols) {
                String val = row.getOrDefault(sym, "");

                // Format actions
                if (val.startsWith("s")) {
                    val = "SHIFT " + val.substring(1);
                } else if (val.startsWith("r")) {
                    val = "REDUCE " + val.substring(1);
                } else if (val.equalsIgnoreCase("acc")) {
                    val = "ACCEPT";
                }

                sb.append(String.format(" %-" + (colWidth - 1) + "s|", val));
            }
            sb.append("\n");

            // Row separator (no '|')
            for (int i = 0; i < totalCols; i++) {
                sb.append("-".repeat(colWidth));
            }
            sb.append("\n");
        }

        return sb.toString();
    }




    public List<String[]> getProductionList() {
        List<String[]> prodList = new ArrayList<>();

        for (Rule rule : grammar.getRules()) {
            String[] rhs = rule.getRightSide(); //fixed
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

        StringBuilder log = new StringBuilder();
        log.append(String.format("%-30s %-30s %-10s\n", "STACK", "INPUT", "ACTION"));
        log.append("\n");

        int maxSteps = 1000;
        int steps = 0;

        while (!input.isEmpty() && steps++ < maxSteps) {
            int state;
            try {
                state = Integer.parseInt(stack.peek());
            } catch (NumberFormatException e) {
                log.append("ERROR: Stack top is not a valid state: ").append(stack.peek()).append("\n");
                return log.toString() + "\nRejected";
            }

            String symbol = input.get(0);
            HashMap<String, String> actionRow = table.get(state);
            if (actionRow == null) {
                log.append("ERROR: No action row for state ").append(state).append("\n");
                return log.toString() + "\nRejected";
            }

            String action = actionRow.get(symbol);
            if (action == null) {
                log.append("ERROR: No action found for state ").append(state).append(" and symbol '").append(symbol).append("'\n");
                return log.toString() + "\nRejected";
            }

            // Log current step
            log.append(String.format("%-30s %-30s %-10s\n",
                String.join(" ", stack),
                String.join(" ", input),
                formatAction(action, productionList)
            ));

            if (action.startsWith("s")) {
                // Shift
                stack.push(symbol);
                stack.push(action.substring(1));
                input.remove(0);

            } else if (action.startsWith("r")) {
                // Reduce
                int ruleIndex = Integer.parseInt(action.substring(1));
                String[] production = productionList.get(ruleIndex);
                int symbolsToPop = (production.length - 2) * 2;

                for (int i = 0; i < symbolsToPop; i++) {
                    if (!stack.isEmpty()) stack.pop();
                    else {
                        log.append("ERROR: Tried to pop from empty stack during reduce\n");
                        return log.toString() + "\nRejected";
                    }
                }

                String topStateStr = stack.peek();
                int topState = Integer.parseInt(topStateStr);
                String lhs = production[0];

                String gotoState = table.get(topState).get(lhs);
                if (gotoState == null) {
                    log.append("ERROR: No GOTO state for non-terminal '").append(lhs).append("' from state ").append(topState).append("\n");
                    return log.toString() + "\nRejected";
                }

                stack.push(lhs);
                stack.push(gotoState);

            } else if (action.equals("acc") || action.startsWith("a")) {
                log.append("\nAccepted\n");
                return log.toString();
            } else {
                log.append("ERROR: Unknown action '").append(action).append("'\n");
                return log.toString() + "\nRejected";
            }
        }

        if (steps >= maxSteps) {
            log.append("ERROR: Too many steps. Infinite loop suspected.\n");
        }

        return log.toString() + "\nRejected";
    }


    // private void printStackAndInput(Stack<String> stack, List<String> input) {
    //     System.out.println(String.join(" ", stack) + "\t\t\t\t\t" + String.join(" ", input));
    // }

}
