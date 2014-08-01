package de.imise.tool3lgm.graphtools.analyse.redundancy;

/**
 * Knoten eines Entscheidungsbaumes.<br>
 * Man kann in beide Richtungen navigieren, d.h. ein Knoten kennt seinen Vater-Knoten und seine beiden
 * Kindknoten (wenn vorhanden).
 * Hat ein Knoten nur einen Kindknoten, so ist dieser immer der erste Kindknoten (<code>firstNode</code>).
 * Entfernt man von einem Knoten mit 2 Kindknoten den ersten Knoten, so wird der vormals zweite Kindknoten
 * dann der erste.<br>
 * Es können maximal 2 Kindknoten hinzugefügt werden.<br>
 * Jeder Knoten hat einen <code>boolean</code>-Status. Im Kontext eines Entscheidungsbaumes gibt der Status an,
 * ob ein Element, das der Knoten repräsentiert, dazugehört oder nicht.
 * 
 * @author AXS
 */
public class DecisionTreeNode {
	
	/**
	 * Der Status des Knotens
	 */
	private boolean needed;
	
	/**
	 * Vaterknoten des Knotens
	 */
	private DecisionTreeNode parent;
	
	/**
	 * Erster Kindknoten
	 */
	private DecisionTreeNode firstNode = null;

	/**
	 * Zweiter Kindknoten. Ist nur ungleich <code>null</code>, wenn es einen ersten Kindknoten gibt.
	 */
	private DecisionTreeNode secondNode = null;
	
	/**
	 * @param needed
	 * @param parent
	 */
	protected DecisionTreeNode(boolean needed, DecisionTreeNode parent) {
		super();
		this.needed=needed;
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
	public DecisionTreeNode addChild(boolean needed){
		DecisionTreeNode node = null;
		if (secondNode==null){
			node = new DecisionTreeNode(needed, this);
			if (firstNode==null){
				firstNode = node;
			}else{
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
	public DecisionTreeNode addLeaf(boolean needed, int value){
		DecisionTreeNode node = null;
		if (secondNode==null){
			node = new DecisionTreeLeaf(needed, this, value);
			if (firstNode==null){
				firstNode = node;
			}else{
				secondNode = node;
			}
		}
		return node;
	}

	boolean deleted = false;
	
	/**
	 * Entfernt den übergebenen Knoten von diesem Knoten, wenn er der Vater davon ist. Falls
	 * der zu löschende Knoten der erste Kindknoten ist und es noch einen zweiten Knidknoten
	 * gibt, ist der ehemals zweite nach dem Löschen der erste.
	 * @param node
	 */
	public void removeChild(DecisionTreeNode node) {
		if (node==null)
			return;
		if (node == firstNode){
			firstNode.deleted = true;
			firstNode.parent = null;
			firstNode = secondNode;
			secondNode = null;
		}else if (node==secondNode){
			secondNode.deleted = true;
			secondNode.parent = null;
			secondNode = null;
		}
	}
	
	/**
	 * Entfernt beide Kinder dieses Knotens.
	 */
	public void removeChildren(){
		if (firstNode!=null){
			firstNode.deleted = true;
			firstNode.parent=null;
			firstNode = null;
		}
		if (secondNode!=null){
			secondNode.deleted = true;
			secondNode.parent=null;
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
	public void _setFirstNode(DecisionTreeNode firstNode) {
		this.firstNode = firstNode;
	}

	/**
	 * @param secondNode
	 */
	public void _setSecondNode(DecisionTreeNode secondNode) {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString()+" [" +needed + ", " + (deleted?"deleted":"not deleted")+", "+ (isLeaf()?new Integer(getValue()).toString():"-1") +"]";
	}

	/**
	 * @return
	 */
	public int getValue(){
		return Integer.MAX_VALUE;
	}
	
	/**
	 * @return
	 */
	public boolean isLeaf(){
		return false;
	}
	
	/**
	 * Klasse für die Blätter des Baumes. Sie merken sich zusätzlich noch den Gesamtwert der
	 * Unterstützung
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
		public DecisionTreeLeaf(boolean needed, DecisionTreeNode parent, int value) {
			super(needed, parent);
			this.value = value;
		}

		/* (non-Javadoc)
		 * @see tool3lgm.graphtools.analyse.redundancy.DecisionTreeNode#getValue()
		 */
		@Override
		public final int getValue(){
			return value;
		}

		/* (non-Javadoc)
		 * @see tool3lgm.graphtools.analyse.redundancy.DecisionTreeNode#isLeaf()
		 */
		@Override
		public final boolean isLeaf(){
			return true;
		}
	}

}
