package de.imise.tool3lgm.graphtools.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InferenceEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.InstanciationEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SerialMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.path.metapaths.WrapperMetaPath;
import de.imise.util.Alphabetical;

/**
 * Definition von MetaPfaden.
 *
 * @author AXS
 * @create 13.10.2010
 */
public class MetaPathDefinition extends MetaModelSpecificAdapter {

    /** Sammlung aller definierten Metapfade */
    private final Set<MetaPath> definedMetaPaths = new HashSet<>();

    /** Der MetaPathCreator zum zugehörigen Metamodel */
    protected final SimpleMetaPathCreator simpleMetaPathCreator;

    /**
     * Legt eine neue Pfaddefinition an, in der nichts definiert ist.
     *
     * @param metaModel
     */
    @SuppressWarnings("unchecked")
    public MetaPathDefinition(final MetaModel metaModel) {
        this(metaModel, new Class[0]);
    }

    /**
     * Legt eine neue Pfaddefinition an. Werden dem Konstruktor Kantenklassen
     * übergeben, dann werden nur diese Kantenklassen als
     * {@link ElementaryMetaPath} zur Definition hinzugefügt. Ist das übergebene
     * Array leer,dann werden alle Kanten des Metamodells hinzugefügt.
     *
     * @param metaModel
     * @param edgeClasses
     */
    @SafeVarargs
    public MetaPathDefinition(final MetaModel metaModel, final Class<? extends Edge>... edgeClasses) {
        super(metaModel);
        simpleMetaPathCreator = new SimpleMetaPathCreator(metaModel);
        Iterable<Class<? extends Edge>> edgeClassesIt = edgeClasses == null || edgeClasses.length == 0 ? metaModel.allEdgesSet : Arrays.asList(edgeClasses);
        ElementaryMetaPathHandler elementaryMetaPathHandler = metaModel.getElementaryMetaPathHandler();
        //Alle Edgen in beiden Richtungen für alle direkten Startklassen und ihre Unterklassen als MetaPfade hinzufügen
        for (Class<? extends Edge> edgeClass : edgeClassesIt) {
            if (CoreMetaModel.isDoubleMeaningEdge(edgeClass)) {
                Class<? extends DoubleMeaningEdge> doubleMeaningEdgeClass = edgeClass.asSubclass(DoubleMeaningEdge.class);
                put(elementaryMetaPathHandler.getForwardMetaPath(doubleMeaningEdgeClass, ConnectionState.FORWARD));
                put(elementaryMetaPathHandler.getForwardMetaPath(doubleMeaningEdgeClass, ConnectionState.BACKWARD));
            } else {
                put(elementaryMetaPathHandler.getForwardMetaPath(edgeClass));
            }
        }
        init();
    }

    /**
     * Kann in Unterklassen zur Initialisierung überschrieben werden und wird im
     * Konstruktor aufgerufen
     */
    protected void init() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (definedMetaPaths == null ? 0 : definedMetaPaths.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MetaPathDefinition other = (MetaPathDefinition) obj;
        if (definedMetaPaths == null) {
            if (other.definedMetaPaths != null) {
                return false;
            }
        } else if (!definedMetaPaths.equals(other.definedMetaPaths)) {
            return false;
        }
        return true;
    }

    /**
     * Convenience method for
     * {@link SimpleMetaPathCreator#createSimpleMetaPath(Class, Class, Class...)}
     *
     * @param startClass
     * @param endClass
     * @param associations
     * @return
     * @see SimpleMetaPathCreator#createSimpleMetaPath(Class, Class, Class...)
     */
    @SafeVarargs
    public final SimpleMetaPath smp(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final Class<? extends Edge>... associations) {
        return simpleMetaPathCreator.createSimpleMetaPath(startClass, endClass, associations);
    }

