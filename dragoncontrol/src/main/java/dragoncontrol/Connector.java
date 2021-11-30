package dragoncontrol;

import javafx.scene.text.Text;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.*;

public class Connector {

    private final Logger log = Logger.getLogger(Connector.class.getSimpleName());

    private DatagramSocket clientSocket;
    private int udpPort;
    private String host;
    private InetAddress ipaddress;
    private Text output;

    public Connector(String host,int port,Text output)
    {
        this.udpPort = port;
        this.host = host;
        this.output = output;
    }

    public boolean Connect(){
        try {
            ipaddress = InetAddress.getByName(host);
            clientSocket = new DatagramSocket();
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendUDP(String message) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), ipaddress, udpPort);
            clientSocket.send(sendPacket);
            return true;
        } catch (IOException e){
            e.printStackTrace();
            output.setText("Error "+e.getMessage());
            return false;
        }
    }
}
