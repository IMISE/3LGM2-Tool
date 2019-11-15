package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.path.MetaPathDefinition;

/**
 * Use this Interface to mark inferenced Edges. These edges emerge automatically if
 * their condition is fulfilled. The condition is a MetaPath which indicates the
 * needed connection(s) to the elements. The condition MetaPaths are defined in the
 * {@link MetaPathDefinition} of the corresponding {@link MetaModelDefinition}.
 * A valid condition MetaPath must start by the start element type of this edge and
 * and by the end element type of this edge. Inferenced edges are automatic derived
 * edges for a path.
 *
 * @author AXS (08.11.2019)
 */
public interface InferenceEdge {

}
