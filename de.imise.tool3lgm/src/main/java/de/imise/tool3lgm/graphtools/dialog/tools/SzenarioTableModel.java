/*
 * Created on 11.12.2003 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog.tools;

import javax.swing.table.AbstractTableModel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.Szenario;

/**
 * @author Thomas Rudert Tabellenmodell zum auflisten der Szenarios (mit Titel und Beschreibung)
 *         eines GDCollection und auswahl einzelner Szenarios
 */
public class SzenarioTableModel extends AbstractTableModel {

    private final Boolean[] selections;
    private final GDCollection collection;
    private final String selectionColName;

    @Override
    public int getRowCount() {
        return collection.getNumberOfSzenarios();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    /**
     * erstellt das Tabellemodell
     * 
     * @param collection GDCollection mit den Szenarios
     * @param selectioColName Titel der Spalte zum Auswählen der Szenarios
     */
    public SzenarioTableModel(final GDCollection collection, final String selectioColName) {
        super();
        this.collection = collection;
        selectionColName = selectioColName;
        selections = new Boolean[collection.getNumberOfSzenarios()];
        for (int i = 0; i < collection.getNumberOfSzenarios(); i++) {
            selections[i] = new Boolean(false);
        }
    }

    @Override
    public String getColumnName(final int column) {
        switch (column) {
        case 0:
            return selectionColName;
        case 1:
            return Tool3lgmConstants.getResString("name");
        case 2:
            return Tool3lgmConstants.getResString("description");
        default:
            return null;
        }
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        switch (col) {
        case 0:
            if (row < getRowCount()) {
                return selections[row];
            }
            break;
        case 1:
            if (row < getRowCount()) {
                return collection.getSzenario(row).getTitle();
            }
            break;
        case 2:
            if (row < getRowCount()) {
                return collection.getSzenario(row).getDescription();
            }
            break;
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass(final int col) {
        switch (col) {
        case 0:
            return Boolean.class;
        case 1:
            return String.class;
        case 2:
            return String.class;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        if (col == 0 && row < getRowCount()) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(final Object aValue, final int row, final int column) {
        if (!isCellEditable(row, column)) {
            return;
        }
        selections[row] = (Boolean) aValue;
    }

    /**
     * selektiert alle Szenarios
     */
    public void selectAll() {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt(new Boolean(true), i, 0);
        }
    }

    /**
     * gibt die ausgewählten Seznarios zurück
     * 
     * @return Array mit den selektierten Szenarios
     */
    public Szenario[] getSelectedSzenarios() {
        int counter = 0;
        for (int i = 0; i < selections.length; i++) {
            if (selections[i].booleanValue()) {
                counter++;
            }
        }
        Szenario[] szenarios = new Szenario[counter];
        counter = 0;
        for (int i = 0; i < selections.length; i++) {
            if (selections[i].booleanValue()) {
                szenarios[counter++] = collection.getSzenario(i);
            }
        }
        return szenarios;
    }

}
