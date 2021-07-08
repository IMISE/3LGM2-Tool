package de.imise.tool3lgm.graphtools.metamodel;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.util.Sys;

/**
 * Adapter for the interface {@link MetaModelSpecific}. Thsi Adapter makes all
 * interface functions final.
 *
 * @author AXS (04.09.2019)
 */
public class MetaModelSpecificAdapter implements MetaModelSpecific {

    private final Class<? extends MetaModelDefinition> metaModelDefintionClass;

    /**
     * Stores the Metamodel if a valid was given in
     */
    protected MetaModel metaModel;

    /**
     * @param metaModelDefintionClass
     */
    public MetaModelSpecificAdapter(final Class<? extends MetaModelDefinition> metaModelDefintionClass) {
        this.metaModelDefintionClass = metaModelDefintionClass;
        if (metaModelDefintionClass == null) {
            Sys.err(getClass());
        }
        metaModel = null;
    }

    /**
     * @param metaModel the metamodel of this instance
     */
    public MetaModelSpecificAdapter(final MetaModel metaModel) {
        metaModelDefintionClass = metaModel.getMetaModelDefinitionClass();
        if (metaModelDefintionClass == null) {
            Sys.err(getClass());
        }
        this.metaModel = metaModel;
    }

    /**
     * @param metaModelContext the metamodel context with the metamodel of this
     *            instance
     */
    public MetaModelSpecificAdapter(final MetaModelContext metaModelContext) {
        metaModelDefintionClass = metaModelContext.getMetaModelDefinitionClass();
        if (metaModelDefintionClass == null) {
            Sys.err(getClass());
        }
        metaModel = metaModelContext.isMetaModelInitialized() ? metaModelContext.getMetaModel() : null;
    }

    /**
     * Wrapper constructor for elementes which are MetaModelSpecific by a
     * parameter.
     *
     * @param metaModelSpecific a MetaModelSpecifict object that provides the
     *            metamodel context with the metamodel for this instance
     */
    public MetaModelSpecificAdapter(final MetaModelSpecific metaModelSpecific) {
        metaModelDefintionClass = metaModelSpecific.getMetaModelDefinitionClass();
        if (metaModelDefintionClass == null) {
            Sys.err(getClass());
        }
        metaModel = metaModelSpecific instanceof MetaModel ? (MetaModel) metaModelSpecific : null;
    }

    /**
     * Wrapper constructor for elementes which are MetaModelSpecific by a
     * parameter.
     *
     * @param metaModelSpecificAdapter a MetaModelSpecifictAdapter object that
     *            provides the metamodel context with the metamodel for this
     *            instance
     */
    public MetaModelSpecificAdapter(final MetaModelSpecificAdapter metaModelSpecificAdapter) {
        metaModelDefintionClass = metaModelSpecificAdapter.metaModelDefintionClass;
        metaModel = metaModelSpecificAdapter.metaModel != null ? metaModelSpecificAdapter.metaModel : null;
    }

    @Override
    public Class<? extends MetaModelDefinition> getMetaModelDefinitionClass() {
        return metaModelDefintionClass;
    }

    @Override
    public final MetaModelContext getMetaModelContext() {
        return MetaModelSpecific.super.getMetaModelContext();
    }

    @Override
    public final MetaModel getMetaModel() {
        if (metaModel == null) {
            metaModel = MetaModelSpecific.super.getMetaModel();
        }
        return metaModel;
    }

    @Override
    public final String getMetaModelID() {
        return MetaModelSpecific.super.getMetaModelID();
    }

    @Override
    public final ElementsNameBuilder getElementsNameBuilder() {
        return MetaModelSpecific.super.getElementsNameBuilder();
    }

    @Override
    public String getResString(final String resKey) { //nicht final, weil Unterklassen ihre String von sonstwo laden können
        return MetaModelSpecific.super.getResString(resKey);
    }

    @Override
    public final String getResStringWithoutError(final Object o) {
        return MetaModelSpecific.super.getResStringWithoutError(o);
    }

    @Override
    public final String getResStringWithoutError(final String prefix, final Object o) {
        return MetaModelSpecific.super.getResStringWithoutError(prefix, o);
    }

    @Override
    public String getResStringWithoutError(final String resKey) { //nicht final, weil Unterklassen ihre String von sonstwo laden können
        return MetaModelSpecific.super.getResStringWithoutError(resKey);
    }

    @Override
    public final boolean hasMetaModel(final MetaModel metaModel) {
        return MetaModelSpecific.super.hasMetaModel(metaModel);
    }

    @Override
    public final boolean hasMetaModelContext(final MetaModelContext metaModelContext) {
        return MetaModelSpecific.super.hasMetaModelContext(metaModelContext);
    }

    @Override
    public boolean hasMetaModelDefinitionClass(final Class<? extends MetaModelDefinition> metaModelDefinitionClass) {
        return MetaModelSpecific.super.hasMetaModelDefinitionClass(metaModelDefinitionClass);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (metaModelDefintionClass == null ? 0 : metaModelDefintionClass.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MetaModelSpecificAdapter other = (MetaModelSpecificAdapter) obj;
        if (metaModelDefintionClass == null) {
            if (other.metaModelDefintionClass != null) {
                return false;
            }
        } else if (!metaModelDefintionClass.equals(other.metaModelDefintionClass)) {
            return false;
        }
        return true;
    }

}
