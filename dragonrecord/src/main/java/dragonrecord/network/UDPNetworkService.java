package dragonrecord.network;

import dragonrecord.OrchestrationService;
import dragonrecord.config.ConfigReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDPNetworkService implements Runnable{
	
	private OrchestrationService orchestrationService;
	private final Logger log=Logger.getLogger(UDPNetworkService.class.getSimpleName());
	private boolean running=true;
	private byte[] receiveData = new byte[1024];
	private DatagramSocket serverSocket;
	
	
	public UDPNetworkService(OrchestrationService orchestrationService) {
		try {
			this.orchestrationService = orchestrationService;
			if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
			log.info("Initialize the networking service");
			serverSocket = new DatagramSocket(3001);
			startUDPNetworkService();
		} catch (SocketException e) {
			log.fatal("UDP Socket Error "+e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void startUDPNetworkService() {
		log.info("Start UDPNetworkService on port 3001");
		Thread thisThread=new Thread(this);
		thisThread.start();
	}
	
	@Override
	public void run() {
		String receivedDataString = "";
		while (running) {
			try {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				receivedDataString = new String(receivePacket.getData());
				receivedDataString=receivedDataString.substring(0, receivePacket.getLength());
				log.debug("UDP data received:"+(receivedDataString.trim()));
				char choice=receivedDataString.charAt(0);
				if(choice=='p')positionServo(receivedDataString);
				if(choice=='n') gameControllerInput(receivedDataString);
				if(choice=='c')orchestrationService.setCurrentMotion(receivedDataString.substring(1));
				if(choice=='r')orchestrationService.toggleTrackRecording();
				if(choice=='t')orchestrationService.stopTrackRecording(Integer.parseInt(receivedDataString.substring(2,4)));
				if(choice=='d')orchestrationService.dumpCurrentMotion();
				if(choice=='s')orchestrationService.saveCurrentMotion(receivedDataString.substring(1));
				if(choice=='e')orchestrationService.executeCurrentMotion();
				if(choice=='v')orchestrationService.sendServoValues(receivePacket.getAddress());
				if(choice=='f')orchestrationService.filterServo(Integer.parseInt(receivedDataString.substring(2,4)));
				if(choice=='x')orchestrationService.totalReset();
				receiveData = new byte[1024];	// reset the buffer
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.info("UDPNetworkService stopped");
	}

	private void gameControllerInput(String receivedDataString) {
		log.debug(receivedDataString);
		String[] parameters=receivedDataString.split(" ");
		int value = Integer.parseInt(parameters[2]);
		switch(parameters[1]){
			case "0": if(value==1000)orchestrationService.toggleTrackRecording();
						break;
			case "1": if(value==1000)orchestrationService.executeCurrentMotion();
						break;
			case "2": if(value==1000)orchestrationService.dumpCurrentMotion();
						break;
			case "3": if(value==1000)orchestrationService.resetTrack();
						break;
			case "x": orchestrationService.setSingleServo(0,value);
						break;
			case "y":   orchestrationService.setSingleServo(1,value);
						break;
			case "rx":  orchestrationService.setSingleServo(2,value);
						break;
			case "ry":  orchestrationService.setSingleServo(3,value);
						break;
			case "z":   orchestrationService.setSingleServo(10,value);
						break;
		}
	}

	public void stop() {
		log.info("Stopping the UDPNetworkService service");
		running=false;
	}

	private String positionServo(String clientSentence) {
		try {
			int servo = Integer.parseInt(clientSentence.substring(2, 4));
			int servoValue = Integer.parseInt(clientSentence.substring(5, 9));
			orchestrationService.setSingleServo(servo, servoValue);
			return "OK\n\r";
		} catch (NumberFormatException e) {
			log.error("NumberFormatException " + e.getMessage());
			return "NumberFormatException\n\r";
		}
	}

	public void sendString(InetAddress inetAddress,String sendString) {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			DatagramPacket sendPacket = new DatagramPacket(sendString.getBytes(), sendString.length(), inetAddress, 3003);
			clientSocket.send(sendPacket);
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void handlePov(int value){

	}
}