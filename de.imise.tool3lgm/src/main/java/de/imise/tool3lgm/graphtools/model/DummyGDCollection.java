package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * A class to create dummy models. Is needed to display the userFedined
 * Properties for dummy elements.
 *
 * @author AXS (20.04.2021)
 */
public final class DummyGDCollection extends GDCollection {

    /**
     * @param modelType
     */
    public DummyGDCollection(final Tool3lgmModelType modelType) {
        super(modelType);
    }

    /**
     * @param <T>
     * @param elementClass
     * @return
     */
    public ModelElement getDummyElement(final Class<? extends ModelElement> elementClass) {
        LGMGraphDocument mainDoc = getMainDoc();
        ElementsNameBuilder elementsNameBuilder = getElementsNameBuilder();
        String displayableClassName = elementsNameBuilder.getDisplayableName(elementClass);
        String name = getResString("dummy_element_name") + " " + displayableClassName;
        String description = getResString("dummy_element_description");
        NodeContainer dummyNc = mainDoc.createNodeAndContainer(elementClass, name, description, STANDARD_PID);
        ModelElement dummyMe = dummyNc.getElement();
        fillUSerFieldsWithDummyValues(dummyMe);
        return dummyMe;
    }

    int numberValueCounter = 1;

    private void fillUSerFieldsWithDummyValues(final ModelElement me) {
        Class<? extends ModelElement> elementClass = me.getClass();
        UserFieldDefinitions userFieldDefinitions = getUserFieldDefinitions();
        for (UserField userField : userFieldDefinitions.getUserFields(elementClass)) {
            Style style = userField.getStyle();
            switch (style) {
            case CHECK_BOX:
                me.setUserFieldInputValue(userField, "true");
                break;
            case COMBO_BOX:
            case RADIO_BUTTON:
                int listValuesCount = userField.getListValuesCount();
                if (listValuesCount > 0) {
                    String firstValue = userField.getListValueAt(0);
                    me.setUserFieldInputValue(userField, firstValue);
                }
                break;
            case NUMBER:
                Double d = numberValueCounter + numberValueCounter / 2d + numberValueCounter++ / 3d;
                me.setUserFieldInputValue(userField, d.toString());
                break;
            case HYPERLINK:
                me.setUserFieldInputValue(userField, "https://www.3lgm2.de");
                break;
            case ID:
                me.setUserFieldInputValue(userField, String.valueOf(numberValueCounter));
                break;
            case MULTI_LINE:
                me.setUserFieldInputValue(userField,
                        "Lorem ipsum dolor sit amet, consectetur adipisici elit,\nsed eiusmod tempor incidunt ut labore et dolore magna aliqua.\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat.");
                break;
            case SINGLE_LINE:
                me.setUserFieldInputValue(userField, "Lorem ipsum dolor sit amet, consectetur adipisici elit.");
                break;

            default:
                break;
            }
        }
    }

}
