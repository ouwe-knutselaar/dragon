package dragonrecord;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;

public class OrchestrationService {

	private static final Logger log = Logger.getLogger(OrchestrationService.class.getSimpleName());
	private static OrchestrationService classInstance;
	private static final TimerService timerService =TimerService.getInstance();
	private static boolean recording=false;
	private static boolean playing=false;
	private static boolean moving=false;
	private static final MovementRecorder movementRecorder =new MovementRecorder();
	private static final I2CService i2cService = new I2CService();
	private static final WaveService waveService = WaveService.getInstance();
	private static final RandomMovementService randomMovementService = RandomMovementService.getInstance();
	private static final FileXferServer xferServer = new FileXferServer();
	private static final ConfigReader configReader = ConfigReader.getInstance();
	private static final String ACTIONS_DIR="actions";
	private static int currentServo;
	private static int currentServoValue;
	private static String currentActionName;
	
	private OrchestrationService() {
		log.info("Init Orchestration service");
		i2cService.init(50);
		
		timerService.addOnTimerEvent(new DragonEvent(){
			@Override
			public void handle(String msg, int step, int val2) throws InterruptedException, DragonException, IOException {
				if(recording)movementRecorder.record(currentServo,currentServoValue,step);
				if(playing)i2cService.writeAllServos(movementRecorder.getServoValuesFromStep(step));
				if(moving)randomMovementService.nextStep();
			}});
	}
	
	public static OrchestrationService getInstance() {
		if(classInstance == null) {
			classInstance = new OrchestrationService();
		}
		return classInstance;
	}

	public static void startTrackRecording(int servo) {
		waveService.playWave(getRecordingWaveName());
		timerService.stepReset();
		movementRecorder.startRecording();
		recording=true;
		playing=true;
		moving = false;
		log.info("Start recording of "+servo);
	}

	public static void stopTrackRecording(int servo) {
		recording=false;
		playing=false;
		moving = false;
		movementRecorder.stopRecording(servo);
		log.info("Stop recording at "+movementRecorder.getLastStep());
	}

	public static void startRandomMoving(){
		recording = false;
		playing = false;
		moving = true;
		log.info("Switch to random movements");
	}

	public void stopAll() {
		recording = false;
		playing = false;
		moving = false;
		log.info("Stop all activities");
	}

	public static void stopRandomMoving() {
		log.info("Stop random moving");
		randomMovementService.stopRandomMovement();
	}

	public static void totalReset() {
		log.info("Reset current recording");
		movementRecorder.reset();
		i2cService.reset();
	}

	public static void writeCurrentMotion() {
		log.info("Write current motion");
	}

	public static void setSingleServo(int servo, int servoValue) {
		try {
			i2cService.writeSingleLed(servo, servoValue);
			currentServo=servo;
			currentServoValue=servoValue;
		} catch (DragonException e) {
			log.fatal("Value "+servoValue+" is invalid for servo "+servo);
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void dumpCurrentMotion() {
		log.info("dump current motion");
		log.info(movementRecorder);

	}

	public static void saveCurrentMotion(String actionType) throws IOException {
		log.info("Save current motion");
		movementRecorder.writeSequenceFile(getSequenceFileName(),actionType);
	}
	public static void createNewRecording(String recordingName) throws IOException {
		recordingName=recordingName.trim();
		log.info("Set on recording named '"+recordingName+"'");
		currentActionName=recordingName;
		movementRecorder.openNewSequence(getSequenceFileName());
	}

	public static void executeCurrentMotion() {
		log.info("Play current motion");
		waveService.playWave(getRecordingWaveName());
		timerService.stepReset();
		recording=false;
		playing=true;
	}


	public void receiveWaveFile(String waveName) {
		waveName=waveName.trim();
		String waveFile=String.format("%s\\actions\\%s\\%s.wav",selectRootDir(),waveName,waveName);
		log.info("Receive file "+waveFile);
		xferServer.Serverloop(waveFile);
		log.info("File received");
	}

	
	public void sendActions(InetAddress inetAddress) {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			log.info("Reqeust to deliver the list of actions");
			String dirlist = xferServer.getSemiColonSeparatedDirectoryListing(getActionsDir());
			DatagramPacket sendPacket = new DatagramPacket(dirlist.getBytes(), dirlist.length(), inetAddress, 3003);
			clientSocket.send(sendPacket);
			clientSocket.close();
			log.info("List of actions send");
		} catch (SocketException e) {
			log.error("Socket opening issue "+e.getMessage());
		} catch (IOException e) {
			log.error("IO exception "+e.getMessage());
		}
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
		movementRecorder.filter(servo);
	}
	
	
	public static String selectRootDir() {
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		if(operatingSystem.contains("win"))return "D:\\erwin\\dragon\\";
		if(operatingSystem.contains("nix") || operatingSystem.contains("nux") || operatingSystem.contains("aix"))return "/var/dragon/";
		return "unknown";
	}
	
	
	private String getActionsDir() {
		StringBuilder actionDir=new StringBuilder(selectRootDir())
								.append(ACTIONS_DIR);
		return actionDir.toString();
	}
	
	
	private static String getRecordingWaveName() {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
									.append(ACTIONS_DIR)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(".wav");
		return waveFile.toString();
	}

	private static String getSequenceFileName() {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
									.append(ACTIONS_DIR)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(".seq");
		return waveFile.toString();
	}


	public void dumpListOfAction() {
		log.info("List of actions");
		String dirlist = xferServer.getSemiColonSeparatedDirectoryListing(getActionsDir());
		String[] actions =dirlist.split(";");
		for (String action : actions) log.info(action);
	}

	public void dumpConfig() {
		configReader.dumpConfig();
	}


}
