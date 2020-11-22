package dragonraspberry;

import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import dragonraspberry.pojo.Globals;
import dragonraspberry.services.OrchestrationService;
import dragonraspberry.services.TCPPNetworkService;

public class DragonRaspberry {

	private Logger log = Logger.getLogger(DragonRaspberry.class);
	private OrchestrationService orchestrationService;
	private TCPPNetworkService tcpPNetworkService;
	private static boolean running=true;

	public static void main(String[] args) throws IOException, UnsupportedBusNumberException, ParseException {
		
		// First we parse the command line to override the defaults
		Options options = new Options();
		options.addOption("f", false, "Directory containing the properties and the actions");
		
		CommandLineParser parser = new DefaultParser();
		CommandLine line=parser.parse(options, args);
		if(line.hasOption("f"))Globals.baseDirectory=line.getOptionValue("f");
		
		
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

		tcpPNetworkService = new TCPPNetworkService();
		tcpPNetworkService.startTCPPNetworkService();
		
	}

}
