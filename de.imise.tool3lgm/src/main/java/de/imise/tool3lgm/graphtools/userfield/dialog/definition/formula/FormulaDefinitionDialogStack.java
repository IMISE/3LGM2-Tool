package de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula;

import java.util.Stack;
import java.util.StringTokenizer;

import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.calculator.Calculator;

public class FormulaDefinitionDialogStack extends Stack<String> {

    final FormulaDefinitionDialog dialog;

    private boolean update;

    public FormulaDefinitionDialogStack(final FormulaDefinitionDialog dialog) {
        this.dialog = dialog;
        update = true;
    }

    public String getLastElement() {
        return isEmpty() ? "" : get(size() - 1);
    }

    @Override
    public String push(final String item) {
        super.push(item);
        update();
        return item;
    }

    @Override
    public synchronized String pop() {
        String popped = isEmpty() ? "" : super.pop();
        update();
        return popped;
    }

    @Override
    public void clear() {
        super.clear();
        update();
    }

    private void update() {
        if (update) {
            dialog.update();
        }
    }

    /**
     * Konvertiert einen FormelString in einen Stack. Der String wird dazu mittel StringTokenizers zerlegt. Argumente, die ein userField kennzeichenen
     * oder +,*,-,/ werden auf den Stack draufgelegt. Verrechnungsfunktionen werden als String zusammengefasst und als einzelnes zusammengehörendes
     * Argument auf den Stack gelegt.
     *
     * @param internalFormula Die Formel in interner repräsentation Bsp: UserFieldHash_XXX_X + ( UserFieldHash_XXX_X )
     * @return Stack, der die Formel enthält.
     */
    public final void fill(final String internalFormula) {
        if (internalFormula != null) {
            update = false;
            StringTokenizer st = new StringTokenizer(internalFormula);
            StringBuilder sb;
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (UserField.isAccountingFunction(token)) {
                    sb = new StringBuilder();
                    while (!token.equals(")")) {
                        sb.append(" ");
                        sb.append(token);
                        token = st.nextToken();
                    }
                    sb.append(" )");
                    push(sb.toString());
                } else if (token.equals("(")) {
                    push(FormulaDefinitionDialog.BRACKETS);
                } else if (token.equals(")")) {
                    push(FormulaDefinitionDialog.LEAVE_BRACKET_ESCAPE_CHARS);
                } else if (token.startsWith(UserField.USERFIELD_HASH_STRING_PREFIX)) {
                    push(token);
                } else if (Calculator.OPERATOR_SIGNS.contains(token)) {
                    push(token);
                }
            }
            update = true;
        }
        update();
        return;
    }

}
