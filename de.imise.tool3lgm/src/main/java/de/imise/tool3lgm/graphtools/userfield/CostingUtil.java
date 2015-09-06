/*
 * Created on 20.11.2007
 */
package de.imise.tool3lgm.graphtools.userfield;

import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula.FormulaDefinitionDialog;

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
                if (Calculator.OPERATOR_SIGNS.contains(s) || s.equals(Calculator.OPEN_BRACKET) || s.equals(Calculator.CLOSE_BRACKET) || UserField.ACCOUNTING_FUNCTIONS_SET.contains(s)) {
                    resultString.append(" " + s);
                } else {
                    if (s.contains(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                        try {
                            tmpField = definitions.getUserField(s);
                            resultString.append(" " + tmpField.getName());
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, Tool3lgmConstants.getErrString("formula_integrity_err"), Tool3lgmConstants.getResString("fehler"), JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (s.contains(UserField.DIRECTION_FROM_PART_TO_WHOLE)) {
                        resultString.append(" " + Tool3lgmConstants.getResString("part_to_whole"));
                    } else if (s.contains(UserField.DIRECTION_FROM_WHOLE_TO_PART)) {
                        resultString.append(" " + Tool3lgmConstants.getResString("whole_to_part"));
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

        if (Calculator.OPERATOR_SIGNS.contains(lastChar)) {
            return false;
        }

        // Wenn aus es der Benutzer irgendwie schafft einen Operator als erstes Zeichen zu setzen.
        if (Calculator.OPERATOR_SIGNS.contains(firstChar)) {
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
            if (Calculator.OPERATOR_SIGNS.contains(first) && Calculator.OPERATOR_SIGNS.contains(second)) {
                return false;
            }

            // Wenn auf ein Operatorzeichen eine schließende Klammer folgt
            if (Calculator.OPERATOR_SIGNS.contains(first) && second.equals(Calculator.CLOSE_BRACKET)) {
                return false;
            }

            // Wenn auf eine schließende Klammer ein Operatorzeichen folgt
            if (first.equals(Calculator.CLOSE_BRACKET) && second.equals(USERFIELDHASH)) {
                return false;
            }

            // Wenn auf eine schließende Klammer eine Verrechnungsfunktion folgt
            if (first.equals(Calculator.CLOSE_BRACKET) && UserField.ACCOUNTING_FUNCTIONS_SET.contains(second)) {
                return false;
            }

            // Wenn auf eine öffnende Klammer ein Operatorzeichen folgt.
            if (first.equals(Calculator.OPEN_BRACKET) && Calculator.OPERATOR_SIGNS.contains(second)) {
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
     * Gibt zu dem übergebenen {@link Style} den Lokalisierten Anzeigenamen des Styles zurück.
     * 
     * @param styleValue
     * @return Lokalisierten Anzeigenamen des Styles oder ""
     */
    public final static String getDisplayableStyleName(final UserField.Style style) {
        return Tool3lgmConstants.getResString(style.toString());
    }

    //	/** 
    //	 * Gibt zu dem übergebenen UserField den Lokalisierten Anzeigenamen des Styles zurück.
    //	 * 
    //	 * @param userField
    //	 * 			{@link UserField}, von dessen Style der Anzeigename ermittelt werden soll 
    //	 * @return Lokalisierten Anzeigenamen des Styles oder ""
    //	 */
    //	public final static String getDisplayableStyleName(UserField userField){
    //		if (userField==null)
    //			return "";
    //		return getDisplayableStyleName(userField.getStyle());
    //	}

    //	/** 
    //	 * Gibt zu dem übergebenen int - Stylewert den Namen des Styls zurück.
    //	 * 
    //	 * @param styleValue
    //	 * @return den Namen des Styles
    //	 */
    //	public static final String getStyleNameForStyleValue(int styleValue) {
    //		String userFieldStyle = "";
    //
    //		switch (styleValue) {
    //		case UserField.CHECK_BOX_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_checkbox");
    //			break;
    //		}
    //		case UserField.COMBO_BOX_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_combobox");
    //			break;
    //		}
    //		case UserField.CLASSIFICATION_NUMBER_FORMULA_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_classification_number_formula_short");
    //			break;
    //		}
    //		case UserField.CLASSIFICATION_NUMBER_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_classification_number_short");
    //			break;
    //		}
    //		case UserField.HYPERLINK_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_hyperlink");
    //			break;
    //		}
    //		case UserField.MULTI_LINE_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_multi_line");
    //			break;
    //		}
    //		case UserField.RADIO_BUTTON_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_radiobutton");
    //			break;
    //		}
    //		case UserField.SEPARATOR_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_separator");
    //			break;
    //		}
    //		case UserField.SINGLE_LINE_STYLE: {
    //			userFieldStyle = Tool3lgmConstants.getResString("userFieldEditor_single_line");
    //			break;
    //		}
    //
    //		default:
    //		}
    //
    //		return userFieldStyle;
    //	}

    /**
     * Konvertiert einen FormelString in einen Stack. Der String wird dazu mittel StringTokenizers zerlegt. Argumente, die ein userField kennzeichenen
     * oder +,*,-,/ werden auf den Stack draufgelegt. Verrechnungsfunktionen werden als String zusammengefasst und als einzelnes zusammengehörendes
     * Argument auf den Stack gelegt.
     * 
     * @param internalFormula Die Formel in interner repräsentation Bsp: UserFieldHash_XXX_X + ( UserFieldHash_XXX_X )
     * @return Stack, der die Formel enthält.
     */
    public static final Stack<String> getStackForInternalFormula(final String internalFormula) {
        if (internalFormula == null) {
            return null;
        }
        Stack<String> stack = new Stack<String>();

        StringTokenizer st = new StringTokenizer(internalFormula);
        StringBuilder sb;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            //			if (token.equals(UserField.ACCOUNTING_FUNCTION_SUM) || token.equals(UserField.ACCOUNTING_FUNCTION_TWSUM)|| token.equals(UserField.ACCOUNTING_FUNCTION_INDI) || token.equals(UserField.ACCOUNTING_FUNCTION_AVG)|| token.equals(UserField.ACCOUNTING_FUNCTION_MULT)|| token.equals(UserField.ACCOUNTING_FUNCTION_MAX)|| token.equals(UserField.ACCOUNTING_FUNCTION_MIN)) {
            if (UserField.ACCOUNTING_FUNCTIONS_SET.contains(token)) {
                sb = new StringBuilder();
                while (!token.equals(")")) {
                    sb.append(" ");
                    sb.append(token);
                    token = st.nextToken();
                }
                sb.append(" )");
                stack.push(sb.toString());
            } else if (token.equals("(")) {
                stack.push("(  )");
            } else if (token.equals(")")) {
                stack.push(FormulaDefinitionDialog.USERFIELD_IN_FORMULA_BRACKET_LEAVE);
            } else if (token.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                stack.push(token);
            } else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                stack.push(token);
            }
        }
        return stack;
    }
}
