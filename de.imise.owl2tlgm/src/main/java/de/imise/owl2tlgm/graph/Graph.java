package de.imise.owl2tlgm.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.ext.com.google.common.base.Strings;

/**
 * @author Dietmar (28 May 2019)
 */
public class Graph {

    /** Wenn <code>true</code> wird Info ausgegeben, ob eine Kante hinzugefügt wurde oder nicht */
    private static final boolean DEBUG = true;

    /** Collection aller Kanten im Graph */
    private final Collection<Edge> edges = new ArrayList<>();

    /** Mappt vom Namen eines Knotens auf den Knoten */
    private final Map<String, Node> nodeName2Node = new HashMap<>();

    /**
     * @param nodeName
     * @return
     */
    public boolean containsNode(final String nodeName) {
        return nodeName2Node.containsKey(nodeName);
    }

    /**
     * @param nodeName
     * @return
     */
    public Node getNode(final String nodeName) {
        return nodeName2Node.get(nodeName);
    }

    /**
     * @param node
     * @return
     */
    private String addNode(final Node node) {
        if (node != null) {
            String nodeName = node.getName();
            if (!containsNode(nodeName)) { // ist der Name wirklich eine ID des Knotens?
                nodeName2Node.put(nodeName, node);
            }
            return nodeName;
        }
        return "";
    }

    /**
     * @param edge
     */
    public void addEdge(final Edge edge) {
        String domainNodeName = addNode(edge.getDomainNode());
        String rangeNodeName = addNode(edge.getDomainNode());
        String edgeName = edge.getName();
        if (isValid(domainNodeName, edgeName, rangeNodeName)) {
            edges.add(edge);
        }
    }

    /**
     * @param domainNodeName
     * @param edgeName
     * @param rangeNodeName
     * @return
     */
    private boolean isValid(final String domainNodeName, final String edgeName, final String rangeNodeName) {
        boolean validEdge = !Strings.isNullOrEmpty(domainNodeName) && !Strings.isNullOrEmpty(rangeNodeName);
        if (DEBUG) {
            String not = validEdge ? "" : "not";
            System.out.println("Kante " + not + "aufgenommen: " + domainNodeName + " " + edgeName + " " + rangeNodeName);
        }
        return validEdge;
    }

}
