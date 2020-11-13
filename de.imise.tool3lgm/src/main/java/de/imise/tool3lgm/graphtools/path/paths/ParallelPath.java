package de.imise.tool3lgm.graphtools.path.paths;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.IMetaPath;

/**
 * @author AXS (10.03.2020)
 */
public class ParallelPath extends ListPath {

    /**
     * @param metaPath
     * @param startElement
     * @param endElement
     * @param paths
     */
    public ParallelPath(final IMetaPath metaPath, final ModelElement startElement, final ModelElement endElement, final AbstractPath... paths) {
        super(metaPath, startElement, endElement, paths);
    }

    /**
     * @param metaPath
     * @param startElement
     * @param endElement
     * @param paths
     */
    public ParallelPath(final IMetaPath metaPath, final ModelElement startElement, final ModelElement endElement, final List<AbstractPath> paths) {
        super(metaPath, startElement, endElement, paths);
    }

}
