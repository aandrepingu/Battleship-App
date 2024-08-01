/**
 * 
 */
package p2p.java;

import game.java.BattleshipPlayer.BattleshipWinner;
import game.java.BattleshipPlayer;


/**
 * Network protocol for the Battleship game. Defines different commands in certain formats.
 * @author lungua
 * @since hw3
 */
public class BattleshipProtocol {
	/**
	 * Protocol states that dictate whose turn it is in the battleship game, or if the game is over.
	 * @author lungua
	 * @since hw3
	 */
	public enum BattleshipStates{
		/**
		 * Initial state, placing down ships
		 */
		INIT, 
		/**
		 * Player1's turn to shoot
		 */
		PLAYER1, 
		/**
		 * Player2's turn to shoot
		 */
		PLAYER2, 
		/**
		 * Game over, voting to restart
		 */
		VOTING, 
		/**
		 * Game is fully over
		 */
		FINISHED};
	private BattleshipStates state;
	
	// player2 connects to player1.
	private BattleshipPlayer game;
	/**
	 * Constructs a protocol object in the initial state, ready to accept commands as if players are just
	 * setting up the game
	 * @author lungua
	 * @since hw3
	 * @param game Battleship game which has been created for both players.
	 */
	public BattleshipProtocol(BattleshipPlayer game) {
		this.state = BattleshipStates.INIT;
		this.game = game;
	}
	
	/**
	 * Returns the current game state as dictated by the protocol.
	 * @author lungua
	 * @since hw3
	 * @return current game state.
	 */
	public BattleshipStates getState() {
		return this.state;
	}
	
	/**
	 * Process multiple protocol commands via a single string of underscore-delimited protocol commands.
	 * @author lungua
	 * @since hw3
	 * @param commands String containing multiple protocol commands delimited by underscore characters (_)
	 * @return the responses of each protocol command in commands, also delimited by underscores.
	 */
	public String processMultiple(String commands) {
		String result = "";
		String[] commandsSplit = commands.split("_");
		for(int i = 0; i < commandsSplit.length - 1; ++i) {
			String temp = this.process(commandsSplit[i]);
			result += temp + "_";
		}
		String temp = this.process(commandsSplit[commandsSplit.length-1]);
		result += temp;
		return result;
	}
	/**
	 * Process a command and execute its effects on player1 and player2's battleship games.
	 * @author lungua
	 * @since hw3
	 * @param command Command to execute.
	 * @return a response string containing information about if the command is correct and was properly executed or not. 
	 */
	public String process(String command) {
		
		String response = "";
		String[] commands = parseCommand(command.toLowerCase());
		/**
		 * Commands in INIT state: 
		 * <player> ready
		 * 
		 * Commands in PLAYER1 state: 
		 * player1 shoot <row> <col>
		 * player1 update <row> <col> <data>
		 * player1 pass
		 * 
		 * Commands in PLAYER2 state:
		 * player2 shoot <row> <col>
		 * player2 update <row> <col> <data>
		 * player2 pass
		 * 
		 * Commands in FINISHED state:
		 * player1 restart <yes or no>
		 * player2 restart <yes or no>
		 */
		switch(state) {
		case BattleshipStates.INIT:
//			if(commands[1].equals("place-ship")) {
//				int l,r,c;
//				Direction d;
//				try {
//					if(commands.length != 6) throw new NumberFormatException();
//					l = Integer.parseInt(commands[2]);
//					r = Integer.parseInt(commands[3]);
//					c = Integer.parseInt(commands[4]);
//					if(commands[5].equals("vertical")) d = Direction.VERTICAL;
//					else if(commands[5].equals("horizontal")) d = Direction.HORIZONTAL;
//					else throw new NumberFormatException();
//					
//					// check who should place this ship down
//					if(commands[0].equals("player1") || commands[0].equals("player2")) {
//						boolean result = game.placeShip(commands[0], l, r, c, d);
//						response = (result) ? commands[0] + " place-ship OK" : "place-ship failed";
//						return response;
//					}
//					else {
//						throw new NumberFormatException();
//					}
//				}catch(Exception e) {
//					response = "place-ship invalid-arguments";
//					return response;
//				}
//				
//			}
//			else if(commands[1].equals("move-ship")) {
//				int rOld, cOld, r, c;
//				try {
//					if(commands.length != 6) throw new NumberFormatException();
//					rOld = Integer.parseInt(commands[2]);
//					cOld = Integer.parseInt(commands[3]);
//					r = Integer.parseInt(commands[4]);
//					c = Integer.parseInt(commands[5]);
//					
//					
//					// check player
//					if(commands[0].equals("player1") || commands[0].equals("player2")) {
//						boolean result = game.moveShip(commands[0], rOld, cOld, r, c);
//						response = (result) ? commands[0] + " move-ship OK" : "move-ship failed";
//						return response;
//					}
//					else {
//						throw new NumberFormatException();
//					}
//				}catch(Exception e) {
//					response = "move-ship invalid-arguments";
//					return response;
//				}
//				
//				
//			}
//			else if(commands[1].equals("remove-ship")) {
//				int r, c;
//				try {
//					if(commands.length != 4) throw new NumberFormatException();
//					r = Integer.parseInt(commands[2]);
//					c = Integer.parseInt(commands[3]);
//					
//					
//					// check player
//				
//					if(commands[0].equals("player1") || commands[0].equals("player2")) {
//						boolean result = game.removeShip(commands[0], r,c);
//						response = (result) ? commands[0] + " remove-ship OK" : "remove-ship failed";
//						return response;
//					}
//					else {
//						throw new NumberFormatException();
//					}
//				}catch(Exception e) {
//					response = "remove-ship invalid-arguments";
//					return response;
//				}
//				
//			}
			return processInit(command, commands);
		case BattleshipStates.PLAYER1:
			return processP1(command, commands);
			
		case BattleshipStates.PLAYER2:
			return processP2(command, commands);
		case BattleshipStates.VOTING:
			return processVoting(command, commands);
		case BattleshipStates.FINISHED:
			response = "" + command + " not-valid-for-game-state " + this.state.toString();	
			return response;
		}
		assert !response.isEmpty();
		return response;
	}
	
