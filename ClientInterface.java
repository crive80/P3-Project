package Client;
import java.rmi.*;

public interface ClientInterface extends Remote {
	public String getClientName() throws RemoteException;
}