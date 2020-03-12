package dragoncontrol;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class DragonController extends Application{

	private DragonFileConnector fileReader=new DragonFileConnector();

	private BorderPane mainPane=new BorderPane();
	private ServoSlider servoSlider=new ServoSlider();	
	//private DragonTerm terminal = new DragonTerm();
	
	
	public static void main(String argv[])
	{
		DragonController dragonController=new DragonController();
		launch();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Dragon Controller 1.0");
		Scene scene=new Scene(mainPane);			// Make a new scene with the main client area
		mainPane.setCenter(servoSlider);   			// Plaats de controller
		primaryStage.setScene(scene);
		primaryStage.show();			// Show the GUI
	}
	
	


}
