package dragonrecord;

import org.apache.log4j.Logger;

public class MovementRecorder {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	private final int NUM_OF_SERVOS=16;
	private final int MAXSTEPS=300*50;			// 50 hz x 300 seconden
	private int tracklist[][];
	
	
	public MovementRecorder()
	{
		reset();
	}
	
	public void reset()
	{
		tracklist=new int[NUM_OF_SERVOS][MAXSTEPS];
	}

	
	public void record(int servo, int servoValue, int step) {
		
		if(step>MAXSTEPS)return;
		tracklist[servo][step]=servoValue;
	}
	
}
