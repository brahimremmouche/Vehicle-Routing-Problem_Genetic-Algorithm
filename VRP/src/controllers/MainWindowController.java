/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shapes.Circle;
import shapes.Line;
import vrp.*;
import shapes.Rectangle;

/**
 * FXML Controller class
 *
 * @author Brahim
 */
public class MainWindowController implements Initializable {
    
    @FXML private BorderPane root;
    @FXML private Label runTime;
    @FXML private Label status;
    @FXML private Label file_name;
    @FXML private Label vehicle_number;
    @FXML private Label vehicle_capacity;
    @FXML private Label customers_number;
    @FXML private Label distance;
    @FXML private Label vehicles;
    @FXML private Label sb_info;
    @FXML private Label sb_generation;
    
    private StackPane stackPane;
    
    private GraphPane graphPane;
    private Instance instance = null;
    private AlgorithmResult result;
    private Thread runThread;
    
    private ObservableList<Node> solution_nodes;
    
    private final Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.YELLOW,
        Color.GREEN, Color.BLACK, Color.PINK, Color.ORANGE, Color.MAGENTA,
        Color.LIGHTSEAGREEN, Color.CYAN, Color.SPRINGGREEN, Color.MEDIUMVIOLETRED};
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sb_info.setText("");
        sb_generation.setText("");
        graphPane = new GraphPane(15);
        root.setCenter(graphPane);
        Event e = new Event(EventType.ROOT);
        graphPane.layoutBoundsProperty().addListener((observable) -> {
            resize(e);
        });
        solution_nodes = FXCollections.observableArrayList();
        runThread = new Thread();
        ProgressIndicator progressIndicator = new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setScaleX(7);
        progressIndicator.setScaleY(7);
        Label label = new Label("Please wait for it to finish...");
        label.setPrefWidth(230);
        label.setPrefHeight(28);
        label.setStyle("-fx-background-color: #FFFFFF88;-fx-border-color: rgba(0,150,201,0.53);-fx-padding: 8 16 8 16;-fx-border-radius: 12;-fx-background-radius: 12;-fx-border-width: 2;-fx-font-size: 14;-fx-text-alignment: center;");
        stackPane = new StackPane(progressIndicator, label);
        Platform.runLater(() -> {
            root.getScene().getWindow().setOnCloseRequest((event) -> {
                close(null);
            });
        });
    }

    public void statusBar(String target, String status) {
        Platform.runLater(()->{
            if (target.equals("generation")) this.sb_generation.setText(status);
            else if (target.equals("info")) this.sb_info.setText(status);
            else if (target.equals("status")) this.status.setText(status);
        });
    }
    
    private void resize(Event e) {
        e.consume();
        if ( instance != null ) {
            Platform.runLater(() -> {
                this.draw(graphPane);
                if (instance.isResolved()) {
                    this.drawSolution(graphPane);
                }
                System.gc();
            });
        } else {
            graphPane.drawBorder();
        }
        adjustProgress(230, 28);
    }
    
    private void adjustProgress(int x, int y) {
        Platform.runLater(() -> {
            stackPane.setLayoutX((graphPane.getWidth()-x)/2);
            stackPane.setLayoutY((graphPane.getHeight()-y)/2);
        });
    }
    
    @FXML void about(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent aboutRoot = FXMLLoader.load(getClass().getResource("/views/About.fxml"));
        Scene scene = new Scene(aboutRoot, 400, 275);
        stage.setTitle("About");
        stage.getIcons().add(new Image("/icons/icons8-info-squared-80.png"));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    @FXML void adjustGraph(ActionEvent event) {
        graphPane.adjust();
    }
    
    @FXML void close(ActionEvent event) {
        if (runThread.isAlive()) {
            runThread.interrupt();
        }
        System.exit(0);
    }
    
    @FXML void openFile(ActionEvent event) {
        if (!runThread.isAlive()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("STP files (*.txt)", "*.txt"));
            fileChooser.setTitle("Choose Data File");
            File file = fileChooser.showOpenDialog(null);
            if ( file != null && file.exists() ) {
                changeStatusMsg("Loading Data from file ...");
                try {
                    instance = new Instance(file);
                } catch (Exception ex) {
                    instance = null;
                    (new Alert(Alert.AlertType.ERROR, "File Syntax Error !")).show();
                }
                this.setInformations(instance);
                if ( instance != null ) {
                    changeStatusMsg("Loading Graph ...");
                    Platform.runLater(() -> {this.draw(graphPane);});
                } else {
                    changeStatusMsg("No file selected");
                    graphPane.clear();
                }
            } else {
                //(new Alert(Alert.AlertType.ERROR, "File Not Correct")).show();
            }
        } else {
            (new Alert(Alert.AlertType.INFORMATION, "The algorithm is running, try again after completion .")).show();
        }
    }
    
    private void draw(GraphPane graphPane) {
        int [][] graphCoordinate = instance.getGraphCoordinate((int)graphPane.getWidth(), (int)graphPane.getHeight(), graphPane.MARGIN);
        
        ObservableList<StackPane> components = FXCollections.observableArrayList();
        components.add(new Rectangle("Depot",
                instance.getDepositInformations(),(double)graphCoordinate[0][0],
                (double)graphCoordinate[0][1], 50, 35, Color.BLANCHEDALMOND, Color.CHOCOLATE, 2));
        for (int i = 1; i < graphCoordinate.length; i++) {
            components.add(new Circle(i, instance.getCustomerInformations(i),
                    graphCoordinate[i][0], graphCoordinate[i][1], 8, Color.BISQUE, Color.BROWN, 1));
        }
        changeStatusMsg("Designing Graph ...");
        graphPane.drawGraph(components);
        changeStatusMsg("Done");
    }
    
    private void drawSolution(GraphPane graphPane) {
        ArrayList<Integer> path = instance.getSolutionPATH();
        String total_distance = String.valueOf(instance.getSolutionDistance());
        String vehicles_used = String.valueOf((int)instance.getSolutionVehicles_number());
        this.distance.setText(total_distance);
        this.vehicles.setText(vehicles_used);
        // ---------------------------------------------------------------------
        solution_nodes.clear();
        int j = 0;
        int[] c01 = instance.getNodeCoordinate(0);
        int[] c02 = instance.getNodeCoordinate(path.get(0));
        solution_nodes.add(new Line(c01[0], c01[1], c02[0], c02[1], colors[j%colors.length], StrokeLineCap.BUTT, 1));
        for (int i = 0; i < path.size()-1; i++) {
            if (path.get(i) == 0) j++;
            int[] c1 = instance.getNodeCoordinate(path.get(i));
            int[] c2 = instance.getNodeCoordinate(path.get(i+1));
            solution_nodes.add(new Line(c1[0], c1[1], c2[0], c2[1], colors[j%colors.length], StrokeLineCap.BUTT, 1));
        }
        int[] cl1 = instance.getNodeCoordinate(path.get(path.size()-1));
        int[] cl2 = instance.getNodeCoordinate(0);
        solution_nodes.add(new Line(cl1[0], cl1[1], cl2[0], cl2[1], colors[j%colors.length], StrokeLineCap.BUTT, 1));
        //----------------------------------------------------------------------
        graphPane.addComponents(solution_nodes, 0);
    }
    
    public void drawSolution() {
        Platform.runLater(() -> {
            graphPane.removeComponents(solution_nodes);
            drawSolution(graphPane);
        });
    }
    
    public void setProgressText(String text) {
        Platform.runLater(() -> {((Label)stackPane.getChildren().get(1)).setText(text);});
    }
    
    private void startProgress(String text) {
        ((Label)stackPane.getChildren().get(1)).setText(text);
        ((Pane)root.getCenter()).getChildren().add(stackPane);
        adjustProgress(230, 28);
    }
    
    private void stopProgress(int e) {
        Platform.runLater(() -> {
            ((Pane)root.getCenter()).getChildren().remove(stackPane);
            if (e == 0) changeStatusMsg("Done");
            else changeStatusMsg("Stopped");
        });
    }
    
    @FXML void run(ActionEvent event) {
        if (instance != null) {
            if (!runThread.isAlive()) {
                changeStatusMsg("Process");
                draw(graphPane);
                this.distance.setText("- - -");
                this.vehicles.setText("- - -");
                runThread = new Thread(() -> {
                    Timer time = new Timer(runTime);
                    GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(this, instance, 100);
                    try {
                        Platform.runLater(() -> {runTime.setText("- - -");});
                        /*----------------------------------------------------*/
                        time.start();
                        geneticAlgorithm.start();
                        /*----------------------------------------------------*/
                        stopProgress(0);
                    } catch (Exception ex) {stopProgress(1); }
                    time.interrupt();
                    result = geneticAlgorithm.getResult(runTime.getText());
                });
                runThread.start();
                startProgress("Please wait for it to finish...");
            } else {
                (new Alert(Alert.AlertType.INFORMATION, "The algorithm is running, try again after completion .")).show();
            }
        } else {
            (new Alert(Alert.AlertType.INFORMATION, "Open an instance to start the algorithm .")).show();
        }
    }
    
    public void runAG(int POPULATION_SIZE, int MUTATE_PROBABILITY, int NUMBERGENERATIONS,
                      int distance_coefficient, int vehicles_coefficient) {
        Platform.runLater(() -> {
            changeStatusMsg("Process");
            draw(graphPane);
            this.distance.setText("- - -");
            this.vehicles.setText("- - -");
            runThread = new Thread(() -> {
                Timer time = new Timer(runTime);
                GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(this, instance,
                        POPULATION_SIZE, MUTATE_PROBABILITY, NUMBERGENERATIONS,
                        distance_coefficient, vehicles_coefficient);
                try {
                    Platform.runLater(() -> {runTime.setText("- - -");});
                    /*----------------------------------------------------*/
                    time.start();
                    geneticAlgorithm.start();
                    /*----------------------------------------------------*/
                    stopProgress(0);
                } catch (Exception ex) { stopProgress(1); }
                time.interrupt();
                result = geneticAlgorithm.getResult(runTime.getText());
            });
            runThread.start();
            startProgress("Please wait for it to finish...");
        });
    }
    
    @FXML void custom_run(ActionEvent event) throws IOException {
        if (instance != null) {
            if (!runThread.isAlive()) {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CustomRun.fxml"));
                Parent CustomRun = loader.load();
                loader.<CustomRunController>getController().init(this);
                Scene scene = new Scene(CustomRun, 375, 330);
                stage.setTitle("Custom Parameters");
                stage.getIcons().add(new Image("/icons/icons8-info-squared-80.png"));
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } else {
                (new Alert(Alert.AlertType.INFORMATION, "The algorithm is running, try again after completion .")).show();
            }
        } else {
            (new Alert(Alert.AlertType.INFORMATION, "Open an instance to start the algorithm .")).show();
        }
    }
    
    @FXML void kill(ActionEvent event) {
        if (runThread.isAlive()) {
            runThread.interrupt();
        } else {
            System.out.println("thread not alive");
        }
    }

    @FXML void show_result(ActionEvent event) throws IOException {
        if (instance != null) {
            if (instance.getSolutionPATH() != null) {
                if (!runThread.isAlive()) {
                    Stage stage = new Stage();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Result.fxml"));
                    Parent CustomRun = loader.load();
                    loader.<ResultController>getController().init(result);
                    Scene scene = new Scene(CustomRun, 800, 600);
                    stage.setTitle("Result");
                    stage.getIcons().add(new Image("/icons/icons8-info-squared-80.png"));
                    stage.setScene(scene);
                    //stage.setResizable(false);
                    stage.show();
                }
            } else {
                (new Alert(Alert.AlertType.INFORMATION, "Run to show result .")).show();
            }
        } else {
            (new Alert(Alert.AlertType.INFORMATION, "Open an instance and run it to show result .")).show();
        }
    }
    
    private void setInformations(Instance graph) {
        if ( graph == null ) {
            this.file_name.setText("- - -");
            this.vehicle_number.setText("- - -");
            this.vehicle_capacity.setText("- - -");
            this.customers_number.setText("- - -");
            this.distance.setText("- - -");
            this.vehicles.setText("- - -");
            this.runTime.setText("- - -");
        } else {
            this.file_name.setText(graph.getName());
            this.vehicle_number.setText(String.valueOf(graph.getVehicles_number()));
            this.vehicle_capacity.setText(String.valueOf(graph.getVehicle_capacity()));
            this.customers_number.setText(String.valueOf(graph.getCustomers_number()-1));
            this.distance.setText("- - -");
            this.vehicles.setText("- - -");
            this.runTime.setText("- - -");
        }
    }
    
    private void changeStatusMsg(String msg) {
        Platform.runLater(() -> { status.setText(msg); });
    }
    
}
