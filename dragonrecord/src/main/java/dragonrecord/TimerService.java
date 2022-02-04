package dragonrecord;

import dragonrecord.config.ConfigReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TimerService implements Runnable{

	private Logger log = Logger.getLogger(TimerService.class);
	private boolean running=true;
	private int interval=20;
	private int step;
	private long starttime;
	private static final TimerService INSTANCE=new TimerService();
	private List<DragonEvent> eventHandlersList=new ArrayList<>();		// Lijst met eventhandlers
	
	
	private TimerService()
	{
		if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
		log.info("Make TimerService");
		this.startTimer();
	}
	
	
	public static TimerService getInstance()
	{
		return INSTANCE;
	}
	
	
	public void startTimer()
	{
		log.info("Start TimerService");
		Thread thisThread=new Thread(this);
		thisThread.start();
	}
	

	// HEt timer loop
	@Override
	public void run() {
		log.info("Start the timer");
		
		long oldtime		= 0;									// laatste keer dat er een puls werdt gegeven
		starttime		 	= System.currentTimeMillis();		// Nu in  milliseconds
		int difftime	 	= 0;									// aantal milisecondens dat we lopen
		step			 	= 0;											// Aantals pulsen 
		long currentTime 	= 0;						// De tijd NU
		while(running)
			{
				currentTime=System.currentTimeMillis();	
				if((currentTime-oldtime)>interval)						// Meer dan Interval voorbij?
				{
					for(DragonEvent dragonEvent:eventHandlersList)	// Genereer events
					{
						try {
							dragonEvent.handle("timer", step,0);
						} catch (InterruptedException e) {
							e.printStackTrace();
							Thread.currentThread().interrupt();
						} catch (DragonException e) {
							e.printStackTrace();
							log.error(e.getMessage());
						} catch (IOException e) {
							e.printStackTrace();
							log.error(e.getMessage());
						}
					}
					oldtime=currentTime;							// Reset de laatste keer
					difftime=(int) (currentTime-starttime);			// Maak het verschil
					step=difftime / interval;						// Bepaal het aantal stepds
				}
				
				try {
					Thread.sleep(0, 500);
				} catch (InterruptedException e) {
					log.fatal("Error in Timerthread");
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		log.info("TimerThread is stopped");
		
	}
	
	
	public void addOnTimerEvent(DragonEvent event)
	{
		log.info("Add timerListener from "+event);
		eventHandlersList.add(event);
	}
	
	
	public void stepReset()
	{
		log.info("Timer reset requested");
		step=0;
		starttime=System.currentTimeMillis();
	}
	
	public void stopService()
	{
		log.info("Try to stop the Timer Service");
		running=false;
	}

	public void setTimeStep(int timeStep) {
		interval=timeStep;
		log.info("interval is set to "+interval+" ms");
	}
}
