package dragonrecord;

import org.apache.log4j.Logger;

public class RandomMovementService {

    private final Logger log = Logger.getLogger(this.getClass().getSimpleName());
    private boolean running=true;
    private static RandomMovementService classInstance;


    public static RandomMovementService getInstance() {
        if(classInstance == null)
        {
            classInstance = new RandomMovementService();
        }
        return classInstance;
    }
}
