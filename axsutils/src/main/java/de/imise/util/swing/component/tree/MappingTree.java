package de.imise.util.swing.component.tree;

import java.util.Comparator;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Ein Baum, der sich in einer extra Map merkt, für welches UserObject welcher Knoten angelegt wurde. Um den Knoten
 * für ein UserObject heruas zu bekommen, muss also nicht der ganze baum durchsucht werden. Das bringt vor allem
 * in sehr großen Bäumen Performancevorteile.
 * 
 * Falls ein UserObject mehr als einen Knoten hat, müsste man die Klasse dahingehend ändern, dass die Map auf ein Array
 * von Knoten mappt.
 * 
 * @author AXS
 */
public class MappingTree extends BasicTree {
	
	/**
	 * <code>HashMap</code>, die vom <code>userObject</code> des Knotens auf den dazugehörigen Knoten mappt
	 */
	private HashMap <Object, DefaultMutableTreeNode>userObjects2TreeNodes = new HashMap<Object, DefaultMutableTreeNode>();
	
	/**
	 * @param comparator
	 */
	public MappingTree(Comparator <Object>comparator) {
		super(comparator);
	}

	/**
	 * Erzeugt einen neuen Knoten mit dem übergebenene UserObject, der auch zu den MappedNodes 
	 * hinzugefügt wird.
	 * @param userObject
	 * @return
	 */
	public DefaultMutableTreeNode createMappedNode(Object userObject) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);
		userObjects2TreeNodes.put(userObject, node);
		return node;
	}

	/**
	 * Fügt den übergebenen Knoten mit seinem userObject zu den MappedNodes hinzu
	 * @param node
	 */
	public void mapNode(DefaultMutableTreeNode node){
		userObjects2TreeNodes.put(node.getUserObject(), node);
	}
	
	/**
	 * Entfernt den Knoten mit dem übergebenen UserObject aus dem Baum. Dieser Knoten muss 
	 * zwingend in den MappedNodes vorkommen, aus denen er und auch alle seine gemappten 
	 * Kindknoten entfernt werden.
	 * @param userObject
	 */
	protected final void removeNodeAndRefreshTree(Object userObject){
		//den Knoten und alle seine Kinder aus dem Baum und den MappedNodes löschen
		DefaultMutableTreeNode node2Remove = userObjects2TreeNodes.remove(userObject);
		removeNodeAndAllChildsFromMap(node2Remove);
		super.removeNodeAndRefreshTree(node2Remove);
	}
	
	/**
	 * Löscht aus den MappedNodes alle Kindknoten des übergebenen Knotens und den übergebenen Knoten selbst
	 * @param node
	 */
	protected void removeNodeAndAllChildsFromMap(DefaultMutableTreeNode node){
		for (int i=0; i<node.getChildCount(); i++){
			userObjects2TreeNodes.remove(node.getUserObject());
		}
	}

	/**
	 * Liefert einen Knoten zum übergebnen <code>userObject</code> oder <code>null</code>
	 * 
	 * @param userObject
	 * @return
	 */
	protected DefaultMutableTreeNode getMappedNode(Object userObject){
		Object o = userObjects2TreeNodes.get(userObject);
		if (o==null)
			return null;
		return (DefaultMutableTreeNode)o;
	}

}

