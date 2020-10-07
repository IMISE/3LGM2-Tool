package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;
import static de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions.getDisplayableGlobalFieldIdentifierName;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.util.swing.component.AlphabeticalComboBox;

public class UserFieldDeclarationDialogClassComboBox extends AlphabeticalComboBox<Class<? extends UserFieldTarget>> {

    private static int lastSelectedIndex = 0;

    /**
     * @param metaModel
     * @param maxRowCount
     */
    public UserFieldDeclarationDialogClassComboBox(final MetaModel metaModel, final int maxRowCount) {
        super();
        setMaximumRowCount(13);
        addObject(GLOBAL_USERFIELD_IDENTIFIER_CLASS, getDisplayableGlobalFieldIdentifierName());
        addSeparator(true);
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        //alle nicht abstracten Knotenklassen hinzufügen
        for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
            if (!MetaModel.isAbstract(elementClass)) {
                addObject(elementClass, elementsNameBuilder.getDisplayableFullName(elementClass));
            }
        }
        addSeparator(true);
        //alle Kantenklassen jeweils mit hin und Rückrichtung
        for (Class<? extends Edge> edgeClass : metaModel.allEdgesSet) {
            //Im Moment geht der ganze Mechanismus davon aus, dass es immer nur eine derselben Art Kante zwischen
            //2 Elementen geben kann. D.h. bei MultipleEges würde immer nur die erste Kante beachtet werden.
            //Da weder druchdacht ist noch ausprobiert wurde, was passiert, wenn man Kanten mehrfach verknüpft
            //und dann mit Verteilungegewichten bestückt, sind diese Kanten hier erstmal ausßen vor.
            if (!MetaModel.isMultipleEdgeClass(edgeClass)) {
                addObject(edgeClass, elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass));
                addObject(edgeClass, elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass));
            }
        }
    }

    public void restoreSelection() {
        setSelectedIndex(lastSelectedIndex);
    }

    public Class<? extends UserFieldTarget> getSelectedClass() {
        return getSelectedObject();
    }

    public boolean isGlobalUserFieldClassSelected() {
        return getSelectedClass() == GLOBAL_USERFIELD_IDENTIFIER_CLASS;
    }

    @Override
    public int setSelectedObject(final Class<? extends UserFieldTarget> anObject) {
        lastSelectedIndex = super.setSelectedObject(anObject);
        return lastSelectedIndex;
    }

}
