package de.imise.tool3lgm.imexport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecific;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionOwner;
import de.imise.tool3lgm.graphtools.model.GDCollectionPrinter;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Oberklasse für Importer, die in ein Modell importieren.
 *
 * @author AXS (7 Jun 2019)
 */
public abstract class DataImporter<T> implements GDCollectionOwner, MetaModelSpecific {

    /**
     * Mappt von einem eine Knotenklassen-Instanz repräsentierenden Objekt auf den im 3LGM2-Import-Modell erzeugten korrespondierenden Knoten. Über
     * diese Map kann beim Erstellen der Kanten aus dem SorceModell herausgefunden werden, welcher Source-Knoten zu welchem Target-Knoten geworden
     * ist. Diese Information benötigt man immer, wenn man einen Graphen mit Knoten und Kanten importieren möchte, undabhängig von der Datenquelle.
     */
    private final Map<T, Node> sourceInstanceToTargetNode = new HashMap<>();

    /**
     * Das zu füllende Import-Modell
     */
    protected GDCollection gdcoll;

    /**
     * Importiert alle Daten aus einer Datenquelle ins übergebene Modell.
     *
     * @param modelCategory
     * @return <code>true</code>, wenn der Import erfolgreich war
     */
    public final boolean startImport(final ModelCategory modelCategory) {
        Class<? extends MetaModelDefinition> importMetaModelDefinitionClass = getImportMetaModelDefinitionClass();
        gdcoll = createModel(importMetaModelDefinitionClass, modelCategory);
        boolean oldBulkMode = gdcoll.setBulkMode(true);
        boolean importData = importData();
        gdcoll.setBulkMode(oldBulkMode);
        return importData;
    }

    /**
     * Diese Funktion sollten Unterklassen überschreiben und den eigentlichen Import darin durchführen.
     *
     * @return <code>true</code>, wenn der Import geklappt hat, sonst <code>false</code>
     */
    protected abstract boolean importData();

    /**
     * @return Klasse des Metamodells, das dem Modell (der {@link GDCollection}) zugrunde liegt, in die importiert wird.
     */
    public abstract Class<? extends MetaModelDefinition> getImportMetaModelDefinitionClass();

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return getImportMetaModelDefinitionClass();
    }

    /**
     * Initialisiert ein Modell ({@link GDCollection}) mit dem übergebenen Metamodel. Diese Funktion könnte woanders hin, wo evtl auch der
     * Tool3lgmMetaModelContext abgefragt wird, damit das Metamodel nicht doppelt initialisiert wird.
     *
     * @param metaModelDefinitionClass
     *            Definitionsklasse des Metamodells des zu erzeugenden Modells
     * @param modelCategory
     * @return Leeres Modell mit ImportMetaModel
     */
    public static final GDCollection createModel(final Class<? extends MetaModelDefinition> metaModelDefinitionClass, final ModelCategory modelCategory) {
        MetaModelContext metaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForDefinitionClass(metaModelDefinitionClass);
        Tool3lgmModelType modelType = new Tool3lgmModelType(metaModelContext, modelCategory);
        GDCollection gdcoll = new GDCollection(modelType);
        return gdcoll;
    }

    /**
     * @return importiertes Modell
     */
    @Override
    public GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * Liefert für ein SourceObject den Node der dafür im ImportModel angelegt wurde.
     *
     * @param sourceObject
     * @return
     */
    public Node getTargetNode(final T sourceObject) {
        return sourceInstanceToTargetNode.get(sourceObject);
    }

    /**
     * @return importierte Quell-Knoten
     */
    public Set<T> getSourceNodes() {
        return sourceInstanceToTargetNode.keySet();
    }

    /**
     * @return importierte Ziel-Knoten
     */
    public Collection<Node> getTargetNodes() {
        return sourceInstanceToTargetNode.values();
    }

    /**
     * Fügt im Zielmodell einen Knoten hinzu.
     *
     * @param sourceObject
     *            ein Objetct aus der Source, über das man auf den neuen Knoten mappen kann. Es sollte der korrespondierende Knoten im SourceModell
     *            sein.
     * @param nodeClass
     *            3LGM2 Knotenklasse, die im Target-Modell angelegt werden soll
     * @param name
     *            Name des Knotens im Target-Modell
     * @param description
     *            Beschreibung des Knotens im Target-Modell
     * @return
     */
    protected Node addNode(final T sourceObject, final Class<? extends Node> nodeClass, final String name, final String description) {
        return addNode(sourceObject, nodeClass, name, description, null);
    }

    /**
     * Fügt im Zielmodell einen Knoten hinzu.
     *
     * @param sourceObject
     *            ein Objetct aus der Source, über das man auf den neuen Knoten mappen kann. Es sollte der korrespondierende Knoten im SourceModell
     *            sein.
     * @param nodeClass
     *            3LGM2 Knotenklasse, die im Target-Modell angelegt werden soll
     * @param name
     *            Name des Knotens im Target-Modell
     * @param description
     *            Beschreibung des Knotens im Target-Modell
     * @param hashString
     *            HashString des Knotens im Target-Modell
     * @return den erzeugten Knoten
     */
    protected Node addNode(final T sourceObject, final Class<? extends Node> nodeClass, final String name, final String description, final String hashString) {
        NodeContainer nodeContainer = gdcoll.createNodeAndContainer(nodeClass, name, description, hashString, TransactionManager.STANDARD_PID);
        Node node = nodeContainer.getNode();
        sourceInstanceToTargetNode.put(sourceObject, node);
        return node;
    }

    /**
     * Fügt im Zielmodell eine Kante hinzu.
     *
     * @param edgeClassName
     *            3LGM2 Kantenklasse, die im Target-Modell angelegt werden soll
     * @param name
     *            HashString der Kanten im Target-Modell
     * @param startNode
     *            Startknoten der Kante im Target-Modell
     * @param endNode
     *            Endknoten der Kante im Target-Modell
     * @return die erzeugte Kante
     */
    public Edge addEdge(final String edgeClassName, final String name, final Node startNode, final Node endNode) {
        return addEdge(edgeClassName, name, null, startNode, endNode);
    }
    /**
     * Fügt im Zielmodell eine Kante hinzu.
     *
     * @param edgeClassName
     *            3LGM2 Kantenklasse, die im Target-Modell angelegt werden soll
     * @param name
     *            Name des Knotens im Target-Modell
     * @param hashString
     *            HashString der Kante im Target-Modell
     * @param startNode
     *            Startknoten der Kante im Target-Modell
     * @param endNode
     *            Endknoten der Kante im Target-Modell
     * @return die erzeugte Kante
     */
    public Edge addEdge(final String edgeClassName, final String name, final String hashString, final Node startNode, final Node endNode) {
        Edge edge = gdcoll.link(edgeClassName, hashString, startNode, endNode, -1, -1, false, TransactionManager.STANDARD_PID);
        if (edge != null && !Strings.isNullOrEmpty(name)) {
            edge.setName(name);
        }
        return edge;
    }

    /**
     * Gibt den Inhalt des importierten Modells aus
     */
    public final void printModel() {
        GDCollectionPrinter.print(this);
    }

}
