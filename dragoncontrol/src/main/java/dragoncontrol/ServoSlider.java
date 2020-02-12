package dragoncontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;

public class ServoSlider extends GridPane
{
	
	DragonTelnet dragonApi=new DragonTelnet();
	
	private int id;
	
	private int min=100;
	private int max=600;
	private int rest=350;
	private int servo=0;
	private int port=80;
	private String host="127.0.0.1";
	InetAddress IPAddress;
	
	

	Slider slider=new Slider(min,max,rest);

	Label ipAdressLabel=new Label("ip adres");
	Label ipPortLabel=new Label("port");
	Label minLabel=new Label("min");
	Label maxLabel=new Label("max");
	Label restLabel=new Label("rest");
	Label servoLabel=new Label("servo");
	
	TextField ipAdressField=new TextField("127.0.0.1");
	TextField ipPortField=new TextField("3001");
	TextField minField=new TextField(""+min);
	TextField maxField=new TextField(""+max);
	TextField restField=new TextField(""+rest);
	TextField servoField=new TextField(""+0);
	
	Button connect=new Button("Connect");
	DatagramSocket clientSocket;
	

	
	public ServoSlider() 
	{
		
		try {
			IPAddress = InetAddress.getByName(host);
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		
		this.add(minLabel, 0,0);
		this.add(restLabel, 0,1);
		this.add(maxLabel, 0,2);
		this.add(ipAdressLabel, 0, 3);
		this.add(ipPortLabel, 0, 4);
		this.add(servoLabel, 0, 5);
		
		
		this.add(minField, 1,0);
		this.add(restField, 1,1);
		this.add(maxField, 1,2);
		this.add(ipAdressField, 1, 3);
		this.add(ipPortField, 1, 4);
		this.add(servoField, 1, 5);

		
		this.add(connect,0,6,2,1);
		this.add(slider, 0, 7,2,1);
		
		
		slider.setPrefWidth(max-min);
		slider.setOrientation(Orientation.VERTICAL);
		slider.setShowTickLabels(true);
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				
				String sendString=String.format("p %02d %04d", servo,newValue.intValue());
				DatagramPacket sendPacket = new DatagramPacket(sendString.getBytes(), sendString.length(), IPAddress, port);
				try {
					clientSocket.send(sendPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}});
		
		
		connect.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				try {
					host=ipAdressField.getText();
					IPAddress = InetAddress.getByName(host);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				port=Integer.parseInt(ipPortField.getText());
			}});
		
		
		minField.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				min=Integer.parseInt(newValue);
				slider.setMin(min);
				slider.setPrefWidth(max-min);
			}});
		
		maxField.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				max=Integer.parseInt(newValue);
				slider.setMax(max);
				slider.setPrefWidth(max-min);
			}});
		
		restField.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				rest=Integer.parseInt(newValue);
				slider.setValue(rest);
				
			}});
		
		servoField.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				servo=Integer.parseInt(newValue);
				
				
			}});
		
		this.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
			  System.out.println("keypressed "+key.getText()+" "+key.getCode());
		      if(key.getCode()==KeyCode.F1) {
		    	  String sendString="START";
				  DatagramPacket sendPacket = new DatagramPacket(sendString.getBytes(), sendString.length(), IPAddress, port);
				  try {
						clientSocket.send(sendPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		      }
		});
	}
	
	
	
	
}
