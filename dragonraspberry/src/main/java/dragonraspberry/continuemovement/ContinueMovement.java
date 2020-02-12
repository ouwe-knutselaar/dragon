package dragonraspberry.continuemovement;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import dragonraspberry.connector.DragonFileConnector;
import dragonraspberry.pojo.Globals;
import dragonraspberry.pojo.Motion;


public class ContinueMovement {

	private Logger log 							  	= Logger.getLogger(this.getClass().getSimpleName());
	private Random rand 						  	= new Random();
	private MovementArray[] __MovementArrayList   	= new MovementArray[Globals.numberOfServos];
	private int[] __currentValueOfServos 		  	= new int[Globals.numberOfServos];
	private int __untilNextMotion				  	= 0;
	private DragonFileConnector dragonFileService 	= DragonFileConnector.getInstance();
	private List<String> __motionNamesList			= dragonFileService.getActionList();	 // Lijst met motion names
	private boolean isNormalOperating 				= true;
	private boolean isExecutingMotion 				= false;
	private Motion __currentMotion					= new Motion("none", "none");
	private Map<String, Motion> __motionList 		= new HashMap<>(); // Basic indexed lijst met motion names

	public ContinueMovement() {
		log.info("Make the ContinueMovement");
		__untilNextMotion = 100 + rand.nextInt(250); // Standaard de eerste beweging tussen de 2 en 7 seconden
		
		// Make 16 new movement arrays
		for (int tel = 0; tel < 16; tel++)__MovementArrayList[tel] = new MovementArray(0, 0, 0, tel);

		// load the motions in memory
		for (String actionName : __motionNamesList) {
			Motion tempMotion = new Motion(actionName, dragonFileService.getWaveFileName(actionName));	// Maak een nieuw motion object
			tempMotion.parseSequenceFile(dragonFileService.getServoStepsAsList(actionName));			// laad de waardes uit de sequence file
			__motionList.put(actionName, tempMotion);													// Zet de motion in de list
			__currentMotion=tempMotion;																	// De laatste is altijd de current motion
		}
		
		log.info("Added " + __motionList.size() + " motions to the service");
	}

	
	
	public int[] nextStep() {
		if (isNormalOperating)
			return nextNormalOperatingStep();
		if (isExecutingMotion)
			return nextExecutingMotionStep();
		return new int[Globals.numberOfServos];
	}

	
	public List<String> getMotionNameList() {
		return __motionNamesList;
	}

	public void setCurrentMotionFromName(String motionName) {
		__currentMotion = __motionList.get(motionName);
	}

	public void runCurrentMotion() {
		isExecutingMotion = true;
		isNormalOperating = false;
	}


	private int[] nextExecutingMotionStep() {
		return new int[16];
	}

	public void writeCurrentMotion() {
		try {
			dragonFileService.writeSequenceFile(__currentMotion);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public Motion getCurrentMotion()
	{
		return __currentMotion;
	}
	

	public void updateServoListInCurrentMotion(int recordedServo, int[] valueList) {
		__currentMotion.updateValueListForServo(recordedServo, valueList);
	}

	// ************************************** private functions

	private int[] nextNormalOperatingStep() {
		__untilNextMotion--;
		if (__untilNextMotion < 0)
			setNextNewAction();

		for (int tel = 0; tel < Globals.numberOfServos; tel++) {
			__currentValueOfServos[tel] = __MovementArrayList[tel].getNext();
			//log.debug(__currentValueOfServos[tel] + "  ");
		}
		//log.debug(System.lineSeparator());
		return __currentValueOfServos;
	}

	private void setNextNewAction() {
		int selectedServoToMove = rand.nextInt(16);
		int startPosition = __MovementArrayList[selectedServoToMove].getNext();
		__MovementArrayList[selectedServoToMove] = new MovementArray(rand.nextInt(500), 
																	 startPosition,
																	 rand.nextInt(600), 
																	 selectedServoToMove);
		__untilNextMotion = 500 + rand.nextInt(250);
	}

}
