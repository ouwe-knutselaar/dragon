package dragonrecord;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;




public class UDPNetworkService implements Runnable{
	
	OrchestrationService orchestrationService=OrchestrationService.GetInstance();

	private Logger log=Logger.getLogger(UDPNetworkService.class.getSimpleName());
	private boolean running=true;
	private byte[] receiveData = new byte[1024];
	private byte[] sendData = new byte[1024];
	private DatagramSocket serverSocket;
	
	
	public UDPNetworkService() 
	{
		log.info("Make the networking service");
		orchestrationService=OrchestrationService.GetInstance();
		try {
			serverSocket = new DatagramSocket(3001);
		} catch (SocketException e) {
			log.error("UDP Socket Error "+e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void startUDPNetworkService()
	{
		log.info("Start UDPNetworkService on port 3001");
		Thread thisThread=new Thread(this);
		thisThread.start();
	}
	
	@Override
	public void run() {
		log.info("UDPNetworkService thread started");
		String receivedDataString="";
		while(running)
		{
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
				serverSocket.receive(receivePacket);
				receivedDataString=new  String(receivePacket.getData());
				positionServo(receivedDataString);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		log.info("UDPNetworkService stopped");
	}

	
	public void stop()
	{
		log.info("Stopping the UDPNetworkService service");
		running=false;
	}
	
	
	
	private String positionServo(String clientSentence) {
		try{
			log.debug("Position servo with string "+clientSentence);
			int servo=Integer.parseInt(clientSentence.substring(2,4));
			int servoValue=Integer.parseInt(clientSentence.substring(5,9));
			log.debug("Servo is "+servo+" values is "+servoValue); 
			orchestrationService.setSingleServo(servo,servoValue);
		}
		catch (NumberFormatException e)
		{
			log.debug("NumberFormatException "+e.getMessage());
			return "NumberFormatException\n\r";
		}
		return "OK\n\r";
	}

}
