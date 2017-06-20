/*
 * Created on 05.11.2007
 */

package de.imise.tool3lgm.graphtools.userfield.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;

/**
 * Die Klasse <code>Calculator</code> beinhaltet alle Methoden für die Berechnung von Kennzahlen.
 *
 * @author hboehme, AXS
 * @created 05.11.2007
 */
public class Calculator {

    public static final String OPEN_BRACKET = "(";

    public static final String CLOSE_BRACKET = ")";

    public static final String OPERAND_DELIMITER = "|";

    public static final String OPERATOR_PLUS = "+";

    public static final String OPERATOR_MINUS = "-";

    public static final String OPERATOR_MULT = "*";

    public static final String OPERATOR_DIV = "/";

    public static final String ZERO = "0";

    public static final String ONE = "1";

    public static final String WHITESPACE = " ";

    private static final String OPERATOR_SIGNS = OPERATOR_PLUS + OPERATOR_MINUS + OPERATOR_MULT + OPERATOR_DIV;

    public static final String _ADDITIONAL_FUNCTION_SIGNS = OPEN_BRACKET + CLOSE_BRACKET + OPERAND_DELIMITER;

    public static final String ALL_IN_FUNCTION_SIGNS = WHITESPACE + OPEN_BRACKET + CLOSE_BRACKET + OPERAND_DELIMITER + OPERATOR_PLUS + OPERATOR_MINUS + OPERATOR_MULT + OPERATOR_DIV;

    /**
     * gibt an, auf wieviele Nachkommastellen bei der Berechnung mit BigDecimals gerechnet werden sollen.
     */
    private static final int CALCULATING_DECIMAL_PLACES_COUNT = 30;

    /**
     * Gibt an, auf wieviele Nachkommastellen die BigDecimals-Ergebnisse abgeschnitten werden sollen.
     * Das beseitigt offensichtliche Rundungsfehler, bei der Berechnung mit 30 Nachkmmastellen
     * Rundungsfehler ungefähr bis zur 20 Stelle durchschlagen. Also wird der Rest abgeschnitten.
     */
    private static final int RESULT_DECIMAL_PLACES_COUNT = 20;

    private static final int DECIMAL_ROUND_MODE = BigDecimal.ROUND_UP;

    public static final BigDecimal divide(final BigDecimal dividend, final BigDecimal divisor) {
        //Das Teilen von BigDecimal erfordert die Angabe der Nachkommastellen und des Rundungsverhaltens.
        return dividend.divide(divisor, CALCULATING_DECIMAL_PLACES_COUNT, DECIMAL_ROUND_MODE);
    }

    /**
     *
     */
    private UserFieldDefinitions definitions;

    /**
     *
     */
    public Calculator(final UserFieldDefinitions definitions) {
        super();
        this.definitions = definitions;
    }

    public String calculate(final UserField userField, final UserFieldTarget userFieldTarget) {
        String result = calculateInternal(userField, userFieldTarget);
        //        System.err.print(result);
        //        System.err.print(" -> ");
        if (!UserField.isCriticalError(result)) {
            int pointIndex = result.indexOf('.');
            if (pointIndex + CALCULATING_DECIMAL_PLACES_COUNT <= result.length()) {
                result = result.substring(0, pointIndex + RESULT_DECIMAL_PLACES_COUNT);
            }
        }
        //        System.err.println(result);
        return result;
    }

    /**
     * Errechnet zu einer übergebenen Formel in Infix-Notation das Ergebnis. TODO:Dazu wird die Formel jedes Mal erst in Postfix-Notation umgewandelt,
     * was noch erhebliches Optimierungspotenzial in sich birgt ;) dazu wird der übergebene String zuerst in mittels Stringtokenizers zerlegt und in
     * einen Stack kopiert. Es wird eine spiegelverkerhrte Kopie des Stacks angelegt, die nicht mehr die Referenzen auf die Werte in Form von
     * <code>UserField</code> s enthält sonderen die konkreten Werte! Der Stack ist spiegelverkerhrt damit auch korrekt gerechnet werden kann. Zum
     * Rechnen wird ein weiterer Hilfs-Stack initialisiert, der solange Operanden aufnimmt, bis im HauptStack ein Operator gepoppt wird. Dieser
     * Operator wird mit den letzten zwei Operanden des Hilfsstacks verrechnent. Das Ergebnis wird wieder auf den Hautstack gelegt. Das geschieht
     * iterativ so lange, bis der HautptStack keine Operatoren mehr beinhaltet.
     *
     * @param userField : das zu berechnende UserField
     * @param data.me : Das konkrete ModelElement, für das die KF ausgewertet wird.
     * @return Das Ergebnis als String
     */

