/**
 * 
 */
package gui.java;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import p2p.java.BattleshipApp;
import p2p.java.BattleshipApp.Mode;
import p2p.java.BattleshipProtocol.BattleshipStates;

/**
 * Controller for the battleship game window
 */
public class BattleshipGameController {
	private BattleshipApp game;
	private int rows, cols;
	private boolean lockedIn, myTurn; // TODO might not need turnExecuted
	private Integer shootResult,receiveShotResult;
	private int myScore, enemyScore;
	
	private Pane paneToShoot;
	@FXML
	private ColorPicker shipColorPicker, hitColorPicker, missColorPicker;
	
	@FXML
	private GridPane grid,settings;
	
	@FXML
	private Button lockInButton, placeButton
;
	@FXML
	private Text myScoreText, enemyScoreText, timerText, turnText, turnResultText;
	
	private Timer timer;
	private int roundTime, currentTime;
	
	@FXML
	private TextField shipRowField, shipColField;
	
	private ObservableList<Integer> shipLengths, savedShipLengths;
	
	@FXML
	private ChoiceBox<Integer> shipChoice;
	
	@FXML
	private ChoiceBox<String> directionChoice;
	
	@FXML
	private void initialize() {
		lockedIn = false;
		shootResult = null;
		receiveShotResult = null;
//		game = null;
//		timer = null;
		this.savedShipLengths = FXCollections.observableArrayList();
		ObservableList<String> directions = FXCollections.observableArrayList("Vertical","Horizontal");
		directionChoice.setItems(directions);
		directionChoice.setValue("Vertical");
	}
	
	/**
	 * Initially display the ship grid
	 */
	private void displayShipGrid() {
		// completely clear gridpane
	    grid.getColumnConstraints().clear();
	    grid.getRowConstraints().clear();
	    grid.getChildren().clear();
	    // Set new column constraints
	    for (int i = 0; i < cols; i++) {
	    	ColumnConstraints constr = new ColumnConstraints();
	    	constr.setPercentWidth(100.0 / cols);
	    	grid.getColumnConstraints().add(constr);
	    }
	       
	    // Set new row constraints
	    for (int i = 0; i < rows; i++) {
	    	RowConstraints constr = new RowConstraints();
	    	constr.setPercentHeight(100.0 / rows);
	    	grid.getRowConstraints().add(constr);
	    }
		for(int i = 0; i < rows; ++i) {
	    	for(int j = 0; j < cols; ++j) {
	    		Pane p = new Pane();
	    		p.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
	    		p.setStyle("-fx-border-color: black; -fx-border-width: 0.25px;");
	        	grid.add(p, j, i);
	        }
	    }
		
		this.changeShipColor();
	}
	/**
	 * Once a connection is established, the BattleshipApp object from the startup window is transferred to 
	 * the battleship game window, along with the list of ships and length of each round. The game is then started.
	 * @author lungua
	 * @since hw3
	 * @param game This player's battleship game.
	 * @param shiplengths lengths of the ships in the game.
	 * @param roundTime allowed time for each round.
	 * @param rows number of rows for the game.
	 * @param cols number of columns for the game.
	 */
	public void configureGame(BattleshipApp game, ObservableList<Integer> shiplengths, int roundTime, int rows, int cols) {
		this.game = game;
		this.rows = rows;
		this.cols = cols;
		displayShipGrid();
		this.shipLengths = shiplengths;
		this.savedShipLengths.addAll(shiplengths); // for restarting the game
		this.shipChoice.setItems(shiplengths);
		this.shipChoice.setValue(shipLengths.getFirst());
		this.roundTime = roundTime;
		this.turnText.setVisible(true);
		this.myTurn = false;
		timerInit();

	}
	
	
	private void timerInit() {
		this.currentTime = this.roundTime;
		Runnable r = null;
		
		if(this.game.getTurn() == BattleshipStates.INIT) {
			// ready up based on if we locked in or not. If both players locked in then start a new round.
			r = () -> {
				
				if(lockedIn && this.shipLengths.size() == 0) {
					boolean someoneDidntLockIn = game.readyUp(true);
					if(someoneDidntLockIn) {
						displayGameOverWindow(true, "Enemy didn't place all ships down!");
					}
					else {
						startNewRound();
					}
				}
				else {
					game.readyUp(false);
					displayGameOverWindow(false, "You didn't place all ships down.");
				}
	            
			};
		}
		/**
		 * If it's not my turn to shoot and the timer runs out, I will receive a shot.
		 * If it's my turn to shoot and the timer runs out, I should have some square on 
		 * 	the grid selected for shooting. If no square is selected then I lose.
		 */
		else if(this.myTurn) {
			
			r = () -> {
				if(this.paneToShoot == null || !lockedIn) {
					// send a pass command to the opponent
					this.game.sendPassCommand();
					displayGameOverWindow(false, "You did not lock in in time.");
				}
				else {
					this.shootResult = this.game.shoot(this.game.getPlayer(), GridPane.getRowIndex(paneToShoot), GridPane.getColumnIndex(paneToShoot));
					startNewRound();
				}
				
	            
			};
		}
		else {
			r = () -> {
				receiveShotResult = this.game.receiveShot();
				if(receiveShotResult == 0) {
					// enemy passed
					displayGameOverWindow(true, "Enemy did not lock in in time!");
				}
				else {
					startNewRound();
				}
				
			};
		}
//		else if(this.game.getTurn() == BattleshipStates.PLAYER2) {
//			
//			r = () -> {
//				if(this.game.getMode() == Mode.SERVER) {
//					if(this.paneToShoot == null) {
//						// send a pass command to the opponent
//						this.game.sendPassCommand();
//					}
//					else {
//						this.lastShotResult = this.game.shoot(this.game.getPlayer(), GridPane.getRowIndex(paneToShoot), GridPane.getColumnIndex(paneToShoot));
//					}
//				}
//				else {
//					// time is up, receive from the enemy
//					this.game.receiveShot();
//				}
//	            
//			};
//		}
        
		if(r != null) {
			timer = new Timer();
			final Runnable rFinal = r;
			TimerTask task = new TimerTask() {
	        	public void run() {
	                currentTime--;
	                Platform.runLater(()->{timerText.setText("Time Remaining: " + Integer.toString(currentTime));});
	                if (currentTime <= 0) {
	                    timer.cancel();
	                    Platform.runLater(rFinal);
//	                    try {
//	                    	startNewRound();
//	                    }catch(IOException e) {
//	                    	ErrorWindow.displayErrorWindow("Failed to load the game over window.");
//	                    	Stage currentWindow = (Stage) grid.getScene().getWindow();
//	            	        currentWindow.close();
//	                    }
	                    
	                }
	            }
	        };
	        timer.scheduleAtFixedRate(task, 0, 1000);
		}
        
	}
	
	
	
