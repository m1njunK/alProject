package application;

/**
 * 해당 Client 클래스는 FXML 바인딩 클래스이나 
 * 오류로 인해 FXML 파일 생성 불가 
 * 
 * Client.fxml의 fx:controller="Client" 참고
 * 
 * 원래 서버클래스와 다른 프로젝트 파일에 있음 참고
 * 
 * */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import userVO.UserVO;

public class Client implements Initializable {

	@FXML
	Button clientStart;
	@FXML
	TextArea clientNoti;
	@FXML
	Button toServer;
	@FXML
	TextField idInput;
	
	Stage stage = StageStore.stage;

	private final String serverIP = "10.100.205.175";
	private final int port = 7777;

	Codes code;
	Socket client = null;
	
	// ------------IO 스트림
	PrintWriter clientOutput = null;
	BufferedReader clientInput = null;
	
	WaitRoomController wrc;
	GameController gc;
	
	public Client(MainController m) {};

	public void ClientStart() {
		try {
			// System.out.println("콘솔 clientStart ");
			code = new Codes();
			// System.out.println("1");

			client = new Socket(serverIP, port);
			System.out.println(client);
			System.out.println("Server연결 완료!!");

			clientOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

			clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
			fromServer();
			System.out.println("3");
		} catch (UnknownHostException e) {
			toServer("logOut", "logOut");
			System.out.println("서버와 연결이 끊어졌습니다 ㅠ");
		} catch (IOException e) {
			Platform.runLater(() -> {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("서버연결 끊김");
				alert.setHeaderText("서버와 연결이 실패하였습니다.");
				alert.setContentText("재접 ㄱㄱ");
				alert.showAndWait();

				StageStore.stage.close();
				stopClient();
			});
		}
	}

	// 자원해제 후 client 종료
	public void stopClient() {
		if (client != null && !client.isClosed()) {
			try {
				client.close();
			} catch (IOException e) {
			}
		}
	}

	// 서버에 메세지 전달 루트
	public void toServer(String code, String content) {
		System.out.println("toServer실행");
		clientOutput.println(code + "|" + content);
		System.out.println("코드 컨");
	}
	
	// 회원가입 완료 버튼을 눌렀을 때 실행된다.

	// 이미지를 클릭했을때 포커스가 유지되게 해야 함
	// @FXML field imageview인스턴스 .setOnAction () -> { }
//      public void join() {
//         String id="getText문 들어오는 자리" +"|",
//               pw="getText문 들어오는 자리"+"|",
//               pw2="getText문 들어오는 자리"+"|", 
//               imageUrl  = getImageUrl();
//         if(!pw.equals(pw2)) return;    // dialog 띄우기 추가 
//            
//            // 두 비밀번호가 일치하지 않습니다
//         
////         toServer(code.join,id+pw+pw2+imageUrl);
//         rootChange("");
//         
//      }

