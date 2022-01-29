package dragonrecord.config;

public class Servo {

    private int servonummer;
    private String name="servo1";
    private int minvalue=0;
    private int maxvalue=4095;
    private int restvalue=2048;
    private int steps=20;
    private double factor=0.5;

    public int getServonummer() {
        return servonummer;
    }

    public void setServonummer(int servonummer) {
        this.servonummer = servonummer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinvalue() {
        return minvalue;
    }

    public void setMinvalue(int minvalue) {
        this.minvalue = minvalue;
    }

    public int getMaxvalue() {
        return maxvalue;
    }

    public void setMaxvalue(int maxvalue) {
        if(maxvalue>4096)return;
        if(maxvalue<minvalue)return;
        this.maxvalue = maxvalue;
    }

    public int getRestvalue() {
        return restvalue;
    }

    public void setRestvalue(int restvalue) {
        this.restvalue = restvalue;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }


    @Override
    public String toString() {
        return "Servo{" +
                "servonummer=" + servonummer +
                ", name='" + name + '\'' +
                ", minvalue=" + minvalue +
                ", maxvalue=" + maxvalue +
                ", restvalue=" + restvalue +
                ", steps=" + steps +
                ", factor=" + factor +
                '}';
    }
}
