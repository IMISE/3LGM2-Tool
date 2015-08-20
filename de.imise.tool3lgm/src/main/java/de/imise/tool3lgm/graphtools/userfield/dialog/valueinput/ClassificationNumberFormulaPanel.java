/*
 * Created on 01.04.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.GeneralUserFieldTableModel;

/**
 * Panel zur Darstellung von berechneten Kennzahlen
 * 
 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.GeneralUserFieldEditorPanel
 * @author fstephan
 */
public class ClassificationNumberFormulaPanel extends GeneralUserFieldEditorPanel {

    /**
     * zeigt an, ob sich Daten geändert haben und drawTable aufgerufen werden muss
     */
    private boolean needsRedraw;

    /**
     * @param dialog
     */
    public ClassificationNumberFormulaPanel(final UserFieldEditorDialog dialog) {
        super(dialog, UserField.Style.CLASSIFICATION_NUMBER_FORMULA);
    }

    @Override
    protected void setNodeBoxContent() {

        super.setNodeBoxContent();

        GraphDocument doc = getDialog().getGraphDocument();
        GDCollection gdcoll = doc.getCollection();
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();

        // Hier werden jetzt die Kanten hinzugefügt, für die Kennzahlformeln definiert sind. 
        // Das passiert aber nur, wenn die <code>setNodeBoxContent()</code> 
        // implizit durch das super() des <code>ClassificationNumberFormulaPanel</code> aufgerufen wird.
        // Die lokale <code>isNodeBoxContent()</code> hier lässt das hinzufügen von Kanten nicht zu - die überschriebene Methode lässt das hinzufügen zu.  
        for (int i = 0; i < ModelConstants.ALL_EDGES.length; i++) {

            // Falls Verteilungsgewichte exisitieren, füge Kantenklasse ein
            if (isNodeBoxContent(ModelConstants.ALL_EDGES[i], definitions)) {

                //if (definitions.getUserFieldCount(ModelConstants.ALL_EDGES[i]) > 0) {
                nodeBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullForwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
                nodeBox.addItem(ModelConstants.ALL_EDGES[i], ModelConstants.getFullBackwardMetaAssociationName(ModelConstants.ALL_EDGES[i]));
            }
            // sonst, füge nicht ein
        }
    }

    @Override
    protected UserFieldTable initTable() {
        UserFieldTableLayout uftl = UserFieldTableLayout.getLayoutForClassificationNumberFormulaTable();
        UserFieldTable table = new UserFieldTable(uftl);
        return table;
    }

    @Override
    protected void takeOver() {
        needsRedraw = true;
    }

    @Override
    protected void drawTable() {
        table.removeFromLayoutContainer();
        if (nodeBox.getSelectedObject() == null || !(nodeBox.getSelectedObject() instanceof Class)) {
            return;
        }
        Class<? extends ModelElement> selectedClass = (Class<? extends ModelElement>) nodeBox.getSelectedObject();
        GraphDocument doc = getDialog().getGraphDocument();
        GeneralUserFieldTableModel uftm = new GeneralUserFieldTableModel(doc, selectedClass, typePane.showTopLevel(), typePane.showInner(), typePane.showLeafs(), UserField.Style.CLASSIFICATION_NUMBER_FORMULA);
        UserFieldTableController tec = UserFieldTableController.getNewClassificationNumberFormulaTableController(uftm);
        super.modifyTable(uftm, tec);
    }

    /**
     * Falls sich Daten geändert haben, wird der Table neu gezeichnet
     * 
     * @see javax.swing.JComponent#setVisible(boolean)
     */
    @Override
    public void setVisible(final boolean b) {
        super.setVisible(b);
        if (b == true && needsRedraw == true) {
            needsRedraw = false;
            drawTable();
        }
    }

    /**
     * @see GeneralUserFieldEditorPanel#setActionsForNodeBox()
     * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.GeneralUserFieldEditorPanel#setActionsForNodeBox()
     */
    @Override
    protected void setActionsForNodeBox() {
        final UserFieldEditorPanel pane = this;
        nodeBox.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                stopEditing();
                drawTable();
                pane.distributeSelectionChangedEvent();
            }
        });
    }

    @Override
    public void stopEditing() {
        // tue nichts, weil keine Zelle editierbar ist
    }
}
