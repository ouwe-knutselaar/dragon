package dragonrecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import dragonrecord.config.Config;
import dragonrecord.config.Servo;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigReader {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private static final ConfigReader INSTANCE = new ConfigReader();
    private Config config;
    private ObjectMapper mapper;
    Map<Integer, Servo> servoList = new HashMap<>();
    private String configFile;

    public static ConfigReader getInstance(){
        return INSTANCE;
    }

    private ConfigReader(){};


    public void readConfiguration(String configFile) {
        try {
            this.configFile = configFile;
            FileInputStream fis = new FileInputStream(new File(configFile));
            mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(fis, Config.class);
            config.getServolist().forEach(servo -> servoList.put(servo.getServonummer(),servo));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void writeConfiguration(){
        try {
            log.info("Store config in "+configFile);
            mapper.writeValue(new File(configFile),config);
        } catch (IOException e) {
            log.error("Cannot write "+configFile);
            e.printStackTrace();
        }
    }

    public void dumpConfig() {
        log.info("List the configuration");
        log.info(config);
    }

    public String getSemiColonSeparatedServoValuesListing() {
        StringBuilder servovalueList = new StringBuilder();
        servoList.forEach((key,servo) -> servovalueList.append(servo.getName()).append(' ').append(servo.getServonummer()).append(';'));
        return servovalueList.toString();
    }

    public int getTimeStep() {
        return config.getTimestep();
    }

    public int getRandommaxinterval(){
        return config.getRandommaxinterval();
    }

    public boolean isValidServo(int servoNumber){
        return servoList.containsKey(servoNumber);
    }

    public int getServoMinValue(int servoNumber){
        return servoList.get(servoNumber).getMinvalue();
    }

    public int getServoMaxValue(int servoNumber){
        return servoList.get(servoNumber).getMaxvalue();
    }

    public int getServoDefaultValue(int servoNumber){
        return servoList.get(servoNumber).getRestvalue();
    }

    public String getServoName(int servoNumber){
        return servoList.get(servoNumber).getName();
    }

    public boolean isDebug(){ return config.isDebug(); };

    public List<Servo> getServoList(){
        return config.getServolist();

    }

    public String getActionPath(){
        return config.getActionpath();
    }

    public void updateServo(String param,int servo, int value){
        if(servoList.containsKey(servo)){
            if(param.equals("max"))servoList.get(servo).setMaxvalue(value);
            if(param.equals("min"))servoList.get(servo).setMinvalue(value);
            if(param.equals("rest"))servoList.get(servo).setRestvalue(value);
            writeConfiguration();
            return;
        }
        log.error("Invalid servo "+servo);
    }

    public void addServo(String name, int number){
        if(servoList.containsKey(number)){
            log.error("Servo "+ number + " already exists");
        }
        Servo newServo = new Servo();
        newServo.setName(name);
        newServo.setServonummer(number);
        servoList.put(number,newServo);
        config.getServolist().add(newServo);
        log.info("New servo added. name is "+name+", number is "+number);
        writeConfiguration();
    }

}
