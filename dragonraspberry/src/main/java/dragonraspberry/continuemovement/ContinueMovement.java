package dragonraspberry.continuemovement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.log4j.Logger;
import dragonraspberry.connector.DragonFileDB;
import dragonraspberry.pojo.Motion;
import dragonraspberry.services.TimerService;


public class ContinueMovement {

	private Logger log 							  	= Logger.getLogger(this.getClass().getSimpleName());
	private Random rand 						  	= new Random();
	private DragonFileDB dragonFileDB 				= DragonFileDB.getInstance();
	private List<String> __motionNamesList			= null;
	private Motion __currentMotion					= new Motion("none", "none");
	//private Map<String, Motion> __motionList 		= new HashMap<>(); // Basic indexed lijst met motion names
	private TimerService timerService				= TimerService.getInstance();
	private boolean makePause						= false;

	public ContinueMovement() {
		log.info("Make the ContinueMovement");
		// load the motions in memory
		
		log.info("Start with motion:"+__currentMotion.getSeqFileName());
	}

	
	public int[] nextStep(int stepFromTimer) {
		if(__currentMotion.isEndOfSequenceReached())
		{
			timerService.stepReset();
			__currentMotion.resetMotion();
			if(makePause)
				{
				 __currentMotion=new Motion("empty","empty");
				 __currentMotion.makeEmtpyMotion(100+rand.nextInt(400));
				 makePause=false;
				}
			else
				{
				 //__currentMotion=__motionList.get(__motionNamesList.get(rand.nextInt(__motionNamesList.size())));
				 makePause=true;
				 
				}
			log.info("Select motion named: "+__currentMotion.getSeqFileName());
		}
		return __currentMotion.getListFromStep(stepFromTimer);
	}

	
	public List<String> getMotionNameList() {
		return __motionNamesList;
	}



}
