package dragonrecord;

import dragonrecord.movement.MovementCoordinator;
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
	private static final TimerService timerService =TimerService.getInstance();
	private boolean recording=false;
	private boolean playing=false;
	private boolean moving=false;
	private final MovementRecorder movementRecorder =new MovementRecorder();
	private final WaveService waveService = WaveService.getInstance();
	private final RandomMovementService randomMovementService = RandomMovementService.getInstance();
	private final ConfigReader configReader = ConfigReader.getInstance();
	private final String ACTIONS_DIR="actions";
	private String currentMotionName;
	private MovementCoordinator movementCoordinator;
	
	private OrchestrationService() {
		if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
		movementCoordinator = new MovementCoordinator();
		log.info("Init Orchestration service");

		
		timerService.addOnTimerEvent(new DragonEvent(){
			@Override
			public void handle(String msg, int step, int val2) throws DragonException {
				/*if(recording) {
					movementRecorder.record(currentServo, currentServoValue, step);
					i2cService.writeSingleLed(currentServo,currentServoValue);
				}
				if(playing){
					int[] servosteps = movementRecorder.getServoValuesFromStep(step);
					if(servosteps[0] != -1)i2cService.writeAllServos(servosteps);
					else{
						log.info("Current motion ended");
						stopAll();
					}
				}
				if(moving)randomMovementService.nextStep(); */

				movementCoordinator.processServoStepsList();
			}});


	}
	
	public static OrchestrationService getInstance() {
		if(classInstance == null) {
			classInstance = new OrchestrationService();
		}
		return classInstance;
	}

	public void startTrackRecording(int servo) {
		waveService.playWave(getRecordingWaveName());
		timerService.stepReset();
		movementRecorder.startRecording();
		recording=true;
		playing=true;
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

	public void stopAll() {
		recording = false;
		playing = false;
		moving = false;
		//i2cService.reset();
		/*int valueList[]=new int[16];
		for(int i = 0 ; i<16;i++){
			valueList[i] = configReader.getServoDefaultValue(i);
		}
		i2cService.writeAllServos(valueList);*/
		movementCoordinator.allToDefault();
		log.info("Stop all activities");
	}

	public void stopRandomMoving() {
		log.info("Stop random moving");
		randomMovementService.stopRandomMovement();
	}

	public void totalReset() {
		log.info("Reset current recording");
		movementRecorder.reset();
		movementCoordinator.fullReset();
	}

	public void setSingleServo(int servo, int servoValue) {
		log.debug("Command to set servo "+servo+" to relative position "+servoValue);
		movementCoordinator.goToNewValue(servo,servoValue);
	}

	public void dumpCurrentMotion() {
		log.info("dump current motion " + currentMotionName);
		log.info(System.lineSeparator()+movementRecorder);
	}

	public void saveCurrentMotion(String actionType) throws IOException {
		log.info("Save current motion");
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
		waveService.playWave(getRecordingWaveName());
		timerService.stepReset();
		recording=false;
		playing=true;
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


}
