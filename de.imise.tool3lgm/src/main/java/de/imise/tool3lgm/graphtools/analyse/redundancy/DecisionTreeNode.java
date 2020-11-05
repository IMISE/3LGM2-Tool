package de.imise.tool3lgm.graphtools.analyse.redundancy;

/**
 * Node eines Entscheidungsbaumes.<br>
 * Man kann in beide Richtungen navigieren, d.h. ein Node kennt seinen
 * Vater-Node und seine beiden Kindknoten (wenn vorhanden). Hat ein Node nur
 * einen Kindknoten, so ist dieser immer der erste Kindknoten
 * (<code>firstNode</code>). Entfernt man von einem Node mit 2 Kindknoten den
 * ersten Node, so wird der vormals zweite Kindknoten dann der erste.<br>
 * Es können maximal 2 Kindknoten hinzugefügt werden.<br>
 * Jeder Node hat einen <code>boolean</code>-Status. Im Kontext eines
 * Entscheidungsbaumes gibt der Status an, ob ein Element, das der Node
 * repräsentiert, dazugehört oder nicht.
 *
 * @author AXS
 */
public class DecisionTreeNode {

    /**
     * Der Status des Knotens
     */
    private final boolean needed;

    /**
     * Vaterknoten des Knotens
     */
    private DecisionTreeNode parent;

    /**
     * Erster Kindknoten
     */
    private DecisionTreeNode firstNode = null;

    /**
     * Zweiter Kindknoten. Ist nur ungleich <code>null</code>, wenn es einen
     * ersten Kindknoten gibt.
     */
    private DecisionTreeNode secondNode = null;

    /**
     * @param needed
     * @param parent
     */
    protected DecisionTreeNode(final boolean needed, final DecisionTreeNode parent) {
        super();
        this.needed = needed;
        this.parent = parent;
    }

    /**
     * @return
     */
    public static DecisionTreeNode createDecisionTreeRoot() {
        return new DecisionTreeNode(false, null);
    }

    /**
     * @param needed
     * @return
     */
    public DecisionTreeNode addChild(final boolean needed) {
        DecisionTreeNode node = null;
        if (secondNode == null) {
            node = new DecisionTreeNode(needed, this);
            if (firstNode == null) {
                firstNode = node;
            } else {
                secondNode = node;
            }
        }
        return node;
    }

    /**
     * @param needed
     * @param value
     * @return
     */
    public DecisionTreeNode addLeaf(final boolean needed, final int value) {
        DecisionTreeNode node = null;
        if (secondNode == null) {
            node = new DecisionTreeLeaf(needed, this, value);
            if (firstNode == null) {
                firstNode = node;
            } else {
                secondNode = node;
            }
        }
        return node;
    }

    boolean deleted = false;

    /**
     * Entfernt den übergebenen Node von diesem Node, wenn er der Vater davon
     * ist. Falls der zu löschende Node der erste Kindknoten ist und es noch
     * einen zweiten Knidknoten gibt, ist der ehemals zweite nach dem Löschen
     * der erste.
     *
     * @param node
     */
    public void removeChild(final DecisionTreeNode node) {
        if (node == null) {
            return;
        }
        if (node == firstNode) {
            firstNode.deleted = true;
            firstNode.parent = null;
            firstNode = secondNode;
            secondNode = null;
        } else if (node == secondNode) {
            secondNode.deleted = true;
            secondNode.parent = null;
            secondNode = null;
        }
    }

    /**
     * Entfernt beide Kinder dieses Knotens.
     */
    public void removeChildren() {
        if (firstNode != null) {
            firstNode.deleted = true;
            firstNode.parent = null;
            firstNode = null;
        }
        if (secondNode != null) {
            secondNode.deleted = true;
            secondNode.parent = null;
            secondNode = null;
        }
    }

    /**
     * @return
     */
    public DecisionTreeNode getFirstNode() {
        return firstNode;
    }

    /**
     * @return
     */
    public DecisionTreeNode getSecondNode() {
        return secondNode;
    }

    /**
     * @param firstNode
     */
    public void _setFirstNode(final DecisionTreeNode firstNode) {
        this.firstNode = firstNode;
    }

    /**
     * @param secondNode
     */
    public void _setSecondNode(final DecisionTreeNode secondNode) {
        this.secondNode = secondNode;
    }

    /**
     * @return
     */
    public DecisionTreeNode getParent() {
        return parent;
    }

    /**
     * @return
     */
    public boolean isNeeded() {
        return needed;
    }

    @Override
    public String toString() {
        int leafValue = isLeaf() ? getValue() : -1;
        return super.toString() + " [" + needed + ", " + (deleted ? "deleted" : "not deleted") + ", " + leafValue + "]";
    }

    /**
     * @return
     */
    public int getValue() {
        return Integer.MAX_VALUE;
    }

    /**
     * @return
     */
    public boolean isLeaf() {
        return false;
    }

    /**
     * Klasse für die Blätter des Baumes. Sie merken sich zusätzlich noch den
     * Gesamtwert der Unterstützung
     *
     * @author AXS
     */
    private static final class DecisionTreeLeaf extends DecisionTreeNode {

        /**
         * COMMENTME
         */
        private int value = 0;

        /**
         * @param needed
         * @param parent
         * @param value
         */
        public DecisionTreeLeaf(final boolean needed, final DecisionTreeNode parent, final int value) {
            super(needed, parent);
            this.value = value;
        }

        @Override
        public final int getValue() {
            return value;
        }

        @Override
        public final boolean isLeaf() {
            return true;
        }
    }

}
