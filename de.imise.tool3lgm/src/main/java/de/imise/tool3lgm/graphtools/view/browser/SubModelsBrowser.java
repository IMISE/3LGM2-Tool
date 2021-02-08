package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.tree.ModelBrowserTree;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentListener;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * Interface für alle {@link Component}, die im {@link ModelBrowser} Teilmodelle
 * anzeigen.
 *
 * @author AXS
 */
public final class SubModelsBrowser extends JPanel implements FocusListener, ItemListener, PopupMenuListener, LGMChangeListenerSimple {

    /**
     * Das Modell das über dieses Tab-Pane dargestellt wird
     */
    private final GDCollection gdcoll;

    /**
     * Der Baum, in dem in diesem TeilomodellBrwoser alle Daten angezeigt
     * werden. Er wird immer in den Tab im Vordergrund eingebaut.
     */
    private final ModelBrowserTree tree;

    /**
     * Combobox, in der das aktuelle Teilmodell ausgewählt werden kann
     */
    private final AlphabeticalComboBox<GraphDocument> submodelBox;

    /**
     * @param gdcoll
     */
    public SubModelsBrowser(final GDCollection gdcoll) {
        super(new BorderLayout());
        this.gdcoll = gdcoll;
        gdcoll.addClosedTransactionsListener(this);
        //Submodel ComboBox
        submodelBox = addFocusListener(new AlphabeticalComboBox<>());
        submodelBox.addItemListener(this);
        submodelBox.addPopupMenuListener(this);
        //ModelElements Tree
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        tree = addFocusListener(new ModelBrowserTree(mainDoc));
        JScrollPane scrollPane = addFocusListener(new JScrollPane(tree));
        addFocusListener(scrollPane.getHorizontalScrollBar());
        addFocusListener(scrollPane.getVerticalScrollBar());
        //add
        add(submodelBox, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * @param comp
     * @return
     */
    private <T extends Component> T addFocusListener(final T comp) {
        comp.addFocusListener(this);
        return comp;
    }

    /**
     * @param doc
     */
    public void addGraphDocument(final GraphDocument doc) {
        if (submodelBox.contains(doc)) {
            return;
        }
        submodelBox.addObject(doc);
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
            if (submodelBox.getObjectAt(i) != null) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param doc
     */
    public void removeGraphDocument(final GraphDocument doc) {
        submodelBox.removeObject(doc);
    }

    /**
     * @return
     */
    GraphDocument getCurrentDoc() {
        return submodelBox.getSelectedObject();
    }

    /**
     * @param doc
     */
    void setCurrentDoc(final GraphDocument doc) {
        submodelBox.removeItemListener(this);
        submodelBox.setSelectedObject(doc);
        submodelBox.addItemListener(this);
    }

    /**
     * @return Namen des Modells, das dieser Browser darstellt
     */
    public final String getTitle() {
        return gdcoll.getName();
    }

    @Override
    public void modelOrSzenarioNameChanged(final GraphDocument source) {
        if (source != null) { //can be null -> prevent to add null
            submodelBox.removeItemListener(this);
            submodelBox.removeObject(source);
            submodelBox.addObject(source);
            submodelBox.setSelectedObject(source);
            submodelBox.addItemListener(this);
        }
    }

    /**
     *
     */
    private void setSelectedDoc() {
        GraphDocument currentDoc = getCurrentDoc();
        if (currentDoc != Static.getSelectedDoc()) {
            Static.setSelectedDoc(currentDoc);
        }
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        setSelectedDoc();
    }

    @Override
    public void focusGained(final FocusEvent e) {
        setSelectedDoc();
    }

    @Override
    public void focusLost(final FocusEvent e) {
    }

    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
        //ignore

    }

    @Override
    public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
        ViewPaneFrameComponentListener mainFrameDesktopPane = Static.getMainFrameDesktopPane();
        GraphDocument doc = getCurrentDoc();
        mainFrameDesktopPane.activateOrCreateGraphView(doc);
    }

    @Override
    public void popupMenuCanceled(final PopupMenuEvent e) {
        //ignore
    }

}
