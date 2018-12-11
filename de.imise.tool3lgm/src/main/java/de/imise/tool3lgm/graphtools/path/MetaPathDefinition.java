package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPathHandler.getForwardMetaPath;
import static de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator.createSimpleMetaPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.WrapperMetaPath;
import de.imise.util.Alphabetical;

/**
 * Definition von MetaPfaden.
 *
 * @author AXS
 * @create 13.10.2010
 */
public class MetaPathDefinition {

    /** Sammlung aller definierten Metapfade */
    private final Set<AbstractMetaPath> definedMetaPaths = new HashSet<>();

    /**
     * Legt eine neue Pfaddefinition an. Werden dem Konstruktor Kantenklassen übergeben, dann werden nur diese Kantenklassen als
     * {@link ElementaryMetaPath} zur Definition hinzugefügt. Ist das übergebene Array leer,dann werden alle Kanten des Metamodells hinzugefügt.
     *
     * @param edgeClasses
     */
    @SafeVarargs
    public MetaPathDefinition(final Class<? extends Edge>... edgeClasses) {
        Iterable<Class<? extends Edge>> edgeClassesIt = edgeClasses == null || edgeClasses.length == 0 ? ModelConstants.ALL_EDGES_SET : Arrays.asList(edgeClasses);
        //Alle Edgen in beiden Richtungen für alle direkten Startklassen und ihre Unterklassen als MetaPfade hinzufügen
        for (Class<? extends Edge> edgeClass : edgeClassesIt) {
            if (DoubleMeaningEdge.class.isAssignableFrom(edgeClass)) {
                Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass = edgeClass.asSubclass(DoubleMeaningEdge.class);
                put(getForwardMetaPath(doubleMeaningEdgeClass, ConnectionState.FORWARD));
                put(getForwardMetaPath(doubleMeaningEdgeClass, ConnectionState.BACKWARD));
            } else {
                put(getForwardMetaPath(edgeClass));
            }
        }
        init();
    }

    /**
     * Kann in Unterklassen zur Initialisierung überschrieben werden und wird im Konstruktor aufgerufen
     */
    protected void init() {
    }

    /**
     * Liefert alle MetaPaths die für die übergebene Klasse definiert sind.
     *
     * @param startClass
     * @param asSubClass
     * @param asSuperClass
     * @return
     */
    public final Set<AbstractMetaPath> getMetaPaths(final Class<? extends ModelElement> startClass, final boolean asSubClass, final boolean asSuperClass) {
        Set<AbstractMetaPath> metaPaths = new HashSet<>();
        for (AbstractMetaPath metaPath : definedMetaPaths) {
            if (AbstractMetaPath.isStartClass(metaPath, startClass, asSubClass, asSuperClass)) {
                metaPaths.add(metaPath);
            }
        }
        return metaPaths;
    }

    /**
     * Liefert alle MetaPaths die für die übergebene Startklasse hin zur Endklasse definiert sind.
     *
     * @param startClass
     * @param endClass
     * @param asSubClass
     *            Wenn <code>true</code> dürfen die übergebenen Elementklassen auch Unterklasse der Pfadklassen sein
     * @param asSuperClass
     *            Wenn <code>true</code> dürfen die übergebenen Elementklassen auch Oberklasse der Pfadklassen sein
     * @param wrap
     *            Wenn <code>true</code>, dann werden bei den gefundenen MetaPfaden die Start- und Endklasse(n) durch die übergebene Start- und
     *            Endklasse ersetzt, wenn sie nicht bereits übereinstimmen
     * @return
     */
    public final Set<AbstractMetaPath> getMetaPaths(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final boolean asSubClass, final boolean asSuperClass, final boolean wrap) {
        Set<AbstractMetaPath> metaPaths = new HashSet<>();
        for (AbstractMetaPath metaPath : definedMetaPaths) {
            if (AbstractMetaPath.isStartAndEndClass(metaPath, startClass, endClass, asSubClass, asSuperClass)) {
                if (wrap) {
                    metaPath = WrapperMetaPath.wrapMetaPath(startClass, endClass, metaPath);
                }
                metaPaths.add(metaPath);
            }
        }
        return metaPaths;
    }

    /**
     * Legt den übergebenen Metapfad für alle Startklassen und alle Unterklassen davon in die {@link #ELEMENT_CLASS_TO_START_PATHES}.
     * Wenn der Metapfad eine Gegenrichtung hat, wird diese auch gleich für hinzugefügt.
     *
     * @param metaPaths
     */
    protected final void put(final AbstractMetaPath... metaPaths) {
        if (metaPaths == null) {
            return;
        }
        for (AbstractMetaPath metaPath : metaPaths) {
            definedMetaPaths.add(metaPath);
            metaPath = metaPath.getOtherDirection();
            if (metaPath != null) {
                definedMetaPaths.add(metaPath);
            }
        }
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SequenceMetaPath} zwischen der Start- und Endklasse, die übergeben wurden. Die
     * Richtungen werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht eindeutig ist, ob die Startklasse die Kante vorwärts oder
     * rückwärts dreht, dann wird immer vorwärts angenommen.
     * Dieser Metapfad wird in die Definition mit aufgenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations
     * @see #createSimpleMetaPath(Class, Class, String, Class...)
     */
    @SafeVarargs
    protected final SimpleMetaPath put(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) {
        SimpleMetaPath simpleMetaPath = createSimpleMetaPath(startClass, endClass, baseResKeyOrName, associations);
        put(simpleMetaPath);
        return simpleMetaPath;
    }

