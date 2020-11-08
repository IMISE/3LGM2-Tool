package de.imise.tool3lgm.graphtools.consistency.error.type;

import java.util.Collection;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.consistency.error.condition.MissingPathErrorCheckCondition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.SectionMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * @author AXS (21.03.2020)
 */
public class MissingPathError extends AbstractPathError {

    /**
     * Elements which should be connected to remove this error
     */
    private final Collection<ModelElement> missingElements;

    /**
     *
     */
    private final MissingPathErrorCheckCondition missingPathErrorCheckCondition;

    /**
     * @param elementWithError
     * @param elementWithMissingPath
     * @param metaPath
     * @param missingElements
     * @param errorSolution
     */
    public MissingPathError(final ModelElement elementWithError, final MissingPathErrorCheckCondition missingPathErrorCheckCondition, final Collection<ModelElement> missingElements) {
        super(elementWithError, missingPathErrorCheckCondition.getToConnectableAndToConnectedSectionMetaPath(), missingPathErrorCheckCondition.errorSolution);
        this.missingPathErrorCheckCondition = missingPathErrorCheckCondition;
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
    public SectionMetaPath getMetaPath() {
        return (SectionMetaPath) metaPath;
    }

    /**
     * @return the errorCorrectingCreatableMetaPath that is the SimpleMetaPath
     *         equals to the secondSubMetaPathToConnectedElements or a subpath
     *         of this and is the metaPath that must be created to remove the
     *         error
     */
    public SimpleMetaPath getErrorFixingCreatableMetaPath() {
        return missingPathErrorCheckCondition.getToFixTheErrorMetaPath();
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
        String errorDescription = metaModel.getResString(errorDescriptionResourceKey);
        errorDescription = Tool3lgmConstants.getReplacedString(errorDescription, me);
        if (errorDescription == errorDescriptionResourceKey) {
            errorDescription = super.getMessage();
        }
        return errorDescription;
    }

}