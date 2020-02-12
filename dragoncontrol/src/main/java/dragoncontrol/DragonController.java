package dragoncontrol;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class DragonController extends Application{

	private DragonFileConnector fileReader=new DragonFileConnector();
	private BorderPane mainPane=new BorderPane();
	private DragonMenu menuBar = new DragonMenu();
	private ServoSlider servoSlider=new ServoSlider();	
	private DragonTerm terminal = new DragonTerm();
	
	
	public static void main(String argv[])
	{
		
		DragonController dragonController=new DragonController();
		dragonController.BuildGui();
		launch();
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Dragon Controller 1.0");

		
				
		Scene scene=new Scene(mainPane);			// Make a new scene with the main client area
		mainPane.setTop(menuBar);                  // Plaats de menubar
		mainPane.setCenter(servoSlider);   			// Plaats de controller
		mainPane.setBottom(terminal);
		
		terminal.connect("127.0.0.1", 3000);
		
		primaryStage.setScene(scene);
		
		primaryStage.show();			// Show the GUI
	}
	
	
	
	private void BuildGui()
	{
		
		
		Label ipLabel=new Label("IP Adress");
		TextField ipText=new TextField("192.168.178.28");
		FlowPane ipPane=new FlowPane();
		ipPane.setOrientation(Orientation.HORIZONTAL);
		ipPane.getChildren().addAll(ipLabel,ipText);
		
		
	}
	

}
