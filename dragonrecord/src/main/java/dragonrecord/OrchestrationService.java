package dragonrecord;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class OrchestrationService {

	private final Logger log = Logger.getLogger(OrchestrationService.class.getSimpleName());
	private static OrchestrationService classInstance;
	private final TimerService timerService =TimerService.getInstance();
	private boolean recording=false;
	private boolean playing=false;
	private boolean moving=false;
	private final MovementRecorder movementRecorder =new MovementRecorder();
	private final I2CService i2cService = new I2CService();
	private final WaveService waveService = WaveService.getInstance();
	private final RandomMovementService randomMovementService = RandomMovementService.getInstance();
	private final FileXferServer xferServer = new FileXferServer();
	private final ConfigReader configReader = ConfigReader.getInstance();
	private final String ACTIONS_DIR="actions";
	private int currentServo;
	private int currentServoValue;
	private String currentActionName;
	private DatagramSocket clientSocket;
	
	private OrchestrationService() throws SocketException {
		if(ConfigReader.isDebug())log.setLevel(Level.DEBUG);
		log.info("Init Orchestration service");
		i2cService.init(50);
		clientSocket=new DatagramSocket();
		
		timerService.addOnTimerEvent(new DragonEvent(){
			@Override
			public void handle(String msg, int step, int val2) throws InterruptedException, DragonException, IOException {
				if(recording) {
					movementRecorder.record(currentServo, currentServoValue, step);
<<<<<<< HEAD
					i2cService.writeSingleLed(currentServo,currentServoValue);					
=======
					i2cService.writeAllServos(movementRecorder.getServoValuesFromStep(step));
>>>>>>> 0458ed1666ae2ccf061c0adaefb839f3cf0059a1
				}
				if(playing)i2cService.writeAllServos(movementRecorder.getServoValuesFromStep(step));
				if(moving)randomMovementService.nextStep();
				if(!moving & !playing & !recording)i2cService.writeSingleLed(currentServo,currentServoValue);
			}});
	}
	
	public static OrchestrationService getInstance() {
		if(classInstance == null) {
			try {
				classInstance = new OrchestrationService();
			} catch (SocketException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return classInstance;
	}

	public void startTrackRecording(int servo) {
		waveService.playWave(getRecordingWaveName());
		timerService.stepReset();
		movementRecorder.startRecording();
		recording=true;
		playing=false;
		moving = false;
		log.info("Start recording of "+servo);
	}

	public void stopTrackRecording(int servo) {
		recording=false;
		playing=false;
		moving = false;
		movementRecorder.stopRecording(servo);
		log.info("Stop recording at "+movementRecorder.getLastStep());
	}

	public void startRandomMoving(){
		recording = false;
		playing = false;
		moving = true;
		log.info("Switch to random movements");
	}

	public void executeCurrentMotion() {
		log.info("Play current motion");
		waveService.playWave(getRecordingWaveName());
		timerService.stepReset();
		recording=false;
		playing=true;
		moving=false;
	}

	public void stopAll() {
		recording = false;
		playing = false;
		moving = false;
		i2cService.reset();
		log.info("Stop all activities");
	}

	public void stopRandomMoving() {
		log.info("Stop random moving");
		randomMovementService.stopRandomMovement();
	}

	public void totalReset() {
		log.info("Reset current recording");
		movementRecorder.reset();
		i2cService.reset();
	}

	public void writeCurrentMotion() {
		log.info("Write current motion");
	}

	public void setSingleServo(int servo, int servoValue) {
		currentServo=servo;
		currentServoValue=servoValue;
	}

	public void dumpCurrentMotion() {
		log.info("dump current motion");
		log.info(System.lineSeparator()+movementRecorder);
	}

	public void saveCurrentMotion(String actionType) throws IOException {
		log.info("Save current motion");
		movementRecorder.writeSequenceFile(getSequenceFileName(),actionType);
	}

	public void createNewRecording(String recordingName) throws IOException {
		recordingName=recordingName.trim();
		log.info("Set on recording named '"+recordingName+"'");
		currentActionName=recordingName;
		movementRecorder.openNewSequence(getSequenceFileName());
	}



	public void receiveWaveFile(String waveName) {
		waveName=waveName.trim();
		String waveFile=String.format("%s\\actions\\%s\\%s.wav",selectRootDir(),waveName,waveName);
		log.info("Receive file "+waveFile);
		xferServer.serverloop(waveFile);
		log.info("File received");
	}

	public void sendActions(InetAddress inetAddress) {
		try {
			log.info("Reqeust to deliver the list of actions");
			String dirlist = xferServer.getSemiColonSeparatedDirectoryListing(getActionsDir());
			DatagramPacket sendPacket = new DatagramPacket(dirlist.getBytes(), dirlist.length(), inetAddress, 3003);
			clientSocket.send(sendPacket);
			log.info("List of actions send");
		} catch (SocketException e) {
			log.error("Socket opening issue "+e.getMessage());
		} catch (IOException e) {
			log.error("IO exception "+e.getMessage());
		}
	}

	public void sendServoValues(InetAddress inetAddress) {
		try {

			log.info("Reqeust to deliver the list of servo values");
			String servolist = configReader.getSemiColonSeparatedServoValuesListing();
			DatagramPacket sendPacket = new DatagramPacket(servolist.getBytes(), servolist.length(), inetAddress, 3003);
			clientSocket.send(sendPacket);
			log.info("List of servo's send");
			log.info(servolist);
		} catch (SocketException e) {
			log.error("Socket opening issue "+e.getMessage());
		} catch (IOException e) {
			log.error("IO exception "+e.getMessage());
		}
	}

	public void filterServo(int servo) {
		log.info("Run smooth filter for servo "+servo);
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

	private String getRecordingWaveName() {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
									.append(ACTIONS_DIR)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(".wav");
		return waveFile.toString();
	}

	private String getSequenceFileName() {
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

	public void cleanCurrentServoTrack() {
		log.info("clear servo track "+currentServo);
		movementRecorder.servoTrackClean(currentServo);
	}
}
