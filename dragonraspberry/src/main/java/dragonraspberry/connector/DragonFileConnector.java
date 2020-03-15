package dragonraspberry.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import dragonraspberry.pojo.Globals;
import dragonraspberry.pojo.Servo;
import dragonraspberry.services.WaveService;

public class DragonFileConnector {

	Logger log = Logger.getLogger(DragonFileConnector.class);

	private String rootDir="unknown";									// Rootdit van de acties
	private List<String> actionList = new ArrayList<>();				// Lijst met acties

	private static DragonFileConnector INSTANCE=new DragonFileConnector();
		
	
	private DragonFileConnector() {
		
		try {
			log.info("Make the DragonFileConnector");
			rootDir=__selectRootDir();
			log.info("read the default values");
			readTheDeafaultFromTheProperiesFileAndPutItInGlobals(rootDir);
			log.info("The root directory for the actions is "+rootDir);
			actionList=__ScanTheActionToTheActionList(this.rootDir);
			actionList.forEach(action -> __checkForSequenceFile(action));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}



	private void readTheDeafaultFromTheProperiesFileAndPutItInGlobals(String directory) throws IOException 
	{
		directory=directory+"/dragon.properties";
		InputStream input = new FileInputStream(directory);
		Properties prop = new Properties();    
		prop.load(input);
		
		Globals.servoLimitList[0]=new Servo(0,prop.getProperty("servo0", "none,0,0,0,0"));
		Globals.servoLimitList[1]=new Servo(1,prop.getProperty("servo1", "none,0,0,0,0"));
		Globals.servoLimitList[2]=new Servo(2,prop.getProperty("servo2", "none,0,0,0,0"));
		Globals.servoLimitList[3]=new Servo(3,prop.getProperty("servo3", "none,0,0,0,0"));
		Globals.servoLimitList[4]=new Servo(4,prop.getProperty("servo4", "none,0,0,0,0"));
		Globals.servoLimitList[5]=new Servo(5,prop.getProperty("servo5", "none,0,0,0,0"));
		Globals.servoLimitList[6]=new Servo(6,prop.getProperty("servo6", "none,0,0,0,0"));
		Globals.servoLimitList[7]=new Servo(7,prop.getProperty("servo7", "none,0,0,0,0"));
		Globals.servoLimitList[8]=new Servo(8,prop.getProperty("servo8", "none,0,0,0,0"));
		Globals.servoLimitList[9]=new Servo(9,prop.getProperty("servo9", "none,0,0,0,0"));
		Globals.servoLimitList[10]=new Servo(10,prop.getProperty("servo10", "none,0,0,0,0"));
		Globals.servoLimitList[11]=new Servo(11,prop.getProperty("servo11", "none,0,0,0,0"));
		Globals.servoLimitList[12]=new Servo(12,prop.getProperty("servo12", "none,0,0,0,0"));
		Globals.servoLimitList[13]=new Servo(13,prop.getProperty("servo13", "none,0,0,0,0"));
		Globals.servoLimitList[14]=new Servo(14,prop.getProperty("servo14", "none,0,0,0,0"));
		Globals.servoLimitList[15]=new Servo(15,prop.getProperty("servo15", "none,0,0,0,0"));
		
		for(Servo servo: Globals.servoLimitList)log.info(servo.toString());
	}



	public static DragonFileConnector getInstance()
	{
		return INSTANCE;
	}
	
	
	// Geef de lijst met Acties/Motions
	public List<String> getActionList() {
		return actionList;
	}

	
	public List<String> getServoStepsAsList(String actionName) {
		
		try {
			String fullActionFileName = rootDir + "/" + actionName + "/" + actionName+".seq";
			List<String> fileAsList=null;
			fileAsList = Files.readAllLines(Paths.get(fullActionFileName));
			return fileAsList;
			
		} catch (IOException e) {
			log.error("IO Exception "+e.getMessage());
			e.printStackTrace();
			System.exit(1);
			return null;
		}
		
	}
	
	
	public String getWaveFileName(String actionName){
		return  rootDir + "/" + actionName + "/" + actionName+".wav";
	}



	private List<String> __ScanTheActionToTheActionList(String rootDir) {
		List<String> templist=new ArrayList<>();;
		File rootDirHandle = new File(rootDir);
		for (File subFile : rootDirHandle.listFiles()) {
			if (subFile.isDirectory())templist.add(subFile.getName());
		}
		log.info("Loaded " + actionList.size() + " actions from " + rootDir);
		return templist;
	}
	
	
	private void __checkForSequenceFile(String motion) {
		if(Files.exists(Paths.get(rootDir+"/"+motion+"/"+motion+".seq")))return;			// Test of de file er is
		log.info("The sequence for "+motion+" does not exist, create one");
		WaveService waveService=WaveService.getInstance();									// Open de wave Service
		waveService.loadWaveFile(rootDir+"/"+motion+"/"+motion+".wav");							// open de wave file					
		return;
	}
	
	private String __selectRootDir() {
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.contains("win"))return Globals.windowsBaseDirectory;
		if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))return Globals.linuxBaseDirectory;
		return "unknown";
	}
	
}
