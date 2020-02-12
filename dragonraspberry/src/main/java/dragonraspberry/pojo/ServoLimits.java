package dragonraspberry.pojo;

public class ServoLimits {

	private final int numOfServos = 16;

	private int[] min = new int[numOfServos];
	private int[] max = new int[numOfServos];
	private int[] rest = new int[numOfServos];
	private boolean[] enabled = new boolean[numOfServos];
	private String[] name = new String[numOfServos];
	
	private final static ServoLimits INSTANCE=new ServoLimits(); 

	private ServoLimits() {
		__set(8, "head", 200, 500, 350);
		__set(9, "tail", 50, 400, 225);
		__set(10, "Neck muscle rigth", 50, 350, 225);
		__set(11, "Neck muscle left", 50, 400, 225);
		__set(13, "Wing rigth", 310, 500, 490);
		__set(14, "Wing left", 110, 300, 120);
		__set(0, "Eye green", 0, 4096, 0);
		__set(1, "Eye red", 0, 4096, 0);
		__set(2, "Eye blue", 0, 4096, 0);
		__set(7, "Jaw", 270, 300, 300);
	}

	
	public static ServoLimits getInstance()
	{
		return INSTANCE;
	}
	
	private void __set(int servo, String nameval, int minval, int maxval, int restval) {
		min[servo] = minval;
		max[servo] = maxval;
		rest[servo] = restval;
		name[servo] = nameval;
		enabled[servo] = true;
	}

	public int correctToLimits(int servo, int value) {
		if (value < min[servo])
			value = min[servo];
		if (value > max[servo])
			value = max[servo];
		return value;
	}

	public int getRestPositionOfServo(int servo) {
		return rest[servo];
	}

	public int getMinimumOfServo(int servo) {
		return min[servo];
	}

	public int getMaximumOfServo(int servo) {
		return max[servo];
	}

	public String getNameOfServo(int servo) {
		return name[servo];
	}

	public boolean isEnabled(int servo) {
		return enabled[servo];
	}

}
