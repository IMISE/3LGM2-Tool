package de.imise.owl2tlgm.graph;

/**
 * @author Dietmar (28 May 2019)
 */
public class Edge {

    /**
     * Der Knoten mit der Klasse domain class oder <code>null</code>, wenn dieser nicht vorhanden ist.
     */
    private final Node domainNode;

    /**
     * Der Knoten mit der Klasse range class oder <code>null</code>, wenn dieser nicht vorhanden ist.
     */
    private final Node rangeNode;

    /**
     * Der Name der Kante.
     */
    private final String name;

    /**
     * Erzeugt eine neue Instanz.
     *
     * @param name
     * @param domainNode
     * @param rangeNode
     */
    public Edge(final String name, final Node domainNode, final Node rangeNode) {
        this.domainNode = domainNode;
        this.rangeNode = rangeNode;
        this.name = name;
    }

    /**
     * Liefert den Namen der Kante.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Liefert den Domain Knoten.
     */
    public Node getDomainNode() {
        return domainNode;
    }

    /**
     * Liefert den Range Knoten.
     */
    public Node getRangeNode() {
        return rangeNode;
    }
}
