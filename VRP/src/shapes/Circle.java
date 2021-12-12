/*
 * Copyright (C) 2019 brahim
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public class Circle extends StackPane {
    
    private final String id;
    private String informations;
    private Info info;
    private boolean isVisible = false;
    
    double x;
    double y;
    
    public Circle(Object txt, String informations, double d, double d1, double d2, Paint paint, Paint stroke, double width) {
        this.id = String.valueOf(txt);
        this.informations = informations;
        this.setLayoutX(d-d2);
        this.setLayoutY(d1-d2);
        javafx.scene.shape.Circle circle = new javafx.scene.shape.Circle();
        circle.setRadius(d2);
        circle.setFill(paint);
        circle.setStroke(stroke);
        circle.setStrokeWidth(width);   
        Text text = new Text(this.id);
        text.setFont(new Font(10));
        this.getChildren().addAll(circle, text);
        
        this.setOnMouseClicked((event) -> {
            if (!isVisible) {
                x = d+10;
                y = d1+5;
                double w = ((Pane)((Group)this.getParent()).getParent()).getWidth();
                double h = ((Pane)((Group)this.getParent()).getParent()).getHeight();
                this.info = new Info(this.informations, x, y, w, h);
                ((Group)((Circle)event.getSource()).getParent()).getChildren().add(info);
                isVisible = true;
            }
        });
        this.setOnMouseExited((event) -> {
            if (isVisible) {
                ((Group)((Circle)event.getSource()).getParent()).getChildren().remove(info);
                this.info = null;
                isVisible = false;
            }
        });
    }
    
}
