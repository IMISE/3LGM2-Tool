package de.imise.tool3lgm.graphtools.consistency.error;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.meta.ConsistencyCheckSectionMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SectionMetaPath;

/**
 * @author AXS (21.03.2020)
 */
public class MissingPathError extends AbstractPathError {

    /**
     * Elements which should be connected to remove this error
     */
    private final Collection<ModelElement> missingElements;

    /**
     * @param me
     * @param metaPath
     * @param gdcoll
     * @param missingElements
     */
    public MissingPathError(final ModelElement me, final ConsistencyCheckSectionMetaPath metaPath, final GDCollection gdcoll, final Collection<ModelElement> missingElements) {
        super(me, metaPath, gdcoll);
        this.missingElements = missingElements;
    }

    @Override
    public String getErrorFieldString() {
        SectionMetaPath metaPath = getMetaPath();
        return metaPath.getFullName();
    }

    @Override
    public String getMessage() {
        return getErrorResourceString(false);
    }

    @Override
    public ConsistencyCheckSectionMetaPath getMetaPath() {
        return (ConsistencyCheckSectionMetaPath) errorField;
    }

    /**
     * @return the missingElements
     */
    public Collection<ModelElement> getMissingElements() {
        return missingElements;
    }

    @Override
    public String getLongMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(getErrorResourceString(true));
        sb.append("</br>");
        sb.append("<ul>");
        for (ModelElement missingElement : missingElements) {
            sb.append("<li>");
            sb.append(missingElement);
            sb.append("</li>");
        }
        sb.append("</ul>");
        sb.append("</html>");
        return sb.toString();
    }

    /**
     * @param longDescription
     * @return
     */
    private String getErrorResourceString(final boolean longDescription) {
        SectionMetaPath metaPath = getMetaPath();
        String errorDescriptionResourceKey = metaPath.getBaseResKeyOrName();
        errorDescriptionResourceKey += longDescription ? PATH_ERROR_LONG_DESCRIPTION_RESOURCE_KEY_SUFFIX : PATH_ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX;
        MetaModel metaModel = metaPath.getMetaModel();
        String errorDescription = metaModel.getResStringWithoutError(errorDescriptionResourceKey);
        if (errorDescription == errorDescriptionResourceKey) {
            errorDescription = super.getMessage();
        }
        return errorDescription;
    }

}