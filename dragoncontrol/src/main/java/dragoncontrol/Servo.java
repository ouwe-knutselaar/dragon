package dragoncontrol;

public class Servo {
	
	private String servoName;
	private int minPos;
	private int maxPos;
	private int restPos;
	private int servoValue;

	public Servo(String servoName,int minpPos,int maxPos,int restPos,int ServoValue)
	{
		this.servoName = servoName;
		this.minPos = minpPos;
		this.maxPos = maxPos;
		this.restPos = restPos;
		this.servoValue = ServoValue;
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

	@Override
	public String toString() {
		return "Servo servoName=" + servoName 
				+ ", minPos=" + minPos 
				+ ", maxPos=" + maxPos 
				+ ", restPos=" + restPos
				+ ", servoValue=" + servoValue ;
	}

}