	private String processInit(String command, String[] commands) {
		String response;
		if(commands[1].equals("ready") && (commands[0].equals("player1") || commands[0].equals("player2"))) {
			this.game.ready(commands[0]);
			if(this.game.isReady() && this.game.oppReady()) {
				this.state = BattleshipStates.PLAYER1;
				response = commands[0] + " ready game-start";
			}
			else {
				response = commands[0] + " ready OK";
			}
			return response;
		}
		else if(commands[1].equals("pass") && (commands[0].equals("player1") || commands[0].equals("player2"))) {
			// player just won't ready up
			// change state accordingly to the voting stage
			this.state = BattleshipStates.VOTING;
			return commands[0] + " pass OK";
		}
		else {
			return "" + command + " not-valid-for-game-state " + this.state.toString();	
		}
	}

	private String processP1(String command, String[] commands) {
		String response;
		if(commands[1].equals("shoot")) {
			try {
				// player1 shoot r c
				// player 1 is shooting at player2, so player2 should be calling receiveShot
				if(commands.length != 4) {
					throw new Exception();
				}
				// if player2 is not processing this command, we don't care about the output
				if(!commands[0].equals("player1")) {
					response = "shoot wrong-player";
					return response;
				}
				// player2 should be handling this 
				if(!game.getPlayer().equals("player2")) {
					response = this.game.getPlayer() + " says player1 shoot OK";
					return response;
				}
				
				int r = Integer.parseInt(commands[2]);
				int c = Integer.parseInt(commands[3]);
				
				int result = game.receiveShot(r,c);
				if(result == -1) {
					response = "player1 shoot OK no-hit";
				}
				else if(result == 1) {
					response = "player1 shoot OK hit";
				}
				else {
					response = "player1 shoot OK sunk";
				}					
				
				
				// see if player1 just won
				if(game.getState() == BattleshipWinner.I_LOSE) {
					response = "player1 shoot OK win";
					this.state = BattleshipStates.VOTING;
				}
				else {
					this.state = BattleshipStates.PLAYER2;
				}
				return response;
			}catch(Exception e) {
				response = "shoot invalid-arguments";
				return response;
			}
		}
		else if(commands[1].equals("pass") && (commands[0].equals("player1") || commands[0].equals("player2"))) {
			// player just won't ready up
			// change state accordingly to the voting stage
			this.state = BattleshipStates.VOTING;
			return commands[0] + " pass OK";
		}
		else {
			response = "" + command + " not-valid-for-game-state " + this.state.toString();	
		}
		return response;
	}
	
