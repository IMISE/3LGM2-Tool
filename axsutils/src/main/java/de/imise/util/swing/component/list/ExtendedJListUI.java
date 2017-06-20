package de.imise.util.swing.component.list;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

/**
 * UI Klasse für die {@link ExtendedJList}.
 * 
 * @author fstephan
 */
public class ExtendedJListUI extends BasicListUI {

	public static ComponentUI createUI(JComponent c) {
		return new ExtendedJListUI();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#getCellBounds(javax.swing.JList, int, int)
	 */
	@Override
	public Rectangle getCellBounds(JList list, int index1, int index2) {
		maybeUpdateLayoutState();
		return list.getCellBounds(index1, index2);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#getPreferredSize(javax.swing.JComponent)
	 */
	@Override
	public Dimension getPreferredSize(JComponent c) {
		maybeUpdateLayoutState();

		int lastRow = list.getModel().getSize() - 1;
		if (lastRow < 0) {
			return new Dimension(0, 0);
		}
		Insets insets = list.getInsets();
		int width = cellWidth + insets.left + insets.right;
		int height;
		Rectangle bounds = getCellBounds(list, lastRow, lastRow);
		if (bounds != null)
			height = bounds.y + bounds.height + insets.bottom;
		else
			height = 0;
		return new Dimension(width, height);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#locationToIndex(javax.swing.JList, java.awt.Point)
	 */
	@Override
	public int locationToIndex(JList list, Point location) {
		return list.locationToIndex(location);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#paint(java.awt.Graphics, javax.swing.JComponent)
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		paintImpl(g, c);
	}

	/**
	 * Zeichnet die Liste.
	 * <p>
	 * Funktioniert analog der Methode {@link BasicListUI#paintImpl(Graphics, JComponent)}
	 * und wurde hier neu eingeführt, da die Methode der Superklasse <code>private</code> ist.<br>
	 * {@link #paint(Graphics, JComponent)} wurde so überschrieben, dass nur diese Methode aufgerufen wird.
	 * 
	 * @param g
	 * @param c
	 */
	protected void paintImpl(Graphics g, JComponent c) {

		ListCellRenderer renderer = list.getCellRenderer();
		ListModel dataModel = list.getModel();
		ListSelectionModel selModel = list.getSelectionModel();

		if ((renderer == null) || dataModel.getSize() == 0) {
			return;
		}

		Rectangle paintBounds = g.getClipBounds();
		int maxY = paintBounds.y + paintBounds.height;
		int leadIndex = list.getLeadSelectionIndex();
		ExtendedJList mList = (ExtendedJList) list;
		int row = mList.convertYToRow(paintBounds.y);
		int rowCount = mList.getRowCount();
		Rectangle rowBounds = getCellBounds(list, row, row);
		while (row < rowCount && rowBounds.y < maxY) {
			rowBounds = getCellBounds(list, row, row);
			g.setClip(rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height);
			g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width, paintBounds.height);
			paintCell(g, row, rowBounds, renderer, dataModel, selModel, leadIndex);
			row++;
		}
		
		rendererPane.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#maybeUpdateLayoutState()
	 */
	@Override
    protected void maybeUpdateLayoutState() {
		super.maybeUpdateLayoutState();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#updateLayoutState()
	 */
	@Override
	protected void updateLayoutState()
    {
        /* If both JList fixedCellWidth and fixedCellHeight have been
         * set, then initialize cellWidth and cellHeight, and set
         * cellHeights to null.
         */

        int fixedCellHeight = list.getFixedCellHeight();
        int fixedCellWidth = list.getFixedCellWidth();

        cellWidth = (fixedCellWidth != -1) ? fixedCellWidth : -1;

        if (fixedCellHeight != -1) {
            cellHeight = fixedCellHeight;
        }
        else {
            cellHeight = -1;
        }

        /* If either of  JList fixedCellWidth and fixedCellHeight haven't
         * been set, then initialize cellWidth and cellHeights by
         * scanning through the entire model.  Note: if the renderer is
         * null, we just set cellWidth and cellHeights[*] to zero,
         * if they're not set already.
         */

        if ((fixedCellWidth == -1) || (fixedCellHeight == -1)) {

            ListModel dataModel = list.getModel();
            int dataModelSize = dataModel.getSize();
            ListCellRenderer renderer = list.getCellRenderer();

            if (renderer != null) {
                for(int index = 0; index < dataModelSize; index++) {
                    Object value = dataModel.getElementAt(index);
                    Component c = renderer.getListCellRendererComponent(list, value, index, false, false);
                    rendererPane.add(c);
                    Dimension cellSize = c.getPreferredSize();
                    if (fixedCellWidth == -1) {
                        cellWidth = Math.max(cellSize.width, cellWidth);
                    }
                }
            }
            else {
                if (cellWidth == -1) {
                    cellWidth = 0;
                }
            }
        }
    }

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#convertYToRow(int)
	 */
	@Override
    protected int convertYToRow(int y0) {
	    return ((ExtendedJList)list).convertYToRow(y0);
    }

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#createMouseInputListener()
	 */
	@Override
    protected MouseInputListener createMouseInputListener() {
		final MouseInputListener handler = super.createMouseInputListener();
		
		MouseInputListener mil = new MouseInputListener() {

			@Override
            public void mouseClicked(MouseEvent e) {handler.mouseClicked(e);}

			@Override
            public void mouseEntered(MouseEvent e) {handler.mouseEntered(e);}

			@Override
            public void mouseExited(MouseEvent e) {handler.mouseExited(e);}

			@Override
            public void mousePressed(MouseEvent e) {handler.mousePressed(e);}

			@Override
            public void mouseReleased(MouseEvent e) {handler.mouseReleased(e);}

			@Override
            public void mouseDragged(MouseEvent e) { 
				// tue nichts --> Dragging wird nicht registriert
			}

			@Override
            public void mouseMoved(MouseEvent e) {handler.mouseMoved(e);}
		};
		return mil;
    }

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#getRowHeight(int)
	 */
	@Override
    protected int getRowHeight(int row) {
	    return ((ExtendedJList)list).getCellHeight(row, true);
    }

	/*
	 * (non-Javadoc)
	 * @see javax.swing.plaf.basic.BasicListUI#paintCell(java.awt.Graphics, int, java.awt.Rectangle, javax.swing.ListCellRenderer, javax.swing.ListModel, javax.swing.ListSelectionModel, int)
	 */
	@Override
	protected void paintCell(Graphics g, int row, Rectangle rowBounds, ListCellRenderer cellRenderer, ListModel dataModel, ListSelectionModel selModel, int leadIndex) {
		Object value = dataModel.getElementAt(row);
		boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
		boolean isSelected = selModel.isSelectedIndex(row);

		ExtendedJList mList = (ExtendedJList) list;

		if (mList.isEditing() && row == mList.getEditingIndex()) {
			Component editorComponent = mList.getEditorComponent();
			editorComponent.setBounds(rowBounds);
			editorComponent.validate();
		} else {
			Component rendererComponent = cellRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);
			int cx = rowBounds.x;
			int cy = rowBounds.y;
			int cw = rowBounds.width;
			int ch = rowBounds.height;
			rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
		}
	}
}
	
	
