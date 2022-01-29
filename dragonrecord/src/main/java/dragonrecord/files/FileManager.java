package dragonrecord.files;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private String actionPath;
    private List<String> actionList = new ArrayList<>();
    private Logger log = Logger.getLogger(this.getClass().getSimpleName());

    public FileManager(String actionPath){
        this.actionPath = actionPath;
        try {
            Files.walk(Paths.get(actionPath)).filter(file -> file.toFile().isDirectory()).forEach(file -> actionList.add(file.toString()));
            actionList.forEach(name -> log.info("Found action "+name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
