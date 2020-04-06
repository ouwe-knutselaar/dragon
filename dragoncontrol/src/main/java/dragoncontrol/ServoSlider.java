package dragoncontrol;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ServoSlider extends GridPane
{

	private int servo=0;
	private int udpPort=80;
	private String host="127.0.0.1";
	InetAddress IPAddress;
	
	Slider slider;

	Label ipAdressLabel=new Label("ip adres");
	Label ipPortLabel=new Label("port");
	Label minLabel=new Label("min");
	Label maxLabel=new Label("max");
	Label restLabel=new Label("rest");
	Label servoLabel=new Label("servo");
	Label actionName=new Label("sequence name");
	Label servoNameLabel=new Label("servo name");
	
	Text messageField = new Text("messages");
	Text servoName = new Text(Globals.servoLimitList[0].getServoName());
	
	TextField ipAdressField=new TextField(host);
	TextField ipPortField=new TextField("3001");
	TextField minField;
	TextField maxField;
	TextField restField;
	TextField nameField;
	
	Button connect=new Button("Set IP");
	Button createNewRecordButton=new Button("Create");
	Button startRecordingButton = new Button("record");
	Button stopRecordingButton = new Button("stop");
	Button playRecordingButton = new Button("play");
	Button dumpRecordingButton = new Button("dump");
	Button saveRecordingButton = new Button("save");
	Button smoothRecordingButton = new Button("smooth*");
	Button uploadWavButton = new Button("wav upload");

	GridPane fieldGrid = new GridPane();
	GridPane buttonGrid = new GridPane();
	
	
	DatagramSocket clientSocket;
	
	private Integer[] servoList={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	ComboBox<Integer> servoDropDownList=new ComboBox<>(FXCollections.observableArrayList(servoList));
	
	
	public ServoSlider() 
	{
		
		try {
			IPAddress = InetAddress.getByName(host);
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	
		 slider=new Slider(Globals.servoLimitList[servo].getMinPos(),Globals.servoLimitList[servo].getMaxPos(),Globals.servoLimitList[servo].getRestPos());

		 ipAdressLabel=new Label("ip adres");
		 ipPortLabel=new Label("port");
		 minLabel=new Label("min");
		 maxLabel=new Label("max");
		 restLabel=new Label("rest");
		 servoLabel=new Label("servo");
		
		 ipAdressField=new TextField("127.0.0.1");
		 ipPortField=new TextField("3001");
		 minField=new TextField(""+Globals.servoLimitList[servo].getMinPos());
		 maxField=new TextField(""+Globals.servoLimitList[servo].getMaxPos());
		 restField=new TextField(""+Globals.servoLimitList[servo].getRestPos());
		 nameField=new TextField("tempname");
		 
		
		fieldGrid.add(minLabel, 0,0);
		fieldGrid.add(restLabel, 0,1);
		fieldGrid.add(maxLabel, 0,2);
		fieldGrid.add(ipAdressLabel, 0, 3);
		fieldGrid.add(ipPortLabel, 0, 4);
		fieldGrid.add(servoLabel, 0, 5);
		fieldGrid.add(actionName, 0, 6);
		fieldGrid.add(servoNameLabel,0,7);
		
		
		fieldGrid.add(minField, 1,0);
		fieldGrid.add(restField, 1,1);
		fieldGrid.add(maxField, 1,2);
		fieldGrid.add(ipAdressField, 1, 3);
		fieldGrid.add(ipPortField, 1, 4);
		fieldGrid.add(servoDropDownList, 1, 5);
		fieldGrid.add(nameField, 1, 6);
		fieldGrid.add(servoName, 1, 7);
		
		
		// colom , row, colspan, rowspan
		buttonGrid.add(connect,0,0);
		buttonGrid.add(smoothRecordingButton,1,0);
		buttonGrid.add(startRecordingButton, 2, 0);
		
		buttonGrid.add(createNewRecordButton, 0, 1);
		buttonGrid.add(stopRecordingButton, 2, 1);
		
		buttonGrid.add(saveRecordingButton, 0, 2);
		buttonGrid.add(uploadWavButton,2,2);
		
		buttonGrid.add(dumpRecordingButton, 0, 3);
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
		
			
		minField.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				servo=(int) servoDropDownList.getValue();
				Globals.servoLimitList[servo].setMinPos(Integer.parseInt(newValue));
				rebuildSlider();
			}});
		
		maxField.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(newValue.equals(""))return;
				servo=(int) servoDropDownList.getValue();
				Globals.servoLimitList[servo].setMaxPos(Integer.parseInt(newValue));
				rebuildSlider();
			}});
		
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				try {
					String sendString=String.format("p %02d %04d", servo,newValue.intValue());
					DatagramPacket sendPacket = new DatagramPacket(sendString.getBytes(), sendString.length(), IPAddress, udpPort);
					clientSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}});
		
		
		connect.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				try {
					host=ipAdressField.getText();
					IPAddress = InetAddress.getByName(host);
					udpPort=Integer.parseInt(ipPortField.getText());
					messageField.setText("Host is "+host+"at port "+ipPortField.getText());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}});
		
		
		startRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				messageField.setText("Start recording for servo "+servo);
				sendUDP(String.format("r %02d", servo));
			}
		});
		
		
		playRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				messageField.setText("Play sequence");
				sendUDP("e 00");
			}
		});

		stopRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sendUDP("t 00");
				messageField.setText("Stop sequence");
			}
		});

		dumpRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				messageField.setText("Dump sequence");
				sendUDP("d xx");
			}
		});
		
		
		createNewRecordButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				messageField.setText("New recording named "+nameField.getText());
				sendUDP("c"+nameField.getText());
			}
		});
		
		
		saveRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				messageField.setText("save sequence "+nameField.getText());
				sendUDP("s"+nameField.getText());
			}
		});
		
		uploadWavButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				messageField.setText("upload wae for "+nameField.getText());
				try {
					waveUpload();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		smoothRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				messageField.setText("Smooth recording for servo "+servo);
				sendUDP(String.format("f %02d", servo));
			}
		});
		
		servoDropDownList.setValue(0);
		servoDropDownList.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				rebuildSlider();
			}});
		
		
		this.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			  System.out.println("keypressed "+key.getText()+" "+key.getCode());
		      if(key.getCode()==KeyCode.F1) {
		    	  messageField.setText("Start recording for servo "+servo);
		    	  sendUDP(String.format("r %02d", servo));
		      }
		});
	}
	
	
	private void sendUDP(String sendString)
	{
		try {
			DatagramPacket sendPacket = new DatagramPacket(sendString.getBytes(), sendString.length(), IPAddress, udpPort);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void rebuildSlider()
	{
		servo=(int) servoDropDownList.getValue();
		slider.setPrefHeight(Globals.servoLimitList[servo].getMaxPos()-Globals.servoLimitList[servo].getMinPos());
		slider.setValue(Globals.servoLimitList[servo].getRestPos());
		slider.setMax(Globals.servoLimitList[servo].getMaxPos());
		slider.setMin(Globals.servoLimitList[servo].getMinPos());
		minField.setText(""+Globals.servoLimitList[servo].getMinPos());
		maxField.setText(""+Globals.servoLimitList[servo].getMaxPos());
		restField.setText(""+Globals.servoLimitList[servo].getRestPos());
		messageField.setText("Switch to servo "+Globals.servoLimitList[servo].getServoName());
		servoName.setText(Globals.servoLimitList[servo].getServoName());
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
		
		
		
		sendUDP("u"+nameField.getText());
		messageField.setText("Wait 2 seconds for the server to start");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		FileXferClient xferClient=new FileXferClient();
		int rc=xferClient.SendFile(selectedFile.getCanonicalPath(),ipAdressField.getText());
		servoName.setText("File send is "+(rc!=-1));		
		
	}
	
}
