package dragonrecord;

import dragonrecord.config.ConfigReader;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class WaveService{
	
	private static final Logger  log=Logger.getLogger(WaveService.class);
	private static WaveService instance;
	private File waveFile;

	private WaveService()
	{
		if(ConfigReader.getInstance().isDebug())log.setLevel(Level.DEBUG);
		log.info("Make the WaveService ");
	}

	/**
	 * Singleton initializer
	 * @return
	 */
	public static WaveService getInstance()
	{
        Mixer.Info[] infos = AudioSystem.getMixerInfo();
        for(Mixer.Info minfo : infos)
        {
            log.debug("mixer info");
            log.debug("--"+minfo.getName());
            log.debug("--"+minfo.getDescription());
            log.debug("--"+minfo.getVendor());
            log.debug("--"+minfo.getVersion());
            log.debug("--"+minfo.hashCode());
        }


		if(instance ==null) instance =new WaveService();
		return instance;
	}
	
	
	/**
	 * Load a wave fiel from disk
	 * @param audioFile File handler
	 * @return
	 */
	public boolean loadWaveFile(String audioFile)
	{
		try {
			log.info("Load wave file "+audioFile);
			waveFile=new File(audioFile);
			AudioInputStream audioInputStream= AudioSystem.getAudioInputStream(waveFile);		// Open de audiofile naar een inputstream
            AudioFormat format=audioInputStream.getFormat();								// Haal het formaat op
			
			float sampleRate	= format.getSampleRate();							// Zet de varaibelen
			int frameLength		= (int)audioInputStream.getFrameLength();
			int frameSize		= format.getFrameSize();
			int sampleFormat	= format.getSampleSizeInBits();
			float duration		= frameLength/sampleRate;
			int steps			= (int) ((duration*1000)/20);
			int channels		= format.getChannels();
			int[][] samples		= new int[channels][frameLength];					// Dit wordt de definitieve lijst met samples

			double maxvol		= 0;												// Reset de max volume
			int sampleIndex = 0;												// teller om langs de sample te gaan
			byte[]eightBitByteArray = new byte[(int) (frameLength * frameSize)];		// Maak een 8bits buffer om de ruwe wave data in op te slaan
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
				for (int t = 1; t < eightBitByteArray.length; t=t+2) {				// loop door de ruwe data
					for (int channel = 0; channel < channels; channel++) {			// ga alle channels langs
						int low = (int) eightBitByteArray[t-1];						// lage byte
						int high = (int) eightBitByteArray[t];						// hoge byte

						int sample = getSixteenBitSample(high, low) + 16000;		// Maak er 16 bits van
						if (sample > maxvol)
							maxvol = sample;										// en bepalen wat het maximum is
						samples[channel][sampleIndex] = sample;						// En opslaan in de array
					}
					sampleIndex++;
				}
			}
			log.info("Sample loaded size "+sampleIndex);
			log.info("Maximal volume is "+maxvol);
			return true;
			
		} catch (UnsupportedAudioFileException | IOException e) {
            log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	/**
	 * Start the wave file
	 */
	public void playWave(String waveFileName) {
		try {
			log.info("Play wave file " + waveFileName);
			waveFile = new File(waveFileName);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(waveFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.setMicrosecondPosition(0);

			clip.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			log.error("Cannot play, line unavailable " + waveFileName);
			log.error(e.getMessage());
		} catch (IOException e) {
            e.printStackTrace();
			log.error("Cannot play IO exception" + waveFileName);
            log.error(e.getMessage());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			log.error("Cannot play this audio file " + waveFileName);
            log.error(e.getMessage());
		}
	}
	
	
	protected int getSixteenBitSample(int high, int low) {
		return (high << 8) + (low & 0x00ff);
	}

}
