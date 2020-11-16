package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

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

    /**
     * @param showAllElementaryMetaPaths If <code>true</code> then the whole
     *            connection classes of the elementary path steps are searched
     *            out. If <code>false</code> then the connection classes of the
     *            outer contained SequenceMetaPaths are returned. If the
     *            metapath consists only of SequenceMetaPaths of length 1 (i.e.
     *            only one elementary metaPath at a time), then this parameter
     *            is irrelevant.
     * @return a set of all classes which are defined as vissible through this
     *         tree branch
     */
    public final Set<Class<? extends ModelElement>> getVisibleElementTypes(final boolean showAllElementaryMetaPaths) {
        Set<Class<? extends ModelElement>> visibleElementTypes = new HashSet<>();
        for (PathTreeBranchDefinition branch : branches) {
            Set<Class<? extends ModelElement>> branchVisibleElementTypes = branch.getVisibleElementTypes(showAllElementaryMetaPaths);
            visibleElementTypes.addAll(branchVisibleElementTypes);
        }
        return visibleElementTypes;
    }

}
