/*
 * Created on 13.11.2007
 */
package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.list.AlphabeticalJList;

/**
 * Diese Klasse stellt 3 zusammengehörige Komponenten bereit, mit denen ein Benutzter einen speziellen Metapfad auswählen kann.<br>
 * In die erste ComboBox kann man eine Klasse des Meta-Pfades eingeben, in die zweite die andere Klasse und, falls es zw. diesen Klassen mehrere
 * MetaPfade gibt, kann man aus einer Liste einen auswählen.<br>
 * Dieser Mechanismus wird in der Matrix-Sicht verwendet.
 *
 * @author AXS
 */
public class MetaPathSelector implements ActionListener {

    /**
     * ComboBox für die erste Klasse
     */
    private final AlphabeticalComboBox class1ComboBox;

    /**
     * ComboBox für die zweite Klasse
     */
    private final AlphabeticalComboBox class2ComboBox;

    /**
     * Listenkomponente zur Auswahl des Metapfades zwischen den in den ComboBoxen ausgewählten Klassen.
     */
    private final AlphabeticalJList metaPathJList;

    /**
     * <code>MetaPath</code>es that can be choosed in the <code>metaPathJList</code>
     */
    private Collection<MetaPath> selectableMetaPathes;

    /**
     * Listeners for change events
     */
    private final ArrayList<ChangeListener> changeListenerList = new ArrayList<>();

    /**
     * Selektor der in allen Dialigen benutzt wird, die statisch von dieser Klasse angezeigt werden. Dadurch bleibt eine alte Auswahl immer erhalten.
     */
    private static MetaPathSelector dialogMetaPathSelecor;

    /**
     *
     */
    private MetaPathSelector() {
        super();

        class1ComboBox = new AlphabeticalComboBox();
        for (Class<? extends ModelElement> elementClass : PathFinder.getElementClassesInPathes()) {
            String name = ElementsNameBuilder.getDisplayableName(elementClass);
            if (name != null && name != "") {
                class1ComboBox.addItem(elementClass, name);
            }
        }
        class1ComboBox.setSelectedItem(null);
        class1ComboBox.addActionListener(this);

        class2ComboBox = new AlphabeticalComboBox();
        class2ComboBox.addActionListener(this);
        class2ComboBox.setEnabled(false);
        class2ComboBox.setPreferredSize(class1ComboBox.getPreferredSize());

        metaPathJList = new AlphabeticalJList();
        metaPathJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Gibt einen neuen <code>MetaPathSelector</code>, für den alle Komponenten initialisiert sind, zurück.
     *
     * @return einen neuen <code>MetaPathSelector</code>
     */
    public static final MetaPathSelector createComponents() {
        return new MetaPathSelector();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == class1ComboBox) {
            class2ComboBox.removeAllItems();
            if (class1ComboBox.getSelectedItem() == null) {
                class2ComboBox.setEnabled(false);
                return;
            }
            class2ComboBox.setEnabled(true);
            Class<? extends ModelElement> class1BoxSelection = ((Class<?>) class1ComboBox.getSelectedObject()).asSubclass(ModelElement.class);
            for (Class<? extends ModelElement> elementClass : PathFinder.getElementClassesInPathes()) {
                if (!PathFinder.getMetaPathes(elementClass, class1BoxSelection).isEmpty()) {
                    class2ComboBox.addItem(elementClass, ElementsNameBuilder.getDisplayableName(elementClass));
                }
            }
            metaPathJList.removeAllElements();
            selectableMetaPathes = null;
            deliverChangeEvent(class1ComboBox);
        } else if (e.getSource() == class2ComboBox) {
            if (class2ComboBox.getSelectedItem() == null) {
                return;
            }
            Class<? extends ModelElement> class1BoxSelection = ((Class<?>) class1ComboBox.getSelectedObject()).asSubclass(ModelElement.class);
            Class<? extends ModelElement> class2BoxSelection = ((Class<?>) class2ComboBox.getSelectedObject()).asSubclass(ModelElement.class);
            metaPathJList.removeAllElements();
            selectableMetaPathes = PathFinder.getMetaPathes(class1BoxSelection, class2BoxSelection);
            if (!selectableMetaPathes.isEmpty()) {
                for (MetaPath selectableMetaPath : selectableMetaPathes) {
                    metaPathJList.addItem(selectableMetaPath, selectableMetaPath.getFullDescription());
                }
                metaPathJList.setSelectedIndex(0);
            }
            deliverChangeEvent(class2ComboBox);
        }
    }

