package de.imise.util.swing.component.tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Ein Baum der sich seine Selektion merken und wiederherstellen kann. (U.v.m.)
 * 
 * @author AXS 
 */
public class BasicTree extends JTree{
	
	/**
	 * Der Comparator, mit dem alle alphabetischen Einsortierungen erfolgen.
	 */
	protected Comparator<Object> comparator = null;
	
	/**
	 * Das <code>TreeModel</code> dieses Baumes
	 */
	protected DefaultTreeModel treeModel = (DefaultTreeModel)getModel();
	
	/**
	 * Der root-Knoten dieses Baumes
	 */
	protected DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel.getRoot();
	
	/**
	 * 
	 */
	public BasicTree() {
		this(null);
	}
	
	/**
	 * @param comparator
	 */
	public BasicTree(Comparator<Object> comparator) {
		super();
		root.removeAllChildren();
		treeModel.reload();
		this.comparator = comparator;
	}
	
	/**
	 * Setzt das neue <code>TreeModel</code> und aktualisiert die Variablen <code>treeModel</code>
	 * und <code>root</code>.
	 * @see javax.swing.JTree#setModel(javax.swing.tree.TreeModel)
	 */
	@Override
	public void setModel(TreeModel newModel) {
		super.setModel(newModel);
		treeModel = (DefaultTreeModel)getModel();
		root = (DefaultMutableTreeNode)treeModel.getRoot();
	}

	/**
	 * @param parent
	 * @param node2add
	 */
	public void addNodeAndRefreshTree(DefaultMutableTreeNode parent, DefaultMutableTreeNode node2add){
		insertNodeAndRefreshTree(parent, node2add, parent.getChildCount());
	}

	/**
	 * @param parent
	 * @param node2add
	 * @param index
	 */
	public void insertNodeAndRefreshTree(DefaultMutableTreeNode parent, DefaultMutableTreeNode node2add, int index){
		//Expansion merken
		saveExpansionState();
		//füge den Kindknoten an Position index hinzu
		parent.insert(node2add, index);
		//Baum neu aufbauen
		treeModel.reload();
		//Expansionen wieder herstellen
		restoreExpansionState();
	}

	/**
	 * Fügt den übergebenen <code>node2Insert</code> dem <code>parent</code>-Knoten unter, nach den
	 * Regeln, die der Comparator vorgibt.<br>
	 * Vorrausetzung ist, dass alle Kindknoten unter dem Parent schon alphabetisch sortiert sind.
	 * Ist der Knoten bereits unter diesem Parent vorhanden wird er neu einsortiert.
	 * @param parent
	 * @param node2Insert
	 */
	public void insertNodeAlphabeticalAndRefreshTree(DefaultMutableTreeNode parent, DefaultMutableTreeNode node2Insert){
		insertNodeAlphabeticalAndRefreshTree(parent, node2Insert, 0);
	}


	/**
	 * Fügt den übergebenen <code>node2Insert</code> dem <code>parent</code>-Knoten unter, nach den
	 * Regeln, die der Comparator vorgibt.
	 * Der <code>startIndex</code> gibt an, beim wievielten Knoten mit dem einsortieren erst begonnen
	 * werden soll.<br>
	 * Vorrausetzung ist, dass alle Kindknoten unter dem Parent schon alphabetisch sortiert sind.
	 * Ist der Knoten bereits unter diesem Parent vorhanden wird er neu einsortiert.
	 * @param parent
	 * @param node2Insert
	 * @param startIndex
	 */
	public void insertNodeAlphabeticalAndRefreshTree(DefaultMutableTreeNode parent, DefaultMutableTreeNode node2Insert, int startIndex){
		//Expansion merken
		saveExpansionState();

		//hole den jetzigen Parent des Knotens
		MutableTreeNode oldParent = (MutableTreeNode)node2Insert.getParent();
		//wenn der Knoten schon diesem parent untergeordnet war
		if (parent==oldParent)
			//Knoten entfernen
			parent.remove(node2Insert);

		//Teile und Herrsche aus dem Lehrbuch :)
		int l = startIndex;
		int r = parent.getChildCount()-1;
		while (r>=l){
			int pos = l + ((r-l)/2);
			Object o = parent.getChildAt(pos);
			int compareValue = comparator.compare(node2Insert, o);
			if (compareValue>0){
				l = pos+1;
			}else if (compareValue<0){
				r = pos-1;
			}else{
				l=pos;
				break;
			}
		}
		//füge den Knoten an der neuen Position hinzu
		parent.insert(node2Insert, l);
		
		//Baum neu aufbauen
		treeModel.reload();
		//Expansionen wieder herstellen
		restoreExpansionState();
	}


