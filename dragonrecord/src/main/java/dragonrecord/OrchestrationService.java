package dragonrecord;

import dragonrecord.config.ConfigReader;
import dragonrecord.movement.MovementCoordinator;
import dragonrecord.movement.RandomMovementService;
import dragonrecord.network.UDPNetworkService;
import dragonrecord.recorder.MovementRecorder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class OrchestrationService {

	private final Logger log = Logger.getLogger("OrchestrationService");
	private WaveService waveService;
	private final MovementRecorder movementRecorder;
	private final RandomMovementService randomMovementService;
	private final UDPNetworkService udpNetworkService;
	private final ConfigReader configReader;
	private final String ACTIONS_DIR="actions";
	private String currentMotionName;
	private KeyboardService keyboardService;
	private MovementCoordinator movementCoordinator;


	public OrchestrationService(ConfigReader configReader) {
		this.configReader=  configReader;
		if(this.configReader.isDebug())log.setLevel(Level.DEBUG);
		log.info("Init Orchestration service");
		udpNetworkService = new UDPNetworkService(this);
		keyboardService = new KeyboardService(this);
		waveService = WaveService.getInstance();
		movementRecorder =new MovementRecorder();
		randomMovementService = new RandomMovementService(this);
		movementCoordinator = new MovementCoordinator();
	}
	
	//public static OrchestrationService getInstance() {
	//	if(classInstance == null) {
	//		classInstance = new OrchestrationService();
	//	}
	//	return classInstance;
	//}

	public void toggleTrackRecording() {
		if(movementRecorder.isRecording())movementRecorder.stopRecording();
		else movementRecorder.startRecording();
	}

	public void stopTrackRecording(int servo) {
		if(movementRecorder.isRecording())movementRecorder.stopRecording();
	}

	public void resetTrack(){
		if(movementRecorder.isRecording())return;
		movementRecorder.reset();
	}

	public void startRandomMoving(){
		randomMovementService.startRandomMovement();
	}

	public void stopAll() {

		movementCoordinator.allToDefault();
		log.info("Stop all activities");
	}

	public void stopRandomMoving() {
		randomMovementService.stopRandomMovement();
	}

	public void totalReset() {
		log.info("Reset current recording");
		movementRecorder.reset();
		movementCoordinator.fullReset();
	}

	public void setSingleServo(int servo, int servoValue) {
		movementCoordinator.goToNewValue(servo,servoValue);
		movementRecorder.writeServo(servo,servoValue);
	}

	public void setSingleServoDirect(int servo, int servoValue) {
		movementCoordinator.goToNewValueDirect(servo,servoValue);
	}

	public void dumpCurrentMotion() {
		log.info(System.lineSeparator()+movementRecorder);
	}

	public void saveCurrentMotion(String actionType) throws IOException {
		movementRecorder.writeSequenceFile(getSequenceFileName(),actionType);
	}

	public void setCurrentMotion(String recordingName)  {
		try {
			recordingName=recordingName.trim();
			log.info("Set on motion named '"+recordingName+"'");
			currentMotionName =recordingName;
			movementRecorder.openNewSequence(getSequenceFileName());
		} catch (IOException e) {
			log.error("Cannot open " + currentMotionName + "caused by "+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void executeCurrentMotion() {
		log.info("Play current motion");
	}

	public void sendServoValues(InetAddress inetAddress) {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			log.info("Reqeust to deliver the list of servo values");
			String servolist = configReader.getSemiColonSeparatedServoValuesListing();
			DatagramPacket sendPacket = new DatagramPacket(servolist.getBytes(), servolist.length(), inetAddress, 3003);
			clientSocket.send(sendPacket);
			clientSocket.close();
			log.info("List of servo's send");
			log.info(servolist);
		} catch (SocketException e) {
			log.error("Socket opening issue "+e.getMessage());
		} catch (IOException e) {
			log.error("IO exception "+e.getMessage());
		}
	}

	public void filterServo(int servo) {
		log.info("Run filter for servo "+servo);

	}

	public static String selectRootDir() {
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		if(operatingSystem.contains("win"))return "D:\\erwin\\dragon\\";
		if(operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix"))return "/var/dragon/";
		return "unknown";
	}

	private String getRecordingWaveName() {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
									.append(ACTIONS_DIR)
									.append(File.separatorChar)
									.append(currentMotionName)
									.append(File.separatorChar)
									.append(currentMotionName)
									.append(".wav");
		return waveFile.toString();
	}

	private String getSequenceFileName() {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
									.append(ACTIONS_DIR)
									.append(File.separatorChar)
									.append(currentMotionName)
									.append(File.separatorChar)
									.append(currentMotionName)
									.append(".seq");
		return waveFile.toString();
	}

	public void dumpConfig() {
		configReader.dumpConfig();
		movementRecorder.listTrackEnabled();
	}

	public void playWaveFile(String waveName) {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
				.append(ACTIONS_DIR)
				.append(File.separatorChar)
				.append(waveName)
				.append(File.separatorChar)
				.append(waveName)
				.append(".wav");
		waveService.playWave(waveFile.toString());
	}


	public void stopDragon() {
	}

	public void toggleTrackRecordng(int servo) {
		movementRecorder.toggleEnabledForRecording(servo);
	}
}