	/**
	 * Update grid state if it's the opponent's turn to shoot.
	 */
	@FXML
	private void changeShipColor() {
		for(Node child : grid.getChildren()) {
			Pane p = (Pane) child;
			final int i = GridPane.getRowIndex(p);
			final int j = GridPane.getColumnIndex(p);
			// if not my turn to shoot then display the ships
			if(!this.myTurn) {
				if(this.game.shipData(i, j) == 0) {
					if(p != this.paneToShoot) {
						p.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
					}
				}
				else if(this.game.shipData(i, j) == 1) {
					p.setBackground(new Background(new BackgroundFill(shipColorPicker.getValue(), null, null)));
				}
				else {
					p.setBackground(new Background(new BackgroundFill(hitColorPicker.getValue(), null, null)));
				}
			}
			
			p.setOnMouseClicked(event -> {
				if(this.game.getTurn() == BattleshipStates.INIT) {
					if(this.game.shipData(i, j) == 0) {
						placeShipCoords(i,j);
					}
					else {
						removeShip(i,j);
					}
					
				}else {
					if(this.myTurn && this.game.hitGridEntry(i, j) == 0) {
						p.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
						if(this.paneToShoot!= null && this.paneToShoot != p) {
							// set previous pane to its old value
							this.paneToShoot.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
						}
						this.paneToShoot = p;
					}
				}
			});
		}
	}
	
	private void removeShip(int i, int j) {
		int length = this.game.removeShip(i, j);
		this.changeShipColor();
		this.shipLengths.add(length);
		this.shipLengths.sort(null);
		this.shipChoice.setItems(shipLengths);
		this.shipChoice.setValue(shipLengths.getFirst());
		
	}

	@FXML
	private void changeHitColor() {
		// if we are getting shot now, call changeShipColor
		if(this.myTurn) {
			changeMissColor();
		}
		else {
			changeShipColor();
		}
		
	}
	
