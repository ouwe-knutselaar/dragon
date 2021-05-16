package dragonrecord;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;


public class I2CService {

	private final Logger log = Logger.getLogger(this.getClass().getSimpleName());

	private static final int PCAADDR = 0x40;
	private static final int MODE1 = 0x00;
	private static final int PRESCALE = 0xFE;
	private static final int SLEEP = 0b00010000;
	private static final int AI = 0b00100000;
	private static final int LEDBASE = 0x06;
	//private static final int[] LEDBASELIST = { 6, 10, 14, 18, 22, 26, 30, 34, 38, 42, 46, 50, 54, 58, 62, 66 };
	private static final int[] FULLZERO={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	private I2CDevice i2cdev;
	private boolean demoMode=false;

	public I2CService() {
		if(ConfigReader.isDebug())log.setLevel(Level.DEBUG);
		log.info("Make I2CService");
	}

	
	public void init(int frequency) {
		log.info("Init the PCA9685");

		try {
			I2CBus i2cbus = I2CFactory.getInstance(I2CBus.BUS_1);
			i2cdev = i2cbus.getDevice(PCAADDR);

			int settingsMode1 = i2cdev.read(MODE1);
			log.info("Current settings MODE1 is B" + Integer.toBinaryString(settingsMode1));

			settingsMode1 = settingsMode1 & 0xEF | AI;
			log.info("Enable auto increment B" + Integer.toBinaryString(settingsMode1));
			i2cdev.write(MODE1, (byte) settingsMode1);

			setFrequency(frequency);
			log.info("Init done");
			
		} catch (UnsupportedBusNumberException e) {
			log.info("UnsupportedBusNumberException switch to demo mode"+e.getMessage());
			demoMode = true;
		} catch (IOException e) {
			log.error("IO Exception, switch to demo mode"+e.getMessage());
			e.printStackTrace();
			demoMode = true;
		} 
	}

	
	public void setFrequency(int frequency) {
		try {
			log.info("Set the frequencyof the  PCA9685 on " + frequency + "Hz");
			int prescale = (25_000_000 / (4096 * frequency)) - 1;
			log.info("Prescale set on " + prescale);
			if (demoMode)return;
			int settingsMode1 = i2cdev.read(MODE1);
			i2cdev.write(MODE1, (byte) (settingsMode1 | SLEEP));
			i2cdev.write(PRESCALE, (byte) prescale);
			i2cdev.write(MODE1, (byte) (settingsMode1 & 0xEF));
			Thread.sleep(500);
		} catch (IOException e) {
			log.error("Error cannot write to the I2C device");
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	
	public void reset() {
		
		try {
			log.info("Reset the PCA9685");
			if(demoMode)return;
			int settingsMode1 = i2cdev.read(MODE1);
			i2cdev.write(MODE1, (byte) (settingsMode1 | 0x80));
			writeAllServos(FULLZERO);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*public void writeSingleLed(int lednumber, int data) throws DragonException {

		if(lednumber < SERVOMIN)throw new DragonException ("led or servo number cannot be less then 0");
		if(lednumber > SERVOMAX)throw new DragonException("led or servo number cannot be more then 15");
		if(data < 0)throw new DragonException("led or servo value cannot be more then 0");
		if(data > 4095)throw new DragonException("led or servo value cannot be more then 4095");

        byte[] result=new byte[4];
        result[1] = (byte) ((data & 0xFF000000) >> 24);		// LED ON_L
        result[0] = (byte) ((data & 0x00FF0000) >> 16);		// LED ON_H
        result[3] = (byte) ((data & 0x0000FF00) >> 8);		// LED OFF_L
        result[2] = (byte) ((data & 0x000000FF) >> 0);		// LED OFF_H
            
		if(demoMode)return;
		try {
			i2cdev.write(LEDBASELIST[lednumber],result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	
	public void writeAllServos(int[] valueList) {
		try {
			log.debug("Write valuelist tot the I2C device " + Arrays.toString(valueList));
			if (demoMode)return;
			byte[] byteValueList = new byte[valueList.length * 4];
			for (int tel = 0; tel < valueList.length; tel++) {
				byte[] result = intToBytes(valueList[tel]);
				byteValueList[tel * 4] = result[0];
				byteValueList[1 + tel * 4] = result[1];
				byteValueList[2 + tel * 4] = result[2];
				byteValueList[3 + tel * 4] = result[3];
			}
			i2cdev.write(LEDBASE, byteValueList);
		} catch (IOException e) {
			log.error("Error cannot write to the I2C device");
			e.printStackTrace();
		}
	}
	

	private static byte[] intToBytes(final int data) {
		return new byte[] { (byte) ((data >> 16) & 0xff), 
							(byte) ((data >> 24) & 0xff), 
							(byte) ((data >> 0) & 0xff),
							(byte) ((data >> 8) & 0xff), };
	}

	

}
