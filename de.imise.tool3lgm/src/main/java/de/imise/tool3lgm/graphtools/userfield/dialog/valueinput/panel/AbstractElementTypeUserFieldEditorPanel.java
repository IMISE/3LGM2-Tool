/*
 * Created on 14.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel.InsertType.AS_EDGE_FORWARD_AND_BACKWARD;
import static de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel.InsertType.AS_MODELELEMENT;
import static de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractElementTypeUserFieldEditorPanel.InsertType.NO;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.UserFieldEditorDialog;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTable;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.layout.UserFieldTableLayout;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.AbstractUserFieldTableModel;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.GeneralUserFieldTableModel;
import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * Panel zur Darstellung und Eingabe von UserFields
 * <p>
 * Die Dateneingabe erfolgt für jede Klasse von Knotenelementen separat - die Auswahl der Klasse erfolgt in der enthaltenen
 * <code>elementTypeBox</code>.
 * <p>
 * Wenn es sich um eine Elementklasse mit Tiel-Von-Beziehungen handelt, kann mittels der enthaltenen Auswahlbuttons eine Auswahl der anzuzeigenden
 * Elemente vorgenommenwerden: <br>
 * i) alle Elemente anzeigen <br>
 * ii) nur toplevel Elemente anzeigen <br>
 * iii) nur Blatt-Elemente anzeigen
 *
 * @see de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.AbstractUserFieldEditorPanel
 * @author fstephan, AXS
 */
public abstract class AbstractElementTypeUserFieldEditorPanel extends AbstractUserFieldEditorPanel {

    /** Auswahlbox für den Elementtyp */
    protected AlphabeticalComboBox elementTypeBox;

    /**
     * Rückgabewerte für die Funktion, die prüft, ob eine Elementklasse
     *
     * @author Ich
     * @create 10.11.2015
     */
    protected static enum InsertType {
        NO,
        AS_MODELELEMENT,
        AS_EDGE_FORWARD,
        AS_EDGE_BACKWARD,
        AS_EDGE_FORWARD_AND_BACKWARD
    }

    /**
     * Panel zur Auswahl wleche Elemente in einer Hierarchie gezeigt werden sollen. Nur absolute Oberelemente,
     * Elemente mit Parents und Parts oder nur Blattelemente der Elementhiearchie
     */
    protected ElementTypePane hierarchyTypeFilterPane;

    protected final Set<Style> visibleUserFields;

    private GridBagConstraints constraints;

    private final Class<? extends ModelElement> selectableElementsClass;

    /* ************************* Beginn: Initialisierungsteil *********************************** */
    /**
     * Konstruktor
     *
     * @param dialog Dialog, der dieses Panel enthält
     * @param selectableElementClass Oberklasse aller instaziierbaren Elementklassen, die in der Auswahlbox stehen sollen
     * @param visibleUserField Style der UserFields, die angezeigt werden sollen
     * @param name Name des Panels
     */
    public AbstractElementTypeUserFieldEditorPanel(final UserFieldEditorDialog dialog, final Class<? extends ModelElement> selectableElementClass, final Style visibleUserField, final String name) {
        this(dialog, selectableElementClass, ImmutableSet.of(visibleUserField), name);
    }

    /**
     * Konstruktor
     *
     * @param dialog Dialog, der dieses Panel enthält
     * @param selectableElementClass Oberklasse aller instaziierbaren Elementklassen, die in der Auswahlbox stehen sollen
     * @param visibleUserFields Styles der UserFields, die angezeigt werden sollen
     * @param name Name des Panels
     */
    public AbstractElementTypeUserFieldEditorPanel(final UserFieldEditorDialog dialog, final Class<? extends ModelElement> selectableElementsClass, final Set<Style> visibleUserFields, final String name) {
        super(dialog, name);
        this.selectableElementsClass = selectableElementsClass;
        this.visibleUserFields = visibleUserFields;

        addTypePane();
        addElementTypeBox();
    }