	/**
	 * Update grid state if it's your turn to shoot.
	 */
	@FXML
	private void changeMissColor() {
		for(Node child : grid.getChildren()) {
			Pane p = (Pane) child;
			final int i = GridPane.getRowIndex(p);
			final int j = GridPane.getColumnIndex(p);
			// if it is my turn to shoot then display the hit grid.
			if(this.myTurn) {
				if(this.game.hitGridEntry(i, j) == 0) {
					// no info
					if(p != this.paneToShoot) {
						p.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
					}
				}
				else if(this.game.hitGridEntry(i, j) == -1) {
					// miss
					p.setBackground(new Background(new BackgroundFill(missColorPicker.getValue(), null, null)));
				}
				else{
					// hit
					p.setBackground(new Background(new BackgroundFill(hitColorPicker.getValue(), null, null)));
				}
			}
			
			p.setOnMouseClicked(event -> {
				// if it is our turn to shoot then shoot wherever we clicked, if it hasnt been shot already
				if(this.game.getTurn() == BattleshipStates.INIT) {
					placeShipCoords(i,j);
				}else {
					if(this.myTurn && this.game.hitGridEntry(i, j) == 0) {
						p.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
						if(this.paneToShoot!= null && this.paneToShoot != p) {
							// set previous pane to its old value
							this.paneToShoot.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
						}
						this.paneToShoot = p;
					}
				}			   
				   
			});
		}
	}
	
	@FXML
	// place ship based on menu options
	private void placeShip(){
		int r, c;
		try {
			// place a ship down and update ship choices
			r = Integer.parseInt(this.shipRowField.getText());
			c = Integer.parseInt(this.shipColField.getText());
			if(r < 0 || r >= this.rows || c < 0 || c >= this.cols)
				throw new NumberFormatException();
			if(this.game.shipData(r,c) != 0 ) {
//				System.out.println("Ship already exists there!");
				return;
			}
			this.placeShipCoords(r,c);
//			l = shipChoice.getValue();
//			if(!game.placeShip(l, r, c, directionChoice.getValue())) return;
//			shipLengths.remove(shipLengths.indexOf(l));
//			
//			shipChoice.setItems(shipLengths);
//			if(shipLengths.size() != 0) {
//				shipChoice.setValue(shipLengths.getFirst());
//			}
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Row and column numbers be integers which\ndo not exceed grid dimensions!");
			return;
		}
		catch(NoSuchElementException | IndexOutOfBoundsException e) {
			return;
		}
		
	}
	
	/**
	 * Place down the first ship in the list of ships.
	 */
	private void placeShipCoords(int i, int j) {
		try{
			if(this.game.shipData(i, j) != 0 ) return;
			Integer shipLength = shipChoice.getValue();
			if(shipLength == null) {
				
				return;
			}
			if(!game.placeShip(shipLength, i, j, directionChoice.getValue().toLowerCase())) {
				return;
			}
			
			shipLengths.remove(shipLength);
			
			shipChoice.setItems(shipLengths);
			if(shipLengths.size() != 0) {
				shipChoice.setValue(shipLengths.getFirst());
			}
			
			String direction = directionChoice.getValue().toLowerCase();
			game.placeShip(shipLength, i, j, direction);
			changeShipColor();
		}catch(NoSuchElementException e) {
			return;
		}
	}
	
