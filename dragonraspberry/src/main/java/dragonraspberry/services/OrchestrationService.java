package dragonraspberry.services;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import dragonraspberry.connector.DragonFileDB;
import dragonraspberry.pojo.DragonEvent;
import dragonraspberry.pojo.Globals;
import dragonraspberry.pojo.MovementPlayer;


// The core of the robot
// All actions are coordinated from this class

public class OrchestrationService {

	private Logger log=Logger.getLogger(this.getClass().getSimpleName());	
	private MovementPlayer movementPlayer			= new MovementPlayer();
	private TimerService timerService				= TimerService.getInstance();
	private I2CService i2cService					= new I2CService();
	private static OrchestrationService INSTANCE	= new OrchestrationService();
	private String currentAction;
	private DragonFileDB dragonFileDB = DragonFileDB.getInstance();
	
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
		selectNewMovement();
		
		// Voeg een actie aan de timer toe
		timerService.addOnTimerEvent(new DragonEvent() {
			@Override
			public void handle(String msgFromTimer, int step, int val2) {
				try {
					i2cService.writeAllLeds(movementPlayer.getServoValuesFromStep(step));
					if(movementPlayer.isRunning() == false)
						{selectNewMovement();
						timerService.stepReset();
						}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			
		});
		
		timerService.stepReset();
	}
	
	
	public void stopAll() {
		timerService.stopService();
	}



	public void totalReset() {
		try {
			i2cService.reset();
		} catch (IOException e) {
			log.error("Reset operation failed");
			e.printStackTrace();
		}
	}


	private void selectNewMovement() {
		
		currentAction = dragonFileDB.selectRandomAction("male");
		log.info("New selected action "+currentAction);
		movementPlayer.openNewSequence(Globals.selectRootDir()+File.separator+currentAction+File.separator+currentAction+".seq");
	}
}
