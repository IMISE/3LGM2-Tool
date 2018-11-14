package de.imise.tool3lgm.graphtools.path.pathmodel;

import java.util.List;

import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;

/**
 * Ein SequencePath ist ein Pfad der selbst wieder Pfade enthält
 * 
 * @author AXS
 * @create 08.02.2011
 */
public final class SequencePath extends AbstractPath {

    /** Ein SequencePath enthält eine Liste anderer Pfade */
    private final List<AbstractPath> paths;

    /**
     * @param paths
     * @param metaPath
     */
    public SequencePath(final List<AbstractPath> paths, final AbstractMetaPath metaPath) {
        super(paths != null && paths.size() > 0 ? paths.get(0).getStartElement() : null, paths != null && paths.size() > 0 ? paths.get(paths.size() - 1).getEndElement() : null, metaPath);
        this.paths = paths;
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @return the paths
     */
    public final List<AbstractPath> getPaths() {
        return paths;
    }

}
