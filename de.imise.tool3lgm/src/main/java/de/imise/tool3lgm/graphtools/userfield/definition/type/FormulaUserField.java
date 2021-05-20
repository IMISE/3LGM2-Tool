package de.imise.tool3lgm.graphtools.userfield.definition.type;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;

/**
 * @author AXS (18.05.2021)
 */
public class FormulaUserField extends AccountingUserField {

    /**
     * Repräsentiert den String from_part_to_whole
     */
    public static final String DIRECTION_FROM_PART_TO_WHOLE = "from_part_to_whole";

    /**
     * Repräsentiert den Sting from_whole_to_part
     */
    public static final String DIRECTION_FROM_WHOLE_TO_PART = "from_whole_to_part";

    /**
     * TODO:Alle Funktionen durch diesen ENUM ersetzen!
     */
    //  public static enum FUNC {SUM, MULT, TWSUM, AVG, INDI, REF, MIN, MAX};

    /**
     * Repräsentiert den Sting SUM
     */
    public static final String ACCOUNTING_FUNCTION_SUM = "SUM";

    /**
     * Repräsentiert den String MULT
     */
    public static final String ACCOUNTING_FUNCTION_MULT = "MULT";

    /**
     * Repräsentiert den Sting TWSUM
     */
    public static final String ACCOUNTING_FUNCTION_TWSUM = "TWSUM";

    /**
     * Repräsentiert den Sting AVG
     */
    public static final String ACCOUNTING_FUNCTION_AVG = "AVG";

    /**
     * Repräsentiert den Sting INDI
     */
    public static final String ACCOUNTING_FUNCTION_INDI = "INDI";

    /**
     * Repräsentiert den Sting REF
     */
    public static final String ACCOUNTING_FUNCTION_REF = "REF";

    /**
     * Repräsentiert den Sting MIN
     */
    public static final String ACCOUNTING_FUNCTION_MIN = "MIN";

    /**
     * Repräsentiert den Sting MAX
     */
    public static final String ACCOUNTING_FUNCTION_MAX = "MAX";

    /**
     *
     */
    private String formula = "";

    /**
     * <code>true</code>, wenn die Formel dieses UserFields eine einfache
     * Teilwertsumme mit oder ohne Verteilungsgewicht ist.
     */
    private boolean simplePartValueSumFormula = false;

    /**
     * Dieses Set beinhaltet Strings, die interne Verrechnungsfunktionen
     * kennzeichen beinhaltet:
     */
    private static final Set<String> ACCOUNTING_FUNCTIONS_SET = ImmutableSet.of(ACCOUNTING_FUNCTION_SUM, ACCOUNTING_FUNCTION_TWSUM, ACCOUNTING_FUNCTION_MAX, ACCOUNTING_FUNCTION_MIN, ACCOUNTING_FUNCTION_MULT, ACCOUNTING_FUNCTION_AVG,
            ACCOUNTING_FUNCTION_INDI, ACCOUNTING_FUNCTION_REF);

    /**
     * @param targetClass
     * @param id
     */
    public FormulaUserField(final Class<? extends UserFieldTarget> targetClass, final String id) {
        super(targetClass, id);
    }

    /**
     * @param targetClass
     */
    public FormulaUserField(final Class<? extends UserFieldTarget> targetClass) {
        super(targetClass);
    }

    /**
     * Setzt die Kennzahlformel.
     *
     * @param formula
     * @return boolean true: Wenn es sich bei dem <code>userField</code> um eine
     *         sekundäre Kennzahl handelt, d.h. eine Kennzahl, die
     *         Kennzahlformeln repräsentiert - ansonsten false
     */
    public void setFormula(final String formula) {
        this.formula = Strings.isNullOrEmpty(formula) ? "" : formula.trim();
        simplePartValueSumFormula = CostingUtil.isSimpleFractionValueSumFormula(formula);
    }

    /**
     * Gibt die Formel zurück, die sich hinter dem <code>UserField</code>
     * verbirgt.
     *
     * @return wenn es sich um ein UserField handelt, dass eine Kennzhalformel
     *         repräsentierne soll, wird die Formel in INFIX-Notation
     *         zurückgegeben ansonsten null.
     */
    public String getFormula() {
        return formula;
    }

    /**
     * Prüft, ob <code>this</code> das übergebenene <code>UserField</code>
     * <code>other</code> benutzt. Möglich ist als Format oder innerhalb einer
     * Formel.
     */
    public boolean uses(final UserField possibleUsedField) {
        if (possibleUsedField == null || !(possibleUsedField instanceof AccountingUserField)) {
            return false;
        }
        Set<String> idsInFormula = getIDsInFormula();
        if (idsInFormula != null && getIDsInFormula().contains(possibleUsedField.getID())) {
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public final boolean isSimplePartValueSumFormula() {
        return simplePartValueSumFormula;
    }

    /**
     * Prüft, ob dieses {@link UserField} ein Formel-UserFIeld ist, das einen
     * Indikator definiert.
     *
     * @param userField
     * @return
     */
    public final boolean isIndicatorFormula() {
        return formula.startsWith(ACCOUNTING_FUNCTION_INDI);
    }

    /**
     * Liefert eine Liste aller in der Formel vorkommenden IDs von anderen
     * <code>UserField</code>s.
     *
     * @return
     */
    public Set<String> getIDsInFormula() {
        if (formula == null || formula.equals("")) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(formula, " ()+-/*|");
        Set<String> ids = new HashSet<>(st.countTokens());
        while (st.hasMoreElements()) {
            String token = st.nextToken();
            if (token.startsWith(USERFIELD_ID_PREFIX)) {
                ids.add(token);
            }
        }
        return ids;
    }

}
