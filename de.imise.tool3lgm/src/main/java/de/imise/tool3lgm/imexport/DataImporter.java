package de.imise.tool3lgm.imexport;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionOwner;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * Oberklasse für Importer, die in ein Modell importieren.
 *
 * @author AXS (7 Jun 2019)
 */
public abstract class DataImporter<T> implements GDCollectionOwner {

    /**
     * Mappt von einem eine Knotenklassen-Instanz repräsentierenden Objekt auf den im 3LGM2-Import-Modell erzeugten korrespondierenden Knoten. Über
     * diese Map kann beim Erstellen der Kanten aus dem SorceModell herausgefunden werden, welcher Source-Knoten zu welchem Target-Knoten geworden
     * ist. Diese Information benötigt man immer, wenn man einen Graphen mit Knoten und Kanten importieren möchte undabhängig von der Datenquelle.
     */
    private final Map<T, Node> sourceInstanceToNode = new HashMap<>();

    /**
     * Das zu füllende Import-Modell
     */
    protected final GDCollection gdcoll;

    public DataImporter() {
        gdcoll = createModel();
        gdcoll.setBulkMode(true);
    }

    /**
     * Importiert alle Daten aus einer Datenquelle in das übergebene Modell.
     *
     * @param gdcoll Zielmodell des Imports
     * @return <code>true</code>, wenn der Import erfolgreich war
     */
    protected abstract boolean importData();

    /**
     * Liefert die Klasse des Metamodells, das dem Modell (der {@link GDCollection}) zugrunde liegt, in die importiert wird.
     *
     * @return
     */
    public abstract Class<? extends ImportMetaModelDefinition> getImportMetaModelClass();

    /**
     * Initialisiert ein Modell ({@link GDCollection}) mit dem Metamodel, das durch die Klasse aus der Funktion {@link #getImportMetaModelClass()}
     * definiert wird.
     *
     * @return Leeres Modell mit ImportMetaModel
     */
    private final GDCollection createModel() {
        Class<? extends ImportMetaModelDefinition> importMetaModelClass = getImportMetaModelClass();
        MetaModelContext metaModelContext = new MetaModelContext(importMetaModelClass);
        GDCollection gdcoll = new GDCollection(metaModelContext);
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
    public Node getNode(final T sourceObject) {
        return sourceInstanceToNode.get(sourceObject);
    }

    /**
     * @return importierte Quell-Knoten
     */
    public Set<T> getSourceNodes() {
        return sourceInstanceToNode.keySet();
    }

    /**
     * @return importierte Ziel-Knoten
     */
    public Collection<Node> getTargetNodes() {
        return sourceInstanceToNode.values();
    }

    /**
     * @param sourceObject
     * @param nodeClass
     * @param name
     * @param description
     * @return
     */
    protected Node addNode(final T sourceObject, final Class<? extends Node> nodeClass, final String name, final String description) {
        return addNode(sourceObject, nodeClass, name, description, null);
    }

    /**
     * @param sourceObject
     * @param nodeClass
     * @param name
     * @param description
     * @param hashString
     * @return
     */
    protected Node addNode(final T sourceObject, final Class<? extends Node> nodeClass, final String name, final String description, final String hashString) {
        NodeContainer nodeContainer = gdcoll.createKnotenWithContainer(nodeClass, name, description, hashString, TransactionManager.STANDARD_PID);
        Node node = nodeContainer.getNode();
        sourceInstanceToNode.put(sourceObject, node);
        return node;
    }

    /**
     * @param edgeClassName
     * @param edgeHash
     * @param startNode
     * @param endNode
     * @return
     */
    public Edge addEdge(final String edgeClassName, final String edgeHash, final Node startNode, final Node endNode) {
        return addEdge(edgeClassName, null, edgeHash, startNode, endNode);
    }
    /**
     * @param edgeClassName
     * @param edgeName
     * @param edgeHash
     * @param startNode
     * @param endNode
     * @return
     */
    public Edge addEdge(final String edgeClassName, final String edgeName, final String edgeHash, final Node startNode, final Node endNode) {
        Edge edge = gdcoll.link(edgeClassName, edgeHash, startNode, endNode, -1, -1, false, TransactionManager.STANDARD_PID);
        if (!Strings.isNullOrEmpty(edgeName)) {
            edge.setName(edgeName);
        }
        return edge;
    }

}
