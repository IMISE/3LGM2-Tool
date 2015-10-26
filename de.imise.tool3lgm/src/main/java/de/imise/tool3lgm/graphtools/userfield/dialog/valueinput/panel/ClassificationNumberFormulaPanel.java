/*
 * Created on 01.04.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout.UserFieldTableLayout;

/**
 * Panel zur Darstellung von berechneten Kennzahlen
 * 
 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.GeneralUserFieldEditorPanel
 * @author fstephan
 */
public class ClassificationNumberFormulaPanel extends GeneralUserFieldEditorPanel {

    /**
     * zeigt an, ob sich Daten geändert haben und drawTable aufgerufen werden muss
     */
    private boolean needsRedraw;

    /**
     * @param dialog
     * @param name
     */
    public ClassificationNumberFormulaPanel(final UserFieldEditorDialog dialog, final String name) {
        super(dialog, UserField.Style.CLASSIFICATION_NUMBER_FORMULA, name);
    }

    @Override
    protected UserFieldTable initTable() {
        UserFieldTableLayout uftl = new UserFieldTableLayout();
        UserFieldTable table = new UserFieldTable(uftl);
        return table;
    }

    @Override
    public void takeOver() {
        needsRedraw = true;
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
     * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.GeneralUserFieldEditorPanel#setActionsForNodeBox()
     */
    @Override
    protected void setActionsForNodeBox() {
        final AbstractUserFieldEditorPanel pane = this;
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
