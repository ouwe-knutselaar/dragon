package dragoncontrol;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import org.apache.log4j.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;



public class ServoSlider extends GridPane
{
	private final Logger log = Logger.getLogger(ServoSlider.class.getSimpleName());

	int servo = 0;
	private int udpPort=80;
	private String host="192.168.2.216";
	private InetAddress ipaddress;

	private Slider slider=new Slider();

	private Label ipAdressLabel=new Label("ip adres");
	private Label ipPortLabel=new Label("port");
	private Label minLabel=new Label("min");
	private Label maxLabel=new Label("max");
	private Label restLabel=new Label("rest");
	Label servoLabel=new Label("servo");
	Label actionName=new Label("sequence name");
	Label actionType=new Label("sequence type");
	Label servoNameLabel=new Label("servo name");
	
	Text messageField = new Text("messages");
	Text servoName = new Text("empty");
	
	TextField ipAdressField=new TextField(host);
	TextField ipPortField=new TextField("3001");
	TextField minField;
	TextField maxField;
	TextField restField;
	
	ObservableList<String> actionNames=FXCollections.observableArrayList();
	ComboBox<String> actionNamesList =new ComboBox<>(actionNames);
	
	ObservableList<String> actionTypes=FXCollections.observableArrayList();
	ComboBox<String> actionTypesList =new ComboBox<>(actionTypes);

	private GridPane fieldGrid = new GridPane();
	private GridPane buttonGrid = new GridPane();
	
	private Connector connector;
	
	private String[] servoList={"empty"};
	private ComboBox<String> servoDropDownList=new ComboBox<>(FXCollections.observableArrayList(servoList));

	Alert errorAlert = new Alert(Alert.AlertType.ERROR);
	
