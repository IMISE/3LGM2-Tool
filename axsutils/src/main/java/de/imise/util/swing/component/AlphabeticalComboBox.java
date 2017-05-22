package de.imise.util.swing.component;

import java.awt.Component;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSeparator;

import de.imise.util.Alphabetical;
import de.imise.util.NamedObjectContainer;

/**
 * Combobox, die eine oder mehrere getrennte Listen von Items jeweils immer alphabetisch
 * sortiert anzeigt.<br>
 * Die Sortierung erfolgt über die Klasse <code>Alphabetical</code>.<br>
 * <br>
 * Alle Einträge der Auswahlliste außer Separatoren lassen sich um eine beliebige Anzahl
 * von Leerzeichen nach rechts einrücken.<br>
 * Separatoren können wahlweise unsichtbar oder als nicht selektierbare Einträge mit einem
 * eigenen Label in die Liste eingefügt werden.
 *
 * @see <code>Alphabetical</code>
 * @author AXS
 *         created on 15.08.2007
 */
public class AlphabeticalComboBox extends JComboBox {

    /**
     * Referenz auf den Vector der Einträge der Combobox. (Der Parent-Vector hat
     * die Sichtbarkeit 'package').
     */
    private final Vector<Object> items = new Vector<Object>();

    /**
     * Eintrag für eine Leerzeile, um "keine Auswahl" zu treffen.
     */
    public static final String EMPTY_VALUE_ENTRY = " ";

    /**
     * Existiert in Combobox ein Leerfeld ja/nein
     */
    private boolean nullAble = false;
    /**
     * Index, ab dem neue Elemente alphabetisch einsortiert werden. Alle Einträge davor bleiben in
     * ihrer Reihenfolge unverändert.
     */
    int newListStartIndex = 0;

    /**
     * Zuletzt selektierter Index.<br>
     * Da Separatoren nicht selektierbar sein sollen, wird sich in dieser Variable der Index
     * gemerkt, der vor der Selektion eines Separators selektiert war und dann die Selektion
     * auf diesen zurückgesetzt.
     */
    private int lastCorrectSelectedIndex = -1;

    /**
     * Legt eine neue ComboBox an, die ihre Einträge alphabetisch sortiert.<br>
     */

    public AlphabeticalComboBox() {
        this(0);
    }

    /**
     * Legt eine neue ComboBox an, die ihre Einträge alphabetisch sortiert.<br>
     *
     * @param Leerfeld?
     */
    public AlphabeticalComboBox(final boolean nullAble) {
        this(0);
        setNullAble(nullAble);
    }

    /**
     * Legt eine neue ComboBox an, die ihre Einträge alphabetisch sortiert.<br>
     * Die Auswahlliste wird mit den Elementen der übergebenen Liste gefüllt.
     *
     * @param objects
     *            Initiale Liste von Elementen in der Auswahlliste
     */
    public AlphabeticalComboBox(final Collection<Object> objects) {
        this(objects, 0);
    }

    /**
     * Legt eine neue ComboBox an, die ihre Einträge alphabetisch sortiert.<br>
     * Allen Einträge der Auswahlliste, die hinzugefügt werden, werden in der
     * Darstellung der Auswahlliste <code>shift</code> Leerzeichen vorangestellt.
     *
     * @param shift
     *            Anzahl der Leerzeichen, die vor jedem Listeneintrag dargestellt werden sollen
     */
    public AlphabeticalComboBox(final int shift) {
        super();
        setModel(new DefaultComboBoxModel(items));
        setRenderer(new MyRenderer(shift));
        //30 Zeilen solten eigentlich immer darstellbar sein
        setMaximumRowCount(30);
    }

    /**
     * Legt eine neue ComboBox an, die ihre Einträge alphabetisch sortiert.<br>
     * Die Auswahlliste wird mit den Elementen der übergebenen Liste gefüllt.
     * Allen Einträge der Auswahlliste, die hinzugefügt werden, werden in der
     * Darstellung der Auswahlliste <code>shift</code> Leerzeichen vorangestellt.
     *
     * @param objects
     *            Initiale Liste von Elementen in der Auswahlliste
     * @param shift
     *            Anzahl der Leerzeichen, die vor jedem Listeneintrag dargestellt werden sollen
     */
    public AlphabeticalComboBox(final Collection<Object> objects, final int shift) {
        this(shift);
        items.addAll(objects);
        Alphabetical.sort(items);
    }

