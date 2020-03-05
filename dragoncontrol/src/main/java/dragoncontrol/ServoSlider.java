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
	

	private int servo=0;
	private int port=80;
	private String host="127.0.0.1";
	InetAddress IPAddress;
	
	Slider slider;

	Label ipAdressLabel=new Label("ip adres");
	Label ipPortLabel=new Label("port");
	Label minLabel=new Label("min");
	Label maxLabel=new Label("max");
	Label restLabel=new Label("rest");
	Label servoLabel=new Label("servo");
	
	TextField ipAdressField=new TextField("127.0.0.1");
	TextField ipPortField=new TextField("3001");
	TextField minField;
	TextField maxField;
	TextField restField;

	
	
	
	Button connect=new Button("Connect");
	DatagramSocket clientSocket;
	
	private Integer[] servoList={0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	ComboBox<Integer> servoDropDownList=new ComboBox<>(FXCollections.observableArrayList(servoList));
	
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
		this.add(servoDropDownList, 1, 5);

		
		this.add(connect,0,6,2,1);
		this.add(slider, 0, 7,2,1);
		
		
		slider.setPrefWidth(Globals.servoLimitList[servo].getMaxPos()-Globals.servoLimitList[servo].getMinPos());
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
		
		
		
		
		servoDropDownList.setValue(0);
		servoDropDownList.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				servo=(int) servoDropDownList.getValue();
				
				slider.setPrefWidth(Globals.servoLimitList[servo].getMaxPos()-Globals.servoLimitList[servo].getMinPos());
				slider.setValue(Globals.servoLimitList[servo].getRestPos());
				slider.setMax(Globals.servoLimitList[servo].getMaxPos());
				slider.setMin(Globals.servoLimitList[servo].getMinPos());
				
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
