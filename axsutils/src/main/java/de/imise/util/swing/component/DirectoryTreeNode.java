package de.imise.util.swing.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * <code>TreeNode</code> der ein Objekt des Filesystems repräsentieren kann (Ordner, Laufwerke, Dateien, ...) 
 * 
 * @author Thomas Rudert
 * Created on 07.01.2004
 */
public class DirectoryTreeNode extends DefaultMutableTreeNode {

	/**
	 * COMMENTME
	 */
	private boolean childrenAreLoaded;

	/**
	 * COMMENTME
	 */
	private final FileSystemView fileSystemView;
	
	/**
	 * @param directory
	 * @param fileSystemView
	 */
	private DirectoryTreeNode(File directory, FileSystemView fileSystemView) {
		super(directory);
		this.fileSystemView = fileSystemView;
		childrenAreLoaded = false;
	}
	
	/**
	 * @param directory
	 * @param fileSystemView
	 * @param childrenAreLoaded
	 */
	public DirectoryTreeNode(File directory, FileSystemView fileSystemView, boolean childrenAreLoaded) {
		super(directory);
		this.fileSystemView = fileSystemView;
		this.childrenAreLoaded = childrenAreLoaded;
	}
	
	/**
	 * @param name
	 * @param children
	 * @param fileSystemView
	 */
	public DirectoryTreeNode(String name, File[] children, FileSystemView fileSystemView) {
		super(name);
		this.fileSystemView = fileSystemView;
		addChildren(children);
	}
	
	/**
	 * @param children
	 */
	private void addChildren(File[] children) {
		childrenAreLoaded = true;
		
		for(int i=0; i<children.length; i++)
			add(new DirectoryTreeNode(children[i], fileSystemView));
	}
	
    /* (non-Javadoc)
     * @see javax.swing.tree.DefaultMutableTreeNode#children()
     */
    @Override
	public Enumeration<DirectoryTreeNode> children() {
		ensureChildrenAreLoaded();
		return super.children();
	}
	
	/**
	 * 
	 */
	public void ensureChildrenAreLoaded() {
		if (!childrenAreLoaded)
			loadChildren();
	}
	
	/**
	 * 
	 */
	private void loadChildren() {
		childrenAreLoaded = true;
		File[] files = fileSystemView.getFiles(getDirectory(), false);
		
		if (files == null) return;
		
		ArrayList<File> childDirectories = new ArrayList<File>();
		for (int i=0; i<files.length; i++)
			if (fileSystemView.isTraversable(files[i]).booleanValue())
				childDirectories.add(files[i]);
				
		Collections.sort(childDirectories);
		
		for (int i=0; i<childDirectories.size(); i++)
			add(new DirectoryTreeNode(childDirectories.get(i), fileSystemView));
	}
	
	
	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getChildAt(int)
	 */
	@Override
	public TreeNode getChildAt(final int index) {
		ensureChildrenAreLoaded();
		return super.getChildAt(index);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#getChildCount()
	 */
	@Override
	public int getChildCount() {
		ensureChildrenAreLoaded();
		return super.getChildCount();
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return childrenAreLoaded && super.isLeaf();
	}

	/**
	 * @return getUserObject() if it's instance of File, otherwise null
	 */
	public File getDirectory() {
		if (getUserObject() instanceof File)
			return (File) getUserObject();
		
		return null;
	}
	
	/**
	 * @return
	 */
	public FileSystemView getFileSystemView() {
		return fileSystemView;
	}
	
}




