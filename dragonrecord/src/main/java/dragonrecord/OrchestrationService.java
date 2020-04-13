package dragonrecord;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

public class OrchestrationService {

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private static OrchestrationService INSTANCE	= new OrchestrationService();
	private TimerService timerService;
	private boolean recording=false;
	private boolean playing=false;
	private MovementRecorder movementRecorder =new MovementRecorder();
	private I2CService i2cService = new I2CService();
	private WaveService waveService = WaveService.getInstance();
	FileXferServer xferServer = new FileXferServer();
	private int currentServo;
	private int currentServoValue;
	
	private OrchestrationService()
	{
		log.info("Init Orchestration service");
		i2cService.init(50);
		timerService=TimerService.getInstance();
		
		timerService.addOnTimerEvent(new DragonEvent(){
			@Override
			public void handle(String msg, int step, int val2) {
				if(recording)movementRecorder.record(currentServo,currentServoValue,step);
				if(playing)i2cService.writeAllServos(movementRecorder.getServoValuesFromStep(step));
			}});
	}
	
	public static OrchestrationService GetInstance()
	{
		return INSTANCE;
	}

	public void startTrackRecording(int servo) 
	{
		waveService.playWave(movementRecorder.getRecordingWaveName());
		timerService.stepReset();
		recording=true;
		playing=true;
		log.info("Start recording of "+servo);
	}

	public void stopTrackRecording() {
		recording=false;
		playing=false;
		log.info("Stop recording at "+movementRecorder.getLastStep());
	}

	public void totalReset() {
		log.info("Reset current recording");
		movementRecorder.reset();
		i2cService.reset();
	}

	public void writeCurrentMotion() {
		log.info("Write current motion");
	}

	public void setSingleServo(int servo, int servoValue) throws IOException {
		i2cService.writeSingleLed(servo, servoValue);
		this.currentServo=servo;
		this.currentServoValue=servoValue;
	}

	public void dumpCurrentMotion() {
		log.info("dump current motion");
		System.out.println(movementRecorder);
	}

	public void saveCurrentMotion() throws IOException {
		log.info("Save current motion");
		movementRecorder.writeSequenceFile();
	}

	public void createNewRecording(String recordingName) throws IOException {
		log.info("Create new recording named "+recordingName+"-");
		movementRecorder.createNewSequence(recordingName);
	}

	public void executeCurrentMotion() {
		log.info("Play current motion");
		waveService.playWave(movementRecorder.getRecordingWaveName());
		timerService.stepReset();
		recording=false;
		playing=true;
	}

	public void receiveWaveFile(String waveName) {
		waveName=waveName.trim();
		String waveFile=String.format("%s%s\\%s.wav",movementRecorder.selectRootDir(),waveName,waveName);
		log.info("Receive file "+waveFile);
		xferServer.Serverloop(waveFile);
		log.info("File received");
	}

	
	public void sendActions(InetAddress inetAddress) {
		try {
			log.info("Reqeust to deliver the list of actions");
			DatagramSocket clientSocket = new DatagramSocket();
			String dirlist = xferServer.getSemiColonSeparatedDirectoryListing(movementRecorder.selectRootDir());
			DatagramPacket sendPacket = new DatagramPacket(dirlist.getBytes(), dirlist.length(), inetAddress, 3003);
			clientSocket.send(sendPacket);
			clientSocket.close();
			log.info("List of actions send");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void filterServo(int servo) {
		log.info("Run filter for servo "+servo);
		movementRecorder.filter(servo);
	}
	
}