    /**
     * Convenience method for
     * {@link SimpleMetaPathCreator#createSimpleMetaPath(Class, Class, String, Class...)}
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations
     * @return
     * @throws IllegalArgumentException
     * @see SimpleMetaPathCreator#createSimpleMetaPath(Class, Class, String,
     *      Class...)
     */
    @SafeVarargs
    public final SimpleMetaPath smp(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) throws IllegalArgumentException {
        return simpleMetaPathCreator.createSimpleMetaPath(startClass, endClass, baseResKeyOrName, associations);
    }

    /**
     * Liefert <code>Tool3lgmConstants.getResString(resKey)</code>. Dient nur
     * zur Verkürzung des Codes.
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
     * @return
     */
    public final Set<MetaPath> getMetaPaths(final Class<? extends ModelElement> startClass) {
        Set<MetaPath> metaPaths = new HashSet<>();
        for (MetaPath metaPath : definedMetaPaths) {
            if (metaPath.isStartClass(startClass)) {
                metaPaths.add(metaPath);
            }
        }
        return metaPaths;
    }

    /**
     * Liefert alle MetaPaths die für die übergebene Startklasse hin zur
     * Endklasse definiert sind.
     *
     * @param startClass
     * @param endClass
     * @param wrap Wenn <code>true</code>, dann werden bei den gefundenen
     *            MetaPfaden die Start- und Endklasse(n) durch die übergebene
     *            Start- und Endklasse ersetzt, wenn sie nicht bereits
     *            übereinstimmen
     * @return
     */
    public final Set<MetaPath> getMetaPaths(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final boolean wrap) {
        Set<MetaPath> metaPaths = new HashSet<>();
        for (MetaPath metaPath : definedMetaPaths) {
            if (metaPath.isStartAndEndClass(startClass, endClass)) {
                if (wrap) {
                    metaPath = WrapperMetaPath.wrapMetaPath(startClass, endClass, metaPath);
                }
                metaPaths.add(metaPath);
            }
        }
        return metaPaths;
    }

    /**
     * Legt den übergebenen Metapfad für alle Startklassen und alle Unterklassen
     * davon in die {@link #ELEMENT_CLASS_TO_START_PATHES}. Wenn der Metapfad
     * eine Gegenrichtung hat, wird diese auch gleich für hinzugefügt.
     *
     * @param metaPaths
     */
    protected final void put(final MetaPath... metaPaths) {
        if (metaPaths == null) {
            return;
        }
        for (MetaPath metaPath : metaPaths) {
            definedMetaPaths.add(metaPath);
            metaPath = metaPath.getOtherDirection();
            if (metaPath != null) {
                definedMetaPaths.add(metaPath);
            }
        }
    }

    /**
     * Erzeugt aus den übergebenen Assoziationen einen {@link SerialMetaPath}
     * zwischen der Start- und Endklasse, die übergeben wurden. Die Richtungen
     * werden aus diesen Start- und Endklassen abgeleitet. Wenn es nicht
     * eindeutig ist, ob die Startklasse die Kante vorwärts oder rückwärts
     * dreht, dann wird immer vorwärts angenommen. Dieser Metapfad wird in die
     * Definition mit aufgenommen.
     *
     * @param startClass
     * @param endClass
     * @param baseResKeyOrName
     * @param associations
     * @see #createSimpleMetaPath(Class, Class, String, Class...)
     */
    @SafeVarargs
    protected final SimpleMetaPath put(final Class<? extends ModelElement> startClass, final Class<? extends ModelElement> endClass, final String baseResKeyOrName, final Class<? extends Edge>... associations) {
        SimpleMetaPath simpleMetaPath = simpleMetaPathCreator.createSimpleMetaPath(startClass, endClass, baseResKeyOrName, associations);
        put(simpleMetaPath);
        return simpleMetaPath;
    }

    /**
     * Gibt die ModellElement-Klassen aus, für die Pfade definiert sind und
     * dahinter die Pfade. / public final void writePathes() { ArrayList<Class<?
     * extends ModelElement>> ccc = new
     * ArrayList<>(element_class_to_direct_start_pathes.keySet());
     * Alphabetical.sort(ccc); for (Class<? extends ModelElement> elementClass :
     * ccc) System.err.println(elementClass.getSimpleName() + ": " +
     * element_class_to_direct_start_pathes.get(elementClass)); }
     */
    public final void writePathes() {
        ArrayList<MetaPath> metaPaths = new ArrayList<>(definedMetaPaths);
        Alphabetical.sort(metaPaths);
        for (MetaPath metaPath : metaPaths) {
            System.err.println(metaPath);
        }
    }

