package de.imise.tool3lgm.graphtools.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class ResultPanel extends JPanel {

    /**
     * 
     */
    private class MyTable extends JTable {

        /**
         * @param contents
         * @param colheads
         */
        public MyTable(final String[][] contents, final String[] colheads) {
            super(contents, colheads);
        }

        @Override
        public boolean isCellEditable(final int col, final int row) {
            return false;
        }
    }

    /**
     * 
     */
    public ResultPanel() {
        super();
        this.setSize(500, 250);
        setLayout(new GridLayout(1, 1));
    }

    /**
     * @param v
     */
    public void config(final ArrayList<ElementContainer> v) {
        // JPanel panel = new JPanel();
        String[] colheads = {
                getResString("bez"),
                getResString("description")
        };
        String[][] contents = new String[v.size()][2];
        for (int i = 0; i < v.size(); i++) {
            ModelElement me = v.get(i).getElement();
            contents[i][0] = me.getName();
            contents[i][1] = me.getDescription();
        }
        MyTable table = new MyTable(contents, colheads);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        // table.setCellEditor(new MyCellEditor(new JCheckBox()));
        this.add(new JScrollPane(table));
    }
}