/**
 * 
 */
package game.java;

import java.util.HashSet;
import game.java.Ship.Direction;


/**
 * Class that manages the state of the Battleship game for one player, with a board for hits on enemy and player ships.
 * @author lungua
 * @since hw3
 */
public class BattleshipPlayer {
	/**
	 * Current state of the game: initial, in progress, this player won, this player's enemy won.
	 * @author lungua
	 * @since hw3
	 */
	public enum BattleshipWinner{/**Initial state, placing ships*/INIT, /** Game in progress*/IN_PROGRESS,/**This player won */I_WIN,/** This player lost*/I_LOSE};
	// Info about enemy ships. Entries are 0 if user has not yet shot that location, -1 if enemy has no ship there, 1 if they do
	private int[][] hitGrid; 
	
	// where the user places their ships. Entries are empty if no ship is there, <Ship Length><H for hit or N for not>
    private Ship[][] shipGrid; 
    
    private int rows, cols;
    	
    private HashSet<Ship> ships;
    private BattleshipWinner state;
    private Boolean meRestart, oppRestart;
    private Boolean meReady, oppReady;
    private String player;

    
    /**
     * Initializes the battleship game with empty grids, no ships added, and in the initial state of the game.
     * 
     * @author lungua
     * @since hw3
     * @param r desired number of rows for the battleship grid.
     * @param c desired number of columns for the battleship grid.
     * @param player string that designates this player as either player1 or player2 in the battleship game.
     */
    public BattleshipPlayer(int r, int c, String player){
        this.rows = r;
        this.cols  = c;

        this.ships = new HashSet<>(); // ships will be added as the game progresses
        this.shipGrid = new Ship[r][c];
        this.hitGrid = new int[r][c];
        this.state = BattleshipWinner.INIT;
        this.player = player;
        this.meRestart = null;
        this.oppRestart = null;
        this.meReady = false;
        this.oppReady = false;
    }
    /**
	 * Returns the state of the Battleship game for this player.
	 * @author lungua
     * @since hw3
	 * @return the state of the game.
	 */
	public BattleshipWinner getState() {
		return state;
	}

	/**
	 * Return the number of rows for this game.
	 * @author lungua
	 * @since hw3
	 * @return number of rows for this game.
	 */
	public int getRows() {
		return this.rows;
	}
	
	/**
	 * Return the number of columns for this game.
	 * @author lungua
	 * @since hw3
	 * @return number of columns for this game.
	 */
	public int getCols() {
		return this.cols;
	}
	
	/**
	 * Sets this player to be ready to play or notifies them that their opponent is ready.
	 * @param player player who is ready to play.
	 */
	public void ready(String player) {
		assert this.state == BattleshipWinner.INIT;
		if((this.player.equals("player1") && player.equals("player1")) || (this.player.equals("player2") && player.equals("player2"))) {
			this.meReady = true;
		}
		else if((this.player.equals("player1") && player.equals("player2")) || (this.player.equals("player2") && player.equals("player1"))) {
			this.oppReady = true;;
		}
		else {
			throw new IllegalArgumentException();
		}
		if(this.meReady && this.oppReady) {
			this.state = BattleshipWinner.IN_PROGRESS;
		}
		
	}
	
	/**
	 * Returns true if this player is ready to play, false otherwise.
	 * @return  true if this player is ready to play, false otherwise.
	 */
	public boolean isReady() {
		return this.meReady;
	}
	
	/**
	 * Returns true if this player's opponent is ready to play, false otherwise.
	 * @return  true if this player's opponent is ready to play, false otherwise.
	 */
	public boolean oppReady() {
		return this.oppReady;
	}
	
	/**
	 * Returns this player's vote to restart; true if they want to restart, false if they don't, null if they haven't voted yet.
	 * @return this player's vote to restart.
	 */
	public Boolean getRestart() {
		return this.meRestart;
	}
	
