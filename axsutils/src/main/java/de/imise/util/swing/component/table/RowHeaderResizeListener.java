package de.imise.util.swing.component.table;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * Listener, der das Resizing des rowHeaders ermöglicht und überwacht
 * 
 * @author fstephan
 */
public class RowHeaderResizeListener extends MouseAdapter {
	
	/** 
	 * MousePointer - Verschiebung
	 * <br>
	 * Sorgt dafür, dass beim Eintreten eines Resize-Ereignisses der rowHeader 
	 * das source-Objekt der MouseEvents ist.
	 */
	private static final int MOUSE_POINT_X_PITCH = 1;
	
	/** der RowHeader des Tables */
	private JList header;
	
	/** das ScrollPane, das den Table und den Header enthält */
	private JScrollPane pane;
	
	/** gibt, wieder ob die Größe des RowHeaders gerade geändert wird */ 
	private boolean isResizing = false;
	
	/** 
	 * der MouseCursor 
	 * <br> ändert sich, wenn er sich auf dem Rand zwischen RowHeader und Table befindet 
	 */
	private Cursor cursor;
	
	/** 
	 * Gibt wieder, ob sich der {@link #cursor} auf dem Rand zwischen Table und
	 * RowHeader befindet.
	 */
	private boolean canResize = false;
	
	/**
	 * Konstruktor
	 * 
	 * @param sp
	 * 		{@link ScrollPane}, das den {@link #header} als <code>RowHeaderViewportView</code> beinhaltet
	 */
	public RowHeaderResizeListener(JScrollPane sp) {
		this.pane = sp;
		this.header = (JList) sp.getRowHeader().getView();
	}
	
	/**
	 * Verändert den Cursor-Typ in Abhängigkeit seiner Position.
	 * Ist er über dem Rand zwischen rowHeader und table, 
	 * wechselt er in die resize-Dartsellung, sonst wir der Standard-Cursor angezeigt.
	 * @param e
	 * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		
		if(isResizing == true)
			return;
		
		int mouseX = e.getX() + MOUSE_POINT_X_PITCH;
		int headerX = header.getX() + header.getWidth();
		Rectangle visibleCells = header.getCellBounds(header.getFirstVisibleIndex(), header.getLastVisibleIndex());
		
		// Cursor über dem Rand von rowHeader und table
		if((mouseX == headerX || mouseX+1 == headerX) && 
				e.getSource() == header && 
				e.getY() <= (visibleCells.y + visibleCells.height)) {
			
			canResize = true;
			cursor = new Cursor(Cursor.E_RESIZE_CURSOR);
		}
		else { // sonst
			canResize = false;
			cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		}
		
		if (cursor.getType() != pane.getCursor().getType())
			pane.setCursor(cursor);
	}
	
	/**
	 * Führt den resize aus.
	 * Unterbindet das Verkleinern, falls die minimale Breite unterschritten wird.
	 * Unterbindet das Vergrößern, falls die Breite des rowHeaders fast die Fensterbreite
	 * erreicht.
	 * 
	 * @param e
	 * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		
		if(canResize == false)
			return;
		
		isResizing = true;
		
		int mouseX = e.getX() + MOUSE_POINT_X_PITCH;
		
		/*
		 * Verhindert, dass der rowHeader seine minimale Größe unterschreiten
		 * bzw. die Fenstergröße überschreiten kann
		 */
		JViewport p = pane.getViewport();
		if (mouseX <= header.getMinimumSize().width || mouseX >= p.getWidth() + p.getX() -10)
			return;
		
		header.setFixedCellWidth(mouseX);
	}
	
	/**
	 * Zeigt das Ende eines Resize-Vorganges an
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		isResizing = false;
	}

}
