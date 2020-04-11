package dragonrecord;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

public class MovementRecorder {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private final int NUM_OF_SERVOS=16;
	private final int MAXSTEPS=300*50;			// 50 hz x 300 seconden
	private int tracklist[][];
	private int laststep=0;
	private String recordingName="xxxxxxx";
	private String recordingDirectory=selectRootDir()+recordingName;
	private String directorySeparator=__selectDirSeparator();
	
	
	public MovementRecorder()
	{
		log.info("Init MovementRecorder()");
		reset();
	}
	
	public void reset()
	{
		log.info("Reset the recorder to "+MAXSTEPS+" steps");
		tracklist=new int[NUM_OF_SERVOS][MAXSTEPS];
		laststep=0;
	}

	
	public void record(int servo, int servoValue, int step) {
		if(step>MAXSTEPS)return;
		tracklist[servo][step]=servoValue;
		laststep=Math.max(step, laststep);
		log.debug("record servo "+servo+" value "+servoValue+" step "+step);
	}
	
	

		
	
	public void writeSequenceFile() throws IOException
	{
		String sequenceFileName=recordingDirectory+directorySeparator+recordingName+".seq";
		log.info("Write sequence file :"+sequenceFileName);
		File seqenueceFile=new File(sequenceFileName);
		
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(seqenueceFile));
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
	
	
	public String selectRootDir() {
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.contains("win"))return "D:\\erwin\\dragon\\actions\\";
		if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))return "/var/dragon/";
		return "unknown";
	}
	
	
	private String __selectDirSeparator() {
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.contains("win"))return "\\";
		if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))return "/";
		return "/";
	}
	

	public void createNewSequence(String newRecordingName) throws IOException {
		recordingName=newRecordingName.trim();
		recordingDirectory=(selectRootDir()+recordingName).trim();
		log.info("Created new recoding in -"+recordingDirectory+"-");
		log.info("Recordng name is "+recordingName);
		Path path = Paths.get(recordingDirectory);
        Files.createDirectories(path);
	}
	
	public String getRecordingWaveName()
	{
		return recordingDirectory+directorySeparator+recordingName+".wav";
	}
	
	
	public int[] getServoValuesFromStep(int step)
	{
		int result[]=new int[NUM_OF_SERVOS];
		if(step>MAXSTEPS) return result;
		for(int tel=0;tel<NUM_OF_SERVOS;tel++)result[tel]=tracklist[tel][step];
		return result;
	}
	
	
	@Override
	public String toString() {
		StringBuilder record = new StringBuilder();
		for (int stepcount = 0; stepcount < laststep; stepcount++) {
			for (int servocount = 0; servocount < NUM_OF_SERVOS; servocount++) {
				record.append(tracklist[servocount][stepcount]).append('\t');
			}
			record.append(System.lineSeparator());
		}
		return record.toString();
	}
	
}
