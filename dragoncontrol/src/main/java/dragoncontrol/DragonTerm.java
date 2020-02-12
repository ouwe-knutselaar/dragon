package dragoncontrol;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DragonTerm extends VBox{
	
	Socket dragonSocket;
	
	TextArea output=new TextArea();
	TextField input=new TextField();
	
	String  inBuffer;
	
	BufferedWriter tcpOutput;
	BufferedReader tcpInput;
	
	public DragonTerm() 
	{
		
		output.setStyle("-fx-text-fill: green; -fx-font-size: 12px;-fx-background-color:black");
		output.setEditable(false);
		output.setPrefRowCount(24);
		output.setPrefColumnCount(40);
		
		this.getChildren().add(output);
		this.getChildren().add(input);
		
		
		/*input.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				output.appendText(newValue);
			}
		});*/
		
		input.setOnKeyPressed(new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER)
				{
					
					output.appendText(input.getText()+"\n");
					try {
						tcpOutput.write(input.getText()+"\n");
						tcpOutput.flush();
						//tcpInput.lines().forEach(line -> output.appendText(line) );
						while((inBuffer=tcpInput.readLine())!=null)
						{
							output.appendText(inBuffer+"\n");
						}
						
					}catch(SocketTimeoutException e)
					{
						
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					input.setText("");
				}
				
			}});
			
	}
	
	
	public void connect(String address,int port) 
	{
		try {
			dragonSocket=new Socket(InetAddress.getByName(address),port);
			dragonSocket.setSoTimeout(500);
		} catch (UnknownHostException e) {
			output.setText(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			output.setText(e.getMessage());
			e.printStackTrace();
		}
		
		try {
			
			tcpOutput=new BufferedWriter(new OutputStreamWriter(dragonSocket.getOutputStream()));
			tcpInput=new BufferedReader(new InputStreamReader(dragonSocket.getInputStream()));
			output.appendText(tcpInput.readLine());
		} catch (IOException e) {
			output.setText(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
