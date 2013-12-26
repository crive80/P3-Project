package Server;
import java.rmi.*;

public interface ServerInterface extends Remote {
	public void appendLog(String s) throws RemoteException;
}
