package de.imise.tool3lgm.graphtools.consistency.tableview;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.imise.tool3lgm.graphtools.consistency.checker.ConsistencyChecker;

/**
 * A Scrollpane that holds the consistency error table and the
 * corresponding {@link ConsistencyErrorTableGenerator}
 *
 * @author AXS (03.09.2020)
 */
public class ConsistencyErrorTablePane extends JScrollPane {

    private final ConsistencyErrorTableGenerator consistencyErrorTableGenerator;

    public ConsistencyErrorTablePane() {
        consistencyErrorTableGenerator = new ConsistencyErrorTableGenerator();
        ConsistencyChecker consistencyChecker = ConsistencyChecker.getConsistencyChecker();
        consistencyChecker.addPropertyChangeListener(consistencyErrorTableGenerator);
        JTable table = consistencyErrorTableGenerator.getTable();
        setViewportView(table);
    }

    public void dispose() {
        consistencyErrorTableGenerator.dispose();
    }
}
