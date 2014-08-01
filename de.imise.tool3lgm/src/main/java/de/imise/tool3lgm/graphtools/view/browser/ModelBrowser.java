package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.util.swing.component.TabbedPane;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.userproperties.UserProperties;

/** Erzeugt ModelBrowser für 3lgm */
public final class ModelBrowser extends TabbedPane implements ChangeListener, FocusListener {

	/** icon for tap with model of active frame */
	private final static ImageIcon activeIcon = new ImageIcon(Tool3lgmConstants.getIcon("toolIcon.gif").getImage().getScaledInstance(13, 13, Image.SCALE_FAST));

	/** xmlText color for tab with model of active frame */
	private final static Color activeColor = Color.BLUE;

	/** xmlText color for tabs with models of non-active frames */
	private static Color inactiveColor;

	/** Der zuletzt aktive Browser */
	protected static ModelBrowser lastActiveBrowser = null;

	/** Styles, die für die Anzeige der Teilmodelle einer {@link GDCollection} zur Verfügung stehen. */
	public enum STYLE {tab, combobox}
	
	/** Aktueller Style dieses Browsers */
	private STYLE style;
	
	/**
	 * Ein neuer Browser
	 */
	protected ModelBrowser(STYLE style) {
		super(TabbedPane.TOP, TabbedPane.SCROLL_TAB_LAYOUT);
		this.style = style;
		setMinimumSize(new Dimension(10, 10));
		addChangeListener(this);
		addFocusListener(this);
		inactiveColor = getForeground();
	}

	/**
	 * Tab für eine {@link GDCollection} wird hinzugefügt
	 * 
	 * @param gdcoll
	 * @return
	 */
	protected void addCollection(GDCollection gdcoll) {
		JComponent panel;
		if (style ==  STYLE.combobox)
			panel = new SubModelComboBoxPane(gdcoll);
		else 
			panel = new SubModelsTabbedPane(gdcoll);
		addTab(gdcoll.getName(), panel);
		setSelectedComponent(panel);
	}

	/**
	 * @param name
	 * @param doc
	 * @return
	 */
	protected void addGraphDocument(GraphDocument doc) {
		SubModelsBrowser collectionPane = getCollectionPane(doc.getCollection());
		if (collectionPane != null)
			collectionPane.addGraphDocument(doc);
	}

	/**
	 * @param doc
	 */
	protected void removeGraphDocument(GraphDocument doc) {
		SubModelsBrowser collectionPane = getCollectionPane(doc.getCollection());
		if (collectionPane != null)
			collectionPane.removeGraphDocument(doc);
		if (collectionPane.getDocCount() == 0)
			remove(collectionPane);
	}

	/**
	 * @return <code>GraphDocument</code> of the selected <code>ModelBrowser</code> -tree
	 */
	protected GraphDocument getSelectedDoc() {
		SubModelsBrowser collectionPane = (SubModelsBrowser) getSelectedComponent();
		if (collectionPane == null)
			return null;
		return collectionPane.getSelectedDoc();
	}

	/**
	 * @param doc
	 * @return tab-index of document/model-pane or -1
	 */
	protected final void setSelectedDoc(GraphDocument doc) {
		if (doc != null) {
			SubModelsBrowser pane = getCollectionPane(doc.getCollection());
			setSelectedComponent(pane);
			if (pane != null)
				pane.setSelectedDoc(doc);
			updateActiveBrowserTab();
		}
	}

	/**
	 * @param coll
	 * @return
	 */
	private SubModelsBrowser getCollectionPane(GDCollection coll) {
		for (int i = 0; i < getTabCount(); i++) {
			SubModelsBrowser collectionPane = (SubModelsBrowser) getComponentAt(i);
			if (collectionPane.getCollection() == coll)
				return collectionPane;
		}
		return null;
	}

	/**
	 * @param doc
	 */
	protected void updateTitle(GraphDocument doc) {
		SubModelsBrowser pane = getCollectionPane(doc.getCollection());
		if (pane!=null)
			pane.updateTitle(doc);
	}

	/**
	 * @param gdcoll
	 */
	protected void updateTitle(GDCollection gdcoll) {
		SubModelsBrowser modelPane = getCollectionPane(gdcoll);
		if (modelPane != null) {
			int index = indexOfComponent(modelPane);
			setTitleAt(index, gdcoll.getName());
		}
	}

	/**
	 * @param index
	 * @return
	 */
	protected GDCollection getCollectionAt(int index) {
		return (getTabCount() > index ? ((SubModelsBrowser)getComponentAt(index)).getCollection() : null);
	}

	/**
	 * @return
	 */
	protected int getCollectionIndex(GDCollection gdcoll) {
		for (int i = 0; i < getTabCount(); i++) {
			if (getCollectionAt(i) == gdcoll)
				return i;
		}
		return -1;
	}

	/**
	 * @return
	 */
	protected boolean hasCollection(GDCollection gdcoll) {
		return getCollectionIndex(gdcoll) > -1;
	}

	
	/**
	 * Hebt den aktiven Tab hervor und löscht die Hervorhebung beim letzten aktiven Tab.
	 */
	private void updateActiveBrowserTab() {
		if (UserProperties.isShowModelsInSeparateBrowser() && lastActiveBrowser == this)
			return;
		int index = -1;
		if (lastActiveBrowser != null) {
			if (UserProperties.isShowModelsInSeparateBrowser()) {
				 index = lastActiveBrowser.getSelectedIndex();
			} else {
				for (int i=0; i<lastActiveBrowser.getTabCount(); i++) {
					if (lastActiveBrowser.getForegroundAt(i) == activeColor) {
						index = i;
						break;
					}
				}
			}
		}
		if (index >= 0) {
			lastActiveBrowser.setIconAt(index, null);
			lastActiveBrowser.setForegroundAt(index, inactiveColor);
		}
		index = getSelectedIndex();
		if (index >= 0) {
			setIconAt(index, activeIcon);
			setForegroundAt(index, activeColor);
		}
		
		lastActiveBrowser = this;
	}


	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		updateActiveBrowserTab();
		GraphDocument doc = getSelectedDoc();
		Tool3lgm.tool.setSelectedDoc(doc, doc != null);
	}

	/* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    @Override
    public void focusGained(FocusEvent e) {
    	fireStateChanged();
    }

	/* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    @Override
    public void focusLost(FocusEvent e) {
    }


}
