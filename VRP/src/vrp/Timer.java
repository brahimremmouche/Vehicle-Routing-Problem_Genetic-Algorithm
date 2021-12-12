package vrp;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Timer extends Thread {

    private Label root;

    public Timer(Label root) {
        this.root = root;
    }

    @Override
    public void run() {
        Platform.runLater(()->{ root.setText("0 second"); });
        int s = 1;
        try {
            while (true) {
                sleep(1000);
                String time = s+" second";
                s++;
                Platform.runLater(()->{ root.setText(time); });
            }
        } catch (InterruptedException e) {}
    }
}
