package dragoncontrol;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileXferClient {

	String xsendFile="d:\\tmp\\foto-12.zip";
	
	public int SendFile(String sendFile,String address) 
	{
		byte[] buffer=new byte[1024];
		int bytesRead=0;
		int total=0;
		try {
			InetAddress addr = InetAddress.getByName(address);
			FileInputStream fis=new FileInputStream(sendFile);
			System.out.println("Connect to server");
			Socket sendSock=new Socket(addr,5000);
			
			OutputStream oos=sendSock.getOutputStream();
			do{
				bytesRead=fis.read(buffer);
				oos.write(buffer);
				total +=bytesRead;
			}while(bytesRead>-1);
			
			System.out.println("File send");
			oos.close();
			fis.close();
			sendSock.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return total;
	}
	
}
