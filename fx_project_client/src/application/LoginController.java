package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * move() 함수 "문자열.fxml" 정리 미필 
 * */
	public class LoginController implements Initializable{
		
	@FXML TextField txtLoginID; 
	@FXML TextField txtLoginPw; 
	@FXML Button buttonMainJoin;
	@FXML Button buttonMainLogin;
		
	Client c = MainController.getClient();

	private Stage stage = StageStore.stage;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		buttonMainLogin.setOnAction(e->{
			String id = txtLoginID.getText().trim();
			String pw = txtLoginPw.getText().trim();
			
			if(!id.equals("") && !pw.equals("")) {
				c.toServer("log",id+","+pw);
			} else {
				Alert a = new Alert(AlertType.WARNING);
				a.setContentText("입력을 실수하셨군요");
				a.showAndWait();	
			} // 전달 X 
		}); // action
		
		buttonMainJoin.setOnAction(e->{
			moveToJoin();
		});
		
		txtLoginPw.setOnKeyPressed(e->{
			 if(e.getCode() == KeyCode.ENTER) {
	    		  buttonMainLogin.fire();
	    	  }
		});
	} // init
	
	public void moveToJoin() {
		try {
			Parent join = FXMLLoader.load(getClass().getResource("join.fxml"));
			Scene joinScene = new Scene(join);
			stage.setScene(joinScene);
		} catch (IOException e) {}
	}
}
