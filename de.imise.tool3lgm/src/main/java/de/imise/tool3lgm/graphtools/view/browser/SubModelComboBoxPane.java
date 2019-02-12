/**
 *
 */
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
public class SubModelComboBoxPane extends SubModelsBrowser implements ItemListener {

    /**
     * Combobox, in der das aktuelle Teilmodell ausgewählt werden kann
     */
    private final AlphabeticalComboBox submodelBox;

    /**
     * @param gdcoll
     */
    public SubModelComboBoxPane(final GDCollection gdcoll) {
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
    public GraphDocument getSelectedDoc() {
        Object o = submodelBox.getSelectedObject();
        if (o instanceof GraphDocument) {
            return (GraphDocument) o;
        }
        return null;
    }

    @Override
    public void removeGraphDocument(final GraphDocument doc) {
        submodelBox.removeItem(doc);
    }

    @Override
    public void setSelectedDoc(final GraphDocument doc) {
        submodelBox.setSelectedObject(doc);
    }

    @Override
    public void updateTitle(final GraphDocument doc) {
        submodelBox.resort();
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
        update();
    }

    @Override
    public void update() {
        GraphDocument mySelDoc = getSelectedDoc();
        //Das muss extra abgefragt werden und darf nicht einfach mit der unteren if-Abfrage zusammen erledigt werden,
        //beim Starten des Baukastens der Baum sonst gar kein Doc hat, solange man nicht wenigstens 1 Mal das Teilmodell
        //wechselt.
        //Außerdem muss dieser Aufruf auch stattfinden, wenn das selDoc dasselbe ist, das der tree schon hat, damit der
        //Tree sich in jedem Fall neu aufbaut, falls sich die Option UserProperties.OPTION_ENABLE_EXPERT_MODE geändert
        //hat und der Baum einige Klassenknoten aus- oder einblenden soll.
        tree.setGraphDocument(mySelDoc);
        if (mySelDoc != Static.getSelectedDoc()) {
            Static.setSelectedDoc(mySelDoc, true);
        }
    }

}
