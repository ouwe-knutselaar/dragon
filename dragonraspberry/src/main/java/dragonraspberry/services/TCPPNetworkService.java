package dragonraspberry.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dragonraspberry.DragonRaspberry;
import dragonraspberry.pojo.DragonEvent;


public class TCPPNetworkService implements Runnable{
	
	private Logger log=Logger.getLogger(TCPPNetworkService.class.getSimpleName());
	private List<DragonEvent> eventHandler=new ArrayList<>();
	private boolean running=true;
	private ServerSocket welcomeSocket;
	private String clientInputString;
	private DataOutputStream outToClient;
	private OrchestrationService orchestrationService;
	boolean __sessionIsRunning = false;
	
	
	private final String help="Dragon help"+System.lineSeparator()
			+ "rnd:         start random actions"+System.lineSeparator()
			+ "lst:         list actions"+System.lineSeparator()
			+ "cur xxxxxxx: set new current motion"+System.lineSeparator()
			+ "pse:         pause all activity"+System.lineSeparator()
			+ "nor:         operate normal"+System.lineSeparator()
			+ "rst:         total reset"+System.lineSeparator()
			+ "end:         End software"+System.lineSeparator();
	
	public TCPPNetworkService() 
	{
		log.info("Make the networking service");
		orchestrationService=OrchestrationService.GetInstance();
		try {
			welcomeSocket = new ServerSocket(3000);
		} catch (IOException e) {
			log.error("TCP Socket Error "+e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Start the TCP networking thread
	 */
	public void startTCPPNetworkService()
	{
		log.info("Start TCPNetworkService");
		Thread thisThread=new Thread(this);
		thisThread.start();
	}
	
	@Override
	public void run() {	
		log.info("Start TCPNetworkService Thread");
		Socket connectionSocket;
		while(running)
		{
			try {
				connectionSocket = welcomeSocket.accept();
				tcpSession(connectionSocket);
				connectionSocket.close();
			} catch (IOException e) {
				log.error("Error during session "+e.getMessage());
				e.printStackTrace();
			}
		}
		log.info("TCPPNetworkService stopped");
	}

	private void tcpSession(Socket connectionSocket) throws IOException {
		log.info("Session with " + connectionSocket.getInetAddress());
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		__sessionIsRunning=true;
		__write("Welcome to the Dragon I2C");
		while (__sessionIsRunning) {
			try {
				clientInputString = inFromClient.readLine();
				if (clientInputString == null)clientInputString = "empty";
				
				log.debug("tcp read: " + clientInputString);

				switch (clientInputString.substring(0, 3)) {
				case "rnd":
					eventHandler.forEach(handler -> handler.handle("random", 1, 1));
					__write("OK RANDOM");
					break;
				case "hlp":
					__write(help());
					break;
				case "rst":
					orchestrationService.totalReset();
					break;
				case "end":
					DragonRaspberry.endDragon();
					break;
				default:
					__write("ERROR");
				}
			} catch (StringIndexOutOfBoundsException e) {
				__write("ERROR StringIndexOutOfBoundsException");
			} catch (NumberFormatException e)
			{
				__write("ERROR NumberFormatException");
			} catch(ArrayIndexOutOfBoundsException e)
			{
				__write("ERROR ArrayIndexOutOfBoundsException");
			}

		}
		log.info("Session ended");
	}
	
	
	private void __write(String lineToWrite) {
		try {
			log.debug("tcp write " + lineToWrite);
			outToClient.writeBytes(lineToWrite + System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
			log.info("Error in the session, abort connection");
			__sessionIsRunning = false;
		}
	}
	
	private String help() {
		return help;
	}


	/**
	 * Stop the TCP networking service
	 */
	public void stop()
	{
		log.info("Stopping the TCPPNetworkService service");
		running=false;
	}
	
	/**
	 * Add an event handler to the TCP network service
	 * @param event
	 */
	public void onNetworkEvent(DragonEvent event)
	{
		eventHandler.add(event);
	}

}
