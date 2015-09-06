/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableSumHandler;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;
import de.imise.util.clipboard.ContentExchangeListener;

/**
 * Abstrake Oberklasse für alle Panels zur Massendateneingabe von Kennzahlen und Verteilungsgewichten, d.h. für:
 * <p>
 * tool3lgm.graphtools.userfield.dialog.valueinput.GeneralUserFieldEditorPanel <br>
 * tool3lgm.graphtools.userfield.dialog.valueinput.DistributionWeigthEditorPanel <br>
 * tool3lgm.graphtools.userfield.dialog.valueinput.ModelVariableEditorPanel
 * 
 * @author fstephan
 */
public abstract class AbstractUserFieldEditorPanel extends JPanel {

    /**
     * Beinhaltet die darzustellende Tabelle
     */
    protected UserFieldTable table;

    /**
     * Der Dialog, der dieses Panel beinhaltet
     */
    private final UserFieldEditorDialog dialog;

    /**
     * das Reihen- und Spaltenelement an der aktuellen Mouse-Position im Table
     */
    private String[] elementsAtMousePointer;

    /**
     * Gibt wieder, ob sich Daten im {@link #table} geändert haben.
     */
    private boolean dataChanged;

    /**
     * Listener zur Überwachung von Änderungen in Modeldaten des {@link #table}s
     */
    private TableModelListener dataChangeListener;

    /**
     * Konstruktor
     * 
     * @param dialog Der Dialog, der dieses Panel beinhaltet
     */
    public AbstractUserFieldEditorPanel(final UserFieldEditorDialog dialog) {
        super(new GridBagLayout());
        this.dialog = dialog;
        init();

    }

    /**
     * Initialisiert den <code>table</code> und fügt ihn an dieses Panel an.
     */
    private void init() {
        table = initTable();
        add(table, constraintsForTable());
        initElementsAtMousePointerListener();

        /*
         * Listener, der Änderungen im Model des Tables erkennt. Der UserFieldEditorDialog, der dieses Panel beinhaltet, nutzt diese Informationen um
         * das richtige Verhalten bei Ok, Übernehmen und Abbrechen zu bestimmen.
         */
        dataChangeListener = new TableModelListener() {
            @Override
            public void tableChanged(final TableModelEvent e) {
                dataChanged = true;
            }
        };
        table.addTableModelListener(dataChangeListener);
        table.addContentExchangeListener(new ContentExchangeListener(table));
    }

