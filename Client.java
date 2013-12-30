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
    private static final int delay = 5000;
    private ClientGUI clientGUI;
    private int downloadCapacity;
    private String clientName;
    private Vector<Resource> res;
    private ServerInterface serverConnected = null;
    private ServerChecker sc = null;
    private Object sync = new Object();
    private boolean downloading = false;
    private Scheduler scheduler = null;
    
    public Client(String n, String s, int c, Vector<Resource> r) throws RemoteException {
        downloadCapacity = c;
        clientName = n;
        clientGUI = new ClientGUI(clientName,this);
        res = r;
        setResourceList();
        sc = new ServerChecker(); sc.start();
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
                    "CheckServerThread report: Server non più presente nel sistema"); 
                    return;
                }
                try {
                    sleep(10);
                } catch(InterruptedException e) { System.out.println("ServerChecker Thread interrotto"); return; }
            }
        }
        public void checkServer() throws Exception {
            clientGUI.setServerList(Naming.list("rmi://" + HOST + "/Server/"));
        }
    }

    public class downloadThread extends Thread {
        public downloadThread(ClientInterface c, ResourceInterface r, int p) { 
            i = c; 
            ri = r;
            part = p;
            try {
                cname = c.getClientName();
                rname = r.getName();
                parts = r.getParts();
            } catch(RemoteException e) {
                clientGUI.appendLog("Problemi di connessione!");
                return;
            }
        }
        ClientInterface i;
        ResourceInterface ri;
        int part;
        String cname;
        String rname;
        int parts;
        public void run() {
            try {
                int index = clientGUI.getDownloadListSize() - parts + part - 1;
                clientGUI.modifyDownloadList(index,rname + "[" + part + 
                    "]" + " : " + cname + " {downloading}");
                clientGUI.appendLog("Downloading " + rname + "[" + part + "] from " 
                    + cname);
                i.upload(Client.this,ri,part);
                clientGUI.modifyDownloadList(index,rname + "[" + part + 
                    "]" + " : " + cname + " {completed}");
                clientGUI.appendLog(rname + "[" + part + "] correctly downloaded from " 
                    + cname + "!");
            } catch (RemoteException e) {
                clientGUI.appendLog("Problemi di connessione durante il download.");
                int index = clientGUI.getDownloadListSize() - parts + part - 1;
                clientGUI.modifyDownloadList(index,rname + "[" + part + 
                "]" + " : " + cname + " {failed}");
                scheduler.interrupt();
            }
            catch(Exception e) { 
                e.printStackTrace();
            }
        }
    }
    
    public class Scheduler extends Thread {
        public Scheduler(Vector<ClientInterface> c, ResourceInterface r, int p) {
            ci = c;
            ri = r;
            parts = p;
        }
        private Vector<ClientInterface> ci;
        private ResourceInterface ri;
        private int parts;
        public void run() {
            synchronized(sync) {
                downloadThread[] T = new downloadThread[parts];
                int j = 0;
                // Schedulazione dei processi
                for (int i=0; i<T.length; i++) {
                    T[i] = new downloadThread(ci.elementAt(j),ri,i+1);
                    try {
                        clientGUI.addDownloadList(ri.getName() + "[" + i+1 + "] " + "from " + 
                        ci.elementAt(j).getClientName() + "{waiting}");
                    } catch (RemoteException e) {
                        clientGUI.appendLog("Errore di connessione!");
                        return;
                    }
                    j++;
                    if (j>=ci.size()) j = 0;
                }
                int cnt1 = 0; int cnt2 = 0; int cnt3 = 0;
                while (cnt1 < T.length) {
                    if (cnt3 < ci.size()) {
                        T[cnt1].start();
                        cnt3++;
                        cnt2++;
                        if (cnt3 >= downloadCapacity) {
                            try {
                                T[cnt1].join();
                            } catch (InterruptedException e) {
                                clientGUI.appendLog("Download interrotto. Riprovare!");
                                downloading = false;
                                return;
                                }
                            cnt3 = 0;
                        }
                        if (cnt2 >= ci.size()) {
                            try {
                                T[cnt1].join();
                            } catch (InterruptedException e) {
                                clientGUI.appendLog("Download interrotto. Riprovare!");
                                downloading = false;
                                return;
                            }
                            cnt2 = 0;
                            cnt3 = 0;
                        }
                            
                    }
                    cnt1++;
                }
                try {
                    T[T.length-1].join();
                    addResource(ri.getName(),ri.getParts());
                    clientGUI.appendLog("Resource correctly downloaded!");
                    downloading = false;
                } catch(InterruptedException e) {
                    clientGUI.appendLog("Download interrotto. Riprovare!");
                    downloading = false;
                    return;
                }
                catch (RemoteException e) {
                    clientGUI.appendLog("Errore di connessione");
                    return;
                }
            }
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
        sc = new ServerChecker(); sc.start();
    }

    public void disconnect() {
        if (serverConnected != null) {
            try {
                serverConnected.clientDisconnect(this);
            } catch (RemoteException e) { clientGUI.appendLog("Errore di connessione"); System.out.println("Errore di connessione"); }
            sc.interrupt();
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
            downloading = true;
            ResourceInterface aux = new Resource(s1,Integer.parseInt(s2));
            // Controllo che il client non possegga già la risorsa cercata
            for (int i=0; i<res.size(); i++) {
                if (res.elementAt(i).compare(aux)) {
                    clientGUI.popError("Possiedi già la risorsa richiesta!");
                    return;
                }
            }
            Vector<ClientInterface> v = serverConnected.getRequest(aux);
            String s = new String();
            for (int i=0; i<v.size(); i++) {
                s += v.elementAt(i).getClientName() + " ";
            }
            if (v.isEmpty()) {
                clientGUI.appendLog("Resource " + s1 + ":" + s2 + " not found!");
            }
            else {
                clientGUI.appendLog("Ricevuta lista per " + s1 + "  " + s2 + ": " + s);
                scheduler = new Scheduler(v,aux,aux.getParts());
                scheduler.start();
            }

        } catch (RemoteException e) { clientGUI.appendLog("Errore di connessione"); downloading = false; }
    }

    public void upload(ClientInterface c, ResourceInterface r, int p) throws Exception {
        try {
            clientGUI.appendLog("Uploading " + r.getName() + "[" + p + "] to " 
                + c.getClientName() + "...");
            Thread.sleep(delay);
            clientGUI.appendLog(r.getName() + "[" + p + "] correctly uploaded to " + 
                c.getClientName() + "!");
        } catch (InterruptedException e) {
            clientGUI.appendLog("Thread interrupted while sleeping!");
            throw (new Exception("An error occurred while uploading."));
        }
        catch (RemoteException e) {
            clientGUI.appendLog("Errore di conessione!");
            throw (new Exception("An error occurred while uploading."));
        }
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

    public void addResource(String s, int p) {
        try {
            res.add(new Resource(s,p));
            setResourceList();
        } catch (RemoteException e) {
            clientGUI.appendLog("Errore di connessione!");
        }
    }

    public boolean isDownloading() { return downloading; }

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
