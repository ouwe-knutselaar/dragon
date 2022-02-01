package dragonrecord.movement;

import dragonrecord.ConfigReader;
import dragonrecord.DragonEvent;
import dragonrecord.DragonException;
import dragonrecord.TimerService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MovementCoordinator {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

    ConfigReader configReader = ConfigReader.getInstance();
    TimerService timerService = TimerService.getInstance();

    private final I2CService i2cService = new I2CService();

    Map<Integer,ServoRecord> servoRecordList = new HashMap<>();
    public MovementCoordinator(){
        if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
        i2cService.init(50);
        configReader.getServoList().forEach(servo -> servoRecordList.put(servo.getServonummer(),new ServoRecord(servo)));

        timerService.addOnTimerEvent(new DragonEvent() {
            @Override
            public void handle(String msg, int val1, int val2) throws InterruptedException, DragonException, IOException {
                processServoStepsList();
            }
        });
    }


    public void processServoStepsList(){
        servoRecordList.forEach((number,servo) -> processServo(servo));
    }

    private void processServo(ServoRecord servo){
        int newPos = servo.doOneStepToNewPos();
        if(newPos!=0) {
            i2cService.writeSingleLed(servo.getNumber(), newPos);
            log.debug("Set servo " + servo.getNumber() + ":" + servo.getName() + " to value " + newPos);
        }
    }


    public void goToNewValue(int servo, int newvalue){
        log.debug("Send servo "+servo+" to new relative position "+ newvalue);
        servoRecordList.get(servo).goToNewPosition(newvalue);
    }

    public void allToDefault() {
        servoRecordList.forEach((number,servo) -> goToNewValue(number,servo.getRestValue()));
    }

    public void fullReset(){
        log.info("Full reset of all the servo, disable the PWM of the PCA9685");
        servoRecordList.forEach((number,servo) -> i2cService.writeSingleLed(number,0));

    }
}
