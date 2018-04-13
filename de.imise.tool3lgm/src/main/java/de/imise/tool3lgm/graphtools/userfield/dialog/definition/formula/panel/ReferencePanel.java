/*
 * Created on 26.05.2008 Window - Preferences - Java - Code Style - Code Templates
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayableName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getEdgeTypes;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getFullForwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;
import static javax.swing.BorderFactory.createTitledBorder;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.HasPartEdge;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.util.swing.component.list.AlphabeticalJList;

/**
 * Das <code>ReferncePanel</code> wird genutzt um für Kennzahlformeln an Kanten die Verrechunugsfunktion Reference zu definieren.
 *
 * @author hboehme
 */
public class ReferencePanel extends JPanel implements ActionListener {

    /**
     * die Klasse der Edge, zu der die gebunden Elemente und wiederum deren <code>UserField</code>s gesucht werden.
     */
    private final Class<? extends Edge> edgeClass;

    /**
     * Das neue <code>UserField</code>
     */
    private final UserField userField;

    /**
     * Das Array, dass alle Elementklassen entählt. Von diesen Elementklassen können <code>UserField></code>s ausgewählt werden.
     */
    private Class<? extends ModelElement>[] classes;

    /**
     * Wenn die Edge eine Teil-Von Beziehung ist, wird dieses Panel angezeigt. Darin befinden sich die Radion Button zum Kennzeichen der Richtung
     */
    private JPanel directionPanel;

    /**
     * Wenn die Edge eine Teil-Von Beziehung ist, kann der RadioButton die Richung gekennzeichnen
     */
    private JRadioButton vtzmRB;

    /**
     * Wenn die Edge eine Teil-Von Beziehung ist, kann der RadioButton die Richung gekennzeichnen
     */
    private JRadioButton vgzmRB;

    /**
     * Diese <code>AlphabeticalJList</code> hält die userFields der Start bzw. Endklassen
     */
    private AlphabeticalJList userFieldList;

    /**
     * Die <code>startClassesList</code> hält die möglichen Klassen der Startklassen
     */
    private AlphabeticalJList classesList;

    /**
     * @param userField
     */
    public ReferencePanel(final UserField userField) {
        super();
        edgeClass = userField.getTargetClass().asSubclass(Edge.class);
        this.userField = userField;
        init();
    }

    /**
     * Die GUI aufbauen
     */
    @SuppressWarnings("unchecked")
    private void init() {
        setLayout(new GridBagLayout());
        classes = new Class[1];
        classes[0] = getEndClass(edgeClass);
        Class<? extends ModelElement>[] tmpClasses = new Class[1];
        tmpClasses[0] = getStartClass(edgeClass);

        ArrayList<Class<? extends ModelElement>> tmpList = new ArrayList<>();

        for (int i = 0; i < classes.length; i++) {
            tmpList.add(classes[i]);
        }

        for (int i = 0; i < tmpClasses.length; i++) {
            if (!tmpList.contains(tmpClasses[i])) {
                tmpList.add(tmpClasses[i]);
            }
        }
        classes = tmpList.toArray(classes);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(0, 3, 0, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel(getResString("headline_reference_panel")), gbc);
        gbc.gridy++;

        add(new JLabel(getResString("Edge") + ": " + getFullForwardMetaAssociationName(edgeClass)), gbc);
        ButtonGroup bg = new ButtonGroup();
        directionPanel = new JPanel(new BorderLayout());
        directionPanel.setBorder(BorderFactory.createTitledBorder(getResString("direction_information")));
        vtzmRB = new JRadioButton(getResString("part_to_whole"));
        vgzmRB = new JRadioButton(getResString("whole_to_part"));
        directionPanel.add(vtzmRB, BorderLayout.WEST);
        directionPanel.add(vgzmRB, BorderLayout.EAST);
        bg.add(vtzmRB);
        bg.add(vgzmRB);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        if (HasPartEdge.class.isAssignableFrom(edgeClass) || getEdgeTypes(edgeClass).length != 0) {
            add(directionPanel, gbc);
        } else {
            directionPanel.setVisible(false);
        }

        JPanel attributePanel = new JPanel(new GridBagLayout());
        attributePanel.setBorder(createTitledBorder(getResString("attributes")));

        classesList = new AlphabeticalJList();
        classesList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

        classesList.addListSelectionListener(e -> {
            clearUserFieldList();
            setUserFields();
        });

        GridBagConstraints apgbc = new GridBagConstraints();
        apgbc.insets = new Insets(3, 0, 3, 0);
        apgbc.gridx = 0;
        apgbc.gridy = 0;
        apgbc.anchor = GridBagConstraints.WEST;
        apgbc.fill = GridBagConstraints.BOTH;
        apgbc.weightx = 0.5;
        setClassesInLists(classesList);
        attributePanel.add(new JLabel(getResString("element_class")), apgbc);
        apgbc.gridy++;

        attributePanel.add(new JScrollPane(classesList), apgbc);

        apgbc.gridy++;
        attributePanel.add(new JLabel(getResString("attributes")), apgbc);
        apgbc.gridy++;
        apgbc.gridwidth = 2;
        apgbc.fill = GridBagConstraints.HORIZONTAL;
        apgbc.weightx = 1;
        userFieldList = new AlphabeticalJList();
        attributePanel.add(new JScrollPane(userFieldList), apgbc);

        gbc.gridy++;
        add(attributePanel, gbc);
    }

