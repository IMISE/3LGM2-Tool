package de.imise.tool3lgm.imexport;

import java.util.HashMap;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

public class DisplayableNameHandler {

    private final HashMap<String, Class<? extends ModelElement>> displayableName2ClassMap = new HashMap<String, Class<? extends ModelElement>>();

    public DisplayableNameHandler() {
        init();
    }

    private void init() {
        for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_ELEMENTS) {
            String name = ModelConstants.getDisplayableName(elementClass);
            displayableName2ClassMap.put(name, elementClass);
        }
    }

    public Class<? extends ModelElement> getElementClass(final String displayableName) {
        return displayableName2ClassMap.get(displayableName);
    }
}
