package de.imise.tool3lgm.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import de.imise.tool3lgm.Tool3lgm;



/**
 * @author thomas
 *
 */
public class Tool3lgmServerImpl extends UnicastRemoteObject implements Tool3lgmServer {

	/**
	 * COMMENTME
	 */
	private Tool3lgm tool3lgm;

	/**
	 * @param tool3lgm
	 * @throws RemoteException
	 */
	public Tool3lgmServerImpl(Tool3lgm tool3lgm) throws RemoteException {
		super();
		this.tool3lgm = tool3lgm;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.Tool3lgmServer#processCommand(java.lang.String, java.lang.String[])
	 */
	@Override
	public void processCommand(String command, String[] params) throws RemoteException {
		tool3lgm.processCommand(command, params);
	}

}
