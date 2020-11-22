package dragonraspberry.pojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;

public class MovementPlayer {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private final int NUM_OF_SERVOS=16;
	private final int MAXSTEPS=300*50;					// 50 hz x 300 seconden
	private int tracklist[][];							//[servo][step]
	private int laststep=0;
	private boolean running = false;

	
	public MovementPlayer()
	{
		log.info("Init MovementPlayer()");
	}
	


	public int getLastStep()
	{
		return laststep;
	}
		

	public void openNewSequence(String sequenceFileName) 
	{
		try{
		
		
		log.info("Read sequencefile "+sequenceFileName);
		List<String> sequenceLines = Files.readAllLines(Paths.get(sequenceFileName));
		if(sequenceLines.size()==0)
		{
			log.info("Empty file");
			return;
		}
		sequenceLines.get(0);					// chop the action type
		tracklist=new int[NUM_OF_SERVOS][MAXSTEPS];
		for (int lines=1;lines < sequenceLines.size();lines++) {
			//log.debug("Parse " + line);
			
			for (int tel = 0; tel < NUM_OF_SERVOS; tel++) {
				tracklist[tel][lines] = Integer.parseInt(sequenceLines.get(lines).substring((tel * 4), 4 + (tel * 4)));
			}
		}
		laststep = sequenceLines.size();
		log.info("Parsed action file of " + laststep + " steps");
		running = true;
		}catch( IOException e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	
	public int[] getServoValuesFromStep(int step)
	{
		int result[]=new int[NUM_OF_SERVOS];
		if(running == false ) return result;
		if(step>laststep)
			{
			 running = false;
			 log.info("Action ended");
			 return result;
			}
		for(int tel=0;tel<NUM_OF_SERVOS;tel++)result[tel]=tracklist[tel][step];
		//log.info("Execute step :"+step);
		return result;
	}
	
	

	public boolean isRunning()
	{
		return running;
	}


}