    /**
     * Prüft, ob an allen nötigen Stellen Selektionen vorgenommen wurden.
     */
    private boolean verify() {
        if (classesList.getSelectedIndex() == -1) {
            return false;
        }

        if (directionPanel.isVisible()) {
            if (!vgzmRB.isSelected() && !vtzmRB.isSelected()) {
                return false;
            }
        }

        if (userFieldList.getSelectedIndex() == -1) {
            return false;
        }

        return true;
    }

    /**
     * Hier wird der Ergebnis-String zusammengesetzt
     *
     * @return
     */
    public String getRetVal() {
        if (!verify()) {
            return null;
        }
        StringBuilder resultString = new StringBuilder(UserField.ACCOUNTING_FUNCTION_REF);
        resultString.append(" ( ");
        if (classesList.getSelectedIndex() != -1) {
            resultString.append(((Class<?>) classesList.getSelectedObject()).getSimpleName());
        }

        resultString.append(" | ");
        resultString.append(((UserField) userFieldList.getSelectedObject()).getHashCode());

        if (vgzmRB.isSelected()) {
            resultString.append(" | ");
            resultString.append(UserField.DIRECTION_FROM_WHOLE_TO_PART);
        } else if (vtzmRB.isSelected()) {
            resultString.append(" | ");
            resultString.append(UserField.DIRECTION_FROM_PART_TO_WHOLE);
        }

        resultString.append(" )");
        return resultString.toString();
    }

    /**
     * @param classesList
     */
    private void setClassesInLists(final AlphabeticalJList classesList) {
        for (int i = 0; i < classes.length; i++) {
            classesList.addItem(classes[i], getDisplayableName(classes[i]));
        }
    }

    /**
     *
     */
    private void setUserFields() {
        UserFieldDefinitions definitions = userField.getDefinitions();
        Class<? extends ModelElement> selectedClass = ((Class<?>) classesList.getSelectedObject()).asSubclass(ModelElement.class);
        for (UserField uf : definitions.getUserFields(selectedClass)) {
            if (uf.isClassificationUserField()) {
                if (uf.getName().trim().equals("")) {
                    String name = getResString("this_classification_number");
                    userFieldList.addItem(uf, name);
                } else {
                    userFieldList.addItem(uf);
                }
            }
        }
    }

    /**
     *
     */
    private void clearUserFieldList() {
        userFieldList.removeAllElements();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
    }

}
