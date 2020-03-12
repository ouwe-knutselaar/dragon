package dragonrecord;

import java.io.IOException;

import org.apache.log4j.Logger;

public class OrchestrationService {

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	private static OrchestrationService INSTANCE	= new OrchestrationService();
	private TimerService timerService;
	private boolean recording=false;

	private MovementRecorder movementRecorder =new MovementRecorder();
	private I2CService i2cService = new I2CService();
	
	private int currentServo;
	private int currentServoValue;
	
	private OrchestrationService()
	{
		log.info("Init Orchestration service");
		i2cService.init(50);
		timerService=TimerService.getInstance();
		timerService.addOnTimerEvent(new DragonEvent(){
			@Override
			public void handle(String msg, int val1, int val2) {
				if(recording)movementRecorder.record(currentServo,currentServoValue,val1);
			}});
	}
	
	
	/**
	 * This is a singleton
	 * @return
	 */
	public static OrchestrationService GetInstance()
	{
		return INSTANCE;
	}

	public void startTrackRecording(int servo) 
	{
		timerService.stepReset();
		recording=true;
		log.info("Start recording of "+servo);
	}

	public void stopTrackRecording() {
		recording=false;
		log.info("Stop recording");
	}

	public void totalReset() {
		movementRecorder.reset();
	}

	public void writeCurrentMotion() {
		log.info("Write current motion");
	}

	public void runCurrentMotion() {
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
		movementRecorder.writeSequenceFile();
	}
	
}
