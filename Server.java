package Server;
import GUI.ServerGUI;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private ServerGUI serverGui;
    private String serverName;
    //private Client[] clientList;
    private Server[] serverList;
    private static final String HOST = "localhost";
    private final int port = 3456;
    private ServerSocket s = null;
    
    public Server(String n) throws RemoteException {
        serverName = n;
        serverGui = new ServerGUI(n);
        ServerChecker c = new ServerChecker();
        c.start();
    }

    public String getServerName() { return serverName; }

    public void appendLog(String s) throws RemoteException {
        serverGui.appendLog(s);
    }

    class ServerChecker extends Thread {
        public ServerChecker() { setDaemon(true); }
        public void run() {
            while (true) {
                try {
                    checkServer();
                } catch (Exception exc) { exc.printStackTrace(); }
            }
        }
        public void checkServer() throws Exception {
            serverGui.setServerList(Naming.list("rmi://" + HOST + "/Server/"));
        }
    }
    
    public static void main(String[] args) throws Exception {
        try {
            Server s = new Server(args[0]);
            String rmiObjName = "rmi://" + HOST + "/Server/" + s.getServerName();
            Naming.rebind(rmiObjName,s);
        } catch (RemoteException e) {
            System.out.println("Errore di connessione.");
            System.exit(1);
        }
    }
    
}
