package de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.list.AlphabeticalJList;

/**
 * Dieses Panel stellt die möglichen auswählbaren Attribute für die Verrechnungsfunktionen bereit.
 *
 * @author hboehme, AXS
 * @created 02.01.2008
 */
public class OperatorInputPanel extends JPanel implements ActionListener {

    /**
     * Das UserField, für welches die verrechnungsfunktion Summe definiert wird.
     */
    private final UserField userField;

    /**
     * ist die <code>ComboBox</code>, die in alphabetischer Reihenfolge die Assoziationen zwischen Start- und Endklasse anzeigt.
     */
    private AlphabeticalComboBox associationBox;

    /**
     * Ist die <code>ComboBox</code>, die die UserField der Endklasse anzeigt.
     */
    private AlphabeticalComboBox connectedAttributesBox;

    /**
     * die Box, die die Verteilungsgewichte enthält. <code>vgBox</code>
     */
    private AlphabeticalComboBox vgBox;

    /**
     * "Gleichverteilt" in der gewählten Loacle. Wird angezeigt, wenn als Verteilungsgewicht bei einer Verrechnung über die Teilwertsumme kein
     * spezielles Verteilungsgewicht genutzt werden soll.
     */
    private static final String UNIFORMLY_DISTRIBUTED = Tool3lgmConstants.getResString("uniformly_distributed");

    /**
     * Eine der beiden Konstanten <code>UserField.SUM</code> oder <code>UserField.TWSUM</code>. Legt die Art der verrechnungsfunktion fest.
     */
    private final String vfOperator;

    /**
     * Das Panel, dass die ComboBoxen darstellt.
     *
     * @param vfOperator Eine der beiden Konstanten <code>UserField.SUM</code> oder <code>UserField.TWSUM</code>
     * @param classElement Elementklasse, die sich über die Verrechnungsfunktion irgendeinen Wert einer verbundenen Klasse holen soll
     * @param userField
     */
    public OperatorInputPanel(final String vfOperator, final UserField userField) {
        super();
        this.userField = userField;
        this.vfOperator = vfOperator;
        init();
    }

