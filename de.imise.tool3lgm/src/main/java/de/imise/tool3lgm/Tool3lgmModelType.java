package de.imise.tool3lgm;

import static de.imise.tool3lgm.Tool3lgmModelType.ModelCategory.REGULAR;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;

/**
 * Encapsulates all informations which states the absulute type of a model. This contains a {@link MetaModelContext} and a
 * {@link ModelCategory}.
 *
 * @author AXS (26.08.2019)
 */
public class Tool3lgmModelType {

    /**
     * Typen von Modellen unabhängig vom konkreten Metamodell.
     *
     * @author AXS (26.08.2019)
     */
    public static enum ModelCategory {
        /**
         * Type of 'ordinary' model where all elements are unique which have no graphical representation. These elements are automatically visible in
         * every submodel (in the model explorer)
         */
        REGULAR,
        /**
         * Type of models where all elements of a submodel must be explicitly taken over to a submodel and only these elements are visible in the
         * model explorer.
         */
        TEMPLATE,
    }

    /** Metamodell dieses Modell-Typs */
    private final MetaModelContext metaModelContext;

    /** Modell-Kategorie dieses Typs */
    private ModelCategory modelCategory;

    /**
     * @param metaModelContext
     * @param modelCategory
     */
    public Tool3lgmModelType(final MetaModelContext metaModelContext, final Tool3lgmModelType.ModelCategory modelCategory) {
        this.metaModelContext = metaModelContext;
        this.modelCategory = modelCategory != null ? modelCategory : REGULAR;
    }

    /**
     * @return the metaModelContext
     */
    public MetaModelContext getMetaModelContext() {
        return metaModelContext;
    }

    /**
     * @return the metaModel of the metaModelContext
     */
    public MetaModel getMetaModel() {
        return metaModelContext.getMetaModel();
    }

    /**
     * @return the modelCategory
     */
    public ModelCategory getModelCategory() {
        return modelCategory;
    }

    /**
     * @param modelCategory
     */
    public void setModelCategory(final Tool3lgmModelType.ModelCategory modelCategory) {
        this.modelCategory = modelCategory;
    }

}
