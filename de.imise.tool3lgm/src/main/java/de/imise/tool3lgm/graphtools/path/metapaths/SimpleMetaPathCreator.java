package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.ReflectionUtils;

/**
 * @author AXS (5 Dec 2018)
 */
public class SimpleMetaPathCreator extends MetaModelSpecificAdapter {

    //    /**
    //     * Erzeugt aus den übergebenen Assoziationen einen {@link SimpleMetaPath}
    //     * ausgehend von der Startklasse, die übergeben wurde. Die Richtungen werden
    //     * aus dder Startklasse abgeleitet. Wenn es nicht eindeutig ist, ob die
    //     * Startklasse die Kante vorwärts oder rückwärts dreht, dann wird immer
    //     * vorwärts angenommen.
    //     *
    //     * @param startClass
    //     * @param associations
    //     * @return
    //     */
    //    @SafeVarargs
    //    public static final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends Edge>... associations) {
    //        return createSimpleMetaPath(startClass, null, null, associations);
    //    }

    /**
     * @param metaModelSpecific MetaModel source, in dem die Pfade angelegt
     *            werden
     */
    public SimpleMetaPathCreator(final MetaModelSpecific metaModelSpecific) {
        super(metaModelSpecific);
    }

    // braucht keine eigene equals() und hashCode(), weil es derselbe Builder wird, wenn die selbe MetaModelDefintion-Klasse zurgunde liegt
    //    @Override
    //    public int hashCode() {
    //        return super.hashCode();
    //    }
    //
    //    @Override
    //    public boolean equals(final Object obj) {
    //        return super.equals(obj);
    //    }
    //
    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param associations
     * @return
     */
    @SafeVarargs
    public final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        MetaModel metaModel = getMetaModel();
        return createSimpleMetaPath(metaModel, startClass, endClass, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param associations
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        return createSimpleMetaPath(metaModel, startClass, endClass, true, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param tryForwardDirectionFirst This parameter is only relevant, if the
     *            metapath contains edge classes which fit the path in both
     *            directions, because the start and end class of the edge are
     *            the same or assignable. If <code>true</code> (default) all
     *            ambigious edges will be interpreted in forward direction. If
     *            <code>false</code> in backward direction.
     * @param associations
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final boolean tryForwardDirectionFirst,
            final Class<? extends Edge>... associations) {
        return createSimpleMetaPath(metaModel, startClass, endClass, null, -1, tryForwardDirectionFirst, associations);
    }

    /**
     * @param startClass
     * @param endClass
     * @param metaPaths
     */
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        MetaModel metaModel = getMetaModel();
        return createSimpleMetaPath(metaModel, startClass, endClass, metaPaths);
    }