    /**
     * Zeigt einen Dialog an, in dem man Klassen und einen MetaPfad auswählen kann.
     *
     * @return einen <code>MetaPathSelector</code> der durch die Einträge aus einem angezeigten Dialog gefüllt wurde
     */
    public static final MetaPathSelector showDialog() {
        String class1Name = getResString("class1");
        String class2Name = getResString("class2");
        String metaPathName = getResString("metapath");
        return showDialog(class1Name, class2Name, metaPathName);
    }

    /**
     * Zeigt einen Dialog an, in dem man Klassen und einen MetaPfad auswählen kann.
     *
     * @param class1Label Label, das für die erste auszuwählende Klasse angezeigt werden soll
     * @param class2Label Label, das für die zweite auszuwählende Klasse angezeigt werden soll
     * @param metaPathListLabel Label, das über der MetaPathList angezeigt werden soll
     * @return einen <code>MetaPathSelector</code> der durch die Einträge aus einem angezeigten Dialog gefüllt wurde
     */
    public static final MetaPathSelector showDialog(final String class1Label, final String class2Label, final String metaPathListLabel) {
        if (dialogMetaPathSelecor == null) {
            dialogMetaPathSelecor = new MetaPathSelector();
        }
        Object[] metaPathSelectorMessage = {
                class1Label,
                dialogMetaPathSelecor.getClass1ComboBox(),
                class2Label,
                dialogMetaPathSelecor.getClass2ComboBox(),
                metaPathListLabel,
                new JScrollPane(dialogMetaPathSelecor.getMetaPathJList())
        };
        JOptionPane op = new JOptionPane();
        op.setMessage(metaPathSelectorMessage);
        op.createDialog(null, getResString("metapath_selection")).setVisible(true);
        return dialogMetaPathSelecor;
    }

    /**
     * @return Returns the class1ComboBox.
     */
    public AlphabeticalComboBox getClass1ComboBox() {
        return class1ComboBox;
    }

    /**
     * @return Returns the class2ComboBox.
     */
    public AlphabeticalComboBox getClass2ComboBox() {
        return class2ComboBox;
    }

    /**
     * @return Returns the metaPathJList.
     */
    public AlphabeticalJList getMetaPathJList() {
        return metaPathJList;
    }

    /**
     * @param classComboBox
     * @return selected class of <code>classComboBox</code>
     */
    private static final Class<? extends ModelElement> getSelectedClass(final AlphabeticalComboBox classComboBox) {
        Object o = classComboBox.getSelectedObject();
        if (o != null && o instanceof Class && ModelElement.class.isAssignableFrom((Class<?>) o)) {
            return ((Class<?>) o).asSubclass(ModelElement.class);
        }
        return null;
    }

    /**
     * @return Selected class in <code>class1ComboBox</code> or <code>null</code>
     */
    public Class<? extends ModelElement> getSelectedClass1() {
        return getSelectedClass(class1ComboBox);
    }

    /**
     * @return Selected class in <code>class2ComboBox</code> or <code>null</code>
     */
    public Class<? extends ModelElement> getSelectedClass2() {
        return getSelectedClass(class2ComboBox);
    }

    /**
     * @return Selected <code>MetaPath</code> in <code>metaPathJList</code>
     */
    public MetaPath getSelectedMetaPath() {
        Object o = metaPathJList.getSelectedObject();
        if (o != null) {
            return (MetaPath) o;
        }
        return null;
    }

    /**
     * @return Returns the selectableMetaPathes.
     */
    public Collection<MetaPath> getSelectableMetaPathes() {
        return selectableMetaPathes;
    }

    /**
     * Liefert <code>true</code>, wenn gültige Elementklassen und ein gültiger MetaPfad (jeweils ungleich <code>null</code>) gesetzt sind.
     *
     * @return <code>true</code>, wenn gültige Klassen und ein gültiger Metapfad ausgewählt wurde, sonst <code>false</code>
     */
    public boolean isValid() {
        if (getSelectedClass1() == null || getSelectedClass2() == null || getSelectedMetaPath() == null) {
            return false;
        }
        return true;
    }

    /**
     * @param listener
     */
    public void addChangeListener(final ChangeListener listener) {
        changeListenerList.add(listener);
    }

    /**
     * @param listener
     */
    public void removeChangeListener(final ChangeListener listener) {
        changeListenerList.remove(listener);
    }

    /**
     * @param e
     */
    private void deliverChangeEvent(final Object source) {
        ChangeEvent e = new ChangeEvent(source);
        for (ChangeListener cl : changeListenerList) {
            cl.stateChanged(e);
        }
    }

}
