/*
 * Copyright (C) 2019 dell
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

import java.util.Collections;
import javafx.collections.FXCollections;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author brahim
 */
public class Info extends StackPane {

    public Info(String text, double x, double y, double w, double h) {
        int width = 6+max(text.split("\n"))*8;
        int height = 18*text.split("\n").length;
        if ( x + width > w ) x -= (20+width);
        if ( y + height+5 > h ) y -= 10+height;
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setOpacity(0.9);
        Rectangle r = new Rectangle(width, height);
        r.setFill(Color.BISQUE);
        r.setStroke(Color.BROWN);
        r.setStrokeWidth(2);
        r.setStyle("-fx-arc-width: 6; -fx-arc-height: 6;");
        Text t = new Text(text);
        t.setStyle("-fx-font-family: 'monospaced';-fx-font-size: 12;");
        this.getChildren().addAll(r,t);
    }
    
    private int max(String[] args) {
        int m = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].length() > m) {
                m = args[i].length();
            }
        }
        return m;
    }
    
}
