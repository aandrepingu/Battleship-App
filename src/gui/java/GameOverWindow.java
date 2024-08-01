package gui.java;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Displays the game over window and allows each player to vote on restarting or not.
 * @author lungua
 * @since hw3
 */
public class GameOverWindow {
	@FXML
	private Text winLoseText, waitText, reasonText;
	
	@FXML
	private Button yesButton, newButton;
	
	private BattleshipGameController gameWindow;
	
	/**
	 * Connects the battleship game window to the game over window.
	 * @author lungua
	 * @since hw3
	 * @param b battleship game window.
	 * @param win true if the player displaying this window won, false otherwise.
	 * @param message message for the player stating why they win/lost.
	 */
	public void connectGame(BattleshipGameController b, boolean win, String message) {
		this.gameWindow = b;
		if(win) {
			winLoseText.setText("You win!");
		}else {
			winLoseText.setText("You lose!");
		}
		this.reasonText.setText(message);
	}
	
	@FXML
	private void voteYes() {
		gameWindow.executeVote(true);
		waitText.setVisible(true);
		Stage currentWindow = (Stage) winLoseText.getScene().getWindow();
        currentWindow.close();
	}
	
	@FXML
	private void voteNo() {
		gameWindow.executeVote(false);
		waitText.setVisible(true);
		Stage currentWindow = (Stage) winLoseText.getScene().getWindow();
        currentWindow.close();
	}
	@FXML
	private void displayHelpDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Help");
        alert.setContentText("Please consult the user manual in the manuals folder for\na detailed explanation of this program.");
        alert.showAndWait();
	}
}
