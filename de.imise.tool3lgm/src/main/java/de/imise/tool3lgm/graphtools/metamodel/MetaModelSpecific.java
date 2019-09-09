package de.imise.tool3lgm.graphtools.metamodel;

import java.util.MissingResourceException;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;

/**
 * @author AXS (02.09.2019)
 */
public interface MetaModelSpecific {

    /**
     * @return the metamodel context
     */
    //    public default MetaModelContext getMetaModelContext() {
    //        return Tool3lgmMetaModelContext.DUMMY_META_MODEL_CONTEXT;
    //    }
    public MetaModelContext getMetaModelContext();

    /**
     * Returns the metamodel. If the context has not initialzed the metamodel,
     * it will be initialzed.
     *
     * @return the metamodel
     */
    public default MetaModel getMetaModel() {
        MetaModelContext metaModelContext = getMetaModelContext();
        MetaModel metaModel = metaModelContext.getMetaModel();
        return metaModel;
    }

    /**
     * @return the elements name builder for this metamodel
     */
    public default ElementsNameBuilder getElementsNameBuilder() {
        MetaModelContext metaModelContext = getMetaModelContext();
        ElementsNameBuilder elementsNameBuilder = metaModelContext.getElementsNameBuilder();
        return elementsNameBuilder;
    }

    /**
     * @param resKey
     *            resource key
     * @return the string from the resources for the given resKey
     * @throws MissingResourceException
     */
    public default String getResString(final String resKey) {
        MetaModelContext metaModelContext = getMetaModelContext();
        String resString = metaModelContext.getResString(resKey);
        return resString;
    }

    /**
     * @param resKey
     *            resource key
     * @return the string from the resources for the given resKey or the key if
     *         there is no string in the resources for the given key
     */
    public default String getResStringWithoutError(final String resKey) {
        MetaModelContext metaModelContext = getMetaModelContext();
        String resString = metaModelContext.getResStringWithoutError(resKey);
        return resString;
    }

}
