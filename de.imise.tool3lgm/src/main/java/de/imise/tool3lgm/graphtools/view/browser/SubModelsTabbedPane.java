/*
 * Created on 09.08.2004
 *
 */
package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.util.swing.component.TabbedPane;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GDCollectionOwner;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.tree.DynamicTree;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * Tab-Pane, in dem alle Teilmodelle eines Modells aufgelistet werden. Es besitzt einen {@link DynamicTree}, der immer in das ScrollPane des im Vordergrund befindlichen Tabs gelegt wird.
 * 
 * @author imi0wendt, AXS
 */
public class SubModelsTabbedPane extends SubModelsBrowser implements GDCollectionOwner, ChangeListener {

	/**
	 * Das Tabpane, in dem die Teilmodelle angezeigt werden
	 */
	private TabbedPane tabPane;
	
	/**
	 * @param gdcoll
	 */
	public SubModelsTabbedPane(GDCollection gdcoll) {
		super(gdcoll);
	    tabPane = new TabbedPane();
	    add(tabPane, BorderLayout.CENTER);
	    tabPane.setTabsInOneLineLayout(UserProperties.isShowSubModelsInBrowserSideBySide());
	    tabPane.addChangeListener(this);
		addFocusListener(this);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#getCollection()
	 */
	@Override
	public GDCollection getCollection() {
		return gdcoll;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#addGraphDocument(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void addGraphDocument(GraphDocument doc) {
		GraphDocumentScrollPane scrollPane = new GraphDocumentScrollPane(doc);
		scrollPane.addMouseListener(this);
		scrollPane.getHorizontalScrollBar().addMouseListener(this);
		scrollPane.getVerticalScrollBar().addMouseListener(this);
		tabPane.addTab(doc.getTitle(), scrollPane);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#updateTitle(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void updateTitle(GraphDocument doc) {
		int index = getTabIndex(doc);
		if (index >= 0)
			tabPane.setTitleAt(index, doc.toString());
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#getSelectedDoc()
	 */
	@Override
	public GraphDocument getSelectedDoc() {
		GraphDocumentScrollPane treeScrollPane = (GraphDocumentScrollPane) tabPane.getSelectedComponent();
		if (treeScrollPane == null)
			return null;
		return treeScrollPane.getGraphDocument();
	}
	
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#setSelectedDoc(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void setSelectedDoc(GraphDocument doc) {
		int index = getTabIndex(doc);
		if (index >= 0)
			tabPane.setSelectedIndex(index);
	}
	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#removeGraphDocument(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void removeGraphDocument(GraphDocument doc) {
		int index = getTabIndex(doc);
		if (index >= 0)
			tabPane.removeTabAt(index);
	}

	/**
	 * Liefert den Indes des Tab, an dem sich das übergebene {@link GraphDocument} befindet oder -1;
	 * 
	 * @param doc
	 * @return
	 */
	private int getTabIndex(GraphDocument doc) {
		for (int i = 0; i < tabPane.getTabCount(); i++) {
			GraphDocument tabDoc = ((GraphDocumentScrollPane) tabPane.getComponentAt(i)).getGraphDocument();
			if (doc == tabDoc)
				return i;
		}
		return -1;
	}

	/* (non-Javadoc)
     * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#getDocCount()
     */
    @Override
    public int getDocCount() {
	    return tabPane.getTabCount();
    }

    /* (non-Javadoc)
     * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#update()
     */
    @Override
    public void update() {
		Component selComp = tabPane.getSelectedComponent();
		GraphDocument selDoc = null;
		if (selComp instanceof GraphDocumentScrollPane) {
			GraphDocumentScrollPane scrollPane = (GraphDocumentScrollPane) selComp;
			selDoc = scrollPane.getGraphDocument();
			scrollPane.setViewportView(tree);
		}
		if (tree.getGraphDocument() != selDoc)
			tree.setGraphDocument(selDoc);
		if (selDoc != Tool3lgm.tool.getSelectedDoc())
			Tool3lgm.tool.setSelectedDoc(selDoc, true);
    }
    
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		update();
	}

}
