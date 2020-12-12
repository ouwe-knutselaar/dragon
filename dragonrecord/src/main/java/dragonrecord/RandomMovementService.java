package dragonrecord;

import org.apache.log4j.Logger;
import java.util.Random;

public class RandomMovementService {

    private static final Logger log = Logger.getLogger(RandomMovementService.class.getSimpleName());
    private static boolean running = false;
    private static boolean stopFlag= false;
    private static RandomMovementService classInstance;
    private static final int NUM_OF_SERVOS = 16;
    private static final Random rand = new Random();
    private static final ConfigReader configRead=ConfigReader.getInstance();
    private static int minValue;
    private static int maxValue;
    private static int defaultValue;
    private static String servoName;
    private static int moveStep;
    private static int currentPositionOfServo;
    private static int targetValue;
    private static int selectedServo;

    private RandomMovementService()
    {

    }

    public static RandomMovementService getInstance() {
        if(classInstance == null){
            classInstance = new RandomMovementService();
        }
        return classInstance;
    }


    private static int selectServoToMove()
    {
        selectedServo=rand.nextInt(NUM_OF_SERVOS);
        if(!configRead.isValidServo(selectedServo)) {
            selectedServo=selectServoToMove();
            }
        log.info("Selected servo is "+selectedServo);
        maxValue = configRead.getServoMaxValue(selectedServo);
        minValue = configRead.getServoMinValue(selectedServo);
        defaultValue = configRead.getServoDefaultValue(selectedServo);
        currentPositionOfServo = defaultValue;
        servoName = configRead.getServoName(selectedServo);
        log.info("Selected servo is "+servoName+ " at "+selectedServo);
        running = true;
        createMovement();
        return selectedServo;
    }

    private static void createMovement()
    {
        // Bepaal waar naar toe wordt bewogen
        int boundary = maxValue - minValue;
        log.info("boundry "+boundary);
        targetValue= rand.nextInt(boundary-1) +1+ minValue;     // prevent 0 values

        // Bepaal de richting
        moveStep=1;
        if(targetValue<defaultValue)moveStep=-1;
        log.info("Movement from "+defaultValue+ " tot "+targetValue);
    }

    public static void nextStep() {
        OrchestrationService orchestrationService = OrchestrationService.getInstance();
        if(!running)selectServoToMove();
        log.debug("newpos "+servoName+ " at "+currentPositionOfServo);
        orchestrationService.setSingleServo(selectedServo,currentPositionOfServo);
        currentPositionOfServo += moveStep;
        if(currentPositionOfServo == targetValue )moveStep = moveStep * -1;
        if(stopFlag && currentPositionOfServo == defaultValue) {
            orchestrationService.stopAll();
            stopFlag=false;
            running=false;
            log.info("Random movement stopped");
        }
        if( !stopFlag && currentPositionOfServo == defaultValue ) {
            running = false;
        }

    }

    public static void stopRandomMovement() {
        log.info("stop random movement");
        stopFlag=true;
    }

}
