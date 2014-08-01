package de.imise.util.swing.component.list;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.SizeSequence;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * {@link JList} Implementation mit zwei neuen Funktionen:
 * 
 * <li> Editierbarkeit der Zellen
 * <li> Höhe für jede einzelne Zelle einstellbar
 * 
 * @author fstephan
 *
 */
public class ExtendedJList extends JList implements CellEditorListener, ListDataListener {

	/**
	 * @see #getUIClassID
	 * @see #readObject
	 */
	private static final String uiClassID = "ExtendedJListUI";

	/*
	 * zugehörige UI Klasse festlegen
	 */
	static {
		UIManager.put(uiClassID, ExtendedJListUI.class.getName());
	}

	/**
	 * Beinhaltet die Höhe der Zellen
	 * @see SizeSequence
	 */
	private CellHeightModel cellHeightModel;
	
	/** Editor der Zellen */
	private ListCellEditor cellEditor;
	
	/** Index der Zelle, die gerade editiert wird. */
	private transient int editingIndex = -1;
	
	/** Komponente, die in der {@link ExtendedJList} als Editor dargestellt wird */
	private transient Component editorComponent;

	/**
	 * @param model
	 * 			Modell der Werte für die Liste
	 * @param editor
	 * 			Editor für das Ändern der Werte
	 */
	public ExtendedJList(EditableListModel model, ListCellEditor editor) {
		super(model);
		model.addListDataListener(this);
		this.cellEditor = editor;
		setDragEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JList#getModel()
	 */
	@Override
	public EditableListModel getModel() {
		return (EditableListModel) super.getModel();
	}

	/**
	 * Setzt die Höhe der Zellen entsprechend der Werte von {@link CellHeightModel#getHeight(int)}
	 * @param cellHeightModel
	 */
	public void setCellHeightModel(CellHeightModel cellHeightModel) {
		this.cellHeightModel = cellHeightModel;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JList#setFixedCellHeight(int)
	 */
	@Override
	public void setFixedCellHeight(int height) {
		super.setFixedCellHeight(height);
		setCellHeightModel(null);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JList#getPreferredScrollableViewportSize()
	 */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		if (getLayoutOrientation() != VERTICAL) {
			return getPreferredSize();
		}
		Insets insets = getInsets();
		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;

		int visibleRowCount = getVisibleRowCount();
		int fixedCellWidth = getFixedCellWidth();
		int fixedCellHeight = getFixedCellHeight();

		if ((fixedCellWidth > 0) && (fixedCellHeight > 0)) {
			int width = fixedCellWidth + dx;
			int height = (visibleRowCount * fixedCellHeight) + dy;
			return new Dimension(width, height);
		} else if (getModel().getSize() > 0) {
			int width = getPreferredSize().width;
			int height = 0;
			Rectangle r = getCellBounds(0, 0);

			if (r != null) {
				for (int row = 0; row < getModel().getSize(); row++) {
					// Berücksichtigt die unterschiedlichen Zellhöhen
					height += getCellHeight(row, true);
				}
			} else {
				height = 1;
			}
			return new Dimension(width, height);
		} else {
			fixedCellWidth = (fixedCellWidth > 0) ? fixedCellWidth : 256;
			fixedCellHeight = (fixedCellHeight > 0) ? fixedCellHeight : 16;
			return new Dimension(fixedCellWidth, fixedCellHeight * visibleRowCount);
		}
	}

	/**
	 * Gibt die Höhe der Zelle am spezifizierten Index wieder
	 * @param index
	 * @return
	 */
	public int getCellHeight(int index, boolean includeMargin) {
		int height = cellHeightModel == null ? getFixedCellHeight() : cellHeightModel.getHeight(index);
		return includeMargin ? (height + getRowMargin(index)) : height;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JList#getCellBounds(int, int)
	 */
	@Override
	public Rectangle getCellBounds(int index0, int index1) {
		Rectangle cellBounds = new Rectangle();
		Point p0 = indexToLocation(index0);
		Point p1 = indexToLocation(index1);
		cellBounds.x = p0.x;
		cellBounds.y = p0.y;
		cellBounds.width = getFixedCellWidth();
		cellBounds.height = p1.x;
		if (cellHeightModel == null)
			cellBounds.height += (index1 - (index0 - 1)) * getFixedCellHeight();
		else {
			for (int i = index0; i <= index1; i++)
				cellBounds.height += getCellHeight(i, true);
		}
		return cellBounds;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#processMouseEvent(java.awt.event.MouseEvent)
	 */
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int index = locationToIndex(e.getPoint());
		if (e.getID() == MouseEvent.MOUSE_PRESSED && isCellEditable(index)) {
			editCellAt(index);
			if (cellEditor.shouldSelectCell(e) && !editorComponent.hasFocus())
				editorComponent.requestFocus();
		}

		/*
		 * Erneutes Senden des MouseEvents an die Editorkomponente, da diese beim ersten
		 * Eingang des Events zunächst erst initialisiert wird
		 */
		if (editorComponent != null) {
			editorComponent.requestFocus();
			MouseEvent e2 = SwingUtilities.convertMouseEvent(this, e, editorComponent);
			editorComponent.dispatchEvent(e2);
		}
	}

	/**
	 * Gibt wieder, ob die Zelle am spezifizierten Index editierbar ist.
	 * @param index
	 * @see EditableListModel#isCellEditable(int)
	 */
	public boolean isCellEditable(int index) {
		return getModel().isCellEditable(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JList#getUIClassID()
	 */
	@Override
	public String getUIClassID() {
		return uiClassID;
	}
	
	/**
	 * Gibt wieder, ob im Moment eine Zelle editiert wird
	 * @return
	 */
	public boolean isEditing() {
		return editingIndex > -1;
	}

	/**
	 * Gibt den Index der Zelle zurück, die im Moment editiert wird.
	 * @return <code>-1</code>, wenn keine Zelle editiert wird
	 */
	public int getEditingIndex() {
		return editingIndex;
	}

	/**
	 * Löst das Entfernen des Editors von der Zelle aus.
	 * @see javax.swing.event.CellEditorListener#editingCanceled(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void editingCanceled(ChangeEvent e) {
		removeEditor();
	}

	/**
	 * Die {@link ExtendedJList} ist nur für die {@link JList#VERTICAL} Layout Orienation definiert.
	 */
	@Override
	@Deprecated
	public void setLayoutOrientation(int layoutOrientation) {
		super.setLayoutOrientation(JList.VERTICAL);
	}

	/**
	 * Löst das Editieren der Zelle am spezifizierten Index aus.
	 * @param index
	 * @return
	 */
	public boolean editCellAt(int index) {
		if (cellEditor != null && !cellEditor.stopCellEditing()) {
			return false;
		}

		editorComponent = prepareEditor(cellEditor, index);
		if (editorComponent == null) {
			removeEditor();
			return false;
		}
		editorComponent.setBounds(getCellBounds(index, index));
		add(editorComponent);
		editorComponent.validate();
		editorComponent.repaint();
		editingIndex = index;
		return true;
	}

	/**
	 * Löst das Beenden des Editors aus. 
	 * <p>
	 * Der neue Wert wird ins Modell übernommen und der Editor von der Zelle gelöst.
	 */
	@Override
	public void editingStopped(ChangeEvent e) {
		if (editingIndex >= 0 && editingIndex < getModel().getSize()) {
			Object value = cellEditor.getCellEditorValue();
			getModel().setElementAt(value, editingIndex);
			removeEditor();
		}
	}

	/** Entfernt die {@link #editorComponent} von der Liste */
	private void removeEditor() {
		cellEditor.removeCellEditorListener(this);
		remove(editorComponent);
		editorComponent = null;
		Rectangle dirtyRegion = getCellBounds(editingIndex, editingIndex);
		repaint(dirtyRegion);
		editingIndex = -1;
	}

	/**
	 * Gibt die in dieser {@link ExtendedJList} dargestellte grafische Komponente des aktuellen
	 * Editors wieder.
	 * @return <code>null</code>, wenn keine Zell editiert wird
	 */
	public Component getEditorComponent() {
		return editorComponent;
	}

	/**
	 * Gibt den Abstand zwischen der Zelle am spezifizierten Index und der daraufolgenden Zelle wieder.
	 * @param index
	 * @return default:<code>0</code>
	 */
	public int getRowMargin(int index) {
		return 0;
	}

	/**
	 * Veranlasst den Editor die grafische Komponente für die Zelle am spezifizierten Index zu erzeugen.<br>
	 * Die Funktion kann zum Beispiel verwendet werden, um bereits vor dem Editieren Informationen über die
	 * darszustellende Komponente zu erhalten (Layout, etc.).
	 * 
	 * @param editor
	 * @param index
	 * @return
	 */
	public Component prepareEditor(ListCellEditor editor, int index) {
		Component c = editor.getListCellEditorComponent(this, getModel().getElementAt(index), isSelectedIndex(index), index);
		return c;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JList#indexToLocation(int)
	 */
	@Override
	public Point indexToLocation(int index) {
		if (index >= getModel().getSize())
			return null;

		Point p = new Point();
		if (cellHeightModel == null) {
			p.y = index * getFixedCellHeight();
			for (int i = 0; i < index; i++) {
				p.y += getRowMargin(i);
			}
		} else {
			for (int i = 0; i < index; i++) {
				p.y += cellHeightModel.getHeight(i);
				p.y += getRowMargin(i);
			}
		}

		if (getComponentOrientation().isLeftToRight())
			p.x = getInsets().left;
		else
			p.x = getWidth() - getInsets().right;

		p.y += getInsets().top;
		return p;
	}
	
	/**
	 * Wandelt die y-Koordinate in den dazugehörigen Zellindex um. 
	 * @param y0
	 * @return -1, wenn y0 ausserhalb der Koordinaten dieser {@link ExtendedJList} liegt.
	 */
	protected int convertYToRow(int y0) {
		int size = getModel().getSize();

		if (size <= 0) {
			return -1;
		}
		Insets insets = getInsets();

		int y = insets.top;
		int row = 0;

		int i;
		for (i = 0; i < size; i++) {
			if ((y0 >= y) && (y0 < y + getCellHeight(i, true))) {
				return row;
			}
			y += getCellHeight(i, true);
			row += 1;
		}
		return i - 1;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JList#locationToIndex(java.awt.Point)
	 */
	@Override
	public int locationToIndex(Point location) {
		for (int i = 0; i < getModel().getSize(); i++) {
			Rectangle r = getCellBounds(i, i);
			if (r.contains(location))
				return i;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void contentsChanged(ListDataEvent e) {
		paint(e.getIndex0(), e.getIndex1());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void intervalAdded(ListDataEvent e) {
		paint(e.getIndex0(), e.getIndex1());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
	 */
	@Override
	public void intervalRemoved(ListDataEvent e) {
		paint(e.getIndex0(), e.getIndex1());
	}
	
	/** Zeichnet alle Zellen innerhalb der Indices */
	private void paint(int fromIndex, int toIndex) {
		repaint(getCellBounds(fromIndex, toIndex));
	}
	
	/**
	 * Gibt die Anzahl der Zellen wieder
	 * @return
	 */
	public int getRowCount() {
		return getModel().getSize();
	}
}
