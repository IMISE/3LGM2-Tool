package de.imise.tool3lgm.imexport;

import java.util.HashMap;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

public class DisplayableNameHandler {

    private final HashMap<String, Class<? extends ModelElement>> displayableName2ClassMap = new HashMap<String, Class<? extends ModelElement>>();

    public DisplayableNameHandler() {
        init();
    }

    private void init() {
        for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_ELEMENTS) {
            String name = ElementsNameBuilder.getDisplayableName(elementClass);
            displayableName2ClassMap.put(name, elementClass);
            if (ModelConstants.isEdgeType(elementClass)) {

            }
        }
    }

    public Class<? extends ModelElement> getElementClass(final String displayableName) {
        return displayableName2ClassMap.get(displayableName);
    }
}
