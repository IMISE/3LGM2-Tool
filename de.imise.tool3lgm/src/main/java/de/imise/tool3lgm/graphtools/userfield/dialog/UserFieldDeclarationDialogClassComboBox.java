package de.imise.tool3lgm.graphtools.userfield.dialog;

import static de.imise.tool3lgm.graphtools.elements.ModelConstants.ALL_EDGES;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.ALL_NODES;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.getDisplayableName;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.getFullBackwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.getFullForwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;
import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.getDisplayableGlobalFieldIdentifierName;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.util.swing.component.AlphabeticalComboBox;

public class UserFieldDeclarationDialogClassComboBox extends AlphabeticalComboBox {

    /**
     * @param maxRowCount
     */
    public UserFieldDeclarationDialogClassComboBox(final int maxRowCount) {
        super();
        setMaximumRowCount(13);
        addItem(GLOBAL_USERFIELD_IDENTIFIER_CLASS, getDisplayableGlobalFieldIdentifierName());
        addSeparator(true);
        //alle nicht abstracten Knotenklassen hinzufügen
        for (Class<? extends ModelElement> elementClass : ALL_NODES) {
            if (!ModelConstants.isAbstract(elementClass)) {
                addItem(elementClass, getDisplayableName(elementClass));
            }
        }
        addSeparator(true);
        //alle Kantenklassen jeweils mit hin und Rückrichtung
        for (Class<? extends Kante> edgeClass : ALL_EDGES) {
            addItem(edgeClass, getFullForwardMetaAssociationName(edgeClass));
            addItem(edgeClass, getFullBackwardMetaAssociationName(edgeClass));
        }
    }

    public void selectFirstItem() {
        setSelectedIndex(0);
    }

    public Class<? extends UserFieldTarget> getSelectedClass() {
        return (Class<? extends UserFieldTarget>) getSelectedObject();
    }

    public boolean isGlobalUserFieldClassSelected() {
        return getSelectedClass() == GLOBAL_USERFIELD_IDENTIFIER_CLASS;
    }

}
