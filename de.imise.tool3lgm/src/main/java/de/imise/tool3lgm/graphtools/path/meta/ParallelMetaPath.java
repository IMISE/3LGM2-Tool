package de.imise.tool3lgm.graphtools.path.meta;

import java.util.List;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Abstrakte Oberklasse für alle Pfade, die sich aus mind. 1 Pfad zusammen setzen und bei
 * ihrer Anwendung eine der üblichen Mengenoperationen auf die Ergebniselemente anwenden.
 *
 * @author AXS
 * @create 12.10.2010
 */
public abstract class ParallelMetaPath extends ListMetaPath {

    /**
     * @param metaPaths
     */
    public ParallelMetaPath(final AbstractMetaPath... metaPaths) {
        this(null, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public ParallelMetaPath(final String baseResKeyOrName, final AbstractMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
    }

    @Override
    protected void initStartEndClasses() {
        ImmutableSet.Builder<Class<? extends ModelElement>> startElementClassesBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<Class<? extends ModelElement>> endElementClassesBuilder = ImmutableSet.builder();
        for (AbstractMetaPath metaPath : metaPaths) {
            startElementClassesBuilder.addAll(metaPath.startElementClasses);
            endElementClassesBuilder.addAll(metaPath.endElementClasses);
        }
        startElementClasses = startElementClassesBuilder.build();
        endElementClasses = endElementClassesBuilder.build();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        //Klassengleichheit wird in super.equals() schon getestet
        ParallelMetaPath other = (ParallelMetaPath) obj;
        return other.metaPaths.equals(metaPaths);
    }

    @Override
    public boolean isCreateable() {
        return false;
    }

    @Override
    public List<ElementaryMetaPath> getElementaryMetaPaths() {
        return null;
    }

    @Override
    public boolean isDirected() {
        for (AbstractMetaPath metaPath : metaPaths) {
            if (!metaPath.isDirected()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsHasPartEdge() {
        for (AbstractMetaPath metaPath : metaPaths) {
            if (metaPath.containsHasPartEdge()) {
                return true;
            }
        }
        return false;
    }

}
