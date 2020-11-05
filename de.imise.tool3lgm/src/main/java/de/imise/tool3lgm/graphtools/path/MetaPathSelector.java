package de.imise.tool3lgm.graphtools.path;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.jena.ext.com.google.common.base.Strings;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.gui.MainFrame;
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
    private final AlphabeticalComboBox<Class<? extends ModelElement>> class1ComboBox;

    /**
     * ComboBox für die zweite Klasse
     */
    private final AlphabeticalComboBox<Class<? extends ModelElement>> class2ComboBox;

    /**
     * Chekcbox über die eingestellt werden kann, ob nur absolute Kindelemente (also Elemente ohne
     * eigene Teilelemente) angezeigt werden soll. Diese Box ist disabled, wenn weder in den Zeilen
     * noch in den Spalten Elemente angezeigt werden, die in Teil-Von-Beziehung stehen können.
     */
    private final JCheckBox showPartsOnlyCheckBox;

    /**
     * Metapaths that can be choosed in the <code>metaPathJList</code>
     */
    private List<MetaPath> selectableMetaPaths = new ArrayList<>();

    /**
     * Choosed metapaths
     */
    private final List<MetaPath> selectedMetaPaths;

    /**
     * Listeners for change events
     */
    private final List<ChangeListener> changeListenerList = new ArrayList<>();

    /**
     * Selektor der in allen Dialigen benutzt wird, die statisch von dieser Klasse angezeigt werden. Dadurch bleibt eine alte Auswahl immer erhalten.
     */
    private static MetaPathSelector dialogMetaPathSelecor;

    /**
     * If the selector is displayed in a dialog with this list
     * as view for the selectable meta paths, this list must
     * be updated if the selection of the comboboxes is changed.
     */
    private AlphabeticalJList metaPathJList;

    /**
     * Model, das die auswählbaren Elementklassen und Pfade festlegt.
     */
    private final MetaPathDefinition model;

    /** Maximale Anzahl gleichzeitig auswählbarer Pfade, wenn es mehrere gibt */
    private int maxParallelSelectedPaths = -1;

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
        class1ComboBox = new AlphabeticalComboBox<>();
        Set<Class<? extends ModelElement>> startElementClassesInPaths = model.getStartElementClassesInPaths();
        endElementClassesInPaths = model.getEndElementClassesInPaths();
        MetaModel metaModel = model.getMetaModel();
        elementsNameBuilder = metaModel.getElementsNameBuilder();
        for (Class<? extends ModelElement> elementClass : startElementClassesInPaths) {
            String displayableClassName = elementsNameBuilder.getDisplayableName(elementClass);
            if (!Strings.isNullOrEmpty(displayableClassName)) {
                Collection<Class<? extends ModelElement>> instanciableAssignableClasses = metaModel.getInstanciableAssignableClasses(elementClass);
                boolean showInstanciableElementClassList = CoreMetaModel.isAbstract(elementClass) || instanciableAssignableClasses.size() > 1;
                if (showInstanciableElementClassList) {
                    StringBuilder sb = new StringBuilder(displayableClassName);
                    sb.append(" (");
                    String displayableNames = elementsNameBuilder.getDisplayableName(instanciableAssignableClasses);
                    sb.append(displayableNames);
                    sb.append(")");
                    displayableClassName = sb.toString();
                }
                class1ComboBox.addObject(elementClass, displayableClassName);
            }
        }
        class1ComboBox.setSelectedItem(null);
        class1ComboBox.addActionListener(this);

        class2ComboBox = new AlphabeticalComboBox();
        class2ComboBox.addActionListener(this);
        class2ComboBox.setEnabled(false);
        Dimension preferredSizeBox1 = class1ComboBox.getPreferredSize();
        class2ComboBox.setPreferredSize(preferredSizeBox1);

        String showPartsOnlyCheckBoxLabel = Tool3lgmConstants.getResString("showAbsolutePartsOnly");
        showPartsOnlyCheckBox = new JCheckBox(showPartsOnlyCheckBoxLabel);
        showPartsOnlyCheckBox.addActionListener(this);

        selectedMetaPaths = new ArrayList<>();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        Object eventSource = e.getSource();
        if (eventSource == class1ComboBox) {
            class2ComboBox.removeAllItems();
            Class<? extends ModelElement> class1BoxSelection = class1ComboBox.getSelectedObject();
            if (class1BoxSelection == null) {
                class2ComboBox.setEnabled(false);
                return;
            }
            class2ComboBox.setEnabled(true);
            for (Class<? extends ModelElement> elementClass : endElementClassesInPaths) {
                Set<MetaPath> metaPathes = model.getMetaPaths(class1BoxSelection, elementClass, false);
                if (!metaPathes.isEmpty()) {
                    String name = elementsNameBuilder.getDisplayableName(elementClass);
                    class2ComboBox.addObject(elementClass, name);
                }
            }
            selectedMetaPaths.clear();
            selectableMetaPaths.clear();
            deliverChangeEvent(class1ComboBox);
        } else if (eventSource == class2ComboBox) {
            class2ComboBox.setPopupVisible(false);
            Class<? extends ModelElement> class2BoxSelection = class2ComboBox.getSelectedObject();
            if (class2BoxSelection == null) {
                return;
            }
            Class<? extends ModelElement> class1BoxSelection = class1ComboBox.getSelectedObject();
            selectedMetaPaths.clear();
            selectableMetaPaths = new ArrayList<>(model.getMetaPaths(class1BoxSelection, class2BoxSelection, true));
            Alphabetical.sort(selectableMetaPaths);
            if (selectableMetaPaths != null) {
                int selectableMetaPathsCount = selectableMetaPaths.size();
                if (selectableMetaPathsCount == 1) {
                    MetaPath metaPath = selectableMetaPaths.iterator().next();
                    selectableMetaPaths = new ArrayList<>(1);
                    selectableMetaPaths.add(metaPath);
                    selectedMetaPaths.add(metaPath);
                } else if (selectableMetaPathsCount > 1) {
                    List<NamedObjectContainer<MetaPath>> pathNames = new ArrayList<>(selectableMetaPathsCount);
                    for (MetaPath metaPath : selectableMetaPaths) {
                        String metaPathFullName = metaPath.getFullName();
                        pathNames.add(new NamedObjectContainer<>(metaPath, metaPathFullName));
                    }
                    StringBuilder sb = append(null, "text_path_1");
                    sb.append(" ");
                    //beliebig viele Pfade sind auswählbar
                    if (maxParallelSelectedPaths <= 0 || maxParallelSelectedPaths >= selectableMetaPathsCount) {
                        append(sb, "text_path_2");
                        //maximal 1 Pfad ist auswählbar
                    } else if (maxParallelSelectedPaths == 1) {
                        append(sb, "text_path_3");
                        //es dürfen einer oder mehrere, aber nicht alle zur Verfügung stehenden Pfade ausgewählt werden
                    } else {
                        append(sb, "text_path_4_1");
                        append(sb, " ");
                        append(sb, maxParallelSelectedPaths);
                        append(sb, " ");
                        append(sb, "text_path_4_2");
                    }
                    Object[] selectedArray = new Object[selectableMetaPathsCount];
                    List<?> selected = Arrays.asList(selectedArray);
                    while (selected != null && (selectedMetaPaths.isEmpty() || selectedMetaPaths.size() > maxParallelSelectedPaths)) {
                        selectedMetaPaths.clear();
                        MainFrame mainFrame = getMainFrame();
                        String title = getResString("choice");
                        String message = sb.toString();
                        selected = MultipleOptionPane.showCheckBoxOptionDialog(mainFrame, title, message, pathNames, null, false);
                        for (int i = 0; selected != null && i < selected.size(); i++) {
                            Object selectedI = selected.get(i);
                            if (selectedI != null) {
                                @SuppressWarnings("unchecked")
                                NamedObjectContainer<MetaPath> metaPathCont = (NamedObjectContainer<MetaPath>) selectedI;
                                MetaPath metaPath = metaPathCont.getObject();
                                selectedMetaPaths.add(metaPath);
                            }
                        }
                    }
                }
                if (metaPathJList != null) {
                    metaPathJList.setItems(selectableMetaPaths);
                }
            }
            deliverChangeEvent(class2ComboBox);
        } else {
            deliverChangeEvent(e.getSource());
        }
    }

    /**
     * @param sb
     * @param resKey
     * @return
     */
    private StringBuilder append(StringBuilder sb, final Object resKeyOrToStringObject) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        String s = String.valueOf(resKeyOrToStringObject);
        if (resKeyOrToStringObject instanceof String) {
            s = Tool3lgmConstants.getResStringWithoutError(s);
        }
        sb.append(s);
        return sb;
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
            dialogMetaPathSelecor.metaPathJList = new AlphabeticalJList();
        }
        if (maxParallelSelectedPaths <= 1) {
            dialogMetaPathSelecor.metaPathJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            dialogMetaPathSelecor.metaPathJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        Object[] metaPathSelectorMessage = {
                class1Label, dialogMetaPathSelecor.getClass1ComboBox(), class2Label, dialogMetaPathSelecor.getClass2ComboBox(), metaPathListLabel, new JScrollPane(dialogMetaPathSelecor.metaPathJList)
        };
        JOptionPane op = new JOptionPane();
        op.setMessage(metaPathSelectorMessage);
        MainFrame mainFrame = Static.getMainFrame();
        String message = Tool3lgmConstants.getResString("metapath_selection");
        JDialog dialog = op.createDialog(mainFrame, message);

        dialog.setVisible(true);
        dialogMetaPathSelecor.selectedMetaPaths.clear();
        Object selectedMetaPath = dialogMetaPathSelecor.metaPathJList.getSelectedObject();
        if (selectedMetaPath != null) {
            dialogMetaPathSelecor.selectedMetaPaths.add((MetaPath) selectedMetaPath);
        }
        return dialogMetaPathSelecor;
    }

    /**
     * @return Returns the class1ComboBox.
     */
    public AlphabeticalComboBox<Class<? extends ModelElement>> getClass1ComboBox() {
        return class1ComboBox;
    }

    /**
     * @return Returns the class2ComboBox.
     */
    public AlphabeticalComboBox<Class<? extends ModelElement>> getClass2ComboBox() {
        return class2ComboBox;
    }

    /**
     * @return Returns the showPartsOnlyCheckBox
     */
    public JCheckBox getShowPartsOnlyCheckBox() {
        return showPartsOnlyCheckBox;
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

    /**
     * @return
     */
    public MetaPathSelection getSelection() {
        MetaPathSelection selection = new MetaPathSelection();
        selection.class1 = class1ComboBox.getSelectedObject();
        selection.class2 = class2ComboBox.getSelectedObject();
        selection.showPartsOnly = showPartsOnlyCheckBox.isSelected();
        selection.selectedMetaPaths = ImmutableList.copyOf(selectedMetaPaths); // es muss unbedingt eine Kopie sein!
        return selection;
    }

    /**
     * @param selection
     */
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
        public List<MetaPath> selectedMetaPaths;

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