    /**
     * Sortiert alle Listen neu
     */
    public void resort() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof SeparatorItem) {
                continue;
            }
            int j = i + 1;
            for (; j < items.size(); j++) {
                if (!(items.get(i) instanceof SeparatorItem)) {
                    continue;
                }
                j--;
                break;
            }
            if (j == items.size()) {
                j--;
            }
            if (j < items.size()) {
                List<Object> subList = items.subList(i, j + 1);
                Alphabetical.sort(subList);
                for (int k = i; k <= j; k++) {
                    items.set(k, subList.get(k - i));
                }
            }
            i = j + 1;
        }
        revalidate();
        repaint();
    }

    @Override
    public void insertItemAt(final Object anObject, final int index) {
        if (newListStartIndex > 0) {
            List<Object> sortedSubList = items.subList(newListStartIndex, items.size());
            int insertPos = Alphabetical.getInsertPosition(sortedSubList, anObject);
            insertPos += newListStartIndex;
            super.insertItemAt(anObject, insertPos);
        } else {
            super.insertItemAt(anObject, Alphabetical.getInsertPosition(items, anObject));
        }
    }

    @Override
    public void addItem(final Object anObject) {
        insertItemAt(anObject, 0);
        //		//wenn nullable, dann muss 0tes Item übersprungen werden
        //		if (!isNullAble())
        //			insertItemAt(anObject, 0);
        //		else
        //			insertItemAt(anObject, 1);
    }

    /**
     * Fügt alle Einträge in diese ComboBox ein.
     *
     * @param entries
     */
    public void addAll(final Iterable<?> entries) {
        for (Object o : entries) {
            addItem(o);
        }
    }

    /**
     * Selektiert das erste Auftreten des übergebenen Objektes in der Liste.
     * Sind <code>NamedObjectContainer</code> in der Liste enthalten, wird von diesen die Methode
     * <code>getObject()<code> aufgerufen und das übergebene Objekt mit diesem dann vergleichen.
     * Sind sie gleich, wird die Zeile selektiert. Ist das Objekt in der Liste kein <code>NamedObjectContainer</code>,
     * wird einfach damit verglichen.
     *
     * @param o
     */
    public int setSelectedObject(final Object o) {
        for (int i = 0; i < getItemCount(); i++) {
            Object oAtI = getObjectAt(i);
            if (oAtI == null) {
                if (o != null) {
                    continue;
                }
                setSelectedIndex(i);
                return i;
            }
            if (oAtI.equals(o)) {
                setSelectedIndex(i);
                return i;
            }
        }
        return -1;
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
        return getObjectAt(getSelectedIndex());
    }

    /**
     * Liefert das <code>Object</code> an Index <code>index</code>.<br>
     * Wenn das ein <code>NamedObjectContainer</code> ist, wird von diesem die Methode
     * <code>getObject()<code> aufgerufen und das Ergebnis zurück gegeben, sonst wird einfach
     * das <code>Object</code> am Index <code>index</code> zurückgegeben.
     *
     * @return selektierte Objekt
     */
    @SuppressWarnings("rawtypes")
    public Object getObjectAt(final int index) {
        Object o = getItemAt(index);
        if (o instanceof NamedObjectContainer) {
            return ((NamedObjectContainer) o).getObject();
        }
        return o;
    }

    /**
     * Fügt am Ende der bestehenden Liste ein <code>SeparatorItem</code> ein
     * und beginnt eine neue alphabetisch sortierte Liste.
     * Der Parameter <code>showSeparatorLine</code> legt fest, ob eine Zeichenkette
     * bestehend aus Minuszeichen angezeigt werden soll. Wird fals übergeben, werden
     * alle neu hinzugefügten Elemente ab diesem Separator wieder neu aplhabetisch sortiert.
     *
     * @param showSeparatorLine
     *            wird <code>true</code> übergeben, wird in der Auswahlliste eine Zeichenkette aus Minuszeichen angezeigt
     */
    public void addSeparator(final boolean showSeparatorLine) {
        if (showSeparatorLine) {
            items.add(new SeparatorItem());
        }
        newListStartIndex = items.size();
    }

    /**
     * Fügt am Ende der bestehenden Liste ein <code>SeparatorItem</code> ein
     * und beginnt eine neue alphabetisch sortierte Liste.
     *
     * @name <code>String</code> der als Separator angezeigt werden soll
     */
    public void addSeparator(final String name) {
        items.add(new SeparatorItem(name));
        newListStartIndex = items.size();
    }

    /**
     * Fügt zur Liste ein <code>NamedObjectContainer</code> hinzu mit <code>anObject</code> als Objekt und dem Anzeige-String
     * <code>anObject.toString()</code>, dem <code>shift</code> Leerzeichen vorangestellt werden.
     *
     * @param anObject
     * @param shift
     */
    public void addItem(final Object anObject, final int shift) {
        addItem(anObject, anObject.toString(), shift);
    }

    /**
     * Fügt zur Liste ein <code>NamedObjectContainer</code> hinzu mit <code>anObject</code> als Objekt und dem Anzeige-String <code>displayName</code>
     * .
     *
     * @param anObject
     * @param displayName
     */
    public void addItem(final Object anObject, final String displayName) {
        addItem(anObject, displayName, 0);
    }

    /**
     * Fügt zur Liste ein <code>NamedObjectContainer</code> hinzu mit <code>anObject</code> als Objekt und dem Anzeige-String <code>displayName</code>
     * .
     *
     * @param anObject
     * @param displayName
     * @param shift
     *            Anzahl der Leerzeichen, um die der neue Eintrag nach rechts eingerückt werden soll
     */
    public void addItem(final Object anObject, final String displayName, final int shift) {
        StringBuilder sb = new StringBuilder(displayName.length() + shift);
        for (int i = 0; i < shift; i++) {
            sb.append(" ");
        }
        sb.append(displayName);
        insertItemAt(new NamedObjectContainer<Object>(anObject, sb.toString()), 0);
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.JComboBox#selectedItemChanged()
     */
    @Override
    protected void selectedItemChanged() {
        //erstmal die Selektion durchführen
        super.selectedItemChanged();
        //das selektierte Objekt holen
        Object selected = getSelectedItem();
        //wenn nichts selektiert ist -> raus
        if (selected == null) {
            return;
        }
        //wenn ein Separator selektiert wurde
        if (selected.getClass() == SeparatorItem.class) {
            //Selektion auf den vorher selektierten Eintrag zurück setzen
            setSelectedIndex(lastCorrectSelectedIndex);
        } else {
            //speichere den Index des jetzt selektierten Elementes
            lastCorrectSelectedIndex = getSelectedIndex();
        }
    }

    /**
     * Ein Separator, der in die Liste einer <code>AlphabeticalComboBox</code> eingefügt werden kann, um
     * mehrere Listen voneinander zu trennen.<br>
     * Wird dem Separator kein eigener <code>String</code> übergeben, wird er als <code>JSeparator</code> gerendert. Wird ein <code>String name</code>
     * übergeben, so wird dieser angezeigt.
     *
     * @author AXS
     * @created 19.10.2007
     */
    private static final class SeparatorItem {

        /**
         * <code>String</code>, der angezeigt werden soll
         */
        private final String name;

        /**
         * Erzeugt einen neuen Separator, der in der <code>AlphabeticalComboBox</code> als Zeichenfolge
         * aus Minuszeichen ('-') angezeigt wird.
         */
        public SeparatorItem() {
            this(null);
        }

        /**
         * @param name <code>String</code>, der angezeigt werden soll
         */
        public SeparatorItem(final String name) {
            super();
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Sorgt dafür, dass <code>SeparatorItem</code>s nicht selektierbar und nicht enabled
     * dargestellt werden. Der Rest der Liste wird defaultmäßig gerendert.
     *
     * @author AXS
     * @created 19.10.2007
     */
    private class MyRenderer extends DefaultListCellRenderer {

        /**
         * <code>String</code> bestehend aus Leerzeichen, die allen Einträgen außer Separatoren vorangestellt
         * werden, wenn im Konstruktor eine Zahl <code>shift > 0</code> übergeben wurde.<br>
         * Die Anzahl der Leerzeichen wird durch <code>shift</code> festgelegt.
         */
        private String shiftString = null;

        /**
         * Erzeugt einen neuen Renderer, der alle Einträge, die keinen Separatoren sind um <code>shift</code> Leerzeichen nach rechts verschiebt.
         *
         * @param shift
         *            Anzahl der Leerzeichen, um die Einträge verschoben werden sollen.
         */
        public MyRenderer(int shift) {
            super();
            //shiftString aus so wielen Leerzeichen aufbauen, wie shift > 0 ist
            if (shift > 0) {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(' ');
                } while (shift-- > 0);
                shiftString = sb.toString();
            }
        }

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, boolean isSelected, boolean cellHasFocus) {
            JLabel c = null;
            //Separatoren
            if (value instanceof SeparatorItem) {
                //disabled und nicht selektierbar darstellen
                cellHasFocus = false;
                isSelected = false;
                //wenn ein Separator ohne speziellen Text angezeigt werden soll
                if (value.toString() == null) {
                    return new JSeparator();
                }
                //wenn der Separator mit Text angezeigt werden soll
                c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setEnabled(false);
                //normale Einträge
            } else {
                c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                //Einträge verschieben
                if (shiftString != null) {
                    c.setText(shiftString + c.getText());
                }

                //Tooltips
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                    if (index > -1) {
                        list.setToolTipText(null);
                        Object selectedValue = list.getSelectedValue();
                        if (selectedValue != null) {
                            String itemToString = list.getSelectedValue().toString();
                            int stringWidth = list.getFontMetrics(list.getFont()).stringWidth(itemToString);
                            if (stringWidth > list.getVisibleRect().width) {
                                list.setToolTipText(itemToString);
                            }
                        }
                    }
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setFont(list.getFont());
                setText(value == null ? "" : value.toString());
            }
            return c;
        }
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
        newListStartIndex = 0;
    }

    /**
     * Zuerst wird versucht, das übergebene Object zu entfernen. Wenn es sich nicht entfernen ließ,
     * werden alle <code>NamedObjectContainer</code> durchsucht, ob sie das übergebene Object einthalten,
     * bis der erste von hinten gefunden wurde. Dieser wird dann entfernt.
     *
     * @see javax.swing.JComboBox#removeItem(java.lang.Object)
     */
    @Override
    public void removeItem(final Object anObject) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) getModel();
        int index = model.getIndexOf(anObject);

        // Wenn der zurückgegebene Index <0 ist, ist das zu löschende Element nicht in der comboBox und der removeItemAt würde eine Ex. werfen.
        if (index < 0) {
            return;
        }

        if (index >= 0 && index < newListStartIndex) {
            newListStartIndex--;
        } else {
            for (int i = 0; i < model.getSize(); i++) {
                Object o = model.getElementAt(i);
                if (o instanceof NamedObjectContainer) {
                    o = ((NamedObjectContainer<?>) o).getObject();
                    if (o == anObject || o != null && o.equals(anObject)) {
                        index = i;
                        break;
                    }
                }
            }
        }
        super.removeItemAt(index);
    }

    @Override
    public void removeItemAt(final int anIndex) {
        super.removeItemAt(anIndex);
        if (anIndex < newListStartIndex) {
            newListStartIndex--;
        }
    }

    /**
     * @return Leerfeld ja/nein
     */
    public boolean isNullAble() {
        return nullAble;
    }

    /**
     * setter für Leerfeld, ruft zusätzlich jeweilige Methode auf
     * auf private gesetzt: nur einmal beim konstruktor gültig!
     *
     * @param nullAble
     */
    private void setNullAble(final boolean nullAble) {
        if (!isNullAble() && nullAble) {
            addItem(EMPTY_VALUE_ENTRY);
        } else if (isNullAble() && !nullAble) {
            removeItem(EMPTY_VALUE_ENTRY);
        }
        this.nullAble = nullAble;
    }

}