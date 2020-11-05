package de.imise.tool3lgm.graphtools.consistency.error.solution;

import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * @author AXS (14.10.2020)
 */
public class MissingPathErrorSolution extends ErrorSolution {

    /**
     * @param targetClass
     * @param panelMetaPath
     */
    public MissingPathErrorSolution(final SimpleMetaPath panelMetaPath) {
        super(null, panelMetaPath);
    }

}
