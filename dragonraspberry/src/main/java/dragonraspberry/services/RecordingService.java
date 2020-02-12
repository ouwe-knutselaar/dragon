package dragonraspberry.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import dragonraspberry.pojo.DisplayTools;
import dragonraspberry.pojo.DragonEvent;

public class RecordingService {
	
	private Logger log = Logger.getLogger(RecordingService.class);
	private int 	__recordings[];				// All the recordings of the track
	private boolean __received[];				// All the steps if they are recorded
	private int 	__steps=100;				// number of steps to record
	
	private List<DragonEvent> eventHandlersList=new ArrayList<>();	
		
	
	public  RecordingService()
	{
		log.info("Make RecordingService");
		makeNewRecordingTrask(100);
	}
	
	
	/**
	 * Make a new recording track
	 * @param steps
	 */
	public void makeNewRecordingTrask(int steps)
	{
		log.info("Make new Track of " + steps + " steps");
		__steps=steps;						// Zet het aantal steps
		__recordings=new int[steps];		// Maak een nieuwe track
		__received=new boolean[steps];		// Maak ee =n nieuwe controle track
	}
	
	
	/**
	 * Record a new value at a certain step
	 * @param step
	 * @param value
	 */
	public void setValue(int step, int value)
	{
		if(step >=__steps)
		{
			eventHandlersList.forEach(event -> event.handle("recevent", 0, 0));
			return;
		}
		log.debug("Write value "+value+" at "+step);
		__recordings[step]=value;
		__received[step]=true;
	}
	
	
	public int getValue(int step)
	{
		return __recordings[step];
	}
	
	
	public int[] getValueList()
	{
		return __recordings;
	}
	
	
	public void correctNotReceived() {
		for(int tel=0;tel<__steps-1;tel++)
		{
			if(!__received[tel])
			{
				if(tel==0)
				{
					__recordings[0]=__recordings[1];
					continue;
				}
				__recordings[tel]=(__recordings[tel+1]+__recordings[tel-1])/2;
			}
		}
	}
	
	
	public String dumpRecordedTrack()
	{
		return DisplayTools.ArrayToHexString(__recordings);
	}

	public void addOnRecordingEvent(DragonEvent event)
	{
		log.info("Add timerListener from "+event);
		eventHandlersList.add(event);
	}
	

}
