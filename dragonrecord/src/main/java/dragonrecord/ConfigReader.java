package dragonrecord;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.Scanner;

public class ConfigReader {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private String configFile = "config.conf";
    private static boolean debug=false;
    private static final ConfigReader INSTANCE = new ConfigReader();
    private static final String SERVO_NAME = "servo";

    private final Servo[] servoList = new Servo[16];
    private int timestep = 20;

    private int rx,ry,Xaxis,Yaxis,knop1,knop2,knop3,knop4,knop5,knop6,knop7,knop8,knop9;

    public static ConfigReader getInstance(){
        return INSTANCE;
    }

    private ConfigReader(){};

    public void setConfigFile(String newFileName) {
        configFile = newFileName;
        processLineForServer("servo0 0 0 0 servo0");
        processLineForServer("servo1 0 0 0 servo1");
        processLineForServer("servo2 0 0 0 servo2");
        processLineForServer("servo3 0 0 0 servo3");
        processLineForServer("servo4 0 0 0 servo4");
        processLineForServer("servo5 0 0 0 servo5");
        processLineForServer("servo6 0 0 0 servo6");
        processLineForServer("servo7 0 0 0 servo7");
        processLineForServer("servo8 0 0 0 servo8");
        processLineForServer("servo9 0 0 0 servo9");
        processLineForServer("servo10 0 0 0 servo10");
        processLineForServer("servo11 0 0 0 servo11");
        processLineForServer("servo12 0 0 0 servo12");
        processLineForServer("servo13 0 0 0 servo13");
        processLineForServer("servo14 0 0 0 servo14");
        processLineForServer("servo15 0 0 0 servo15");
    }

    public void readConfiguration() {
        try {
            File inFile = new File(configFile);
            Scanner scan = new Scanner(inFile);
            log.info("Read "+configFile);
            while(scan.hasNextLine()) {
                processReadedLine(scan.nextLine());
            }
            log.info("Finished reading config file");
            scan.close();
        } catch (FileNotFoundException e) {
            log.error("file "+configFile+" not found. Use defaults");
        }
    }

    private void processReadedLine(String configString) {
        log.info(configString);
        if(configString.length()==0)return;
        if(configString.charAt(0)=='#')return;

        configString = configString.replaceAll("[\\t\\s]+"," ");
        processLineForServer(configString);
    }

    private void processLineForServer(String configString) {
        String[] paramlist = configString.split(" ");
        if (paramlist[0].startsWith(SERVO_NAME) && paramlist.length == 5) {
            for (int tel = 0; tel < 16; tel++) {
                if (paramlist[0].equals(SERVO_NAME + tel)) {
                    servoList[tel] = new Servo();
                    servoList[tel].defaultValue = Integer.parseInt(paramlist[3]);
                    servoList[tel].minValue = Integer.parseInt(paramlist[1]);
                    servoList[tel].maxValue = Integer.parseInt(paramlist[2]);
                    servoList[tel].name = paramlist[4];
                }
            }
       }

        if (paramlist[0].equals("timestep")) {
            timestep = Integer.parseInt(paramlist[1]);
        }

        if (paramlist[0].equals("debug") && paramlist.length == 2 && paramlist[1].equals("true")) {
            debug = true;
        }
    }

    public void dumpConfig() {
        log.info("List the configuration");
        for(int tel = 0 ; tel<16 ; tel++) {
            log.info(SERVO_NAME+" "+tel+" "+servoList[tel].toString());
        }
    }

    public String getSemiColonSeparatedServoValuesListing() {
        StringBuilder servovalueList = new StringBuilder();
        for(int tel=0 ; tel<servoList.length ; tel++) {
            servovalueList.append(servoList[tel].toString()).append(' ').append(tel).append(';');
        }
        return servovalueList.toString();
    }

    public int getTimeStep() {
        return timestep;
    }

    public boolean isValidServo(int servoNumber){
        return servoList[servoNumber].maxValue!=0;
    }

    public int getServoMinValue(int servoNumber){
        return servoList[servoNumber].minValue;
    }

    public int getServoMaxValue(int servoNumber){
        return servoList[servoNumber].maxValue;
    }

    public int getServoDefaultValue(int servoNumber){
        return servoList[servoNumber].defaultValue;
    }

    public String getServoName(int servoNumber){
        return servoList[servoNumber].name;
    }


    public static boolean isDebug(){ return debug; };

    private static class Servo{
        int minValue;
        int maxValue;
        int defaultValue;
        String name;
        public String toString()
        {
            return name+" "+minValue+" "+maxValue+" "+defaultValue+" "+name;
        }
    }
}
