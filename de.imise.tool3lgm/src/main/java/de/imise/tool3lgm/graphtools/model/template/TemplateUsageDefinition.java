package de.imise.tool3lgm.graphtools.model.template;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * AXS: 25.06.2020: Nichts hiervon wird genutzt! Das kann man aber evtl. gebrauchen,
 * wenn man das Draggen umsetzt, um die draggbaren Elemente zu definieren und was
 * mitkommen soll, wenn man sie draggt. Ansonsten wird das hier angedachte (insbesondere
 * die CopyDependencies) bereits über die ganz normalen CopyDependencies des Metamodells
 * erledigt. Auf der rekursive Auschluss in beide Richtungen erfolgt in resolveCopyDependencies()
 * beim Kopieren von einem Modell (z.B. einem Template) zu einem anderen (z.B. zu einem Zielmodell.
 * <br>
 * Defines the elements and their relations which can be applied from a template to a regular model.
 *
 * @author AXS (19.09.2019)
 */
public class TemplateUsageDefinition {

    /**
     * If <code>true</code> every element of a template that can exist without other elements
     * can be applied from a template to a model.
     * If <code>false</code> only elements which are stated in {@link #copyableElements} can
     * be applied.
     * Elements which need other elements to exist means that these elements need at least one
     * edge to another element with a mininmum cardinality of 1 or greater.
     * Default is <code>true</code>.
     */
    public boolean allowApplyForAllElements = true;

    /**
     * All element types which can be applied direct from a template to model. This is only
     * relevant if {@link #allowCopyForAllElements} is <code>false</code>.
     */
    private final Set<Class<? extends ModelElement>> appliableElements = new HashSet<>();

    /**
     * Maps from a element type to the edge type which must be copied too
     * an element of the given type will be apllied from the template to a model.
     */
    private final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToCopyDependeciesForwardEdgeClasses = HashMultimap.create();

    private final Multimap<Class<? extends ModelElement>, Class<? extends Edge>> elementClassToCopyDependeciesBackwardEdgeClasses = HashMultimap.create();

    /**
     *
     */
    public TemplateUsageDefinition() {
        init();
    }

    /** Initialize the defintion. Should be overwritten in subclasses. Default implementation is empty. */
    public void init() {
        //Default implementation is empty
    }

    /**
     * Adds the given elements to the {@link #copyableElements} set.
     *
     * @param copyableElements
     */
    @SafeVarargs
    protected final void addAppliableElements(final Class<? extends ModelElement>... appliableElements) {
        for (Class<? extends ModelElement> appliableElement : appliableElements) {
            this.appliableElements.add(appliableElement);
        }
    }

    @SafeVarargs
    public final void addAppliableElementAndCopyDependencies(final Class<? extends ModelElement> elementClass, final Class<? extends Edge>... egdeClasses) {
        addAppliableElements(elementClass);
        addAppliableElementAndForwardCopyDependency(elementClass, egdeClasses);
        addAppliableElementAndBackwardCopyDependency(elementClass, egdeClasses);
    }

    @SafeVarargs
    public final void addAppliableElementAndForwardCopyDependency(final Class<? extends ModelElement> elementClass, final Class<? extends Edge>... edgeClasses) {
        addAppliableElements(elementClass);
        elementClassToCopyDependeciesForwardEdgeClasses.putAll(elementClass, Arrays.asList(edgeClasses));
    }

    @SafeVarargs
    public final void addAppliableElementAndBackwardCopyDependency(final Class<? extends ModelElement> elementClass, final Class<? extends Edge>... edgeClasses) {
        addAppliableElements(elementClass);
        elementClassToCopyDependeciesBackwardEdgeClasses.putAll(elementClass, Arrays.asList(edgeClasses));
    }

}