    /**
     * Gibt die ModellElement-Klassen aus, für die Pfade definiert sind und dahinter die Pfade.
     * /
     * public final void writePathes() {
     * ArrayList<Class<? extends ModelElement>> ccc = new ArrayList<Class<? extends ModelElement>>(element_class_to_direct_start_pathes.keySet());
     * Alphabetical.sort(ccc);
     * for (Class<? extends ModelElement> elementClass : ccc)
     * System.err.println(elementClass.getSimpleName() + ": " + element_class_to_direct_start_pathes.get(elementClass));
     * }
     */
    public final void writePathes() {
        ArrayList<AbstractMetaPath> metaPaths = new ArrayList<>(definedMetaPaths);
        Alphabetical.sort(metaPaths);
        for (AbstractMetaPath metaPath : metaPaths) {
            System.err.println(metaPath);
        }
    }

    /**
     * Liefert alle Elementklassen, für die Pfade als Startklasse definiert sind.
     *
     * @param subClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Unterklassen einer Startklasse eines Pfades sind.
     * @param superClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Oberklassen einer Startklasse eines Pfades sind.
     * @return
     */
    public final Set<Class<? extends ModelElement>> getStartElementClassesInPaths(final boolean subClasses, final boolean superClasses) {
        return getElementClassesInPaths(true, subClasses, superClasses);
    }

    /**
     * Liefert alle Elementklassen, für die Pfade als Startklasse definiert sind.
     *
     * @param subClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Unterklassen einer Startklasse eines Pfades sind.
     * @param superClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Oberklassen einer Startklasse eines Pfades sind.
     * @return
     */
    public final Set<Class<? extends ModelElement>> getEndElementClassesInPaths(final boolean subClasses, final boolean superClasses) {
        return getElementClassesInPaths(false, subClasses, superClasses);
    }

    /**
     * Liefert alle Elementklassen, für die Pfade definiert sind.
     *
     * @param start wenn <code>true</code> werden alle Startklassen aller Pfade rausgesucht, bei <code>false</code> alle Endklassen
     * @param subClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Unterklassen einer Startklasse eines Pfades sind.
     * @param superClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Oberklassen einer Startklasse eines Pfades sind.
     * @return
     */
    private final Set<Class<? extends ModelElement>> getElementClassesInPaths(final boolean start, final boolean subClasses, final boolean superClasses) {
        Set<Class<? extends ModelElement>> pathsElementClassesSet = new HashSet<>();
        //alle Metapfade durchlaufen und alle neu gefundenen Startklassen zur Rückgabeliste hinzufügen
        for (AbstractMetaPath metaPath : definedMetaPaths) {
            ArrayList<Class<? extends ModelElement>> newElementClasses = new ArrayList<>();
            //Für alle Startklassen des aktuellen Metapfades
            for (Class<? extends ModelElement> elementClass : start ? metaPath.getStartClasses() : metaPath.getEndClasses()) {
                //wenn diese Klasse noch neu ist -> merken
                if (!pathsElementClassesSet.contains(elementClass)) {
                    newElementClasses.add(elementClass);
                }
            }
            //wenn der aktuelle MetaPfad keine bisher nicht bekannte Startklasse hatte -> nächster Metapfad
            if (newElementClasses.size() == 0) {
                continue;
            }
            //alle neuen Startklassen in der Gesamtliste speichern
            pathsElementClassesSet.addAll(newElementClasses);
            //wenn auch die Unter- oder Oberklassen der Startklassen zurück gegeben werden sollen
            if (subClasses || superClasses) {
                //für jede der neuen Startklasse aus allen Elementklassen alle Unter- oder Oberklassen bestimmen
                for (Class<? extends ModelElement> newElementClass : newElementClasses) {
                    //ModelConstants.ALL_MODELELEMENT_CLASSES enthält alle Elementklassen (auch alle abstracten bis hin zu ModelElement.class)
                    for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_MODELELEMENT_CLASSES_WITH_SUPER_CLASSES) {
                        //die Startklasse selbst ist schon in der Liste -> weiter
                        if (elementClass == newElementClass) {
                            continue;
                        }
                        //wenn Unterklassen auch zurück gegeben werden sollen und die Startklasse eine Oberklasse der Elementklasse ist
                        if (subClasses && newElementClass.isAssignableFrom(elementClass)) {
                            pathsElementClassesSet.add(elementClass);
                        }
                        //wenn Oberklassen auch zurück gegeben werden sollen und die Startklasse eine Unterklasse der Elementklasse ist
                        if (superClasses && elementClass.isAssignableFrom(newElementClass)) {
                            pathsElementClassesSet.add(elementClass);
                        }
                    }
                }
            }
        }
        return pathsElementClassesSet;
    }

    /**
     * Liefert <code>Tool3lgmConstants.getResString(resKey)</code>. Dient nur zur Verkürzung des Codes.
     *
     * @param resKey
     * @return
     */
    public static final String _s(final String resKey) {
        return Tool3lgmConstants.getResString(resKey);
    }

}
