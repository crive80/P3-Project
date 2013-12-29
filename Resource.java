package Resource;
import java.rmi.*;
import java.rmi.server.*;

public class Resource extends UnicastRemoteObject implements ResourceInterface {
	private String name;
	private int parts;

	public Resource(String n, int p) throws RemoteException {
		name = n;
		parts = p;
	}

	public String getName() throws RemoteException {
		return name;
	}
	public int getParts() throws RemoteException {
		return parts;
	}

	public boolean compare(ResourceInterface r) throws RemoteException {
		if (this.getName().equals(r.getName()) && this.getParts() == r.getParts()) return true;
		else return false;
	}
}
