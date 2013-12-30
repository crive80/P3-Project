package Client;
import java.rmi.*;
import java.util.Vector;
import Resource.ResourceInterface;

public interface ClientInterface extends Remote {
	public String getClientName() throws RemoteException;
	public Vector<ResourceInterface> getResourceList() throws RemoteException;
	public boolean searchResource(ResourceInterface i) throws RemoteException;
	public void upload(ClientInterface v, ResourceInterface r, int p) throws RemoteException, Exception;
}