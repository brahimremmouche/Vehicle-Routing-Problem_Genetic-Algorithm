/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shapes;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author brahim
 */
public class Rectangle extends StackPane {
    
    private final String id;
    private String informations;
    private Info info;
    private boolean isVisible = false;
    
    double x;
    double y;
    
    public Rectangle(Object txt, String informations, double posX, double posY, double d1, double d2, Paint paint, Paint stroke, double width) {
        this.id = String.valueOf(txt);
        this.informations = informations;
        this.setLayoutX(posX-d1/2);
        this.setLayoutY(posY-d2/2);
        javafx.scene.shape.Rectangle rectangle = new javafx.scene.shape.Rectangle(d1, d2);
        rectangle.setFill(paint);
        rectangle.setStroke(stroke);
        rectangle.setStrokeWidth(width);
        rectangle.setStyle("-fx-arc-width: 8; -fx-arc-height: 8;");
        Text text = new Text(this.id);
        text.setFont(new Font(12));
        this.getChildren().addAll(rectangle, text);
        this.setOnMouseClicked((event) -> {
            if (!isVisible) {
                x = posX+10;
                y = posY+5;
                double w = ((Pane)((Group)this.getParent()).getParent()).getWidth();
                double h = ((Pane)((Group)this.getParent()).getParent()).getHeight();
                this.info = new Info(this.informations, x, y, w, h);
                ((Group)((Rectangle)event.getSource()).getParent()).getChildren().add(info);
                isVisible = true;
            }
        });
        this.setOnMouseExited((event) -> {
            if (isVisible) {
                ((Group)((Rectangle)event.getSource()).getParent()).getChildren().remove(info);
                this.info = null;
                isVisible = false;
            }
        });
    }
}
