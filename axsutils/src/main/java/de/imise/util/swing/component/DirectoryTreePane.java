package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * <code>ScrollPane</code> mit einem <code>JTree</code>, der das Filesystem darstellen kann.
 * 
 * @author Thomas Rudert
 * created on 07.01.2004
 */
public class DirectoryTreePane extends JScrollPane	implements TreeWillExpandListener, TreeSelectionListener{

	/**
	 * COMMENTME
	 */
	private JTree directoryTree;
	
	/**
	 * COMMENTME
	 */
	private DefaultTreeModel treeModel;

	/**
	 * COMMENTME
	 */
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	/**
	 * 
	 */
	public DirectoryTreePane() {
		super();
		
		TreeNode root = new DirectoryTreeNode("Roots", getRoots(), fileSystemView);
		treeModel = new DefaultTreeModel(root);
		directoryTree = new JTree(treeModel);
		directoryTree.setRootVisible(false);
		directoryTree.setShowsRootHandles(true);
		directoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		if (root.getChildCount() == 1)
			directoryTree.expandRow(0);
			
		directoryTree.setCellRenderer(createDirectoryTreeRenderer());
		directoryTree.addTreeSelectionListener(this);
		directoryTree.addTreeWillExpandListener(this);
		
		setViewportView(directoryTree);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		DirectoryTreeNode node = (DirectoryTreeNode) event.getPath().getLastPathComponent();
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		node.ensureChildrenAreLoaded();
		
		setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * @return
	 */
	private TreeCellRenderer createDirectoryTreeRenderer() {
		return new DefaultTreeCellRenderer() {
			
			/* (non-Javadoc)
			 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
			 */
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value,	sel, expanded, false, row, hasFocus);
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				if (!(node.getUserObject() instanceof File))
					return this;
					
				File directory = (File) node.getUserObject();
				setText(getSystemDisplayName(directory));
				setIcon(getSystemIcon(directory));
				return this;
			}

		};
	}
	
	/**
	 * @param directory
	 * @return
	 */
	private String getSystemDisplayName(File directory) {
		return fileSystemView.getSystemDisplayName(directory);
	}
	
	/**
	 * @param file
	 * @return
	 */
	private Icon getSystemIcon(File file) {
		return fileSystemView.getSystemIcon(file);
	}
	
	/**
	 * @return
	 */
	private File[] getRoots() {
		return fileSystemView.getRoots();
	}

	/**
	 * @return
	 */
	public File[] getSelectedDirectories() {
		TreePath[] selection = directoryTree.getSelectionPaths();
		if (selection == null)
			return new File[0];

		File[] returnValue = new File[selection.length];
		for (int i = 0; i < selection.length; i++)
			returnValue[i] = ((DirectoryTreeNode) selection[i].getLastPathComponent()).getDirectory();
		return returnValue;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
//		File selectedDirectory = ((DirectoryTreeNode)e.getPath().getLastPathComponent()).getDirectory();
		EventListener[] listeners = listenerList.getListeners(TreeSelectionListener.class);
		for (int i = 0; i < listeners.length; i++)
			((TreeSelectionListener) listeners[i]).valueChanged(e);
		
	}
	
	/**
	 * @param listener
	 */
	public void addTreeSelectionListener(TreeSelectionListener listener) {
		listenerList.add(TreeSelectionListener.class, listener);
	}
	
	/**
	 * @param listener
	 */
	public void removeTreeSelectionList(TreeSelectionListener listener) {
		listenerList.remove(TreeSelectionListener.class, listener);
	}

	/**
	 * @param b
	 */
	public void setMultiSelection(boolean b) {
		directoryTree.getSelectionModel().setSelectionMode(b ? TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION : TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	/**
	 * @param dir
	 */
	public void createDir(String dirName) {

		if (dirName == null)
			return;

		DirectoryTreeNode parent = (DirectoryTreeNode) directoryTree.getSelectionModel().getSelectionPath().getLastPathComponent();
		DirectoryTreeNode child;
		
		File dir = new File(parent.getDirectory(), dirName);
		dir.mkdir();
		if (!dir.isDirectory())
			return;

		Enumeration<DirectoryTreeNode> children = parent.children();
		int index = 0;
		while (children.hasMoreElements()) {
			if (dir.equals((child = children.nextElement()).getDirectory())) {
				directoryTree.getSelectionModel().setSelectionPath(new TreePath(child.getPath()));
				return;
			}
			
			if (dir.getName().compareToIgnoreCase(child.getDirectory().getName()) > 0)
				index++;
		}
			
		child = new DirectoryTreeNode(dir, parent.getFileSystemView(), true);
		treeModel.insertNodeInto(child, parent, index);
		directoryTree.getSelectionModel().setSelectionPath(new TreePath(child.getPath()));
	}
}
