package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class OutputController implements Initializable {

    @FXML
    private TextField input;

    @FXML
    private Label result;

    @FXML
    private TextArea output;
    
    @FXML
    private void handleGrammar(ActionEvent event){
        if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
            output.setText(GrammarInputController.lr0Parser.getGrammar()+"");
        }else{
            output.setText(GrammarInputController.lr1Parser.getGrammar()+"");
        }
    }

    @FXML
    private void handleFirst(ActionEvent event){
        if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
            String str = "";
            for(String s : GrammarInputController.lr0Parser.getGrammar().getFirstSets().keySet()){
                str += s + " : " + GrammarInputController.lr0Parser.getGrammar().getFirstSets().get(s) + "\n";
            }
            output.setText(str);
        }else{
            String str = "";
            for(String s : GrammarInputController.lr1Parser.getGrammar().getFirstSets().keySet()){
                str += s + " : " + GrammarInputController.lr1Parser.getGrammar().getFirstSets().get(s) + "\n";
            }
            output.setText(str);
        }
    }

    @FXML
    private void handleFollow(ActionEvent event){
        if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
            String str = "";
            for(String s : GrammarInputController.lr0Parser.getGrammar().getFallowSets().keySet()){
                str += s + " : " + GrammarInputController.lr0Parser.getGrammar().getFallowSets().get(s) + "\n";
            }
            output.setText(str);
        }else{
            String str = "";
            for(String s : GrammarInputController.lr1Parser.getGrammar().getFallowSets().keySet()){
                str += s + " : " + GrammarInputController.lr1Parser.getGrammar().getFallowSets().get(s) + "\n";
            }
            output.setText(str);
        }
    }

    @FXML
    private void handleState(ActionEvent event){
        if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
            output.setText(GrammarInputController.lr0Parser.canonicalCollectionStr());
        }else{
            output.setText(GrammarInputController.lr1Parser.canonicalCollectionStr());
        }
    }

    @FXML
    private void handleGoTo(ActionEvent event){
        if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
            output.setText(GrammarInputController.lr0Parser.goToTableStr());
        }else{
            output.setText(GrammarInputController.lr1Parser.goToTableStr());
        }
    }

    @FXML
    private void handleAction(ActionEvent event){
        if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
            output.setText(GrammarInputController.lr0Parser.actionTableStr());
        }else{
            output.setText(GrammarInputController.lr1Parser.actionTableStr());
        }
    }

    @FXML
    private void handleMergedTable(ActionEvent event) {
        if (GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")) {
            output.setText(GrammarInputController.lr0Parser.mergedTableStr());
        } else {
            output.setText(GrammarInputController.lr1Parser.mergedTableStr());
        }
    }

    @FXML
    private void handleStackImplementation(ActionEvent event) {
        String rawInput = input.getText().trim();
        if (rawInput.isEmpty()) {
            output.setText("Input string is empty!");
            return;
        }

        List<String> inputString = new ArrayList<>(Arrays.asList(rawInput.split("\\s+")));
        String resultStr = "";
        inputString.add("$");

        if (GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")) {
            HashMap<Integer, HashMap<String, String>> combinedTable = GrammarInputController.lr0Parser.getCombinedTable();
            List<String[]> productionList = GrammarInputController.lr0Parser.getProductionList();

            resultStr = GrammarInputController.lr0Parser.parseInputString(inputString, combinedTable, productionList);

        } else {
            HashMap<Integer, HashMap<String, String>> combinedTable = GrammarInputController.lr1Parser.getCombinedTable();
            List<String[]> productionList = GrammarInputController.lr1Parser.getProductionList();

            resultStr = GrammarInputController.lr1Parser.parseInputString(inputString, combinedTable, productionList);
        }

        output.setText(resultStr);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        result.setVisible(false);
        output.setStyle("-fx-font-family: monospace");
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            String str = input.getText();
            ArrayList<String> words = new ArrayList<>();
            String[] split = str.trim().split("\\s+");
            for(String s: split){
                words.add(s);
            }
            if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
                boolean accept = GrammarInputController.lr0Parser.accept(words);
                if(accept){
                    result.setText("Accepted");
                    result.setTextFill(Color.GREEN);
                    result.setVisible(true);
                }else {
                    result.setText("Rejected");
                    result.setTextFill(Color.RED);
                    result.setVisible(true);
                }
            }else{
                boolean accept = GrammarInputController.lr1Parser.accept(words);
                if(accept){
                    result.setText("Accepted");
                    result.setTextFill(Color.GREEN);
                    result.setVisible(true);
                }else {
                    result.setText("Rejected");
                    result.setTextFill(Color.RED);
                    result.setVisible(true);
                }
            }
        });
        if(GrammarInputController.parserKind.equals("LR(0)") || GrammarInputController.parserKind.equals("SLR(1)")){
            output.setText(GrammarInputController.lr0Parser.getGrammar()+"");
        }else{
            output.setText(GrammarInputController.lr1Parser.getGrammar()+"");
        }
    }
}
