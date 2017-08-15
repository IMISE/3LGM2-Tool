package de.imise.tool3lgm.graphtools.userfield;

import de.imise.util.HashStringGenerator;

public class UserFieldPropertyDialogDefinitionTab implements Cloneable, HashSource {

    /**
     * Ist der <code>String</code>, durch den der Tab eindeutig identifizierbar wird.
     */
    private String hashCode;

    /**
     * Gibt an, zu welcher Klasse das <code>UserField</code> gehört.
     */
    private Class<? extends UserFieldTarget> targetClass;

    /**
     * Ist der Name des <code>UserField</code>s.
     */
    private String name = "";

    public UserFieldPropertyDialogDefinitionTab() {
        hashCode = HashStringGenerator.getHash("TAB");
    }

    @Override
    public String getHashCode() {
        return hashCode;
    }

    public final void setHashCode(final String hashCode) {
        this.hashCode = hashCode;
    }

    public final Class<? extends UserFieldTarget> getTargetClass() {
        return targetClass;
    }

    public final void setTargetClass(final Class<? extends UserFieldTarget> targetClass) {
        this.targetClass = targetClass;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

}
