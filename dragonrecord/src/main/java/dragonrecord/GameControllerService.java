package dragonrecord;


import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Optional;

public class GameControllerService implements Runnable{

    private final Logger log = Logger.getLogger(KeyboardService.class.getSimpleName());
    private OrchestrationService orchestrationService = OrchestrationService.getInstance();
    private ConfigReader configReader = ConfigReader.getInstance();


    private boolean isRunning = true;
    private int pollTime= 25;
    private  Controller gamepad;

    public GameControllerService(){


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
        log.info("Start game controller service "+this.gamepad.getName());
        Thread thisThread = new Thread(this);
        thisThread.start();
    }


    @Override
    public void run() {
        Event event = new Event();
        while(isRunning){
            gamepad.poll();
            EventQueue eventQueue = gamepad.getEventQueue();
            while(eventQueue.getNextEvent(event)){
                processEvent(event);
            }
            try {
                Thread.sleep(pollTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void processEvent(Event event) {

        switch(event.getComponent().getIdentifier().toString()){
            case "rx": log.info("move bek "+event.getValue()+" "+event.getComponent().getIdentifier());
                        orchestrationService.setSingleServo(configReader.getRx(),(int)(50*event.getValue()));
                       break;
            case "x": log.info("move bek "+event.getValue()+" "+event.getComponent().getIdentifier());
                orchestrationService.setSingleServo(configReader.getXaxis(),(int)(50*event.getValue()));
                break;

            case "ry": log.info("move bek "+event.getValue()+" "+event.getComponent().getIdentifier());
                orchestrationService.setSingleServo(configReader.getRy(),(int)(50*event.getValue()));

                break;
            case "y": log.info("move bek "+event.getValue()+" "+event.getComponent().getIdentifier());
                orchestrationService.setSingleServo(configReader.getYaxis(),(int)(50*event.getValue()));

                break;
        }


    }


}
