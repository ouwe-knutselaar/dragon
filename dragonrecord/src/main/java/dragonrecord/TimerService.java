package dragonrecord;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


public class TimerService implements Runnable{

	private Logger log = Logger.getLogger(TimerService.class);
	private boolean running=true;
	private int interval=20;
	private int step;
	private long starttime;
	private static TimerService INSTANCE=new TimerService();
	List<DragonEvent> eventHandlersList=new ArrayList<>();		// Lijst met eventhandlers
	
	
	private TimerService()
	{
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
					for(DragonEvent handler:eventHandlersList)	// Genereer events
					{
						handler.handle("timer", step,0);
					}
					oldtime=currentTime;								// Reset de laatste keer
					difftime=(int) (currentTime-starttime);			// Maak het verschil
					step=difftime / interval;					// Bepaal het aantal stepds
					//log.debug("Timer "+oldtime+" \tdifftime "+difftime+"\tstep "+step);
				}
				
				try {
					Thread.sleep(0, 500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		log.info("Timer reset reqested");
		step=0;
		starttime=System.currentTimeMillis();
	}
	
	public void stopService()
	{
		log.info("Try to stop the Timer Service");
		running=false;
	}


	public int getStep() {
		// TODO Auto-generated method stub
		return step;
	}

}
