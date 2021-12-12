package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import vrp.AlgorithmResult;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ResultController implements Initializable {

    @FXML private AnchorPane root;
    @FXML private TextArea area;

    private AlgorithmResult result;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void init(AlgorithmResult result) {
        this.result = result;
        area.appendText("**********************************************************************************************");
        area.appendText("\n*    Instance Information ...                                                                *");
        area.appendText("\n**********************************************************************************************");
        area.appendText("\nFile Name : "+result.getFileName());
        area.appendText("\nVehicle Number : "+result.getMaxVehiclesNumber());
        area.appendText("\nVehicles Capacity : "+result.getMaxVehiclesCapacity());
        area.appendText("\nCustomers Number : "+result.getCustomersNumber());
        area.appendText("\n**********************************************************************************************");
        area.appendText("\n*    Initial Paramaters...                                                                   *");
        area.appendText("\n**********************************************************************************************");
        area.appendText("\nPopulation Size : "+result.getPopulationSize()+" individual.");
        area.appendText("\nMutation Probability : "+result.getMutationProbability()+" %.");
        area.appendText("\nFitness Function : f(D, V) = "+result.getFitnessFunction()+".");
        area.appendText("\nD : Distance, V : Vehicles Number.");
        area.appendText("\n**********************************************************************************************");
        area.appendText("\n*    Results...                                                                              *");
        area.appendText("\n**********************************************************************************************");
        area.appendText("\nProblem Solved After "+result.getGenerationsNumber()+" generation in "+result.getTime()+".");
        area.appendText("\nTotal Distance : "+result.getDistance());
        area.appendText("\nNumber of Vehicles Used : "+result.getVehicleNumber()+" Vehicle.");
        area.appendText("\n----------------------------------------------------------------------------------------------");
        int v = 1;
        String s = "\nVehicle "+v+" : D";
        for (Integer elem:result.getPATH()) {
            if (elem == 0) {
                area.appendText(s+" → D");
                s = "\nVehicle "+(++v)+" : D";
            } else {
                s += " → "+elem;
            }
        }
        area.appendText(s+" → D");
        area.appendText("\n**********************************************************************************************");
        area.appendText("\nFull Path : "+result.getPATH()+"\n");
    }

    @FXML void close(ActionEvent event) {
        root.getScene().getWindow().hide();
    }

    @FXML void save(ActionEvent event) {
        try {
            String fileName = "VRP_"+result.getFileName()+"_"+
                    (DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")).format(LocalDateTime.now())+"_"+result.getDistance()+".txt";
            Writer writer = null;
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"));
            writer.write(area.getText());
            writer.close();
            (new Alert(Alert.AlertType.INFORMATION, "Successed, File saved .")).show();
        } catch (IOException e) {
            (new Alert(Alert.AlertType.ERROR, "Error, File not saved .")).show();
        }
    }
}
