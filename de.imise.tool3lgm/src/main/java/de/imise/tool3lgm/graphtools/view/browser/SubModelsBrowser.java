package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.tree.DynamicTree;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * Interface für alle {@link Component}, die im {@link ModelBrowser} Teilmodelle anzeigen.
 *
 * @author AXS
 */
public final class SubModelsBrowser extends JPanel implements MouseListener, FocusListener, ItemListener {

    /**
     * Das Modell das über dieses Tab-Pane dargestellt wird
     */
    private final GDCollection gdcoll;

    /**
     * Der Baum, in dem in diesem TeilomodellBrwoser alle Daten angezeigt werden. Er wird immer in den Tab im Vordergrund eingebaut.
     */
    private final DynamicTree tree;

    /**
     * Combobox, in der das aktuelle Teilmodell ausgewählt werden kann
     */
    private final AlphabeticalComboBox submodelBox;

    /**
     * @param gdcoll
     */
    public SubModelsBrowser(final GDCollection gdcoll) {
        super(new BorderLayout());
        this.gdcoll = gdcoll;
        tree = new DynamicTree(gdcoll.getMainGraphDocument());
        tree.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.addMouseListener(this);
        scrollPane.getHorizontalScrollBar().addMouseListener(this);
        scrollPane.getVerticalScrollBar().addMouseListener(this);
        submodelBox = new AlphabeticalComboBox();
        submodelBox.addItemListener(this);
        submodelBox.addMouseListener(this);
        add(submodelBox, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * @param doc
     */
    public void addGraphDocument(final GraphDocument doc) {
        submodelBox.addItem(doc);
        //das erste doc ist immer das Gesamtmodell -> erstes Doc in eigene Liste packen (also einen
        //Separator nach dem ersten einfügen), damit es immer oben steht (egal wie es heißt) und nur
        //die Elemente darunter (alle Szenarios) sortiert werden
        if (submodelBox.getItemCount() == 1) {
            submodelBox.addSeparator(false);
        }
    }

    /**
     * @return
     */
    public GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * @return
     */
    public int getDocCount() {
        int count = 0;
        for (int i = 0; i < submodelBox.getItemCount(); i++) {
            if (submodelBox.getObjectAt(i) instanceof GraphDocument) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param doc
     */
    public void removeGraphDocument(final GraphDocument doc) {
        submodelBox.removeItem(doc);
    }

    /**
     * @return
     */
    GraphDocument getCurrentDoc() {
        Object o = submodelBox.getSelectedObject();
        if (o instanceof GraphDocument) {
            return (GraphDocument) o;
        }
        return null;
    }

    /**
     * @param doc
     */
    void setCurrentDoc(final GraphDocument doc) {
        submodelBox.removeItemListener(this);
        submodelBox.setSelectedObject(doc);
        tree.setGraphDocument(doc);
        submodelBox.addItemListener(this);
    }

    /**
     *
     */
    public void update() {
        submodelBox.removeItemListener(this);
        submodelBox.resort();
        submodelBox.addItemListener(this);
    }

    /**
     * @return Namen des Modells, das dieser Browser darstellt
     */
    public final String getTitle() {
        return gdcoll.getName();
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        GraphDocument currentDoc = getCurrentDoc();
        Static.setSelectedDoc(currentDoc);
    }

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
