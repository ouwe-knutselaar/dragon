package dragoncontrol;

import java.util.ArrayList;
import java.util.List;

public class Globals {

	private Globals(){}
	public static List<Servo> servoLimitList = new ArrayList<>();		// list of servo's

	public static Servo getServoByName(String servoName)
	{
		for(Servo servo : servoLimitList)
		{
			if(servo.getServoName().equals(servoName))return servo;
		}
		return null;
	}
}
