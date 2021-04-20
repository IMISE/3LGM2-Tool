package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
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
        return dummyMe;
    }

}
