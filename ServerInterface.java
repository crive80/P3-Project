package Server;
import Client.ClientInterface;
import java.rmi.*;

public interface ServerInterface extends Remote {
	public void appendLog(String s) throws RemoteException;
	public void clientConnect(ClientInterface i) throws RemoteException;
}
