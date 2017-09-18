/*
 * Created on 20.11.2007
 */
package de.imise.tool3lgm.graphtools.userfield;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.calculator.Calculator.isOperator;

import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.calculator.Calculator;

/**
 * @author hboehme
 * @created 20.11.2007
 */
public class CostingUtil {

    /**
     * Eine Stringkonstante, die Kennzeichent, dass es sich um einen UserFieldHash handelt. Sämtliche UserFieldHashes werden während der Prüfung auf
     * sytaktische Korrekthet einer Formel gegen diese Konstante ersetzt.
     */
    private static final String USERFIELDHASH = "ufh";

    /**
     * Hier sind verschiedene Methoden enthalten, die an unterschiedlichen Stellen genutzt werden.
     */
    public CostingUtil() {
    }

    /**
     * Syncronisiert den HashString und den FormelString. Da nur der HashString gespeichert wird, muss aus dem HashString wieder ein FormelString
     * gemacht werden, der in lesbarer Form vorliegt.
     *
     * @param hash_formula FormelString in hash-Form.
     * @param definitions
     * @return Den FormelString in menschenlesbarer Form
     */
    public static final String getHumanReadableFormulaString(final String hash_formula, final UserFieldDefinitions definitions) {
        StringBuilder resultString = new StringBuilder();
        if (hash_formula != null) {
            StringTokenizer hashtok = new StringTokenizer(hash_formula, " ");
            String s = "";
            UserField tmpField = null;
            while (hashtok.hasMoreTokens()) {
                s = hashtok.nextToken();
                if (isOperator(s) || s.equals(Calculator.OPEN_BRACKET) || s.equals(Calculator.CLOSE_BRACKET) || UserField.isAccountingFunction(s)) {
                    resultString.append(" " + s);
                } else {
                    if (s.contains(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                        try {
                            tmpField = definitions.getUserField(s);
                            resultString.append(" " + tmpField.getName());
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, getResString("formula_integrity_err"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (s.contains(UserField.DIRECTION_FROM_PART_TO_WHOLE)) {
                        resultString.append(" " + getResString("part_to_whole"));
                    } else if (s.contains(UserField.DIRECTION_FROM_WHOLE_TO_PART)) {
                        resultString.append(" " + getResString("whole_to_part"));
                    } else {
                        resultString.append(" " + s);
                    }

                }
            }
        }
        return resultString.toString();
    }

    /**
     * Prüft die übergebene Formel auf Korrektheit. Kriterien für Korrektheit: Die Anzahl der öffnenden und schließenden Klammern muss übereinstimmen
     * auf eine öffnende Klammer darf keine schließende Folgen
     *
     * @param formula Der zu prüfende Formelstring
     * @return Gibt true zurück, wenn die übergebene Formel korrekt ist, ansonsten false
     */
    public static final boolean isFormulaValid(final String formula) {
        if (formula == null) {
            return false;
        }
        if (formula.equals("")) {
            return true;
        }

        // wenn der FormelString mit einem Operanden endet
        String lastChar = formula.substring(formula.length() - 2, formula.length() - 1);
        String firstChar = formula.trim().substring(0, 1);

        if (isOperator(lastChar)) {
            return false;
        }

        // Wenn aus es der Benutzer irgendwie schafft einen Operator als erstes Zeichen zu setzen.
        if (isOperator(firstChar)) {
            return false;
        }

        StringTokenizer formulaTokens = new StringTokenizer(formula);
        String first = "";
        String second = "";
        if (formulaTokens.hasMoreTokens()) {
            first = formulaTokens.nextToken();
            if (first.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                first = USERFIELDHASH;
            }
        }
        while (formulaTokens.hasMoreTokens()) {
            second = formulaTokens.nextToken();

            if (second.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                second = USERFIELDHASH;
            }

            //Damit wird verhindert, dass zwei Operatoren aufeinander folgen
            if (isOperator(first) && isOperator(second)) {
                return false;
            }

            // Wenn auf ein Operatorzeichen eine schließende Klammer folgt
            if (isOperator(first) && second.equals(Calculator.CLOSE_BRACKET)) {
                return false;
            }

            // Wenn auf eine schließende Klammer ein Operatorzeichen folgt
            if (first.equals(Calculator.CLOSE_BRACKET) && second.equals(USERFIELDHASH)) {
                return false;
            }

            // Wenn auf eine schließende Klammer eine Verrechnungsfunktion folgt
            if (first.equals(Calculator.CLOSE_BRACKET) && UserField.isAccountingFunction(second)) {
                return false;
            }

            // Wenn auf eine öffnende Klammer ein Operatorzeichen folgt.
            if (first.equals(Calculator.OPEN_BRACKET) && isOperator(second)) {
                return false;
            }

            // Auf eine öffnende Klammer darf keine schließende Folgen
            if (first.equals(Calculator.OPEN_BRACKET) && second.equals(Calculator.CLOSE_BRACKET)) {
                return false;
            }

            // Wenn zwei Operanden auf ein anderfolgen
            if (first.equals(USERFIELDHASH) && second.equals(USERFIELDHASH)) {
                return false;
            }
            first = second;
        }
        return true;
    }

    /**
     * Wenn das übergebene UserField eine einfache Teilwertsummenformel ist, dann kommt hier die Formel ohne
     * alle WhiteSpaces und ohne die evtl. vorhandenen und überflüssigen Klammern am Anfang und Endezurück.
     *
     * @param userField
     * @return
     */
    private static String getSimpleFractionValueSumFormula(final UserField userField) {
        String formula = userField.getFormula().trim();
        if (Strings.isNullOrEmpty(formula)) {
            return null;
        }

        //alle whitespaces in der Formel löschen
        formula = formula.replaceAll("\\s", "");

        //evtl. vorhandene äußere Klammern beachten und entfernen
        // ((( TWSUM (UF1, UF2, VG1) ))) wird zu TWSUM (UF1, UF2, VG1)
        int initialBrackets = 0;
        int formulaLenght = formula.length();
        for (int i = 0; i < formulaLenght; i++) {
            if (Character.isWhitespace(formula.charAt(i))) {
                continue;
            }
            if (!formula.startsWith(Calculator.OPEN_BRACKET, i)) {
                break;
            }
            initialBrackets++;
        }
        int endBrackets = 0;
        for (int i = 1; i < formulaLenght; i++) {
            int offset = formulaLenght - i;
            if (Character.isWhitespace(formula.charAt(offset))) {
                continue;
            }
            if (!formula.startsWith(Calculator.CLOSE_BRACKET, offset)) {
                break;
            }
            endBrackets++;
        }

        //am Ende muss eine Klammer mehr stehen, weil die eigenliche Funktion auch eine schließende Klammer hat
        if (initialBrackets != endBrackets - 1) {
            return null;
        }
        //äußere Klammern entfernen
        if (initialBrackets > 0) {
            formula = formula.substring(initialBrackets, formulaLenght - 2 * initialBrackets);
        }

        //jetzt muss die Formel mit TWSUM beginnen
        if (!formula.startsWith(UserField.ACCOUNTING_FUNCTION_TWSUM)) {
            return null;
        }
        //die nächste gefundene schließende Klammer muss ganz am Ende stehen
        int closeBracketIndex = formula.indexOf(Calculator.CLOSE_BRACKET, UserField.ACCOUNTING_FUNCTION_TWSUM.length());
        if (closeBracketIndex != formula.length() - 1) {
            return null;
        }
        return formula;
    }

    /**
     * Prüft, ob die Formel des übergebenen UserFields eine einfache Teilwertsummenformel ist.
     * Diese Funktion setzt vorraus, dass die Formel valide ist!
     *
     * @param userField
     * @return
     */
    public static final boolean isSimpleFractionValueSumFormula(final UserField userField) {
        String formula = getSimpleFractionValueSumFormula(userField);
        return formula != null;
    }

    private static String extractSimpleFractionValueSumFormulaEdgeClassName(final UserField userField) {
        String edgeClassName = null;
        String formula = getSimpleFractionValueSumFormula(userField);
        if (formula != null) {
            int startIndex = formula.indexOf(Calculator.OPEN_BRACKET) + 1;
            int endIndex = formula.indexOf(Calculator.OPERAND_DELIMITER);
            edgeClassName = formula.substring(startIndex, endIndex).trim();
        }
        return edgeClassName;
    }

    /**
     * Wenn das übergebene UserField eine einfache Teilwertsumme ist, dann wird hier die Kantenklasse der Formel extrahiert.
     *
     * @param userField
     * @return Kantenklasse der einfachenTeilwertsummenformel oder <code>null</code>
     */
    public static final Class<? extends Edge> getSimpleFractionValueSumFormulaEdgeClass(final UserField userField) {
        Class<? extends Edge> edgeClass = null;
        String edgeClassName = extractSimpleFractionValueSumFormulaEdgeClassName(userField);
        if (edgeClassName != null) {
            edgeClass = ModelConstants.getClassForName(edgeClassName).asSubclass(Edge.class);
        }
        return edgeClass;
    }

    /**
     * Gibt zu dem übergebenen {@link Style} den Lokalisierten Anzeigenamen des Styles zurück.
     *
     * @param styleValue
     * @return Lokalisierten Anzeigenamen des Styles oder ""
     */
    public final static String getDisplayableStyleName(final UserField.Style style) {
        return getResString(style.toString());
    }

}
