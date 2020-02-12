package dragonraspberry;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import dragonraspberry.pojo.DragonEvent;
import dragonraspberry.services.OrchestrationService;
import dragonraspberry.services.TCPPNetworkService;
import dragonraspberry.services.UDPNetworkService;

public class DragonRaspberry {

	private Logger log = Logger.getLogger(DragonRaspberry.class);
	private UDPNetworkService udpNetworkService;
	private OrchestrationService orchestrationService;
	private TCPPNetworkService tcpPNetworkService;
	private static boolean running=true;

	public static void main(String[] args) throws IOException, UnsupportedBusNumberException {
		DragonRaspberry dragonRaspberry = new DragonRaspberry();
		dragonRaspberry.init();
		dragonRaspberry.blockAll();
		dragonRaspberry.stopAll();

	}

	public DragonRaspberry() {
	}

	public void testrun() {
		log.info("Start the Dragon");
		// orchestrationService.runRandomMotion();
		orchestrationService.runNamedMotion("alive");

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void blockAll()
	{
		while(running)
		{
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log.info("Dragon stoppped");
	}
	

	public void stopAll() {
		orchestrationService.stopAll();
		udpNetworkService.stop();
		tcpPNetworkService.stop();
		log.info("Dragon ended");
	}

	
	public static void endDragon()
	{
		running=false;
	}
	
	public void init()  {
		log.info("Init Dragon for the Raspberry PI");

		
		
		orchestrationService = OrchestrationService.GetInstance();

		udpNetworkService = new UDPNetworkService();
		udpNetworkService.startUDPNetworkService();
		udpNetworkService.onNetworkEvent(new DragonEvent() {
			@Override
			public void handle(String msg, int val1, int val2) {
				log.info("Event received " + msg + " " + val1 + " " + val2);
			}
		});

		tcpPNetworkService = new TCPPNetworkService();
		tcpPNetworkService.startTCPPNetworkService();
		
	}

}
