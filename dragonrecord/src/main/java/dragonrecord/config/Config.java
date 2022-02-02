package dragonrecord.config;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private List<Servo> servolist = new ArrayList<>();
    private boolean debug;
    private int timestep;
    private String actionpath;
    private int randommaxinterval;

    public List<Servo> getServolist() {
        return servolist;
    }

    public void setServolist(List<Servo> servolist) {
        this.servolist = servolist;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getTimestep() {
        return timestep;
    }

    public void setTimestep(int timestep) {
        this.timestep = timestep;
    }

    public String getActionpath() {
        return actionpath;
    }

    public void setActionpath(String actionpath) {
        this.actionpath = actionpath;
    }

    public int getRandommaxinterval() {
        return randommaxinterval;
    }

    public void setRandommaxinterval(int randommaxinterval) {
        this.randommaxinterval = randommaxinterval;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Config").append(System.lineSeparator());
        out.append("debug=").append(debug).append(System.lineSeparator());
        out.append("timestep=").append(timestep).append(System.lineSeparator());
        out.append("actionpath=").append(actionpath).append(System.lineSeparator());
        out.append("randommaxinterval=").append(randommaxinterval).append(System.lineSeparator());
        servolist.forEach(servo -> out.append(servo.toString()).append(System.lineSeparator()));
        return out.toString();
    }
}
