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

    @Override
    public default GDCollection getCollection() {
        GraphDocument doc = getGraphDocument();
        return doc == null ? null : doc.getCollection();
    }

    /**
     * @param doc
     * @return <code>true</code> if the given {@link GraphDocument} is the same
     *         like this class returns over {@link #getGraphDocument()}
     */
    public default boolean hasGraphDocument(final GraphDocument doc) {
        GraphDocument graphDocument = getGraphDocument();
        return graphDocument == doc;
    }

}