    /**
     * Erstellt und fügt einen Listener an den Table an, der das Reihen- und Spaltenelement an der Mausposition erkennt und an Dialog übergibt.
     */
    private void initElementsAtMousePointerListener() {
        ListSelectionListener l = new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                AbstractUserFieldTableModel tableModel = (AbstractUserFieldTableModel) table.getModel();
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();
                String[] oldValue = elementsAtMousePointer;
                //wenn nicht selektiert ist -> löschen
                if (row == -1 || col == -1) {
                    elementsAtMousePointer = new String[] {
                            "", "", "", "",
                    };
                    firePropertyChange(UserFieldEditorDialog.PROPERTY_TABLE_SELECTION_CHANGED, oldValue, elementsAtMousePointer);
                    return;
                }
                //Zeilensumme
                String rowSum = UserFieldTableSumHandler.getFormattedRowSum(tableModel, row);
                //Spaltensumme
                String colSum = UserFieldTableSumHandler.getFormattedColumnSum(tableModel, col);

                //Zeilenname holen
                Vector<Object> rowIdentifiers = tableModel.getRowIdentifiers();
                Object rowIdentifier = rowIdentifiers.get(row);
                String rowIdentifierName = rowIdentifier.toString();

                //Spaltenname holen
                TableColumnModel tableColumnModel = table.getColumnModel();
                TableColumn column = tableColumnModel.getColumn(col);
                Object columnHeaderValue = column.getHeaderValue();
                String columnHeaderName = columnHeaderValue.toString();

                elementsAtMousePointer = new String[] {
                        rowIdentifierName, columnHeaderName, rowSum, colSum,
                };
                // Änderung dem Dialog mitteilen
                firePropertyChange(UserFieldEditorDialog.PROPERTY_TABLE_SELECTION_CHANGED, oldValue, elementsAtMousePointer);
            }

        };
        table.addSelectionListener(l);
    }

    /**
     * Sichert die spezielle Darstellung von <code>UserFieldTable</code>s. Es wird nicht der <code>UserFieldTable</code> selbst, sondern dass ihn
     * beinhaltende <code>JScrollPane</code> angefügt.
     * 
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object)
     */
    @Override
    public void add(final Component comp, final Object constraints) {

        /* Darstellung der Komponente, die den Table und das passende Layout enthält */
        if (comp instanceof UserFieldTable) {
            this.add(((UserFieldTable) comp).getLayoutContainer(), constraints);
        } else {
            super.add(comp, constraints);
        }
    }

    /**
     * Sichert das korrekte Entfernen des <code>table</code>s zu. Es wird nicht der Table, sondern das ihn beinhaltende <code>JScrollPane</code>
     * entfernt.
     * 
     * @see java.awt.Container#remove(java.awt.Component)
     */
    @Override
    public void remove(final Component comp) {
        if (comp instanceof UserFieldTable) {
            super.remove(((UserFieldTable) comp).getLayoutContainer());
        } else {
            super.remove(comp);
        }
    }

    /**
     * Methode verändert das Attribut <code>table</code> und löst ein Update aus. Das Model bzw. die EditCondition des <code>table</code>s wird auf
     * <code>newModel</code> bzw. <code>newController</code> gesetzt.
     * 
     * @param newModel
     * @param newController
     */
    protected void modifyTable(final AbstractUserFieldTableModel newModel, final UserFieldTableController newController) {
        table.removeTableModelListener(dataChangeListener);
        table.setModel(newModel, newController);
        table.activateFormatting();
        table.updateLayout();
        table.activateMultipleSelection();
        table.updateCells();
        table.addTableModelListener(dataChangeListener);
        table.revalidate();
        revalidate();
    }

    /**
     * Gibt den Dialog wieder, der dieses Panel enthält
     * 
     * @return dialog
     */
    public UserFieldEditorDialog getDialog() {
        return dialog;
    }

    /**
     * so zu überschreiben, dass Werte aus <code>table</code> im Model übernommen werden
     */
    public abstract void takeOver();

    /**
     * so zu überschreiben, dass <code>table</code> entsprechend des Anwendungsbereiches korrekt erzeugt wird
     */
    protected abstract UserFieldTable initTable();

    /**
     * so zu überschreiben, dass die Constraints für den <code>table</code> zurückgegeben werden
     */
    protected abstract Object constraintsForTable();

    /**
     * so zu überschreiben, dass <code>table</code> korrekt dargestellt wird
     */
    protected abstract void drawTable();

    /**
     * Führt im GraphDocument eine Selection_Changed Aktion aus
     * 
     * @param panel
     */
    protected void distributeSelectionChangedEvent() {
        dialog.getGraphDocument().distributeEvent(GraphDocument.SELECTION_CHANGED, getDialog().getTransactionID());
    }

    /**
     * Beendet das Editieren der aktuelle ausgewählten Zelle im Table. Methode muss vor dem Schließen des <code>UserFieldEditorDialog</code>s
     * aufgerufen werden, damit auch der aktuelle Wert der gerade editierten Zelle übernommen wird.
     */
    public void stopEditing() {
        table.stopEditing();
    }

    /**
     * Gibt wieder, ob sich Daten im {@link #table} geändert haben.
     * 
     * @return
     */
    public boolean dataChanged() {
        return dataChanged;
    }

    /**
     * Setzt das Attribut {@link #dataChanged} und bestimmt damit, ob dem {@link #dialog} angezeigt werden soll, dass Änderungen gemacht wurden, oder
     * nicht.
     * 
     * @param b
     */
    public void dataChanged(final boolean b) {
        dataChanged = b;
    }

    /**
     * Gibt das Reihen- und Spaltenelement an der aktuellen Mouse-Position im Table wieder.
     * 
     * @return {@link #elementsAtMousePointer}
     */
    public String[] getElementsAtMousePointer() {
        return elementsAtMousePointer;
    }

    /**
     * Liefert die {@link UserFieldDefinitions} der {@link GDCollection} des Dialoges dieses Panels
     * 
     * @return
     */
    public UserFieldDefinitions getUserFieldDefinitions() {
        return getDialog().getUserFieldDefinitions();
    }

}
