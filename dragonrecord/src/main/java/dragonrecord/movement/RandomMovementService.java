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
    private OrchestrationService orchestrationService;

    public RandomMovementService(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
        if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
        log.info("Initialize RandomMovementService");
        maxrandomsteps=ConfigReader.getInstance().getRandommaxinterval();
        ConfigReader.getInstance().getServoList().forEach(servo -> servoPositionList.add(servo.getServonummer()));
        numberOfServos = servoPositionList.size();
        TimerService.getInstance().addOnTimerEvent((msg, val1, val2) -> {
            if(isRunning)randomMovement();
        });
    }


    private  void randomMovement(){
        if(stepWaiting > 0){
            stepWaiting--;
            return;
        }
        stepWaiting= random.nextInt(maxrandomsteps);
        orchestrationService.setSingleServo(random.nextInt(numberOfServos),random.nextInt(2000)-1000);
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
