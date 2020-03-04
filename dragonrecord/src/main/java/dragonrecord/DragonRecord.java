package dragonrecord;

import org.apache.log4j.Logger;





public class DragonRecord {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	private UDPNetworkService udpNetworkService;
	private OrchestrationService orchestrationService;
	private TCPPNetworkService tcpPNetworkService;
	private TimerService timerService;
	
	private boolean RUNNING=true;
	
	public static void main(String[] argv)
	{
		DragonRecord dragonRecord=new DragonRecord();
		dragonRecord.init();
		dragonRecord.run();
	}

	
	
	
	
	public void run()
	{
		while(RUNNING)
		{
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		udpNetworkService.stop();
		tcpPNetworkService.stop();
		timerService.stopService();
	}
	
	
	public void init()  {
		log.info("Init Dragon Recorder");

		timerService=TimerService.getInstance();
		timerService.startTimer();
		
		udpNetworkService = new UDPNetworkService();
		udpNetworkService.startUDPNetworkService();

		tcpPNetworkService = new TCPPNetworkService();
		tcpPNetworkService.startTCPPNetworkService();
		
		orchestrationService = OrchestrationService.GetInstance();
		
	}
	
}
