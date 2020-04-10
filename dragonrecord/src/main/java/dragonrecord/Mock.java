package dragonrecord;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Mock {

	public static void main(String[] args) throws IOException {
		String test = "D:\\erwin\\dragon\\actions\\bagger\\bagger.wav";

		Path path=Paths.get(test); 
		
		System.out.println("parent     "+path.getParent().toString());
		System.out.println("filename   "+path.getFileName().toString());
		System.out.println("filesystem "+path.getFileSystem().getSeparator());
		System.out.println("root       "+path.getRoot().toString());
		
		
		
		System.out.println("exists "+Files.exists(path.getParent(),LinkOption.NOFOLLOW_LINKS ));
		
		if(!Files.exists(path.getParent(),LinkOption.NOFOLLOW_LINKS ))
			{
			 Files.createDirectory(path.getParent());
			}
		make_sure_the_receiveing_directory_exists(test);
	}

	
	private static void make_sure_the_receiveing_directory_exists(String receiveFile) {
		try {
			Files.createDirectory(Paths.get(receiveFile).getParent());
		} catch (FileAlreadyExistsException e) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
