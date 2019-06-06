package de.imise.tool3lgm.graphtools.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.meta.SequenceMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.path.meta.WrapperMetaPath;
import de.imise.util.Alphabetical;

/**
 * Definition von MetaPfaden.
 *
 * @author AXS
 * @create 13.10.2010
 */
public class MetaPathDefinition {

    /** Das MetaModel der für das diese Definition gilt */
    protected final MetaModel metaModel;

    /** Sammlung aller definierten Metapfade */
    private final Set<AbstractMetaPath> definedMetaPaths = new HashSet<>();

    /** Der MetaPathCreator zum zugehörigen Metamodel */
    protected final SimpleMetaPathCreator simpleMetaPathCreator;

    /**
     * Legt eine neue Pfaddefinition an. Werden dem Konstruktor Kantenklassen übergeben, dann werden nur diese Kantenklassen als
     * {@link ElementaryMetaPath} zur Definition hinzugefügt. Ist das übergebene Array leer,dann werden alle Kanten des Metamodells hinzugefügt.
     *
     * @param metaModel
     * @param edgeClasses
     */
    @SafeVarargs
    public MetaPathDefinition(final MetaModel metaModel, final Class<? extends Edge>... edgeClasses) {
        this.metaModel = metaModel;
        simpleMetaPathCreator = new SimpleMetaPathCreator(metaModel);
        Iterable<Class<? extends Edge>> edgeClassesIt = edgeClasses == null || edgeClasses.length == 0 ? metaModel.allEdgesSet : Arrays.asList(edgeClasses);
        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
        //Alle Edgen in beiden Richtungen für alle direkten Startklassen und ihre Unterklassen als MetaPfade hinzufügen
        for (Class<? extends Edge> edgeClass : edgeClassesIt) {
            if (DoubleMeaningEdge.class.isAssignableFrom(edgeClass)) {
                Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass = edgeClass.asSubclass(DoubleMeaningEdge.class);
                put(elementaryMetaPathHandler.getForwardMetaPath(doubleMeaningEdgeClass, ConnectionState.FORWARD));
                put(elementaryMetaPathHandler.getForwardMetaPath(doubleMeaningEdgeClass, ConnectionState.BACKWARD));
            } else {
                put(elementaryMetaPathHandler.getForwardMetaPath(edgeClass));
            }
        }
        init();
        initCreatableMetaPaths();
    }

    /**
     * Kann in Unterklassen zur Initialisierung überschrieben werden und wird im Konstruktor aufgerufen
     */
    protected void init() {
    }

    /**
     * @return
     */
    public final MetaModel getMetaModel() {
        return metaModel;
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
        SimpleMetaPath simpleMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(metaModel, startClass, endClass, baseResKeyOrName, associations);
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
                    for (Class<? extends ModelElement> elementClass : metaModel.allModelElementClassesWithSuperClasses) {
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

    //////////////////////////////
    // weitere PfadDefinitionen //
    //////////////////////////////

    /**
     * @return Liefert eine Sammlung aller {@link SimpleMetaPath}, die man zwischen 2 Elementen anlegen kann, wobei die Zwischenelemente ebenfalls neu
     *         angelegt werden. Diese Pfade werden im Kontextmenü bei Mehrfachselektion oder Einfachselektion angeboten.
     */
    public Collection<SimpleMetaPath> getCreatablePaths() {
        return ImmutableList.of();
    }

    private final Multimap<Class<? extends ModelElement>, SimpleMetaPath> elementClassToCreatableMetaPaths = ArrayListMultimap.create();

    private final void initCreatableMetaPaths() {
        Collection<SimpleMetaPath> creatablePaths = getCreatablePaths();
        if (creatablePaths != null) {
            for (SimpleMetaPath metaPath : creatablePaths) {
                elementClassToCreatableMetaPaths.put(metaPath.getStartClass(), metaPath);
                elementClassToCreatableMetaPaths.put(metaPath.getEndClass(), metaPath.getOtherDirection());
            }
        }
    }

    /**
     * Liefert alle anlegbaren MetaPfade die für die übergebene Elementart im Metamodell definiert sind.
     *
     * @param elementClass
     */
    public Collection<SimpleMetaPath> getCreatableMetaPaths(final Class<? extends ModelElement> elementClass) {
        Collection<SimpleMetaPath> creatablePaths = elementClassToCreatableMetaPaths.get(elementClass);
        return creatablePaths == null ? ImmutableList.of() : creatablePaths;
    }

    /**
     * Liefert für die übergebene Kantenklasse den MetaPfad, über den die verbindbaren Elemente ebenfalls bereits verbunden sein müssen.
     * Dieser Mechanismus ist dafür gedacht, verbindbare Elemente einzuschränken auf bestimmte Elemente.
     *
     * @return
     */
    public Map<Class<? extends Edge>, SimpleMetaPath> getConditionPaths() {
        return ImmutableMap.of();
    }

    /**
     * Sammlung aller Pfade, die ausgehend vom Startelement dieser Kante ebenfalls angelegt werden sollen, wenn eine Instanziierung über diese
     * Kantenklasse durchgeführt wird. <br>
     * Jeder der Pfade muss zwingend bei derselben Klasse starten, bei der diese Kante startet.<br>
     * Der Pfad hat nur einen Effekt, wenn seine Startklasse zur Startklasse dieser Kante zuweisungskompatibel ist und er mind. eine
     * {@link InstanciationEdge} enthält. Der hiermit verbundene Mechanismus geht durch die Kantenklassen des Pfades. Ist die aktuelle
     * Kantenklasse keine {@link InstanciationEdge}, dann suche von den aktuellen Elementen ausgehend (am Anfang ist das das Startelement dieser
     * Kante) alle damit über diese Kantenart verbundenen Elemente und nimmt sie für den nächsten Schritt als Startelemente. Sobald im Pfad eine
     * {@link InstanciationEdge} auftaucht, werden alle Elementarten und Kanten der dahinter liegenden Pfadschritte kompeltt neu erzeugt und die
     * entstehenden Elemente immer mit den vorherigen verbunden. Wenn der Pfad mit einer Klasse endet (was er in den meisten Fällen tun wird, damit
     * das ganze sinnvoll ist), die zuweisungskompatibel zur Endklasse dieser Kante ist (also zum durch diese Kante neu erzeugten Element), dann wird
     * die letzte Verbindung bzw. die letzte Kante hin zum EndElementdieser Kante erzeugt und nicht nochmal ein Element der Endelementart angelegt.
     * Damit kann man "Nebenbedingungspfade" für das Startelement gleich mit anlegen, wenn man das Startelement über diese Kante hier intsanziiert.
     *
     * @param instanciationEdgeClass
     * @return
     */
    public Multimap<Class<? extends InstanciationEdge>, SimpleMetaPath> getInstanciationEdgeToAdditionalInstanciationMetaPaths() {
        return ImmutableListMultimap.of();
    }

    /**
     * Liefert eine Map, die für alle Elementklassen, bei denen der Name verbundendener Elemente in der Grafik in Klammern unter der eigentlichen
     * Elementart angezeigt werden soll, den MetaPfad zu den anzuzeigenden verbundenen Elementen liefert.
     *
     * @return
     */
    public Map<Class<? extends ModelElement>, AbstractMetaPath> getElementClassToNameExtensionPath() {
        return ImmutableMap.of();
    }

}
