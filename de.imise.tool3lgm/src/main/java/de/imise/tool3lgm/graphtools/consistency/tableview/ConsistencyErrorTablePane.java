package de.imise.tool3lgm.graphtools.consistency.tableview;

import static de.imise.tool3lgm.gui.GUIFocusContextManager.SET_FOCUS_TO_CLICKED_COMPONENT_MOUSE_LISTENER;

import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * A Scrollpane that holds the consistency error table and the corresponding
 * {@link ConsistencyErrorTableGenerator}
 *
 * @author AXS (03.09.2020)
 */
public class ConsistencyErrorTablePane extends JScrollPane {

    private final ConsistencyErrorTableGenerator consistencyErrorTableGenerator;

    public ConsistencyErrorTablePane() {
        consistencyErrorTableGenerator = new ConsistencyErrorTableGenerator();
        JTable table = consistencyErrorTableGenerator.getTable();
        setViewportView(table);
        addMouseListener(SET_FOCUS_TO_CLICKED_COMPONENT_MOUSE_LISTENER);
        table.addMouseListener(SET_FOCUS_TO_CLICKED_COMPONENT_MOUSE_LISTENER);
    }

    public void dispose() {
        consistencyErrorTableGenerator.dispose();
    }

}
