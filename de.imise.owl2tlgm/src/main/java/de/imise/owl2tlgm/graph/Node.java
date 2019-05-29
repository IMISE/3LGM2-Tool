package de.imise.owl2tlgm.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dietmar (28 May 2019)
 */
public class Node {

    /**
     * Eine Liste der Kanten, bei denen dieser Knoten der domainNode ist.
     */
    private final List<Edge> domainEdges = new ArrayList<>();

    /**
     * Eine Liste der Kanten, bei denen dieser Knoten der rangeNode ist.
     */
    private final List<Edge> rangeEdges = new ArrayList<>();

    /**
     * Der Name des Knoten.
     */
    private final String name;

    /**
     * Erzeugt eine neue Instanz.
     *
     * @param name
     */
    public Node(final String name) {
        this.name = name;
    }

    /**
     * Nimmt die übergebene Kante in die Liste der domain Kanten auf.
     *
     * @param edge
     */
    public void addDomainEdge(final Edge edge) {
        domainEdges.add(edge);
    }

    /**
     * Nimmt die übergebene Kante in die Liste der range Kanten auf.
     *
     * @param edge
     */
    public void addRangeEdge(final Edge edge) {
        rangeEdges.add(edge);
    }

    /**
     * Liefert den Namen des Knoten.
     */
    public String getName() {
        return name;
    }
}
