package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
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
    public SubModelsBrowser(final GDCollection gdcoll) {
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
     * Liefert das aktuell selektierte {@link GraphDocument}
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
     * @return Namen des Modells, das dieser Browser darstellt
     */
    public final String getTitle() {
        return gdcoll.getName();
    }

    /**
     * Aktualisiert die Komponente
     */
    public abstract void update();

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
    }

    @Override
    public void mouseExited(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        update();
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    @Override
    public void focusGained(final FocusEvent e) {
        update();
    }

    @Override
    public void focusLost(final FocusEvent e) {
    }

}
