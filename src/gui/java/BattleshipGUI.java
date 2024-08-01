package gui.java;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

/**
 * Main class for the battleship application.
 */
public class BattleshipGUI extends Application{
	
	
	/**
	 * Starts the program!
	 * @author lungua
	 * @since hw3
	 * @param arg0 Main window.
	 */
	@Override
	public void start(Stage arg0) {
		// TODO Auto-generated method stub
		try {
			Parent root = FXMLLoader.load(getClass().getResource("../resources/StartupWindow.fxml"));
			arg0.setTitle("Battleship");
			arg0.setScene(new Scene(root));
			arg0.show();
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Launches the program.
	 * @author lungua
	 * @since hw3
	 * @param args cmd line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
