package application;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import userVO.UserVO;

public class WaitRoomController implements Initializable {

   @FXML TableView<UserVO> tableview;
   @FXML TextArea chatArea;
   @FXML TextField inputText;
   @FXML Button send_btn,request_btn,logout_btn;
   
   ObservableList<UserVO> userlist = FXCollections.observableArrayList();
   
   private Client c = MainController.getClient();
   
   @Override
   public void initialize(URL arg0, ResourceBundle arg1) {
	   
	   Class<UserVO> clazz = UserVO.class;
	   Field[] fields = clazz.getDeclaredFields();
		
	   for(int i = 0; i < fields.length; i++) {
			String name = fields[i].getName();
			
			TableColumn<UserVO, ?> tc = new TableColumn<>(name);
			tc.setCellValueFactory(new PropertyValueFactory<>(name));
			// column 너비 지정
			tc.setPrefWidth(85);
			// column 크기 수정 불가
			tc.setResizable(false);
			tableview.getColumns().add(tc);
		}
	   
      send_btn.setOnAction(e->{
         String chat = inputText.getText();
         if(chat != null) {
        	 c.toServer("waitChat",chat);
         }
         inputText.clear();
      });
      
      inputText.setOnKeyPressed(e ->{
    	  if(e.getCode() == KeyCode.ENTER) {
    		  send_btn.fire();
    	  }
      });
     
      request_btn.setOnAction(e -> {
    	  c.toServer("matching"," ");
      });
      
      logout_btn.setOnAction(e->{
    	  c.toServer("logOut"," ");
      });
      
   }
   public void printText(String message) {
	   Platform.runLater(()->{
		   chatArea.appendText(message+"\n");
	   });
   }
   // 테이블에 추가해주는 놈
   public void addList(String nick, String win, String lose , String now) {
	    userlist.add(new UserVO(nick,Integer.parseInt(win),Integer.parseInt(lose),now));
		tableview.setItems(userlist);
   }
}