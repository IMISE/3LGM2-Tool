package de.imise.tool3lgm.graphtools.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge.Direction;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.util.Alphabetical;

/**
 * Definition von MetaPfaden.
 *
 * @author AXS
 * @create 13.10.2010
 */
public class MetaPathDefinition {

    /** Platzhaltermetapfad für die Definition einer beliebigen Verbindung z. B. in einem {@link SequenceMetaPath} */
    public static final ElementaryMetaPath GENERAL_ELEMENTARY_SUPER_PATH = new ElementaryMetaPath(Edge.class, Direction.FORWARD);

    /**
     * Mappt von einer Edgenklasse auf ein 2-elementiges Array von MetaPathes, wobei der erste MetaPath
     * im Array der zur Edge gehörige Metapath in Richtung Edge.Direction.FORWARD und der zweite in
     * Richtung Edge.Direction.BACKWARD ist.
     */
    private static final HashMap<Class<? extends Edge>, ElementaryMetaPath[]> edge_class_to_forward_and_backward_metapathes = new HashMap<>();

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
            put(getForwardMetaPath(edgeClass));
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
     * @return
     */
    public final Set<AbstractMetaPath> getMetaPaths(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final boolean asSubClass, final boolean asSuperClass) {
        Set<AbstractMetaPath> metaPaths = new HashSet<>();
        for (AbstractMetaPath metaPath : definedMetaPaths) {
            if (AbstractMetaPath.isStartAndEndClass(metaPath, startClass, endClass, asSubClass, asSuperClass)) {
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
    protected final SimpleMetaPath put(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) {
        SimpleMetaPath simpleMetaPath = createSimpleMetaPath(startClass, endClass, baseResKeyOrName, associations);
        put(simpleMetaPath);
        return simpleMetaPath;
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
    public final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        return createSimpleMetaPath(startClass, endClass, null, associations);
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
    public final SimpleMetaPath createSimpleMetaPath(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) {
        return SimpleMetaPath.create(startClass, endClass, baseResKeyOrName, this, associations);
    }

    /**
     * Liefert für eine Edge den dazugehörigen ElementarMetaPfad. Wenn der Rückgabepfad noch nicht in der Map für die
     * Vorwärts- und Rückwärtsrichtung der Elementarpfade enthalten ist, dann wird er hinzugefügt.
     *
     * @param edgeClass
     * @param direction
     * @return
     */
    public final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction) {
        return getMetaPath(edgeClass, direction, null);
    }

    public final ElementaryMetaPath getMetaPath(final Class<? extends Edge> edgeClass, final Direction direction, final ConnectionState connectionState) {
        ElementaryMetaPath[] metaPathes = edge_class_to_forward_and_backward_metapathes.get(edgeClass);
        if (metaPathes == null) {
            boolean isDoubleMeaningEdge = ModelConstants.isDoubleMeaningEdge(edgeClass);
            //Kanten mit doppelter Bedeutung haben für jeden ConnectionState (null, FORWARD, BACKWARD, DOUBLE) und jede Richtung (FORWARD,
            //BACKWARD) je einen Elementarmetapfad mit eigener Bedeutung. Alle anderen haben nur für jede Richtung eine Bedeutung.
            //Index des Elementarpfades ergibt sich aus Direction und ConnectionState:
            //cs = (connectionState == null ? 0 : connectionState.ordinal() + 1)
            //dir = direction.ordinal();
            //Index = dir + 2 * cs
            metaPathes = new ElementaryMetaPath[isDoubleMeaningEdge ? 8 : 2];
            metaPathes[0] = new ElementaryMetaPath(edgeClass, Direction.FORWARD); //0 = Index des Pfades = Direction.FORWARD.ordinal(). Das hier entspricht bei DoubleMeaningEdges dem ConnectionState.null
            metaPathes[1] = new ElementaryMetaPath(edgeClass, Direction.BACKWARD); //1 = Index des Pfades = Direction.BACKWARD.ordinal(). Das hier entspricht bei DoubleMeaningEdges dem ConnectionState.null
            if (isDoubleMeaningEdge) {
                metaPathes[2] = new ElementaryMetaPath(edgeClass, Direction.FORWARD, ConnectionState.FORWARD);
                metaPathes[3] = new ElementaryMetaPath(edgeClass, Direction.BACKWARD, ConnectionState.FORWARD);
                metaPathes[4] = new ElementaryMetaPath(edgeClass, Direction.FORWARD, ConnectionState.BACKWARD);
                metaPathes[5] = new ElementaryMetaPath(edgeClass, Direction.BACKWARD, ConnectionState.BACKWARD);
                metaPathes[6] = new ElementaryMetaPath(edgeClass, Direction.FORWARD, ConnectionState.DOUBLE);
                metaPathes[7] = new ElementaryMetaPath(edgeClass, Direction.BACKWARD, ConnectionState.DOUBLE);
            }
            edge_class_to_forward_and_backward_metapathes.put(edgeClass, metaPathes);
        }
        int metaPathIndex = direction.ordinal() + 2 * (connectionState == null ? 0 : connectionState.ordinal() + 1);
        return metaPathes[metaPathIndex];
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getForwardMetaPath(final Class<? extends Edge> edgeClass) {
        return getMetaPath(edgeClass, Direction.FORWARD);
    }

    /**
     * @param edgeClass
     * @return
     */
    public final ElementaryMetaPath getBackwardMetaPath(final Class<? extends Edge> edgeClass) {
        return getMetaPath(edgeClass, Direction.BACKWARD);
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
     * Liefert alle Elementklassen, für die Pfade definiert sind.
     *
     * @param subClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Unterklassen einer Startklasse eines Pfades sind.
     * @param superClasses
     *            Wenn <code>true</code> werden auch alle Modelelementklassen zurück gegeben, die Oberklassen einer Startklasse eines Pfades sind.
     * @return
     */
    public final Set<Class<? extends ModelElement>> getStartElementClassesWithPaths(final boolean subClasses, final boolean superClasses) {
        Set<Class<? extends ModelElement>> pathsStartClassesSet = new HashSet<>();
        //alle Metapfade durchlaufen und alle neu gefundenen Startklassen zur Rückgabeliste hinzufügen
        for (AbstractMetaPath metaPath : definedMetaPaths) {
            ArrayList<Class<? extends ModelElement>> newStartClasses = new ArrayList<>();
            //Für alle Startklassen des aktuellen Metapfades
            for (Class<? extends ModelElement> startClass : metaPath.getStartClasses()) {
                //wenn diese Klasse noch neu ist -> merken
                if (!pathsStartClassesSet.contains(startClass)) {
                    newStartClasses.add(startClass);
                }
            }
            //wenn der aktuelle MetaPfad keine bisher nicht bekannte Startklasse hatte -> nächster Metapfad
            if (newStartClasses.size() == 0) {
                continue;
            }
            //alle neuen Startklassen in der Gesamtliste speichern
            pathsStartClassesSet.addAll(newStartClasses);
            //wenn auch die Unter- oder Oberklassen der Startklassen zurück gegeben werden sollen
            if (subClasses || superClasses) {
                //für jede der neuen Startklasse aus allen Elementklassen alle Unter- oder Oberklassen bestimmen
                for (Class<? extends ModelElement> startClass : newStartClasses) {
                    //ModelConstants.ALL_MODELELEMENT_CLASSES enthält alle Elementklassen (auch alle abstracten bis hin zu ModelElement.class)
                    for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_MODELELEMENT_CLASSES_WITH_SUPER_CLASSES) {
                        //die Startklasse selbst ist schon in der Liste -> weiter
                        if (elementClass == startClass) {
                            continue;
                        }
                        //wenn Unterklassen auch zurück gegeben werden sollen und die Startklasse eine Oberklasse der Elementklasse ist
                        if (subClasses && startClass.isAssignableFrom(elementClass)) {
                            pathsStartClassesSet.add(elementClass);
                        }
                        //wenn Oberklassen auch zurück gegeben werden sollen und die Startklasse eine Unterklasse der Elementklasse ist
                        if (superClasses && elementClass.isAssignableFrom(startClass)) {
                            pathsStartClassesSet.add(elementClass);
                        }
                    }
                }
            }
        }
        return pathsStartClassesSet;
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

    public ElementaryMetaPath _eF(final Class<? extends Edge> edgeClass) {
        return getMetaPath(edgeClass, Direction.FORWARD);
    }

    public ElementaryMetaPath _eB(final Class<? extends Edge> edgeClass) {
        return getMetaPath(edgeClass, Direction.BACKWARD);
    }

    public ElementaryMetaPath _eFF(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, ConnectionState.FORWARD);
    }

    public ElementaryMetaPath _eBF(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.BACKWARD, ConnectionState.FORWARD);
    }

    public ElementaryMetaPath _eFB(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, ConnectionState.BACKWARD);
    }

    public ElementaryMetaPath _eBB(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.BACKWARD, ConnectionState.BACKWARD);
    }

    public ElementaryMetaPath _eFD(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.FORWARD, ConnectionState.DOUBLE);
    }

    public ElementaryMetaPath _eBD(final Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass) {
        return getMetaPath(doubleMeaningEdgeClass, Direction.BACKWARD, ConnectionState.DOUBLE);
    }

    public SequenceMetaPath _s(final String baseResKeyOrName, final AbstractMetaPath... metaPaths) {
        return new SequenceMetaPath(baseResKeyOrName, metaPaths);
    }

    //    public static  SequenceMetaPath s(final String baseResKeyOrName, Class<? >) {
    //        return new SequenceMetaPath(baseResKeyOrName, metaPaths);
    //    }

}
