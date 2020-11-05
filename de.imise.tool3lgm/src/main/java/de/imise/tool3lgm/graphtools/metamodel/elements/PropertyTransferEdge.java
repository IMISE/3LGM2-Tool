package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * Diese Klasse ist für alle Kantenklasse die Oberklasse, bei denen
 * Verbindungseigenschaften von einem der verbundenen Elemente auf das andere
 * übertragen werden sollen.
 *
 * @author AXS (29 Dec 2018)
 */
public abstract class PropertyTransferEdge extends SubordinationEdge {

    public enum PropertyTransferType {
        /**
         * Verbindungseigenschaften werden vom Startelement auf das Endelement
         * übertragen
         */
        PROPERTY_TRANSFER_START_TO_END,
        /**
         * Verbindungseigenschaften werden vom Endelement auf das Startelement
         * übertragen
         */
        PROPERTY_TRANSFER_END_TO_START,
        /**
         * Verbindungseigenschaften werden in beiden Richtungen übertragen
         */
        PROPERTY_TRANSFER_BOTH,
    }

    /**
     * Typ, der die Art der Übertragung der Verbindungseigenschaften festlegt.
     */
    private PropertyTransferType propertyTransferType;

    /**
     * Wenn <code>true</code>, dann kann der {@link #propertyTransferType} nicht
     * geändert werden.
     */
    protected final boolean fixedPropertyTransferType;

    /**
     *
     */
    protected PropertyTransferEdge() {
        this(true);
    }

    /**
     * @param fixedPropertyTransferType
     */
    protected PropertyTransferEdge(final boolean fixedPropertyTransferType) {
        this(PropertyTransferType.PROPERTY_TRANSFER_BOTH, fixedPropertyTransferType);
    }

    /**
     * @param propertyTransferType
     */
    protected PropertyTransferEdge(final PropertyTransferType propertyTransferType) {
        this(propertyTransferType, true);
    }

    /**
     * @param propertyTransferType
     * @param fixedPropertyTransferType
     */
    protected PropertyTransferEdge(final PropertyTransferType propertyTransferType, final boolean fixedPropertyTransferType) {
        this.propertyTransferType = propertyTransferType;
        this.fixedPropertyTransferType = fixedPropertyTransferType;
    }

    /**
     * @return
     */
    public final PropertyTransferType getPropertyTransferType() {
        return propertyTransferType;
    }

    /**
     * @return
     */
    public final boolean isPropertyTransferStartToEnd() {
        return propertyTransferType == PropertyTransferType.PROPERTY_TRANSFER_BOTH || propertyTransferType == PropertyTransferType.PROPERTY_TRANSFER_START_TO_END;
    }

    /**
     * @return
     */
    public final boolean isPropertyTransferEndToStart() {
        return propertyTransferType == PropertyTransferType.PROPERTY_TRANSFER_BOTH || propertyTransferType == PropertyTransferType.PROPERTY_TRANSFER_END_TO_START;
    }

    /**
     * @param propertyTransferType
     */
    public final void setPropertyTransferType(final PropertyTransferType propertyTransferType) {
        if (!fixedPropertyTransferType) {
            this.propertyTransferType = propertyTransferType == null ? PropertyTransferType.PROPERTY_TRANSFER_BOTH : propertyTransferType;
        }
    }

    /**
     * @return
     */
    public final boolean isFixedPropertyTransferType() {
        return fixedPropertyTransferType;
    }

    /**
     * Liefert <code>true</code>, wenn diese Kante die Eigenschaften des anderen
     * verbundenen Elementes auf das übergebene ModelElement überträgt.
     *
     * @param me
     * @return
     */
    public final boolean isPropertyTransferTo(final ModelElement me) {
        if (isStart(me) && isPropertyTransferEndToStart()) {
            return true;
        }
        if (isEnd(me) && isPropertyTransferStartToEnd()) {
            return true;
        }
        return false;
    }

    /**
     * Liefert <code>true</code>, wenn die übergebene Kante die Eigenschaften
     * des anderen verbundenen Elementes auf das übergebene ModelElement
     * überträgt.
     *
     * @param edge
     * @param me
     * @return
     */
    public static final boolean isPropertyTransferTo(final Edge edge, final ModelElement me) {
        if (edge instanceof PropertyTransferEdge) {
            PropertyTransferEdge propertyTransferEdge = (PropertyTransferEdge) edge;
            return propertyTransferEdge.isPropertyTransferTo(me);
        }
        return false;
    }

}