    /**
     * Liefert alle Elementklassen, für die Pfade als Startklasse definiert
     * sind.
     *
     * @return
     */
    public final Set<Class<? extends ModelElement>> getStartElementClassesInPaths() {
        return getElementClassesInPaths(true);
    }

    /**
     * Liefert alle Elementklassen, für die Pfade als Startklasse definiert
     * sind.
     *
     * @return
     */
    public final Set<Class<? extends ModelElement>> getEndElementClassesInPaths() {
        return getElementClassesInPaths(false);
    }

    /**
     * Liefert alle Elementklassen, für die Pfade definiert sind.
     *
     * @param start wenn <code>true</code> werden alle Startklassen aller Pfade
     *            rausgesucht, bei <code>false</code> alle Endklassen
     * @return
     */
    private final Set<Class<? extends ModelElement>> getElementClassesInPaths(final boolean start) {
        Set<Class<? extends ModelElement>> pathsElementClassesSet = new HashSet<>();
        //alle Metapfade durchlaufen und alle neu gefundenen Startklassen zur Rückgabeliste hinzufügen
        MetaModel metaModel = getMetaModel();
        for (MetaPath metaPath : definedMetaPaths) {
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
            //für jede der neuen Startklasse aus allen Elementklassen alle Unter- oder Oberklassen bestimmen
            for (Class<? extends ModelElement> newElementClass : newElementClasses) {
                //ModelConstants.ALL_MODELELEMENT_CLASSES enthält alle Elementklassen (auch alle abstracten bis hin zu ModelElement.class)
                for (Class<? extends ModelElement> elementClass : metaModel.allModelElementClassesWithSuperClasses) {
                    //die Startklasse selbst ist schon in der Liste -> weiter
                    if (elementClass == newElementClass) {
                        continue;
                    }
                    //auch Unterklassen zurück geben, wenn die Startklasse eine Oberklasse der Elementklasse ist
                    if (newElementClass.isAssignableFrom(elementClass)) {
                        pathsElementClassesSet.add(elementClass);
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
     * @return Liefert eine Sammlung aller {@link SimpleMetaPath}, die man
     *         zwischen 2 Elementen anlegen kann, wobei die Zwischenelemente
     *         ebenfalls neu angelegt werden. Diese Pfade werden im Kontextmenü
     *         bei Mehrfachselektion oder Einfachselektion angeboten.
     */
    public Collection<SimpleMetaPath> getCreatableMetaPaths() {
        return ImmutableList.of();
    }

    /**
     * Mappt von einer Kantenklasse auf den MetaPfad, über den verbindbare
     * Elemente ebenfalls bereits verbunden sein müssen. Dieser Mechanismus ist
     * dafür gedacht, verbindbare Elemente einzuschränken auf bestimmte
     * Elemente.
     *
     * @return
     */
    public Map<Class<? extends Edge>, SimpleMetaPath> getConditionMetaPaths() {
        return ImmutableMap.of();
    }

    /**
     * Mappt von einer Kantenklasse auf den MetaPfad, über den verbindbare
     * Elemente ebenfalls bereits verbunden sein SOLLTEN, aber nicht müssen.
     * Dieser Mechanismus ist dafür gedacht, aus allen verbindbaren Elemente
     * diejenigen herauszusuchen, die besser als andere zum Verbinden geeignet
     * sind. Außerdem könnte man eine Warnung (aber eben keinen Fehler)
     * erzeugen, wenn die Kante zu einem Element besteht, das nicht über einen
     * hier beschriebenen Pfad verfügt.
     *
     * @return
     */
    public Map<Class<? extends Edge>, MetaPath> getSoftConditionMetaPaths() {
        return ImmutableMap.of();
    }

    /**
     * Sammlung aller Pfade, die ausgehend vom Startelement dieser Kante
     * ebenfalls angelegt werden sollen, wenn eine Instanziierung über diese
     * Kantenklasse durchgeführt wird. <br>
     * Jeder der Pfade muss zwingend bei derselben Klasse starten, bei der diese
     * Kante startet.<br>
     * Der Pfad hat nur einen Effekt, wenn seine Startklasse zur Startklasse
     * dieser Kante zuweisungskompatibel ist und er mind. eine
     * {@link InstanciationEdge} enthält. Der hiermit verbundene Mechanismus
     * geht durch die Kantenklassen des Pfades. Ist die aktuelle Kantenklasse
     * keine {@link InstanciationEdge}, dann suche von den aktuellen Elementen
     * ausgehend (am Anfang ist das das Startelement dieser Kante) alle damit
     * über diese Kantenart verbundenen Elemente und nimmt sie für den nächsten
     * Schritt als Startelemente. Sobald im Pfad eine {@link InstanciationEdge}
     * auftaucht, werden alle Elementarten und Kanten der dahinter liegenden
     * Pfadschritte kompeltt neu erzeugt und die entstehenden Elemente immer mit
     * den vorherigen verbunden. Wenn der Pfad mit einer Klasse endet (was er in
     * den meisten Fällen tun wird, damit das ganze sinnvoll ist), die
     * zuweisungskompatibel zur Endklasse dieser Kante ist (also zum durch diese
     * Kante neu erzeugten Element), dann wird die letzte Verbindung bzw. die
     * letzte Kante hin zum EndElementdieser Kante erzeugt und nicht nochmal ein
     * Element der Endelementart angelegt. Damit kann man "Nebenbedingungspfade"
     * für das Startelement gleich mit anlegen, wenn man das Startelement über
     * diese Kante hier instanziiert.
     *
     * @return
     */
    public Multimap<Class<? extends InstanciationEdge>, SimpleMetaPath> getInstanciationEdgeToAdditionalInstanciationMetaPaths() {
        return ImmutableListMultimap.of();
    }

    /**
     * Liefert eine Map, die für alle Elementklassen, bei denen der Name
     * verbundendener Elemente in der Grafik in Klammern unter der eigentlichen
     * Elementart angezeigt werden soll, den MetaPfad zu den anzuzeigenden
     * verbundenen Elementen liefert.
     *
     * @return
     */
    public Map<Class<? extends ModelElement>, MetaPath> getElementClassToNameExtensionMetaPath() {
        return ImmutableMap.of();
    }

    /**
     * Liefert eine Map, die von einer Kantenklasse auf den Pfad zu den
     * Elementen mappt, deren Name initial für eine neue Kante der übergebenen
     * Klasse übernommen werden soll. Sind es mehrere, werden sie durch Komma
     * getrennt. Ist es keines, bleibt der Standardname von
     * {@link GraphDocument#getNextNewName(Class)} erhalten. Damit kann man z.B.
     * einer neuen Ihe-Kommunikationsbeziehung statt 'IHE
     * Kommunikationsbeziehung 10' den Namen der über die beiden Schnittstellen
     * verbundenen Transaktion geben.<br>
     * Das funktioniert im Moment nur bei Kanten, da bei Knoten zum Zeitpunkt
     * des Festlegens des Namens der Knoten noch mit gar nichts verbunden ist.
     */
    public Map<Class<? extends Edge>, MetaPath> getEdgeClassToInitialCreatedNameSourceMetaPath() {
        return ImmutableMap.of();
    }

    /**
     * Maps from an {@link InferenceEdge} class to the MetaPath that must be
     * existing to create such an inference edge. The paths must be defined
     * between the same element types that the edge class connects. The
     * direction will be tested. That means the path can be defined from the
     * start element of the {@link InferenceEdge} to the end of this edge or in
     * the other direction.
     *
     * @return
     */
    public Map<Class<? extends InferenceEdge>, MetaPath> getInferenceEdgeToConditionMetaPath() {
        return ImmutableMap.of();
    }

}
