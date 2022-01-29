package dragonrecord.movement;

import dragonrecord.ConfigReader;
import dragonrecord.DragonException;
import dragonrecord.config.Servo;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MovementCoordinator {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    ConfigReader configReader = ConfigReader.getInstance();
    Map<Integer, Servo> servoList = new HashMap<>();
    Map<Integer,Integer> valueToGoTo = new HashMap<>();
    Map<Integer,Integer> currentValue = new HashMap<>();
    Map<Integer,Double> factor = new HashMap<>();
    private final I2CService i2cService = new I2CService();

    public MovementCoordinator(){
        configReader.getServoList().forEach(servo -> servoList.put(servo.getServonummer(),servo));
        servoList.forEach((number,servo) -> valueToGoTo.put(number,servo.getRestvalue()));
        servoList.forEach((number,servo) -> currentValue.put(number,servo.getRestvalue()));
        servoList.forEach((number,servo) -> factor.put(number,servo.getFactor()));
        i2cService.init(50);
    }


    public void processServoStepsList(){
        servoList.forEach((number,servo) -> processNextStep(number));
    }

    private void processNextStep(Integer servo) {
        int diff = valueToGoTo.get(servo)-currentValue.get(servo);
        int newValue = currentValue.get(servo)+  (int)(diff*factor.get(servo));
        if(newValue==currentValue.get(servo))return;
        currentValue.put(servo,newValue);
        log.info("set servo "+servo+" to value "+newValue);
        try {
            i2cService.writeSingleLed(servo,newValue);
        } catch (DragonException e) {
            log.error("Problem settings servo "+servo+" to new value "+newValue);
            e.printStackTrace();
        }
    }

    public void goToNewValue(int servo, int newvalue){
        valueToGoTo.put(servo,newvalue);
        log.info("Set servo "+servo+" to value "+newvalue);
    }

    public void allToDefault() {
        servoList.forEach((number,servo) -> goToNewValue(number,servo.getRestvalue()));
    }

    public void fullReset(){
        servoList.forEach((number,servo) -> goToNewValue(number,0));

    }
}
