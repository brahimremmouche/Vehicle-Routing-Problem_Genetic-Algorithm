/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author dell
 */
public class CustomRunController implements Initializable {

    @FXML private GridPane root;
    @FXML private Spinner<Integer> POPULATION_SIZE;
    @FXML private Spinner<Integer> MUTATE_PROBABILITY;
    @FXML private Spinner<Integer> NUMBERGENERATIONS;
    @FXML private Slider fx;
    @FXML private Label distance_coefficient;
    @FXML private Label vehicles_coefficient;
    
    private MainWindowController rootClass;
    private int distance_coefficient_value = 50;
    private int vehicles_coefficient_value = 50;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        POPULATION_SIZE.setValueFactory(new SpinnerValueFactory.
                IntegerSpinnerValueFactory(10, Integer.MAX_VALUE, 100));
        MUTATE_PROBABILITY.setValueFactory(new SpinnerValueFactory.
                IntegerSpinnerValueFactory(0, 100, 10));
        NUMBERGENERATIONS.setValueFactory(new SpinnerValueFactory.
                IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 300));
        POPULATION_SIZE.getStyleClass().
                add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        MUTATE_PROBABILITY.getStyleClass().
                add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        NUMBERGENERATIONS.getStyleClass().
                add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        fx.valueProperty().addListener((observable, oldValue, newValue) -> {
            distance_coefficient.setText(String.valueOf(Math.round(100-fx.getValue())));
            vehicles_coefficient.setText(String.valueOf(Math.round(fx.getValue())));
            distance_coefficient_value = Integer.parseInt(String.valueOf(Math.round(100-fx.getValue())));
            vehicles_coefficient_value = Integer.parseInt(String.valueOf(Math.round(fx.getValue())));
        });
    }

    public void init(MainWindowController root) {
        this.rootClass = root;
    }
    
    @FXML
    void Cancale(ActionEvent event) {
        root.getScene().getWindow().hide();
    }

    @FXML
    void run(ActionEvent event) {
        POPULATION_SIZE.getValueFactory().setValue(
                Integer.parseInt(POPULATION_SIZE.getEditor().getText()));
        MUTATE_PROBABILITY.getValueFactory().setValue(
                Integer.parseInt(MUTATE_PROBABILITY.getEditor().getText()));
        NUMBERGENERATIONS.getValueFactory().setValue(
                Integer.parseInt(NUMBERGENERATIONS.getEditor().getText()));
        rootClass.runAG(POPULATION_SIZE.getValue(),
                MUTATE_PROBABILITY.getValue(),
                NUMBERGENERATIONS.getValue(),
                distance_coefficient_value,
                vehicles_coefficient_value);
        root.getScene().getWindow().hide();
    }
    
    @FXML
    void check(KeyEvent event) {
        Spinner s = (Spinner)event.getSource();
        try {
            Integer.parseInt(s.getEditor().getText());
        } catch (NumberFormatException e) {
            switch (s.getId()) {
                case "POPULATION_SIZE":
                    s.getEditor().setText("100");
                    break;
                case "MUTATE_PROBABILITY":
                    s.getEditor().setText("10");
                    break;
                case "NUMBERGENERATIONS":
                    s.getEditor().setText("300");
                    break;
            }
        }
    }
}
