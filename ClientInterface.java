package Client;
import java.rmi.*;
import java.util.Vector;
import Resource.ResourceInterface;

public interface ClientInterface extends Remote {
	public String getClientName() throws RemoteException;
	public Vector<ResourceInterface> getResourceList() throws RemoteException;
}