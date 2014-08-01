/*
 * Created on 14.04.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.graphtools.analyse.context;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * @author AXS
 *
 * Dieser Renderer bewirkt, dass die Zelle mit dem Focus auch mit blauem Hintergrund
 * dargstellt wird. Standardverhalten ist, dass sie weiss bleibt.
 */
public class AnalyseRepositoryFrameTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * COMMENTME
	 */
	Component comp;
	
	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected || hasFocus) {
			super.setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			super.setForeground((getForeground() != null) ? getForeground() : table.getForeground());
			super.setBackground((getBackground() != null) ? getBackground() : table.getBackground());
		}
		setFont(table.getFont());
		setValue(value);
		return this;
	}

}
