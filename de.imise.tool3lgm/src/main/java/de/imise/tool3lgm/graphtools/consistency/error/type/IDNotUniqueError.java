package de.imise.tool3lgm.graphtools.consistency.error.type;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;

/**
 * ACHTUNG: Wenn der Name der Klasse refactored werden sollte, müssen die
 * ErrorResourcen angepasst werden!
 *
 * @author Ich
 * @create 17.08.2015
 */
public class IDNotUniqueError extends AbstractIDError {

    /**
     * Alle Elemente, die dieselbe ID benutzen (auch das ModelElement des
     * Fehlers)
     */
    private final Collection<ModelElement> allWithSameID;

    /**
     * @param me
     * @param idUserField
     * @param allWithSameID
     */
    public IDNotUniqueError(final ModelElement me, final UserField idUserField, final Collection<ModelElement> allWithSameID) {
        super(me, idUserField);
        this.allWithSameID = allWithSameID;
    }

    @Override
    protected String[] getMessageReplaceArguments() {
        String[] replacements = new String[1];
        replacements[0] = Integer.toString(allWithSameID.size() - 1);
        return replacements;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        IDNotUniqueError other = (IDNotUniqueError) obj;
        return allWithSameID.equals(other.allWithSameID);
    }

}
