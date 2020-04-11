package dragonrecord;

import org.apache.log4j.Logger;


public class DragonRecord {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private UDPNetworkService udpNetworkService;
	private OrchestrationService orchestrationService;
	private TimerService timerService;
	private boolean RUNNING=true;
	
	public static void main(String[] argv)
	{
		DragonRecord dragonRecord=new DragonRecord();
		dragonRecord.init();
		dragonRecord.run();
	}

	
	public void run() {
		try {
			while (RUNNING) {Thread.sleep(50000);}
			udpNetworkService.stop();
			timerService.stopService();
		} catch (InterruptedException e) {
			log.fatal("Error in Sleep Thread");
			e.printStackTrace();
		}
	}
	
	public void init()  {
		log.info("Init Dragon Recorder");
		timerService=TimerService.getInstance();
		timerService.startTimer();
		udpNetworkService = new UDPNetworkService();
		udpNetworkService.startUDPNetworkService();
		orchestrationService = OrchestrationService.GetInstance();
	}
	
}
