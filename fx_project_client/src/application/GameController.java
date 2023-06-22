package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class GameController implements Initializable {

    @FXML ImageView black1,black2,black3, white1,white2,white3 , board, profile_img;
    @FXML Canvas guideLine;
    @FXML AnchorPane pane;
    @FXML TextArea roomChatArea;
    @FXML TextField roomChat;
    @FXML Button roomSend,exitBtn;

    // 돌들을 관리하기 위한 리스트
    List<ImageView> stones; 
    List<ImageView> blackStones;
    List<ImageView> whiteStones;
    
    String color;
    GraphicsContext gc;
    
    double x1, y1;
    double x2, y2;
    double divideX, divideY;
    boolean isRun = true;
    
    Client c = MainController.getClient();
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	
    	// 나가기 버튼 클릭시 항복 -> 패배처리, 대기실로 이동
    	exitBtn.setOnAction(e->{
    		c.toServer("giveUp",color);
    	});
    	
    	roomChat.setOnKeyPressed(e -> {
    		if(e.getCode() == KeyCode.ENTER) {
    			roomSend.fire(); 
    		}
    	});
    	
    	roomSend.setOnAction(e->{
    		String chat = roomChat.getText();
    		if(chat != null) {
    			c.toServer("roomChat",chat);
    		}
    		roomChat.clear();
    	});
    	
        gc = guideLine.getGraphicsContext2D();
        
        // 모든 돌들을 관리하기 위한 리스트
        stones = new ArrayList<>();
        stones.add(black1);
        stones.add(black2);
        stones.add(black3);
        stones.add(white1);
        stones.add(white2);
        stones.add(white3);
        
        // 검은돌 리스트
        blackStones = new ArrayList<>();
        blackStones.add(black1);
        blackStones.add(black2);
        blackStones.add(black3);
        
        // 흰돌 리스트
        whiteStones = new ArrayList<>();
        whiteStones.add(white1);
        whiteStones.add(white2);
        whiteStones.add(white3);
        
        for(ImageView stone : stones) {
        	// 돌 클릭시 클릭한 돌의 x,y좌표를 저장한다.
        	stone.setOnMousePressed(e -> {
                x1 = stone.getLayoutX();
                y1 = stone.getLayoutY();
                // while문 실행
                isRun = true;
            });
            
            // 마우스를 드래그 할때 돌의 예상 이동경로를 보여줌
        	// 게임의 재미를 위하여 삭제 ^^
        	stone.setOnMouseDragged(e -> {
        		/*
            	// 가이드 라인 그려주기
            	gc.strokeLine(
            		stone.getLayoutX() + 10,
                    stone.getLayoutY() + 10,
                   (stone.getLayoutX() + ((stone.getLayoutX() - e.getSceneX())) + 40),
                   (stone.getLayoutY() + ((stone.getLayoutY() - e.getSceneY())) + 40)
                );
            	// 마우스가 움직일때 캔버스를 초기화 시킴
            	stone.addEventHandler(MouseEvent.MOUSE_DRAGGED, e2->{
                	gc.clearRect(0, 0, guideLine.getWidth(), guideLine.getHeight());
                });
                */
        		
                // 돌의 도착 좌표를 저장한다. 
                x2 = stone.getLayoutX() - 10 + ((stone.getLayoutX() - e.getSceneX())) + 40;
                y2 = stone.getLayoutY() - 10 + ((stone.getLayoutY() - e.getSceneY())) + 40;
                // 돌의 이동을 표현, 돌의 충돌 여부를 확인하기 위해 50번 나눠서 이동
                divideX = (Math.abs(x1 - x2) / 50);
                divideY = (Math.abs(y1 - y2) / 50);
            });

            // 마우스를 놓았을때..
        	stone.setOnMouseReleased(e -> {
                // gc.clearRect(0, 0, guideLine.getWidth(), guideLine.getHeight());

            	// 서버에 턴이 끝났다고 알려줌
                c.toServer("turnEnd"," ");
                setTurn("니턴");
                
                Thread t = new Thread(() -> {
					while (isRun) {
                        	// 이동 시키는 방향 지정..
                            if (x1 > x2) {
                                x1 -= divideX;
                            } else if (x1 < x2) {
                                x1 += divideX;
                            }
                            
                            if (y1 > y2) {
                                y1 -= divideY;
                            } else if (y1 < y2) {
                                y1 += divideY;
                            }
                        	
                        	// 돌을 이동시킨다 돌의 위치를 계속해서 변경
                            Platform.runLater(()->{
                                stone.setLayoutX(x1);
                                stone.setLayoutY(y1);
                            });

                            // 서버로 black의 이동좌표를 보냄, 다른 클라이언트의 Scene에도 똑같이 black의 움직임을 수행 
                            c.toServer("move"+color, stone.getId() + "," +x1+","+y1);
                            			// code[0]    // code[1]
                        	
                        	// 바둑판의 좌표 범위를 벗어났을 경우를 체크
                            // 벗어날 경우 돌을 제거한다
                        	if(getOut(stone)) {
                        		removeStone(stone);
                        		isRun = false;
                        	}
                        	
                            // 모든 돌들과 충돌을 감지하면서 이동
                            for(ImageView another : stones) {
                                if(another != stone) {
                                	if(isColliding(stone, another)) {
                                		// 충돌하였을때도 이동시켜야겠지?
                                		moveStone(stone,another);
                                		isRun = false;
                                	}
                            	}
                        	}
                            // 도착예상 좌표에 도착 하였을경우 실행중지
                            if(moveEnd()) {
                        		isRun = false;
                            }
                            try {
        						Thread.sleep(5);
        					} catch (InterruptedException e1) {}
					} // while
                });
                t.setDaemon(true);
                t.start();
        	});
        }
    }
	                          
		// 바둑판의 범위를 벗어났을경우 체크 
    	// width 0~440
    	// height 0~460
    	private boolean getOut(ImageView stone) {
    		if(Math.round(stone.getLayoutX()) < 0 || Math.round(stone.getLayoutX()) > 430) {
    			return true;
    		} else if(Math.round(stone.getLayoutY()) < 0 || Math.round(stone.getLayoutY()) > 450) {
    			return true;
    		}
    		return false;
    	}
    
    
	    // 돌 제거 메소드
	    private void removeStone(ImageView stone) {
	    	
	        StringBuilder winner = new StringBuilder();
	        
	        Platform.runLater(()->{
	        	pane.getChildren().remove(stone);
	        });
	        
	        stones.remove(stone);
	        
	        // 돌이 제거 되고 모든 돌이 제거 되었을때 승자를 판별하여 서버에 전송한다.
	        if (whiteStones.contains(stone)) {
	            whiteStones.remove(stone);
	            if (whiteStones.isEmpty()) {
	                winner.append("Black");
	            }
	        } else {
	            blackStones.remove(stone);
	            if (blackStones.isEmpty()) {
	                winner.append("White");
	            }
	        }
	        
	        if (winner.length() > 0) {
	            String winColor = winner.toString();
	            if (color.equals(winColor)) {
	                c.toServer("win", " ");
	            } else {
	                c.toServer("lose", " ");
	            }
	        }
	    } // end remove
	
	    // 충돌 감지 메소드
	    public boolean isColliding(ImageView stone1, ImageView stone2) {
	        // 돌의 중심 좌표 구하기	
	    	double x1 = stone1.getLayoutX();
	        double y1 = stone1.getLayoutY();
	        
	        double x2 = stone2.getLayoutX();
	        double y2 = stone2.getLayoutY();
	    	
	        // 두 돌 사이의 거리 계산 (유클리드 거리)
	        double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	
	        // 거리 <= 두 돌의 반지름의 합 (10+10)
	        return distance <= 20;
	    }
	    
	    public void moveStone(ImageView hitt,ImageView hitStone) {
	    	// 도착지점 계산 (원래 예상 도착 경로/2) 
	    	double arriveX = (x2 - x1) / 100;
	        double arriveY = (y2 - y1) / 100;

	        String hit = hitStone.getId();
        	String target = hit.contains("black") ? "Black" : "White";

    		Thread t = new Thread(()->{
        		for(int i = 0; i < 50; i++) {
    		        double X = hitStone.getLayoutX();
    		        double Y = hitStone.getLayoutY();
    		        
    		        Platform.runLater(()->{
    		        	hitStone.setLayoutX(X+arriveX);
    			        hitStone.setLayoutY(Y+arriveY);
    		        });
    		        
    		        for (ImageView another : stones) {
    		            if (another != hitStone && another != hitt) {
    		                if (isColliding(hitStone, another)) {
    		                	moveStone(hitStone,another);
    		                	if(getOut(another)) {
    		                		removeStone(another);
    		                	}
    		                }
    		            }
    		        }
    		        try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    		        if(getOut(hitStone)) {
    		        	c.toServer("move" + target, hit + "," + hitStone.getLayoutX() + "," + hitStone.getLayoutY());
    		        	removeStone(hitStone);
    		        	break;
    		        }else {
    		        	c.toServer("move" + target, hit + "," + hitStone.getLayoutX() + "," + hitStone.getLayoutY());
    		        }
        		} // end for
    		});
    		t.setDaemon(true);
    		t.start();
    	}
	    
	    // 상대가 돌을 움직였을경우 서버로 부터 움직인 돌의 번호와 x,y좌표를 받아 똑같이 실행한다.
	    public void anotherMoveBlack(String stoneNum,double x, double y) {
    		for(ImageView stone : blackStones) {
    			if(stone.getId().equals(stoneNum)) {
					Platform.runLater(()->{
	    	    		stone.setLayoutX(x);
	    		    	stone.setLayoutY(y);
	    		    	if(getOut(stone)) {
	    					removeStone(stone);
	    					isRun = false;
	    				}
    				});
    			}
    		}
	    };

	    // 이하 동일
	    public void anotherMoveWhite(String stoneNum,double x, double y) {
    		for(ImageView stone : whiteStones) {
    			if(stone.getId().equals(stoneNum)) {
					Platform.runLater(()->{
	    	    		stone.setLayoutX(x);
	    		    	stone.setLayoutY(y);
						if(getOut(stone)) {
	    					removeStone(stone);
	    					isRun = false;
	    				}
					});
    			}
    		}
	    };
	    
    
	    public void printText(String message) {
 		   roomChatArea.appendText(message+"\n");
	    }
	    
	    // 내 돌만 조작가능하게 설정
	    public void setColor(String color) {
	    	this.color = color;
	    	if(color.equals("Black")) {
	    		white1.setDisable(true);
	    		white2.setDisable(true);
	    		white3.setDisable(true);
	    	}else {
	    		black1.setDisable(true);
	    		black2.setDisable(true);
	    		black3.setDisable(true);
	    	}
	    }
	    
	    // 나의 턴에만 조작가능
	    public void setTurn(String turn) {
	    	if(turn.equals("myTurn")) {
	    		pane.setDisable(false);
	    	}else {
	    		pane.setDisable(true);
	    	}
	    }
	    
	    public boolean moveEnd() {
	    	return Math.round(x1) == Math.round(x2) && Math.round(y1) == Math.round(y2);
	    }
	    
}// 






    
    





