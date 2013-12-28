package Server;
import Client.ClientInterface;
import Resource.ResourceInterface;
import java.rmi.*;
import java.util.Vector;

public interface ServerInterface extends Remote {
	public String getServerName() throws RemoteException;
	public void appendLog(String s) throws RemoteException;
	public void clientConnect(ClientInterface i) throws RemoteException;
	public void clientDisconnect(ClientInterface i) throws RemoteException;
	public Vector<ClientInterface> getClients() throws RemoteException;
	public Vector<ClientInterface> getRequest(ResourceInterface r) throws RemoteException;
}

