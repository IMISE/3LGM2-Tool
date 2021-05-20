/*
 * Created on 30.10.2007
 */
package de.imise.tool3lgm.graphtools.userfield.definition;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ENABLE_FORMULA_CALCULATION;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField.Style;
import de.imise.util.htmlxml.XMLCharacterCoder;

/**
 * Oberklasse für alle Klassen, denen man benutzerdefinierte Eigenschaften geben
 * kann. Hinweis: Beim Löschen eines UserFields bleibt seine ID und Value-Paar
 * in der HashMap. Nimmt man das Löschen zurück, indem man im
 * <code>UserFieldDeclarationDialog</code> auf abbrechen geht, wird die beim
 * Öffnen des Dialogs erstellte Kopie der <code>UserFieldDefinitions</code>
 * zurück gesetzt und alles ist wie vorher.
 *
 * @author AXS
 * @created 30.10.2007
 */
public abstract class UserFieldTarget implements Cloneable {

    /**
     * Mappt von den für das Modell definierten <code>UserField</code>s auf die
     * vom Benutzer eingegebenen Werte.
     */
    private Map<UserField, String> userFieldToInputValuesMap = null;

    /**
     * Mappt von den für das Modell definierten <code>UserField</code>s auf die
     * berechneten Werte. Die Map wird nur initialisiert, wenn das
     * <code>UserFieldTarget</code> mind. einen berechneten Wert hat.
     */
    private Map<UserField, String> userFieldToCalculatedValuesMap = null;

    /**
     * Ein konstant lereres Set
     */
    private static final Set<UserField> EMPTY_SET = new HashSet<>(0);

    @Override
    protected Object clone() {
        UserFieldTarget retVal;
        try {
            retVal = (UserFieldTarget) super.clone();
        } catch (Exception e) {
            return null;
        }
        retVal.userFieldToInputValuesMap = userFieldToInputValuesMap == null ? null : new HashMap<>(userFieldToInputValuesMap);

        return retVal;
    }

    /**
     * @return the {@link UserFieldDefinitions}
     */
    public abstract UserFieldDefinitions getUserFieldDefinitions();

    /**
     * Ersetzt alle UserFields in der Map der Eingabewerte durch die in der
     * übergebenen Definition mit derselben ID.
     *
     * @param definitions
     */
    public void replaceUserFields(final UserFieldDefinitions definitions) {
        //Kopie des bestehenden KeySets anlegen. toArray() ist Performace-technisch am besten
        //nicht einfach über keySet() iterieren, weil sich das Set in der Schleife ändert!
        if (userFieldToInputValuesMap != null) {
            Set<UserField> keySet = userFieldToInputValuesMap.keySet();
            Object[] oldUserFields = keySet.toArray();
            for (Object oldUserFieldObject : oldUserFields) {
                if (oldUserFieldObject != null) {
                    UserField oldUserField = (UserField) oldUserFieldObject;
                    String userFieldID = oldUserField.getID();
                    UserField newUserField = definitions.getUserField(userFieldID);
                    String inputValue = userFieldToInputValuesMap.remove(oldUserField);
                    userFieldToInputValuesMap.put(newUserField, inputValue);
                }
            }
        }
    }

    ////////////////////////////
    // eingegebene Kennzahlen //
    ////////////////////////////

    /**
     * Liefert den vom Benutzer für das <code>UserField</code> mit der
     * übergebenen ID eingegebenen Wert. Wurde noch keiner eingegeben oder
     * gehört das betreffende <code>UserField</code> gar nicht zu diesem
     * <code>UserFieldTarget</code>, kommt <code>null</code> zurück.
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
            userFieldToInputValuesMap = new HashMap<>(5);
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
     * Hängt an den übergebenen <code>StringBuilder</code> für jedes
     * <code>UserField</code> einen XML-Eintrag an.
     *
     * @param sb <code>StringBuilder</code>, an den die Einträge gehängt werden
     */
    protected void appendUserFieldXMLString(final StringBuilder sb) {
        for (UserField keyUserField : getUserFieldInputValueKeys()) {
            //Hier muss geprüft werden, ob das rauszuschreibende userfield null ist, denn darf es nicht rausgeschrieben werden.
            if (keyUserField != null) {
                sb.append("<userField hash=\"" + keyUserField.getID() + "\">" + XMLCharacterCoder.encodeString(getUserFieldInputValue(keyUserField)) + "</userField>");
            }
        }
    }

