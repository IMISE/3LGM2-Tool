package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;

/**
 * @author AXS (01.09.2019)
 */
public class PathTreeDefinition extends MetaModelSpecificAdapter implements Iterable<PathTreeBranchDefinition> {

    /**
     * Definition of the visible branches in the tree
     */
    private final List<PathTreeBranchDefinition> branches = new ArrayList<>();

    /**
     * @param metaModelContext
     */
    public PathTreeDefinition(final MetaModelContext metaModelContext) {
        super(metaModelContext);
    }

    /**
     * @param branch
     */
    public void addBranch(final PathTreeBranchDefinition branch) {
        if (!branches.contains(branch)) { //falls man durch die Defintion die Reihenfolge der Braches beeinflussen können will, muss es eine Liste sein -> contais(..) abfragen
            branches.add(branch);
        }
    }

    /**
     * @param branches
     */
    public void addBranches(final Iterable<PathTreeBranchDefinition> branches) {
        for (PathTreeBranchDefinition branch : branches) {
            addBranch(branch);
        }
    }

    @Override
    public Iterator<PathTreeBranchDefinition> iterator() {
        return branches.iterator();
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return branches.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (branches == null ? 0 : branches.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PathTreeDefinition other = (PathTreeDefinition) obj;
        if (branches == null) {
            if (other.branches != null) {
                return false;
            }
        } else if (!branches.equals(other.branches)) {
            return false;
        }
        return true;
    }

}
