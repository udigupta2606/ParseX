package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lr0.LR0Parser;
import lr1.LR1Parser;
import util.Grammar;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class GrammarInputController implements Initializable{

    @FXML
    private Label error;

    @FXML
    private Label selectedParserLabel;


    @FXML
    private TextArea input;

    @FXML
    private ComboBox parser;

    public static String parserKind;

    public static LR0Parser lr0Parser;

    public static LR1Parser lr1Parser;
    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        if(parser.getValue() == null){
            error.setText("Choose a parser");
            error.setVisible(true);
        }else{
            parserKind = (String)parser.getValue();
            boolean canBeParse = true;
            String grammarText = input.getText();

            if (!isValidGrammar(grammarText)) {
                error.setText("Invalid Grammar!");
                error.setVisible(true);
                return;
            }

            Grammar grammar = new Grammar(grammarText);
            if(parser.getValue().equals("LR(0)") || parser.getValue().equals("SLR(1)")){
                lr0Parser = new LR0Parser(grammar);
                if(parser.getValue().equals("LR(0)")){
                    canBeParse = lr0Parser.parserLR0();
                }else {
                    canBeParse = lr0Parser.parserSLR1();
                }
            }else {
                lr1Parser = new LR1Parser(grammar);
                if(parser.getValue().equals("CLR(1)")){
                    canBeParse = lr1Parser.parseCLR1();
                }else {
                    canBeParse = lr1Parser.parseLALR1();
                }
            }
            if(!canBeParse){
                error.setText("The grammar can not be parsed. Choose a different parser or grammar");
                error.setVisible(true);
            }else{
                Button button = (Button)event.getSource();
                Stage stage = (Stage)button.getScene().getWindow();
                Parent root = FXMLLoader.load(getClass().getResource("Output.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);
            }
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        error.setVisible(false);

        ObservableList<String> options = FXCollections.observableArrayList(
                "LR(0)",
                "SLR(1)",
                "CLR(1)",
                "LALR(1)"
        );
        parser.setItems(options);

        parser.setOnAction(event -> {
            String selected = (String) parser.getValue();
            selectedParserLabel.setText("Selected: " + selected);
        });
    }

    private static boolean isValidGrammar(String grammarStr) {
        Set<String> definedNonTerminals = new HashSet<>();
        Set<String> usedNonTerminals = new HashSet<>();

        String[] lines = grammarStr.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("->");
            if (parts.length != 2) {
                return false; 
            }

            String lhs = parts[0].trim();
            String rhs = parts[1].trim();

            definedNonTerminals.add(lhs);

            String[] productions = rhs.split("\\|");
            for (String production : productions) {
                for (String symbol : production.trim().split("\\s+")) {
                    symbol = symbol.trim();
                    if (!symbol.isEmpty() && Character.isUpperCase(symbol.charAt(0))) {
                        usedNonTerminals.add(symbol);
                    }
                }
            }
        }

        for (String nonTerminal : usedNonTerminals) {
            if (!definedNonTerminals.contains(nonTerminal)) {
                return false; 
            }
        }

        return true;
    }

}
