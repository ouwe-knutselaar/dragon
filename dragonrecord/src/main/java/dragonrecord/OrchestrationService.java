package dragonrecord;

import org.apache.log4j.Logger;

public class OrchestrationService {

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	private static OrchestrationService INSTANCE	= new OrchestrationService();
	private TimerService timerService;
	private boolean recording=false;

	private MovementRecorder movementRecorder =new MovementRecorder();
	I2CService i2cService = new I2CService();
	
	
	private OrchestrationService()
	{
		log.info("Init Orchestration service");
		i2cService.init(50);
		
		timerService=TimerService.getInstance();
	}
	
	
	/**
	 * This is a singleton
	 * @return
	 */
	public static OrchestrationService GetInstance()
	{
		return INSTANCE;
	}

	public void startTrackRecording(int parseInt) 
	{
		timerService.stepReset();
		recording=true;
	}

	public void stopTrackRecording() {
		
	}

	public void totalReset() {
		movementRecorder.reset();
	}

	public void writeCurrentMotion() {
		
	}

	public void runCurrentMotion() {
		// TODO Auto-generated method stub
		
	}

	public void setSingleServo(int servo, int servoValue) {
		
		if(recording)movementRecorder.record(servo,servoValue,timerService.getStep());
	}
	
}
