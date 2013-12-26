package Server;
import Client.ClientInterface;
import java.rmi.*;
import java.util.Vector;

public interface ServerInterface extends Remote {
	public void appendLog(String s) throws RemoteException;
	public void clientConnect(ClientInterface i) throws RemoteException;
	public Vector<ClientInterface> getClients() throws RemoteException;
}

