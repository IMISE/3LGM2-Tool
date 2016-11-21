/*
 * Created on 30.10.2007
 */
package de.imise.tool3lgm.graphtools.userfield;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.XMLCharacterCoder;

/**
 * Oberklasse für alle Klassen, denen man benutzerdefinierte Eigenschaften geben kann. Hinweis: Beim Löschen eines UserFields bleibt sein Hash und
 * Value-Paar in der HashMap. Nimmt man das Löschen zurück, indem man im <code>UserFieldDeclarationDialog</code> auf abbrechen geht, wird die beim
 * Öffnen des Dialogs erstellte Kopie der <code>UserFieldDefinitions</code> zurück gesetzt und alles ist wie vorher.
 * 
 * @author AXS
 * @created 30.10.2007
 */
public class UserFieldTarget implements Cloneable {

    /**
     * Mappt von den für das Modell definierten <code>UserField</code>s auf die vom Benutzer eingegebenen Werte.
     */
    private HashMap<UserField, String> userFieldToInputValuesMap = null;

    /**
     * Mappt von den für das Modell definierten <code>UserField</code>s auf die berechneten Werte. Die Map wird nur initialisiert, wenn das
     * <code>UserFieldTarget</code> mind. einen berechneten Wert hat.
     */
    private HashMap<UserField, String> userFieldToCalculatedValuesMap = null;

    /**
     * Ein konstant lereres Set
     */
    private static final Set<UserField> EMPTY_SET = new HashSet<UserField>(0);

    /**
	 * 
	 */
    public UserFieldTarget() {
        super();
    }

    @Override
    protected Object clone() {
        UserFieldTarget retVal;
        try {
            retVal = (UserFieldTarget) super.clone();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return null;
        }
        retVal.userFieldToInputValuesMap = userFieldToInputValuesMap == null ? null : new HashMap<UserField, String>(userFieldToInputValuesMap);
        //Das folgende kann man sich schenken, weil nur neue Strings gesetzt werden. Da man Strings aber nicht ändern kann
        //ohne sie neu zu setzen, können hier die Referenzen erstmal ruhig auch auf das Origibal zeigen
        //		for (UserField key : getUserFieldInputValueKeys())
        //			retVal.setUserFieldInputValue(key, new String(getUserFieldInputValue(key)));

        return retVal;
    }

    ////////////////////////////
    // eingegebene Kennzahlen //
    ////////////////////////////

    /**
     * Liefert den vom Benutzer für das <code>UserField</code> mit dem übergebenen HashCode eingegebenen Wert. Wurde noch keiner eingegeben oder
     * gehört das betreffende <code>UserField</code> gar nicht zu diesem <code>UserFieldTarget</code>, kommt <code>null</code> zurück.
     * 
     * @param userField
     * @return den eingebenen Wert oder <code>UserField.EMPTY_STRING</code>
     */
    public String getUserFieldInputValue(final UserField userField) {
        if (userFieldToInputValuesMap == null) {
            return UserField.EMPTY_STRING;
        }
        Object value = userFieldToInputValuesMap.get(userField);
        if (value != null) {
            return value.toString();
        }
        return UserField.EMPTY_STRING;
    }

    /**
     * @param userField
     * @param value
     */
    public void setUserFieldInputValue(final UserField userField, String value) {

        if (userFieldToInputValuesMap == null) {
            userFieldToInputValuesMap = new HashMap<UserField, String>(5);
        }
        userFieldToInputValuesMap.remove(userField);
        value = value.trim();
        if (value == null || value.equals("") || value.equals("\"\"") || value.equalsIgnoreCase("null") || value.equalsIgnoreCase("__3LGM_DELETE__")) {
            return;
        }
        if (value.length() > 0) {
            userFieldToInputValuesMap.put(userField, value);
        }
    }

    /**
     * Löscht das übergebene <code>UserField</code> aus der Eingabewerte-Map
     * 
     * @param userField
     * @return
     */
    public Object removeUserField(final UserField userField) {

        // Wenn für eine Elementart keine primären Kennzahlen definiert sind, 
        // ist ist die <code>userFieldToInputValuesMap</code> == null
        // In diesem Fall gibt es auch nichts zu entfernen
        if (userFieldToInputValuesMap == null) {
            return null;
        }
        return userFieldToInputValuesMap.remove(userField);
    }

    /**
     * @return
     */
    public Set<UserField> getUserFieldInputValueKeys() {
        if (userFieldToInputValuesMap == null) {
            return EMPTY_SET;
        }
        return userFieldToInputValuesMap.keySet();
    }

    /**
     * Hängt an den übergebenen <code>StringBuilder</code> für jedes <code>UserField</code> einen XML-Eintrag an.
     * 
     * @param sb <code>StringBuilder</code>, an den die Einträge gehängt werden
     */
    protected void appendUserFieldXMLString(final StringBuilder sb) {
        for (UserField keyUserField : getUserFieldInputValueKeys()) {
            //Hier muss geprüft werden, ob das rauszuschreibende userfield null ist, denn darf es nicht rausgeschrieben werden.
            if (keyUserField != null) {
                sb.append("<userField hash=\"" + keyUserField.getHashCode() + "\">" + XMLCharacterCoder.encodeString(getUserFieldInputValue(keyUserField)) + "</userField>");
            }
        }
    }

    ////////////////////////////
    // berechntete Kennzahlen //
    ////////////////////////////

    /**
     * Liefert den berechneten Wert eines <code>UserField</code>s, wenn es ihn gibt. Gibt es ihn nicht, weil er noch nicht berechnet wurde oder das
     * userField gar nicht zu dieser Klasse gehört, kommt <code>null</code> zurück. ACHTUNG: Diese Funktion sollte nur die Klasse
     * <code>UserField</code> aufrufen. Kein anderer! Möchte man den berechneten oder eingegebenen Wert eines <code>UserField</code>s für ein Element
     * abfragen, sollte das immer über die Funktion <code>UserField.getValue(ModelElement)</code> geschehen, da diese ggf. die Neuberechnung von
     * Kennzahlformeln anstößt.
     * 
     * @param userField <code>UserField</code> für das der berechnete Wert dieses Elementes zurück gegeben werden soll
     * @see #_getUserFieldInputValue(String)
     * @see #_getUserFieldInputValue(UserField)
     * @see UserField#getValue(ModelElement)
     * @return den für den übergebenen HashString eines Formel-UserFields vermerkten berechenten Wert oder <code>EMPTY_STRING</code>, wenn kein Wert
     *         vermerk ist
     */
    protected String getCalculatedUserFieldValue(final UserField userField) {
        if (userFieldToCalculatedValuesMap == null) {
            return UserField.EMPTY_STRING;
        }
        Object value = userFieldToCalculatedValuesMap.get(userField);
        if (value == null) {
            return UserField.EMPTY_STRING;
        }
        return value.toString();
    }

    /**
     * Setzt den berechneten Wert eines <code>UserField</code>s.
     */
    protected void setCalculatedUserFieldValue(final UserField userField, final String value) {
        if (userFieldToCalculatedValuesMap == null) {
            userFieldToCalculatedValuesMap = new HashMap<UserField, String>(1);
        }
        userFieldToCalculatedValuesMap.put(userField, value);
    }

    /**
     * Löscht alle Einträge aus der Map, die von den UserFields auf die berechneten Werte mappt.
     */
    protected void resetCalculatedUserFieldMap() {
        if (userFieldToCalculatedValuesMap != null) {
            userFieldToCalculatedValuesMap.clear();
        }
    }
}
