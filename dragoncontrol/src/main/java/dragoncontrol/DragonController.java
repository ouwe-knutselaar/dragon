package dragoncontrol;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class DragonController extends Application{

	private final BorderPane mainPane=new BorderPane();
	private final MainScreen mainScreen =new MainScreen();
	
	public static void main(String[] argv)
	{
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Dragon Controller 1.0");
		Scene scene=new Scene(mainPane);			// Make a new scene with the main client area
		scene.getStylesheets().add("dragonstyle.css");
		mainPane.setCenter(mainScreen);   			// Plaats de controller
		primaryStage.setScene(scene);
		primaryStage.show();			// Show the GUI
	}

   @Override
   public void stop(){
	    mainScreen.closeAll();
   }

}
