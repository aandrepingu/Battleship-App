/**
 * 
 */
package p2p.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;
/**
 * Class which manages network connections for the server in the battleship game, i.e. player1, 
 * the player who receives a connection from the other player.
 * @author lungua
 * @since hw3
 */
public class BattleshipServer implements Connectable {
	/**
	 * Default port number.
	 */
	public static final int DEFAULT_PORT = 8189;
	
	private int port;
	private Socket socket;
	private ServerSocket servSocket;
	private InputStream inStream;
	private OutputStream outStream;
	Scanner in;
	PrintWriter out;
	private Logger log;
	
	/**
	 * Construct this server using the default port number.
	 * @author lungua
	 * @since hw3
	 * @throws IOException if an I/O error occurs when opening the socket for this server.
	 */
	public BattleshipServer() throws IOException {
		this(DEFAULT_PORT);
	}
	
	/**
	 * Construct this server using a custom port number.
	 * @author lungua
	 * @since hw3
	 * @param port desired port number.
	 * @throws IOException if an I/O error occurs when opening the socket for this server.
	 */
	public BattleshipServer(int port) throws IOException {
		this.log = Logger.getLogger("global");
		
		this.port = port;
		this.servSocket = new ServerSocket(this.port);
//		log.info(String.format("Server socket was created on port %d.\n", port));
	}
	
	/**
	 * Accept a connection from a client.
	 * @author lungua
	 * @since hw3
	 * @throws IOException if an I/O error occurs when waiting for a connection or when creating input/output streams.
	 */
	@Override
	public void connect() throws IOException {
		this.socket = this.servSocket.accept();
//		log.info(String.format("Incoming connection from a client at %s accepted.\n", this.socket.getRemoteSocketAddress().toString()));
		this.inStream =  this.socket.getInputStream();
		this.outStream = this.socket.getOutputStream();
		this.in = new Scanner(this.inStream);
		this.out = new PrintWriter(new OutputStreamWriter(this.outStream, StandardCharsets.UTF_8), true /*autoFlush */);

	}

	/**
	 * Send a message to the client.
	 * @author lungua
	 * @since hw3
	 * @param message Message to send.
	 */
	@Override
	public void send(String message) {
		this.out.println(message);
//		log.info(String.format("Message %s sent.\n", message));
	}

	/**
	 * Read in a message from the client.
	 * @author lungua
	 * @since hw3
	 */
	@Override
	public String receive() {
		String message = this.in.nextLine();
//		log.info(String.format("Message %s received.\n", message));
		return message;
	}

	/**
	 * Get this server's port number.
	 * @author lungua
	 * @since hw3
	 * @return this server's port number.
	 */
	@Override
	public int getPort() {
		return this.port;
	}

}