	public void fromServer() {
		System.out.println("fromServer start");
		Thread t = new Thread(() -> {
			while (true) {
				try {
					System.out.println("5");
					String message = clientInput.readLine();
					
					if (message == null) {
						Platform.runLater(() -> {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("서버연결 끊김");
							alert.setHeaderText("서버와 연결이 실패하였습니다.");
							alert.setContentText("재접 ㄱㄱ");
							alert.showAndWait();
							StageStore.stage.close();
						});
						stopClient();
						break;
					}
					Platform.runLater(() -> {
						if (message.equals("아이디중복")) {
							Alert a = new Alert(AlertType.ERROR);
							a.setContentText("이미 사용 중인 아이디입니다.");
							a.showAndWait();
						} else if (message.equals("회원가입완료")) {
							Alert a = new Alert(AlertType.INFORMATION);
							a.setContentText("회원 가입 성공!!");
							a.showAndWait();
							moveToLogin();
						} else if (message.equals("로그인오류")) {
							Alert a = new Alert(AlertType.ERROR);
							a.setContentText("아이디가 없거나 비밀번호가 틀렸습니다.");
							a.showAndWait();
						} else if (message.equals("닉네임중복")) {
							Alert a = new Alert(AlertType.ERROR);
							a.setContentText("사용 중인 닉네임입니다.");
							a.showAndWait();
						} else if (message.equals("아이디사용가능")) {
							Alert a = new Alert(AlertType.INFORMATION);
							a.setContentText("사용 가능한 아이디 입니다.");
							a.showAndWait();
						} else if (message.equals("log")) {
							move();
						} else if (message.equals("gameWait")) {
							moveToWait();
						} else if (message.equals("good")) {
							System.out.println("매칭 완료!");
							gamingRoom();
						} else if(message.startsWith("roomChat")) {
	                    	String chat = message.replace("roomChat","");  
	                    	gc.printText(chat);
	                    } else if(message.contains("waitChat")){
	                    	String chat = message.replace("waitChat", "");
	                    	wrc.printText(chat); 
						} else if(message.contains("moveBlack") && gc != null){
							String move = message.replace("moveBlack,", "");
							String xy[] = move.split(",");
							// xy[0] == stoneID
							String id = xy[0];
							double x = Double.parseDouble(xy[1]);
							double y = Double.parseDouble(xy[2]);
							if(gc != null) {
								gc.anotherMoveBlack(id,x,y);
							}
						} else if(message.contains("moveWhite") && gc != null) {
							String move = message.replace("moveWhite,", "");
							String xy[] = move.split(",");
							String id = xy[0];
							double x = Double.parseDouble(xy[1]);
							double y = Double.parseDouble(xy[2]);
							if(gc != null) {
								gc.anotherMoveWhite(id,x,y);
							}
						} else if(message.contains("선공") && gc != null) {
							gc.printText("게임이 시작되었습니다!"+"\n");
							gc.printText(message.replace("선공", "")+"님 흑돌(선공) 입니다."+"\n");
							gc.setColor("Black");
							gc.setTurn("myTurn");
						} else if(message.contains("후공") && gc != null) {
							gc.printText("게임이 시작되었습니다"+"\n");
							gc.printText(message.replace("후공", "")+"님 백돌(후공) 입니다."+"\n");
							gc.setColor("White");
							gc.setTurn("yourTurn");
						} else if(message.equals("isYourTurn")) {
							gc.setTurn("myTurn");
						} else if(message.equals("win")) {
							win();
							move();
						} else if(message.equals("lose")) {
							lose();
							move();
						} else if(message.equals("logOut")) {
							moveToLogin();
						} else if(message.equals("closeRoom")) {
							move();
						} else if(message.contains("addList")) {
							if(wrc != null) {
								String[] list = message.split(",");
								// 1      2     3      4
								// nick  win   lose   now
								System.out.println("받았다! : " + Arrays.toString(list));
								wrc.addList(list[1], list[2], list[3], list[4]);
							}
						} else if(message.contains("removeUser")) {
							System.out.println("remove는 왔지만");
							String[] remove = message.split("\\|");
							String nick = remove[1];
							System.out.println(nick);
							UserVO removeUser;
							for(UserVO user : wrc.userlist) {
								System.out.println(user.getNick());
								if(user.getNick().trim().equals(nick)) {
									System.out.println("참입니다");
									removeUser = user;
									wrc.userlist.remove(removeUser);
									wrc.tableview.setItems(wrc.userlist);
									break;
								}
							}
						} else if(message.equals("giveUpWin")) {
							toServer("win"," ");
						}
					}); // runLater
				}catch (IOException e) {
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("서버연결 끊김");
						alert.setHeaderText("서버와 연결이 실패하였습니다.");
						alert.setContentText("재접 ㄱㄱ");
						alert.showAndWait();
						StageStore.stage.close();
					});
					stopClient();
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	private void lose() {
		Platform.runLater(()->{
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setHeaderText("패배..");
			a.setContentText("분발하세요!");
			a.showAndWait();
		});
	}

	private void win() {
		Platform.runLater(()->{
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setHeaderText("승리!");
			a.setContentText("ㅊㅋㅊㅋ!");
			a.showAndWait();
		});
	}

	public void move() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("waitRoom.fxml"));
			Parent waitRoom = loader.load();
			Scene waitRoomScene = new Scene(waitRoom);
			stage.setScene(waitRoomScene);
			wrc = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public void moveToWait() {
		try {
			Parent wait = FXMLLoader.load(getClass().getResource("wait.fxml"));
			Scene waitScene = new Scene(wait);
			stage.setScene(waitScene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void gamingRoom() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
			Parent game = loader.load();
			Scene gameScene = new Scene(game);
			stage.setScene(gameScene);
			gc = loader.getController();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// 선택한 이미지에 따라서 매개변수 다르게 설정 image1 image2... 모두 버튼식
	// 클릭되었을 때 실행된다.
	// 누가 구현좀
//      public String getImageUrl() {
//         // 포커스 유지된 (선택된) 이벤트에 따라서 작동하게
//         
//         int selectNum = 1; 
//         String url = "";
//         switch(selectNum) {
//         case 1:
//            url = "img/image1";
//            break;
//         case 2:
//            url = "img/image2";
//            break;
//         case 3:
//            url = "";
//            break;
//         case 4:
//            url = "";
//            break;
//         }
//         
//         return url;
//      }

//      //  client UI변경스레드 
//      public void clientCurrent(String message) {
//         
//         Platform.runLater(() -> {
//            clientNoti.setText(message);
//         });
//      
//         return ; 
//      }

//      public String delimeterMaker(String... arguments) {
//         
//         
//         String result =""; 
//         return result;
//      }

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}
