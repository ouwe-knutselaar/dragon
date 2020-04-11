package dragonrecord;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.log4j.Logger;

public class FileXferServer {

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());
	
	public int Serverloop(String receiveFile) 
	{
		int total=0;
		int bytesRead=0;
		byte[] buffer=new byte[1024];
		FileOutputStream outputFile;
		
		try {
			make_sure_the_receiveing_directory_exists(receiveFile);
			
			ServerSocket serversocket=new ServerSocket(5000);
			outputFile = new FileOutputStream(receiveFile);
			BufferedOutputStream os=new BufferedOutputStream(outputFile);
			log.info("Wait for incoming connection");
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
		
		log.info("File Received");
		return total;
	}
	
	
	private void make_sure_the_receiveing_directory_exists(String receiveFile) {
		try {
			Files.createDirectory(Paths.get(receiveFile).getParent());
		} catch (FileAlreadyExistsException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("Created new directory "+Paths.get(receiveFile).getParent().toString());
	}
	
	
	public String getSemiColonSeparatedDirectoryListing(String source) {
		StringBuilder tempSb = new StringBuilder();
		try {
			Files.newDirectoryStream(Paths.get(source))
					.forEach(item -> tempSb.append(item.getFileName().toString()).append(';'));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempSb.deleteCharAt(tempSb.length()-1).toString();

	}
}