	/**
	 * Entfernt den übergebenen Knoten aus dem Baum, baut den Baum neu auf und stellt von den
	 * verbliebenen Knoten die Expansion wieder her.
	 * @param node2Remove
	 */
	public void removeNodeAndRefreshTree(DefaultMutableTreeNode node2Remove){
		//Expansion merken
		saveExpansionState();
		//den Parent-Knoten holen
		MutableTreeNode parent = (MutableTreeNode)node2Remove.getParent();
		//Knoten entfernen
		parent.remove(node2Remove);
		//Baum neu aufbauen
		treeModel.reload();
		//Expansionen wieder herstellen
		restoreExpansionState();
	}

	/**
	 * Entfernt alle übergebenen Knoten aus dem Baum, baut den Baum neu auf und stellt von den
	 * verbliebenen Knoten die Expansion wieder her.
	 * 
	 * @param nodes2RemoveList
	 */
	public void removeNodesAndRefreshTree(ArrayList<MutableTreeNode> nodes2RemoveList){
		//Expansion merken
		saveExpansionState();
		//für alle zu entfernenden Knoten
		for (MutableTreeNode node2Remove : nodes2RemoveList)
			//Knoten  vom Parent-Knoten des zu entfernenden Knoten entfernen
			((MutableTreeNode) node2Remove.getParent()).remove(node2Remove);
		//Baum neu aufbauen
		treeModel.reload();
		//Expansionen wieder herstellen
		restoreExpansionState();
	}


	/**
	 * Selektiert den übergebenen Knoten, wenn er im Baum vorkommt
	 * @param node
	 */
	protected void selectNode(TreeNode node){
		TreeNode[] nodes = treeModel.getPathToRoot(node);
		if (nodes==null)
			return;
		TreePath path = new TreePath(nodes);
		setSelectionPath(path);
		scrollPathToVisible(path);
	}

	/**
	 * Setzt den übergebenen Knoten in den Editier-Modus, wenn der <code>TreeCellEditor</code> des 
	 * Baumes nicht <code>null</code> ist.
	 * 
	 * @param node
	 */
	protected void editNode(DefaultMutableTreeNode node){
		//den Knoten selektieren und in den Editier-Modus setzen, wenn dieser Baum sichtbar ist
		if (isVisible() && node!=null && getCellEditor()!=null){
			TreePath path = new TreePath(node.getPath());
			startEditingAtPath(path);
			scrollPathToVisible(path);
		}
	}


	////////////////////////////////////////////
	// Expansion merken und wieder herstellen //
	////////////////////////////////////////////
	
	/**
	 * <code>Enumeration</code> der expandierten Pfade
	 */
	private Enumeration<TreePath> expansionEnum = null;

	/**
	 * Aktuelle Selektion merken
	 */
	protected void saveExpansionState() {
		expansionEnum = getExpandedDescendants(new TreePath(treeModel.getRoot()));
	}
	/**
	 * Selektion wieder herstellen. Da sich wenn man die Selektion wieder herstellen möchte eigentlich
	 * immer das <code>TreeModel</code> geändert hat, wird es auch neu geladen. 
	 */
	protected void restoreExpansionState() {
		treeModel.reload();
		if (expansionEnum != null) {
			while (expansionEnum.hasMoreElements()) {
				TreePath path = expansionEnum.nextElement();
				expandPath(path);
			}
		}
	}

}

