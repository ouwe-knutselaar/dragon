package dragonrecord;


import net.java.games.input.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.Arrays;
import java.util.Optional;

public class GameControllerService implements Runnable{

    private final Logger log = Logger.getLogger(GameControllerService.class.getSimpleName());
    private OrchestrationService orchestrationService = OrchestrationService.getInstance();
    private ConfigReader configReader = ConfigReader.getInstance();


    private boolean isRunning = true;
    private int pollTime= 25;
    private  Controller gamepad;

    public GameControllerService(){
        System.setProperty("jinput.loglevel","FINEST");
        if(ConfigReader.isDebug())log.setLevel(Level.DEBUG);

        log.info("open gamecontroller");
        Optional<Controller> gamePad = Arrays.
                asList(ControllerEnvironment.getDefaultEnvironment().getControllers()).stream().
                filter(controller -> controller.
                        getType().
                        equals(Controller.Type.GAMEPAD)).
                findFirst();
        if(gamePad.isPresent())startService(gamePad.get());
    }

    public void startService(Controller gamepad) {
        this.gamepad = gamepad;

        Thread thisThread = new Thread(this);
        thisThread.start();
    }


    @Override
    public void run() {
        log.info("Start game controller service "+gamepad.getName());
        Event event = new Event();
        while(isRunning){
            if(gamepad.poll()){
                EventQueue eventQueue = gamepad.getEventQueue();
                while(eventQueue.getNextEvent(event)){
                    processEvent(event);
                }
                log.debug("Poll data "+gamepad.getComponent(Component.Identifier.Axis.X).getPollData());
            }
            else{
                log.error("Controller in not longer valid");
            }

            try {
                Thread.sleep(pollTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.info("Gamecontroller stopped");
    }

    private void processEvent(Event event) {
        log.info(event.getComponent().getIdentifier()+" "+event.getValue());
        int total=0;
        switch(event.getComponent().getIdentifier().toString()){
            case "x":
                total = gamePadToServo(3,event.getValue());
                orchestrationService.setSingleServo(3,total);
                log.debug("move "+configReader.getServoName(3)+" "+event.getValue()+" "+event.getComponent().getIdentifier() + " swing:"+total);
                break;

            case "y":
                total = gamePadToServo(4,event.getValue());
                orchestrationService.setSingleServo(4,total);
                log.debug("move "+configReader.getServoName(4)+" "+event.getValue()+" "+event.getComponent().getIdentifier() + " swing:"+total);
                break;

            case "z":
                total = gamePadToServo(5,event.getValue());
                orchestrationService.setSingleServo(5,total);
                log.debug("move "+configReader.getServoName(5)+" "+event.getValue()+" "+event.getComponent().getIdentifier() + " swing:"+total);
                break;

            case "rx":
                total = gamePadToServo(7,event.getValue());
                orchestrationService.setSingleServo(7,total);
                log.debug("move "+configReader.getServoName(7)+" "+event.getValue()+" "+event.getComponent().getIdentifier() + " swing:"+total);
                break;
        }

    }


    private int gamePadToServo(int servo,float gamePadValue){
        float max = configReader.getServoMaxValue(servo);
        float min = configReader.getServoMinValue(servo);
        float defaultValue = configReader.getServoDefaultValue(servo);

        if(gamePadValue<0) {
            return (int)(defaultValue-((defaultValue-min)*gamePadValue));
        }
        return (int)(((max-defaultValue)*gamePadValue)+defaultValue);
    }

    public void stop(){
        isRunning=false;
    }

}
