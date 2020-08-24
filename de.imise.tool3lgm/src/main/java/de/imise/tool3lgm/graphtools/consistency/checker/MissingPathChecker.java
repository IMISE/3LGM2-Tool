package de.imise.tool3lgm.graphtools.consistency.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.MissingPathError;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ConsistencyCheckSectionMetaPath;

/**
 * @author AXS (22.03.2020)
 */
public class MissingPathChecker implements ConsistencyErrorChecker {

    @Override
    public Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll, final boolean checkOnly) {
        ArrayList<AbstractConsistencyError> errors = new ArrayList<>();
        MetaModel metaModel = gdcoll.getMetaModel();
        Map<ConsistencyCheckSectionMetaPath, Class<? extends Edge>> consistencyConditionMissingConnectedElementsMetaPathsMap = metaModel.getConsistencyConditionMissingConnectedElementsMetaPaths();
        Collection<ConsistencyCheckSectionMetaPath> consistencyConditionMissingConnectedElementsMetaPaths = consistencyConditionMissingConnectedElementsMetaPathsMap.keySet();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        //for every of the SectionMetaPaths with a consistency condition
        for (ConsistencyCheckSectionMetaPath consistencyConditionSectionMetaPath : consistencyConditionMissingConnectedElementsMetaPaths) {
            //get start element class of thsi metapath = element class which must have these connections to be valid
            Class<? extends ModelElement> startClass = consistencyConditionSectionMetaPath.getStartClass();
            //get all of the elements of the path start class
            List<ModelElement> possibleInconsistentElements = mainDoc.getModelItems(startClass, true);
            //for every of these elements
            for (ModelElement me : possibleInconsistentElements) {
                //the first sub metapath of the SectionMetaPath describes the connection to the needed elements -> get the first
                AbstractMetaPath metaPathToNeededElements = consistencyConditionSectionMetaPath.getFirstSubMetaPathToConnectableElements();
                //get the elements which should be connected over the other subpaths too
                Collection<ModelElement> neededElements = PathFunctions.getConnectedElements(me, metaPathToNeededElements);
                //if there are needed elements
                if (!neededElements.isEmpty()) {
                    //get the result of the whole SectionMetaPath = the section of the set of needed elements and the really connected elements
                    Collection<ModelElement> connectedElements = PathFunctions.getConnectedElements(me, consistencyConditionSectionMetaPath);
                    //if there is not at least on of the needed connected to the current path start element -> error
                    if (connectedElements.isEmpty()) {
                        MissingPathError error = new MissingPathError(me, consistencyConditionSectionMetaPath, gdcoll, neededElements);
                        errors.add(error);
                        if (checkOnly) {
                            return errors;
                        }
                    }
                }
            }
        }
        return errors;
    }

}
