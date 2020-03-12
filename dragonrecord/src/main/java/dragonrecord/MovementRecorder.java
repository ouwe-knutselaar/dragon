package dragonrecord;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.log4j.Logger;

public class MovementRecorder {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	private final int NUM_OF_SERVOS=16;
	private final int MAXSTEPS=300*50;			// 50 hz x 300 seconden
	private int tracklist[][];
	private int laststep=0;
	private String tempname="xxxxxxx";
	private String tempdir=__selectRootDir()+tempname;;
	
	
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
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder record=new StringBuilder();
		for(int stepcount=0;stepcount<laststep;stepcount++)
		{
			for(int servocount=0;servocount<NUM_OF_SERVOS;servocount++)
			{
				record.append(tracklist[servocount][stepcount]).append('\t');
			}
			record.append(System.lineSeparator());
		}
		
		return record.toString();
	}

	
	
	public void writeSequenceFile() throws IOException
	{
		String sequenceFileName=tempdir+tempname+".seq";
		log.info("Write sequence file :"+sequenceFileName);
		File seqenueceFile=new File(sequenceFileName);
		
		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(seqenueceFile));
		for(int tel=0;tel<laststep;tel++)
			{
			 
			 bos.write(String.format("%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d%04d\n",
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
	
	

	
	
	private String __selectRootDir() {
		String OS = System.getProperty("os.name").toLowerCase();
		if(OS.contains("win"))return "D:\\erwin\\dragon\\actions\\";
		if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))return "/var/dragon/";
		return "unknown";
	}

	public void createNewSequence(String recordingName) throws IOException {
		tempname=recordingName;
		tempdir=__selectRootDir()+recordingName+"\\";
		Path path = Paths.get(tempdir);
        Files.createDirectories(path);
	}
	
	
	
	
}
