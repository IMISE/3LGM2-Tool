package de.imise.tool3lgm.graphtools.userfield.calculator;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;

import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;

public class PartValueSumFunction {

    /**
     * Errechnet das Ergebnis der Verrechnungsfunktion TEILWERTSUMME. Die Funktion hat 2, 3 oder 4 Argumente. Das erste Argument ist in jedem Fall der
     * Name der Assoziation über den verrechnet wird, das 2. ist immer das <code>UserField</code> der verbundenen Klasse, dessen Wert verrechnet
     * werden soll. Bei 3 Argumenten kann das 3. Argument entweder ein Verteilungsgewicht sein oder, wenn die Kante über die verrechent wird eine
     * <code>PartOfBeziehung</code> ist, die Richtung der Verrechnung. Bei 4 Argumenten ist das 3. Argument immer das Verteilungsgewicht und das 4
     * immer die Richtung.
     *
     * @param definitions
     * @param resultUserField
     * @param me : Modelelement
     * @param twsumFormula
     * @return
     */
    public static String getTWSUM(final UserFieldDefinitions definitions, final UserField resultUserField, final ModelElement me, final String twsumFormula) {

        TWSumArguments args = new TWSumArguments(twsumFormula, definitions);

        //alle verbundenen Elemente mit einem aufzuteilenden Attribut holen
        List<Kante> connectionsTo = Calculator.getEdges(me, args.elementClass, args.edgeClass, args.direction);

        //Wenn keine Elemente verbunden sind
        if (connectionsTo.size() == 0) {
            return UserField.NO_ELEMENTS_CONNECTED;
        }

        //ist eine Richtung gesetzt, muss diese jetzt umgedreht werden, da von allen vorwärts verbundenen Elementen
        //jetzt alle rückwärts verbundenen Elemente gesucht werden müssen
        String backDirection = getOtherDirection(args.direction);

        //dies wird die Summe aller Anteilswerte
        BigDecimal erg = BigDecimal.ZERO;
        //dieser String bleibt in der folgenden Schleife solange null, bis irgendein Fehlerwert einer Einzelrechnung zurück kommt
        String fullErgString = null;

        PartValueSumSinglePartResults partResults = definitions.getPartValueSumSinglePartResults();

        //jetzt von allen verbundenen den jeweiligen Anteil aufsummieren
        for (Kante connectionTo : connectionsTo) {

            ModelElement other = connectionTo.getOther(me);
            UserField vgUserField = getReplacer(definitions, other, args.edgeClass, args.vgUserField);

            String ergString = getPartValueSumSingleResult(me, connectionTo, vgUserField, args, backDirection);
            //den Wert als 0 annehmen, wenn keine Verbindungen bestehen
            if (!UserField.NO_ELEMENTS_CONNECTED.equals(ergString)) {
                if (UserField.isError(ergString)) {
                    fullErgString = ergString;
                } else {
                    //wenn noch kein Zwischenergebnis einen Fehler zurück gegeben hat
                    if (fullErgString == null) {
                        //das muss auf jeden Fall als BigDecimal umwandelbar sein
                        erg = erg.add(new BigDecimal(ergString));
                    }
                }
            }
            if (resultUserField.isSimplePartValueSumFormula()) {
                partResults.add(me, resultUserField, other, ergString);
            }
        }
        if (fullErgString == null) {
            fullErgString = erg.toString();
        }
        return fullErgString;
    }

