package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Arrays;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Ein {@link SequenceMetaPath}, der immer nur aus einer einfachen Folge von Kanten bzw. {@link ElementaryMetaPath} besteht.
 *
 * @author AXS (15 Nov 2018)
 */
public class SimpleMetaPath extends SequenceMetaPath {

    /**
     * @param metaPaths
     */
    public SimpleMetaPath(final ElementaryMetaPath... metaPaths) {
        super(metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleMetaPath(final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param direction
     * @param metaPaths
     */
    protected SimpleMetaPath(final String baseResKeyOrName, final Direction direction, final ElementaryMetaPath... metaPaths) {
        super(baseResKeyOrName, direction, metaPaths);
    }

    /**
     * @return the startClass
     */
    public Class<? extends ModelElement> getStartClass() {
        return getSimpleMetaPath().get(0).getStartClass();
    }

    /**
     * @return the endClass
     */
    public Class<? extends ModelElement> getEndClass() {
        List<ElementaryMetaPath> simpleMetaPath = getSimpleMetaPath();
        return simpleMetaPath.get(simpleMetaPath.size() - 1).getEndClass();
    }

    @Override
    public SimpleMetaPath getOtherDirection() {
        return (SimpleMetaPath) super.getOtherDirection();
    }

    @Override
    protected SimpleMetaPath createOtherDirection(final String baseResKeyOrName) {
        ElementaryMetaPath[] otherDirectionMetaPaths = getOtherDirectionMetaPaths();
        return otherDirectionMetaPaths != null ? new SimpleMetaPath(baseResKeyOrName, Direction.BACKWARD, getOtherDirectionMetaPaths()) : null;
    }

    @Override
    protected ElementaryMetaPath[] getOtherDirectionMetaPaths() {
        ElementaryMetaPath[] otherDirectionElementaryMetaPaths = null;
        AbstractMetaPath[] otherDirectionMetaPaths = super.getOtherDirectionMetaPaths();
        if (otherDirectionMetaPaths != null) {
            otherDirectionElementaryMetaPaths = Arrays.copyOf(otherDirectionMetaPaths, otherDirectionMetaPaths.length, ElementaryMetaPath[].class);
        }
        return otherDirectionElementaryMetaPaths;
    }

    /**
     * Liefert einen Sub-Path beginnend vom angegebenen Start-Index bis zur letzten Kante.
     *
     * @param pathStepStartIndex
     * @return
     */
    public SimpleMetaPath getSubPath(final int pathStepStartIndex) {
        return getSubPath(pathStepStartIndex, getMetaPathCount());
    }

    /**
     * Liefert einen Sub-Path beginnend vom angegebenen Start-Index bis zum MetaPath vor dem End-Index (exklusive).
     *
     * @param pathStepStartIndex
     * @param pathStepEndIndex
     * @return
     */
    public SimpleMetaPath getSubPath(final int pathStepStartIndex, final int pathStepEndIndex) {
        int fullPathLength = metaPaths.size();
        if (pathStepStartIndex >= pathStepEndIndex || pathStepStartIndex < 0 || pathStepStartIndex >= fullPathLength || pathStepEndIndex < 0 || pathStepEndIndex > fullPathLength) {
            throw new IllegalArgumentException("Invalid pathStepStartIndex=" + pathStepStartIndex + " and pathStepEndIndex=" + pathStepEndIndex);
        }
        //ACHTUNG: Hier muss man evtl. noch vor und nach der letzten Kante einen SimpleMetaPath hinzufügen, der nur die Elementart einscgränkt, weil die Elementart der "weggeschnittenen" Kante die Start- oder Zielklasse des verkürzten Pfades eigentlich einschränken kann
        ElementaryMetaPath[] metaPathsArray = metaPaths.toArray(new ElementaryMetaPath[0]);
        ElementaryMetaPath[] subMetaPathsArray = new ElementaryMetaPath[pathStepEndIndex - pathStepStartIndex];
        System.arraycopy(metaPathsArray, pathStepStartIndex, subMetaPathsArray, 0, subMetaPathsArray.length);
        return new SimpleMetaPath(subMetaPathsArray);
    }

    //public final int getEdgesCount

}
