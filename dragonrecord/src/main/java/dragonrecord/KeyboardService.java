package dragonrecord;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Scanner;

public class KeyboardService implements Runnable{

    private final Logger log = Logger.getLogger(KeyboardService.class.getSimpleName());
    private boolean isRunning = true;

    public KeyboardService()
    {
        if(ConfigReader.isDebug())log.setLevel(Level.DEBUG);
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
            OrchestrationService orchestrationService = OrchestrationService.getInstance();
            if(readedline.isEmpty())return;
            if (readedline.equals("help")) printHelpText();
            if (readedline.charAt(0) == 's') toNewServoPosition(readedline);
            if (readedline.charAt(0) == 'd') orchestrationService.dumpConfig();
            if (readedline.charAt(0) == 'l') orchestrationService.dumpListOfAction();
            if (readedline.charAt(0) == 'a') orchestrationService.startRandomMoving();
            if (readedline.charAt(0) == 'b') orchestrationService.stopRandomMoving();
            if (readedline.charAt(0) == 'w') waveFilepay(readedline);
            if (readedline.charAt(0) == 'r') orchestrationService.totalReset();
            if (readedline.charAt(0) == 'e') playSequence(readedline);
            if (readedline.charAt(0) == 't') toggleDebug();
        } catch (DragonException e)
        {
            log.error(e.getMessage());
        }
    }

    private void toggleDebug() {
        if(Logger.getRootLogger().isDebugEnabled()){
            Logger.getRootLogger().setLevel(Level.INFO);
            log.info("Loglevel is set to INFO");
        }
        else{
            Logger.getRootLogger().setLevel(Level.DEBUG);
            log.info("Loglevel is set to DEBUG");
        }
    }


    private void printHelpText()
    {
        log.info("Helptext");
        log.info("s [servonumber] [position]  Set a servo");
        log.info("e [name]  Execute action");
        log.info("d  Dump the config");
        log.info("l  List all the actions");
        log.info("a  Automovement start");
        log.info("b  Automovement stop");
        log.info("r  Reset all to default");
        log.info("w  [name] Play the wave file");
        log.info("e  [name] execute sequence");
        log.info("t  toggle debug mode");
    }

    public void toNewServoPosition(String readedline) throws DragonException
    {
        try {
            log.info("Execute "+readedline);
            OrchestrationService orchestrationService = OrchestrationService.getInstance();
            String[] paramlist = readedline.split("[\\s\\t]+");
            if(paramlist.length != 3) throw new DragonException("invalid number of parameters: use S [x] [y]");
            int servonumber = Integer.parseInt(paramlist[1]);
            int value = Integer.parseInt(paramlist[2]);
            log.info("Set servo " + servonumber + " at position " + value);
            orchestrationService.setSingleServo(servonumber, value);
        } catch (NumberFormatException e) {
            log.error("number error in provided command "+e.getMessage() );
        }
    }

    private void waveFilepay(String readedline) throws DragonException{
        log.info("Execute "+readedline);
        OrchestrationService orchestrationService = OrchestrationService.getInstance();
        String[] paramlist = readedline.split("[\\s\\t]+");
        if(paramlist.length != 2) throw new DragonException("invalid number of parameters: use w [name]");
        orchestrationService.playWaveFile(paramlist[1]);
    }

    private void playSequence(String readedline) throws DragonException{
        try {
            String[] paramlist = readedline.split("[\\s\\t]+");
            if(paramlist.length != 2) throw new DragonException("invalid number of parameters: use e [name]");

            OrchestrationService orchestrationService = OrchestrationService.getInstance();
            orchestrationService.createNewRecording(paramlist[1]);
            orchestrationService.executeCurrentMotion();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("IO excpetion "+e.getMessage());
        }
    }

}

