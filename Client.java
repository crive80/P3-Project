package Client;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.*;
import GUI.ClientGUI;
import Server.ServerInterface;
import Resource.*;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final String HOST = "localhost";
    private ClientGUI clientGUI;
    private int downloadCapacity;
    private String clientName;
    private Vector<Resource> res;
    private ServerInterface serverConnected = null;
    
    public Client(String n, String s, int c, Vector<Resource> r) throws RemoteException {
        downloadCapacity = c;
        clientName = n;
        clientGUI = new ClientGUI(clientName,this);
        res = r;
        setResourceList();
        ServerChecker sc = new ServerChecker(); sc.start();
        try {
            ServerInterface i = (ServerInterface) Naming.lookup("rmi://" + HOST + "/Server/" + s);
            serverConnected = i;
            i.clientConnect(this);
            clientGUI.appendLog("Connesso al server " + i.getServerName());
        } catch (Exception e) {
            clientGUI.appendLog("Il server " + s + " non è presente nel sistema.");
        }
    }

    public class ServerChecker extends Thread {
        public ServerChecker() { setDaemon(true); }
        public void run() {
            while (true) { 
                try {
                    checkServer(); 
                } catch (Exception exc) {  System.out.println(
                    "CheckServerThread report: Server non più presente nel sistema"); }
                try {
                    sleep(10);
                } catch(InterruptedException e) { System.out.println("ServerChecker Thread interrotto"); }
            }
        }
        public void checkServer() throws Exception {
            clientGUI.setServerList(Naming.list("rmi://" + HOST + "/Server/"));
        }
    }
    
    public String getClientName() { return clientName; }

    public Vector<ResourceInterface> getResourceList() {
        Vector<ResourceInterface> aux = new Vector<ResourceInterface>();
        for (int i=0; i<res.size(); i++) {
            aux.add(res.elementAt(i));
        }
        return aux;
    }

    public void setResourceList() {
        Vector<String> aux = new Vector<String>();
        for (int i=0; i<res.size(); i++) {
            try {
                aux.add(res.elementAt(i).getName() + " : " + res.elementAt(i).getParts());
            } catch (RemoteException e) { clientGUI.appendLog("Problemi di connessione!"); }
        }
        clientGUI.setResourceList(aux);
    }

    public void connect(String url) throws Exception {
        ServerInterface ref = (ServerInterface) Naming.lookup(url);
        serverConnected = ref;
        ref.appendLog("Client " + getClientName() + " connesso");
    }

    public void disconnect() {
        if (serverConnected != null) {
            try {
                serverConnected.clientDisconnect(this);
            } catch (RemoteException e) { clientGUI.appendLog("Errore di connessione"); System.out.println("Errore di connessione"); }
        }
    }

    public void sendRequest(String s1, String s2) {
        try {
            int x = Integer.parseInt(s2);
        } catch (NumberFormatException e) { 
            clientGUI.popError("Impossibile convertire la stringa in intero"); 
            return;
        }
        try {
            ResourceInterface aux = new Resource(s1,Integer.parseInt(s2));
            // Controllo che il client non possegga già la risorsa cercata
            for (int i=0; i<res.size(); i++) {
                if (res.elementAt(i).compare(aux)) {
                    clientGUI.popError("Possiedi già la risorsa richiesta!");
                    return;
                }
            }
            Vector<ClientInterface> v = serverConnected.getRequest(aux);
            clientGUI.appendLog(v.size() + " client trovati!");
        } catch (RemoteException e) { clientGUI.appendLog("Errore di connessione"); }
    }

    public boolean searchResource(ResourceInterface r) {
        for (int i=0; i<res.size(); i++) {
            try {
            if (res.elementAt(i).compare(r))
                return true;
            } catch (RemoteException e) { clientGUI.appendLog("Errore di connessione"); }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            Vector<Resource> aux = new Vector<Resource>();
            for (int i=3; i<args.length; i+=2) {
                aux.add(new Resource(args[i],Integer.parseInt(args[i+1])));
            }
            Client c = new Client(args[0],args[1],Integer.parseInt(args[2]),aux);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
