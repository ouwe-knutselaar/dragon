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

    public void startKeyBoardService() {
        log.info("Start keyboardservice");
        Thread thisThread = new Thread(this);
        thisThread.start();
    }

    private void processStringCommand(String readedline) {
        try {
            OrchestrationService orchestrationService = OrchestrationService.getInstance();
            if(readedline.isEmpty())return;
            if (compareCommand(readedline,"help")) printHelpText();
            if (compareCommand(readedline,"ss")) toNewServoPosition(readedline);
            if (compareCommand(readedline,"dc")) orchestrationService.dumpConfig();
            if (compareCommand(readedline,"dm")) orchestrationService.dumpCurrentMotion();
            if (compareCommand(readedline,"la")) orchestrationService.dumpListOfMotion();
            if (compareCommand(readedline,"as")) orchestrationService.startRandomMoving();
            if (compareCommand(readedline,"bs")) orchestrationService.stopRandomMoving();
            if (compareCommand(readedline,"wa")) waveFilepay(readedline);
            if (compareCommand(readedline,"rd")) orchestrationService.totalReset();
            if (compareCommand(readedline,"em")) playMotion(readedline);
            if (compareCommand(readedline,"tm")) toggleDebug();
        } catch (DragonException e)
        {
            log.error(e.getMessage());
        }
    }

    private boolean compareCommand(String inputLine, String command) {
        String[] paramlist = inputLine.split("[\\s\\t]+");
        if(paramlist.length == 0)return false;
        return paramlist[0].equals(command);
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

    private void printHelpText() {
        log.info("Helptext");
        log.info("ss [servonumber] [position]  Set a servo");
        log.info("em [name]  Execute motion");
        log.info("dc  Dump the config");
        log.info("dm  dump current motion");
        log.info("la  List all the motions");
        log.info("as  Automovement start");
        log.info("bs  Automovement stop");
        log.info("rd  Reset all to default");
        log.info("wa  [name] Play the wave file");
        log.info("es  [name] execute motion");
        log.info("tm  toggle debug mode");
    }

    public void toNewServoPosition(String readedline) throws DragonException {
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

    private void playMotion(String readedline) throws DragonException {
        String[] paramlist = readedline.split("[\\s\\t]+");
        if(paramlist.length != 2) throw new DragonException("invalid number of parameters: use e [name]");

        OrchestrationService orchestrationService = OrchestrationService.getInstance();
        orchestrationService.setCurrentMotion(paramlist[1]);
        orchestrationService.executeCurrentMotion();
    }

}

