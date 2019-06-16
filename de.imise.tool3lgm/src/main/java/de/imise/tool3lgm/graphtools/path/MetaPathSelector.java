package de.imise.tool3lgm.graphtools.path;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.util.Alphabetical;
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

    /** Der ElementsNameBuilder, der zum zugehörigen MetaModel gehört */
    private final ElementsNameBuilder elementsNameBuilder;

    /**
     * ComboBox für die erste Klasse
     */
    private final AlphabeticalComboBox class1ComboBox;

    /**
     * ComboBox für die zweite Klasse
     */
    private final AlphabeticalComboBox class2ComboBox;

    /**
     * Chekcbox über die eingestellt werden kann, ob nur absolute Kindelemente (also Elemente ohne
     * eigene Teilelemente) angezeigt werden soll. Diese Box ist disabled, wenn weder in den Zeilen
     * noch in den Spalten Elemente angezeigt werden, die in Teil-Von-Beziehung stehen können.
     */
    private final JCheckBox showPartsOnlyCheckBox;

    /**
     * Metapaths that can be choosed in the <code>metaPathJList</code>
     */
    private List<AbstractMetaPath> selectableMetaPaths;

    /**
     * Choosed metapaths
     */
    private final List<AbstractMetaPath> selectedMetaPaths;

    /**
     * Listeners for change events
     */
    private final List<ChangeListener> changeListenerList = new ArrayList<>();

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
     * Wenn <code>true</code> werden auch alle MetaPfade zurück gegeben, die für Oberklassen der Startklasse definiert wurden.
     * Bei <code>false</code> werden nur die Metepfade zurück gegeben, die für die Startklasse definiert wurden.
     * Dieser Parameter sorgt dafür, dass ein Pfad auch die Unterklassen der eigentlichen Pfadstart- und Pfadenklassen angeboten wird.
     */
    private final boolean pathsForSubClasses = true;

    /**
     * Wenn <code>true</code> werden auch alle MetaPfade zurück gegeben, die für Unterklassen der Startklasse definiert wurden.
     * Bei <code>false</code> werden nur die Metepfade zurück gegeben, die für die Startklasse selbst definiert wurden.
     * Dieser Parameter sorgt dafür, dass ein Pfad auch die Oberklassen der eigentlichen Pfadstart- und Pfadenklassen angeboten wird.
     * DAS IST NUR BEDINGT SINNVOLL UND DESWEGEN FALSE. Wenn man diese Nach-Oben-Vererbung doch mal brauchen sollte, muss man diesen
     * Wert über einen Konstruktor-Paramter setzen.
     */
    private final boolean pathsForSuperClasses = false;

    /** Alle Elementklasse, bei denenlaut {@link MetaPathDefinition} Pfade enden. */
    private final Set<Class<? extends ModelElement>> endElementClassesInPaths;

    /**
     * @param model
     *            Model, das die auswählbaren Elementklassen und Pfade festlegt.
     * @param maxParallelSelectedPaths
     *            Maximale Anzahl gleichzeitig auswählbarer Pfade, wenn es mehrere gibt
     */
    public MetaPathSelector(final MetaPathDefinition model, final int maxParallelSelectedPaths) {
        this.model = model;
        this.maxParallelSelectedPaths = maxParallelSelectedPaths;
        class1ComboBox = new AlphabeticalComboBox();
        Set<Class<? extends ModelElement>> startElementClassesInPaths = model.getStartElementClassesInPaths(pathsForSubClasses, pathsForSuperClasses);
        endElementClassesInPaths = model.getEndElementClassesInPaths(pathsForSubClasses, pathsForSuperClasses);
        MetaModel metaModel = model.getMetaModel();
        elementsNameBuilder = metaModel.getElementsNameBuilder();
        for (Class<? extends ModelElement> elementClass : startElementClassesInPaths) {
            String name = elementsNameBuilder.getDisplayableName(elementClass);
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

        showPartsOnlyCheckBox = new JCheckBox(Tool3lgmConstants.getResString("showAbsolutePartsOnly"));
        showPartsOnlyCheckBox.addActionListener(this);

        selectedMetaPaths = new ArrayList<>();
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
            for (Class<? extends ModelElement> elementClass : endElementClassesInPaths) {
                Set<AbstractMetaPath> metaPathes = model.getMetaPaths(class1BoxSelection, elementClass, pathsForSubClasses, pathsForSuperClasses, false);
                if (metaPathes != null && metaPathes.size() > 0) {
                    class2ComboBox.addItem(elementClass, elementsNameBuilder.getDisplayableName(elementClass));
                }
            }
            selectedMetaPaths.clear();
            selectableMetaPaths = null;
            deliverChangeEvent(class1ComboBox);
        } else if (e.getSource() == class2ComboBox) {
            class2ComboBox.setPopupVisible(false);
            if (class2ComboBox.getSelectedItem() == null) {
                return;
            }
            Class<? extends ModelElement> class1BoxSelection = ((Class<?>) class1ComboBox.getSelectedObject()).asSubclass(ModelElement.class);
            Class<? extends ModelElement> class2BoxSelection = ((Class<?>) class2ComboBox.getSelectedObject()).asSubclass(ModelElement.class);
            selectedMetaPaths.clear();
            selectableMetaPaths = new ArrayList<>(model.getMetaPaths(class1BoxSelection, class2BoxSelection, pathsForSubClasses, pathsForSuperClasses, true));
            Alphabetical.sort(selectableMetaPaths);
            if (selectableMetaPaths != null) {
                if (selectableMetaPaths.size() == 1) {
                    AbstractMetaPath metaPath = selectableMetaPaths.iterator().next();
                    selectableMetaPaths = ImmutableList.of(metaPath);
                    selectedMetaPaths.add(metaPath);
                } else if (selectableMetaPaths.size() > 1) {
                    List<NamedObjectContainer<AbstractMetaPath>> pathNames = new ArrayList<>(selectableMetaPaths.size());
                    for (AbstractMetaPath metaPath : selectableMetaPaths) {
                        pathNames.add(new NamedObjectContainer<>(metaPath, metaPath.getFullName()));
                    }

                    StringBuilder sb = new StringBuilder(Tool3lgmConstants.getResString("text_path_1"));
                    sb.append(" ");
                    //beliebig viele Pfade sind auswählbar
                    if (maxParallelSelectedPaths <= 0 || maxParallelSelectedPaths >= selectableMetaPaths.size()) {
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
                    Object[] selectedArray = new Object[selectableMetaPaths.size()];
                    List<?> selected = Arrays.asList(selectedArray);
                    while (selected != null && (selectedMetaPaths.size() == 0 || selectedMetaPaths.size() > maxParallelSelectedPaths)) {
                        selectedMetaPaths.clear();
                        selected = MultipleOptionPane.showCheckBoxOptionDialog(Static.getTool(), Tool3lgmConstants.getResString("choice"), sb.toString(), pathNames, null, false);
                        for (int i = 0; selected != null && i < selected.size(); i++) {
                            Object selectedI = selected.get(i);
                            if (selectedI != null) {
                                @SuppressWarnings("unchecked")
                                NamedObjectContainer<AbstractMetaPath> metaPathCont = (NamedObjectContainer<AbstractMetaPath>) selectedI;
                                selectedMetaPaths.add(metaPathCont.getObject());
                            }
                        }
                    }
                }
            }
            deliverChangeEvent(class2ComboBox);
        } else {
            deliverChangeEvent(e.getSource());
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
            dialogMetaPathSelecor = new MetaPathSelector(model, maxParallelSelectedPaths);
        }
        AlphabeticalJList metaPathJList = new AlphabeticalJList(dialogMetaPathSelecor.selectableMetaPaths);
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
        dialogMetaPathSelecor.selectedMetaPaths.clear();
        Object selectedMetaPath = metaPathJList.getSelectedObject();
        if (selectedMetaPath != null) {
            dialogMetaPathSelecor.selectedMetaPaths.add((AbstractMetaPath) selectedMetaPath);
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
     * @return Returns the showPartsOnlyCheckBox
     */
    public JCheckBox getShowPartsOnlyCheckBox() {
        return showPartsOnlyCheckBox;
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
     * Liefert <code>true</code>, wenn gültige Elementklassen und ein gültiger MetaPfad (jeweils
     * ungleich <code>null</code>) gesetzt sind.
     *
     * @return <code>true</code>, wenn gültige Klassen und ein gültiger Metapfad ausgewählt wurde,
     *         sonst <code>false</code>
     */
    public boolean isValidSelection() {
        MetaPathSelection selection = getSelection();
        return selection.class1 != null && selection.class2 != null && selection.selectedMetaPaths != null && selection.selectedMetaPaths.size() > 0;
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

    public MetaPathSelection getSelection() {
        MetaPathSelection selection = new MetaPathSelection();
        selection.class1 = getSelectedClass(class1ComboBox);
        selection.class2 = getSelectedClass(class2ComboBox);
        selection.showPartsOnly = showPartsOnlyCheckBox.isSelected();
        selection.selectedMetaPaths = ImmutableList.copyOf(selectedMetaPaths); // es muss unbedingt eine Kopie sein!
        return selection;
    }

    public void setSelection(final MetaPathSelection selection) {
        class2ComboBox.removeActionListener(this);//von der 2. Combobox muss der ActionListener entfernt werden, damit nicht die MetaPfad-Auswahl an den Benutzer gestellt wird
        class1ComboBox.setSelectedObject(selection == null ? null : selection.class1);
        class2ComboBox.setSelectedObject(selection == null ? null : selection.class2);
        class2ComboBox.addActionListener(this);//ActionListener wieder hinzufügen
        selectedMetaPaths.clear();
        if (selection != null) {
            selectedMetaPaths.addAll(selection.selectedMetaPaths);
        }
        showPartsOnlyCheckBox.setSelected(selection != null && selection.showPartsOnly);
        deliverChangeEvent(class2ComboBox);//Tabelle aufbauen
    }

    /**
     * Spiegelt genau eine Auswahl der Einstellungsmöglichkeiten des Selektors wieder
     *
     * @author AXS (23 Nov 2018)
     */
    public static final class MetaPathSelection {

        /** Selected Class 1 */
        public Class<? extends ModelElement> class1;

        /** Selected Class 2 */
        public Class<? extends ModelElement> class2;

        /** Choosed metapaths */
        public List<AbstractMetaPath> selectedMetaPaths;

        /** Show parts only in matrix rows and columns **/
        public boolean showPartsOnly;

        @Override
        public String toString() {
            String class1Name = class1 == null ? "null" : class1.getSimpleName();
            String class2Name = class2 == null ? "null" : class2.getSimpleName();
            return class1Name + " " + selectedMetaPaths + " " + class2Name + " (" + showPartsOnly + ")";
        }

    }

}
