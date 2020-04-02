package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.elements.Edge.getStartClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.ElementaryMetaPathHandler;

/**
 * @author AXS (15.08.2017)
 */
public class CopyDependencies extends MetaModelSpecificAdapter {

    /**
     *
     */
    private final Map<Class<? extends ModelElement>, Collection<ElementaryMetaPath>> copyDependencies = new HashMap<>();

    /**
     *
     */
    private static final Collection<ElementaryMetaPath> EMPTY_COLLECTION = ImmutableSet.of();

    /**
     *
     */
    private final Set<Class<? extends ModelElement>> avoidDuplicatesClasses = new HashSet<>();

    /**
     * @param metaModel
     */
    public CopyDependencies(final MetaModel metaModel) {
        super(metaModel);
    }

    //////////////////////
    // copyDependencies //
    //////////////////////

    /**
     * @param elementClass
     * @param copyElementaryMetaPaths
     */
    protected final void set(final Class<? extends ModelElement> elementClass, @SuppressWarnings("unchecked") final Class<? extends Edge>... copyEdgeClasses) {
        MetaModel metaModel = getMetaModel();
        ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();
        ImmutableSet.Builder<ElementaryMetaPath> copyElementaryMetaPathsBuilder = ImmutableSet.builder();
        for (Class<? extends Edge> edgeClass : copyEdgeClasses) {
            ElementaryMetaPath elementaryMetaPath = emph.getMetaPath(elementClass, edgeClass);
            copyElementaryMetaPathsBuilder.add(elementaryMetaPath);
        }
        copyDependencies.put(elementClass, copyElementaryMetaPathsBuilder.build());
    }

    /**
     * @param elementClass
     * @param copyElementaryMetaPaths
     */
    protected final void set(final Class<? extends ModelElement> elementClass, final ElementaryMetaPath... copyElementaryMetaPaths) {
        copyDependencies.put(elementClass, ImmutableSet.copyOf(copyElementaryMetaPaths));
    }

    /**
     * @param elementClass
     * @return
     */
    public final Collection<ElementaryMetaPath> get(final Class<? extends ModelElement> elementClass) {
        Collection<ElementaryMetaPath> immutableCollection = copyDependencies.get(elementClass);
        return immutableCollection == null ? EMPTY_COLLECTION : immutableCollection;
    }

    /////////////////////
    // avoidDuplicates //
    /////////////////////

    /**
     * @author Thomas Rudert sollte auf true gesetzt werden, wenn beim Kopieren kein Duplikat erstellte werden soll, falls das Modelelement nur durch
     *         aufgeloeste copyDependencies mitkopiert wird <br>
     *         (Bsp: kopieren von PhyDvBausteinen soll der Standort nicht doppelt vorhanden sein)
     * @param elementClass
     */
    protected final void addToAvoidDuplicates(final Class<? extends ModelElement> elementClass) {
        avoidDuplicatesClasses.add(elementClass);
    }

    /**
     * @param elementClass
     * @return
     */
    public final boolean avoidDuplicates(final Class<? extends ModelElement> elementClass) {
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            return avoidDuplicates(getStartClass(edgeClass)) && avoidDuplicates(getEndClass(edgeClass));
        }
        return avoidDuplicatesClasses.contains(elementClass);
    }

}
