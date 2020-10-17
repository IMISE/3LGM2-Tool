package de.imise.tool3lgm.graphtools.consistency.error.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.consistency.error.condition.MissingPathErrorCheckCondition;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MissingPathError;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;

/**
 * @author AXS (22.03.2020)
 */
public class MissingPathChecker implements ConsistencyErrorChecker {

    @Override
    public Class<? extends AbstractConsistencyError> getErrorType() {
        return MissingPathError.class;
    }

    @Override
    public Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll, final boolean checkOnly) {
        ArrayList<AbstractConsistencyError> errors = new ArrayList<>();
        MetaModel metaModel = gdcoll.getMetaModel();
        Collection<MissingPathErrorCheckCondition> missingPathErrorCheckConditions = metaModel.getMissingPathErrorCheckConditions();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        //for every consistency condition
        for (MissingPathErrorCheckCondition condition : missingPathErrorCheckConditions) {
            MetaPath toRealStartElementsMetaPath = condition.getMetaPathToRealStartElements();
            MetaPath toConnectableAndToConnectedSectionMetaPath = condition.getToConnectableAndToConnectedSectionMetaPath();
            MetaPath metaPathFromStartElements = toRealStartElementsMetaPath != null ? toRealStartElementsMetaPath : toConnectableAndToConnectedSectionMetaPath;
            Class<? extends ModelElement> elementClassToCheck = metaPathFromStartElements.getStartClass();
            List<ModelElement> possibleInconsistentElements = mainDoc.getModelItems(elementClassToCheck, true);
            for (ModelElement elementToCheck : possibleInconsistentElements) {
                Collection<ModelElement> realStartElements;
                if (toRealStartElementsMetaPath != null) {
                    realStartElements = toRealStartElementsMetaPath.getConnectedElements(elementToCheck);
                } else {
                    realStartElements = ImmutableList.of(elementToCheck);
                }
                //for every of these elements
                for (ModelElement realStartElement : realStartElements) {
                    //the first sub metapath of the SectionMetaPath describes the connection to the needed elements -> get the first
                    MetaPath metaPathToNeededElements = condition.getToConnectableMetaPath();
                    //get the elements which should be connected over the other subpaths too
                    Collection<ModelElement> neededElements = metaPathToNeededElements.getConnectedElements(realStartElement);
                    //if there are needed elements
                    if (!neededElements.isEmpty()) {
                        //get the result of the whole SectionMetaPath = the section of the set of needed elements and the really connected elements
                        Collection<ModelElement> connectedElements = toConnectableAndToConnectedSectionMetaPath.getConnectedElements(realStartElement);
                        //if there is not at least on of the needed connected to the current path start element -> error
                        if (connectedElements.isEmpty()) {
                            MissingPathError error = new MissingPathError(elementToCheck, condition, neededElements);
                            errors.add(error);
                            if (checkOnly) {
                                return errors;
                            }
                        }
                    }
                }
            }
        }
        return errors;
    }

}