    private void initConstraints() {
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 5, 0, 5);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
    }

    private void addTypePane() {
        hierarchyTypeFilterPane = new ElementTypePane();
        hierarchyTypeFilterPane.setVisible(false);
        addComponent(hierarchyTypeFilterPane);
    }

    private void addElementTypeBox() {
        // elementTypeBox initialisieren
        elementTypeBox = new AlphabeticalComboBox(10);
        setActionsForElementTypeBox();
        setElementTypeBoxContent();
        addComponent(elementTypeBox);
    }

    protected void addComponent(final JComponent component) {
        if (constraints == null) {
            initConstraints();
        }
        this.add(component, constraints);
        constraints.gridy++;
        constraints.insets.set(3, 3, 3, 3);
    }

    /**
     * Methode setzt den Inhalt der <code>elementTypeBox</code> Es werden nur die Klassen von Knotenelementen aufgelistet, für die mindestens ein
     * Kennzahl
     * vom Typ <code>UserField.CLASSIFICATION_NUMBER_STYLE</code> definiert ist.
     */
    private void setElementTypeBoxContent() {
        GraphDocument doc = dialog.getMainDoc();
        GDCollection gdcoll = doc.getCollection();
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
        MetaModel metaModel = gdcoll.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();

        boolean isEdgeType = MetaModel.isEdgeType(selectableElementsClass);
        String resKey = isEdgeType ? "userFieldEditor_edge_type" : "userFieldEditor_element_type";
        elementTypeBox.addSeparator(getResString(resKey));

        for (Class<? extends ModelElement> elementClass : metaModel.allElementsSet) {

            //von allen Elementklassen nur die zulassen, die im Konstruktor angegeben wurde
            if (!selectableElementsClass.isAssignableFrom(elementClass)) {
                continue;
            }

            // füge elementClass in die elementTypeBox ein
            InsertType insertType = getInsertType(elementClass, definitions);
            if (insertType == NO) {
                continue;
            }

            // Falls kein Element der Klasse elementClass exisitiert, füge elementClass NICHT ein
            if (doc.getModelItems(elementClass).isEmpty()) {
                continue;
            }

            if (insertType == AS_MODELELEMENT) {
                elementTypeBox.addItem(elementClass, elementsNameBuilder.getDisplayableName(elementClass));
            } else if (insertType != NO) {
                Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
                if (insertType == InsertType.AS_EDGE_FORWARD || insertType == AS_EDGE_FORWARD_AND_BACKWARD) {
                    elementTypeBox.addItem(edgeClass, elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass));
                }
                if (insertType == InsertType.AS_EDGE_BACKWARD || insertType == AS_EDGE_FORWARD_AND_BACKWARD) {
                    elementTypeBox.addItem(edgeClass, elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass));
                }
            }

        }

        // Falls kein Item enthalten ist, wird ein null-Item hinzugefügt, weil es sonst
        // zu inkorrekter Darstellung der elementTypeBox kommt

        if (elementTypeBox.getItemCount() == 1) {
            elementTypeBox.addItem(new Object(), "         ");
        }

    }

    /**
     * Prüft, ob mindestens ein <code>UserField</code> der übergebenen <code>elementClass</code> vom Typ Kennzahl ist. Dann wird true zurückggegeben
     * ansonsten false.
     *
     * @param elementClass Die <code>ElementClass</code>e, deren <code>UserField</code>s geprüft werden sollen.
     * @param definitions Die <code>UserFieldDefinition</code>s
     * @return Wenn mindestens ein <code>UserField</code> vom Typ Kennzahl (<code>UserField.CLASSIFICATION_NUMBER_STYLE</code>) ist: true; ansonsten
     *         false
     */
    protected InsertType getInsertType(final Class<? extends ModelElement> elementClass, final UserFieldDefinitions definitions) {
        InsertType insertType = NO;
        for (UserField uf : definitions.getUserFields(elementClass)) {
            if (visibleUserFields.contains(uf.getStyle())) {
                boolean isEdgeType = Edge.class.isAssignableFrom(elementClass);
                insertType = isEdgeType ? AS_EDGE_FORWARD_AND_BACKWARD : AS_MODELELEMENT;
                break;
            }
        }
        return insertType;
    }

    /**
     * Methode setzt die auszuführende Action, bei Änderung der Auswahl in der <code>elementTypeBox</code>. Bei Änderung der Auswahl wird das
     * Neuzeichnen des <code>table</code>s ausgelöst.
     */
    protected void setActionsForElementTypeBox() {
        final AbstractUserFieldEditorPanel pane = this;
        elementTypeBox.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                pane.update();
            }
        });
    }

    @Override
    protected UserFieldTable initTable() {
        UserFieldTableLayout uftl = new UserFieldTableLayout();
        UserFieldTable table = new UserFieldTable(uftl);
        return table;
    }

    @Override
    protected Object constraintsForTable() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 3;
        return constraints;
    }

    /* ************************* Ende: Initialisierungsteil *********************************** */

    /* ************************* Beginn: Funktionale Methoden *********************************** */

    protected abstract UserFieldTableController getTableController(final AbstractUserFieldTableModel uftm);

    protected AbstractUserFieldTableModel getTableModel() {
        Class<? extends ModelElement> selectedClass = ((Class<?>) elementTypeBox.getSelectedObject()).asSubclass(ModelElement.class);
        GraphDocument doc = dialog.getMainDoc();
        GeneralUserFieldTableModel uftm = new GeneralUserFieldTableModel(doc, selectedClass, hierarchyTypeFilterPane.showTopLevel(), hierarchyTypeFilterPane.showInner(), hierarchyTypeFilterPane.showLeafs(), visibleUserFields);
        return uftm;
    }

    protected boolean hasSelectedItem() {
        boolean hasSelectedItem = elementTypeBox.getSelectedObject() instanceof Class;
        return hasSelectedItem;
    }

    @Override
    protected void initSelectFirstItem() {
        //das hier darf man nicht durch hasSelectedItem() ersetzen, weil das in den Unterklassen
        //überschrieben sein kann
        if (!(elementTypeBox.getSelectedObject() instanceof Class)) {
            //das erste Item ist entweder das Dummy-LeerItem oder eine Elemnent-Klasse
            elementTypeBox.setSelectedIndex(1);
        }
    }

    @Override
    protected void drawTable() {
        table.removeFromLayoutContainer();
        if (!hasSelectedItem()) {
            return;
        }
        Class<? extends ModelElement> selectedClass = ((Class<?>) elementTypeBox.getSelectedObject()).asSubclass(ModelElement.class);
        MetaModel metaModel = dialog.getMetaModel();
        if (metaModel.canHavePartsOrParents(selectedClass)) {
            hierarchyTypeFilterPane.setVisible(true);
        }
        AbstractUserFieldTableModel uftm = getTableModel();
        //falls SubClasses andere Controller brauchen, müssen sie die getTableController überschreiben
        UserFieldTableController uftc = getTableController(uftm);
        super.modifyTable(uftm, uftc);
    }

    @Override
    public void takeOver() {
        if (!(table.getModel() instanceof AbstractUserFieldTableModel)) {
            return; // noch keine Werte vorhanden
        }
        AbstractUserFieldTableModel uftm = (AbstractUserFieldTableModel) table.getModel();
        if (uftm.dataChanged() == false) {
            return;
        }
        GraphDocument doc = dialog.getMainDoc();
        Vector<NamedObjectContainer<?>> rowIdentifiers = uftm.getRowIdentifiers();
        Vector<NamedObjectContainer<?>> columnIdentifiers = uftm.getColumnIdentifiers();
        GDCollection gdcoll = doc.getCollection();
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
        for (int i = 0; i < uftm.getRowCount(); i++) {
            // Das ModelElement in der i-ten Zeile
            ModelElement me = (ModelElement) rowIdentifiers.elementAt(i).getObject();
            for (int k = 0; k < uftm.getColumnCount(); k++) {
                // Wert an der Stelle (i,k)
                String newValue = uftm.getValueAt(i, k).toString();
                // Die Kennzahl in der Spalte k
                UserField uf = (UserField) columnIdentifiers.elementAt(k).getObject();
                // neuen Wert setzen
                if (!newValue.equals(uf.getValue(me))) {
                    int pid = dialog.getTransactionID();
                    doc.setUserFieldValue(me.getHashString(), uf.getHashCode(), newValue, pid);
                }
            }
        }
        uftm.dataChanged(false);
        //Das reset durchführen kann auch ganz auf das Ende verlegt werden
        definitions.initReset();
    }

    /* ************************* Ende: Funktionale Methoden *********************************** */

    /* ************************* Beginn: get/set - Methoden *********************************** */

    /**
     * Gibt die <code>elementTypeBox</code> zurück
     *
     * @return <code>elementTypeBox</code>
     */
    public AlphabeticalComboBox getElementTypeBox() {
        return elementTypeBox;
    }

    /* ************************* Ende: get/set - Methoden *********************************** */

    /* ************************* Beginn: Unterklassen *********************************** */

    /**
     * Panel zur Auswahl der anzuzeigenden Element-Typen:
     * <li>Top-Level
     * <li>Innere
     * <li>Blätter
     */
    protected class ElementTypePane extends JPanel {

        // CheckBoxes für die Auswahl der anzuzeigenden Elemente
        private final JCheckBox topLevel;
        private final JCheckBox inner;
        private final JCheckBox leafs;

        public ElementTypePane() {
            topLevel = new JCheckBox(getActionForButtons(getResString("hierarchy_panel_show_toplevel")));
            inner = new JCheckBox(getActionForButtons(getResString("hierarchy_panel_show_inner")));
            leafs = new JCheckBox(getActionForButtons(getResString("hierarchy_panel_show_leafs")));
            topLevel.setSelected(true);
            inner.setSelected(true);
            leafs.setSelected(true);
            add(topLevel);
            add(inner);
            add(leafs);
        }

        /**
         * Gibt die auzuführenden Aktionen für die CheckBoxes wieder. Bei Änderung der Selektion werden Änderungen temporär übernommen und das
         * Neuzeichnen des Tables ausgelöst.
         *
         * @param panel
         * @param buttonLabel
         * @return
         */
        private AbstractAction getActionForButtons(final String buttonLabel) {
            return new AbstractAction(buttonLabel) {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    AlphabeticalComboBox box = getElementTypeBox();
                    if (box.getSelectedObject() == null || !(box.getSelectedObject() instanceof Class)) {
                        return;
                    }
                    // Beenden des editierens, damit auch aktueller Wert übernommen wird
                    stopEditing();
                    // Änderungen werden übernommen
                    takeOver();
                    // Aktualisieren des Tables
                    drawTable();
                    distributeSelectionChangedEvent();
                }
            };
        }

        /** Gibt zurück, ob {@link #topLevel} selektiert ist */
        public boolean showTopLevel() {
            return topLevel.isSelected();
        }

        /** Gibt zurück, ob {@link #leafs} selektiert ist */
        public boolean showLeafs() {
            return leafs.isSelected();
        }

        /** Gibt zurück, ob {@link #inner} selektiert ist */
        public boolean showInner() {
            return inner.isSelected();
        }

    }

    /* ************************* Ende: Unterklassen ************************************* */

}
