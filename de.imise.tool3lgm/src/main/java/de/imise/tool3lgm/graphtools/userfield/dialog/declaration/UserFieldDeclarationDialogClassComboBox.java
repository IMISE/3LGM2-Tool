package de.imise.tool3lgm.graphtools.userfield.dialog.declaration;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions.GLOBAL_USERFIELD_IDENTIFIER_CLASS;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.util.NamedObjectContainer;
import de.imise.util.htmlxml.HTMLConverter;
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
     *
     */
    private final UserFieldDefinitions userFieldDefinitions;

    /**
     * @param userFieldDefinitions
     * @param maxRowCount
     */
    public UserFieldDeclarationDialogClassComboBox(final UserFieldDefinitions userFieldDefinitions, final int maxRowCount) {
        setMaximumRowCount(maxRowCount);
        this.userFieldDefinitions = userFieldDefinitions;
        addClasses();
    }

    /**
     *
     */
    private void addClasses() {
        addObject(GLOBAL_USERFIELD_IDENTIFIER_CLASS, getResString("userFieldEditor_global"));
        addSeparator(true);
        MetaModel metaModel = userFieldDefinitions.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        //alle nicht abstracten Knotenklassen hinzufügen
        for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
            if (!CoreMetaModel.isAbstract(elementClass) && !CoreMetaModel.isEdgeType(elementClass)) { //some Edge classes are regisered as nodes too (all association classes)
                String itemName = elementsNameBuilder.getDisplayableFullName(elementClass);
                addObject(elementClass, itemName);
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
        updateClassHighLight();
    }

    /**
     *
     */
    public void updateClassHighLight() {
        MetaModel metaModel = userFieldDefinitions.getMetaModel();
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        for (int i = 0; i < getItemCount(); i++) {
            NamedObjectContainer<Class<? extends UserFieldTarget>> classItem = getItemAt(i);
            Class<? extends UserFieldTarget> targetClass = classItem.getFirstItem();
            if (targetClass == null) {
                continue;
            }
            String itemName = "";
            if (targetClass == GLOBAL_USERFIELD_IDENTIFIER_CLASS) {
                itemName = getResString("userFieldEditor_global");
            } else if (Edge.class.isAssignableFrom(targetClass)) {
                Class<? extends Edge> edgeClass = targetClass.asSubclass(Edge.class);
                itemName = elementsNameBuilder.getFullForwardMetaAssociationName(edgeClass);
                String oldItemName = classItem.toString();
                if (!oldItemName.contains(itemName)) {
                    itemName = elementsNameBuilder.getFullBackwardMetaAssociationName(edgeClass);
                }
            } else { //Nodes
                Class<? extends ModelElement> elementClass = targetClass.asSubclass(Node.class);
                itemName = elementsNameBuilder.getDisplayableFullName(elementClass);
            }
            if (userFieldDefinitions.hasUserFields(targetClass)) {
                itemName = HTMLConverter.getTextAsHTMLLabelTextBold(itemName);
            }
            classItem.setSecondItem(itemName);
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
