/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package vrp;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 *
 * @author brahim
 */
public class VRP extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        
        Font.loadFont(VRP.class.getResource("/fonts/SansitaOne.ttf").toExternalForm(), 12);
        Font.loadFont(VRP.class.getResource("/fonts/arial.ttf").toExternalForm(), 12);
        Font.loadFont(VRP.class.getResource("/fonts/arialbd.ttf").toExternalForm(), 12);
        Font.loadFont(VRP.class.getResource("/fonts/arialbi.ttf").toExternalForm(), 12);
        Font.loadFont(VRP.class.getResource("/fonts/ariali.ttf").toExternalForm(), 12);
        Font.loadFont(VRP.class.getResource("/fonts/ariblk.ttf").toExternalForm(), 12);
        Parent root = FXMLLoader.load(VRP.class.getResource("/views/MainWindow.fxml"));
        Scene scene = new Scene(root, 1000, 600);
        
        stage.setTitle("VRP");
        stage.getIcons().add(new Image("/icons/icons8-navigate-480.png"));
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
