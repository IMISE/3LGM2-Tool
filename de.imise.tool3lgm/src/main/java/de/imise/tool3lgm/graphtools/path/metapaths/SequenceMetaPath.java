package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.graphtools.metamodel.CoreMetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * This interface is only a marker for MetaPaths, which can always provide a
 * non-empty a simple sequence of elementary metapaths.
 *
 * @author Ich (12.11.2020)
 */
public interface SequenceMetaPath extends MetaPath {

    /**
     * @return All start and end classes of all contained elementary metapaths
     *         with their subclasses.
     */
    public default Set<Class<? extends ModelElement>> getAllElementaryPathsStartAndEndClasses() {
        return getAllMetaPathsStartAndEndClasses(this, true);
    }

    /**
     * @return the connection classes of the outer contained SequenceMetaPaths.
     *         If the path consists only of SequenceMetaPaths of length 1 (i.e.
     *         only one elementary metaPath at a time), then the same returns as
     *         with {@link #getAllElementaryPathsStartAndEndClasses()}
     */
    public default Set<Class<? extends ModelElement>> getAllFirstLevelSubMetaPathsStartAndEndClasses() {
        return getAllMetaPathsStartAndEndClasses(this, false);
    }

    /**
     * @param sequenceMetaPath
     * @param checkElementaryMetaPaths
     * @return
     */
    static Set<Class<? extends ModelElement>> getAllMetaPathsStartAndEndClasses(final SequenceMetaPath sequenceMetaPath, final boolean checkElementaryMetaPaths) {
        Set<Class<? extends ModelElement>> returnSet = new HashSet<>();
        Class<? extends ModelElement> startClass = sequenceMetaPath.getStartClass();
        returnSet.add(startClass);
        int metaPathLength = checkElementaryMetaPaths ? sequenceMetaPath.getElementaryMetaPathCount() : sequenceMetaPath.getSubMetaPathCount();
        for (int i = 0; i < metaPathLength; i++) {
            Class<? extends ModelElement> pathStepElementClass = checkElementaryMetaPaths ? MetaPathFunctions.getElementaryMetaPathsConnectingClass(sequenceMetaPath, i) : MetaPathFunctions.getSubMetaPathsConnectingClass(sequenceMetaPath, i);
            if (CoreMetaModel.isAbstract(pathStepElementClass)) {
                MetaModel metaModel = sequenceMetaPath.getMetaModel();
                Collection<Class<? extends ModelElement>> classAndSubClasses = metaModel.getClassAndSubClasses(pathStepElementClass);
                returnSet.addAll(classAndSubClasses);
            } else {
                returnSet.add(pathStepElementClass);
            }
        }
        return returnSet;
    }

}
