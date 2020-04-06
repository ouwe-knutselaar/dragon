package dragonrecord;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileXferServer {

	
	public int Serverloop(String receiveFile) 
	{
		int total=0;
		int bytesRead=0;
		byte[] buffer=new byte[1024];
		FileOutputStream outputFile;
		
		try {
			ServerSocket serversocket=new ServerSocket(5000);
			outputFile = new FileOutputStream(receiveFile);
			BufferedOutputStream os=new BufferedOutputStream(outputFile);
			
			System.out.println("Wait for incoming connection");
			Socket clntSock=serversocket.accept();
				
			InputStream clntInputStream=clntSock.getInputStream();
			do{
				bytesRead=clntInputStream.read(buffer);	
				os.write(buffer);
				total+=bytesRead;
			}while(bytesRead>-1);	
			clntSock.close();
			os.close();
			outputFile.close();
			serversocket.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		
		System.out.println("File Received");
		return total;
	}
	
	
}
