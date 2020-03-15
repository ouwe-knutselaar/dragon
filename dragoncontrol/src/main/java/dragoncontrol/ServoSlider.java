package dragoncontrol;

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
	Label actionName=new Label("name");
	
	TextField ipAdressField=new TextField(host);
	TextField ipPortField=new TextField("3001");
	TextField minField;
	TextField maxField;
	TextField restField;
	TextField nameField;
	
	Button connect=new Button("Connect");
	Button createNewRecordButton=new Button("Create");
	Button startRecordingButton = new Button("record");
	Button stopRecordingButton = new Button("stop");
	Button playRecordingButton = new Button("play");
	Button dumpRecordingButton = new Button("dump");
	Button saveRecordingButton = new Button("save");

	
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
		 
		
		this.add(minLabel, 0,0);
		this.add(restLabel, 0,1);
		this.add(maxLabel, 0,2);
		this.add(ipAdressLabel, 0, 3);
		this.add(ipPortLabel, 0, 4);
		this.add(servoLabel, 0, 5);
		this.add(actionName, 0, 6);
		
		
		this.add(minField, 1,0);
		this.add(restField, 1,1);
		this.add(maxField, 1,2);
		this.add(ipAdressField, 1, 3);
		this.add(ipPortField, 1, 4);
		this.add(servoDropDownList, 1, 5);
		this.add(nameField, 1, 6);

		
		this.add(connect,0,7,1,1);
		this.add(slider, 1, 7,1,6);
		this.add(createNewRecordButton, 0, 8);
		this.add(startRecordingButton, 0, 9);
		this.add(playRecordingButton, 0, 10);
		this.add(stopRecordingButton, 0, 11);
		this.add(dumpRecordingButton, 0, 12);
		this.add(saveRecordingButton, 0, 13);
		
		
		slider.setOrientation(Orientation.VERTICAL);
		slider.setShowTickLabels(true);
		
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
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}});
		
		
		startRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sendUDP(String.format("r %02d", servo));
			}
		});
		
		
		playRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sendUDP("e 00");
			}
		});

		stopRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sendUDP("t 00");
			}
		});

		dumpRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sendUDP("d xx");
			}
		});
		
		
		createNewRecordButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sendUDP("c"+nameField.getText());
			}
		});
		
		
		saveRecordingButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				sendUDP("s"+nameField.getText());
			}
		});
		
		servoDropDownList.setValue(0);
		servoDropDownList.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				servo=(int) servoDropDownList.getValue();
				slider.setPrefHeight(Globals.servoLimitList[servo].getMaxPos()-Globals.servoLimitList[servo].getMinPos());
				slider.setValue(Globals.servoLimitList[servo].getRestPos());
				slider.setMax(Globals.servoLimitList[servo].getMaxPos());
				slider.setMin(Globals.servoLimitList[servo].getMinPos());
				minField.setText(""+Globals.servoLimitList[servo].getMinPos());
				maxField.setText(""+Globals.servoLimitList[servo].getMaxPos());
				restField.setText(""+Globals.servoLimitList[servo].getRestPos());
			}});
		
		
		this.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			  System.out.println("keypressed "+key.getText()+" "+key.getCode());
		      if(key.getCode()==KeyCode.F1) {
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
	
	
	
}
