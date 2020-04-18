package dragonrecord;

import java.io.File;
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
	private String currentActionName;
	
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
		waveService.playWave(getRecordingWaveName());
		timerService.stepReset();
		movementRecorder.startRecording();
		recording=true;
		playing=true;
		log.info("Start recording of "+servo);
	}

	public void stopTrackRecording(int servo) {
		recording=false;
		playing=false;
		movementRecorder.stopRecording(servo);
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
		movementRecorder.writeSequenceFile(getSequenceFileName());
	}

	public void createNewRecording(String recordingName) throws IOException {
		recordingName=recordingName.trim();
		log.info("Set on recording named '"+recordingName+"'");
		currentActionName=recordingName;
		movementRecorder.openNewSequence(getSequenceFileName());
	}

	public void executeCurrentMotion() {
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
			log.info("Reqeust to deliver the list of actions");
			DatagramSocket clientSocket = new DatagramSocket();
			String dirlist = xferServer.getSemiColonSeparatedDirectoryListing(getActionsDir());
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
	
	
	public String selectRootDir() {
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.contains("win"))return "D:\\erwin\\dragon\\";
		if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))return "/var/dragon/";
		return "unknown";
	}
	
	
	private String getActionsDir()
	{
		StringBuilder actionDir=new StringBuilder(selectRootDir())
								.append("actions");
		return actionDir.toString();
	}
	
	
	private String getRecordingWaveName() {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
									.append("actions")
									.append(File.separatorChar)
									.append(currentActionName)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(".wav");
		return waveFile.toString();
	}

	private String getSequenceFileName() {
		StringBuilder waveFile=new StringBuilder(selectRootDir())
									.append("actions")
									.append(File.separatorChar)
									.append(currentActionName)
									.append(File.separatorChar)
									.append(currentActionName)
									.append(".seq");
		return waveFile.toString();
	}

	public void setActionType(String actionTypename) {
		movementRecorder.setActionType(actionTypename);
		
	}
	
}
