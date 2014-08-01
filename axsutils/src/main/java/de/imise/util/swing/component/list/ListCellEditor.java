package de.imise.util.swing.component.list;

import java.awt.Component;

import javax.swing.CellEditor;
import javax.swing.JList;

/**
 * Interface für den Editor einer {@link JList}.
 * 
 * @author fstephan
 *
 */
public interface ListCellEditor extends CellEditor {

	/**
	 * Gibt die Editorkomponente für Zellen einer {@link JList} wieder.
	 * 
	 * @param list
	 * 			Liste, die die zu editierende Zelle enthält
	 * @param value
	 * 			Initaler Wert des Editors
	 * @param isSelected
	 * 			Zelle ist selektiert
	 * @param index
	 * 			Index der Zelle innerhalb der Liste
	 */
    public Component getListCellEditorComponent(ExtendedJList list, Object value, boolean isSelected, int index);

}
