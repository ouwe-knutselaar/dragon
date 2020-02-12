package dragonraspberry.services;

import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import dragonraspberry.continuemovement.ContinueMovement;
import dragonraspberry.pojo.DragonEvent;
import dragonraspberry.pojo.Globals;


// The core of the robot
// All actions are coordinated from this class

public class OrchestrationService {

	private Logger log=Logger.getLogger(this.getClass().getSimpleName());
	
	// For the recording mode
	private int __servoToRecord			= 0;			// The servo that is recorded
	private boolean __recodingEnabled	= false;		// Enable or disable recording
	private boolean __normalOperation	= false;		// enable or disable normal operation
	private int __recordedValueFromUDP	= 0;			// Value tracked by the UDP Service
	
	// For normal operation
	private ContinueMovement __continueMovement 	= new ContinueMovement();
	
	// The services that the motionserivce needs
	private TimerService timerService				= TimerService.getInstance();
	private I2CService i2cService					= new I2CService();
	private WaveService waveService					= WaveService.getInstance();
	private RecordingService recordingService		= new RecordingService();
	private static OrchestrationService INSTANCE	= new OrchestrationService();
	
	/**
	 * This is a singleton
	 * @return
	 */
	public static OrchestrationService GetInstance()
	{
		return INSTANCE;
	}
	
	
	private OrchestrationService() {
		log.info("Make the OrchestrationService");

		i2cService.init(Globals.servoFrequency);			

		// Voeg een actie aan de timer toe
		timerService.addOnTimerEvent(new DragonEvent() {
			@Override
			public void handle(String msgFromTimer, int stepFromTimer, int val2) {
				try {
					// Recording a movement
					
					if(__recodingEnabled)
					{
						int[] tempValueList=__continueMovement.nextStep();
						tempValueList[__servoToRecord]=__recordedValueFromUDP;					// Alter on value to the record value
						recordingService.setValue(stepFromTimer, __recordedValueFromUDP);		// Write the value to the recording list
					}
					if(__normalOperation)
					{
						i2cService.writeLedString(__continueMovement.nextStep());
					}
					return;
				} catch (IllegalArgumentException e) {
					// log.debug("time gives value that is out of bound "+val1);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		});
		
		recordingService.addOnRecordingEvent(new DragonEvent(){
			@Override
			public void handle(String msg, int val1, int val2) {
				stopTrackRecording();
			}});
	}
		
	
	/**
	 * Run an action based on an motion names
	 * @param name
	 */
	public void runNamedMotion(String name)
	{
		 __continueMovement.setCurrentMotionFromName(name);
		 __continueMovement.runCurrentMotion();
	}
	
	
	/**
	 * Het a list of motion names
	 * @return
	 */
	public List<String> getMotionNameList()
	{
		return __continueMovement.getMotionNameList();
	}

	
	/**
	 * Write a value to a single Led of Servo
	 * @param ledNumber
	 * @param servoValue
	 * @throws IOException
	 */
	public void setSingleServo(int servoNumber, int servoValue) throws IOException {
		log.debug("Write single server/led :"+servoNumber+" with "+servoValue);
        __recordedValueFromUDP=Globals.servoLimitList[servoNumber].correctToLimits(servoValue);
        if(__recodingEnabled)return;
		i2cService.writeSingleLed(servoNumber, servoValue);
	}

	
	/**
	 * Enable the recording of a track
	 * @param clientInputString
	 */
	public void startTrackRecording(int servo) {
		try{
			__normalOperation=false;
			__servoToRecord = servo;															// Determine the servo to record
			waveService.loadWaveFile(__continueMovement.getCurrentMotion().getWaveFileName());
			recordingService.makeNewRecordingTrask(waveService.getSteps());						// Create new recording track
			__recodingEnabled=true;																// enable recording
			timerService.stepReset();															// Reset de timer
			waveService.playWave();																// Start de audio
			log.info("Set "+__continueMovement.getCurrentMotion().getSeqFileName()+" recording for servo "+__servoToRecord);
		}
		catch(NumberFormatException e)
		{
			log.error("Client gave invalid number to record");
		}
	}


	public void stopTrackRecording() {
		__recodingEnabled=false;
		log.info("Recording stopped");
		log.info("Recording "+recordingService.dumpRecordedTrack());
		log.info("Update the motion");
		recordingService.correctNotReceived();
		__continueMovement.updateServoListInCurrentMotion(__servoToRecord, recordingService.getValueList());
	}


	public void setCurrentMotion(String motionName) {
		__continueMovement.setCurrentMotionFromName(motionName);
		log.info("Current motion set to "+motionName);
	}


	public void writeCurrentMotion() throws IOException {
		__continueMovement.writeCurrentMotion();
	}



	public void dumpCurrentMotion() {
		__continueMovement.getCurrentMotion().dumpMotion();
	}


	public void runCurrentMotion() {
		__continueMovement.runCurrentMotion();
	}


	public void stopAll() {
		timerService.stopService();
	}


	public void pauseAllActivities() {
		__recodingEnabled=false;
		__normalOperation=false;
		log.info("Pause all activites");
	}


	public void operateNormal() {
		__recodingEnabled=false;
		__normalOperation=true;
		log.info("Operate normal");
	}



	
}
