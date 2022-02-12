package dragonrecord.recorder;

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
import java.util.Collections;
import java.util.List;

public class MovementRecorder {
	
	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	private List<MoveRecord> tracklist = new ArrayList<>();
	private MoveRecord currentRecord = new MoveRecord();
	private boolean isRecording = false;
	private int recordPosition;
	private int currentRecordSize;
	private List<Boolean> enabledForRecording;

	public MovementRecorder(){
		if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
		log.info("Init MovementRecorder()");
		enabledForRecording = Collections.nCopies(ConfigReader.MAXSERVOS,true);

		TimerService.getInstance().addOnTimerEvent((msg, val1, val2) -> {
			if(isRecording){
				recordStep();
			}
		});
	}

	private void recordStep(){
		if(recordPosition<currentRecordSize){
			tracklist.get(recordPosition).andRecord(currentRecord);
			recordPosition++;
			currentRecord = new MoveRecord();
		}
		else{
			tracklist.add(currentRecord);
			currentRecord = new MoveRecord();
		}
	}

	public void reset()	{
		log.info("Reset the whole track");
		tracklist = new ArrayList<>();
		currentRecord = new MoveRecord();
	}

	public void stopRecording(){
		log.info("Stop recording, tracksize is "+tracklist.size());
		isRecording = false;
		currentRecord=new MoveRecord();
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
		for (int counter = 0; counter<tracklist.size();counter++) {
			dump.append(counter).append(" ").append(tracklist.get(counter)).append(System.lineSeparator());
		}
		return dump.toString();
	}

	public void startRecording() {
		log.info("Start recording");
		isRecording=true;
		recordPosition=0;
		currentRecordSize=tracklist.size();
	}

	public boolean isRecording(){
	    return isRecording;
    }

    public void writeServo(int servo, int value){
		if(enabledForRecording.get(servo))currentRecord.setValue(servo,value);
	}

	public void toggleEnabledForRecording(int servoNumber){
		enabledForRecording.set(servoNumber, !enabledForRecording.get(servoNumber));
		log.info("Track recording for servo "+servoNumber+" is set to "+enabledForRecording.get(servoNumber));

	}

	public void listTrackEnabled(){
		System.out.print("tracks ");
		enabledForRecording.forEach(value -> System.out.print(" "+value));
		System.out.print(System.lineSeparator());
	}

}
