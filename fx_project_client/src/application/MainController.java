package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainController implements Initializable {

	private static Client client;
	
	public static Client getClient() {
		return client;
	}
	
	private double i = 0;
	private Stage stage = StageStore.stage;
	@FXML AnchorPane hide;
	@FXML Button startClient;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		startClient.requestFocus();
		
		startClient.setOnKeyPressed(e->{
			client = new Client(this);
			client.ClientStart();
			moveToLogin();
		});

		Thread t = new Thread(()->{
			while(true) {
				try {
					hide.setOpacity(i);
					if(i < 1) {
						i += 0.01;
					}
					if(i >= 1) {
						i = 0;
					}
					Thread.sleep(15);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		});
		t.setDaemon(true);
		t.start();
	}
	
	public void moveToLogin() {
		try {
			Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));
			Scene loginScene = new Scene(login);
			stage.setScene(loginScene);
		} catch (IOException e) {e.printStackTrace();}
	}
}
