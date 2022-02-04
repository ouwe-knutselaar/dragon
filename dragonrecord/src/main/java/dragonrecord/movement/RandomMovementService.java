package dragonrecord.movement;

import dragonrecord.OrchestrationService;
import dragonrecord.TimerService;
import dragonrecord.config.ConfigReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMovementService {

    private static final Logger log = Logger.getLogger(RandomMovementService.class.getSimpleName());

    private static RandomMovementService classInstance;
    private List<Integer> servoPositionList = new ArrayList<>();
    private boolean isRunning = false;
    private int stepWaiting = 0;
    private Random random = new Random();
    private int maxrandomsteps;
    private int numberOfServos;

    private RandomMovementService() {
        if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
        maxrandomsteps=ConfigReader.getInstance().getRandommaxinterval();
        ConfigReader.getInstance().getServoList().forEach(servo -> servoPositionList.add(servo.getServonummer()));
        numberOfServos = servoPositionList.size();
        TimerService.getInstance().addOnTimerEvent((msg, val1, val2) -> {
            if(isRunning)randomMovement();
        });
    }

    public static RandomMovementService getInstance() {
        if(classInstance == null)classInstance = new RandomMovementService();
        return classInstance;
    }

    private  void randomMovement(){
        if(stepWaiting > 0){
            stepWaiting--;
            return;
        }
        stepWaiting= random.nextInt(maxrandomsteps);
        OrchestrationService.getInstance().setSingleServo(random.nextInt(numberOfServos),random.nextInt(2000)-1000);
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
