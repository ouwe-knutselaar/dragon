package dragonraspberry.services;

import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import dragonraspberry.continuemovement.ContinueMovement;
import dragonraspberry.pojo.DragonEvent;
import dragonraspberry.pojo.Globals;


// The core of the robot
// All actions are coordinated from this class

public class OrchestrationService {

	private Logger log=Logger.getLogger(this.getClass().getSimpleName());
	
	private boolean __normalOperation	= true;		// enable or disable normal operation
	
	// For normal operation
	private ContinueMovement __continueMovement 	= new ContinueMovement();
	
	// The services that the motionserivce needs
	private TimerService timerService				= TimerService.getInstance();
	private I2CService i2cService					= new I2CService();
	private static OrchestrationService INSTANCE	= new OrchestrationService();
	
	/**
	 * This is a singleton
	 * @return
	 */
	public static OrchestrationService GetInstance()
	{
		return INSTANCE;
	}
	
	
	private OrchestrationService() {
		log.info("Make the OrchestrationService");

		i2cService.init(Globals.servoFrequency);			

		// Voeg een actie aan de timer toe
		timerService.addOnTimerEvent(new DragonEvent() {
			@Override
			public void handle(String msgFromTimer, int stepFromTimer, int val2) {
				try {
					
					if(__normalOperation)
					{
						i2cService.writeLedString(__continueMovement.nextStep());
					}
					return;
				} catch (IllegalArgumentException e) {
					// log.debug("time gives value that is out of bound "+val1);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		});
		
		
	}
		
	
	/**
	 * Run an action based on an motion names
	 * @param name
	 */
	public void runNamedMotion(String name)
	{
		 __continueMovement.setCurrentMotionFromName(name);
		 __continueMovement.runCurrentMotion();
	}
	
	
	/**
	 * Het a list of motion names
	 * @return
	 */
	public List<String> getMotionNameList()
	{
		return __continueMovement.getMotionNameList();
	}

	
	/**
	 * Write a value to a single Led of Servo
	 * @param ledNumber
	 * @param servoValue
	 * @throws IOException
	 */
	public void setSingleServo(int servoNumber, int servoValue) throws IOException {
		log.debug("Write single server/led :"+servoNumber+" with "+servoValue);
		i2cService.writeSingleLed(servoNumber, servoValue);
	}

	


	public void setCurrentMotion(String motionName) {
		__continueMovement.setCurrentMotionFromName(motionName);
		log.info("Current motion set to "+motionName);
	}





	public void dumpCurrentMotion() {
		__continueMovement.getCurrentMotion().dumpMotion();
	}


	public void runCurrentMotion() {
		__continueMovement.runCurrentMotion();
	}


	public void stopAll() {
		timerService.stopService();
	}


	public void pauseAllActivities() {
		__normalOperation=false;
		log.info("Pause all activites");
	}


	public void operateNormal() {
		__normalOperation=true;
		log.info("Operate normal");
	}


	public void totalReset() {
		__normalOperation=false;
		try {
			i2cService.reset();
		} catch (IOException e) {
			log.error("Reset operation failed");
			e.printStackTrace();
		}
		
	}



	
}
