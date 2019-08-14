package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JScrollPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * @author Ich
 */
public class ComboBoxSubModelBrowser extends SubModelsBrowser implements ItemListener {

    /**
     * Combobox, in der das aktuelle Teilmodell ausgewählt werden kann
     */
    private final AlphabeticalComboBox submodelBox;

    /**
     * @param gdcoll
     */
    public ComboBoxSubModelBrowser(final GDCollection gdcoll) {
        super(gdcoll);
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

    @Override
    public void addGraphDocument(final GraphDocument doc) {
        submodelBox.addItem(doc);
        //das erste doc ist immer das Gesamtmodell -> erstes Doc in eigene Liste packen (also einen
        //Separator nach dem ersten einfügen), damit es immer oben steht (egal wie es heißt) und nur
        //die Elemente darunter (alle Szenarios) sortiert werden
        if (submodelBox.getItemCount() == 1) {
            submodelBox.addSeparator(false);
        }
    }

    @Override
    public GDCollection getCollection() {
        return gdcoll;
    }

    @Override
    public int getDocCount() {
        int count = 0;
        for (int i = 0; i < submodelBox.getItemCount(); i++) {
            if (submodelBox.getObjectAt(i) instanceof GraphDocument) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void removeGraphDocument(final GraphDocument doc) {
        submodelBox.removeItem(doc);
    }

    @Override
    GraphDocument getCurrentDoc() {
        Object o = submodelBox.getSelectedObject();
        if (o instanceof GraphDocument) {
            return (GraphDocument) o;
        }
        return null;
    }

    @Override
    void setCurrentDoc(final GraphDocument doc) {
        submodelBox.removeItemListener(this);
        submodelBox.setSelectedObject(doc);
        tree.setGraphDocument(doc);
        submodelBox.addItemListener(this);
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        GraphDocument currentDoc = getCurrentDoc();
        Static.setSelectedDoc(currentDoc);
    }

    @Override
    public void update() {
        submodelBox.removeItemListener(this);
        submodelBox.resort();
        submodelBox.addItemListener(this);
    }

}