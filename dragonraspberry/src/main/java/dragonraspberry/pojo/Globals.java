package dragonraspberry.pojo;

public class Globals {
	
	public final static int numberOfServos = 16;
	
	public static Servo servoLimitList[]=new Servo[16];		// list of servo's
	
	public final static int servoFrequency=60; 
	
	public static final String windowsBaseDirectory = "D:\\erwin\\dragon\\actions";
	public static final String linuxBaseDirectory   = "/var/dragon/";
	public static String baseDirectory              = linuxBaseDirectory;
}