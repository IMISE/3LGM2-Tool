package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.SectionMetaPath;

/**
 * @author AXS (21.03.2020)
 */
public class MissingPathError extends AbstractPathError {

    /**
     * @param me
     * @param metaPath
     * @param gdcoll
     */
    public MissingPathError(final ModelElement me, final SectionMetaPath metaPath, final GDCollection gdcoll) {
        super(me, metaPath, gdcoll);
    }

    @Override
    public String getErrorFieldString() {
        SectionMetaPath metaPath = (SectionMetaPath) getMetaPath();
        return metaPath.getFullName();
    }

    @Override
    public String getMessage() {
        SectionMetaPath metaPath = (SectionMetaPath) getMetaPath();
        String errorDescriptionResourceKey = metaPath.getBaseResKeyOrName();
        errorDescriptionResourceKey += PATH_ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX;
        MetaModel metaModel = metaPath.getMetaModel();
        String errorDescription = metaModel.getResStringWithoutError(errorDescriptionResourceKey);
        if (errorDescription == errorDescriptionResourceKey) {
            errorDescription = super.getMessage();
        }
        return errorDescription;
    }

}