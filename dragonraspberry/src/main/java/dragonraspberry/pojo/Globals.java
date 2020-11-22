package dragonraspberry.pojo;

public class Globals {
	
	public final static int numberOfServos = 16;	
	public final static int servoFrequency=60; 
	public static final String windowsBaseDirectory = "D:\\erwin\\dragon\\actions";
	public static final String linuxBaseDirectory   = "/var/dragon";
	public static String baseDirectory              = linuxBaseDirectory;
	
	public static String selectRootDir() {
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.contains("win"))return windowsBaseDirectory;
		if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))return linuxBaseDirectory;
		return "unknown";
	}
	
}