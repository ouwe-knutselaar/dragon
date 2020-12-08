package dragonrecord;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Scanner;

public class KeyboardService implements Runnable{

    private final Logger log = Logger.getLogger(KeyboardService.class.getSimpleName());
    private boolean isRunning = true;
    OrchestrationService orchestrationService;

    public KeyboardService() throws InterruptedException {
        orchestrationService = OrchestrationService.getInstance();
    }

    @Override
    public void run() {
        String readedline;
        try {
            Scanner inkey = new Scanner(System.in);
            while(isRunning){
                if ( System.in.available() > 0 ){
                    readedline = inkey.nextLine();
                    processStringCommand(readedline);
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

    private void processStringCommand(String readedline)
    {
        try {
            readedline = readedline.toUpperCase();
            if (readedline.equals("HELP")) printHelpText();
            if (readedline.charAt(0) == 'S') toNewServoPosition(readedline);
            if (readedline.charAt(0) == 'D') orchestrationService.configReader.dumpConfig();
            if (readedline.charAt(0) == 'L') orchestrationService.dumpListOfAction();
        } catch (DragonException e)
        {
            log.error(e.getMessage());
        }
    }

    private void printHelpText()
    {
        log.info("Helptext");
        log.info("S [servonumber] [position]  Set a servo");
        log.info("E [name]  Execute action");
        log.info("D  Dump the config");
        log.info("L  List all the actions");
        log.info("A  Automovement");
    }

    public void toNewServoPosition(String readedline) throws DragonException
    {
        try {
            log.error("Execute "+readedline);
            String[] paramlist = readedline.split("[\\s\\t]+");
            if(paramlist.length != 3) throw new DragonException("invalid number of parameters: use S [x] [y]");
            int servonumber = Integer.parseInt(paramlist[1]);
            int value = Integer.parseInt(paramlist[2]);
            log.info("Set servo " + servonumber + " at position " + value);
            orchestrationService.setSingleServo(servonumber, value);
        } catch (IOException e) {
            log.error("Cannot set the servo");
             e.printStackTrace();
        } catch (NumberFormatException e) {
            log.error("number error in provided command "+e.getMessage() );
        } catch (DragonException e) {
            log.error(e.getMessage());
        }
    }
}

