package de.imise.tool3lgm.graphtools.path.meta;

import java.util.Arrays;
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
public abstract class ParallelMetaPath extends AbstractMetaPath {

    /**
     * Liste der Pfade, die dieser Metapfad parallel enthält.
     */
    protected List<AbstractMetaPath> metaPaths;

    /**
     * @param metaPaths
     */
    public ParallelMetaPath(final AbstractMetaPath... metaPaths) {
        this(null, metaPaths);
    }

    /**
     * @param name
     * @param metaPaths
     */
    public ParallelMetaPath(final String name, final AbstractMetaPath... metaPaths) {
        super(name);
        if (metaPaths != null && metaPaths.length > 0) {
            this.metaPaths = Arrays.asList(metaPaths);
            initStartEndClasses();
        }
    }

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

    /**
     * @return the metaPaths
     */
    public final List<AbstractMetaPath> getMetaPaths() {
        return metaPaths;
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
    public boolean isValid() {
        for (AbstractMetaPath metaPath : metaPaths) {
            if (!metaPath.isValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isCreateable() {
        return false;
    }

    @Override
    public List<ElementaryMetaPath> getSimpleMetaPath() {
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

}
