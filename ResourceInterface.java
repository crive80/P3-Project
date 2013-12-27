package Resource;
import java.rmi.*;

public interface ResourceInterface extends Remote {
	public String getName() throws RemoteException;
	public int getParts() throws RemoteException;
	public boolean compare(ResourceInterface r) throws RemoteException;
}