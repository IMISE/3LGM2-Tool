package de.imise.tool3lgm.graphtools.path.metapaths;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;

/**
 * Dieses Konsturkt dient einzig un allein dazu, Pfade über eine Liste aus <code>Class<? extends ModelElement></code> zu erzeugen und bei Bedarf für
 * Kanten Richtungen mit anzugeben, wenn sie sich nicht eindeutig ableiten lässt.
 *
 * @author AXS (5 Dec 2018)
 */
public abstract class Direction extends ModelElement {

    public abstract class FORWARD extends Direction {
    };
    public abstract class BACKWARD extends Direction {
    };
    public abstract class FORWARD_FORWARD extends Direction {
    };
    public abstract class FORWARD_BACKWARD extends Direction {
    };
    public abstract class FORWARD_DOUBLE extends Direction {
    };
    public abstract class BACKWARD_FORWARD extends Direction {
    };
    public abstract class BACKWARD_BACKWARD extends Direction {
    };
    public abstract class BACKWARD_DOUBLE extends Direction {
    };
}
