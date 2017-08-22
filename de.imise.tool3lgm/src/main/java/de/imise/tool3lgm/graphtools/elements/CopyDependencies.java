package de.imise.tool3lgm.graphtools.elements;

import static de.imise.tool3lgm.graphtools.elements.Kante.getEndClass;
import static de.imise.tool3lgm.graphtools.elements.Kante.getStartClass;

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

    public CopyDependencies() {
    }

    //////////////////////
    // copyDependencies //
    //////////////////////

    protected void set(final Class<? extends ModelElement> elementClass, @SuppressWarnings("unchecked") final Class<? extends ModelElement>... copyClasses) {
        copyDependencies.put(elementClass, ImmutableSet.copyOf(copyClasses));
    }

    public Collection<Class<? extends ModelElement>> get(final Class<? extends ModelElement> elementClass) {
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
    protected void addToAvoidDuplicates(final Class<? extends ModelElement> elementClass) {
        avoidDuplicatesClasses.add(elementClass);
    }

    public boolean avoidDuplicates(final Class<? extends ModelElement> elementClass) {
        if (Kante.class.isAssignableFrom(elementClass)) {
            Class<? extends Kante> edgeClass = elementClass.asSubclass(Kante.class);
            return avoidDuplicates(getStartClass(edgeClass)) || avoidDuplicates(getEndClass(edgeClass));
        }
        return avoidDuplicatesClasses.contains(elementClass);
    }

}
