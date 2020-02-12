package dragoncontrol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Erwin on 20-8-2019.
 */

public class DragonTelnet  {

	int timeout=20;
	
    Socket socket;
    InputStream inStream;
    OutputStream outStream;
    byte inbuffer[]=new byte[10];

    public DragonTelnet()
    {
    	
    }

    
    public boolean connect(String ipAdres,int port )
    {
    	 try {
             System.out.println("Socket no connected, make new connection");
             socket=new Socket(ipAdres,port);
             socket.setSoTimeout(timeout);

             System.out.println("Get inputstream");
             inStream=socket.getInputStream();
             outStream=socket.getOutputStream();
         } catch (IOException e) {
             e.printStackTrace();
             return false;         
         }
    	 return true;
    }
    
    
    public String setServo(int servoNumber, int value) {

        try {
        	String sendString=String.format("p%02d%04d", servoNumber,value);
        	System.out.println("Send "+sendString);
        	outStream.write(sendString.getBytes());
            inStream.read(inbuffer,0,10);
            System.out.println("Read:"+inbuffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
