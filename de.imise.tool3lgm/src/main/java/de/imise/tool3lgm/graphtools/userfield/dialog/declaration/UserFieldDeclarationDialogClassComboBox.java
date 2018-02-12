package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.ALL_NODES;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getDisplayableName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getFullBackwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.metamodel.ModelConstants.getFullForwardMetaAssociationName;
import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;
import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.getDisplayableGlobalFieldIdentifierName;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.util.swing.component.AlphabeticalComboBox;

public class UserFieldDeclarationDialogClassComboBox extends AlphabeticalComboBox {

    private static int lastSelectedIndex = 0;

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
        for (Class<? extends Edge> edgeClass : ModelConstants.ALL_EDGES_SET) {
            //Im Moment geht der ganze Mechanismus davon aus, dass es immer nur eine derselben Art Kante zwischen
            //2 Elementen geben kann. D.h. bei MultipleEges würde immer nur die erste Kante beachtet werden.
            //Da weder druchdacht ist noch ausprobiert wurde, was passiert, wenn man Kanten mehrfach verknüpft
            //und dann mit Verteilungegewichten bestückt, sind diese Kanten hier erstmal ausßen vor.
            if (!ModelConstants.isMultipleEdgeClass(edgeClass)) {
                addItem(edgeClass, getFullForwardMetaAssociationName(edgeClass));
                addItem(edgeClass, getFullBackwardMetaAssociationName(edgeClass));
            }
        }
    }

    public void restoreSelection() {
        setSelectedIndex(lastSelectedIndex);
    }

    public Class<? extends UserFieldTarget> getSelectedClass() {
        return (Class<? extends UserFieldTarget>) getSelectedObject();
    }

    public boolean isGlobalUserFieldClassSelected() {
        return getSelectedClass() == GLOBAL_USERFIELD_IDENTIFIER_CLASS;
    }

    @Override
    public void setSelectedItem(final Object anObject) {
        super.setSelectedItem(anObject);
        lastSelectedIndex = getSelectedIndex();
    }

}
