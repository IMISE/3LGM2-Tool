package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.ModelElement;

public class IDEmptyError extends AbstractIDError {

    public IDEmptyError(final ModelElement me, final Object errorField, final GDCollection gdcoll) {
        super(me, errorField, gdcoll);
    }

}
