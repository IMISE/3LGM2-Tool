package de.imise.util.swing.component.list;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import de.imise.util.NamedObjectContainer;

import de.imise.util.Alphabetical;

/**
 * JList, die alle Items immer alphabetisch sortiert anzeigt.
 * @author AXS
 * created on 15.08.2007
 */
public class AlphabeticalJList extends JList {

	/**
	 * ListModel, das die alphabetische Einsortierung vornimmt
	 */
	private AlphabeticalListModel lm = new AlphabeticalListModel(null);

	/**
	 *
	 */
	public AlphabeticalJList() {
		super();
		setModel(lm);
	}

	/**
	 * @param objects
	 */
	public AlphabeticalJList(Collection<?> objects) {
		this();
		for (Iterator<?> it = objects.iterator(); it.hasNext();)
			lm.addElement(it.next());

	}

	/**
	 * @param listData
	 */
	public AlphabeticalJList(Object[] listData) {
		this();
		for (int i = 0; i < listData.length; i++)
			lm.addElement(listData[i]);

	}

	/**
	 * @param anObject
	 */
	public void addItem(Object anObject) {
		lm.addElement(anObject);
		revalidate();
	}

	/**
	 * Fügt zur Liste ein <code>NamedObjectContainer</code> hinzu mit <code>anObject</code>
	 * als Objekt und dem Anzeige-String <code>displayName</code>.
	 * 
	 * @param anObject
	 * @param displayName
	 */
	public void addItem(Object anObject, String displayName) {
		addItem(new NamedObjectContainer<Object>(anObject, displayName));
	}

	/**
	 * Liefert das selektierte <code>Object</code>.<br>
	 * Wenn ein <code>NamedObjectContainer</code> selektiert ist, wird von diesem die Methode
	 * <code>getObject()<code> aufgerufen und das Ergebnis zurück gegeben, sonst wird einfach
	 * das <code>Object</code> am selektierten Index zurückgegeben.
	 * 
	 * @return selektierte Objekt
	 */
	public Object getSelectedObject() {
		Object selectedObject = getSelectedValue();
		if (selectedObject instanceof NamedObjectContainer)
			return ((NamedObjectContainer<?>) selectedObject).getObject();
		return selectedObject;
	}

	/**
	 * Liefert die selektierten <code>Object</code>s.<br>
	 * Wenn <code>NamedObjectContainer</code> selektiert sind, wird von diesem die Methode
	 * <code>getObject()<code> aufgerufen und das Ergebnis zurück gegeben, sonst wird einfach
	 * das <code>Object</code> am selektierten Index zurückgegeben.
	 * 
	 * @return selektierte Objekt
	 */
	public Object[] getSelectedObjects() {
		Object[] selectedObjects = getSelectedValues();
		for (int i = 0; i < selectedObjects.length; i++) {
			if (selectedObjects[i] instanceof NamedObjectContainer)
				selectedObjects[i] = ((NamedObjectContainer<?>) selectedObjects[i]).getObject();
		}
		return selectedObjects;
	}

	/**
	 * 
	 */
	public void removeAllElements() {
		lm.removeAllElements();

	}

	/**
	 * Listmodel, das alle Einträge immer alphabetisch einsortiert. Wird beim Hinzufügen ein Index
	 * mit angegeben, an dem das neue Element eingefügt werden soll, wird dieser ignoriert.
	 * 
	 * @author AXS
	 */
	private class AlphabeticalListModel extends DefaultListModel {

		/**
		 * @param items
		 */
		public AlphabeticalListModel(Collection<?> items) {
			super();
			if (items != null)
				addAll(items);
		}

		/**
		 * @param items
		 */
		public void addAll(Collection<?> items) {
			for (Iterator<?> it = items.iterator(); it.hasNext();)
				addElement(it.next());
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListModel#add(int, java.lang.Object)
		 */
		@Override
		public void add(int index, Object element) {
			index = Alphabetical.getInsertPosition(toArray(), element);
			super.add(index, element);
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListModel#addElement(java.lang.Object)
		 */
		@Override
		public void addElement(Object obj) {
			add(0, obj);
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListModel#insertElementAt(java.lang.Object, int)
		 */
		@Override
		public void insertElementAt(Object obj, int index) {
			add(index, obj);
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListModel#set(int, java.lang.Object)
		 */
		@Override
		public Object set(int index, Object element) {
			Object o = remove(index);
			add(0, element);
			return o;
		}

		/* (non-Javadoc)
		 * @see javax.swing.DefaultListModel#setElementAt(java.lang.Object, int)
		 */
		@Override
		public void setElementAt(Object obj, int index) {
			set(index, obj);
		}
	}

}
