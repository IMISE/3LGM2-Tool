package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * @author AXS (03.05.2007)
 */
public class UserFieldDeclarationDialogClassComboBox extends AlphabeticalComboBox<Class<? extends UserFieldTarget>> {

    /**
     *
     */
    private static int lastSelectedIndex = 0;

    /**
     * @param metaModel
     * @param maxRowCount
     */
    public UserFieldDeclarationDialogClassComboBox(final MetaModel metaModel, final int maxRowCount) {
        setMaximumRowCount(13);
        addObject(GLOBAL_USERFIELD_IDENTIFIER_CLASS, getResString("userFieldEditor_global"));
        addSeparator(true);
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        //alle nicht abstracten Knotenklassen hinzufügen
        for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
            if (!CoreMetaModel.isAbstract(elementClass)) {
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
            if (!CoreMetaModel.isMultipleEdgeClass(edgeClass)) {
                addObject(edgeClass, elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass));
                addObject(edgeClass, elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass));
            }
        }
    }

    /**
     *
     */
    public void restoreSelection() {
        try { //metamodel changed -> index maybe invalid
            setSelectedIndex(lastSelectedIndex < 0 ? 0 : lastSelectedIndex);
        } catch (Exception e) {
            setSelectedIndex(0);
        }
    }

    /**
     * @return
     */
    public Class<? extends UserFieldTarget> getSelectedClass() {
        return getSelectedObject();
    }

    /**
     * @return
     */
    public boolean isGlobalUserFieldClassSelected() {
        return getSelectedClass() == GLOBAL_USERFIELD_IDENTIFIER_CLASS;
    }

    /**
     * @return
     */
    public boolean isNodeClassSelected() {
        Class<? extends UserFieldTarget> selectedClass = getSelectedClass();
        return Node.class.isAssignableFrom(selectedClass);
    }

    @Override
    public int setSelectedObject(final Class<? extends UserFieldTarget> anObject) {
        lastSelectedIndex = super.setSelectedObject(anObject);
        return lastSelectedIndex;
    }

}
