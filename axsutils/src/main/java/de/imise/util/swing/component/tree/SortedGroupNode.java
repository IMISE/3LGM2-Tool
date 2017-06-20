package de.imise.util.swing.component.tree;

import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * @author AXS 
 */
public class SortedGroupNode extends DefaultMutableTreeNode{
	
	/**
	 * Die Gruppe, in der ein Knoten unter seinem Parent eingeordnet wird. Einem Paren werden
	 * zuerst alle Knoten der Gruppe 0 untergehangen, dann 1 usw. Die Knoten innerhalb einer
	 * Gruppe können selbst auch sortiert sein. Wird kein Gruppenwert für einen solchen Knoten
	 * gesetzt, so gehört er zur Gruppe 0.
	 */
	private int nodeGroup = 0;
	
	/**
	 * Der Comparator, mit dem die Gruppen der Kindknoten jeweils sortiert werden.
	 */
	private static Comparator <Object>childComparator = null;
	
	/**
	 * @param userObject 
	 */
	public SortedGroupNode(Object userObject) {
		super(userObject);
	}

	/**
	 * @param userObject
	 * @param nodeGroup
	 */
	public SortedGroupNode(Object userObject, int nodeGroup) {
		this(userObject);
		this.nodeGroup = nodeGroup;
	}

	/**
	 * @param userObject
	 * @param nodeGroup
	 * @param childComparator
	 */
	public SortedGroupNode(Object userObject, int nodeGroup, boolean sort) {
		this(userObject);
		this.nodeGroup = nodeGroup;
	}

	/**
	 * @param comparator
	 */
	@SuppressWarnings("unchecked")
	public static void setChildComparator(Comparator <Object>comparator) {
		childComparator = new NodeComparator(comparator);
	}


	/* (Kein Javadoc)
	 * @see javax.swing.tree.DefaultMutableTreeNode#add(javax.swing.tree.MutableTreeNode)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void add(MutableTreeNode newChild) {
		super.add(newChild);
		Collections.sort(children, childComparator);
		
	}

	/* (Kein Javadoc)
	 * @see javax.swing.tree.MutableTreeNode#insert(javax.swing.tree.MutableTreeNode, int)
	 */
	@Override
	public void insert(MutableTreeNode newChild, int childIndex) {
		add(newChild);
	}

	//Hilfsklassen
	
	/**
	 * Wenn ein Comparator übegeben wird, so gibt dieser nur die Ordnung für die Kindknoten
	 * in einer Gruppe (also mit dem selben <code>nodeGroup</code>-Wert) vor. Dieser
	 * Comparator muss dahingehend erweitert werden, dass erst nach den Gruppen und dann
	 * in den Gruppen sortiert wird. 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private static class NodeComparator implements Comparator {

		/**
		 * Der String-Comparator der  für den Vergleich innerhalb einer Gruppe benutzt wird.
		 */
		private Comparator <Object> comparator = null;
		
		/**
		 * @param comparator
		 */
		public NodeComparator(Comparator <Object>comparator) {
			super();
			this.comparator = comparator;
			
		}

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Object arg0, Object arg1) {
			System.out.println("SortedGroupNode.NodeComparator.compare(Object arg0, Object arg1): falscher Comparator ;)");
			if (!SortedGroupNode.class.isAssignableFrom(arg0.getClass()) || !SortedGroupNode.class.isAssignableFrom(arg1.getClass()))
				return comparator.compare(arg0.toString(), arg1.toString());
			SortedGroupNode node1 = (SortedGroupNode) arg0;
			SortedGroupNode node2 = (SortedGroupNode) arg1;
			int groupDiff = node1.nodeGroup - node2.nodeGroup;
			if (groupDiff != 0)
				return groupDiff;
			return comparator.compare(arg0, arg1);
		}
	}


}

