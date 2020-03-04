package dragonraspberry.pojo;

import org.apache.log4j.Logger;

/**
 * Class that can store a full motion
 * @author Erwin
 *
 */
public class Motion {
		
	Logger log=Logger.getLogger(this.getClass().getSimpleName());
	
	private final int __size=Globals.numberOfServos;
	private String seqFileName;
	private String waveFileName;
	private List<int[]> servoValueList=new ArrayList<>();
	private int steps=0;
	
	public Motion(String name,String waveName)
	{
		log.info("Make Motion ");
		this.seqFileName=name;
		this.waveFileName=waveName;
		
		log.debug("Sequence file is "+seqFileName);
		log.debug("wave file is "+waveName);
		log.debug("actionlist has action:"+servoValueList.size());
	}
	
	public int[] getListOfServoValues(int index) throws IllegalArgumentException
	{
		if(index<0)throw new IllegalArgumentException("Negative index number");
		if(index>=servoValueList.size())throw new IllegalArgumentException("Out of bound:"+index);
		return servoValueList.get(index);
	}


	public String getSeqFileName() {
		return seqFileName;
	}
	
	
	public String getWaveFileName()
	{
		return waveFileName;
	}
	
	
	public void parseSequenceFile(List<String> seqFile) {
		servoValueList.clear();
		for (String line : seqFile) {
			//log.debug("Parse " + line);
			int[] valueList = new int[__size];
			for (int tel = 0; tel < __size; tel++) {
				valueList[tel] = Integer.parseInt(line.substring((tel * 4), 4 + (tel * 4)));
			}
			servoValueList.add(valueList);
		}
		steps = servoValueList.size();
		log.debug("Parsed action file of " + steps + " steps");
	}
	
	
	public void updateValueListForServo(int servo,int valueList[])
	{
		for(int tel=0;tel<valueList.length;tel++)
		{
			servoValueList.get(tel)[servo]=valueList[tel];
		}
	}
	
	
	public int getSteps()
	{
		return steps;
	}
	
	
	// Make an empty list
	public void createEmptyMotion(int steps)
	{
		servoValueList.clear();
		for(int tel=0;tel<steps;tel++)
		{
			servoValueList.add(new int[Globals.numberOfServos]);
		}
		this.steps=steps;
		log.debug("Created empty file of " + steps + " steps");
	}
	
	
	public void dumpMotion()
	{
		log.info("Sequence name is "+seqFileName);
		log.info("WaveFile name is "+waveFileName);
		servoValueList.forEach(motion -> log.info(DisplayTools.ArrayToHexString(motion)));
	}
	
}
