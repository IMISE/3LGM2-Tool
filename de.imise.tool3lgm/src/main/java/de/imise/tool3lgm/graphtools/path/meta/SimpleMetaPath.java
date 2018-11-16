package de.imise.tool3lgm.graphtools.path.meta;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;

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
     * @param startClass
     * @param endClass
     * @param metaPaths
     */
    public SimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        super(initFullPath(startClass, endClass, metaPaths));
    }

    /**
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        super(initFullPath(startClass, endClass, metaPaths));
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleMetaPath(final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
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
    public boolean isValid() {
        return super.isValid();
    }

    /**
     * Wenn die übergebene Startklasse nicht dieselbe Klasse ist, wie die Startklasse des ersten Metapfades, dann wird im Ergebis-Array aller
     * MetaPfade ein MetaPfad vorangestellt, der nur die übergebene Startklasse enthält. Dasselbe gilt für die Endklasse und die Endklasse des letzten
     * Elementarpfades.
     *
     * @param startClass
     * @param endClass
     * @param metaPaths
     * @return
     */
    private static final ElementaryMetaPath[] initFullPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        Class<? extends ModelElement> pathStartClass = metaPaths[0].getStartClass();
        boolean appendStartMetaPath = startClass != pathStartClass && pathStartClass.isAssignableFrom(startClass); //die übergebene Startklasse ist spezieller, als die Startklasse der ersten Kante
        Class<? extends ModelElement> pathEndClass = metaPaths[metaPaths.length - 1].getEndClass();
        boolean appendEndMetaPath = endClass != pathEndClass && pathEndClass.isAssignableFrom(endClass); //die übergebene Endklasse ist spezieller als die Endklasse der letzten Kante
        int newPathLength = metaPaths.length + (appendStartMetaPath ? 1 : 0) + (appendEndMetaPath ? 1 : 0);
        if (newPathLength == metaPaths.length) {
            return metaPaths;
        }
        ElementaryMetaPath[] newMetaPaths = new ElementaryMetaPath[newPathLength];
        if (appendStartMetaPath) {
            newMetaPaths[0] = new ElementaryMetaPath(startClass);
        }
        if (appendEndMetaPath) {
            newMetaPaths[newMetaPaths.length - 1] = new ElementaryMetaPath(endClass);
        }
        System.arraycopy(metaPaths, 0, newMetaPaths, appendStartMetaPath ? 1 : 0, metaPaths.length);
        return newMetaPaths;
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param associations
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath create(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        return create(startClass, endClass, null, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath create(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) {
        return create(startClass, endClass, baseResKeyOrName, null, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath create(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final MetaPathDefinition elementaryMetaPathSource,
            final Class<? extends Edge>... associations) {
        Class<? extends ModelElement> start = startClass;
        Class<? extends Edge> edgeClass = associations[0];
        if (!Edge.isStartOrEndClass(edgeClass, startClass)) {
            return null;
        }
        ElementaryMetaPath[] metaPaths = new ElementaryMetaPath[associations.length];
        for (int i = 0; i < associations.length; i++) {
            edgeClass = associations[i];
            Direction direction = getEdgeDirection(start, edgeClass);
            ElementaryMetaPath metaPath = elementaryMetaPathSource != null ? elementaryMetaPathSource.getMetaPath(edgeClass, direction) : new ElementaryMetaPath(edgeClass, direction);
            metaPaths[i] = metaPath;
            start = metaPath.getEndClass();
        }
        //die übergebene Endklasse muss auch eine Endklasse des letzten MetaPfades sein
        if (!AbstractMetaPath.isEndClass(metaPaths[metaPaths.length - 1], endClass, true, true)) {
            return null;
        }
        SimpleMetaPath simpleMetaPath = new SimpleMetaPath(startClass, endClass, baseResKeyOrName, metaPaths);
        return simpleMetaPath;
    }

    /**
     * Wenn die übergebene Elementklasse die Startklasse der übergebenen Kantenklasse ist, dann kommt Direction.FORWARD zurück.
     * Ist sie die Endklasse, kommt Direction.BACKWARD zurück und wenn sie gar nicht passt, dann null. Es wird genau in dieser
     * Reihenfolge geprüft, also wenn die übergebene Klasse Start- und Endklasse der Kantenklasse ist, dann kommt Direction.FORWARD.
     *
     * @param startClass
     * @param edgeClass
     * @return
     */
    public static final Direction getEdgeDirection(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass) {
        Direction direction = Edge.isStartClass(edgeClass, startClass) ? Direction.FORWARD : Edge.isEndClass(edgeClass, startClass) ? Direction.BACKWARD : null;
        return direction;
    }

}