    private static String getPartValueSumSingleResult(final ModelElement me, final Kante connectionTo, final UserField vgUserField, final TWSumArguments args, final String backDirection) {

        //das Verteilungsgewicht, das an der Kante steht (erstmal Gleichverteilung (also alles 1 annhmen))
        BigDecimal normalizedVG = BigDecimal.ONE;
        //wenn mit einem explizit angegebenen Verteilungsgewicht gerechnet werden soll
        if (vgUserField != null) {
            String vgValueString = vgUserField.getValue(connectionTo);
            //wenn das Verteilungsgewicht noch nicht eingegeben wurde, kann das Ergebnis nicht berechnet werden
            if (vgValueString.equals(UserField.EMPTY_STRING)) {
                //Gesamtergebnis ist EMPTY_STRING
                return UserField.EMPTY_STRING;
            }

            //wenn sich der Verteilungsgewichtwert nicht als Zahl parsen lässt, ist das Ergebnis NUMBER_FORMAT_ERROR
            try {
                normalizedVG = new BigDecimal(vgValueString);
            } catch (Exception e) {
                return UserField.NUMBER_FORMAT_ERROR;
            }
        }

        //das andere Element der Kante holen
        ModelElement connectedElement = connectionTo.getStart();
        if (connectedElement == me) {
            connectedElement = connectionTo.getEnd();
        }
        //den aufzuteilenden eingegebenen Wert holen
        String userFieldValueToSplit = args.kzUserField.getValue(connectedElement);

        //Leere Eingaben und in Hierarchien Elemente, die keine Verbundenen
        // Elemente haben
        if (UserField.NO_ELEMENTS_CONNECTED.equals(userFieldValueToSplit)) {
            return UserField.NO_ELEMENTS_CONNECTED;
            //irgend ein anderer Fehler-Wert -> den Fehler selbst zurück geben
        } else if (UserField.isError(userFieldValueToSplit)) {
            return userFieldValueToSplit;
        }

        BigDecimal valueToSplit = null;
        //wenn sich der aufzuteilende Wert nicht als Zahl parsen lässt, ist das Ergebnis NUMBER_FORMAT_ERROR
        try {
            valueToSplit = new BigDecimal(userFieldValueToSplit);
        } catch (Exception e) {
            return UserField.NUMBER_FORMAT_ERROR;
        }

        //Alle Kanten vom Element dessen Kennzahlwert aufgeteilt werden
        // soll zu anderen Elementen holen, die von der
        //gleichen Art sind, wie das Element, das den Wert bekommen soll
        List<Kante> connectionsFrom = Calculator.getEdges(connectedElement, me.getClass(), args.edgeClass, backDirection);

        //wenn mit einer Gleichverteilung gerechnet werden soll, dann
        // braucht man die Kanten nur zu zählen
        if (vgUserField == null) {
            normalizedVG = Calculator.divide(normalizedVG, new BigDecimal(connectionsFrom.size()));
            //die eingegebenen Verteilungsgweichte müssen normiert werden
        } else {
            //das Verteilungsgewicht, das an der Kante steht (erstmal
            // gleichverteilung (also alles 1 annhmen)
            BigDecimal vgSum = BigDecimal.ZERO;
            //von all diesen Kanten die Verteilungsgewichte aufsummieren
            // (es gibt mind. eine solche Kante = die der Hinrichtung)
            for (int j = 0; j < connectionsFrom.size(); j++) {
                Kante connectionFrom = connectionsFrom.get(j);
                //den Wert des VG der aktuellen Kante holen
                String vgValueString = vgUserField.getValue(connectionFrom);
                //wenn für eine Kante kein Verteilungsgewicht eingegeben
                // wurde, wird es als 0 angenommen
                if (vgValueString.equals(UserField.EMPTY_STRING)) {
                    continue;
                }
                //den gefundenen Wert ausummieren
                //wenn sich der gefundenen Wert nicht als Zahl parsen lässt, ist das Ergebnis NUMBER_FORMAT_ERROR
                try {
                    BigDecimal vgValue = new BigDecimal(vgValueString.toString());
                    vgSum = vgSum.add(vgValue);
                } catch (Exception e) {
                    return UserField.NUMBER_FORMAT_ERROR;
                }
            }
            if (vgSum.compareTo(BigDecimal.ZERO) != 0) {
                normalizedVG = Calculator.divide(normalizedVG, vgSum);
            }
        }
        BigDecimal erg = valueToSplit.multiply(normalizedVG);
        return erg.toString();
    }

