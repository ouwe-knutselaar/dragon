package dragonrecord;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileXferServer {

	private Logger log = Logger.getLogger(this.getClass().getSimpleName());


	
	public int serverloop(String receiveFile){
		int total=0;
		int bytesRead=0;
		byte[] buffer=new byte[1024];
		FileOutputStream outputFile;
		
		try {
			makeSureTheReceiveingDirectoryExists(receiveFile);

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
			
			os.flush();
			clntSock.close();
			os.close();
			outputFile.close();
			serversocket.close();
			
		} catch (FileNotFoundException e) {
			log.error("File "+receiveFile+" not found");
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		log.info("File Received of "+total+" bytes");
		return total;
	}
	
	
	private void makeSureTheReceiveingDirectoryExists(String receiveFile) {
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
		File[] dirlist=new File(source).listFiles(File::isDirectory);
		for(File directoryPath : dirlist) {
			tempSb.append(directoryPath.getName()).append(';');
		}
		return tempSb.deleteCharAt(tempSb.length()-1).toString();
	}
}
