package de.imise.tool3lgm.graphtools.metamodel;

import static de.imise.tool3lgm.graphtools.metamodel.Edge.getEndClass;
import static de.imise.tool3lgm.graphtools.metamodel.Edge.getStartClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @author AXS (15.08.2017)
 */
public class CopyDependencies {

    private final Map<Class<? extends ModelElement>, Collection<Class<? extends ModelElement>>> copyDependencies = new HashMap<>();

    private static final Collection<Class<? extends ModelElement>> EMPTY_COLLECTION = ImmutableSet.of();

    private final Set<Class<? extends ModelElement>> avoidDuplicatesClasses = new HashSet<>();

    //////////////////////
    // copyDependencies //
    //////////////////////

    protected final void set(final Class<? extends ModelElement> elementClass, @SuppressWarnings("unchecked") final Class<? extends ModelElement>... copyClasses) {
        copyDependencies.put(elementClass, ImmutableSet.copyOf(copyClasses));
    }

    public final Collection<Class<? extends ModelElement>> get(final Class<? extends ModelElement> elementClass) {
        Collection<Class<? extends ModelElement>> immutableCollection = copyDependencies.get(elementClass);
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

    public final boolean avoidDuplicates(final Class<? extends ModelElement> elementClass) {
        if (Edge.class.isAssignableFrom(elementClass)) {
            Class<? extends Edge> edgeClass = elementClass.asSubclass(Edge.class);
            return avoidDuplicates(getStartClass(edgeClass)) || avoidDuplicates(getEndClass(edgeClass));
        }
        return avoidDuplicatesClasses.contains(elementClass);
    }

}
