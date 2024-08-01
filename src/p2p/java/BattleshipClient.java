/**
 * 
 */
package p2p.java;

import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;
import java.nio.charset.*;

/**
 * Class which manages network connections for the client in the battleship game, i.e. player2, the player who connects
 * to the other player.
 * @author lungua
 * @since hw3
 */
public class BattleshipClient implements Connectable {
	/**
	 * Default port number.
	 */
	public static final int DEFAULT_PORT = 8189;
	
	private Socket socket;
	private String server;
	private int port;
	private InputStream inStream;
	private OutputStream outStream;
	Scanner in;
	PrintWriter out;
	private Logger log;	
	
	
	/**
	 * Construct client using the default port.
	 * @author lungua
	 * @since hw3
	 * @param server server to connect to.
	 */
	public BattleshipClient(String server) {
		this(server, DEFAULT_PORT);		
	}
	
	/**
	 * Construct client with a specified port.
	 * @author lungua
	 * @since hw3
	 * @param server server to connect to.
	 * @param port desired port number.
	 */
	public BattleshipClient(String server, int port) {
		this.log = Logger.getLogger("global");
		this.server = server;
		this.port = port;
	}

		
	/**
	 * Connect to this client's server.
	 * @author lungua
	 * @since hw3
	 * @throws IOException if an I/O error occurs when connecting the socket or attempting to get streams from the socket.
	 */
	@Override
	public void connect() throws IOException {
		this.socket = new Socket(this.server, this.port);
//		log.info(String.format("Connection to server %s established at port %d.\n", server, port));
		this.inStream =  this.socket.getInputStream();
		this.outStream = this.socket.getOutputStream();
		this.in = new Scanner(this.inStream);
		this.out = new PrintWriter(new OutputStreamWriter(this.outStream, StandardCharsets.UTF_8), true /*autoFlush */);	
		
	}
	
	/**
	 * Send a message to the associated server.
	 * @author lungua
	 * @since hw3
	 * @param message message to send.
	 */
	@Override
	public void send(String message) {
		this.out.println(message);
//		log.info(String.format("Message %s sent.\n", message));
	}
	
	/**
	 * Read in a message from the server.
	 * @author lungua
	 * @since hw3
	 * @return Message read from the server.
	 */
	@Override
	public String receive() {
		String message = this.in.nextLine();
//		log.info(String.format("Message %s received.\n", message));
		return message;
	}

	/**
	 * Get client's port number.
	 * @author lungua
	 * @since hw3
	 * @return client's port number.
	 */
	@Override
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Get client's associated server.
	 * @author lungua
	 * @since hw3
	 * @return client's server.
	 */
	public String getServer() {
		return this.server;
	}
	
	/**
	 * Returns true if connection to the server is closed, false otherwise.
	 * @author lungua
	 * @since hw3
	 * @return true if connection to the server is closed, false otherwise.
	 */
	public boolean isConnectionClosed() {
		return this.socket.isClosed();
	}

}
