/**
 * 
 */
package p2p.java;

import game.java.BattleshipPlayer;
import game.java.BattleshipPlayer.BattleshipWinner;
import game.java.Ship.Direction;
import p2p.java.BattleshipProtocol.BattleshipStates;

import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.io.*;

/**
 * Class that manages the network connection and game actions of a player in the battleship game. A BattleshipApp resides in 
 * each player's GUI application and is responsible for updating the state of the game for both players.
 * @author lungua
 * @since hw3
 */
public class BattleshipApp {
	/**
	 * Connection mode for this player, either client (this player connects to an opponent), or server (opponent is connecting to this player)
	 */
	public enum Mode {
		/**
		 * Client, i.e. connecting to opponent
		 */
		CLIENT, 
		/**
		 * Server, i.e. opponent is connecting to you
		 */
		SERVER };
	private BattleshipPlayer game;
	private BattleshipProtocol prot;
	String localCommand, localResponse, remoteResponse;
	private Connectable peer;
//	private boolean player1;
	private Logger log;
//	private StreamHandler handler;
	private String player;
	private Mode mode;
	
	/**
	 * Initializes this BattleshipApp to a new game with specified dimensions, and establishes the connection based on port number,
	 * and IP address if specified.
	 * @author lungua
	 * @since hw3
	 * @param rows number of rows for the game.
	 * @param cols number of columns for the game.
	 * @param portNum port number.
	 * @param peerIP should contain the enemy's IP address if this player is choosing to connect to an opponent, 
	 * 		  but can be null or empty if this player is choosing to wait for an opponent to join them.
	 */
	public BattleshipApp(int rows, int cols, int portNum, String peerIP) {
		if(peerIP == null || peerIP.equals("") ) {
			this.mode = Mode.SERVER;
			this.player = "player1";
		}
		else {
			this.mode = Mode.CLIENT;
			this.player = "player2";
		}
		this.log = Logger.getLogger("BattleshipApp");
		StreamHandler handler;
		try {
			handler = new FileHandler();
		}
		catch (Exception e) {
			log.warning(String.format("Unable to create global logger file handler: %s", e.getMessage()));
			return;
		} 
		handler.setFormatter(new SimpleFormatter());
		log.addHandler(handler);
		
		if (mode == Mode.CLIENT) {
			if (portNum != USE_DEFAULT_PORT) {
				this.peer = new BattleshipClient(peerIP, portNum);
			}
			else {
				this.peer = new BattleshipClient(peerIP);
			}
		}
		else {
			try {
				if (portNum != USE_DEFAULT_PORT) {
					peer = new BattleshipServer(portNum);
				}
				else {
					peer = new BattleshipServer();
				}
			}
			catch (IOException e) {
				log.severe(String.format("Unable to create server socket: %s", e.getMessage()));
				return;
			}
		}
		

		
		this.game = new BattleshipPlayer(rows,cols, this.player);
		this.prot = new BattleshipProtocol(game);
	}
	
	/**
	 * Attempt connection to this BattleshipApp's peer.
	 * @author lungua
	 * @since hw3
	 * @throws IOException if connecting to the peer fails.
	 */
	public void connect() throws IOException{
		
		peer.connect();
	
		
//		catch (IOException e) {
//			log.severe(String.format("Connecting to the peer failed: %s", e.getMessage()));
//		}
	}
	
	/**
	 * Asserts that this player and their peer have the same grid dimensions, timer, and ships
	 * after connection is established.
	 * @author lungua
	 * @since hw3
	 * @param timer this player's timer which will be checked against the other player's.
	 * @param ships this player's list of ships which will be checked against the other player's.
	 * @return true if both players have the same configurations, false otherwise.
	 */
	public boolean verifySameConfigs(int timer, List<Integer> ships) {
		String verifyToken = "ROWS:"+Integer.toString(this.game.getRows())+"COLS:"+Integer.toString(this.game.getCols())+"_";
		verifyToken += Integer.toString(timer) + "_";
		for(Integer ship : ships) verifyToken += Integer.toString(ship) + "_"; 
		// client must follow what the server wants for the game
		boolean result = true;
		if(this.mode == Mode.SERVER) {
			peer.send(verifyToken); 
		}
		else {
			localResponse = peer.receive();
			if(!localResponse.equals(verifyToken)) {
				result = false;
			}
		}
		
		if(this.mode == Mode.CLIENT) {
			if(!result) {
				peer.send("Settings bad"); 
			}
			else {
				peer.send("Settings good");
			}
				
		}
		else {
			localResponse = peer.receive();
			if(localResponse.equals("Settings bad")) {
				result = false;
			}

		}
		return result;
	}
	
	/**
	 * Return the mode of this battleship app, either client or server.
	 * @author lungua
	 * @since hw3
	 * @return this client's mode.
	 */
	public Mode getMode() {
		return this.mode;
	}
	
