package de.imise.tool3lgm.tools;

import java.util.Collection;
import java.util.Set;

import de.imise.util.collections.ExtendedSet;

import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public final class ModelTools {
	
	private ModelTools() {}
	
	/**
	 * Gibt die zu den spezifizierten {@link ElementContainer}n gehörigen {@link ModelElement} zurück.
	 * @param containers
	 * @return
	 */
	public static final Set<ModelElement> toModelElements (Collection<? extends ElementContainer> containers) {
		Set<ModelElement> modelElements = new ExtendedSet<ModelElement>(containers.size());
		for (ElementContainer ec : containers)
			modelElements.add(ec.getElement());
		return modelElements;
	}
	
	/**
	 * Gibt die zu den spezifizierten {@link ModelElement} gehörigen {@link ElementContainer} zurück.
	 * @param containers
	 * @return
	 */
	public static final Set<ElementContainer> toElementContainers (Collection<? extends ModelElement> modelElements, GraphDocument doc) {
		Set<ElementContainer> containers = new ExtendedSet<ElementContainer>(modelElements.size());
		for (ModelElement me : modelElements) {
			containers.add(me.getContainer(doc));
		}
		return containers;
	}

}
