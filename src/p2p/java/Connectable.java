/**
 * 
 */
package p2p.java;

import java.io.IOException;

/**
 * 
 * Interface which defines key operations for a player in the battleship game:
 * connect to an opponent, do something in the game by sending a command, receive a command from an opponent,
 * and return the current port number.
 * @author lungua
 * @since hw3
 */
public interface Connectable {
	
	/**
	 * Accept a network connection.
	 * @author lungua
	 * @since hw3
	 * @throws IOException if an I/O error occurs when waiting for a connection or when creating input/output streams.
	 */
	public void connect() throws IOException;
	/**
	 * Send a message to a peer.
	 * @author lungua
	 * @since hw3
	 * @param message Message to send.
	 */
	public void send(String message);	
	/**
	 * Read in a message to a peer.
	 * @author lungua
	 * @since hw3
	 * @return message read in.
	 */
	public String receive();
	/**
	 * Get this server's port number.
	 * @author lungua
	 * @since hw3
	 * @return this server's port number.
	 */
	public int getPort();
}