	/**
	 * Return the current state of the battleship game; init, player1's turn, player2's turn, or finished.
	 * @author lungua
	 * @since hw3
	 * @return Current state of the battleship game.
	 */
	public BattleshipStates getTurn() {
		return this.prot.getState();
	}
	
	/**
	 * Return this player's player designation string (either player1 if this player is the server or player2 if this player is the client)
	 * @author lungua
	 * @since hw3
	 * @return player1 if this player is the server or player2 if this player is the client
	 */
	public String getPlayer() {
		return this.player;
	}
	
	
	
	/**
	 * Execute a shoot command for one of the players, based on the protocol state. Updates the state of the
	 * game based on if the shot won the player the game, and updates protocol state based on the result of the shot.
	 * @author lungua
	 * @since hw3
	 * @param player player who is shooting, either player1 or player2
	 * @param r row index to shoot at
	 * @param c column index to shoot at
	 * @return -1 if this shot didn't hit any enemy ship,<br>
	 * 			1 if it hit an enemy ship but didn't sink it,<br>
	 * 			2 if it hit an enemy ship and sank it,<br>
	 * 			3 if it hit an enemy ship, sank it, and resulted in you winning the game.
	 */
	public int shoot(String player, int r, int c) {
		String protocolCommand = player + " shoot " + Integer.toString(r) + " " + Integer.toString(c);
		// If it's your turn to shoot then send the shoot command.
		int result = 0;
		if((this.prot.getState() == BattleshipStates.PLAYER1 && this.mode == Mode.SERVER) || 
				(this.prot.getState() == BattleshipStates.PLAYER2 && this.mode == Mode.CLIENT)) {
			
			remoteResponse = sendRecv(this.peer, this.prot, protocolCommand); 
			if(remoteResponse.contains("no-hit")) {
				this.game.updateHitGrid(r,c, -1);
//				log.info("Shoot: result has been set to -1");
				result = -1;
			}
			else if(remoteResponse.contains("hit")) {
				this.game.updateHitGrid(r, c, 1);
//				log.info("Shoot: result has been set to 1");
				result = 1;
			}
			else if(remoteResponse.contains("sunk")) {
				this.game.updateHitGrid(r,c, 2);
//				log.info("Shoot: result has been set to 2");
				result = 2;
				
			}
			else if(remoteResponse.contains("win")) {
				this.game.updateHitGrid(r, c, 2);
				this.game.notifyWin();
				this.prot.setState("voting");
//				log.info("Shoot: result has been set to 3");
				result = 3;
			}
			if(this.prot.getState() == BattleshipStates.PLAYER1) {
				this.prot.setState("player2");
			}
			else if (this.prot.getState() == BattleshipStates.PLAYER2) {
				this.prot.setState("player1");
			}
		}
		return result;
	}
	
	/**
	 * Receive and process a shoot command from the enemy via network.
	 * @author lungua
	 * @since hw3
	 * @return -1 if the received shot didn't hit any ships,<br>
	 * 			1 if the received shot hit a ship but didn't sink it,<br>
	 * 			2 if the received shot hit a ship and sunk it (including game-winning shots)<br>
	 * 		    0 if the enemy passed this turn.
	 */
	public int receiveShot() {
		// server is player1, client is player2
		if((this.prot.getState() == BattleshipStates.PLAYER1 && this.mode == Mode.CLIENT) 
			|| (this.prot.getState() == BattleshipStates.PLAYER2 && this.mode == Mode.SERVER)) {
			// send the shoot command to the enemy
			localResponse = recvSend(peer, prot);
			if(localResponse.contains("no-hit")) {
				return -1;
			}
			else if(localResponse.contains("hit")) {
				return 1;
			}
			else if(localResponse.contains("sunk")) {
				return 2;
			}
			else if(localResponse.contains("win")) {
				return 2;
			}
		}
		return 0;
	}
	
	
	/**
	 * Resets the game.
	 * @author lungua
	 * @since hw3
	 */
	public void reset() {
		this.game.clear();
		this.prot.setState("init");
	}
	
	/**
	 * Gets this player ready for the game and notifies the other player that they're ready, then waits for the other 
	 * player to be ready, or vice-versa (waits for the other player to be ready then gets ready)
	 * @author lungua
	 * @since hw3
	 * @param ready true if this player is ready for the game, i.e. placed their ships down and ended their
	 * turn before time ran out, false otherwise.
	 * @return true some player didn't ready up, false otherwise, i.e. both readied up.
	 */
	public boolean readyUp(boolean ready) {
		String readyCommand = (ready) ? "ready" : "pass";
		boolean result = false;
		if(this.mode == Mode.SERVER) {
			this.prot.process("player1 " + readyCommand);
			remoteResponse = sendRecv(this.peer, this.prot, "player1 " + readyCommand); 
			
		}
		else {
			localResponse = recvSend(peer, prot);
			if(localResponse.contains("pass")) result = true;
		}
		if(this.mode == Mode.CLIENT) {
			this.prot.process("player2 " + readyCommand);
			remoteResponse = sendRecv(this.peer, this.prot, "player2 " + readyCommand); 
			
		}
		else {
			localResponse = recvSend(peer, prot);
			if(localResponse.contains("pass")) result = true;
		}
		return result;
	}
	
