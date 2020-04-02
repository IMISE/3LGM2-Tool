package de.imise.tool3lgm.graphtools.path.paths;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * Ein SequencePath ist ein Pfad der selbst wieder Pfade enthält
 *
 * @author AXS
 * @create 08.02.2011
 */
public class SequencePath extends ListPath {

    /**
     * @param metaPath
     * @param paths
     */
    public SequencePath(final AbstractMetaPath metaPath, final List<AbstractPath> paths) {
        super(metaPath, paths != null && paths.size() > 0 ? paths.get(0).getStartElement() : null, paths != null && paths.size() > 0 ? paths.get(paths.size() - 1).getEndElement() : null, paths);
    }

    /**
     * @param metaPath
     * @param paths
     */
    public SequencePath(final AbstractMetaPath metaPath, final AbstractPath... paths) {
        super(metaPath, paths.length > 0 ? paths[0].getStartElement() : null, paths.length > 0 ? paths[paths.length - 1].getEndElement() : null, paths);
    }

    /**
     * @param metaPath
     * @param paths
     */
    public SequencePath(final SimpleMetaPath metaPath, final List<ElementaryPath> paths) {
        super(metaPath, paths != null && paths.size() > 0 ? paths.get(0).getStartElement() : null, paths != null && paths.size() > 0 ? paths.get(paths.size() - 1).getEndElement() : null, createPathsList(paths));
    }

    @Override
    public final boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        //startElement fits first path startElement?
        AbstractPath firstPath = get(0);
        if (firstPath == null) {
            return false;
        }
        if (!firstPath.isValid()) {
            return false;
        }
        //it's not necessary to check the startElement and endElement of this is null
        ModelElement firstPathStartElement = firstPath.getStartElement();
        if (startElement != firstPathStartElement) {
            return false;
        }
        int pathCount = size();
        //endElement fits last path endElement?
        AbstractPath lastPath = get(pathCount - 1);
        if (lastPath == null) {
            return false;
        }
        if (!lastPath.isValid()) {
            return false;
        }
        ModelElement lastPathEndElement = lastPath.getEndElement();
        if (endElement != lastPathEndElement) {
            return false;
        }
        //
        for (int i = 1; i < pathCount; i++) {
            AbstractPath innerPath = get(i);
            if (innerPath == null) {
                return false;
            }
            //the first and last paths validity is already checked above
            if (i < pathCount - 1 && !innerPath.isValid()) {
                return false;
            }
            AbstractPath previousInnerPath = get(i - 1);
            ModelElement innerPathStartElement = innerPath.getStartElement();
            ModelElement previousPathEndElement = previousInnerPath.endElement;
            if (innerPathStartElement != previousPathEndElement) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param paths
     * @return
     */
    private static final List<AbstractPath> createPathsList(final List<ElementaryPath> paths) {
        ImmutableList.Builder<AbstractPath> pathsBuilder = ImmutableList.builder();
        for (ElementaryPath elementaryMetaPath : paths) {
            pathsBuilder.add(elementaryMetaPath);
        }
        return pathsBuilder.build();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int pathCount = size();
        for (int i = 0; i < pathCount; i++) {
            AbstractPath path = get(i);
            sb.append(path);
            if (i < pathCount - 1) {
                sb.append(" <-> ");
            }
        }
        return sb.toString();
    }

}
