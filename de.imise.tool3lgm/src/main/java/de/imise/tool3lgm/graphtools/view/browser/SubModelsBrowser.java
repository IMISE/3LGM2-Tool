package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.view.tree.DynamicTree;

/**
 * Interface für alle {@link Component}, die im {@link ModelBrowser} Teilmodelle anzeigen.
 * 
 * @author AXS
 */
public abstract class SubModelsBrowser extends JPanel implements MouseListener, FocusListener {

	/**
	 * Das Modell das über dieses Tab-Pane dargestellt wird
	 */
	protected GDCollection gdcoll;

	/**
	 * Der Baum, in dem in diesem TeilomodellBrwoser alle Daten angezeigt werden. Er wird immer in den Tab im Vordergrund eingebaut.
	 */
	protected DynamicTree tree;

	/**
	 * @param gdcoll
	 */
	public SubModelsBrowser(GDCollection gdcoll) {
	    super(new BorderLayout());
	    this.gdcoll = gdcoll;
		tree = new DynamicTree(gdcoll.getMainGraphDocument());
		tree.addMouseListener(this);
    }

	/**
	 * @return Returns the Model.
	 */
	public abstract GDCollection getCollection();

	/**
	 * @param doc
	 */
	public abstract void addGraphDocument(GraphDocument doc);

	/**
	 * Aktualisiert den Titel des Tabs des übergebenen {@link GraphDocument}
	 * 
	 * @param doc
	 */
	public abstract void updateTitle(GraphDocument doc);

	/**
	 * Liefert das aktuell  selektierte {@link GraphDocument}
	 * 
	 * @return
	 */
	public abstract GraphDocument getSelectedDoc();

	/**
	 * @param doc
	 */
	public abstract void setSelectedDoc(GraphDocument doc);
	
	/**
	 * @param doc
	 */
	public abstract void removeGraphDocument(GraphDocument doc);

	/**
	 * Liefert die Anzahl von {@link GraphDocument}, die dieser Browser darstellt
	 * 
	 * @return
	 */
	public abstract int getDocCount();

	
	/**
	 * Aktualisiert die Komponente
	 */
	public abstract void update();
	
	/* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

	/* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

	/* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

	/* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
    	update();
    }

	/* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

	/* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    @Override
    public void focusGained(FocusEvent e) {
    	update();
    }

	/* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    @Override
    public void focusLost(FocusEvent e) {
    }


    
}