    /**
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param metaPaths
     */
    public static SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(initFullPath(metaModel, startClass, endClass, metaPaths));
    }

    /**
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        MetaModel metaModel = getMetaModel();
        return createSimpleMetaPath(metaModel, startClass, endClass, baseResKeyOrName, metaPaths);
    }

    /**
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public static SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(baseResKeyOrName, initFullPath(metaModel, startClass, endClass, metaPaths));
    }

    /**
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param metaPaths
     */
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final ElementaryMetaPath... metaPaths) {
        MetaModel metaModel = getMetaModel();
        return createSimpleMetaPath(metaModel, startClass, endClass, metaPathStepWithPathName, metaPaths);
    }

    /**
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param metaPaths
     */
    public static SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final ElementaryMetaPath... metaPaths) {
        return new SimpleMetaPath(metaPathStepWithPathName, initFullPath(metaModel, startClass, endClass, metaPaths));
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) throws IllegalArgumentException {
        MetaModel metaModel = getMetaModel();
        return createSimpleMetaPath(metaModel, startClass, endClass, baseResKeyOrName, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations)
            throws IllegalArgumentException {
        return createSimpleMetaPath(metaModel, startClass, endClass, baseResKeyOrName, true, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param tryForwardDirectionFirst This parameter is only relevant, if the
     *            metapath contains edge classes which fit the path in both
     *            directions, because the start and end class of the edge are
     *            the same or assignable. If <code>true</code> (default) all
     *            ambigious edges will be interpreted in forward direction. If
     *            <code>false</code> in backward direction.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final boolean tryForwardDirectionFirst,
            final Class<? extends Edge>... associations) throws IllegalArgumentException {
        return createSimpleMetaPath(metaModel, startClass, endClass, baseResKeyOrName, -1, tryForwardDirectionFirst, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * ausgehend von der Startklasse, die übergeben wurde. Die Richtungen und
     * Endklasse werden sukkessive abgeleitet. Wenn es nicht eindeutig ist, ob
     * die Startklasse die Kante vorwärts oder rückwärts dreht, dann wird immer
     * vorwärts angenommen.
     *
     * @param startClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final int metaPathStepWithPathName, final Class<? extends Edge>... associations) throws IllegalArgumentException {
        MetaModel metaModel = getMetaModel();
        return createSimpleMetaPath(metaModel, startClass, metaPathStepWithPathName, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * ausgehend von der Startklasse, die übergeben wurde. Die Richtungen und
     * Endklasse werden sukkessive abgeleitet. Wenn es nicht eindeutig ist, ob
     * die Startklasse die Kante vorwärts oder rückwärts dreht, dann wird immer
     * vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final int metaPathStepWithPathName, final Class<? extends Edge>... associations) throws IllegalArgumentException {
        return createSimpleMetaPath(metaModel, startClass, null, metaPathStepWithPathName, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final Class<? extends Edge>... associations)
            throws IllegalArgumentException {
        MetaModel metaModel = getMetaModel();
        return createSimpleMetaPath(metaModel, startClass, endClass, metaPathStepWithPathName, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName,
            final Class<? extends Edge>... associations) throws IllegalArgumentException {
        return createSimpleMetaPath(metaModel, startClass, endClass, metaPathStepWithPathName, true, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param tryForwardDirectionFirst This parameter is only relevant, if the
     *            metapath contains edge classes which fit the path in both
     *            directions, because the start and end class of the edge are
     *            the same or assignable. If <code>true</code> (default) all
     *            ambigious edges will be interpreted in forward direction. If
     *            <code>false</code> in backward direction.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final boolean tryForwardDirectionFirst,
            final Class<? extends Edge>... associations) throws IllegalArgumentException {
        return createSimpleMetaPath(metaModel, startClass, endClass, null, metaPathStepWithPathName, tryForwardDirectionFirst, associations);
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param tryForwardDirectionFirst This parameter is only relevant, if the
     *            metapath contains edge classes which fit the path in both
     *            directions, because the start and end class of the edge are
     *            the same or assignable. If <code>true</code> (default) all
     *            ambigious edges will be interpreted in forward direction. If
     *            <code>false</code> in backward direction.
     * @param associations Das ist eine Liste aus Element- und Kantenklassen.
     *            Diese Liste kann nur einen validen Pfad definieren, wenn
     *            niemals zwei reine Elementklassen (die also keine
     *            Kantenklassen sind) hintereinander stehen. Es steht immer eine
     *            Kantenklasse hinter einer
     * @return
     * @throws IllegalArgumentException
     */
    @SafeVarargs
    private static final SimpleMetaPath createSimpleMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final int metaPathStepWithPathName,
            final boolean tryForwardDirectionFirst, final Class<? extends Edge>... associations) throws IllegalArgumentException {
        ElementaryMetaPath[] metaPaths = new ElementaryMetaPath[associations.length];
        Class<? extends ModelElement> start = startClass;
        for (int i = 0; i < associations.length; i++) {
            Class<? extends Edge> edgeClass = associations[i];
            ElementaryMetaPath metaPath = createElementaryMetaPath(metaModel, start, edgeClass, i == associations.length - 1 ? endClass : null, tryForwardDirectionFirst); // bei der letzten Kante muss die Endklasse passen. Wenn bei einer Kante in der Mitte des Pfades die nächste Kante nicht passt, dann wird das unten druch Zurücklaufen erkannt
            if (metaPath == null) {
                metaPath = createElementaryMetaPath(metaModel, start, edgeClass, i == associations.length - 1 ? endClass : null, !tryForwardDirectionFirst); // bei der letzten Kante muss die Endklasse passen. Wenn bei einer Kante in der Mitte des Pfades die nächste Kante nicht passt, dann wird das unten druch Zurücklaufen erkannt
            }
            //die Elementklasse passt nicht zur aktuellen Kante
            if (metaPath == null) {
                //solange zur vorherigen Kante zurück gehen, bis man eine findet, die sowohl vorwärts als auch rückwärts passt und diese dann mit rückwärts probieren
                for (--i; i >= 0; i--) {
                    boolean isForward = metaPaths[i].hasDirectionForward();
                    if (tryForwardDirectionFirst && isForward || !tryForwardDirectionFirst && !isForward) {
                        metaPath = createElementaryMetaPath(metaModel, start, edgeClass, i == associations.length - 1 ? endClass : null, !tryForwardDirectionFirst);
                        if (metaPath != null) { //falls die Kante auch rückwärts im Pfad sein kann, also die Startklasse des Pfades auch die Endklasse der Kante sein könnte und somit die Richtung BACKWARD sein könnte
                            break;
                        }
                    }
                }
                if (i < 0) {
                    //der Pfad ist fehlerhaft, d. h. trotz Zurücklaufen und Test mit der Gegenrichtung passen die Kanten nicht zueinander
                    //das kann gewollt sein, deswegen gibt es hier keine zwingende Fehlerausgabe
                    //Sys.err1("EdgeClasses dosn't define a valid metapath");
                    return null;
                }
            }
            metaPaths[i] = metaPath;
            start = metaPath.getEndClass();
        }
        SimpleMetaPath simpleMetaPath = metaPathStepWithPathName < 0 ? new SimpleMetaPath(baseResKeyOrName, metaPaths) : new SimpleMetaPath(metaPathStepWithPathName, metaPaths);
        return simpleMetaPath;
    }

    /////////////////////////////////////////////////////////
    // START createSimpleMetaPath for a given ModelElement //
    /////////////////////////////////////////////////////////

    /**
     * @param me
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final ModelElement me, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        return createSimpleMetaPath(me, null, metaPathStepWithPathName, edgeClasses);
    }

    /**
     * @param me
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final ModelElement me, final Class<? extends Edge>... edgeClasses) {
        return createSimpleMetaPath(me, (Class<? extends ModelElement>) null, edgeClasses);
    }

    /**
     * @param me
     * @param endClass
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final ModelElement me, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... edgeClasses) {
        return SimpleMetaPathCreator.createSimpleMetaPath(me.getMetaModel(), me.getClass(), endClass, edgeClasses);
    }

    /**
     * @param me
     * @param baseResKeyOrName
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final ModelElement me, final String baseResKeyOrName, final Class<? extends Edge>... edgeClasses) {
        return createSimpleMetaPath(null, baseResKeyOrName, edgeClasses);
    }

    /**
     * @param me
     * @param endClass
     * @param baseResKeyOrName
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final ModelElement me, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... edgeClasses) {
        return SimpleMetaPathCreator.createSimpleMetaPath(me.getMetaModel(), me.getClass(), endClass, baseResKeyOrName, edgeClasses);
    }

    /**
     * @param me
     * @param endClass
     * @param metaPathStepWithPathName Index des Elementarpfadschrittes, der den
     *            Namen des Gesamtpfades festlegt. Ist er kleiner 0 läuft die
     *            Namensgenerierung über den super-Namensmechanismus, der den
     *            baseResKeyOrName auswertet und wenn er damit auch nichts
     *            findet "ist verbunden mit" ausgibt.
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath createSimpleMetaPath(final ModelElement me, final Class<? extends ModelElement> endClass, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        return SimpleMetaPathCreator.createSimpleMetaPath(me.getMetaModel(), me.getClass(), endClass, metaPathStepWithPathName, edgeClasses);
    }

    ///////////////////////////////////////////////////////
    // END createSimpleMetaPath for a given ModelElement //
    ///////////////////////////////////////////////////////

    /**
     * Erzeugt ein Array von allen konkreten MetaPfaden, die dem ggf. abstrakten
     * übergebenen MetaPfad entsprechen. Ist keine der übergebenen Kantenklassen
     * abstrakt, dann kommt in dem Set nur der übergebene Pfad zurück.
     *
     * @param me
     * @param metaPathStepWithPathName
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath[] createSimpleMetaPaths(final ModelElement me, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        return SimpleMetaPathCreator.createSimpleMetaPaths(me.getMetaModel(), me.getClass(), metaPathStepWithPathName, edgeClasses);
    }

    /**
     * Erzeugt ein Array von allen konkreten MetaPfaden, die dem ggf. abstrakten
     * übergebenen MetaPfad entsprechen. Ist keine der übergebenen Kantenklassen
     * abstrakt, dann kommt in dem Set nur der übergebene Pfad zurück.
     *
     * @param metaModel
     * @param metaPathStepWithPathName
     * @param edgeClasses
     * @return
     */
    @SafeVarargs
    public static final SimpleMetaPath[] createSimpleMetaPaths(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final int metaPathStepWithPathName, final Class<? extends Edge>... edgeClasses) {
        SimpleMetaPath path = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, startClass, null, metaPathStepWithPathName, edgeClasses);
        Collection<SimpleMetaPath> simpleMetaPathsNonAbstract = getSimpleMetaPathsNonAbstract(path);
        SimpleMetaPath[] simpleMetaPaths = new SimpleMetaPath[simpleMetaPathsNonAbstract.size()];
        simpleMetaPaths = simpleMetaPathsNonAbstract.toArray(simpleMetaPaths);
        return simpleMetaPaths;
    }

    /**
     * Wenn die übergebene Startklasse nicht dieselbe Klasse ist, wie die
     * Startklasse des ersten Metapfades, dann wird im Ergebis-Array aller
     * MetaPfade ein MetaPfad vorangestellt, der nur die übergebene Startklasse
     * enthält. Dasselbe gilt für die Endklasse und die Endklasse des letzten
     * Elementarpfades.
     *
     * @param metaModel
     * @param startClass
     * @param endClass
     * @param metaPaths
     * @return
     */
    private static final ElementaryMetaPath[] initFullPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final ElementaryMetaPath... metaPaths) {
        int lastMetaPathIndex = metaPaths.length - 1;
        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
        if (lastMetaPathIndex == 0) {
            metaPaths[0] = elementaryMetaPathHandler.getMetaPath(startClass, metaPaths[0], endClass);
        } else if (lastMetaPathIndex > 0) {
            metaPaths[0] = elementaryMetaPathHandler.getMetaPath(startClass, metaPaths[0], metaPaths[0].getEndClass());
            metaPaths[lastMetaPathIndex] = elementaryMetaPathHandler.getMetaPath(metaPaths[lastMetaPathIndex].getStartClass(), metaPaths[lastMetaPathIndex], endClass);
        }
        return metaPaths;
    }

    /**
     * Wenn die übergebene Elementklasse die Startklasse der übergebenen
     * Kantenklasse ist, dann kommt Direction.FORWARD zurück. Ist sie die
     * Endklasse, kommt Direction.BACKWARD zurück und wenn sie gar nicht passt,
     * dann null. Es wird genau in dieser Reihenfolge geprüft, also wenn die
     * übergebene Klasse Start- und Endklasse der Kantenklasse ist, dann kommt
     * Direction.FORWARD.
     *
     * @param metaModel
     * @param startClass
     * @param edgeClass
     * @param endClass ist diese Klasse null, wird nur die startClass
     *            berücksichtigt
     * @param tryForwardDirectionFirst This parameter is only relevant, if the
     *            metapath contains edge classes which fit the path in both
     *            directions, because the start and end class of the edge are
     *            the same or assignable. If <code>true</code> (default) all
     *            ambigious edges will be interpreted in forward direction. If
     *            <code>false</code> in backward direction.
     * @return
     */
    private static ElementaryMetaPath createElementaryMetaPath(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass, final boolean forward) {
        //first test if the elements can be combined as an ELEMENT_EDGE_ELEMENT ElementaryMetaPath
        Direction direction = getEdgeDirectionForELEMENT_EDGE_ELEMENT(metaModel, startClass, edgeClass, endClass, forward);
        ElementaryMetaPath elementaryMetaPath;
        if (direction != null) {
            //die vom ElementaryMetaPathHandler angelegten Pfade haben immer den type ELEMENT_EDGE_ELEMENT
            ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
            elementaryMetaPath = elementaryMetaPathHandler.getMetaPath(startClass, edgeClass, direction, endClass);
        } else {
            //try now to constuct a START_WIT_EDGE or ENT_WITH_EDGE metapath
            elementaryMetaPath = createTempateMetPath_START_WITH_EDGE_or_END_WITH_EDGE(metaModel, startClass, edgeClass, endClass, forward);
        }
        return elementaryMetaPath;
    }

    /**
     * @param metaModel
     * @param startClass
     * @param edgeClass
     * @param endClass
     * @param forward
     * @return
     */
    private static ElementaryMetaPath createTempateMetPath_START_WITH_EDGE_or_END_WITH_EDGE(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass,
            final boolean forward) {
        //path cannot be an ELEMENT_EDGE_ELEMENT ElementaryMetaPath -> test START_WITH_EDGE or END_WITH_EDGE
        //one of the start and endClass must a regular start- or endClass of the edge and the other must be an assinable edge class to the edgeClass of the matpath
        ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();
        if (forward) {
            if (startClass != null) {
                Class<? extends ModelElement> commonSuperStartAndEdgeClass = ReflectionUtils.getMostSpecialClass(startClass, edgeClass);
                if (commonSuperStartAndEdgeClass != null) { // START_WITH_EDGE
                    if (endClass == null || CoreMetaModel.isEndClassOrEndClassSuperclass(edgeClass, endClass)) {
                        return emph.getEdgeToEndElementMetaPath(edgeClass, endClass); //FORWARD to edgeEndElement
                    } else if (CoreMetaModel.isStartClassOrStartClassSuperclass(edgeClass, endClass)) {
                        return emph.getEdgeToStartElementMetaPath(edgeClass, endClass); //BACKWARD to edgeStartElement
                    }
                }
            }
        } else {
            if (endClass != null) {
                Class<? extends ModelElement> commonSuperEndAndEdgeClass = ReflectionUtils.getMostSpecialClass(endClass, edgeClass);
                if (commonSuperEndAndEdgeClass != null) { // END_WITH_EDGE
                    if (startClass == null || CoreMetaModel.isStartClassOrStartClassSuperclass(edgeClass, startClass)) {
                        return emph.getStartElementToEdgeMetaPath(startClass, edgeClass);//FORWARD to edge
                    } else if (CoreMetaModel.isEndClassOrEndClassSuperclass(edgeClass, startClass)) {
                        return emph.getEndElementToEdgeMetaPath(startClass, edgeClass);//BACKWARD to edge
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param metaModel
     * @param startClass
     * @param edgeClass
     * @param endClass
     * @param forward
     */
    private static Direction getEdgeDirectionForELEMENT_EDGE_ELEMENT(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass, final boolean forward) {
        if (forward) {
            if (isForward(metaModel, startClass, edgeClass, endClass)) {
                return Direction.FORWARD;
            }
        } else {
            if (isBackward(metaModel, startClass, edgeClass, endClass)) {
                return Direction.BACKWARD;
            }
        }
        return null;
    }

    /**
     * @param metaModel
     * @param startClass
     * @param edgeClass
     * @param endClass
     * @return
     */
    private static boolean isForward(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass) {
        return (startClass == null || CoreMetaModel.isStartClassOrStartClassSuperclass(edgeClass, startClass)) && (endClass == null || CoreMetaModel.isEndClassOrEndClassSuperclass(edgeClass, endClass));
    }

    /**
     * @param metaModel
     * @param startClass
     * @param edgeClass
     * @param endClass
     * @return
     */
    private static boolean isBackward(final MetaModel metaModel, final Class<? extends ModelElement> startClass, final Class<? extends Edge> edgeClass, final Class<? extends ModelElement> endClass) {
        return startClass == null || CoreMetaModel.isEndClassOrEndClassSuperclass(edgeClass, startClass) && endClass == null || CoreMetaModel.isStartClassOrStartClassSuperclass(edgeClass, endClass);
    }

    /**
     * Es werden alle verschiedenen {@link SimpleMetaPath}s zurück gegeben, bei
     * denen die im übergebenen {@link SimpleMetaPath} eventuell abstrakten
     * Kantenklassen durch konkrete ersetzt wurden.
     *
     * @param simpleMetaPath
     * @return
     */
    public static Collection<SimpleMetaPath> getSimpleMetaPathsNonAbstract(final SimpleMetaPath simpleMetaPath) {
        //Ergebnisliste
        List<SimpleMetaPath> simpleMetaPaths = new ArrayList<>();
        //übergebenen MetaPfad als erstes in die Ergebnisliste schreiben
        simpleMetaPaths.add(simpleMetaPath);
        List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
        //jetzt für jeden Elementarpfadschritt des Ausgangspfades immer alle Pfade in die Ergebnisliste schreiben, die nur noch Elementarpfadschritte mit nocht-abstrakten Knotenklassen haben
        for (int i = 0; i < elementaryMetaPaths.size(); i++) {
            replaceSimpleMetaPathsWithNonAbstractPathStepConnectingClasses(simpleMetaPaths, i);
        }
        removeInvalidMetaPaths(simpleMetaPaths);
        //dann alle Varianten von abstracten Kanten in den Pfaden ersetzen
        for (int i = 0; i < elementaryMetaPaths.size(); i++) {
            replaceSimpleMetaPathsWithNonAbstractEdgeClasses(simpleMetaPaths, i);
            replaceSimpleMetaPathsWithDoubleMeaningEdgesBothConnectionStates(simpleMetaPaths, i);
        }
        removeInvalidMetaPaths(simpleMetaPaths);
        return simpleMetaPaths;
    }

    private static void removeInvalidMetaPaths(final List<SimpleMetaPath> simpleMetaPaths) {
        for (int i = simpleMetaPaths.size() - 1; i >= 0; i--) {
            SimpleMetaPath simpleMetaPath = simpleMetaPaths.get(i);
            if (!simpleMetaPath.isValid()) {
                simpleMetaPaths.remove(i);
            }
        }
    }

    /**
     * Für jeden der Pfade in der Liste wird geprüft, ob das Zwischenelement des
     * Elementarpfadschrittes mit dem übergebenen Index und des darauf folgenden
     * abstract ist. Wenn es abstract ist, dann wird der Metapfad in der Liste
     * durch alle MetaPfade ersetzt, bei denen die abstrakte Zwischenklasse
     * durch alle konkreten ersetzt wurde.
     *
     * @param simpleMetaPaths
     * @param currentPathStepIndex
     * @return
     */
    private static List<SimpleMetaPath> replaceSimpleMetaPathsWithNonAbstractPathStepConnectingClasses(final List<SimpleMetaPath> simpleMetaPaths, final int currentPathStepIndex) {
        //bei jedem MetaPfad der Liste
        for (int p = 0; p < simpleMetaPaths.size(); p++) {
            SimpleMetaPath simpleMetaPath = simpleMetaPaths.get(p);
            //alle nicht-abstrakten Elementklassen der Start- und Endklasse ermitteln
            MetaModel metaModel = simpleMetaPath.getMetaModel();

            //Start- und Endklasse des aktuellen Pfadschrittes aus dem originalen MetaPfad ermitteln
            Class<? extends ModelElement> pathStepConnectingStartClass = currentPathStepIndex == 0 ? simpleMetaPath.getStartClass() : simpleMetaPath.getElementaryPathStepConnectingClass(currentPathStepIndex - 1);
            Class<? extends ModelElement> pathStepConnectingEndClass = simpleMetaPath.getElementaryPathStepConnectingClass(currentPathStepIndex);

            //Expand the start & end class only to all of its instanciable subclasses, if they are abstract. If they are not abstract
            //and they have subclasses, you must define the path for the subclasses by it's own or (if this functionality is needed)
            //this function here needs an boolean parameter which indicates if the classes should be also expanded to their subclasses.
            //Until now they will not be expanded if they are not abstract (= the last parameter is true)
            Collection<Class<? extends ModelElement>> instanciableAssignableStartClasses = metaModel.getInstanciableAssignableClasses(pathStepConnectingStartClass, true);
            Collection<Class<? extends ModelElement>> instanciableAssignableEndClasses = metaModel.getInstanciableAssignableClasses(pathStepConnectingEndClass, true);

            int startClassesCount = instanciableAssignableStartClasses.size();
            int endClassesCount = instanciableAssignableEndClasses.size();
            if (startClassesCount == 0 || endClassesCount == 0) {
                simpleMetaPaths.remove(p--);
                continue;
            }
            if (startClassesCount > 1 || endClassesCount > 1 || instanciableAssignableStartClasses.iterator().next() != pathStepConnectingStartClass || instanciableAssignableEndClasses.iterator().next() != pathStepConnectingEndClass) {
                List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
                ElementaryMetaPath originalElementaryMetaPath = elementaryMetaPaths.get(currentPathStepIndex);
                boolean replaceOriginalMetaPathInResultList = true;
                ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
                for (Class<? extends ModelElement> startClass : instanciableAssignableStartClasses) {
                    for (Class<? extends ModelElement> endClass : instanciableAssignableEndClasses) {
                        ElementaryMetaPath newElementaryMetaPath = elementaryMetaPathHandler.getMetaPath(startClass, originalElementaryMetaPath, endClass);
                        SimpleMetaPath newSimpleMetaPath = getPathStepReplacedMetaPath(elementaryMetaPaths, newElementaryMetaPath, currentPathStepIndex, simpleMetaPath.getName(), simpleMetaPath.getMetaPathStepWithPathName());
                        //bei der ersten nicht-abstrakten Kantenklasse wird der neue MetaPfad in der Ergebnisliste einfach über den neuen geschrieben
                        if (replaceOriginalMetaPathInResultList) {
                            replaceOriginalMetaPathInResultList = false;
                            simpleMetaPaths.set(p, newSimpleMetaPath); //den originalen MetaPfad durch den ersten neuen ersetzen
                        } else {
                            simpleMetaPaths.add(++p, newSimpleMetaPath); //den neuen MetaPfad einfügen und Index des aktuellen MetaPfades in der Gesamtliste hochsetzen
                        }
                    }
                }
            }
        }
        return simpleMetaPaths;
    }

    /**
     * @param elementaryMetaPaths
     * @param elementaryMetaPath
     * @param pathStepIndex
     * @param baseResKeyOrName
     * @param metaPathStepWithPathName
     * @return
     */
    private static SimpleMetaPath getPathStepReplacedMetaPath(final List<ElementaryMetaPath> elementaryMetaPaths, final ElementaryMetaPath elementaryMetaPath, final int pathStepIndex, final String baseResKeyOrName, final int metaPathStepWithPathName) {
        ElementaryMetaPath[] elementaryMetaPathArray = new ElementaryMetaPath[elementaryMetaPaths.size()];
        elementaryMetaPathArray = elementaryMetaPaths.toArray(elementaryMetaPathArray);
        elementaryMetaPathArray[pathStepIndex] = elementaryMetaPath;
        return metaPathStepWithPathName < 0 ? new SimpleMetaPath(baseResKeyOrName, elementaryMetaPathArray) : new SimpleMetaPath(metaPathStepWithPathName, elementaryMetaPathArray);
    }

    /**
     * Für jeden der Pfade in der Liste wird geprüft, ob die Kantensklasse des
     * Elementarpfadschrittes mit dem übergebenen Index abstract ist. Wenn er
     * abstract ist, dann wird der Metapfad in der Liste durch alle MetaPfade
     * ersetzt, bei denen die abstrakte Kantenklasse durch alle konkreten
     * ersetzt wurde.
     *
     * @param simpleMetaPaths
     * @param currentPathStepIndex
     * @return
     */
    private static List<SimpleMetaPath> replaceSimpleMetaPathsWithNonAbstractEdgeClasses(final List<SimpleMetaPath> simpleMetaPaths, final int currentPathStepIndex) {
        //bei jedem MetaPfad der Liste
        for (int p = 0; p < simpleMetaPaths.size(); p++) {
            SimpleMetaPath simpleMetaPath = simpleMetaPaths.get(p);
            List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
            //hole die Kantenklasse des aktuellen Pfadschrittes
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(currentPathStepIndex);
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();

            //wenn die Kantenklasse oder das Start- oder Endelement des Pfadschrittes abstract sind
            if (CoreMetaModel.isAbstract(edgeClass)) {
                //alle nicht-abstrakten Kantenklassen zwischen dieser Start- und Endklasse ermitteln
                MetaModel metaModel = simpleMetaPath.getMetaModel();
                //Start- und Edklasse des aktuellen Pfadschrittes aus dem originalen MetaPfad ermitteln
                Class<? extends ModelElement> pathStepConnectingStartClass = currentPathStepIndex == 0 ? simpleMetaPath.getStartClass() : simpleMetaPath.getElementaryPathStepConnectingClass(currentPathStepIndex - 1);
                Class<? extends ModelElement> pathStepConnectingEndClass = simpleMetaPath.getElementaryPathStepConnectingClass(currentPathStepIndex);
                Class<? extends Edge>[] edgeTypes = metaModel.getEdgeTypes(pathStepConnectingStartClass, pathStepConnectingEndClass);
                //Der erste neue SimpleMetaPath, bei dem der aktuelle Elementarpfadschritt durch einen mit nicht-abstrakter Kantenklasse ersetzt wurde, muss in der Ergenisliste den Original-MetaPfad ersetzen.
                //All anderen danach werden dahinter eingefügt und der Index des aktuellen Elementarpfadschrittes erhöht.
                boolean replaceOriginalMetaPathInResultList = true;
                //für alle gefundenen nicht-abstrakten Kantenarten zwischen der Start- und Endklasse des Original-MetaPfades
                for (Class<? extends Edge> edgeType : edgeTypes) {
                    //wenn die nicht-abstrakte Kantenklasse eine Unterklasse der abstrakten des Original-MetaPfades ist
                    if (edgeClass.isAssignableFrom(edgeType)) {
                        //Erzeuge ein neues Array aus Elementarpfaden, bei dem der aktuelle Pfadschritt immer durch einen Elementarmetapfad mit der nicht-abstrakten Kantenklasse ersetzt wird
                        ElementaryMetaPath[] elementaryMetaPathArray = new ElementaryMetaPath[elementaryMetaPaths.size()];
                        elementaryMetaPathArray = elementaryMetaPaths.toArray(elementaryMetaPathArray);
                        Direction elementaryMetaPathDirection = elementaryMetaPath.getDirection();
                        boolean readEdgeForward = elementaryMetaPathDirection == Direction.FORWARD;

                        //Start- und Endklasse des neuen Pfadschrittes ist die speziellere der jeweilgen Klassen vom Original-MetaPafd und der nicht-abstrakten Kantenklasse
                        Class<? extends ModelElement> pathStepStartClass = readEdgeForward ? Edge.getStartClass(edgeType) : Edge.getEndClass(edgeType);
                        Class<? extends ModelElement> pathStepEndClass = readEdgeForward ? Edge.getEndClass(edgeType) : Edge.getStartClass(edgeType);
                        pathStepStartClass = ReflectionUtils.getMostSpecialClass(pathStepConnectingStartClass, pathStepStartClass);
                        pathStepEndClass = ReflectionUtils.getMostSpecialClass(pathStepConnectingEndClass, pathStepEndClass);
                        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
                        //jetzt den neuen Elementarpfadschritt mit den speziellen Start- und Endklasse in derselben Richtung, in der die nicht abstrakte Kantenklasse gilt, anlegen
                        elementaryMetaPathArray[currentPathStepIndex] = elementaryMetaPathHandler.getMetaPath(pathStepStartClass, edgeType, elementaryMetaPathDirection, pathStepEndClass);
                        //den neuen SimpleMetaPfad mit der nicht-abstrakten Kantenklasse analog zum original anlegen (also mit den Index der Kante, die den Namen festlegt übernehmen)
                        int metaPathStepWithPathName = simpleMetaPath.getMetaPathStepWithPathName();
                        SimpleMetaPath newSimpleMetaPath = new SimpleMetaPath(metaPathStepWithPathName, elementaryMetaPathArray);
                        //bei der ersten nicht-abstrakten Kantenklasse wird der neue MetaPfad in der Ergebnisliste einfach über den neuen geschrieben
                        if (replaceOriginalMetaPathInResultList) {
                            replaceOriginalMetaPathInResultList = false;
                            simpleMetaPaths.set(p, newSimpleMetaPath); //den originalen MetaPfad durch den ersten neuen ersetzen
                        } else {
                            simpleMetaPaths.add(++p, newSimpleMetaPath); //den neuen MetaPfad einfügen und Index des aktuellen MetaPfades in der Gesamtliste hochsetzen
                        }
                    }
                }
                //keine einzige nicht-abstrakte Kantenklasse passte -> lösche den Original-MetaPfad aus der Ergebnisliste. Das hier ist relevant, wenn die SimpleMetaPfade nicht nur
                //über eine Folge von zusammenpassenden Kantenklassen sondern über nichtzusammenpassende Kantenklassen oder über Elementarpfadlisten definiert werden, bei denen die
                //z.B. mitten im Pfad zwei hintereinanderfolgende Pfadschtitte haben, die bei nur aus der abtrakten Klasse Edge.class bestehen und danach wieder irgendeine sehr spezielle
                //Kantenklasse, die sich mit vielen der bis dahin entstandenen SimpleMetaPaths nicht mehr zu einem sinnvollen MetaPfad zusammenfügen lassen -> die Pfade löschen
                if (replaceOriginalMetaPathInResultList) {
                    simpleMetaPaths.remove(p--);
                }
            }
        }
        return simpleMetaPaths;
    }

    /**
     * Es werden DoubleMeaningEdges mit der dem
     * <code>connectionState == null</code> in die einzelnen States
     * {@link ConnectionState#FORWARD} und {@link ConnectionState#BACKWARD}
     * zerlegt.
     *
     * @param simpleMetaPaths
     * @param currentPathStepIndex
     * @return Liste mit {@link SimpleMetaPath}, bei denen alle
     *         {@link DoubleMeaningEdge}s mit dem {@link ConnectionState}
     *         <code>null</code> jeweils durch 3 Pfade mit den 3 echten
     *         ConnectionStates ersetzt wurden.
     */
    private static List<SimpleMetaPath> replaceSimpleMetaPathsWithDoubleMeaningEdgesBothConnectionStates(final List<SimpleMetaPath> simpleMetaPaths, final int currentPathStepIndex) {
        //bei jedem MetaPfad der Liste
        for (int p = 0; p < simpleMetaPaths.size(); p++) {
            SimpleMetaPath simpleMetaPath = simpleMetaPaths.get(p);
            List<ElementaryMetaPath> elementaryMetaPaths = simpleMetaPath.getElementaryMetaPaths();
            //hole die Kantenklasse des aktuellen Pfadschrittes
            ElementaryMetaPath elementaryMetaPath = elementaryMetaPaths.get(currentPathStepIndex);
            Class<? extends Edge> edgeClass = elementaryMetaPath.getEdgeClass();

            //wenn die Kantenklasse des Pfadschrittes eine DoubleMeaningEdge ist
            if (CoreMetaModel.isDoubleMeaningEdge(edgeClass) && elementaryMetaPath.getConnectionState() == null) {
                //Der erste neue SimpleMetaPath, bei dem der aktuelle Elementarpfadschritt durch einen mit nicht-abstrakter Kantenklasse ersetzt wurde, muss in der Ergenisliste den Original-MetaPfad ersetzen.
                //All anderen danach werden dahinter eingefügt und der Index des aktuellen Elementarpfadschrittes erhöht.
                boolean replaceOriginalMetaPathInResultList = true;
                MetaModel metaModel = simpleMetaPath.getMetaModel();
                ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
                Class<? extends ModelElement> startClass = elementaryMetaPath.getStartClass();
                Class<? extends ModelElement> endClass = elementaryMetaPath.getEndClass();
                Direction direction = elementaryMetaPath.getDirection();
                Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass = edgeClass.asSubclass(DoubleMeaningEdge.class);
                ConnectionState[] connectionStates = { //nicht Double, weil sonst in den Tabellen die in beiden Richtungen verbundenen 3 mal (für jeden State 1 mal) auftauchen
                        ConnectionState.FORWARD, ConnectionState.BACKWARD
                };
                //für jeden ConnectionState
                for (ConnectionState connectionState : connectionStates) {
                    //Erzeuge ein neues Array aus Elementarpfaden, bei dem der aktuelle ConnectionState null immer durch einen richtigen ConnectionState ersetzt wird
                    ElementaryMetaPath[] elementaryMetaPathArray = new ElementaryMetaPath[elementaryMetaPaths.size()];
                    elementaryMetaPathArray = elementaryMetaPaths.toArray(elementaryMetaPathArray);
                    //jetzt den neuen Elementarpfadschritt mit den speziellen Start- und Endklasse in derselben Richtung, in der die nicht abstrakte Kantenklasse gilt, anlegen
                    elementaryMetaPathArray[currentPathStepIndex] = elementaryMetaPathHandler.getMetaPath(startClass, doubleMeaningEdgeClass, direction, connectionState, endClass);
                    //den neuen SimpleMetaPfad mit der nicht-abstrakten Kantenklasse analog zum original anlegen (also mit den Index der Kante, die den Namen festlegt übernehmen)
                    int metaPathStepWithPathName = simpleMetaPath.getMetaPathStepWithPathName();
                    SimpleMetaPath newSimpleMetaPath = new SimpleMetaPath(metaPathStepWithPathName, elementaryMetaPathArray);
                    //bei der ersten nicht-abstrakten Kantenklasse wird der neue MetaPfad in der Ergebnisliste einfach über den neuen geschrieben
                    if (replaceOriginalMetaPathInResultList) {
                        replaceOriginalMetaPathInResultList = false;
                        simpleMetaPaths.set(p, newSimpleMetaPath); //den originalen MetaPfad durch den ersten neuen ersetzen
                    } else {
                        simpleMetaPaths.add(++p, newSimpleMetaPath); //den neuen MetaPfad einfügen und Index des aktuellen MetaPfades in der Gesamtliste hochsetzen
                    }
                }
            }
        }
        return simpleMetaPaths;
    }

    /**
     * @param simpleMetaPaths
     * @return
     */
    public static Iterable<SimpleMetaPath> getSimpleMetaPathsNonAbstract(final Iterable<SimpleMetaPath> simpleMetaPaths) {
        Set<SimpleMetaPath> allSimpleMetaPathsNonAbstract = new HashSet<>();
        for (SimpleMetaPath simpleMetaPath : simpleMetaPaths) {
            Collection<SimpleMetaPath> simpleMetaPathsNonAbstract = getSimpleMetaPathsNonAbstract(simpleMetaPath);
            allSimpleMetaPathsNonAbstract.addAll(simpleMetaPathsNonAbstract);
        }
        return allSimpleMetaPathsNonAbstract;
    }

}