	private String processP2(String command, String[] commands) {
		String response;
		if(commands[1].equals("shoot")) {
			try {
				if(commands.length != 4) {
					throw new Exception();
				}
				if(!commands[0].equals("player2")) {
					response = "shoot wrong-player";
					return response;
				}
				// player1 should be handling this 
				if(!game.getPlayer().equals("player1")) {
					response = this.game.getPlayer() + " says player2 shoot OK";
					return response;
				}
				int r = Integer.parseInt(commands[2]);
				int c = Integer.parseInt(commands[3]);
				
				int result = game.receiveShot(r, c);
				if(result == -1) {
					response = "player2 shoot OK no-hit";
				}
				else if(result == 1) {
					response = "player2 shoot OK hit";
				}
				else {
					response = "player2 shoot OK sunk";
				}
				
				// see if player2 just won
				if(game.getState() == BattleshipWinner.I_LOSE) {
					response = "player2 shoot OK win";
					this.state = BattleshipStates.VOTING;
				}
				else {
					this.state = BattleshipStates.PLAYER1;
				}
				return response;
			}catch(Exception e) {
				response = "shoot invalid-arguments";
				return response;
			}
		}		
		else if(commands[1].equals("pass") && (commands[0].equals("player1") || commands[0].equals("player2"))) {
			// player just won't ready up
			// change state accordingly to the voting stage
			this.state = BattleshipStates.VOTING;
			return commands[0] + " pass OK";
		}
		else {
			response = "" + command + " not-valid-for-game-state " + this.state.toString();	
			
		}
		return response;
	}
	
	private String processVoting(String command, String[] commands) {
		String response;
		if(commands[1].equals("vote")) {
			try {
				String source = "";
				if(commands.length != 3) {
					throw new Exception();
				}
				if(commands[0].equals("player1")) {
					source = "player1";
					if(commands[2].equals("yes")) {
						game.voteToRestart("player1", true);
					}
					else if(commands[2].equals("no")) {
						game.voteToRestart("player1", false);
					}
					else {
						throw new Exception();
					}
				}
				else if(commands[0].equals("player2")) {
					source = "player2";
					if(commands[2].equals("yes")) {
						game.voteToRestart("player2", true);
					}
					else if(commands[2].equals("no")) {
						game.voteToRestart("player2", false);
					}
					else {
						throw new Exception();
					}
				}
				else {
					throw new Exception();
				}
				response = source + " vote OK " + commands[2];
				
				// determine if we restart the game
//				if(game.getP1restart() != null && game.getP2restart() != null) {
				if(game.getRestart() != null && game.getOppRestart() != null) {
					if(game.getRestart().booleanValue() && game.getOppRestart().booleanValue()) {
						response = source + " restart OK restarting";
						this.state = BattleshipStates.INIT;
						game.clear();
					}
					else {
						this.state = BattleshipStates.FINISHED;
					}
				}
				return response;
			}catch(Exception e) {
				response = "restart invalid-arguments";
				return response;
			}
		}
		else if(commands[1].equals("pass") && (commands[0].equals("player1") || commands[0].equals("player2"))) {
			// player just won't ready up
			// change state accordingly to the voting stage
			this.state = BattleshipStates.VOTING;
			return commands[0] + " pass OK";
		}
		else {
			response = "" + command + " not-valid-for-game-state " + this.state.toString();	
			return response;
		}

	}
	

	private String[] parseCommand(String command) {
		return command.split("\\s+");
	}
	
	/**
	 * Change the state of the game from the protocol side, given a valid choice of state.
	 * @author lungua
	 * @since hw3
	 * @param s desired state of the game, either init, player1, player2, voting, or finished
	 */
	public void setState(String s) {
		if(s.equals("init")) this.state = BattleshipStates.INIT;
		if(s.equals("player1")) this.state = BattleshipStates.PLAYER1;
		if(s.equals("player2"))this.state = BattleshipStates.PLAYER2;
		if(s.equals("voting"))this.state = BattleshipStates.VOTING;
		if(s.equals("finished"))this.state = BattleshipStates.FINISHED;
	}
}
