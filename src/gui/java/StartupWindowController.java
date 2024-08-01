/**
 * 
 */
package gui.java;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import p2p.java.BattleshipApp;

/**
 * Controller for the startup window.
 */
public class StartupWindowController {
	@FXML
	private TextField portField, IPField, rowField, colField, shipLengthField, roundTimeField;
	
	@FXML
	private Text networkStatusText;
	
	@FXML
	private ListView<Integer> shipLengthList;
	private ObservableList<Integer> numbers;
	
	@FXML
	private Button connectToOpponent, waitForOpponent, settingsButton;
	
	@FXML
	private GridPane networkSettings;
	
	
//	private int rows, cols;
//	private int portNum;
//	private String opponentIP;
	
	@FXML
	private void initialize() {
//		updateDimensionFields();
//		updatePortNum();
//		updateIPAddress();
		numbers = FXCollections.observableArrayList();
//		shipLengthList = new ListView<>(numbers);
		shipLengthList.setItems(numbers);
	}
	
	@FXML
	private void toggleNetworkSettings() {
		networkSettings.setVisible(!networkSettings.isVisible());
	}
	
//	@FXML
//	private void updateDimensionFields() {
//		try {
//			int r = Integer.parseInt(rowField.getText());
//			int c = Integer.parseInt(colField.getText());
//			if(r < 1 || c < 1) {
//				throw new NumberFormatException();
//			}
//			rows = r;
//			cols = c;
//		}catch(NumberFormatException e) {
//			ErrorWindow.displayErrorWindow("Rows and columns must be positive integers greater than 0!");
//		}
//	}
	
	@FXML
	private void addShip() {
		try {
			int newShip = Integer.parseInt(shipLengthField.getText());
			if(newShip < 1) throw new NumberFormatException();
			numbers.add(newShip);
			numbers.sort(null);
			shipLengthList.setItems(numbers);
			shipLengthField.clear();
			
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Ship length must be a positive integer!");
		}
	}
	
//	@FXML
//	private void updatePortNum() {
//		try {
//			int port = Integer.parseInt(portField.getText());
//			if(port < 0 || port > 65536) throw new NumberFormatException();
//			portNum = port;
//		}catch(NumberFormatException e) {
//			ErrorWindow.displayErrorWindow("Port number must be an integer between 0 and 63356!");
//		}
//	}
//	
//	@FXML
//	private void updateIPAddress() {
//		opponentIP = IPField.getText();
//	}
	
	@FXML
	private void waitOpponent() {
		if(networkStatusText.isVisible()) return;
		if(this.numbers.size() == 0) {
			ErrorWindow.displayErrorWindow("Cannot start a game with no ships!");
			return;
		}
		int r, c, port, timer;
//		String opponentIP;
		try {
			r = Integer.parseInt(rowField.getText().strip());
			c = Integer.parseInt(colField.getText().strip());
			if(r < 1 || c < 1) {
				throw new NumberFormatException();
			}
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Rows and columns must be positive integers greater than 0!");
			return;
		}
		try {
			port = Integer.parseInt(portField.getText().strip());
			if(port < 0 || port > 65535) throw new NumberFormatException();
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Port number must be an integer between 0 and 63356!");
			return;
		}
		try {
			timer = Integer.parseInt(roundTimeField.getText().strip());
			if(timer < 10) throw new NumberFormatException();
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Time per round must be a positive integer, at least 10 seconds!");
			return;
		}
		networkStatusText.setVisible(true);
		establishConnection(true, port, "", r,c,timer);
	}
	

	@FXML
	private void connect() {
		if(networkStatusText.isVisible()) return;
		if(this.numbers.size() == 0) {
			ErrorWindow.displayErrorWindow("Cannot start a game with no ships!");
			return;
		}
		int r, c, port,timer;
		String opponentIP;
		try {
			r = Integer.parseInt(rowField.getText().strip());
			c = Integer.parseInt(colField.getText().strip());
			if(r < 1 || c < 1) {
				throw new NumberFormatException();
			}
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Rows and columns must be positive integers greater than 0!");
			return;
		}
		try {
			port = Integer.parseInt(portField.getText().strip());
			if(port < 0 || port > 65536) throw new NumberFormatException();
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Port number must be an integer between 0 and 63356!");
			return;
		}
		try {
			timer = Integer.parseInt(roundTimeField.getText().strip());
			if(timer < 10) throw new NumberFormatException();
		}catch(NumberFormatException e) {
			ErrorWindow.displayErrorWindow("Time per round must be a positive integer, at least 10 seconds!");
			return;
		}
		opponentIP = IPField.getText().strip();
		networkStatusText.setVisible(true);
		establishConnection(false, port, opponentIP, r,c,timer);
	}
	
	@FXML
	private void displayHelpWindow() {
		ErrorWindow.displayInfoWindow("Please consult the user manual in the manuals folder for\na detailed explanation of this program.");
	}
	
	
	private void establishConnection(boolean server, int portNum, String opponentIP, int rows, int cols, int timer) {
		BattleshipApp game = null;
		if(server) {
			game = new BattleshipApp(rows,cols,portNum, "");
		}else {
			game = new BattleshipApp(rows,cols,portNum, opponentIP);
		}
		try {
			game.connect();
			
		}catch(IOException e) {
			ErrorWindow.displayErrorWindow("Connecting to the peer failed:\n" + e.getMessage());
			this.networkStatusText.setVisible(false);
			return;
		}
		// connection established, time to change controllers
		try {
			if(!game.verifySameConfigs(timer, numbers)) {
				ErrorWindow.displayErrorWindow("Game configurations (rows, columns, timer, ships)\n do not coincide with opponent!");
				Stage currentWindow = (Stage) portField.getScene().getWindow();
	            currentWindow.close();
	            return;
			}
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/BattleshipGUI.fxml"));
            Parent root = loader.load();
            Stage newWindow = new Stage();
            newWindow.setTitle("Battleship");
            newWindow.setScene(new Scene(root));
            newWindow.show();
            BattleshipGameController c = (BattleshipGameController)loader.getController();
            c.configureGame(game, numbers ,timer,rows,cols);
            // Close the original window
            // Get the current window and close it
            Stage currentWindow = (Stage) portField.getScene().getWindow();
            currentWindow.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	
}
