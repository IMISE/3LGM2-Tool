package de.imise.tool3lgm.imexport.csv;

import java.util.HashMap;
import java.util.Map;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * @author AXS
 */
public class DisplayableNameHandler {

    /** */
    private final Map<String, Class<? extends ModelElement>> displayableName2ClassMap = new HashMap<>();

    /**
     * @param metaModel MetaModell, für das die anzeigbaren Namen hier gecached
     *            werden
     */
    public DisplayableNameHandler(final MetaModel metaModel) {
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        for (Class<? extends ModelElement> elementClass : metaModel.allElementsSet) {
            String name = elementsNameBuilder.getDisplayableName(elementClass);
            displayableName2ClassMap.put(name, elementClass);
            if (CoreMetaModel.isEdgeType(elementClass)) {
                //AXS: 28.05.2019: es sieht aus, als würde hier was fehlen
            }
        }
    }

    /**
     * @param displayableName
     * @return
     */
    public Class<? extends ModelElement> getElementClass(final String displayableName) {
        return displayableName2ClassMap.get(displayableName);
    }
}