    private static UserField getReplacer(final UserFieldDefinitions definitions, final ModelElement me, final Class<? extends Kante> edgeClass, final UserField userFieldToReplace) {
        UserField field = null;
        WeightReplacer replacer = definitions.getWeightReplacer();
        String modelElementHash = me.getHashString();
        String fieldHash = null;
        if (userFieldToReplace == null) {
            fieldHash = replacer.getUniformDistributionReplacement(modelElementHash, edgeClass);
        } else {
            String userFieldHashToReplace = userFieldToReplace.getHashCode();
            fieldHash = replacer.getReplacement(modelElementHash, userFieldHashToReplace);
        }
        if (fieldHash == null) {
            field = userFieldToReplace;
        } else {
            field = definitions.getUserField(fieldHash);
        }
        return field;
    }

    /**
     * Gibt die Gegenrichtung zurück, wenn eine gültige Richtung als String übergeben wurde.
     *
     * @param direction
     * @return
     */
    private static String getOtherDirection(final String direction) {
        String backDirection = null;
        if (direction != null) {
            if (UserField.DIRECTION_FROM_WHOLE_TO_PART.equals(direction)) {
                backDirection = UserField.DIRECTION_FROM_PART_TO_WHOLE;
            } else {
                backDirection = UserField.DIRECTION_FROM_WHOLE_TO_PART;
            }
        }
        return backDirection;
    }

    /**
     * Liefert
     *
     * @param userField
     * @return
     */
    public static TWSumArguments getTWSumArguments(final UserField userField) {
        if (!CostingUtil.isSimpleFractionValueSumFormula(userField)) {
            return null;
        }
        String formula = userField.getFormula();
        UserFieldDefinitions definitions = userField.getDefinitions();
        TWSumArguments arguments = new TWSumArguments(formula, definitions);
        return arguments;
    }

    public static class TWSumArguments {

        public final Class<? extends Kante> edgeClass;

        public final UserField kzUserField;

        public final Class<? extends ModelElement> elementClass;

        //Das UserField, das das Verteilungsgewicht kennzeichnet.
        public final UserField vgUserField;

        // Die angegebene Richtung
        public final String direction;

        /**
         * @param formula
         *            Die Formel kann entweder die ganze Formel oder nur noch die Argumente sein
         * @param definitions
         */
        private TWSumArguments(final String formula, final UserFieldDefinitions definitions) {
            StringTokenizer st = new StringTokenizer(formula, " ()|");

            String token = st.nextToken();
            if (UserField.ACCOUNTING_FUNCTION_TWSUM.equals(token)) {
                token = st.nextToken();
            }
            //Der erste Token ist der Name der Assoziation, die das VG beherbegrt
            // -> hole die Kantenklasse
            edgeClass = ModelConstants.getClassForName(token).asSubclass(Kante.class);

            String ufHash = st.nextToken();

            //Das UserField, das das zu verrechnende Attribut kennzeichnet.
            kzUserField = definitions.getUserField(ufHash);

            //die TargetClass des UserFields als ModelElement-Unterklasse
            elementClass = kzUserField.getTargetClass().asSubclass(ModelElement.class);

            //Das UserField, das das Verteilungsgewicht kennzeichnet.
            UserField vgUserField = null;

            // Die angegebene Richtung
            String direction = null;

            //wenn es mind. 3 Argumente gibt
            if (st.hasMoreTokens()) {
                String nextToken = st.nextToken();
                //wenn der nächste Token das Verteilungsgewicht angibt
                if (nextToken.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                    vgUserField = definitions.getUserField(nextToken);
                    //wenn es nicht das VG ist, kann es nur noch die Richtung sein
                } else {
                    direction = nextToken;
                }
            }

            //die Funktion hat 4 Argumente -> das 4. ist immer die Richtung der
            // Part-Of-Verrechnung
            if (st.hasMoreTokens()) {
                direction = st.nextToken();
            }

            this.vgUserField = vgUserField;
            this.direction = direction;

        }

    }

}
