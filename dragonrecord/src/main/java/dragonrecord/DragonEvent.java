package dragonrecord;

import java.io.IOException;

public interface DragonEvent {
	
	public void handle(String msg,int val1,int val2) throws InterruptedException, DragonException, IOException;

}
