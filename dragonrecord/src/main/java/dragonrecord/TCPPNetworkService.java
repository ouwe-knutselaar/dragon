package dragonrecord;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



public class TCPPNetworkService implements Runnable{
	
	private Logger log=Logger.getLogger(TCPPNetworkService.class.getSimpleName());
	private boolean running=true;
	private ServerSocket welcomeSocket;
	private String clientInputString;
	private DataOutputStream outToClient;
	boolean __sessionIsRunning = false;
	OrchestrationService orchestrationService=OrchestrationService.GetInstance();
	
	
	private final String help="Dragon help"+System.lineSeparator()
			+ "lst:         list actions"+System.lineSeparator()
			+ "pos SS XXX:  set servo SS at XXX"+System.lineSeparator()
			+ "trk XX:      arm recording for servo XX"+System.lineSeparator()
			+ "stp:         stop recording"+System.lineSeparator()
			+ "wrt:         write current motion to storage"+System.lineSeparator()
			+ "rst:         total reset"+System.lineSeparator()
			+ "dmp:         dump record"+System.lineSeparator()
			+ "end:         End software"+System.lineSeparator();
	
	
	
	public TCPPNetworkService() 
	{
		log.info("Make the networking service");
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
		__write("Welcome to the Dragon Recorder");
		while (__sessionIsRunning) {
			try {
				clientInputString = inFromClient.readLine();
				if (clientInputString == null)clientInputString = "empty";
				
				log.debug("tcp read: " + clientInputString);

				switch (clientInputString.substring(0, 3)) {
				case "trk":
					orchestrationService.startTrackRecording(Integer.parseInt(clientInputString.substring(4)));
					__write("OK SET RECORD");
					break;
				case "stp":
					orchestrationService.stopTrackRecording();
					__write("OK STOP RECORD");
					break;
				case "hlp":
					__write(help());
					break;
				case "rst":
					orchestrationService.totalReset();
					break;
				case "wrt":
					orchestrationService.writeCurrentMotion();
					__write("OK WRITE CURRENT");
					break;
				case "dmp":
					orchestrationService.dumpCurrentMotion();
					__write("OK DUMP CURRENT");
					break;	
				case "end":
					//DragonRaspberry.endDragon();
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
	
	
	private void __write(String lineToWrite)
	{
		log.debug("tcp write "+lineToWrite);
		try {
			outToClient.writeBytes(lineToWrite+System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
			log.info("Error in the session, abort connection");
			__sessionIsRunning=false;
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
	



}