	/**
	 * Sends a pass command to the opponent, signifying that this player didn't make a move in time.
	 * @author lungua
	 * @since hw3
	 */
	public void sendPassCommand() {
		peer.send(this.player + " pass");
	}
	
	/**
	 * Place a ship.
	 * @author lungua
	 * @since hw3
	 * @param l length of ship
	 * @param r row index of ship
	 * @param c column index of ship
	 * @param d direction of ship as a string, either vertical or horizontal
	 * @return true if a ship was successfully placed without conflict with an existing ship, false otherwise
	 */
	public boolean placeShip(int l, int r, int c, String d) {
		if(d.equals("vertical"))
			return this.game.placeShip(l, r, c, Direction.VERTICAL);
		else
			return this.game.placeShip(l, r, c, Direction.HORIZONTAL);
	}	
	
	/**
     * Removes a ship from the board at a specified location and returns its length, if a ship exists there.
     * @author lungua
     * @since hw3
     * @param r row index of ship to remove
     * @param c column index of ship to remove
     * @return length of the ship that was removed if there was a ship there, or -1 if there was not.
     */
	public int removeShip(int r, int c) {
		return this.game.removeShip(r, c);
	}
	
	/**
	 * Returns the entry in the hit grid at row r, column c
	 * @author lungua
	 * @since hw3
	 * @param r row index.
	 * @param c column index.
	 * @return 0 if this player has no information about enemy ships at row r, column c,<br>
	 * 		   -1 if this player knows the enemy has no ship at row r, column c via shooting, <br>
	 * 		   1 if this player knows the enemy has a ship at row r, column c via shooting at it,<br>
	 * 		   2 if this player knows the enemy has a ship at row r, column c that has been sunk.
	 */
	public int hitGridEntry(int r, int c) {
		return this.game.hitGridEntry(r,c);
	}
	
	/**
	 * Returns information about this player's ship grid at row r, column c. 
	 * @author lungua
	 * @since hw3
	 * @param r row index.
	 * @param c column index.
	 * @return 0 if this player has no ship at row r, column c,
	 * 		   1 if this player has a ship at row r, column c that has not been hit,
	 * 		   2 if this player has a ship at row r, column c that has been hit.
	 */
	public int shipData(int r, int c) {
		return this.game.shipData(r, c);
	}

	
	
	/**
	 * After the game has ended, return information about whether this player won or not.
	 * @author lungua
	 * @since hw3
	 * @return true if the game is over and this player won, false otherwise.
	 */
	public boolean winner() {
		return this.game.getState() == BattleshipWinner.I_WIN;
	}
	
	/**
	 * Immediately lose the game due to the timer running out.
	 * @author lungua
	 * @since hw3
	 */
	public void loseDueToTimer() {
		this.prot.process(this.player + " pass");
		this.peer.send(this.player + " pass");
		this.prot.setState("voting");
	}
	
	/**
	 * Execute a protocol command to vote for the game to restart or not, then wait for the other player to vote on restarting,
	 * or vice versa (wait then execute)
	 * @author lungua
	 * @since hw3
	 * @param restart true if this player has chosen to restart the game, false otherwise.
	 * @return true if the game was restarted, false otherwise.
	 */
	public boolean vote(boolean restart) {
		// server will send info to client first, then client to server
		assert this.prot.getState() == BattleshipStates.VOTING;
		String protocolCommand = this.player + " vote ";
		if(restart) protocolCommand += "yes";
		else protocolCommand += "no";
		if (this.mode == Mode.SERVER) {
			this.prot.process(protocolCommand);
			remoteResponse = sendRecv(peer, prot, protocolCommand); 
			
		}
		else {
			localResponse = recvSend(peer, prot);
		}
		
		
		if (this.mode == Mode.CLIENT) {
			this.prot.process(protocolCommand);
			remoteResponse = sendRecv(peer, prot, protocolCommand); 
			
		}
		else {
			localResponse = recvSend(peer, prot);
//			log.info(localResponse);
		}
		return this.prot.getState() == BattleshipStates.INIT;
	}
	
	private static String sendRecv(Connectable peer, BattleshipProtocol prot, String localCommand) {
		String remoteResponse;
		peer.send(localCommand);


		remoteResponse = peer.receive();

		return remoteResponse;
	}
	
	private static String recvSend(Connectable peer, BattleshipProtocol prot) {
		String remoteCommand = peer.receive();
		String localResponse = prot.process(remoteCommand);
		peer.send(localResponse);
		return localResponse;
	}
	/**
	 * If -1 is passed in as a port number to this class, the default port number will be used.
	 */
	public static final int USE_DEFAULT_PORT = -1;
	
	
	
}
