package Server;
import GUI.ServerGUI;
import java.util.Vector;
import Client.ClientInterface;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private ServerGUI serverGui;
    private String serverName;
    //private Client[] clientList;
    private static final String HOST = "localhost";
    private final int port = 3456;
    private ServerSocket s = null;
    private Vector<ClientInterface> clients; 
    
    public Server(String n) throws RemoteException {
        serverName = n;
        serverGui = new ServerGUI(n,this);
        ServerChecker c1 = new ServerChecker();
        c1.start();
        ClientChecker c2 = new ClientChecker();
        c2.start(); 
        clients = new Vector<ClientInterface>();
    }

    public String getServerName() { return serverName; }

    public void appendLog(String s) throws RemoteException {
        serverGui.appendLog(s);
    }

    public void disconnect() throws NotBoundException, MalformedURLException, RemoteException {
        Naming.unbind("rmi://" + HOST + "/Server/" + serverName);
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

    class ClientChecker extends Thread {
        public ClientChecker() { setDaemon(true); };
        public void run() {
            while (true) {
                if (!clients.isEmpty()) {
                    String[] c = new String[clients.size()];
                    for (int i=0; i<c.length; i++) {
                        try {
                            c[i] = clients.elementAt(i).getClientName();
                        } catch(RemoteException exc) { 
                            serverGui.appendLog("Client disconnesso\n"); 
                            clients.remove(i);
                        }
                    }
                    serverGui.setClientList(c);
                }
            }
        }
    }

    public void clientConnect(ClientInterface i) {
        clients.add(i);
        try {
            serverGui.appendLog("Client " + i.getClientName() + " connesso.\n");
        } catch (RemoteException exc) { serverGui.appendLog("Problemi di connessione."); }
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
