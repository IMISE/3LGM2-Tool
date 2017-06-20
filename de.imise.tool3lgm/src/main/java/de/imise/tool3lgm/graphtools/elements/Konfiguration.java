package de.imise.tool3lgm.graphtools.elements;

import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.KonfigurationContainer;
import de.imise.tool3lgm.log.Log;

/**
 * @author N.N.
 */
public abstract class Konfiguration extends Knoten {

    /**
	 * 
	 */
    public Konfiguration() {
        super();
    }

    @Override
    public Object clone() {
        Konfiguration retVal;
        try {
            retVal = (Konfiguration) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        return retVal;
    }

    /**
     * @param doc
     * @return
     */
    public abstract ArrayList<ElementContainer> getClientContainer(GraphDocument doc);

    /**
     * @param doc
     * @return
     */
    public abstract ArrayList<ElementContainer> getServerContainer(GraphDocument doc);

    @Override
    public ElementContainer createContainer(final GraphDocument doc) {
        return new KonfigurationContainer(this, doc);
    }

    /**
     * Gibt true zurück, wenn eine Konfiguration im uebergebenen GraphDocument enthalten ist, also dann, wenn mind. ein Client und ein Server in doc existiert. False sonst.
     * 
     * @param GraphDocument
     */
    public boolean isInGraphDocument(final GraphDocument doc) {
        return getClientContainer(doc).size() > 0 && getServerContainer(doc).size() > 0;
    }

    /**
     * @param konfig
     * @param doc
     * @return
     */
    public boolean hasSameServer(final Konfiguration konfig, final GraphDocument doc) {
        //Konfigurationen sind identisch, wenn sie im Hauptmodell dieselben Bausteine als Server haben???
        //		doc = doc.getCollection().getGraphDocument();
        ArrayList<ElementContainer> thisServer = getServerContainer(doc);
        ArrayList<ElementContainer> konfigServers = konfig.getServerContainer(doc);
        if (konfigServers.size() != thisServer.size()) {
            return false;
        }
        for (int i = 0; i < konfigServers.size(); i++) {
            if (!thisServer.contains(konfigServers.get(i))) {
                return false;
            }
        }
        return true;
    }

}
