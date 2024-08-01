package game.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;



/**
 * Represents a ship in the battleship game.
 * @author lungua
 * @since hw3
 */
public class Ship {
	/**
	 * Direction of the ship.
	 * @author lungua
	 * @since hw3
	 */
	public enum Direction {
		/** Vertical ship*/VERTICAL, 
		/** Horizontal ship*/HORIZONTAL};
	
	private int length;
	private int row, column;
	private int health;
	private Direction direction;
	private HashMap<ArrayList<Integer>, Boolean> hits; // contains coordinates of the ship (r,c) as keys
	
	/**
	 * Construct a new ship at row r, column c, of length l and oriented in direction d, occupying a list of coordinates.
	 * @author lungua
	 * @since hw3
	 * @param l length of ship.
	 * @param r row index ship is placed at.
	 * @param c column index ship is placed at.
	 * @param d direction ship is facing.
	 * @param coords coordinates that this ship occupies, as a list of row, column pairs
	 */
	public Ship(int l, int r, int c, Direction d, int[][] coords) {
		this.length = l;
		this.health = l;
		this.row = r;
		this.column = c;
		this.direction = d;
		this.hits = new HashMap<>();
		// initialize hit map
		for(int i = 0; i < l; ++i) {
			ArrayList<Integer> coordList = new ArrayList<>();
			coordList.add(coords[i][0]);
			coordList.add(coords[i][1]);
			this.hits.put(coordList, Boolean.FALSE);
		}
	}
	
	/**
	 * Handles the ship being shot by an enemy. r and c must be coordinates that the ship is on. Returns information about
	 * whether the ship was sunk or not.
	 * @author lungua
	 * @since hw3
	 * @param r row index to be shot at.
	 * @param c column index to be shot at.
	 * @return true if this ship was sunk, false if it was hit but not sunk 
	 */
	public boolean receiveShot(int r, int c) {
		ArrayList<Integer> coords = new ArrayList<>();
		coords.add(r); coords.add(c);
		
		// this spot was not already hit
		if(this.hits.get(coords).equals(Boolean.FALSE)) {
			this.hits.put(coords, Boolean.TRUE);
			this.health --;
		}
		if(this.health == 0) return true; // all coordinates of the ship have been shot
		return false;
	}
	
	
	/**
	 * Return the length of this ship.
	 * @author lungua
     * @since hw3
	 * @return Length of ship.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Return the row index of this ship.
	 * @author lungua
     * @since hw3
	 * @return Row index of Ship
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Return the column index of this ship.
	 * @author lungua
     * @since hw3
	 * @return Column index of Ship
	 */
	public int getColumn() {
		return column;
	}
	
	/**
	 * Returns the health of this ship.
	 * @author lungua
     * @since hw3
	 * @return health of this ship.
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * Returns all coordinates this ship occupies an array of row,column pairs of the same length as this ship.
	 * @author lungua
	 * @since hw3
	 * @return an array of this ship's coordinates as an array of row, column pairs.
	 */
	public int[][] getCoords(){
		int[][] result = new int[this.length][2];
		int i = 0;
		for(ArrayList<Integer> coords : this.hits.keySet()) {
			result[i][0] = coords.get(0);
			result[i][1] = coords.get(1);
			++i;
		}
		return result;
	}
	
	/**
	 * Returns information about if this ship has been hit at coordinates (r,c).
	 * @author lungua
	 * @since hw3
	 * @param r row index.
	 * @param c column index.
	 * @return true if this ship occupies row r and column c and has been hit at that location,
	 * false otherwise.
	 */
	public boolean isHit(int r, int c){
		ArrayList<Integer> coords = new ArrayList<>();
		coords.add(r);
		coords.add(c);
		if(!hits.containsKey(coords)) return false;
		return hits.get(coords).booleanValue();
	}
	
	/**
	 * Returns the hashcode of this ship, based on location (row and column it was placed on), and orientation.
	 * @author lungua
     * @since hw3
	 * @return hash code for this ship, based on row and column.
	 */
	@Override
	public int hashCode() {
		return this.row + this.column;
	}
	
	
	/**
	 * Returns true if this ship is in the same row and column and is of the same direction as another ship.
	 * @author lungua
     * @since hw3
	 * @param o Other ship to compare to.
	 * @return true if this ship is equal to the other object, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o == this) return true;
		if (!(o instanceof Ship)) return false;
		Ship s = (Ship) o;
		return this.row == s.row && this.column == s.column && this.direction == s.direction;
	}
	
	
	/**
	 * Returns the direction of this ship, either horizontal or vertical.
	 * @author lungua
	 * @since hw3
	 * @return direction of this ship.
	 */
	public Direction getDirection() {
		return this.direction;
	}
	
	
}
