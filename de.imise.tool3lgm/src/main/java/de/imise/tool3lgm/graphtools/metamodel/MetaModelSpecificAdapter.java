package de.imise.tool3lgm.graphtools.metamodel;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;

/**
 * Adapter for the interface {@link MetaModelSpecific}. Thsi Adapter makes all interface functions final.
 *
 * @author AXS (04.09.2019)
 */
public class MetaModelSpecificAdapter implements MetaModelSpecific {

    /**
     * Metamodel context with the metamodel of this instance
     */
    protected final MetaModelContext metaModelContext;

    /**
     * Metamodel context with the metamodel of this instance
     */
    protected final MetaModel metaModel;

    /**
     * @param metaModel
     *            the metamodel of this instance
     */
    public MetaModelSpecificAdapter(final MetaModel metaModel) {
        this.metaModel = metaModel;
        metaModelContext = metaModel.getMetaModelContext();
    }

    /**
     * @param metaModelContext
     *            the metamodel context with the metamodel of this instance
     */
    public MetaModelSpecificAdapter(final MetaModelContext metaModelContext) {
        this.metaModelContext = metaModelContext;
        metaModel = metaModelContext.getMetaModel();
    }

    /**
     * Wrapper constructor for elementes which are MetaModelSpecific by a parameter.
     *
     * @param metaModelSpecific
     *            a MetaModelSpecifict object that provides the metamodel context with the metamodel for this instance
     */
    public MetaModelSpecificAdapter(final MetaModelSpecific metaModelSpecific) {
        metaModelContext = metaModelSpecific.getMetaModelContext();
        metaModel = getMetaModel();
    }

    @Override
    public MetaModelContext getMetaModelContext() {
        return metaModelContext;
    }

    @Override
    public final MetaModel getMetaModel() {
        return metaModel;
    }

    @Override
    public final ElementsNameBuilder getElementsNameBuilder() {
        return MetaModelSpecific.super.getElementsNameBuilder();
    }

    @Override
    public final String getResString(final String resKey) {
        return MetaModelSpecific.super.getResString(resKey);
    }

    @Override
    public final String getResStringWithoutError(final String resKey) {
        return MetaModelSpecific.super.getResStringWithoutError(resKey);
    }

    @Override
    public final boolean hasMetaModelContext(final MetaModelContext metaModelContext) {
        return MetaModelSpecific.super.hasMetaModelContext(metaModelContext);
    }

}
