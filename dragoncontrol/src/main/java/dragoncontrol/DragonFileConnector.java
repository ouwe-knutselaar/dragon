package dragoncontrol;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class DragonFileConnector {


	private String rootDir = "unknown"; // Rootdit van de acties

	public DragonFileConnector() {
		try {
			rootDir = __selectRootDir();
			readTheDeafaultFromTheProperiesFileAndPutItInGlobals(rootDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void readTheDeafaultFromTheProperiesFileAndPutItInGlobals(String directory) throws IOException {
		directory = directory + "/dragon.properties";
		InputStream input = new FileInputStream(directory);
		Properties prop = new Properties();
		prop.load(input);

		Globals.servoLimitList[0] = new Servo(0, prop.getProperty("servo0", "none,0,0,0"));
		Globals.servoLimitList[1] = new Servo(1, prop.getProperty("servo1", "none,0,0,0"));
		Globals.servoLimitList[2] = new Servo(2, prop.getProperty("servo2", "none,0,0,0"));
		Globals.servoLimitList[3] = new Servo(3, prop.getProperty("servo3", "none,0,0,0"));
		Globals.servoLimitList[4] = new Servo(4, prop.getProperty("servo4", "none,0,0,0"));
		Globals.servoLimitList[5] = new Servo(5, prop.getProperty("servo5", "none,0,0,0"));
		Globals.servoLimitList[6] = new Servo(6, prop.getProperty("servo6", "none,0,0,0"));
		Globals.servoLimitList[7] = new Servo(7, prop.getProperty("servo7", "none,0,0,0"));
		Globals.servoLimitList[8] = new Servo(8, prop.getProperty("servo8", "none,0,0,0"));
		Globals.servoLimitList[9] = new Servo(9, prop.getProperty("servo9", "none,0,0,0"));
		Globals.servoLimitList[10] = new Servo(10, prop.getProperty("servo10", "none,0,0,0"));
		Globals.servoLimitList[11] = new Servo(11, prop.getProperty("servo11", "none,0,0,0"));
		Globals.servoLimitList[12] = new Servo(12, prop.getProperty("servo12", "none,0,0,0"));
		Globals.servoLimitList[13] = new Servo(13, prop.getProperty("servo13", "none,0,0,0"));
		Globals.servoLimitList[14] = new Servo(14, prop.getProperty("servo14", "none,0,0,0"));
		Globals.servoLimitList[15] = new Servo(15, prop.getProperty("servo15", "none,0,0,0"));

	}

	private String __selectRootDir() {
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.contains("win"))
			return "D:\\erwin\\dragon\\actions";
		if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))
			return "/var/dragon/";
		return "unknown";
	}
}