    /**
     * Initialisiert die GUI-Elemente
     */
    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 3, 3, 3);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridy = 0;
        add(new JLabel(Tool3lgmConstants.getResString("target_of_accounting") + " -> " + Tool3lgmConstants.getResString("source_of_accounting")), gbc);
        associationBox = new AlphabeticalComboBox();
        associationBox.addActionListener(this);
        gbc.insets.top = 3;
        gbc.gridy++;
        gbc.weightx = 1;
        add(associationBox, gbc);
        updateFieldListAssoziation();
        gbc.gridy++;

        add(new JLabel(Tool3lgmConstants.getResString("connected_attributes")), gbc);
        connectedAttributesBox = new AlphabeticalComboBox();
        gbc.gridy++;
        add(connectedAttributesBox, gbc);
        gbc.gridy++;
        gbc.weighty = 0;

        gbc.gridy++;
        gbc.weighty = 1;

        if (vfOperator.equals(UserField.ACCOUNTING_FUNCTION_TWSUM)) {
            gbc.gridy++;
            add(new JLabel(Tool3lgmConstants.getResString("weighting") + ": "), gbc);

            vgBox = new AlphabeticalComboBox();
            vgBox.addActionListener(this);
            gbc.gridy++;
            add(vgBox, gbc);
        }

        //nur ein Leer-Label, damit die ComboBoxen am Oberen Rand bleiben.
        //Der Anchor funktioniert noch nicht so, wie er soll.
        add(new JLabel(), gbc);
    }

    /**
     * Leert die <code>ComboBox</code>, die die UserFields der Endklasse enthält.
     */
    private void clearBoxAttributsOfAssociatedClass() {
        connectedAttributesBox.removeAllItems();
    }

    /**
     * Aktualisiert die {@link AlphabeticalJList}, die die {@link UserField}s der assoziierten Klasse enthält.
     *
     * @param elementClasses
     */
    private final void updateFieldListAttributesOfAssociatedClass(final Class<? extends ModelElement> elementClass) {
        clearBoxAttributsOfAssociatedClass();
        //Manchmal hat das userfield nicht die aktuellen Definitions
        UserFieldDefinitions definitions = userField.getDefinitions().getCollection().getUserFieldDefinitions();
        //wird true, wenn dieses UserField bereits in den Definitions vorkommt
        // (also nicht grade angelegt wurde)
        boolean found = false;
        //wird true, wenn die Elementklasse, für die das aktuelle UserField definiert wird, zuweisungskompatibel
        //zu einer der Zielklassen der ausgewählten Kante ist -> das aktuelle Userfield muss sebst in der Auswahlliste
        //erscheinen
        boolean assignableClass = false;
        if (elementClass.isAssignableFrom(userField.getTargetClass())) {
            assignableClass = true;
        }

        //TODO: das hier sollte eine eigen Funktion der UserFieldDefinitions werden

        //es
        HashSet<Class<? extends ModelElement>> elementClassAssignable = new HashSet<Class<? extends ModelElement>>();
        for (Class<? extends ModelElement> assClass : ModelConstants.ALL_NODES_SET) {
            if (elementClass.isAssignableFrom(assClass)) {
                elementClassAssignable.add(assClass);
            }
        }
        for (Class<? extends ModelElement> assClass : ModelConstants.ALL_EDGES_SET) {
            if (elementClass.isAssignableFrom(assClass)) {
                elementClassAssignable.add(assClass);
            }
        }
        HashSet<Class<? extends ModelElement>> allElementClassAssignable = new HashSet<Class<? extends ModelElement>>();
        for (Class<? extends ModelElement> assClass : elementClassAssignable) {
            allElementClassAssignable.add(assClass);
            while (assClass != elementClass) {
                allElementClassAssignable.add(assClass);
                assClass = assClass.getSuperclass().asSubclass(ModelElement.class);
            }
        }
        allElementClassAssignable.add(elementClass);

        for (Class<? extends UserFieldTarget> assClass : allElementClassAssignable) {
            for (UserField uf : definitions.getUserFields(assClass)) {
                if (uf == userField) {
                    found = true;
                }
                if (uf.isClassificationUserField()) {
                    if (uf.getName().trim().equals("")) {
                        String name = Tool3lgmConstants.getResString("this_classification_number");
                        connectedAttributesBox.addSeparator(true);
                        connectedAttributesBox.addItem(uf, name);
                    } else {
                        connectedAttributesBox.addItem(uf);
                    }
                }
            }
        }
        if (found && !assignableClass) {
            connectedAttributesBox.removeItem(userField);
        } else if (!found && assignableClass) {
            String name = userField.getName().trim();
            if (name.equals("")) {
                name = Tool3lgmConstants.getResString("this_classification_number");
            }
            connectedAttributesBox.addSeparator(true);
            connectedAttributesBox.addItem(userField, name);
        }
    }

    /**
     * Aktualisiert die ComboBox, die die Verteilungsgewichte aktuell ausgewählten Kantenklasse beinhaltet.
     *
     * @param edgeClass die aktuelle Kantenklasse
     */
    private void updateVGComboBoxItems(final Class<? extends ModelElement> edgeClass) {

        vgBox.removeAllItems();

        UserFieldDefinitions definitions = userField.getDefinitions();

        vgBox.addItem(UNIFORMLY_DISTRIBUTED);
        vgBox.addSeparator(false);
        for (UserField uf : definitions.getUserFields(edgeClass)) {
            vgBox.addItem(uf);
        }
        vgBox.setSelectedObject(UNIFORMLY_DISTRIBUTED);
    }

    //TODO: man sollte das Öffnen des Dialoges verhindern, wenn keine verrechenbaren Kennzehlen in verbundenen Klassen existieren

    /**
     * Aktualisiert die Box der Assoziationen der Startklasse.
     */
    private final void updateFieldListAssoziation() {
        associationBox.removeAllItems();
        //Die Assoziationen zur Box hinzufügenen, bei denen die Startklasse
        // gleich der Element-Klasse ist
        Class<? extends Kante>[] edgeClasses = ModelConstants.getEdgeTypes(userField.getTargetClass().asSubclass(ModelElement.class));

        for (int i = 0; i < edgeClasses.length; i++) {
            Class<? extends Kante> tmpEdgeClass = edgeClasses[i];
            Class<? extends ModelElement> startClass = Kante.getStartClass(tmpEdgeClass);
            Class<? extends ModelElement> endClass = Kante.getEndClass(tmpEdgeClass);

            //prüfen, ob die Start- und Endklassen gleich ist ->
            //wenn ja, darf diese Kante nur für Verrechnungen genutzt werden, wenn sie
            //in Vorwärts- und Rückwartsrichtung unterschiedliche Bedeutungen (also eine
            //unterscheidliche Bezeichnung) hat. Bei PartOfBeziehungen sind die Start und
            //Zielklassen-Arrays gleich, aber die Namen in Vorwärts- und Rückwärtsrichtung
            //sind unterscheidlich. Bei der Beziehung "Phys.-DV-Baustein ist verbunden mit
            //Phys.-DV-Baustein" kann man keine eindeutige Richtung zuordnen.
            String forwardName = ModelConstants.getFullForwardMetaAssociationName(tmpEdgeClass);
            String backwardName = ModelConstants.getFullBackwardMetaAssociationName(tmpEdgeClass);
            if (forwardName.equals(backwardName) && (startClass.isAssignableFrom(endClass) || endClass.isAssignableFrom(startClass))) {
                continue;
            }
            if (startClass.isAssignableFrom(userField.getTargetClass())) {
                if (PartOfBeziehung.class.isAssignableFrom(tmpEdgeClass)) {
                    associationBox.addItem(tmpEdgeClass, Tool3lgmConstants.getResString("part_to_whole") + " (" + forwardName + ")");
                } else {
                    associationBox.addItem(tmpEdgeClass, forwardName);
                }
            }
            //das hier darf nicht als else-if geschrieben werden!
            if (endClass.isAssignableFrom(userField.getTargetClass())) {
                if (PartOfBeziehung.class.isAssignableFrom(tmpEdgeClass)) {
                    associationBox.addItem(tmpEdgeClass, Tool3lgmConstants.getResString("whole_to_part") + " (" + backwardName + ")");
                } else {
                    associationBox.addItem(tmpEdgeClass, backwardName);
                }
            }
        }
    }

    /**
     * Gibt aus dem Dialog den String zurück, der die Verrechnungsfunktion beschreibt. Z.B: SUM ( Assoziation | Attribut der verbunden Klasse |
     * Richtung )
     *
     * @return
     */
    public String getRetVal() {
        UserField tmpUserField;
        if (connectedAttributesBox.getSelectedIndex() >= 0) {
            tmpUserField = (UserField) connectedAttributesBox.getSelectedObject();
        } else {
            return null;
        }
        Class<? extends Kante> edgeClass;
        if (associationBox.getSelectedIndex() < 0) {
            return null;
        }
        edgeClass = ((Class<? extends Kante>) associationBox.getSelectedObject()).asSubclass(Kante.class);
        //mit Assoziation:

        StringBuilder sb = new StringBuilder(vfOperator);
        sb.append(" ( ");
        sb.append(edgeClass.getSimpleName());
        sb.append(" | ");
        sb.append(tmpUserField.getHashCode());

        //die vgBox ist nur bei TWSUM nicht null
        if (vgBox != null) {
            Object selectedWeight = vgBox.getSelectedObject();
            if (selectedWeight != null && selectedWeight != UNIFORMLY_DISTRIBUTED) {
                UserField uf = (UserField) vgBox.getSelectedObject();
                sb.append(" | ");
                sb.append(uf.getHashCode());
            }
        }

        //wenn eine Part-Of-beziehung ausgewählt wurde, dann die Richtung
        // merken
        if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
            sb.append(" | ");
            if (associationBox.getSelectedItem().toString().equals(Tool3lgmConstants.getResString("part_to_whole") + " (" + ModelConstants.getFullForwardMetaAssociationName(edgeClass) + ")")) {
                sb.append(UserField.DIRECTION_FROM_PART_TO_WHOLE);
            } else {
                sb.append(UserField.DIRECTION_FROM_WHOLE_TO_PART);
            }

        }
        sb.append(" )");

        return sb.toString();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == associationBox) {
            Class<? extends Kante> edgeClass = (Class<? extends Kante>) associationBox.getSelectedObject();
            if (edgeClass == null) {
                return;
            }
            String displayName = associationBox.getSelectedItem().toString();
            String tmp_string = ModelConstants.getFullForwardMetaAssociationName(edgeClass);
            if (displayName.equals(tmp_string)) {
                updateFieldListAttributesOfAssociatedClass(Kante.getEndClass(edgeClass));
            } else {
                updateFieldListAttributesOfAssociatedClass(Kante.getStartClass(edgeClass));
            }
            if (vfOperator.equals(UserField.ACCOUNTING_FUNCTION_TWSUM)) {
                updateVGComboBoxItems(edgeClass);
            }
        }
    }
}
