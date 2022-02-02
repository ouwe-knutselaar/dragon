package dragonrecord;

import dragonrecord.files.FileManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class DragonRecord {
	
	private static final Logger log = Logger.getLogger(DragonRecord.class.getSimpleName());
	private UDPNetworkService udpNetworkService;
	private TimerService timerService;
	private boolean running =true;
	private static String configfile= "config.yml";
	
	public static void main(String[] argv) throws InterruptedException {

		System.setProperty("java.library.path","..");
		System.setProperty("java.library.path","D:\\onze_projecten\\dragon\\dragonrecord");

		// parse arguments
		for(String arg: argv)
		{
			if(arg.startsWith("-config="))
			{
				configfile=arg.substring(8);
				log.info("config file to use is "+configfile);
			}
		}

		DragonRecord dragonRecord=new DragonRecord();
		dragonRecord.init();
		dragonRecord.run();
	}

	
	public void run() throws InterruptedException {
		while (running) {Thread.sleep(50000);}

	}
	
	public void init() throws InterruptedException {
		log.info("Init Dragon Recorder");



		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            udpNetworkService.stop();
            timerService.stopService();
        }));

		ConfigReader configReader = ConfigReader.getInstance();
		configReader.readConfiguration(configfile);
		if(configReader.isDebug())log.setLevel(Level.DEBUG);

		timerService=TimerService.getInstance();
		timerService.setTimeStep(configReader.getTimeStep());

		FileManager fileManager = new FileManager(configReader.getActionPath());



		udpNetworkService = new UDPNetworkService();
		udpNetworkService.startUDPNetworkService();

		KeyboardService keyboardService=new KeyboardService();
		keyboardService.startKeyBoardService();

		timerService.startTimer();
	}
	
}
