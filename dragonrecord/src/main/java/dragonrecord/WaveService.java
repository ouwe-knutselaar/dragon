package dragonrecord;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.log4j.Logger;



public class WaveService{
	
	Logger log=Logger.getLogger(WaveService.class);
	
	
	private AudioInputStream audioInputStream;
	private float sampleRate;
	private int frameLength;
	private int frameSize;
	private int sampleFormat;
	private float duration;
	private int steps;
	private int channels;
	private AudioFormat format;
	private byte[] eightBitByteArray;
	private int[][] samples;
	private static WaveService INSTANCE;
	private double maxvol = 1;
	private Clip clip;
	private File waveFile;

	
	private WaveService()
	{
		log.debug("Make the WaveService ");
	}
	
	
	/**
	 * Singleton initializer
	 * @return
	 */
	public static WaveService getInstance()
	{
		if(INSTANCE==null)INSTANCE=new WaveService();
		return INSTANCE;
	}
	
	
	/**
	 * Load a wave fiel from disk
	 * @param audioFile File handler
	 * @return
	 */
	public boolean loadWaveFile(String audioFile)
	{
		try {
			log.debug("Load wave file "+audioFile);
			waveFile=new File(audioFile);
			audioInputStream=AudioSystem.getAudioInputStream(waveFile);		// Open de audiofile naar een inputstream
			format=audioInputStream.getFormat();								// Haal het formaat op
			
			sampleRate		= format.getSampleRate();							// Zet de varaibelen				
			frameLength		= (int)audioInputStream.getFrameLength();
			frameSize		= format.getFrameSize();
			sampleFormat	= format.getSampleSizeInBits();
			duration		= frameLength/sampleRate;
			steps			= (int) ((duration*1000)/20);
			channels		= format.getChannels();
			samples			= new int[channels][frameLength];					// Dit wordt de definitieve lijst met samples
			
			maxvol			= 0;												// Reset de max volume
			int sampleIndex = 0;												// teller om langs de sample te gaan
			eightBitByteArray = new byte[(int) (frameLength * frameSize)];		// Maak een 8bits buffer om de ruwe wave data in op te slaan
			int result 		  = audioInputStream.read(eightBitByteArray);		// Vul de 8bits buffer met ruwe data
			
			log.debug("SampleRate is "+sampleRate);				// Some logging
			log.debug("Framelength is "+frameLength);
			log.debug("Framesize is "+frameSize);
			log.debug("Samplesize is "+sampleFormat);
			log.debug("Duration is "+duration+" Seconds");
			log.debug("Number of steps is "+steps);
			log.debug("Number of channels is "+channels);
			log.debug("Number of samples is "+samples.length);
			log.debug("eightBitByteArray size is "+eightBitByteArray.length);
			log.debug("There are "+result+" bytes read");
			
			if (sampleFormat == 8) {												// verwerk een 8 bits sample
				for (int t = 0; t < eightBitByteArray.length; t++) {				// loop alle samples langs
					for (int channel = 0; channel < channels; channel++) {			// Binnen de channels
						int sample= (int) eightBitByteArray[t]+128;					// Laad een sample, een signed bit dus even een +128 correctie naar int
						if (sample > maxvol)
							maxvol = sample;										// Bepaal de hoogste sample
						samples[channel][sampleIndex] = sample;						// En laadt deze in de lijst	
					}
					sampleIndex++;
				}
			}
			
			if (sampleFormat == 16) {												// verwerk een 16 bits sample
				for (int t = 0; t < eightBitByteArray.length; t++) {				// loop door de ruwe data
					for (int channel = 0; channel < channels; channel++) {			// ga alle channels langs
						int low = (int) eightBitByteArray[t];						// lage byte
						t++;
						int high = (int) eightBitByteArray[t];						// hoge byte

						int sample = getSixteenBitSample(high, low) + 16000;		// Maak er 16 bits van
						if (sample > maxvol)
							maxvol = sample;										// en bepalen wat het maximum is
						samples[channel][sampleIndex] = sample;						// En opslaan in de array
					}
					sampleIndex++;
				}
			}
			log.debug("Sample loaded size "+sampleIndex);
			log.debug("Maximal volume is "+maxvol);
			return true;
			
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	/**
	 * Start the wave file
	 */
	public void playWave(String waveFileName) {
		try {
			log.debug("Play wave file " + waveFileName);
			waveFile = new File(waveFileName);
			DataLine.Info debug = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(debug);
			audioInputStream = AudioSystem.getAudioInputStream(waveFile);
			clip.open(audioInputStream);
			clip.setMicrosecondPosition(0);
			clip.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			log.error("Cannot play " + waveFileName);
		} catch (IOException e) {
			log.error("Cannot play " + waveFileName);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			log.error("Cannot play " + waveFileName);
		}
	}
	
	
	protected int getSixteenBitSample(int high, int low) {
		return (high << 8) + (low & 0x00ff);
	}

}
