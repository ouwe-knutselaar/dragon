package dragonraspberry.continuemovement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import dragonraspberry.connector.DragonFileConnector;
import dragonraspberry.pojo.Motion;
import dragonraspberry.services.TimerService;


public class ContinueMovement {

	private Logger log 							  	= Logger.getLogger(this.getClass().getSimpleName());
	private Random rand 						  	= new Random();
	private DragonFileConnector dragonFileService 	= DragonFileConnector.getInstance();
	private List<String> __motionNamesList			= null;
	private Motion __currentMotion					= new Motion("none", "none");
	private Map<String, Motion> __motionList 		= new HashMap<>(); // Basic indexed lijst met motion names
	private TimerService timerService				= TimerService.getInstance();

	public ContinueMovement() {
		log.info("Make the ContinueMovement");
		// load the motions in memory
		 __motionNamesList=dragonFileService.getActionList();	 // Lijst met motion names
		for (String actionName : __motionNamesList) {
			Motion tempMotion = new Motion(actionName, dragonFileService.getWaveFileName(actionName));	// Maak een nieuw motion object
			tempMotion.parseSequenceFile(dragonFileService.getServoStepsAsList(actionName));			// laad de waardes uit de sequence file
			__motionList.put(actionName, tempMotion);													// Zet de motion in de list
			__currentMotion=tempMotion;																	// De laatste is altijd de current motion
		}
		log.info("Added " + __motionList.size() + " motions to the service");
		log.info("Start with motion:"+__currentMotion.getSeqFileName());
	}

	public int[] nextStep(int stepFromTimer) {
		if(__currentMotion.isEndOfSequenceReached())
		{
			timerService.stepReset();
			__currentMotion.resetMotion();
			__currentMotion=__motionList.get(__motionNamesList.get(rand.nextInt(__motionNamesList.size())));
			log.info("Select motion named: "+__currentMotion.getSeqFileName());
		}
		return __currentMotion.getListFromStep(stepFromTimer);
	}

	public List<String> getMotionNameList() {
		return __motionNamesList;
	}

	public void setCurrentMotionFromName(String motionName) {
		__currentMotion = __motionList.get(motionName);
	}

}
