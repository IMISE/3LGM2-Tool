package de.imise.tool3lgm.graphtools.path.metapaths;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Abstrakte Oberklasse für alle Pfade, die sich aus mind. 1 Pfad zusammen
 * setzen und bei ihrer Anwendung eine der üblichen Mengenoperationen auf die
 * Ergebniselemente anwenden.
 *
 * @author AXS
 * @create 12.10.2010
 */
public abstract class ParallelMetaPath extends ListMetaPath {

    /**
     * Wahr, wenn sobald einmal verscht wurde, die Gegenrichtung dieses Pfades
     * anzulegen
     */
    protected boolean otherDirectionInitilized = false;

    /**
     * @param subMetaPaths
     */
    public ParallelMetaPath(final ParallelMetaPath other) {
        super(other);
    }

    /**
     * @param metaPaths
     */
    public ParallelMetaPath(final Collection<SimpleMetaPath> metaPaths) {
        this(toArray(metaPaths));
    }

    /**
     * @param metaPaths
     */
    public ParallelMetaPath(final IMetaPath... metaPaths) {
        this(null, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param metaPaths
     */
    public ParallelMetaPath(final String baseResKeyOrName, final IMetaPath... metaPaths) {
        super(baseResKeyOrName, metaPaths);
    }

    /**
     * @param baseResKeyOrName
     * @param other
     */
    public ParallelMetaPath(final String baseResKeyOrName, final ParallelMetaPath other) {
        super(baseResKeyOrName, other);
    }

    @Override
    protected void initStartEndClasses() {
        ImmutableSet.Builder<Class<? extends ModelElement>> startElementClassesBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<Class<? extends ModelElement>> endElementClassesBuilder = ImmutableSet.builder();
        for (IMetaPath metaPath : subMetaPaths) {
            startElementClassesBuilder.addAll(metaPath.getStartClasses());
            endElementClassesBuilder.addAll(metaPath.getEndClasses());
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
        return other.subMetaPaths.equals(subMetaPaths);
    }

    @Override
    public IMetaPath getOtherDirection() {
        // wenn noch nicht bereits einmal versucht wurde den Gegenrichtungspfad zusammenzubauen
        if (!otherDirectionInitilized) {
            otherDirectionInitilized = true;
            // versuchen, die Gegenrichtung zusammen zu bauen
            IMetaPath[] otherDirectionSubMetaPaths = getOtherDirectionMetaPaths();
            // Gegenrichtung für diesen und den Gegenrichtungspfad setzen, wenn es die Gegenrichtung gibt
            if (otherDirectionSubMetaPaths != null) {
                ParallelMetaPath other = createInstance(otherDirectionSubMetaPaths);
                other.otherDirection = this;
                other.otherDirectionInitilized = true;
                super.otherDirection = other;
            }
        }
        return super.otherDirection;
    }

    /**
     * @return
     */
    private final IMetaPath[] getOtherDirectionMetaPaths() {
        //these kind of constructing the other direction of a metapath makes not
        //sense in every case but only in some very special cases. One condition
        //for sense is that all submetapaths have the same start- and endelements,
        //if the metapath is a DifferenceMetaPath or SectionMetaPath.
        //The only point where it is used at the moment (01.09.2020) is the
        //TLGMServiceMetaPathsDefinition in the Service-metamodel plugin for the
        //(at the moment) only use of a DifferenceMetaPath.
        IMetaPath[] otherDirectionMetaPaths = new IMetaPath[subMetaPaths.size()];
        for (int i = 0; i < otherDirectionMetaPaths.length; i++) {
            IMetaPath subMetaPath = subMetaPaths.get(i);
            IMetaPath subMetaPathOtherDirection = subMetaPath.getOtherDirection();
            if (subMetaPathOtherDirection == null) {
                return null;
            }
            otherDirectionMetaPaths[i] = subMetaPathOtherDirection;
        }
        return otherDirectionMetaPaths;
    }

    /**
     * @param subMetaPaths
     * @return
     */
    public abstract ParallelMetaPath createInstance(IMetaPath... subMetaPaths);

    @Override
    public boolean isRemoveable(final boolean checkEndElement) {
        return false;
    }

    @Override
    public boolean isDirected() {
        for (IMetaPath metaPath : subMetaPaths) {
            if (!metaPath.isDirected()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsPropertyTransferEdge() {
        for (IMetaPath metaPath : subMetaPaths) {
            if (metaPath.containsPropertyTransferEdge()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param simpleMetaPaths
     * @return an array of the {@link SimpleMetaPath}s in the collection
     */
    public static SimpleMetaPath[] toArray(final Collection<SimpleMetaPath> simpleMetaPaths) {
        SimpleMetaPath[] simpleMetaPathsArray = new SimpleMetaPath[simpleMetaPaths.size()];
        return simpleMetaPaths.toArray(simpleMetaPathsArray);
    }

}
