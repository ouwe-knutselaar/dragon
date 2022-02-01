package dragonrecord.movement;

import dragonrecord.ConfigReader;
import dragonrecord.config.Servo;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ServoRecord {

    private final Logger log = Logger.getLogger(ServoRecord.class.getSimpleName());
    private final int RESOLUTION = 1000;

    private int min;
    private int max;
    private int rest;
    private double stepSizeRestToMax;
    private double stepSizeRestToMin;
    private int number;
    private String name;
    private int valueToGoTo;
    private double factor;
    private int currentPos;


    public ServoRecord(Servo servo) {
        if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
        this.min = servo.getMinvalue();
        this.max = servo.getMaxvalue();
        this.rest = servo.getRestvalue();
        this.number = servo.getServonummer();
        this.name = servo.getName();
        this.factor = servo.getFactor();
        log.info("Servo "+name);

        currentPos=rest;
        valueToGoTo=rest;

        stepSizeRestToMax = (double)(max-rest)/RESOLUTION;
        stepSizeRestToMin = (double)(rest - min)/RESOLUTION;

    }

    public void goToNewPosition(int newValue){
        if(newValue<-1000 || newValue>1000)return;
        valueToGoTo=getNewPosition(newValue);
        log.debug("Servo "+number+":"+name+" to absolute position "+valueToGoTo+" based on relative position "+newValue);
    }


    public int doOneStepToNewPos(){
        int diff = valueToGoTo-currentPos;
        int step = (int) (diff*factor);
        if(step == 0 ){
            currentPos=valueToGoTo;
            return 0;
        }
        log.debug("currentPos "+currentPos+" -> valueToGoTo "+valueToGoTo+" with stepsize "+step);
        currentPos +=step;
        return currentPos;
    }


    public int getNewPosition(int inputValue){
        if(inputValue>=0){
            return rest+(int)(inputValue*stepSizeRestToMax);
        }
        return rest+(int)(inputValue*stepSizeRestToMin);
    }

    public int getRestValue(){
        return rest;
    }

    public int getNumber(){
        return number;
    }

    public String getName() {
        return name;
    }
}
