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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class JoinController implements Initializable {

	@FXML TextField useId;
	@FXML PasswordField usePw,rePw;
	@FXML Button goHome;
	@FXML Button joinOk;
	@FXML Button idCheck;
	
	Client c = MainController.getClient();
	
	private Stage stage = StageStore.stage;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
				
		goHome.setOnAction(e->{
			moveToLogin();
		});
		
		joinOk.setOnAction(e->{
			String id = useId.getText();
			String pw = usePw.getText();
			String rePwd = rePw.getText();
			
			if(!pw.trim().equals("") &&
					!id.trim().equals("")&&
					!id.trim().contains(",")&&
					!pw.trim().contains(",")
			  ) 
			{
				if(pw.equals(rePwd)) {
					c.toServer("join",id+","+pw);
				}else {
					Alert a = new Alert(AlertType.WARNING);
					a.setContentText("비밀번호가 다른게 아닐까..?");
					a.showAndWait();
				}
			} else {
				Alert a = new Alert(AlertType.WARNING);
				a.setContentText("사용하실 아이디와 비밀번호를 입력부터 하세요!!!!");
				a.showAndWait();
			}
		});
		
		idCheck.setOnAction(e->{
			String checkId = useId.getText();
			if(!checkId.trim().equals("")) {
				c.toServer("idCheck",checkId);
			}else{
				Alert a = new Alert(AlertType.WARNING);
				a.setContentText("아이디 작성부터 해라");
				a.showAndWait();
			}
		});
	}

	public void moveToLogin() {
		try {
			Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));
			Scene loginScene = new Scene(login);
			stage.setScene(loginScene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
