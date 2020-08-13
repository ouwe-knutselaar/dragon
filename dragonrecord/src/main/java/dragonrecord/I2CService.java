package dragonrecord;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

public class I2CService {

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());

	private final static int PCAADDR = 0x40;
	private final static int MODE1 = 0x00;
	private final static int PRESCALE = 0xFE;
	private final static int SLEEP = 0b00010000;
	private final static int AI = 0b00100000;
	private final static int LEDBASE = 0x06;
	private final static int[] LEDBASELIST = { 6, 10, 14, 18, 22, 26, 30, 34, 38, 42, 46, 50, 54, 58, 62, 66 };
	private final static int[] FULLZERO={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	private I2CDevice i2cdev;
	private I2CBus i2cbus;
	private boolean demoMode=false;

	
	public I2CService() {
		log.debug("Make I2CService");
	}

	
	public void init(int frequency)  {
		log.debug("Init the PCA9685");

		try {
			i2cbus = I2CFactory.getInstance(I2CBus.BUS_1);
			i2cdev = i2cbus.getDevice(PCAADDR);

			int settings_mode1 = i2cdev.read(MODE1);
			log.debug("Current settings MODE1 is B" + Integer.toBinaryString(settings_mode1));

			settings_mode1 = settings_mode1 & 0xEF | AI;
			log.debug("Enable auto increment B" + Integer.toBinaryString(settings_mode1));
			i2cdev.write(MODE1, (byte) settings_mode1);

			setFrequency(frequency);
			log.debug("Init done");
			
		} catch (UnsupportedBusNumberException e) {
			log.debug("UnsupportedBusNumberException switch to demo mode");
			demoMode=true;
		} catch (IOException e) {
			log.error("IO Exception ");
			e.printStackTrace();
		} 
	}

	
	public void setFrequency(int frequency) {
		try {
			log.debug("Set the frequencyof the  PCA9685 on " + frequency + "Hz");
			int prescale = (25_000_000 / (4096 * frequency)) - 1;
			log.debug("Prescale set on " + prescale);
			if (demoMode)return;
			int settings_mode1 = i2cdev.read(MODE1);
			i2cdev.write(MODE1, (byte) (settings_mode1 | SLEEP));
			i2cdev.write(PRESCALE, (byte) prescale);
			i2cdev.write(MODE1, (byte) (settings_mode1 & 0xEF));
			Thread.sleep(500);
		} catch (IOException e) {
			log.error("Error cannot write to the I2C device");
			e.printStackTrace();
		} catch (InterruptedException e) {
			log.error("Cannot execute sleep command");
			e.printStackTrace();
		}
	}

	
	public void reset() {
		
		try {
			log.debug("Reset the PCA9685");
			if(demoMode)return;
			int settings_mode1 = i2cdev.read(MODE1);
			i2cdev.write(MODE1, (byte) (settings_mode1 | 0x80));
			writeAllServos(FULLZERO);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	
	public void writeByteArray(byte[] data) {
		try {
			log.debug("Write byte array to I2C");
			if(demoMode)return;
			i2cdev.write(LEDBASE, data);
		} catch (IOException e) {
			log.error("Error cannot write to the I2C device");
			e.printStackTrace();
		}
	}

	
	public void writeSingleLed(int lednumber, int data) throws IOException {
                byte[] result=new byte[4];
                result[1] = (byte) ((data & 0xFF000000) >> 24);		// LED ON_L
                result[0] = (byte) ((data & 0x00FF0000) >> 16);		// LED ON_H
                result[3] = (byte) ((data & 0x0000FF00) >> 8);		// LED OFF_L
                result[2] = (byte) ((data & 0x000000FF) >> 0);		// LED OFF_H
            
		//log.debug("Write " + Integer.toHexString(result[1])+" "+Integer.toHexString(result[0])+" "+Integer.toHexString(result[3])+" "+Integer.toHexString(result[2]));
		if(demoMode)return;
		i2cdev.write(LEDBASELIST[lednumber],result);
	}

	
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
