package Server;
import GUI.ServerGUI;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;
import Client.ClientInterface;
import Resource.ResourceInterface;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private ServerGUI serverGui;
    private String serverName;
    private static final String HOST = "localhost";
    private Vector<ClientInterface> localClients;
    private Vector<String> systemServers;
    private Map<ResourceInterface,ClientInterface> clientResources;
    private Object sync = new Object();
    
    public Server(String n) throws RemoteException {
        serverName = n;
        serverGui = new ServerGUI(n,this);
        ServerChecker c1 = new ServerChecker();
        c1.start();
        ClientChecker c2 = new ClientChecker();
        c2.start(); 
        localClients = new Vector<ClientInterface>();
        systemServers = new Vector<String>();
        clientResources = new HashMap<ResourceInterface,ClientInterface>();
    }

    public String getServerName() { return serverName; }

    public void appendLog(String s) throws RemoteException {
        serverGui.appendLog(s);
    }

    public void disconnect() throws NotBoundException, MalformedURLException, RemoteException {
        synchronized (sync) {
            Naming.unbind("rmi://" + HOST + "/Server/" + serverName);
        }
    }

    class ServerChecker extends Thread {
        public ServerChecker() { setDaemon(true); }
        public void run() {
            while (true) {
                synchronized (sync) {
                    try {
                        checkServer();
                    } catch (Exception exc) { exc.printStackTrace(); }
                }
            }
        }
        public void checkServer() throws Exception {
            synchronized (sync) {
                String[] list = Naming.list("rmi://" + HOST + "/Server/");
                serverGui.setServerList(list);
                systemServers.clear();
                for (int i=0; i<list.length; i++) systemServers.add(list[i]);
            }
        }
    }

    class ClientChecker extends Thread {
        public ClientChecker() { setDaemon(true); };
        public void run() {
            while (true) {
                synchronized (sync) {
                    if (!localClients.isEmpty()) {
                        String[] c = new String[localClients.size()];
                        for (int i=0; i<c.length; i++) {
                            try {
                                c[i] = localClients.elementAt(i).getClientName();
                            } catch(RemoteException exc) { 
                                serverGui.appendLog("Client disconnesso\n"); 
                                localClients.remove(i);
                            }
                        }
                        serverGui.setClientList(c);
                    }
                    try {
                        checkSystemServers();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
        public void checkSystemServers() throws Exception {
            synchronized (sync) {
                Vector<String> globalClients = new Vector<String>();
                for (int i=0; i<systemServers.size(); i++) {
                    try {
                        ServerInterface in = (ServerInterface) Naming.lookup(systemServers.elementAt(i));
                        Vector<ClientInterface> aux = in.getClients();
                    for (int j=0; j<aux.size(); j++) globalClients.add(aux.elementAt(j).getClientName());
                    } catch (Exception e1) { e1.printStackTrace(); }
                }
                serverGui.setGlobalClientList(globalClients); 
            }
        }
    }

    public void clientConnect(ClientInterface i) {
        localClients.add(i);
        try {
            serverGui.appendLog("Client " + i.getClientName() + " connesso.\n");
            Vector<ResourceInterface> x = i.getResourceList();
            for (int j=0; j<x.size(); j++) {
                try {
                    clientResources.put(x.elementAt(j),i);
                } catch (Exception e) {e.printStackTrace(); }
            }
            setResourceList();
        } catch (RemoteException exc) { serverGui.appendLog("Il client non è riuscito a connettersi."); }
    }

    public void setResourceList() {
        Vector<String> s = new Vector<String>();
        for (Map.Entry<ResourceInterface,ClientInterface> entry : clientResources.entrySet()) {
            try {
                s.add(entry.getKey().getName() + ":" + entry.getKey().getParts() + " -> " + entry.getValue().getClientName());
            } catch (RemoteException exc) { serverGui.appendLog("Impossibile estrarre la Mappa di risorse, problemi di connessione."); } 
        }
        serverGui.setResourceList(s);
    }

    public void clientDisconnect(ClientInterface i) {
        for (int j=0; j<localClients.size(); j++) {
            if (i == (localClients.elementAt(j))) localClients.removeElementAt(j);
        }
    }

    public Vector<ClientInterface> getClients() {
        return localClients;
    }

    public Vector<ClientInterface> getRequest(ResourceInterface r) {
        Vector<ClientInterface> x = new Vector<ClientInterface>();
        for (int i=0; i<localClients.size(); i++) {
            try {
                if (localClients.elementAt(i).searchResource(r)) x.add(localClients.elementAt(i));
            } catch (RemoteException e) { serverGui.appendLog("Errore di connessione"); }
        }
        return x;
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