    private String calculateInternal(final UserField userField, final UserFieldTarget userFieldTarget) {
        //Modellvariablen berechnen
        if (userField.isGlobalOrFormat()) {
            //Modellvariablen berechnen (sowas gibts im Moment noch gar nicht,
            //aber wenn, dann muss das hier passieren)
            return "";
        }

        ModelElement me = (ModelElement) userFieldTarget;

        String formula = userField.getFormula();
        // Wenn für eine userField, dass eine Kennzahlformel ist, keine Formel definiert ist
        if (formula == null) {
            return UserField.EMPTY_STRING;
        }

        //Prüfen, ob die zu berechnende Formel korrekt ist.
        if (!CostingUtil.isFormulaValid(formula)) {
            // Hier müsste man noch eine Message bringen und dem User erklären, dass ein Fehler in einer Formel ist.
            //JOptionPane.showMessageDialog(Tool3lgm.tool, Tool3lgmConstants.getErrString("syntax_error_in_formula"));
            return UserField.EMPTY_STRING;
        }

        StringBuilder infix = new StringBuilder(formula);

        //Indikatorformeln haben keine weiteren Verrechnungsfunktionen in sich
        // und fangen immer mit dem Funktionsnamen an
        if (infix.indexOf(UserField.ACCOUNTING_FUNCTION_INDI) == 1) {
            return getIndi(me, infix.toString());
        }

        //      Alle Teilwertsummen auflösen (das muss vor den Summen apssieren, da
        //      in TWSUM auch SUM steckt)
        String erg = replaceInfixString(infix, userField, me, UserField.ACCOUNTING_FUNCTION_TWSUM);
        if (UserField.isCriticalError(erg)) {
            return erg;
        }

        // Alle Summen
        erg = replaceInfixString(infix, userField, me, UserField.ACCOUNTING_FUNCTION_SUM);
        if (UserField.isCriticalError(erg)) {
            return erg;
        }

        // Alle Multiplikationen
        erg = replaceInfixString(infix, userField, me, UserField.ACCOUNTING_FUNCTION_MULT);
        if (UserField.isCriticalError(erg)) {
            return erg;
        }

        // Alle Minima
        erg = replaceInfixString(infix, userField, me, UserField.ACCOUNTING_FUNCTION_MIN);
        if (UserField.isCriticalError(erg)) {
            return erg;
        }

        // Alle Maxima
        erg = replaceInfixString(infix, userField, me, UserField.ACCOUNTING_FUNCTION_MAX);
        if (UserField.isCriticalError(erg)) {
            return erg;
        }

        // Alle Referenzen
        erg = replaceInfixString(infix, userField, me, UserField.ACCOUNTING_FUNCTION_REF);
        if (UserField.isCriticalError(erg)) {
            return erg;
        }

        // Alle Durchschnitte
        erg = replaceInfixString(infix, userField, me, UserField.ACCOUNTING_FUNCTION_AVG);

        String infixString = infix.toString();
        //Wenn der Infix nur aus irgendeinem Fehlerwert besteht -> raus mit dem
        // Fehlerwert
        if (UserField.isError(infixString)) {
            return infixString;
        }

        String postfix = getPostFix(infixString);

        StringTokenizer st = new StringTokenizer(postfix);
        if (st.countTokens() == 1) {
            String tmp_String = st.nextToken();
            //In Funktionen, die nur auf andere UserFields referenzieren,
            // beginnt der tmp_String
            //immer mit einem UserField-Hash
            // Wenn dieser Fall eintritt, beinhaltet der Postfix-String und
            // somit der stringTokenizer nur einen token.
            // Dieser String ist ein userFieldHash eines userFields, das dem
            // selben Element zugeordnet ist.
            if (tmp_String.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                return getValueOfReferencedUserField(tmp_String, me);
            }
            return postfix;
        }

        String operator = "";
        String operand1 = "";
        String operand2 = "";
        String result = null;

        // Der FormelStack beinhaltet alle Operanden und Operatoren.
        Stack<String> formelStack = new Stack<String>();
        Stack<String> tmp_stack = new Stack<String>();
        // hier wird aus dem StringTokenizer ein Stack gemacht, um auf die Elemente besser zugreifen zu können.

        while (st.hasMoreTokens()) {
            formelStack.push(st.nextToken());
        }

        // Hier werden aus den userFieldHash - Angaben konkrete Werte geholt.
        // Iterativ: Operatoren und userFieldHashes werden vom Stack geholt werden.
        // Die Hashes werden in konkrete Werte gewandetlt. Operatoren und operanden werden auf den Hilfsstack gelegt.
        // Der Hilfsstack, der nur noch konkrete Werte und Operatoren enthält ist der neue Hauptstack.
        while (!formelStack.empty()) {
            String formulaSubString = formelStack.pop().toString();
            if (!isOperator(formulaSubString)) {
                if (formulaSubString.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                    //Das userfield wird gebildet, weil es sein kann, dass der
                    // Wert von einer Modellvariable geholt wird,
                    // der sich nicht mit
                    // "definitions.getUserField(tmp_str).getValue(me)" holen
                    // lässt.

                    UserField tmp_userField = definitions.getUserField(formulaSubString);

                    //Manchmal kann es passieren, dass das userField null ist. Der Grund ist noch unbekannt.
                    /*
                     * if(tmp_userField==null){ System.err.println("'"+formulaSubString+"'"); System.err.println("------------------");
                     * System.err.println(definitions); return UserField.EMPTY_STRING; } ///// TESTAUSGABE /// //
                     * System.err.println("'"+formulaSubString+"'"); // System.err.println("------------------"); // System.err.println(definitions);
                     * // // return UserField.EMPTY_STRING; /// TESTAUSGABE ENDE ///
                     */
                    String ufValue = "";
                    if (tmp_userField != null && tmp_userField.isGlobalOrFormat()) {
                        ufValue = definitions.getCollection().getUserFieldInputValue(tmp_userField);
                    } else {
                        ufValue = tmp_userField.getValue(me);

                    }

                    // Fehler werden durchgereicht und nicht weiter gerechnet.
                    if (UserField.isCriticalError(ufValue)) {
                        return ufValue;
                    }

                    tmp_stack.push(ufValue);
                } else if (isConstantValue(formulaSubString)) {
                    tmp_stack.push(formulaSubString);
                } else {
                    tmp_stack.push(formulaSubString);
                }
            } else {
                tmp_stack.push(formulaSubString);
            }
        }

        formelStack = tmp_stack;

        // In einer Schleife: In den operandenStack werden nur die Operanden aus dem FormelStack kopiert, bis der erste Operator kommt. Dann werden die letzten zwei Operanden aus dem operandenStack mit dem nun folgenen Operator aus dem formelStack verrechnet.
        // Das Ergebnis kommt wieder auf den formelStack und die Schleife beginnt von vorn.
        Stack<String> operandenStack = new Stack<String>();
        // Hier erfolgt das Berechnen einer Operation.

        while (!formelStack.empty()) {
            String tmp_str = formelStack.pop().toString();

            if (!isOperator(tmp_str)) {
                operandenStack.push(tmp_str);
            } else {
                operator = tmp_str;
                operand2 = operandenStack.pop().toString().replace(",", ".");
                operand1 = operandenStack.pop().toString().replace(",", ".");
                result = getResult(operand1, operand2, operator);
                if (result != null) {
                    if (!formelStack.empty()) {
                        formelStack.push(result);
                    } else {
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static boolean isConstantValue(final String formulaSubString) {
        if (!formulaSubString.isEmpty()) {
            char c = formulaSubString.charAt(0);
            return '0' <= c && c <= '9' && formulaSubString.indexOf(WHITESPACE) < 0;
        }
        return false;
    }

    public static boolean isOperator(final String s) {
        return OPERATOR_SIGNS.contains(s);
    }

    /**
     * Ersetzt im übergebenen StringBuilder <code>infix</code> die Formel der Verrechnungsfunktion durch ihren Wert.
     *
     * @param infix Der StringBuilder, dessen Formelwerte ersetzt werden müssen
     * @param userField Das <code>userField</code>, dessen Wert geholt werden soll.
     * @param me Das ModelElement, für das der <code>userField</code>-Wert geholt werden soll.
     * @param accountingFunction Der String der Verrechnungsfunktion, für die die Formeln durch ihre Werte ersetzt werden sollen.
     * @return Gibt bei erfolgreichem Ersetzen nichts zurück. im FehlerFall wird der entsprechende Errorstring zurückgegeben.
     */
    private String replaceInfixString(final StringBuilder infix, final UserField userField, final ModelElement me, final String accountingFunction) {

        int startIndexOfFunction = infix.indexOf(accountingFunction);
        while (startIndexOfFunction >= 0) {
            int firstBracketIndex = infix.indexOf(OPEN_BRACKET, startIndexOfFunction);
            int secondBracketIndex = infix.indexOf(CLOSE_BRACKET, firstBracketIndex);
            String arguments = infix.substring(firstBracketIndex + 1, secondBracketIndex);

            String value = "";

            if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_TWSUM)) {
                //jetzt berechnen der TeilwertSumme anstoßen
                value = PartValueSumFunction.getTWSUM(definitions, userField, me, arguments);
            } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_SUM)) {
                //jetzt berechnen der Summe anstoßen
                value = getSUM(userField, me, arguments, UserField.ACCOUNTING_FUNCTION_SUM);
            } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_MULT)) {
                //jetzt berechnen des Produktes anstoßen
                value = getMULT(userField, me, arguments, UserField.ACCOUNTING_FUNCTION_MULT);
            } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_MIN)) {
                //jetzt Suches des Minimums anstoßen
                value = getMIN(userField, me, arguments);
            } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_MAX)) {
                //jetzt Suchen des Maximums anstoßen
                value = getMAX(userField, me, arguments);
            } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_REF)) {
                //jetzt das Heraussuchen dees Referenzierten wertes anstoßen.
                value = getREF(userField, me, arguments);
            } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_AVG)) {
                //jetzt berechnen des Durchschnittes anstoßen
                value = getAvg(userField, me, arguments);
            }
            if (UserField.isCriticalError(value)) {
                return value;
            }
            //die Funktion im Formel-StringBuilder durch den Wert ersetzen
            infix.replace(startIndexOfFunction, secondBracketIndex + 1, value);
            //ab dem eingefügten Wert nach der nächsten Funktion suchen
            startIndexOfFunction = infix.indexOf(accountingFunction, startIndexOfFunction);
        }
        return "";
    }

    /**
     * Berechnet das Ergebnis einer Atomformel, wenn das geht. Ansonsten wird ein Fehler zurück gegeben. <br>
     * Ist irgendeiner der beiden Operanden ein Fehler-String, wird dieser Fehler-String als Ergebnis zurück gegeben. Ausnahmen: Bei Additionen bei
     * denen ein Operand <code>UserField.NO_ELEMENTS_CONNECTED</code> ist, wird nur der jeweils andere Operand zurückgegeben. Bei Subtraktionen, bei
     * denen der zweite Operand <code>UserField.NO_ELEMENTS_CONNECTED</code> ist, wird der erste Operand zurück gegeben. Ist bei Subtraktionen der
     * erste Operand <code>UserField.NO_ELEMENTS_CONNECTED</code>, dann wird der zweite Operand mal -1 zurück gegeben. Wenn in einer Atom-Formel kein
     * Fehler enthalten ist, wird die Berechnung durchgeführt und das Ergebnis zurückgeliefert. Spezielle Rechenregeln bei der Division. NOC =
     * <code>UserField.NO_ELEMENTS_CONNECTED</code>, ES = UserField.EMPTY_STRING, 7 = beliebige Zahl NOC / NOC = return NOC NOC / ES = return NOC NOC
     * / 7 = return NOC ES / NOC = return NOC ES / ES = return ES ES / 7 = return ES 7 / NOC = return NOC 7 / ES = return ES 7 / 7 = rechne einfach
     * damit
     *
     * @param operand1 Der erste Operand der Atom-Formel
     * @param operand2 Der zweite Operand der Atom-Formel
     * @param operator Der Operator der Atom-Formel
     * @return Wenn in einer Atom-Formel kein Fehler enthalten ist, wird die Berechnung durchgeführt und das Ergebnis zurückgeliefert.
     */
    private static final String getResult(String operand1, String operand2, final String operator) {

        if (UserField.isCriticalError(operand1)) {
            return operand1;
        }
        if (UserField.isCriticalError(operand2)) {
            return operand2;
        }

        if (UserField.NO_ELEMENTS_CONNECTED.equals(operand1) && UserField.NO_ELEMENTS_CONNECTED.equals(operand2)) {
            return UserField.NO_ELEMENTS_CONNECTED;
        }
        //die Reihenfolge in der die Prüfunge hier stehen ist wichtig!
        if (UserField.NO_ELEMENTS_CONNECTED.equals(operand1)) {
            if (OPERATOR_MULT.equals(operator) || OPERATOR_DIV.equals(operator)) {
                return UserField.NO_ELEMENTS_CONNECTED;
            }
            operand1 = ZERO;
        }
        if (UserField.NO_ELEMENTS_CONNECTED.equals(operand2)) {
            if (OPERATOR_MULT.equals(operator) || OPERATOR_DIV.equals(operator)) {
                return UserField.NO_ELEMENTS_CONNECTED;
            }
            operand2 = ZERO;
        }
        if (UserField.EMPTY_STRING.equals(operand1)) {
            return UserField.EMPTY_STRING;
        }
        if (UserField.EMPTY_STRING.equals(operand2)) {
            return UserField.EMPTY_STRING;
        }

        //      System.err.println("\""+operand1+"\"\t" + "\""+operator+"\"\t\""+operand2+"\"");

        //Falls die eingegebenen Werte irgendweswegen nicht als Zahl erkannt werden -> NUMBER_FORMAT_ERROR
        BigDecimal bd1 = null, bd2 = null;
        try {
            bd1 = new BigDecimal(operand1);
            bd2 = new BigDecimal(operand2);
        } catch (Exception e) {
            return UserField.NUMBER_FORMAT_ERROR;
        }

        //Plus
        if (OPERATOR_PLUS.equals(operator)) {
            return bd1.add(bd2).toString();
            //Minus
        } else if (OPERATOR_MINUS.equals(operator)) {
            return bd1.subtract(bd2).toString();
            //Mal
        } else if (OPERATOR_MULT.equals(operator)) {
            return bd1.multiply(bd2).toString();
            //Durch
        } else if (OPERATOR_DIV.equals(operator)) {
            if (bd2.compareTo(BigDecimal.ZERO) == 0) {
                return UserField.ERROR_DIVIDE_BY_ZERO;
            }

            //Das Teilen von BigDecimal erfordert die Angabe der
            // Nachkommastellen
            //und des Rundungsverhaltens.
            BigDecimal quotient = divide(bd1, bd2);
            return quotient.toString();
        }
        return null;
    }

    /**
     * Gibt den wert des referenzierten <code>UserField</code> zurück. Die funktion geht davon aus, dass alle übergebenen Parameter korrekt sind.
     *
     * @param resultUserField das konkrete <code>UserField</code>, auf dessen Wert referenziert wird.
     * @param me Für diese Kante ist die Kennzahlformel definiert. Dieses <code>UserField</code> zeigt auf ein UserField welches an einer
     *            Elementklasse
     * @param refFormula
     * @return
     */
    private String getREF(final UserField resultUserField, final ModelElement me, final String refFormula) {
        //TODO:AXS:prüfen, ob hier Klassenvergleiche auf Zuwesiungskompatibilität oder auf Identität zielen sollten
        StringTokenizer st = new StringTokenizer(refFormula, " (|)");

        //nächster Token ist der Name der Elementclass, deren
        // <code>UserField<code>swert geholt wird
        Class<?> elementClass = ModelConstants.getClassForName(st.nextToken());

        //Der nächste String ist der Hashcode des <code>UserFields</code>,
        // dessen Wert geholt werden soll
        UserField userField = definitions.getUserField(st.nextToken());

        String direction = null;
        if (st.hasMoreTokens()) {
            direction = st.nextToken();
        }

        Kante kante = (Kante) me;
        BigDecimal refVg = BigDecimal.ZERO;
        ModelElement elementWithUserField = null;
        // Wenn es sich um eine Teil-von-Beziehung handelt
        if (PartOfBeziehung.class.isAssignableFrom(kante.getClass()) && direction != null) {
            if (UserField.DIRECTION_FROM_WHOLE_TO_PART.equals(direction)) {
                elementWithUserField = kante.getEnd();
            } else if (UserField.DIRECTION_FROM_PART_TO_WHOLE.equals(direction)) {
                elementWithUserField = kante.getStart();
            }

        } else { // Wenn es sich um keine Teil-Von-Beziehung handelt
            elementWithUserField = kante.getStart();
            if (elementWithUserField.getClass() != elementClass) {
                elementWithUserField = kante.getEnd();
            }
        }
        if (elementWithUserField != null && elementWithUserField.getClass() == elementClass) {
            String value = userField.getValue(elementWithUserField);
            //wenn der referenzierte Wert bereits ein Fehler ist, dann wird dieser Fehler zurück gegeben
            if (UserField.isError(value)) {
                return value;
            }
            //zur Sicherheit alle BigDecimal-Umwandlungen mit einem try-catch ausführen - egal, ob vorher angeblich
            //schon alle Fehler ausgeschlossen wurden. Denn wenn hier an irgend einer Stelle der Berechnung eine
            //exception fliegt, sind einige Werte neu und andere veraltet.
            try {
                BigDecimal numberValue = new BigDecimal(value);
                refVg = refVg.add(numberValue);
            } catch (Exception e) {
                return UserField.NUMBER_FORMAT_ERROR;
            }
        }
        return refVg.toString();
    }

    /**
     * Liefert alle Kanten
     *
     * @param me
     * @param elemClass
     * @param edgeClass
     * @param direction
     * @return
     */
    public static final ArrayList<Kante> getEdges(final ModelElement me, final Class<? extends ModelElement> elemClass, final Class<? extends Kante> edgeClass, final String direction) {
        ArrayList<Kante> kanten = null;
        //Alle Kanten mit der richtigen Richtung holen
        if (UserField.DIRECTION_FROM_WHOLE_TO_PART.equals(direction)) {
            kanten = me.getEdgesTo(elemClass, edgeClass);
        } else if (UserField.DIRECTION_FROM_PART_TO_WHOLE.equals(direction)) {
            kanten = me.getEdgesFrom(elemClass, edgeClass);
        } else {
            kanten = me.getEdgesWith(elemClass, edgeClass);
        }
        return kanten;
    }

    /**
     * Errechnet das Ergebnis der Verrechungsfunktion SUM oder MULT. Die Funktion geht davon aus, dass alle übergebenen Parameter korrekt sind.
     *
     * @param resultUserField
     * @param me das konkrete <code>ModelElement</code>, für das die Verrechnungsfunktion aufgelöst werden soll.
     * @param formula Die Formel (in INFIX-Notation)
     * @return Ergebnis der Verrechungsfunktion SUM
     */
    private String getResultOfVF(final UserField resultUserField, final ModelElement me, final String formula, final String accountingFunction) {
        //      ArrayList elements = gd.getAllModelElements(me.getClass(), true);
        StringTokenizer st = new StringTokenizer(formula, " (|)");

        //nächster Token ist der Name der Kante über die UserFields der
        // verbundenen Elemente aufsummiert werden sollen
        Class<? extends Kante> edgeClass = ModelConstants.getClassForName(st.nextToken()).asSubclass(Kante.class);

        //nächster Token ist der HashString des UserFields das aufsummiert
        // werden soll -> hole dafür das UserField aus den Definitions
        UserField userField = definitions.getUserField(st.nextToken());

        String direction = null;
        if (st.hasMoreTokens()) {
            direction = st.nextToken();
        }
        Class<? extends ModelElement> conntectedElementClass = userField.getTargetClass().asSubclass(ModelElement.class);

        ArrayList<Kante> kanten = getEdges(me, conntectedElementClass, edgeClass, direction);

        //Keine Verbindung zu anderen Elementen
        if (kanten.size() == 0) {
            return UserField.NO_ELEMENTS_CONNECTED;
        }

        String result = ZERO;
        if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_MULT)) {
            result = ONE;
        }

        //für jede dieser Kanten
        for (int j = 0; j < kanten.size(); j++) {
            Kante k = kanten.get(j);
            //das verbundene Element holen
            ModelElement connectedElement = k.getStart();
            if (me == connectedElement) {
                connectedElement = k.getEnd();
            }
            //den Eingabewert des aufzusummierenden Feldes holen
            String value = userField.getValue(connectedElement);
            if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_SUM)) {
                result = getResult(result, value, OPERATOR_PLUS);
            } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_MULT)) {
                result = getResult(result, value, OPERATOR_MULT);
            }
        }
        return result;

    }

    /**
     * Errechnet das Ergebnis der Verrechungsfunktion SUM. Die Funktion geht davon aus, dass alle übergebenen Parameter korrekt sind.
     *
     * @param resultUserField
     * @param me das konkrete <code>ModelElement</code>, für das die Verrechnungsfunktion aufgelöst werden soll.
     * @param formula Die Formel (in INFIX-Notation)
     * @return Ergebnis der Verrechungsfunktion SUM
     */
    private String getSUM(final UserField resultUserField, final ModelElement me, final String formula, final String accountingFunction) {
        return getResultOfVF(resultUserField, me, formula, accountingFunction);
    }

    /**
     * Errechnet das Ergebnis der Verrechungsfunktion MULT. Die Funktion geht davon aus, dass alle übergebenen Parameter korrekt sind.
     *
     * @param resultUserField
     * @param me das konkrete <code>ModelElement</code>, für das die Verrechnungsfunktion aufgelöst werden soll.
     * @param formula Die Formel (in INFIX-Notation)
     * @return Ergebnis der Verrechungsfunktion MULT
     */
    private String getMULT(final UserField resultUserField, final ModelElement me, final String formula, final String accountingFunction) {
        return getResultOfVF(resultUserField, me, formula, accountingFunction);
    }

    /**
     * Sucht den kleinsten Wert des <code>UserField</code>s aller verbunden Elemente und gibt ihn zurück.
     *
     * @param resultUserField
     * @param me
     * @param minFormula
     * @return Den kleinsten Wert.
     */
    private String getMIN(final UserField resultUserField, final ModelElement me, final String minFormula) {
        return getMINMAX(resultUserField, me, minFormula, UserField.ACCOUNTING_FUNCTION_MIN);

    }

    /**
     * Sucht den größen Wert des <code>UserField</code>s aller verbunden Elemente und gibt ihn zurück.
     *
     * @param resultUserField
     * @param me
     * @param maxFormula
     * @return Den größten Wert.
     */
    private String getMAX(final UserField resultUserField, final ModelElement me, final String maxFormula) {
        return getMINMAX(resultUserField, me, maxFormula, UserField.ACCOUNTING_FUNCTION_MAX);

    }

    /**
     * Errechnet das Ergebnis der Verrechungsfunktion MIN bzw MAX. Die Funktion geht davon aus, dass alle übergebenen Parameter korrekt sind.
     *
     * @param resultUserField
     * @param me das konkrete <code>ModelElement</code>, für das die Verrechnungsfunktion aufgelöst werden soll.
     * @param minMaxFormula Die Formel (in INFIX-Notation)
     * @return Ergebnis der Verrechungsfunktion MIN bzw MAX
     */
    private String getMINMAX(final UserField resultUserField, final ModelElement me, final String minMaxFormula, final String accountingFunction) {
        //  ArrayList elements = gd.getAllModelElements(me.getClass(), true);
        StringTokenizer st = new StringTokenizer(minMaxFormula, " (|)");

        //nächster Token ist der Name der Kante über die UserFields der
        // verbundenen Elemente aufsummiert werden sollen
        Class<? extends Kante> edgeClass = ModelConstants.getClassForName(st.nextToken()).asSubclass(Kante.class);

        //nächster Token ist der HashString des UserFields das aufsummiert
        // werden soll -> hole dafür das UserField aus den Definitions
        UserField userField = definitions.getUserField(st.nextToken());

        String direction = null;
        if (st.hasMoreTokens()) {
            direction = st.nextToken();
        }

        Class<? extends ModelElement> conntectedElementClass = userField.getTargetClass().asSubclass(ModelElement.class);

        //Alle Kanten mit der richtigen Richtung holen
        ArrayList<Kante> kanten = getEdges(me, conntectedElementClass, edgeClass, direction);

        //Keine Verbindung zu anderen Elementen
        if (kanten.size() == 0) {
            return UserField.NO_ELEMENTS_CONNECTED;
        }

        String result = ZERO;
        //für jede dieser Kanten
        for (int j = 0; j < kanten.size(); j++) {
            Kante k = kanten.get(j);
            //das verbundene Element holen
            ModelElement connectedElement = k.getStart();
            if (me == connectedElement) {
                connectedElement = k.getEnd();
            }
            //den Eingabewert des aufzusummierenden Feldes holen
            String value = userField.getValue(connectedElement);

            if (UserField.isIgnoreableError(value)) {
                return value;
            }
            if (j == 0) {
                result = value;
            } else {
                BigDecimal valueOne = null;
                BigDecimal valueTwo = null;
                try {
                    valueOne = new BigDecimal(result);
                    valueTwo = new BigDecimal(value);
                } catch (Exception e) {
                    return UserField.NUMBER_FORMAT_ERROR;
                }
                if (valueTwo.compareTo(valueOne) == -1) {
                    if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_MIN)) {
                        result = value;
                    }
                } else if (accountingFunction.equals(UserField.ACCOUNTING_FUNCTION_MAX)) {
                    result = value;
                }

            }
        }
        return result;
    }

    /**
     * Errechnet das Ergebnis der Verrechnungsfunktion Mittelwert.
     *
     * @param resultUserField
     * @param elementClass
     * @param userField
     * @return gibt das Ergebnis der Berechnung zurück.
     */
    private String getAvg(final UserField resultUserField, final ModelElement me, final String avgFormula) {
        String sum = getSUM(resultUserField, me, avgFormula, UserField.ACCOUNTING_FUNCTION_SUM);

        if (UserField.isError(sum)) {
            return sum;
        }

        StringTokenizer st = new StringTokenizer(avgFormula, " (|)");

        //nächster Token ist der Name der Kante über die UserFields der
        // verbundenen Elemente aufsummiert werden sollen
        Class<? extends Kante> edgeClass = ModelConstants.getClassForName(st.nextToken()).asSubclass(Kante.class);

        me.countConnections(edgeClass);
        BigDecimal sumNum = null;
        try {
            sumNum = new BigDecimal(sum);
        } catch (Exception e) {
            return UserField.NUMBER_FORMAT_ERROR;
        }
        if (me.countConnections(edgeClass) == 0) {
            JOptionPane.showMessageDialog(null, Tool3lgmConstants.getResString("fehler") + Tool3lgmConstants.getErrString("divide_zero"));
        }
        BigDecimal erg = divide(sumNum, new BigDecimal(me.countConnections(edgeClass)));
        return erg.toString();
    }

    /**
     * Berechnet eine Indikatorfunktion für das übergebene <code>UserFieldTarget</code>
     *
     * @param indicatorFormulaString Formel einer Kennzahl der eine Indikatorfunktion beschreibt
     * @param target
     * @return gibt den Indikatorwert zurück.
     */
    private String getIndi(final UserFieldTarget target, final String indicatorFormula) {
        StringTokenizer st = new StringTokenizer(indicatorFormula, "| ()");
        Stack<String> stack = new Stack<String>();
        int stacksize = 0;
        while (st.hasMoreElements()) {
            //Falls der Wert ein Komma enthält, muss es durch einen . ersetzt
            // werden,
            //da die Konvertierung zu BigDecimal sonst fehlschlägt.
            String tmp_String = st.nextElement().toString();
            if (tmp_String.contains(",")) {
                tmp_String = tmp_String.replace(",", ".");
            }

            stack.push(tmp_String);
            stacksize++;
        }
        //erstes und zweites Element wegschmeißen
        Stack<String> tmp_stack = new Stack<String>();
        while (!stack.empty()) {
            tmp_stack.push(stack.pop());
        }
        stack = tmp_stack;
        stack.pop();
        stacksize--;

        String userFieldhash = stack.pop().toString();
        stacksize--;

        //Der aktuelle Wert des UserFields, das indiziert werden soll.
        String tmp_value = definitions.getUserField(userFieldhash).getValue(target);

        if (UserField.isError(tmp_value)) {
            return tmp_value;
        }

        //alle new BigDecimal()-Aufrufe müssen mit try-catch unmantelt werden
        try {
            BigDecimal value = new BigDecimal(tmp_value);
            int anzWerte = stacksize;
            int indicator = 0;
            BigDecimal valueOne = new BigDecimal(stack.pop().toString());
            BigDecimal valueTwo = BigDecimal.ZERO;
            for (int i = 1; i < anzWerte; i++) {
                if (value.compareTo(valueOne) > 0) {
                    valueTwo = new BigDecimal(stack.pop().toString());
                    if (value.compareTo(valueTwo) <= 0) {
                        indicator = i;
                    }
                    valueOne = valueTwo;
                }
                if (i == anzWerte - 1 && indicator == 0) {
                    //Dieser Fall tritt ein, wenn der Wert größer ist als die größte untere Grenze.
                    indicator = i;
                }
            }
            // Indikationsbereich: " + indicator + " von " + (anzWerte - 1);
            //String erg = "Indi " + value + " | " + indicator + " | " + (anzWerte - 1);
            String erg = Integer.toString(indicator);
            return erg;
        } catch (Exception e) {
            return UserField.NUMBER_FORMAT_ERROR;
        }
    }

    /**
     * Gibt den Wert eines Referenzierten Attributes zurück, dass zu dem selben Element gehört.
     *
     * @param userFieldHash
     * @param target
     * @return Der Wert des <code>userField</code>s.
     */
    private String getValueOfReferencedUserField(final String userFieldHash, final UserFieldTarget target) {
        String value = definitions.getUserField(userFieldHash).getValue(target);
        return value;
    }

    /**
     * Gibt zu einer Formel in INFIX-Notation (die Normale), die als String übergeben wurde, die Postfix-Notation (umgekehrte plonische) zurück.
     *
     * @param infix
     * @return Postfixnotation der Formel
     */
    private static final String getPostFix(String infix) {
        Stack<String> stack = new Stack<String>();
        stack.push("(");
        infix += " )";
        StringTokenizer st = new StringTokenizer(infix);
        if (st.countTokens() == 2) {
            return st.nextToken();
        }
        String tmp_str = "";
        StringBuilder tmp_string_postfix = new StringBuilder();
        while (!stack.empty()) {
            tmp_str = st.nextToken();
            if (isOperator(tmp_str)) {
                while (!stack.empty()) {
                    String tmp_op;
                    tmp_op = stack.pop().toString();
                    if (tmp_op.equals(OPERATOR_MULT) || tmp_op.equals(OPERATOR_DIV)) {
                        tmp_string_postfix.append(" ");
                        tmp_string_postfix.append(tmp_op);
                    } else {
                        stack.push(tmp_op);
                        break;
                    }
                }
                stack.push(tmp_str);
            } else if (tmp_str.equals("(")) {
                stack.push(tmp_str);
            } else if (tmp_str.equals(")")) {
                while (!stack.empty()) {
                    String tmp_element = "";
                    tmp_element = stack.pop().toString();
                    if (!tmp_element.equals("(")) {
                        tmp_string_postfix.append(" ");
                        tmp_string_postfix.append(tmp_element);
                    } else {
                        break;
                    }
                }
            } else {
                try {
                    if (tmp_str.contains(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                        tmp_string_postfix.append(" ");
                        tmp_string_postfix.append(tmp_str);
                    } else {
                        tmp_string_postfix.append(" ");
                        tmp_string_postfix.append(tmp_str);
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return tmp_string_postfix.toString();
    }

    public void setUserFieldDefinitions(final UserFieldDefinitions definitions) {
        this.definitions = definitions;
    }

}
