package dragonrecord;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;

public class MovementRecorder {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private final int NUM_OF_SERVOS=16;
	private final int MAXSTEPS=300*50;					// 50 hz x 300 seconden
	private int tracklist[][];							//[servo][step]
	private boolean recorded[]=new boolean[MAXSTEPS];	
	private int laststep=0;
	private String actionType="myself";			// male , female , child , myself

	
	public MovementRecorder()
	{
		log.info("Init MovementRecorder()");
		reset();
	}
	
	public void reset()
	{
		log.info("Reset the recorder to "+MAXSTEPS+" steps");
		tracklist=new int[NUM_OF_SERVOS][MAXSTEPS];
		recorded=new boolean[MAXSTEPS];
		laststep=0;
	}

	
	public void record(int servo, int servoValue, int step) {
		if(step>MAXSTEPS)return;
		tracklist[servo][step]=servoValue;
		recorded[step]=true;
		laststep=Math.max(step, laststep);
		log.debug("record servo "+servo+" value "+servoValue+" step "+step);
	}
	
	
	public void stopRecording(int servo)
	{
		int total=0;
		for(int tel=1;tel<laststep;tel++)
		{
			if(recorded[tel]==false)
			{
				tracklist[servo][tel]=(int)(tracklist[servo][tel-1]+tracklist[servo][tel+1])/2;
				total++;
			}
		}
		log.info("Number of autocorrected errors "+total +" for servo "+servo);		
	}
	
	
	
	public int getLastStep()
	{
		return laststep;
	}
		
	
	
	public void writeSequenceFile(String sequenceFileName) throws IOException
	{
		log.info("Write sequence file :"+sequenceFileName);
		File seqenueceFile=new File(sequenceFileName);
		
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(seqenueceFile));
		bos.write(String.format("%s\n", actionType).getBytes());
		for(int tel=0;tel<laststep;tel++)
			{bos.write(String.format("%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d\n",
					 		tracklist[0][tel],
					 		tracklist[1][tel],
					 		tracklist[2][tel],
					 		tracklist[3][tel],
					 		tracklist[4][tel],
					 		tracklist[5][tel],
					 		tracklist[6][tel],
					 		tracklist[7][tel],
					 		tracklist[8][tel],
					 		tracklist[9][tel],
					 		tracklist[10][tel],
					 		tracklist[11][tel],
					 		tracklist[12][tel],
					 		tracklist[13][tel],
					 		tracklist[14][tel],
					 		tracklist[15][tel]).getBytes());
			}
		bos.close();
	}
	

	public void openNewSequence(String sequenceFileName) throws IOException {
		if(!Files.exists(Paths.get(sequenceFileName)))
		{
			Path sequencePath=Paths.get(sequenceFileName);
			log.info("Created new recoding in "+sequenceFileName);
			log.info("Recordng name is "+sequencePath.getFileName());
	        Files.createDirectories(sequencePath.getParent());
	        Files.createFile(Paths.get(sequenceFileName));
		}
		
		log.info("Read sequencefile "+sequenceFileName);
		List<String> sequenceLines = Files.readAllLines(Paths.get(sequenceFileName));
		actionType=sequenceLines.get(0);
		tracklist=new int[NUM_OF_SERVOS][MAXSTEPS];
		for (int lines=1;lines < sequenceLines.size();lines++) {
			//log.debug("Parse " + line);
			
			for (int tel = 0; tel < NUM_OF_SERVOS; tel++) {
				tracklist[tel][lines] = Integer.parseInt(sequenceLines.get(lines).substring((tel * 4), 4 + (tel * 4)));
			}
		}
		laststep = sequenceLines.size();
		log.info("Parsed action file of " + laststep + " steps");
		
	}
	
	
	public int[] getServoValuesFromStep(int step)
	{
		int result[]=new int[NUM_OF_SERVOS];
		if(step>MAXSTEPS-1) return result;
		for(int tel=0;tel<NUM_OF_SERVOS;tel++)result[tel]=tracklist[tel][step];
		return result;
	}
	
	
	@Override
	public String toString() {
		StringBuilder record = new StringBuilder();
		record.append(actionType).append(System.lineSeparator());
		for (int stepcount = 0; stepcount < laststep; stepcount++) {
			for (int servocount = 0; servocount < NUM_OF_SERVOS; servocount++) {
				record.append(tracklist[servocount][stepcount]).append('\t');
			}
			record.append(recorded[stepcount]);
			record.append(System.lineSeparator());
		}
		return record.toString();
	}
	
	
	public void filter(int servo)
	{
		int tempTrack[]=new int[MAXSTEPS];
		for(int tel=1;tel<MAXSTEPS-1;tel++)
		{
			int sub = tracklist[servo][tel - 1] + tracklist[servo][tel] + tracklist[servo][tel + 1];
			sub = sub/3;
			tempTrack[tel]=sub;
		}
		
		for(int tel=0;tel<MAXSTEPS;tel++)
		{
			tracklist[servo][tel]=tempTrack[tel];
		}
	}

	
	
	
	public void startRecording() {
		recorded=new boolean[MAXSTEPS];
	}

	
	public void setActionType(String actionTypename) {
		actionType = actionTypename;
	}
}
