package de.imise.tool3lgm.graphtools.userfield;

import java.util.Set;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;

public class UserFieldDefinitionsAnalyzer {

    private final UserFieldDefinitions definitions;

    public UserFieldDefinitionsAnalyzer(final UserFieldDefinitions definitions) {
        this.definitions = definitions;
    }

    /**
     * @param userFieldTargetClass
     * @return <code>true</code> if there is at least one {@link UserField} defined for the userFieldTargetClass, sonst <code>false</code>
     */
    public boolean hasNumberFields(final Class<? extends UserFieldTarget> userFieldTargetClass) {
        for (UserField uf : definitions.getUserFields(userFieldTargetClass)) {
            if (uf.hasClassfificationStyle()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param style
     * @return <code>true</code>, wenn für irgendeine Knotenklasse (= Unterklasse von {@link UserFieldTarget}) mind. ein UserField mit dem übergebenen
     *         Style definiert ist, sonst <code>false</code>. Für Kantenklasse wird hier nicht geguckt.
     */
    public boolean hasStyle(final UserField.Style style) {
        Set<Class<? extends UserFieldTarget>> userFieldTargets = definitions.getUserFieldTargets();
        for (Class<? extends UserFieldTarget> userFieldTarget : userFieldTargets) {
            if (Knoten.class.isAssignableFrom(userFieldTarget)) {
                Iterable<UserField> userFields = definitions.getUserFields(userFieldTarget);
                for (UserField userField : userFields) {
                    if (userField.hasStyle(style)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Prüft, ob
     * 
     * @return <code>true</code>, wenn für wenigstens eine Kantenklasse ein UserField definiert ist, das eine Kennzahl oder Kennzahlformel ist, sonst
     *         <code>false</code>
     */
    public boolean hasDistributionWeights() {
        Set<Class<? extends UserFieldTarget>> userFieldTargets = definitions.getUserFieldTargets();
        for (Class<? extends UserFieldTarget> userFieldTarget : userFieldTargets) {
            if (Kante.class.isAssignableFrom(userFieldTarget)) {
                Iterable<UserField> userFields = definitions.getUserFields(userFieldTarget);
                for (UserField userField : userFields) {
                    if (userField.hasClassfificationStyle()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return <code>true</code>, wenn für irgendeine {@link UserFieldTarget}-Klasse ein {@link UserField} definiert ist, das eine einfache
     *         Teilwertsumme ist, sonst <code>false</code>
     */
    public boolean hasSimpleFractionValueSums() {
        Set<Class<? extends UserFieldTarget>> userFieldTargets = definitions.getUserFieldTargets();
        for (Class<? extends UserFieldTarget> userFieldTarget : userFieldTargets) {
            Iterable<UserField> userFields = definitions.getUserFields(userFieldTarget);
            for (UserField userField : userFields) {
                if (userField.isSimplePartValueSumFormula()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return <code>true</code>, wenn wenigstens ein globales {@link UserField} definiert ist.
     */
    public boolean hasGlobalUserFields() {
        boolean hasValues = definitions.getGlobalUserFields().iterator().hasNext();
        return hasValues;
    }

}
