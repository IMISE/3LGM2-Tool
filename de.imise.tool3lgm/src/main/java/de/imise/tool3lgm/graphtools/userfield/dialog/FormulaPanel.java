/*
 * Created on 10.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.text.JTextComponent;

import de.imise.util.swing.component.text.ExtendedTextField;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.userfield.CostingUtil;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;

/**
 * @author AXS
 */
public class FormulaPanel extends AbstractInputPanel {

	/**
	 * Das UserField, dessem Formel mit diesem Panel geändert werden sollen.
	 */
	private UserField userField;

	/**
	 * <code>String</code>, in dem die interne Repräsentation der neuen Formel gespeichert wird.
	 * Beim Übernehmen der Eigenschaften in das <code>UserField</code> wird dieser <code>String</code>
	 * dann als Formel im <code>UserField</code> gesetzt.
	 */
	private String newFormulaInternalStyle;
	
	/**
	 * @param userField
	 * 		Feld, dessen Eigenschaften geändert werden sollen
	 * @param owner
	 * 		Owner für weitere Unterdialoge
	 * @param userFieldNameSource
	 * 		Textkomponente, die einen evtl. vom aktuell gesetzten Namen des Userfields abweichenden Namen vorgibt
	 */
	public FormulaPanel(final JDialog owner, final UserField userField, final UserFieldDefinitions definitions, final JTextComponent userFieldNameSource) {
		super();
		this.userField = userField;
		
		setBorder(BorderFactory.createTitledBorder(Tool3lgmConstants.getResString("formula")));
		setLayout(new BorderLayout());
		
		final ExtendedTextField formulaTextField = new ExtendedTextField();
		JButton buttonNewFormula = new JButton();
		add(formulaTextField, BorderLayout.CENTER);
		add(buttonNewFormula, BorderLayout.EAST);

		formulaTextField.setText(userField.getFormula());
		
		buttonNewFormula.setAction(new AbstractAction(Tool3lgmConstants.getResString("editFormula")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				userField.setName(userFieldNameSource.getText());
				newFormulaInternalStyle = FormulaDefinitionDialog.showDialog(owner, definitions, userField, userFieldNameSource.getText());
				formulaTextField.setText(CostingUtil.getHumanReadableFormulaString(newFormulaInternalStyle, definitions));
			}
		});
		
		newFormulaInternalStyle = userField.getFormula();
		formulaTextField.setText(CostingUtil.getHumanReadableFormulaString(newFormulaInternalStyle, definitions));
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.AbstractInputPanel#cancel()
	 */
	@Override
	public void cancel() {
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.AbstractInputPanel#commit()
	 */
	@Override
	public void commit() {
		userField.removeAllStandardValues();
		userField.setFormula(newFormulaInternalStyle);
	}

	
	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.userfield.dialog.AbstractInputPanel#getError()
	 */
	@Override
	public String getError() {
		if (newFormulaInternalStyle == null || newFormulaInternalStyle.trim().equals(""))
			return Tool3lgmConstants.getErrString("missing_formula");
		return super.getError();
	}

}