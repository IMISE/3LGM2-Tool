package de.imise.tool3lgm.graphtools.consistency.error.type;

import de.imise.tool3lgm.graphtools.consistency.error.solution.ErrorSolution;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.IMetaPath;

/**
 * @author AXS (Sa, 21.03.2020, 8:14' (Corona time...))
 */
public abstract class AbstractPathError extends AbstractConsistencyError {

    /**
     * If the metaPath was initialsed with a resorce key (constructor parameter
     * baseResKeyOrName) and in the resource is a key defined with this
     * baseResKeyOrName and the "_error_descrip" suffix, then the standard
     * description of the error from the tool resources will be replaced by the
     * metamodel specific resocure value of thsi key.
     */
    public static final String PATH_ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX = "_error_descrip";

    /**
     * According to the {@link #PATH_ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX} this
     * suffix will be used to show a longer/better description of the error as
     * tooltip or in a detals view.
     */
    public static final String PATH_ERROR_LONG_DESCRIPTION_RESOURCE_KEY_SUFFIX = "_error_descrip_long";

    /**
     *
     */
    protected final IMetaPath metaPath;

    /**
     * @param me
     * @param metaPath
     * @param errorSolution
     */
    public AbstractPathError(final ModelElement me, final IMetaPath metaPath, final ErrorSolution errorSolution) {
        super(me, errorSolution);
        this.metaPath = metaPath;
    }

    /**
     * @return
     */
    public IMetaPath getMetaPath() {
        return metaPath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (metaPath == null ? 0 : metaPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractPathError other = (AbstractPathError) obj;
        if (metaPath == null) {
            if (other.metaPath != null) {
                return false;
            }
        } else if (!metaPath.equals(other.metaPath)) {
            return false;
        }
        return true;
    }

}