    ////////////////////////////
    // berechntete Kennzahlen //
    ////////////////////////////

    /**
     * Liefert den berechneten Wert eines <code>UserField</code>s, wenn es ihn
     * gibt. Gibt es ihn nicht, weil er noch nicht berechnet wurde oder das
     * userField gar nicht zu dieser Klasse gehört, kommt <code>null</code>
     * zurück. ACHTUNG: Diese Funktion sollte nur die Klasse
     * <code>UserField</code> aufrufen. Kein anderer! Möchte man den berechneten
     * oder eingegebenen Wert eines <code>UserField</code>s für ein Element
     * abfragen, sollte das immer über die Funktion
     * <code>UserField.getValue(ModelElement)</code> geschehen, da diese ggf.
     * die Neuberechnung von Kennzahlformeln anstößt.
     *
     * @param userField <code>UserField</code> für das der berechnete Wert
     *            dieses Elementes zurück gegeben werden soll
     * @see #_getUserFieldInputValue(String)
     * @see #_getUserFieldInputValue(UserField)
     * @see UserField#getValue(UserFieldTarget)
     * @return den für die übergebene ID eines Formel-UserFields vermerkten
     *         berechenten Wert oder <code>EMPTY_STRING</code>, wenn kein Wert
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
            userFieldToCalculatedValuesMap = new HashMap<>(1);
        }
        userFieldToCalculatedValuesMap.put(userField, value);
    }

    /**
     * Löscht alle Einträge aus der Map, die von den UserFields auf die
     * berechneten Werte mappt.
     */
    protected void resetCalculatedUserFieldMap() {
        if (userFieldToCalculatedValuesMap != null) {
            userFieldToCalculatedValuesMap.clear();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // AXS: 29.03.2021                                                     //
    // Die folgenden getValue()-Funktionen waren ursprünglich in UserField //
    /////////////////////////////////////////////////////////////////////////

    /**
     * Liefert den unformatierten Wert einer Kennzahl.
     *
     * @param userField UserField, für das der Wert zurück gegeben werden soll
     * @return
     */
    public String getValue(final UserField userField) {
        //wenn es eine Kennzahlformel ist, deren Wert ermittelt werden soll
        if (userField.hasStyle(Style.FORMULA)) {
            //wenn die globale Option der Berechnung eingeschaltet ist
            if (OPTION_ENABLE_FORMULA_CALCULATION.is()) {
                UserFieldDefinitions definitions = getUserFieldDefinitions();
                //alle berechneten Kennzahl Werte löschen
                definitions.reset();
                //falls das reset nicht ausgeführt wurde, da sich nichts geändert
                //hatte -> value hat den bisherigen Wert
                //falls das reset wirklich ausgeführt wurde -> value hat den Wert
                //UserField.EMPTY_STRING
                String value = getCalculatedUserFieldValue(userField);
                //wenn das reset ausgeführt wurde
                if (value == UserField.EMPTY_STRING) {
                    //berechne den Wert neu
                    value = definitions.calculate(userField, this);
                    //setze ihn im UserFieldTarget
                    setCalculatedUserFieldValue(userField, value);
                }
                //gib den berechneten Wert zurück
                return value;
            }
            //wenn nicht berechnet werden sollte -> gib einen leeren String zurück
            return UserField.CALCULATION_DISABLED;
        }
        //gib den eingegebenen Wert des UserFields zurück
        return getUserFieldInputValue(userField);
    }

}
