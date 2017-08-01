/**
 * 
 */
package de.imise.tool3lgm.graphtools.model;

/**
 * @author AXS
 */
public interface GraphDocumentOwner extends GDCollectionOwner {

    /**
     * Liefert das {@link GraphDocument}, das mit diesem Objekt assoziiert wird
     * 
     * @return
     */
    public GraphDocument getGraphDocument();

}