	/**
	 * Returns this player's opponent's vote to restart; true if they want to restart, false if they don't, null if they haven't voted yet.
	 * @return this player's opponent's vote to restart.
	 */
	public Boolean getOppRestart() {
		return this.oppRestart;
	}
	
	/**
	 * Returns this player's designation as player1 or player2 in the game.
	 * @return "player1" if this player is the server in the game, "player2" if this player is the client in the game.
	 */
	public String getPlayer() {
		return player;
	}
	
	
	
	/**
	 * Executes a player's vote on restarting the game or not.
	 * @param player player who is voting on the game to restart, should be player1 or player2.
	 * @param restart true if player wants to restart, false otherwise.
	 * @throws IllegalArgumentException if player is not equal to "player1" or "player2"
	 */
	public void voteToRestart(String player, boolean restart) {
		assert this.state == BattleshipWinner.I_WIN || this.state == BattleshipWinner.I_LOSE;
		if((this.player.equals("player1") && player.equals("player1")) || (this.player.equals("player2") && player.equals("player2"))) {
			this.meRestart = restart;
		}
		else if((this.player.equals("player1") && player.equals("player2")) || (this.player.equals("player2") && player.equals("player1"))) {
			this.oppRestart = restart;
		}
		else {
			throw new IllegalArgumentException();
		}
	}
    
    
	/**
	 * Resets the state of the game, clearing out the board and setting the state to the initial state
	 * @author lungua
     * @since hw3
	 */
	public void clear() {
		this.hitGrid = new int[this.rows][this.cols];
		this.shipGrid = new Ship[this.rows][this.cols];
		this.ships.clear();
		this.meRestart = null;
        this.oppRestart = null;
        this.meReady = false;
        this.oppReady = false;
        this.state = BattleshipWinner.INIT;
	}
	
	/**
     * Resizes the grid to a desired number of rows and columns.
     * @param r desired new number of rows for the battleship grid.
     * @param c desired new number of columns for the battleship grid.
     */
    public void resize(int r, int c){
        this.rows = r;
        this.cols = c;
        this.clear();
    }
    
    
    /**
     * Place a ship of length l starting at row r and column c in the grid, either horizontally or vertically via a Direction enum.
     * @param l desired length of ship.
     * @param r row index to place ship at.
     * @param c column index to place ship at.
     * @param d Direction of the ship, either Direction.HORIZONTAL or Direction.VERTICAL
     * @return true if a ship was successfully placed without conflict with an existing ship, false otherwise.
     */
    public boolean placeShip(int l, int r, int c, Direction d) {
    	assert this.state == BattleshipWinner.INIT;
    	if( r >= this.rows || r < 0 || c >= this.cols || c < 0) return false;
    	int[][] coords = new int[l][2]; // storing coordinates we want to place at
    	try {
    		for(int i = 0; i < l; ++i) {
    			if(d == Direction.HORIZONTAL) {
    				if(this.shipGrid[r][c+i] != null) {
    					return false;
    				}
    				coords[i][0] = r;
    				coords[i][1] = c+i;
    			}
    			else if(d == Direction.VERTICAL) {
    				if(this.shipGrid[r+i][c] != null) {
    					return false;
    				}
    				coords[i][0] = r+i;
    				coords[i][1] = c;
    			}
    		}
    	}catch(ArrayIndexOutOfBoundsException e) {
    		return false; // we went out of bounds of the grid
    	}
    	Ship s = new Ship(l,r,c,d, coords);
    	for(int[] coordPair : coords) {
    		this.shipGrid[coordPair[0]][coordPair[1]] = s;
    	}
    	this.ships.add(s);
    	
    	return true;
    }
    