	/**
	 * Locks in this player's move (ship placement, choice of shot)
	 */
	@FXML
	private void lockIn() throws IOException{
		if(this.game.getTurn() == BattleshipStates.INIT) {
			if(this.shipLengths.size() != 0) {
				ErrorWindow.displayErrorWindow("You must place all your ships down!");
				return;
			}
			lockedIn = true;
//			System.out.println("lockedin: " + lockedIn);
			
			this.turnText.setText("Board state saved!");
		}
		else {
			// save the choice of where to shoot for this player
			if(myTurn) {
				if(this.paneToShoot == null) {
					ErrorWindow.displayErrorWindow("You must select a square on the grid to shoot!");
					return;
				}
				lockedIn = true;
//				System.out.println("lockedin: " + lockedIn);
				int r = GridPane.getRowIndex(paneToShoot);
				int c = GridPane.getColumnIndex(paneToShoot);
				this.turnText.setText(String.format("Shot %d, %d locked in!", r,c));
			}
//			this.currentTime = 1;
			
		}
	}
		
	
	/**
	 * Start a new round in the game.
	 */
	private void startNewRound(){
		// set myturn = true if it's this player's turn to shoot
//		System.out.println("this.game.getTurn(): " + this.game.getTurn());
		this.myTurn = (this.game.getTurn() == BattleshipStates.PLAYER1 && this.game.getMode() == Mode.SERVER)
				|| (this.game.getTurn() == BattleshipStates.PLAYER2 && this.game.getMode() == Mode.CLIENT);
		this.placeButton.setVisible(false);
		this.lockedIn = false;
		this.paneToShoot = null;
		if(this.game.getTurn() == BattleshipStates.VOTING) {
			// I win
			if(this.game.winner()) {
				this.myScore++;
				this.myScoreText.setText("Your score: " + myScore);
				displayGameOverWindow(true, "You sunk all the enemy's ships!");
				
			}
			// I did not win
			else {
				
				this.enemyScore++;
				this.enemyScoreText.setText("Enemy score: " + enemyScore);
				displayGameOverWindow(false, "The enemy sunk all your ships!");
			
				
			}
			return;
		}
//		System.out.println("myTurn :" + myTurn);
		timerInit();
		if(myTurn) {
			// i need to shoot enemy
			this.lockInButton.setVisible(true);
			this.turnText.setText("Click on the grid to fire a shot!");
			// display your hits
			this.changeHitColor();
			if(receiveShotResult != null) {
				this.turnResultText.setVisible(true);
				String newText = "";
				if(receiveShotResult == -1) {
					newText ="None of your ships were hit!";
				}
				else if(receiveShotResult == 1) {
					newText = "One of your ships was hit!";
				}
				else if(receiveShotResult == 2) {
					newText = "One of your ships was sunk!";
				}
				this.turnResultText.setText(newText);
//				receiveShotResult = null; // TODO: may be a problem
			}
		}
		else {
			// wait for opponent to shoot
			this.lockInButton.setVisible(false);
			this.turnResultText.setVisible(false);
			if(shootResult != null) {
				String newText = "Your shot ";
				if(shootResult == -1) {
					newText += "missed!";
				}
				else if(shootResult == 1) {
					newText += "hit an enemy ship!";
				}
				else if(shootResult == 2) {
					newText += "sunk an enemy ship!";
				}
				this.turnText.setText(newText);
			}else {
				this.turnText.setText("Waiting for opponent...");
			}
			// display your ships
			this.changeShipColor();
		}
		
		// after this round is done, move on to the next round
//		startNewRound();
	}

	@FXML
	private void toggleSettings() {
		settings.setVisible(!settings.isVisible());
	}
	
	/**
	 * Have this player vote to restart the game or not.
	 * @author lungua
	 * @since hw3
	 * @param restart true if this player wants to restart the game, false otherwise.
	 */
	public void executeVote(boolean restart) {
		boolean result = this.game.vote(restart);
		if(result) {
			// restart the game
			this.game.reset();
			this.displayShipGrid();
			this.lockedIn = false;
			this.shipLengths.clear();
			this.shipLengths  =FXCollections.observableArrayList(savedShipLengths);
			this.shipChoice.setItems(shipLengths);
			this.shipChoice.setValue(shipLengths.getFirst());
			this.turnText.setText("Place down your ships!");
			this.turnResultText.setVisible(false);
			this.lockInButton.setVisible(true);
			this.shootResult = null;
			this.receiveShotResult = null;
			this.placeButton.setVisible(true);

			this.timerInit();
		}
		else {
			Stage currentWindow = (Stage) grid.getScene().getWindow();
	        currentWindow.close();
		}
	}
	
	@FXML
	private void displayHelpWindow() {
		ErrorWindow.displayInfoWindow("Please consult the user manual in the manuals folder for\na detailed explanation of this program.");
	}
	
	private void displayGameOverWindow(boolean win, String message) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/GameOverWindow.fxml"));
		try {
	        Parent root = loader.load();
	
	        Stage newWindow = new Stage();
	        newWindow.setTitle("Game Over!");
	        newWindow.setScene(new Scene(root));
	        newWindow.show();
	        GameOverWindow w = (GameOverWindow)loader.getController();
	        w.connectGame(this,win,message);
		}catch(IOException e) {
			ErrorWindow.displayErrorWindow("Failed to load the game over window.");
        	Stage currentWindow = (Stage) grid.getScene().getWindow();
	        currentWindow.close();
		}
	}
}
