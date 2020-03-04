package dragonraspberry.pojo;

import org.apache.log4j.Logger;

public class Servo {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());

	
	private String servoName;
	private int minPos;
	private int maxPos;
	private int restPos;
	private int servoValue;
	private int diff;						// Max - min
	private boolean active=false;			// Use this servo or not
	
	
	public Servo(int servoValue,String inputStringToParse)
	{
		log.info("Parse "+inputStringToParse);
		String valueList[]=inputStringToParse.split(",");
		this.servoName = valueList[0];
		this.minPos = Integer.parseInt(valueList[1]);
		this.maxPos = Integer.parseInt(valueList[2]);
		this.restPos = Integer.parseInt(valueList[3]);
		if(valueList[4].equals("1"))active=true;
		this.servoValue = servoValue;
		this.diff = maxPos-minPos;
	}
	
	
	public int correctToLimits(int value) {
		if (value < minPos)
			value = minPos;
		if (value > maxPos)
			value = maxPos;
		return value;
	}
	
	
	public String getServoName() {
		return servoName;
	}
	public void setServoName(String servoName) {
		this.servoName = servoName;
	}
	public int getMinPos() {
		return minPos;
	}
	public void setMinPos(int minPos) {
		this.minPos = minPos;
	}
	public int getMaxPos() {
		return maxPos;
	}
	public void setMaxPos(int maxPos) {
		this.maxPos = maxPos;
	}
	public int getRestPos() {
		return restPos;
	}
	public void setRestPos(int restPos) {
		this.restPos = restPos;
	}
	public int getServoValue() {
		return servoValue;
	}
	public void setServoValue(int servoValue) {
		this.servoValue = servoValue;
	}
	public int getDiff() {
		return diff;
	}
	public void setDiff(int diff) {
		this.diff = diff;
	}

	public boolean isActive()
	{
		return active;
	}

	@Override
	public String toString() {
		return "Servo servoName=" + servoName 
				+ ", minPos=" + minPos 
				+ ", maxPos=" + maxPos 
				+ ", restPos=" + restPos
				+ ", servoValue=" + servoValue 
				+ ", active=" + active ;
	}
	
	
	

}