    /**
     * Move a ship at some location in the grid in the grid to row r, column c.
     * @param rOld row index of ship we want to move.
     * @param cOld column index of ship we want to move.
     * @param r new row index for the ship.
     * @param c new column index for the ship.
     * @return true if the ship was successfully moved, false otherwise.
     */
    public boolean moveShip(int rOld, int cOld, int r, int c) {
    	Ship shipToMove = this.shipGrid[rOld][cOld];
    	if(shipToMove == null) return false;
    	for(int[] coordinates : shipToMove.getCoords()) {
    		this.shipGrid[coordinates[0]][coordinates[1]] = null;
    	}
    	this.ships.remove(shipToMove);
    	return this.placeShip(shipToMove.getLength(), r, c, shipToMove.getDirection());
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
    	Ship shipToRemove = shipGrid[r][c];
    	if(shipToRemove == null) return -1;
    	for(int[] coordinates : shipToRemove.getCoords()) {
    		this.shipGrid[coordinates[0]][coordinates[1]] = null;
    	}
    	this.ships.remove(shipToRemove);
    	return shipToRemove.getLength();
    }

    
    /**
     * Upon receiving information that this player won the game, this method should be called to
     * set the state of the player to be "I WIN".
     */
    public void notifyWin() {
    	this.state = BattleshipWinner.I_WIN;
    }
 


	/**
	 * Receive a shot from the enemy at row r, column c. Returns information about if a shit was shot/sunk or not.
	 * @author lungua
	 * @since hw3
	 * @param r row index user is shot at.
	 * @param c column index user is shot at.
	 * @return -1 if none of the user's ships are shot or if the shot is out of bounds, 1 if a user's ship is hit, and 2 if a user's ship is sunk.
	 */
	public int receiveShot(int r, int c) {
		assert this.state == BattleshipWinner.IN_PROGRESS;
		if(this.shipGrid[r][c] == null) return -1;
		
		if(this.shipGrid[r][c].receiveShot(r, c)) {
			// ship at r,c was sunk
			if(this.noShipsRemain()) {
				this.state = BattleshipWinner.I_LOSE;
			}
			return 2;
		}else {
			return 1;
		}
		
	}
	
	/**
	 * Updates this player's hit grid with information about hitting an enemy ship.
	 * @author lungua
	 * @since hw3
	 * @param r row index that an enemy was shot at.
	 * @param c column index that an enemy was shot at.
	 * @param shipInfo information about the enemy grid at location r,c. Should be 1, 2, or -1.<br> 
	 * 		  shipInfo == 1 implies that an enemy ship exists at r,c and was shot.<br>
	 * 		  shipInfo == 2 implies that an enemy ship exists at r,c and was sunk. <br>
	 * 		  shipInfo == -1 implies that no enemy ship exists at r,c.
	 */
	public void updateHitGrid(int r, int c, int shipInfo) {
		assert this.state == BattleshipWinner.IN_PROGRESS;
		this.hitGrid[r][c] = shipInfo;
	}
	
	/**
	 * Returns the entry in the hit grid at row r, column c
	 * @param r row index.
	 * @param c column index.
	 * @return 0 if this player has no information about enemy ships at row r, column c,<br>
	 * 		   -1 if this player knows the enemy has no ship at row r, column c via shooting, <br>
	 * 		   1 if this player knows the enemy has a ship at row r, column c via shooting at it,<br>
	 * 		   2 if this player knows the enemy has a ship at row r, column c that has been sunk.
	 */
	public int hitGridEntry(int r, int c) {
		return this.hitGrid[r][c];
	}
	
	/**
	 * Returns true if this player has no live ships left i.e. they lost the game.
	 * @author lungua
	 * @since hw3
	 * @return true if this player has no live ships remaining and thus has lost the game.
	 */
	public boolean noShipsRemain() {
		for(Ship s : this.ships) {
			if(s.getHealth() != 0) return false;
		}
		return true;
	}
	
	/**
	 * Returns information about this player's ship grid at row r, column c. 
	 * @param r row index.
	 * @param c column index.
	 * @return 0 if this player has no ship at row r, column c,
	 * 		   1 if this player has a ship at row r, column c that has not been hit,
	 * 		   2 if this player has a ship at row r, column c that has been hit.
	 */
	public int shipData(int r, int c) {
		Ship s =this.shipGrid[r][c];
		if(s == null) return 0;
		if(s.isHit(r, c)) return 2;
		else return 1;
	}
	
}
