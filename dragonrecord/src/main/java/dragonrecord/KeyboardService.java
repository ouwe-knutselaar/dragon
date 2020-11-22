package dragonrecord;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Scanner;

public class KeyboardService implements Runnable{

    private Logger log = Logger.getLogger(KeyboardService.class.getSimpleName());
    private boolean isRunning = true;
    OrchestrationService orchestrationService = OrchestrationService.GetInstance();

    public KeyboardService()
    {

    }

    @Override
    public void run() {
        String readedline = "";
        try {
            Scanner inkey = new Scanner(System.in);
            while(isRunning){
                if ( System.in.available() > 0 ){
                    readedline = inkey.nextLine();
                    log.info("Command :"+readedline);
                    ProcessStringCommand(readedline);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startKeyBoardService()
    {
        log.info("Start keyboardservice");
        Thread thisThread = new Thread(this);
        thisThread.start();
    }

    private void ProcessStringCommand(String readedline)
    {
        readedline=readedline.toUpperCase();
        if(readedline.equals("HELP"))PrintHelpText();
        if(readedline.charAt(0) == 'S')ToNewServoPosition(readedline);
    }

    private void PrintHelpText()
    {
        log.info("Helptext");
        log.info("S [servonumber] [position]");
    }

    public void ToNewServoPosition(String readedline)
    {
        String[] paramlist = readedline.split("[\\s\\t]+");
        if(paramlist.length == 3) {
            int servonumber = Integer.parseInt(paramlist[1]);
            int value = Integer.parseInt(paramlist[2]);

            log.info("Set servo "+servonumber+" at position "+value);
            try {
                orchestrationService.setSingleServo(servonumber,value);
            } catch (IOException e) {
                log.error("Cannot set the servo");
                e.printStackTrace();
            }
        }
    }
}

