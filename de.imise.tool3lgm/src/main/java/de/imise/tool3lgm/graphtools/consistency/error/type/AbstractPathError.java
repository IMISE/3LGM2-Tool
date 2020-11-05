package de.imise.tool3lgm.graphtools.consistency.error.type;

import de.imise.tool3lgm.graphtools.consistency.error.solution.ErrorSolution;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;

/**
 * @author AXS (Sa, 21.03.2020, 8:14' (Corona time...))
 */
public abstract class AbstractPathError extends AbstractConsistencyError {

    /**
     * If the metaPath was initialsed with a resorce key (constructor parameter baseResKeyOrName)
     * and in the resource is a key defined with this baseResKeyOrName and the "_error_descrip"
     * suffix, then the standard description of the error from the tool resources will be replaced
     * by the metamodel specific resocure value of thsi key.
     */
    public static final String PATH_ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX = "_error_descrip";

    /**
     * According to the {@link #PATH_ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX} this suffix will be used
     * to show a longer/better description of the error as tooltip or in a detals view.
     */
    public static final String PATH_ERROR_LONG_DESCRIPTION_RESOURCE_KEY_SUFFIX = "_error_descrip_long";

    /**
     *
     */
    protected final MetaPath metaPath;

    /**
     * @param me
     * @param metaPath
     * @param errorSolution
     */
    public AbstractPathError(final ModelElement me, final MetaPath metaPath, final ErrorSolution errorSolution) {
        super(me, errorSolution);
        this.metaPath = metaPath;
    }

    /**
     * @return
     */
    public MetaPath getMetaPath() {
        return metaPath;
    }

}