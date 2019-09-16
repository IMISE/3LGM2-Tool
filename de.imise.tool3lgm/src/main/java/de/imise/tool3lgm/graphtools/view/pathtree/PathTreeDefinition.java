package de.imise.tool3lgm.graphtools.view.pathtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelSpecificAdapter;

/**
 * @author AXS (01.09.2019)
 */
public class PathTreeDefinition extends MetaModelSpecificAdapter implements Iterable<PathTreeBranchDefinition> {

    //später, falls gebraucht
    //    public final PropertyChangeHandler propertyChangeHandler;

    private final List<PathTreeBranchDefinition> branches = new ArrayList<>();

    /**
     * @param metaModelContext
     */
    public PathTreeDefinition(final MetaModelContext metaModelContext, final int i) {
        super(metaModelContext);
        //        propertyChangeHandler = new PropertyChangeHandler(this);
    }

    public void addBranch(final PathTreeBranchDefinition branch) {
        if (!branches.contains(branch)) { //falls man durch die Defintion die Reihenfolge der Braches beeinflussen können will, muss es eine Liste sein -> contais(..) abfragen
            branches.add(branch);
        }
    }

    public void addBranches(final Collection<PathTreeBranchDefinition> branches) {
        for (PathTreeBranchDefinition branch : branches) {
            addBranch(branch);
        }
    }

    @Override
    public Iterator<PathTreeBranchDefinition> iterator() {
        return branches.iterator();
    }

}
