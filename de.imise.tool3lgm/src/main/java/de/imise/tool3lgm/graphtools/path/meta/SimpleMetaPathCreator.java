package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;

/**
 * @author AXS (5 Dec 2018)
 */
public class SimpleMetaPathCreator {

    //    /**
    //     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} ausgehend von der Startklasse, die übergeben wurde. Die
    //     * Richtungen werden aus dder Startklasse abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
    //     * rückwärts dreht, dann wird immer vorwärts angenommen.
    //     *
    //     * @param startClass
    //     * @param associations
    //     * @return
    //     */
    //    @SafeVarargs
    //    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge>... associations) {
    //        return createSimpleMetaPath(startClass, null, null, associations);
    //    }
    //
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
        return createSimpleMetaPath(startClass, endClass, null, -1, associations);
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
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName
     *            Index des Elementarpfadschrittes, der den Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die Namensgenerierung über den
     *            super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn er damit auch nichts findet "ist verbunden mit" ausgibt.
     * @param metaPaths
     */
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(metaPathStepWithPathName, initFullPath(startClass, endClass, metaPaths));
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
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations)
            throws IllegalArgumentException {
        return createSimpleMetaPath(startClass, endClass, baseResKeyOrName, -1, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName
     *            Index des Elementarpfadschrittes, der den Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die Namensgenerierung über den
     *            super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn er damit auch nichts findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen. Diese Liste kann nur einen validen Pfad definieren, wenn niemals zwei
     *            reine Elementklassen (die also keine Kantenklassen sind) hintereinander stehen. Es steht immer eine Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final Class<? extends Edge>... associations)
            throws IllegalArgumentException {
        return createSimpleMetaPath(startClass, endClass, null, metaPathStepWithPathName, associations);
    }
    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPathStepWithPathName
     *            Index des Elementarpfadschrittes, der den Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die Namensgenerierung über den
     *            super-Namensmechanismus, der den baseResKeyOrName auswertet und wenn er damit auch nichts findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen. Diese Liste kann nur einen validen Pfad definieren, wenn niemals zwei
     *            reine Elementklassen (die also keine Kantenklassen sind) hintereinander stehen. Es steht immer eine Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    private static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final int metaPathStepWithPathName,
            final Class<? extends Edge>... associations) throws IllegalArgumentException {
        ElementaryMetaPath[] metaPaths = new ElementaryMetaPath[associations.length];
        Class<? extends ModelElement> start = startClass;
        for (int i = 0; i < associations.length; i++) {
            Class<? extends Edge> edgeClass = associations[i];
            Direction direction = getEdgeDirection(start, edgeClass, i == associations.length - 1 ? endClass : null); // bei der letzten Kante muss die Endklasse passen. Wenn bei einer Kante in der Mitte des Pfades die nächste Kante nicht passt, dann wird das unten druch Zurücklaufen erkannt
            //die Elementklasse passt nicht zur aktuellen Kante
            if (direction == null) {
                //solange zur vorherigen Kante zurück gehen, bis man eine findet, die sowohl vorwärts als auch rückwärts passt und diese dann mit rückwärts probieren
                for (--i; i >= 0; i--) {
                    if (metaPaths[i].hasDirectionForward()) { //das bedeutet, dass die aktuelle Kante in Vorwärsrichtung gelesen genommen wurde, was immer die zuerst gesuchte Richtung ist
                        if (isEdgeDirectionBackward(startClass, edgeClass)) { //falls die Kante auch rückwärts im Pfad sein kann
                            direction = Direction.BACKWARD;
                            break;
                        }
                    }
                }
                if (i < 0) {
                    //der Pfad ist fehlerhaft, d. h. trotz Zurücklaufen und Test mit der Gegenrichtung passen die Kanten nicht zueinander
                    throw new IllegalArgumentException("EdgeClasses dosn't define a valid metapath");
                }
            }
            ElementaryMetaPath metaPath;
            if (metaPaths.length == 1) {
                metaPath = ElementaryMetaPathHandler.getMetaPath(start, edgeClass, direction, endClass);
            } else if (i == 0) {
                metaPath = ElementaryMetaPathHandler.getMetaPath(start, edgeClass, direction);
            } else if (i == metaPaths.length - 1 && endClass != null) {
                metaPath = ElementaryMetaPathHandler.getMetaPath(edgeClass, direction, endClass);
            } else {
                metaPath = ElementaryMetaPathHandler.getMetaPath(edgeClass, direction);
            }
            metaPaths[i] = metaPath;
            start = metaPath.getEndClass();
        }
        SimpleMetaPath simpleMetaPath = metaPathStepWithPathName < 0 ? new SimpleMetaPath(baseResKeyOrName, metaPaths) : new SimpleMetaPath(metaPathStepWithPathName, metaPaths);
        return simpleMetaPath;
    }

    /**
     * Erzeugt ein Array von allen konkreten MetaPfaden, die dem ggf. abstrakten übergebenen MetaPfad entsprechen. Ist keine der übergebenen
     * Kantenklassen abstrakt, dann kommt in dem Set nur der übergebene Pfad zurück.
     *
     * @param metaPathStepWithPathName
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath[] createSimpleMetaPaths(final Class<? extends ModelElement> startClass, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath path = SimpleMetaPathCreator.createSimpleMetaPath(startClass, null, metaPathStepWithPathName, edgeClasses);
        Collection<SimpleMetaPath> simpleMetaPathsNonAbstract = MetaPathFunctions.getSimpleMetaPathsNonAbstract(path);
        SimpleMetaPath[] simpleMetaPaths = new SimpleMetaPath[simpleMetaPathsNonAbstract.size()];
        simpleMetaPaths = simpleMetaPathsNonAbstract.toArray(simpleMetaPaths);
        return simpleMetaPaths;
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
     * @param endClass ist diese Klasse null, wird nur die startClass berücksichtigt
     * @return
     */
    public static final Direction getEdgeDirection(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass) {
        Direction direction = Edge.isStartClass(edgeClass, startClass) && (endClass == null || Edge.isEndClass(edgeClass, endClass)) ? Direction.FORWARD
                : Edge.isEndClass(edgeClass, startClass) && (endClass == null || Edge.isStartClass(edgeClass, endClass)) ? Direction.BACKWARD : null;
        return direction;
    }

    public static boolean isEdgeDirectionBackward(final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass) {
        return Edge.isEndClass(edgeClass, startClass);
    }

}
