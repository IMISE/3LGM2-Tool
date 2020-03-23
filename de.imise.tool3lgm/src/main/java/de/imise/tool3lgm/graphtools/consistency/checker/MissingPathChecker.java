package de.imise.tool3lgm.graphtools.consistency.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.imise.tool3lgm.graphtools.consistency.error.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.MissingPathError;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.SectionMetaPath;

/**
 * @author AXS (22.03.2020)
 */
public class MissingPathChecker implements ConsistencyErrorChecker {

    @Override
    public Collection<AbstractConsistencyError> getErrors(final GDCollection gdcoll) {
        ArrayList<AbstractConsistencyError> errors = new ArrayList<>();
        MetaModel metaModel = gdcoll.getMetaModel();
        Collection<SectionMetaPath> consistencyConditionSameElementsConnectedMetaPaths = metaModel.getConsistencyConditionSameElementsConnectedMetaPaths();
        LGMGraphDocument mainDoc = gdcoll.getMainGraphDocument();
        for (SectionMetaPath consistencyConditionSectionMetaPath : consistencyConditionSameElementsConnectedMetaPaths) {
            Class<? extends ModelElement> startClass = consistencyConditionSectionMetaPath.getStartClass();
            List<ModelElement> possibleInconsistentElements = mainDoc.getModelItems(startClass, true);
            for (ModelElement me : possibleInconsistentElements) {
                Collection<ModelElement> connectedElements = MetaPathFunctions.getConnectedElements(me, consistencyConditionSectionMetaPath);
                if (connectedElements.isEmpty()) {
                    MissingPathError error = new MissingPathError(me, consistencyConditionSectionMetaPath, gdcoll);
                    errors.add(error);
                }
            }
        }
        return errors;
    }

}
