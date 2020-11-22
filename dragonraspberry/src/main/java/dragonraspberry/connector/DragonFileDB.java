package dragonraspberry.connector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import dragonraspberry.pojo.Globals;

public class DragonFileDB {

	private Logger log = Logger.getLogger(DragonFileDB.class);
	private String rootDir = "unknown";									// Rootdit van de acties
	private Map<String,String> actionList = new HashMap<>();			// Lijst met acties en actietypes
	private static DragonFileDB INSTANCE = new DragonFileDB();
	List<String> actionNameList = new ArrayList<String>();
	private Random rand = new Random();
		
	private DragonFileDB() {
		log.info("Make the DragonFileDB");
		rootDir = Globals.selectRootDir();
		log.info("read the default values");
		log.info("The root directory for the actions is " + rootDir);
		__ScanTheActionToTheActionList(this.rootDir);
	}

	
	public static DragonFileDB getInstance()
	{
		return INSTANCE;
	}
	

	private void __ScanTheActionToTheActionList(String rootDir) {
		File rootDirHandle = new File(rootDir);
		for (File subFile : rootDirHandle.listFiles()) {
			if (subFile.isDirectory())actionList.put(subFile.getName(),"");
		}
		log.info("Loaded " + actionList.size() + " actions from " + rootDir);
		
		for(String action:actionList.keySet())
		{
			try {
				String actiontype = Files.readAllLines(Paths.get(rootDir+"/"+action+"/"+action+".seq")).get(0).trim();
				
				actionList.put(action, actiontype);
				log.info("added "+action+" \t "+actiontype+".");
			} catch (IOException e) {
				continue;
			}
			
		}
		actionNameList = new ArrayList<String>(actionList.keySet());
	}
	

	
	
	
	public String selectRandomAction(String actiontype)
	{
		String actionName=actionNameList.get(rand.nextInt(actionNameList.size()));
		String actionNameFromKey = actionList.get(actionName);
		if(actionNameFromKey.equals(actiontype))return actionName;
		return selectRandomAction(actiontype);
	}
	
}
