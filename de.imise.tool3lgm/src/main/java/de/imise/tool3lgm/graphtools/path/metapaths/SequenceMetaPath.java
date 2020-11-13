package de.imise.tool3lgm.graphtools.path.metapaths;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * This interface is only a marker for MetaPaths, which can always provide a
 * non-empty a simple sequence of elementary metapaths.
 *
 * @author Ich (12.11.2020)
 */
public interface SequenceMetaPath extends MetaPath {

    /**
     * Returns the connection class of the path step with the passed index in
     * the element path list of this path. With index 0, this is the more
     * special of the end class of the first elementary path and the start class
     * of the next elementary path. The path step with the index of path length
     * -1 is the end class of the last elementary path = end class of the whole
     * elementary path list. The start class of the complete path is not
     * accessible through this function.
     *
     * @param pathStepIndex
     * @return
     */
    public Class<? extends ModelElement> getPathStepElementClass(final int pathStepIndex);

    /**
     * @return the number of elementary metapaths in the complete path
     */
    public int length();
}
