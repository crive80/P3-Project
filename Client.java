package Client;
import java.rmi.*;
import java.net.*;
import java.io.*;
import GUI.ClientGUI;
import Server.ServerInterface;

public class Client {
    private static final String HOST = "localhost";
    private int port = 3456;
    private InetAddress ip;
    private Socket s = null;
    private ClientGUI clientGUI;
    private int downloadCapacity;
    private String clientName;
    
    public Client(String n, String s, int c) throws Exception {
        downloadCapacity = c;
        clientName = n;
        clientGUI = new ClientGUI(clientName,this);
        clientGUI.setServerList(Naming.list("rmi://" + HOST + "/Server"));
        try {
            ServerInterface i = (ServerInterface) Naming.lookup("rmi://" + HOST + "/Server/" + s);
        } catch (Exception e) {
            clientGUI.appendLog("Il server " + s + " non è presente nel sistema.");
        }
        

    }

    public class ServerChecker extends Thread {
        public ServerChecker() { setDaemon(true); }
        public void run() {
            while (true) { checkServer(); }
        }
        public checkServer() throws Exception {
            clientGUI.setServerList(Naming.list("rmi://" + HOST + "/Server/"));
        }
    }
    
    public String getClientName() { return clientName; }

    public void connect(String url) throws Exception {
        ServerInterface ref = (ServerInterface) Naming.lookup(url);
        ref.appendLog("Client " + getClientName() + " connesso");
    }

    public static void main(String[] args) {
        try {
            Client c = new Client(args[0],args[1],Integer.parseInt(args[2]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
