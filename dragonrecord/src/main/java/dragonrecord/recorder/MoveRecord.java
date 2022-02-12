package dragonrecord.recorder;

import java.util.Arrays;

public class MoveRecord {

    private final int NUM_OF_SERVOS=16;
    private int[] record = new int[NUM_OF_SERVOS];


    public void setValue(int servo, int value){
        if(servo <0 || servo >=NUM_OF_SERVOS)return;
        if(value < -1000 || value > 1000)return;
        record[servo] = value;
    }

    private int getValue(int servo){
        return record[servo];
    }

    public int[] getRecord(){
        return record;
    }

    public void andRecord(MoveRecord andRecord){
        for(int tel=0;tel<NUM_OF_SERVOS;tel++){
            if(andRecord.getValue(tel)!=0)this.record[tel]=andRecord.getValue(tel);
        }
    }


    @Override
    public String toString() {
        return Arrays.toString(record);
    }
}
