package de.imise.tool3lgm.graphtools.path.pathmodel;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.util.collections.CollectionUtils;

/**
 * Ein SequencePath ist ein Pfad der selbst wieder Pfade enthält
 *
 * @author AXS
 * @create 08.02.2011
 */
public class SequencePath extends AbstractPath {

    /** Ein SequencePath enthält eine Liste anderer Pfade */
    private final List<AbstractPath> paths;

    /**
     * @param metaPath
     * @param paths
     */
    public SequencePath(final AbstractMetaPath metaPath, final List<AbstractPath> paths) {
        super(metaPath, paths != null && paths.size() > 0 ? paths.get(0).getStartElement() : null, paths != null && paths.size() > 0 ? paths.get(paths.size() - 1).getEndElement() : null);
        this.paths = CollectionUtils.ensureImmutable(paths);
    }

    /**
     * @param metaPath
     * @param paths
     */
    public SequencePath(final AbstractMetaPath metaPath, final AbstractPath... paths) {
        super(metaPath, paths.length > 0 ? paths[0].getStartElement() : null, paths.length > 0 ? paths[paths.length - 1].getEndElement() : null);
        this.paths = CollectionUtils.ensureImmutable(Arrays.asList(paths));
    }

    /**
     * @param metaPath
     * @param paths
     */
    public SequencePath(final SimpleMetaPath metaPath, final List<ElementaryPath> paths) {
        super(metaPath, paths != null && paths.size() > 0 ? paths.get(0).getStartElement() : null, paths != null && paths.size() > 0 ? paths.get(paths.size() - 1).getEndElement() : null);
        this.paths = createPathsList(paths);
    }

    private static final List<AbstractPath> createPathsList(final List<ElementaryPath> paths) {
        ImmutableList.Builder<AbstractPath> pathsBuilder = ImmutableList.builder();
        for (ElementaryPath elementaryMetaPath : paths) {
            pathsBuilder.add(elementaryMetaPath);
        }
        return pathsBuilder.build();
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @return the paths
     */
    public List<AbstractPath> getPaths() {
        return paths;
    }

    @Override
    protected void replace(final ModelElement original, final ModelElement replacement) {
        for (AbstractPath path : paths) {
            path.replace(original, replacement);
        }
    }

    /**
     * @return number of paths in the path list
     */
    public final int length() {
        return paths.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            sb.append(paths.get(i));
            if (i < paths.size() - 1) {
                sb.append(" <-> ");
            }
        }
        return sb.toString();
    }

}
