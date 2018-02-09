package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;

/**
 * Ganz pragmatisch und maßgeschneidert für das Service-Metamodell:
 * eine Kante, die genau einen boolean Wert als Attribut hat. Man kann über eine maxCardinality angeben, bei wievielen
 * Kanten dieser Art für das EndElement das Attribut auf true sein darf, ohne dass es einen Cardinalitätsfehler gibt.
 * Das ist für die Kante Speicherung (StorageEdge) gedacht, die laut Metamodell das boolean-Attribut isMaster besitzt.
 * Die Kante zeigt von Anwendungsbausteinen (StartElement) auf Objekttypen (Endelement) und für einen Objekttyp sollte
 * maximal 1 Anwendungsbaustein der Master sein. Also muss die maxCardinality auf 1 gestellt werden.
 *
 * @author AXS (11.01.2018)
 */
public abstract class BooleanAttributeEdge extends SimpleEdge {

    private final String attributeNameResKey;

    public final int maxCardinality;

    public BooleanAttributeEdge(final String attributeNameResKey) {
        this(attributeNameResKey, EdgeCardinality.UNLIMITED);
    }

    public BooleanAttributeEdge(final String attributeNameResKey, final int maxCardinality) {
        this.attributeNameResKey = attributeNameResKey;
        this.maxCardinality = maxCardinality;
    }

    public String getAttributeName() {
        return Tool3lgmConstants.getResString(attributeNameResKey);
    }

}
