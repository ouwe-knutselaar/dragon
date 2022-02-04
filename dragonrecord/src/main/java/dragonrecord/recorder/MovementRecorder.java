package dragonrecord.recorder;

import dragonrecord.DragonEvent;
import dragonrecord.DragonException;
import dragonrecord.TimerService;
import dragonrecord.config.ConfigReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MovementRecorder {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private List<MoveRecord> tracklist = new ArrayList<>();
	private MoveRecord currentRecord = new MoveRecord();
	private boolean isRecording = false;

	public MovementRecorder(){
		if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
		log.info("Init MovementRecorder()");

		TimerService.getInstance().addOnTimerEvent(new DragonEvent() {
			@Override
			public void handle(String msg, int val1, int val2) throws InterruptedException, DragonException, IOException {
				if(isRecording){
					tracklist.add(currentRecord);
					currentRecord = new MoveRecord();
				}
			}
		});
	}
	
	public void reset()	{
		log.info("Reset the recorder");
		tracklist = new ArrayList<>();
	}

	public void record(int servo, int servoValue) {
		if(isRecording) {
			currentRecord.setValue(servo, servoValue);
			log.debug("record servo " + servo + " value " + servoValue);
		}
	}

	public void stopRecording(int servo){
		isRecording = false;
	}

	public int getLastStep(){
		return tracklist.size();
	}

	public void writeSequenceFile(String sequenceFileName,String actionType) throws IOException {
		log.info("Write sequence file :"+sequenceFileName + " with actiontype "+actionType);

		BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(new File(sequenceFileName)));
		bos.write(String.format("%s%n", actionType).getBytes());
		tracklist.forEach(record -> {
			try {
				bos.write(record.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		bos.close();
	}

	public void openNewSequence(String sequenceFileName) throws IOException {
		if(!Files.exists(Paths.get(sequenceFileName))){
			Path sequencePath=Paths.get(sequenceFileName);
			log.info("Created new recoding in "+sequenceFileName);
			log.info("Recordng name is "+sequencePath.getFileName());
	        Files.createDirectories(sequencePath.getParent());
	        Files.createFile(Paths.get(sequenceFileName));
		}
		
		log.info("Read sequencefile "+sequenceFileName);
		List<String> sequenceLines = Files.readAllLines(Paths.get(sequenceFileName));
		if(sequenceLines.isEmpty()){
			log.info("Empty file");
			return;
		}
		log.info("Parsed action file of " + tracklist.size() + " steps");
	}

	@Override
	public String toString() {
		StringBuilder dump = new StringBuilder();
		tracklist.forEach(record -> dump.append(record).append(System.lineSeparator()));
		return dump.toString();
	}

	public void startRecording() {
		isRecording=true;
	}

}
