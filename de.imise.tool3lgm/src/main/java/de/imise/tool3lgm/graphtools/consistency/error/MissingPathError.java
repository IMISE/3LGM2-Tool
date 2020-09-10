package de.imise.tool3lgm.graphtools.consistency.error;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.consistency.metapath.ConsistencyCheckSectionMetaPath;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
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
     * If the not the whole path should be created this element is
     * the real start element from where the needed path to remove
     * the error will be created.
     */
    private ModelElement missingPathStartElement;

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
     * @return the real start element where a path should be added
     *         to remove the error
     */
    public ModelElement getMissingPathStartElement() {
        if (missingPathStartElement == null) {
            ConsistencyCheckSectionMetaPath metaPath = getMetaPath();
            SimpleMetaPath subMetaPathToRealStartElement = metaPath.getSubMetaPathToRealStartElement();
            missingPathStartElement = getModelElement();
            if (subMetaPathToRealStartElement != null) {
                //this can only be one because the subMetaPathToRealStartElement is
                //only not null if the path is a single connection.
                Collection<ModelElement> realStartElement = subMetaPathToRealStartElement.getConnectedElements(missingPathStartElement);
                if (!realStartElement.isEmpty()) {
                    missingPathStartElement = realStartElement.iterator().next();
                }
            }
        }
        return missingPathStartElement;
    }

    /**
     * @return the errorCorrectingCreatableMetaPath that is the SimpleMetaPath equals
     *         to the secondSubMetaPathToConnectedElements or a subpath of this and
     *         is the metaPath that must be created to remove the error
     */
    public SimpleMetaPath getErrorCorrectingCreatableMetaPath() {
        ConsistencyCheckSectionMetaPath metaPath = getMetaPath();
        return metaPath.getErrorCorrectingCreatableMetaPath();
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