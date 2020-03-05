package dragonrecord;

import org.apache.log4j.Logger;

public class MovementRecorder {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	private final int NUM_OF_SERVOS=16;
	private final int MAXSTEPS=300*50;			// 50 hz x 300 seconden
	private int tracklist[][];
	private int laststep=0;
	
	
	public MovementRecorder()
	{
		log.info("Init MovementRecorder()");
		reset();
	}
	
	public void reset()
	{
		log.info("Reset the recorder to "+MAXSTEPS+" steps");
		tracklist=new int[NUM_OF_SERVOS][MAXSTEPS];
		laststep=0;
	}

	
	public void record(int servo, int servoValue, int step) {
		if(step>MAXSTEPS)return;
		tracklist[servo][step]=servoValue;
		laststep=Math.max(step, laststep);
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder record=new StringBuilder();
		for(int stepcount=0;stepcount<laststep;stepcount++)
		{
			for(int servocount=0;servocount<NUM_OF_SERVOS;servocount++)
			{
				record.append(tracklist[servocount][stepcount]).append('\t');
			}
			record.append(System.lineSeparator());
		}
		
		return record.toString();
	}


	
}
