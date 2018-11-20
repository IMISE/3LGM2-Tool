package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.graphtools.ElementsNameBuilder.getDisplayableName;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.list.AlphabeticalJList;
import de.imise.util.swing.dialog.MultipleOptionPane;

/**
 * Diese Klasse stellt 3 zusammengehörige Komponenten bereit, mit denen ein Benutzter einen speziellen Metapfad auswählen kann.<br>
 * In die erste ComboBox kann man eine Klasse des Meta-Pfades eingeben, in die zweite die andere Klasse und, falls es zw. diesen Klassen mehrere
 * MetaPfade gibt, kann man aus einer Liste einen auswählen.<br>
 * Dieser Mechanismus wird in der Matrix-Sicht verwendet.
 * Diesen Selector gab es schon für die alten Pfade der Matrixsicht und hiermit wurde er für die neuen Pfade adaptiert.
 *
 * @author AXS
 * @create 13.11.2007
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
     * Metapaths that can be choosed in the <code>metaPathJList</code>
     */
    private Set<AbstractMetaPath> selectableMetaPathes;

    /**
     * Choosed metapaths
     */
    private final ArrayList<AbstractMetaPath> selectedMetaPathes;

    /**
     * Listeners for change events
     */
    private final ArrayList<ChangeListener> changeListenerList = new ArrayList<>();

    /**
     * Selektor der in allen Dialigen benutzt wird, die statisch von dieser Klasse angezeigt werden. Dadurch bleibt eine alte Auswahl immer erhalten.
     */
    private static MetaPathSelector dialogMetaPathSelecor;

    /**
     * Model, das die auswählbaren Elementklassen und Pfade festlegt.
     */
    private final MetaPathDefinition model;

    /** Maximale Anzahl gleichzeitig auswählbarer Pfade, wenn es mehrere gibt */
    private int maxParallelSelectedPaths = -1;

    /**
     * Wenn nichts anderes angegeben wurde, ist dieser Wert der Default von {@link #pathsForSuperClasses}
     */
    private static final boolean DEFAULT_PATHS_FOR_SUPER_CLASSES = false;

    /**
     * Wenn <code>true</code> werden auch alle MetaPfade zurück gegeben, die für Unterklassen der Startklasse definiert wurden.
     * Bei <code>false</code> werden nur die Metepfade zurück gegeben, die für die Startklasse selbst definiert wurden.
     */
    private boolean pathsForSuperClasses = DEFAULT_PATHS_FOR_SUPER_CLASSES;

    /**
     * Wenn <code>true</code> werden auch alle MetaPfade zurück gegeben, die für Oberklassen der Startklasse definiert wurden.
     * Bei <code>false</code> werden nur die Metepfade zurück gegeben, die für die Startklasse definiert wurden.
     */
    private final boolean pathsForSubClasses = true;

    /** Alle Elementklasse, für die laut {@link MetaPathDefinition} Pfade definiert sind. */
    private final Set<Class<? extends ModelElement>> elementClassesWithPaths;

    /**
     * @param model
     *            Model, das die auswählbaren Elementklassen und Pfade festlegt.
     * @param maxParallelSelectedPaths
     *            Maximale Anzahl gleichzeitig auswählbarer Pfade, wenn es mehrere gibt
     * @param pathsForSuperClasses
     *            Wenn <code>true</code> werden auch alle MetaPfade zurück gegeben, die für Unterklassen der Startklasse definiert wurden.
     *            Bei <code>false</code> werden nur die Metapfade zurück gegeben, die für die Startklasse selbst und ihre Oberklassen definiert
     *            wurden.
     */
    public MetaPathSelector(final MetaPathDefinition model, final int maxParallelSelectedPaths, final boolean pathsForSuperClasses) {
        super();
        elementClassesWithPaths = model.getStartElementClassesWithPaths(pathsForSubClasses, pathsForSuperClasses);
        this.model = model;
        this.maxParallelSelectedPaths = maxParallelSelectedPaths;
        this.pathsForSuperClasses = pathsForSuperClasses;
        class1ComboBox = new AlphabeticalComboBox();
        for (Class<? extends ModelElement> elementClass : elementClassesWithPaths) {
            String name = getDisplayableName(elementClass);
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

        selectedMetaPathes = new ArrayList<>();
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
            for (Class<? extends ModelElement> elementClass : elementClassesWithPaths) {
                Set<AbstractMetaPath> metaPathes = model.getMetaPaths(elementClass, class1BoxSelection, pathsForSubClasses, pathsForSuperClasses);
                if (metaPathes != null && metaPathes.size() > 0) {
                    class2ComboBox.addItem(elementClass, getDisplayableName(elementClass));
                }
            }
            selectedMetaPathes.clear();
            selectableMetaPathes = null;
            deliverChangeEvent(class1ComboBox);
        } else if (e.getSource() == class2ComboBox) {
            class2ComboBox.setPopupVisible(false);
            if (class2ComboBox.getSelectedItem() == null) {
                return;
            }
            Class<? extends ModelElement> class1BoxSelection = ((Class<?>) class1ComboBox.getSelectedObject()).asSubclass(ModelElement.class);
            Class<? extends ModelElement> class2BoxSelection = ((Class<?>) class2ComboBox.getSelectedObject()).asSubclass(ModelElement.class);
            selectedMetaPathes.clear();
            selectableMetaPathes = model.getMetaPaths(class1BoxSelection, class2BoxSelection, pathsForSubClasses, pathsForSuperClasses);

            if (selectableMetaPathes != null) {
                if (selectableMetaPathes.size() == 1) {
                    selectedMetaPathes.add(selectableMetaPathes.iterator().next());
                } else if (selectableMetaPathes.size() > 1) {
                    Object[] selected = new Object[selectableMetaPathes.size()];
                    @SuppressWarnings("unchecked")
                    NamedObjectContainer<AbstractMetaPath>[] pathNames = new NamedObjectContainer[selected.length];
                    int i = 0;
                    for (AbstractMetaPath metaPath : selectableMetaPathes) {
                        pathNames[i++] = new NamedObjectContainer<>(metaPath, metaPath.getFullName());
                    }
                    StringBuilder sb = new StringBuilder(Tool3lgmConstants.getResString("text_path_1"));
                    sb.append(" ");
                    //beliebig viele Pfade sind auswählbar
                    if (maxParallelSelectedPaths <= 0 || maxParallelSelectedPaths >= selectableMetaPathes.size()) {
                        sb.append(Tool3lgmConstants.getResString("text_path_2"));
                        //maximal 1 Pfad ist auswählbar
                    } else if (maxParallelSelectedPaths == 1) {
                        sb.append(Tool3lgmConstants.getResString("text_path_3"));
                        //es dürfen einer oder mehrere, aber nicht alle zur Verfügung stehenden Pfade ausgewählt werden
                    } else {
                        sb.append(Tool3lgmConstants.getResString("text_path_4_1"));
                        sb.append(" ");
                        sb.append(maxParallelSelectedPaths);
                        sb.append(" ");
                        sb.append(Tool3lgmConstants.getResString("text_path_4_2"));
                    }
                    while (selected != null && (selectedMetaPathes.size() == 0 || selectedMetaPathes.size() > maxParallelSelectedPaths)) {
                        selectedMetaPathes.clear();
                        selected = MultipleOptionPane.showCheckBoxOptionDialog(Static.getTool(), Tool3lgmConstants.getResString("choice"), sb.toString(), pathNames, null, false);
                        for (i = 0; selected != null && i < selected.length; i++) {
                            if (selected[i] != null) {
                                @SuppressWarnings("unchecked")
                                NamedObjectContainer<AbstractMetaPath> metaPathCont = (NamedObjectContainer<AbstractMetaPath>) selected[i];
                                selectedMetaPathes.add(metaPathCont.getObject());
                            }
                        }
                    }
                }
            }
            deliverChangeEvent(class2ComboBox);
        }
    }

    /**
     * Zeigt einen Dialog an, in dem man Klassen und einen MetaPfad auswählen kann.
     *
     * @param model
     *            Model, das die auswählbaren Elementklassen und Pfade festlegt.
     * @param maxParallelSelectedPaths
     *            Maximale Anzahl gleichzeitig auswählbarer Pfade, wenn es mehrere gibt
     * @return einen <code>MetaPathSelector</code> der durch die Einträge aus einem angezeigten
     *         Dialog gefüllt wurde
     */
    public static final MetaPathSelector showDialog(final MetaPathDefinition model, final int maxParallelSelectedPaths) {
        String class1Name = Tool3lgmConstants.getResString("class1");
        String class2Name = Tool3lgmConstants.getResString("class2");
        String metaPathName = Tool3lgmConstants.getResString("metapath");
        return showDialog(model, class1Name, class2Name, metaPathName, maxParallelSelectedPaths);
    }

    /**
     * Zeigt einen Dialog an, in dem man Klassen und einen MetaPfad auswählen kann.
     *
     * @param model
     *            Model, das die auswählbaren Elementklassen und Pfade festlegt.
     * @param class1Label
     *            Label, das für die erste auszuwählende Klasse angezeigt werden soll
     * @param class2Label
     *            Label, das für die zweite auszuwählende Klasse angezeigt werden soll
     * @param metaPathListLabel
     *            Label, das über der MetaPathList angezeigt werden soll
     * @param maxParallelSelectedPaths
     *            Maximale Anzahl gleichzeitig auswählbarer Pfade, wenn es mehrere gibt
     * @return einen <code>MetaPathSelector</code> der durch die Einträge aus einem angezeigten
     *         Dialog gefüllt wurde
     */
    public static final MetaPathSelector showDialog(final MetaPathDefinition model, final String class1Label, final String class2Label, final String metaPathListLabel, final int maxParallelSelectedPaths) {
        if (dialogMetaPathSelecor == null) {
            dialogMetaPathSelecor = new MetaPathSelector(model, maxParallelSelectedPaths, DEFAULT_PATHS_FOR_SUPER_CLASSES);
        }
        AlphabeticalJList metaPathJList = new AlphabeticalJList(dialogMetaPathSelecor.selectableMetaPathes);
        if (maxParallelSelectedPaths <= 1) {
            metaPathJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            metaPathJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        Object[] metaPathSelectorMessage = {
                class1Label, dialogMetaPathSelecor.getClass1ComboBox(), class2Label, dialogMetaPathSelecor.getClass2ComboBox(), metaPathListLabel, new JScrollPane(metaPathJList)
        };
        JOptionPane op = new JOptionPane();
        op.setMessage(metaPathSelectorMessage);
        op.createDialog(null, Tool3lgmConstants.getResString("metapath_selection")).setVisible(true);
        dialogMetaPathSelecor.selectedMetaPathes.clear();
        Object selectedMetaPath = metaPathJList.getSelectedObject();
        if (selectedMetaPath != null) {
            dialogMetaPathSelecor.selectedMetaPathes.add((AbstractMetaPath) selectedMetaPath);
        }
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
     * @param classComboBox
     * @return selected class of <code>classComboBox</code>
     */
    private Class<? extends ModelElement> getSelectedClass(final AlphabeticalComboBox classComboBox) {
        Object o = classComboBox.getSelectedObject();
        if (o != null && Class.class.isAssignableFrom(o.getClass()) && ModelElement.class.isAssignableFrom((Class<?>) o)) {
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
     * @return
     */
    public AbstractMetaPath[] getSelectedMetaPaths() {
        AbstractMetaPath[] selectedPaths = new AbstractMetaPath[selectedMetaPathes.size()];
        for (int i = 0; i < selectedPaths.length; i++) {
            selectedPaths[i] = selectedMetaPathes.get(i);
        }
        return selectedPaths;
    }

    /**
     * @return Returns the selectableMetaPathes.
     */
    public Set<AbstractMetaPath> getSelectableMetaPathes() {
        return selectableMetaPathes;
    }

    /**
     * Liefert <code>true</code>, wenn gültige Elementklassen und ein gültiger MetaPfad (jeweils
     * ungleich <code>null</code>) gesetzt sind.
     *
     * @return <code>true</code>, wenn gültige Klassen und ein gültiger Metapfad ausgewählt wurde,
     *         sonst <code>false</code>
     */
    public boolean isValidSelection() {
        if (getSelectedClass1() == null || getSelectedClass2() == null || getSelectedMetaPaths() == null) {
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
