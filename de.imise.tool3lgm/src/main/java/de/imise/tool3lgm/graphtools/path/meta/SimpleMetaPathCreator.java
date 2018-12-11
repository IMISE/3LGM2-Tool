package de.imise.tool3lgm.graphtools.path.meta;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS (5 Dec 2018)
 */
public class SimpleMetaPathCreator {

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
    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        return createSimpleMetaPath(startClass, endClass, null, associations);
    }

    /**
     * @param startClass
     * @param endClass
     * @param metaPaths
     */
    public static SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(initFullPath(startClass, endClass, metaPaths));
    }

    /**
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(baseResKeyOrName, initFullPath(startClass, endClass, metaPaths));
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations Das ist eine Liste aus Element- und Kantenklassen. Diese Liste kann nur einen validen Pfad definieren, wenn niemals zwei
     *            reine Elementklassen (die also keine Kantenklassen sind) hintereinander stehen. Es steht immer eine Kantenklasse hinter einer
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) {
        ElementaryMetaPath[] metaPaths = new ElementaryMetaPath[associations.length];
        Class<? extends ModelElement> start = startClass;
        for (int i = 0; i < associations.length; i++) {
            Class<? extends Edge> edgeClass = associations[i];
            Direction direction = getEdgeDirection(start, edgeClass);
            ElementaryMetaPath metaPath = i == 0 ? ElementaryMetaPathHandler.getMetaPath(start, edgeClass, direction)
                    : i == metaPaths.length - 1 ? ElementaryMetaPathHandler.getMetaPath(edgeClass, direction, endClass) : ElementaryMetaPathHandler.getMetaPath(edgeClass, direction);
            metaPaths[i] = metaPath;
            start = metaPath.getEndClass();
        }
        SimpleMetaPath simpleMetaPath = new SimpleMetaPath(baseResKeyOrName, metaPaths);
        return simpleMetaPath;
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
        int lastMetaPathIndex = metaPaths.length - 1;
        if (lastMetaPathIndex == 0) {
            metaPaths[0] = ElementaryMetaPathHandler.getMetaPath(startClass, metaPaths[0], endClass);
        } else if (lastMetaPathIndex > 0) {
            metaPaths[0] = ElementaryMetaPathHandler.getMetaPath(startClass, metaPaths[0], metaPaths[0].getEndClass());
            metaPaths[lastMetaPathIndex] = ElementaryMetaPathHandler.getMetaPath(metaPaths[lastMetaPathIndex].getStartClass(), metaPaths[lastMetaPathIndex], endClass);
        }
        return metaPaths;
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
