package dragonrecord;

import org.apache.log4j.Logger;
import java.io.*;
import java.util.Scanner;

public class ConfigReader {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private String configFile = "config.conf";
    private final Servo[] servoList=new Servo[16];
    private static final ConfigReader INSTANCE = new ConfigReader();
    public static ConfigReader getInstance(){return INSTANCE;}

    public void setConfigFile(String newFileName)
    {
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

    public void readConfiguration()
    {
        try {
            File inFile=new File(configFile);
            Scanner scan = new Scanner(inFile);
            log.info("Read "+configFile);
            while(scan.hasNextLine())
            {
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
        if(configString.startsWith("servo"))processLineForServer(configString);
    }

    private void processLineForServer(String configString) {
        String[] paramlist  = configString.split(" ");
        if(paramlist.length!=5)return;
        for(int tel=0;tel<16;tel++)
        {
            if(paramlist[0].equals("servo"+tel))
            {
                servoList[tel] = new Servo();
                servoList[tel].defaultValue= Integer.parseInt(paramlist[1]);
                servoList[tel].minValue= Integer.parseInt(paramlist[2]);
                servoList[tel].maxValue= Integer.parseInt(paramlist[3]);
                servoList[tel].name=paramlist[4];
            }
        }
    }

    public void dumpConfig()
    {
        log.info("List the configuration");
        for(int tel = 0 ; tel<16 ; tel++) {
            log.info("servo"+tel+" "+servoList[tel].toString());
        }
    }

    private static class Servo{

        int minValue;
        int maxValue;
        int defaultValue;
        String name;

        public String toString()
        {
            return " "+defaultValue+" "+minValue+" "+maxValue+" "+name;
        }
    }

}
