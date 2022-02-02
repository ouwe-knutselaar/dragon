package dragonrecord;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomMovementService {

    private static final Logger log = Logger.getLogger(RandomMovementService.class.getSimpleName());

    private static RandomMovementService classInstance;
    private Map<Integer,Integer> servoPositionMap = new HashMap<>();
    private boolean isRunning = false;
    private int stepWaiting = 0;
    private Random random = new Random();

    private RandomMovementService() {
        if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
        ConfigReader.getInstance().getServoList().forEach(servo -> servoPositionMap.put(servo.getServonummer(),servo.getRestvalue()));
        TimerService.getInstance().addOnTimerEvent(new DragonEvent() {
            @Override
            public void handle(String msg, int val1, int val2) throws InterruptedException, DragonException, IOException {
                if(isRunning)randomMovement();
            }
        });

    }

    public static RandomMovementService getInstance() {
        if(classInstance == null){
            classInstance = new RandomMovementService();
        }
        return classInstance;
    }

    private  void randomMovement(){
        if(stepWaiting > 0){
            stepWaiting--;
            return;
        }
        stepWaiting= random.nextInt(2000);
        OrchestrationService.getInstance().setSingleServo(1,random.nextInt(2000)-1000);

    }


    public void startRandomMovement(){
        log.info("Start random movement service");
        isRunning=true;
    }

    public void stopRandomMovement() {
        log.info("stop random movement");
        isRunning = false;
    }

}
