package application;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
/***
 * ServerTask  입출력 스트림을 통해 client와 신호를 주고 받아 코드분석 메소드를 실행한다.
 * 
 * client는 코드를 필드로 가져야 한다. (final String) 
 * 
 * 
 * */
import user.dao.ClientDAO;
import user.dao.ClientDAOImpl;
import user.vo.ClientVO;

public class ServerController implements Initializable{
	
	@FXML private Button buttonStart;
	@FXML private TextArea serverNoti;
	
	//FXML 컨트롤 속성 바인딩 
	
	ClientDAO dao;
	Vector<ClientVO> clientsVoVector;
	Vector<ClientVO> loginAccountList;
	ExecutorService serverPool;
	// 서버스레드풀
	ServerSocket ss;
	//서버소켓
	
	Hashtable<String,PrintWriter> toLoginUser; // 확성기 대상자 

	//  서버 멀티룸 구현을 위한 유저리스트 (Vector) 
	
	// 리스트 형태로 user를 add할 수 있음 
	// ServerTask가 관장함  
//	Vector<Client> allUser;
//	//Client field == socket,  ServerController ,  멀티룸<Client.this> 정보 , DB  
//	//Client는 ClientController임 ! 
//	Vector<Client> waitingUser;
	Vector<Matched> rooms;
//	//  서버 멀티룸 구현을 위한 유저리스트 
//	
//	//유저 VO와  serverOutput맵
//	Hashtable<Socket, ClientVO> VOPool;
//	//유저 VO와  serverOutput맵 
	
	// 버튼으로 start호출 
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		buttonStart.setOnAction(e->{
			
			String btnText = buttonStart.getText();
			
			if(btnText.equals("START")) {
				startServer();
				new ClientDAOImpl();
				serverCurrent("데이타- 베-이스 연동 완료!");
				buttonStart.setText("STOP");
				
			}else {
				stopServer();
				buttonStart.setText("START");
			}
		});
		// _Start
		// 소켓, 스레드 풀 확보 
	}
	
	// -----------------------methods----------------------------------
	
	// Start Stop ServerNoti() = fx:id=serverNoti  
	  
	// 전체유저, 웨이팅유저, 대전방 목록 생성 & 스레드풀 생성 
		public void startServer() {
		serverPool = Executors.newFixedThreadPool(50);
		// 방 나누기 
		toLoginUser = new Hashtable<>();
		// 데이터베이스 
		dao = new ClientDAOImpl();	
		clientsVoVector = new Vector<>();
		loginAccountList = new LoginAccountsList(); // Vector<UserVo>
		rooms = new Vector<>();
//			allUser = new Vector<>();    
//			// 유저 들어옴 
//			waitingUser = new Vector<>(); 
//			// 유저 들어옴 
//			rooms = new Vector<Matched>(); 
//			// 유저가 들어온 방이 들어옴 Matched's field = Vector<Client>
			try {
				String ip = InetAddress.getLocalHost().toString();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			try {
				ss = new ServerSocket(7777); // Int형 
			} catch (IOException e) {
				serverCurrent("중복된 포트번호 입니다!");
				return;
			}
			
			// 스레드풀이 실행할 runnable runner 
			// 역할 ::  socket accept 후 받은 소켓과 server class 전달 
			//	 	   --> 필드 이용 (serverPool, ss)
			Runnable run = new Runnable() {
				@Override
				public void run() {
//				serverCurrent("serverPool에 run 전달 완료");
				serverCurrent("[서버 오픈]");
				try {
					serverCurrent(InetAddress.getLocalHost().toString());
				} catch (UnknownHostException e) {e.printStackTrace();}
					while(true) {
						try {
	//						serverCurrent("now accepting");
							Socket client = ss.accept();
							// 연결 완료됨 
							String nowAcceptedClient = client.getRemoteSocketAddress().toString();
							serverCurrent(nowAcceptedClient + " accepted");
																		// 유일 인스턴스 
							serverPool.submit(new ServerTask(client,ServerController.this));
																	//서버재원 활용 (rooms, Vector<user>)
						} catch (IOException e) {
							serverCurrent("서버 연결 실패...");
							stopServer();
							break;
						}// try-catch
					}// while 
				}
			};// == run 
			serverPool.submit(run);
		}// startServer()
	
		
//		Matched findUser( ClientVO loginAccount) {
//			for(Matched room : rooms) {
//				if(room.contains(loginAccount)) {
//					return room;
//				}
//			}
//			return null;
//		}	
		
	public void stopServer() {
		try {
			if(loginAccountList != null) {
				loginAccountList.clear();
			}
			
			if(ss != null && !ss.isClosed()) {
				ss.close();
			}
		
			if(serverPool != null && !serverPool.isShutdown()) {
				serverPool.shutdown();
				try {
					serverPool.awaitTermination(5, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} // 작업이 완료될 때까지 최대 5초간 대기합니다
	            serverPool.shutdownNow(); // 남아있는 작업을 중단시킵니다
			}
			
			serverCurrent("[서버 종료]");
			
			if(!ServerTask.client.isClosed()) {
				ServerTask.client.close();
			}
			
		} catch (IOException e) {}
	
	}
	
	// 문자열을 매개해 서버 중계 상황을 알려주는 메소드 
	public void serverCurrent(String text) {
		System.out.println("serverCurrent 실행");
		Platform.runLater(()->{
			serverNoti.appendText(text+"\n");
		});
	}
	
	public int howManyRoom() {
		return this.rooms.size();		
	}// end
	
	public void removeEmptyRoom() {
		for(Matched room : this.rooms) {
			if(room.size() == 0) { 
				this.rooms.remove(room);
			}
		}//for
	}// end
	
	
	
}
