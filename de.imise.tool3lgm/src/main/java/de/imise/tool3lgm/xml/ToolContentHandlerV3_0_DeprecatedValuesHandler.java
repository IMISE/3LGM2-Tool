package de.imise.tool3lgm.xml;

import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;

/**
 * Spezieller Hander für Ausnahmen. In alten Modellen können für PDVB technische Merkmale angegeben sein, die
 * ab jetzt nicht mehr als eigene spezielle Eigenschaften existieren, sondern über UserFields abgebildet werden.
 * Um die alten Werte korrket in die neuen UserFields einzutragen wird hier das Mapping vorgenommen. Die Hash-Werte
 * der UserFields kommen aus der DefaultUserField-Definition im Resourcenverzeichnis.
 *
 * @author astruebi
 * @created 14.06.2017
 */
public class ToolContentHandlerV3_0_DeprecatedValuesHandler {

    public ToolContentHandlerV3_0_DeprecatedValuesHandler() {
        // TODO Auto-generated constructor stub
    }

    public static boolean putDeprecatedXMLFieldString(final GDCollection gdcoll, final ModelElement me, final String fieldName, final String value) {
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
        UserField userField = null;
        String correctedValue = value;
        if (fieldName.equals("os_type")) {
            userField = definitions.getUserField("USERFIELD_1497433961756_10");
        } else if (fieldName.equals("serial")) {
            userField = definitions.getUserField("USERFIELD_1497433989459_11");
        } else if (fieldName.equals("inventar")) {
            userField = definitions.getUserField("USERFIELD_1497434000226_12");
        } else if (fieldName.equals("disksize")) {
            correctedValue = extractNumberValue(value);
            userField = definitions.getUserField("USERFIELD_1497434147788_18");
        } else if (fieldName.equals("ramsize")) {
            correctedValue = extractNumberValue(value);
            userField = definitions.getUserField("USERFIELD_1497434038201_13");
        } else if (fieldName.equals("processor")) {
            userField = definitions.getUserField("USERFIELD_1497434106454_17");
        } else if (fieldName.equals("downtime")) {
            correctedValue = extractNumberValue(value);
            userField = definitions.getUserField("USERFIELD_1497433330855_0");
        }
        if (userField != null) {
            me.setUserFieldInputValue(userField, correctedValue);
            return true;
        }
        return false;
    }

    /**
     * Extrahiert aus einem String den ersten Teilstring, der als Zahl (mit beliebig vielen Kommas oder Punkten darin) erkannt wird
     * und löscht alle Punkte oder Kommas außer das letzte.
     *
     * @param s
     * @return
     */
    private static final String extractNumberValue(final String s) {
        String n = s.replace(',', '.');
        int numberStart = -1;
        int numberEnd = -1;
        for (int i = 0; i < n.length(); i++) {
            if (numberStart < 0) {
                if (isNumberChar(n.charAt(i))) {
                    numberStart = i;
                    numberEnd = i + 1;
                }
            } else if (isNumberChar(n.charAt(i))) {
                numberEnd++;
            } else if (numberStart >= 0) {
                break;
            }
        }
        n = numberStart >= 0 ? numberEnd == n.length() ? n : n.substring(numberStart, numberEnd) : "";
        if (numberStart >= 0) {
            n = removeSuperflousDecimalSeprators(n);
        }
        return n;
    }

    private static final boolean isNumberChar(final char c) {
        return '0' <= c && c <= '9' || c == ',' || c == '.';
    }

    /**
     * Entfernt alle Punkte aus dem String außer den letzten.
     *
     * @param s
     * @return
     */
    private static String removeSuperflousDecimalSeprators(final String s) {
        final char POINT = '.';
        int lastDecimalSeparatorIndex = s.lastIndexOf(POINT);
        if (s.indexOf('.') != lastDecimalSeparatorIndex) {
            int decimalSeparatorIndexFromEnd = s.length() - 1 - lastDecimalSeparatorIndex;
            String n = s.replace("" + POINT, "");
            if (!n.isEmpty()) {
                lastDecimalSeparatorIndex = n.length() - decimalSeparatorIndexFromEnd;
                n = n.substring(0, lastDecimalSeparatorIndex) + POINT + n.substring(lastDecimalSeparatorIndex);
            }
            return n;
        }
        return s;
    }

}