	public ServoSlider() 
	{

		actionNames.add("empty");
		actionNamesList.setEditable(true);
		
		actionTypes.add("male");
		actionTypes.add("female");
		actionTypes.add("myself");
		actionTypes.add("child");
		actionTypesList.getSelectionModel().select(0);

		ipAdressLabel=new Label("ip adres");
		ipPortLabel=new Label("port");
		minLabel=new Label("min");
		maxLabel=new Label("max");
		restLabel=new Label("rest");
		servoLabel=new Label("servo");
		
		ipAdressField=new TextField(host);
		ipPortField=new TextField("3001");
		minField=new TextField("0");
		maxField=new TextField("0");
		restField=new TextField("0");

		fieldGrid.add(minLabel, 0,0);
		fieldGrid.add(restLabel, 0,1);
		fieldGrid.add(maxLabel, 0,2);
		fieldGrid.add(ipAdressLabel, 0, 3);
		fieldGrid.add(ipPortLabel, 0, 4);
		fieldGrid.add(servoLabel, 0, 5);
		fieldGrid.add(actionName, 0, 6);
		fieldGrid.add(actionType, 0, 7);
		fieldGrid.add(servoNameLabel,0,8);
		
		fieldGrid.add(minField, 1,0);
		fieldGrid.add(restField, 1,1);
		fieldGrid.add(maxField, 1,2);
		fieldGrid.add(ipAdressField, 1, 3);
		fieldGrid.add(ipPortField, 1, 4);
		fieldGrid.add(servoDropDownList, 1, 5);
		fieldGrid.add(actionNamesList, 1, 6);
		fieldGrid.add(actionTypesList, 1, 7);
		fieldGrid.add(servoName, 1, 8);
		
		// colom , row, colspan, rowspan
		DragonButton connect = new DragonButton.ButtonBuilder().setButtonName("Connect").hooverOvertext("Make a connection").textOutputField(messageField).build();
		buttonGrid.add(connect,0,0);
		DragonButton smoothRecordingButton = new DragonButton.ButtonBuilder().setButtonName("Smooth").hooverOvertext("Smooth the movement").textOutputField(messageField).build();
		buttonGrid.add(smoothRecordingButton,1,0);
		DragonButton startRecordingButton = new DragonButton.ButtonBuilder().setButtonName("record [F1]").build();
		buttonGrid.add(startRecordingButton, 2, 0);

		DragonButton createNewRecordButton = new DragonButton.ButtonBuilder().setButtonName("create").hooverOvertext("Create a new movement").textOutputField(messageField).build();
		buttonGrid.add(createNewRecordButton, 0, 1);
		DragonButton resetButton = new DragonButton.ButtonBuilder().setButtonName("Reset").hooverOvertext("Reset the movement").textOutputField(messageField).build();
		buttonGrid.add(resetButton,1,1);
		DragonButton stopRecordingButton = new DragonButton.ButtonBuilder().setButtonName("stop [F2]").build();
		buttonGrid.add(stopRecordingButton, 2, 1);

		Button saveRecordingButton = new Button("save");
		buttonGrid.add(saveRecordingButton, 0, 2);
		Button clearTrackButton = new Button("clear track [F3]");
		buttonGrid.add(clearTrackButton,1,2);
		Button uploadWavButton = new Button("wav upload");
		buttonGrid.add(uploadWavButton,2,2);

		DragonButton dumpRecordingButton = new DragonButton.ButtonBuilder().setButtonName("Dump").hooverOvertext("Dump the movement").textOutputField(messageField).build();
		buttonGrid.add(dumpRecordingButton, 0, 3);
		DragonButton playRecordingButton = new DragonButton.ButtonBuilder().setButtonName("Play").hooverOvertext("Play movement").textOutputField(messageField).build();
		buttonGrid.add(playRecordingButton, 2, 3);
		
		fieldGrid.setHgap(10);
		fieldGrid.setVgap(10);
		
		buttonGrid.setHgap(10);
		buttonGrid.setVgap(10);
		
		this.setPadding(new Insets(20));
		this.setVgap(20);
		this.setHgap(20);
		
		this.add(fieldGrid, 0, 0);
		this.add(buttonGrid, 0, 1);
		this.add(slider, 1, 0);
		
		messageField.prefWidth(400);
		this.add(messageField, 0, 2,2,1);
		
		slider.setOrientation(Orientation.VERTICAL);
		slider.setShowTickLabels(true);
		
		
		minField.textProperty().addListener((observable, oldValue, newValue) ->
			Globals.servoLimitList.get(servo).setMinPos(Integer.parseInt(newValue))
		);
		
		
		maxField.textProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue.equals(""))return;
			Globals.servoLimitList.get(servo).setMaxPos(Integer.parseInt(newValue));
		});
		
		
		slider.valueProperty().addListener((observable, oldValue, newValue) -> connector.sendUDP(String.format("p %02d %04d", servo,newValue.intValue())));
	
		
		connect.setOnMouseClicked(event -> {
			try {
				ipaddress = InetAddress.getByName(host);
				connector = new Connector(ipAdressField.getText(),Integer.parseInt(ipPortField.getText()),messageField);
				if(!connector.Connect()){
					showAlert("Cannot connect to "+host);
					return;
				}
				log.info("Host is "+host+" at port "+ipPortField.getText()+" at "+ ipaddress.toString());
				receiveListOfActions();

			} catch (UnknownHostException e) {
				e.printStackTrace();
				showAlert("Unknown host "+e.getMessage());
			}
		});

		ipPortField.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(!newValue){
					udpPort=Integer.parseInt(ipPortField.getText());
					messageField.setText("Updport set to "+udpPort);
				}
			}
		});

		ipAdressField.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(!newValue){
					host=ipAdressField.getText();
					messageField.setText("host is set to "+host);
				}
			}
		});

		startRecordingButton.setOnMouseClicked(event -> connector.sendUDP(String.format("r %02d", servo)));
		playRecordingButton.setOnMouseClicked(event -> connector.sendUDP("e 00"));
		stopRecordingButton.setOnMouseClicked(event -> connector.sendUDP(String.format("t %02d", servo)));
		dumpRecordingButton.setOnMouseClicked(event -> connector.sendUDP("d xx"));
		clearTrackButton.setOnMouseClicked(event -> connector.sendUDP("w"));
		createNewRecordButton.setOnMouseClicked(event -> connector.sendUDP("c"+actionNamesList.getValue()));
		saveRecordingButton.setOnMouseClicked(event -> connector.sendUDP("s"+actionTypesList.getValue()));
		uploadWavButton.setOnMouseClicked(event -> {
			messageField.setText("upload wae for "+actionNamesList.getValue());
			try {
				waveUpload();
			} catch (IOException e) {
				showAlert("Unknown host "+e.getMessage());
			}
		});

		smoothRecordingButton.setOnMouseClicked(event -> connector.sendUDP(String.format("f %02d", servo)));
		servoDropDownList.setOnAction(event -> selectNewServo(servoDropDownList.getValue()));
		actionNamesList.setOnAction(event -> connector.sendUDP("c"+actionNamesList.getValue()));
		actionTypesList.setOnAction(event -> messageField.setText("Set action to "+actionNamesList.getValue()));
		resetButton.setOnMouseClicked(event -> connector.sendUDP("x"));

		this.addEventHandler(KeyEvent.KEY_PRESSED, key -> {
		      if(key.getCode()==KeyCode.F1) {
		    	  messageField.setText("Start recording for servo "+servo);
				  connector.sendUDP(String.format("r %02d", servo));
		      }
			if(key.getCode()==KeyCode.F2) {
				messageField.setText("Stop recording for servo "+servo);
				connector.sendUDP(String.format("t %02d", servo));
			}
			if(key.getCode()==KeyCode.F3) {
				messageField.setText("Clear recording for servo "+servo);
				connector.sendUDP("w");
			}
		});
	}


	private void receiveListOfActions() {
		try {
			// Get list of actions
			connector.sendUDP("l ");
			DatagramSocket serverSocket = new DatagramSocket(3003);
			serverSocket.setSoTimeout(1000);
			byte[] receiveData = new byte[9128];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			String receivedDataString = new String(receivePacket.getData());
			serverSocket.close();
			actionNames.clear();
			actionNames.addAll(Arrays.asList(receivedDataString.split(";")));
			actionNamesList.getSelectionModel().select(0);

			// get list of servo's
			connector.sendUDP("v ");
			serverSocket = new DatagramSocket(3003);
			serverSocket.setSoTimeout(10000);
			receiveData = new byte[9128];
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			receivedDataString = new String(receivePacket.getData());
			serverSocket.close();

			log.info("Received servo's "+receivedDataString);
			List<String> receivedServoList = Arrays.asList(receivedDataString.split(";"));
			Globals.servoLimitList.clear();
			receivedServoList.forEach(servoString -> {String[] valueList=servoString.split("[ \t]");
											  if(valueList.length!=6)return;
											  Globals.servoLimitList.add(new Servo(valueList[0],
																			 Integer.parseInt(valueList[1]),
																			 Integer.parseInt(valueList[2]),
																			 Integer.parseInt(valueList[3]),
																			 Integer.parseInt(valueList[5])
																			));
									});
			Globals.servoLimitList.removeIf(servoObj -> servoObj.getMaxPos()==0);
			rebuildSlider();

		}catch (SocketException e){
			showAlert("Time out error "+e.getMessage());
		}catch (IOException e) {
			showAlert("IO Error "+e.getMessage());
		}
		
	}

	private void rebuildSlider() {
		log.info("rebuild slider");
		servoDropDownList.getItems().clear();
		Globals.servoLimitList.forEach(item -> servoDropDownList.getItems().add(item.getServoName()));
	}

	private void selectNewServo(String servoName)
	{
		log.info("Selected servo is "+servoName);
		Servo selectedServo = Globals.getServoByName(servoName);
		if( selectedServo == null )return;
		servo=selectedServo.getServoValue();
		log.info(selectedServo.toString());
		slider.setPrefHeight(selectedServo.getMaxPos()-selectedServo.getMinPos());
		slider.setValue(selectedServo.getRestPos());
		slider.setMax(selectedServo.getMaxPos());
		slider.setMin(selectedServo.getMinPos());
		minField.setText(""+selectedServo.getMinPos());
		maxField.setText(""+selectedServo.getMaxPos());
		restField.setText(""+selectedServo.getRestPos());
		messageField.setText("Switch to servo "+selectedServo.getServoName());
		this.servoName.setText(selectedServo.getServoName());
	}


	private void waveUpload() throws IOException
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
		         new ExtensionFilter("Wave Files", "*.wave","*.wav"),
		         new ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
		if (selectedFile == null)return;
		
		connector.sendUDP("u"+actionNamesList.getValue());
		messageField.setText("Wait 2 seconds for the server to start");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		
		FileXferClient xferClient=new FileXferClient();
		int rc=xferClient.SendFile(selectedFile.getCanonicalPath(),ipAdressField.getText());
		servoName.setText("File send is "+(rc!=-1));		
		
	}

	private void showAlert(String message){
		errorAlert.setContentText(message);
		errorAlert.setTitle("Error");
		errorAlert.showAndWait();
	}

}
