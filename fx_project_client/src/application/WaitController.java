package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class WaitController implements Initializable {

	@FXML Button buttonWaitCancel;
	@FXML Label lblWaitNowWaiting;
	 
	Client c = MainController.getClient();
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Thread t = new Thread(() -> {
            String x = "매칭 대기중....";
            while (true) {
                Platform.runLater(() -> {
                    lblWaitNowWaiting.setText(lblWaitNowWaiting.getText() + ".");
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                if (lblWaitNowWaiting.getText().equals(x)) {
                    Platform.runLater(() -> {
                        lblWaitNowWaiting.setText("매칭 대기중");
                    });
                }
            }
        });
        t.setDaemon(true);
        t.start();
        
        buttonWaitCancel.setOnAction(e->{
        	c.toServer("closeRoom"," ");
        });
    }
}


