package de.imise.tool3lgm.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author thomas
 */
public interface Tool3lgmServer extends Remote {

    public void processCommand(String command, String[] params) throws RemoteException;

}
