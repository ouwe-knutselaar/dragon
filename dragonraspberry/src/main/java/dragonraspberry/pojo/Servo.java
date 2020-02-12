package dragonraspberry.pojo;

public class Servo {
	
	private String servoName;
	private int minPos;
	private int maxPos;
	private int restPos;
	private int servoValue;
	
	
	public Servo(int servoValue,String inputStringToParse)
	{
		String valueList[]=inputStringToParse.split(",");
		this.servoName = valueList[0];
		this.minPos = Integer.parseInt(valueList[1]);
		this.maxPos = Integer.parseInt(valueList[2]);
		this.restPos = Integer.parseInt(valueList[3]);
		this.servoValue = servoValue;
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


	@Override
	public String toString() {
		return "Servo servoName=" + servoName 
				+ ", minPos=" + minPos 
				+ ", maxPos=" + maxPos 
				+ ", restPos=" + restPos
				+ ", servoValue=" + servoValue ;
	}
	
	
	

}
