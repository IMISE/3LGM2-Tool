/*
 * Created on 07.01.2008 Window - Preferences - Java - Code Style - Code
 * Templates
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula.panel.OperatorInputPanel;
import de.imise.tool3lgm.graphtools.userfield.dialog.definition.formula.panel.ReferencePanel;

/**
 * Der Dialog lässt die Benutzer eine Verrechnungsfunktion spezifieren. Nachdem
 * er im Formeleditor eine Verrechungsfunktion ausgewählt hat ( durch
 * Buttonklick) muss er hier angeben, welche Parameter in der
 * Verrechungsfunktion einfließen sollen.)
 *
 * @author hboehme
 * @created 07.01.2008
 */
public class VfDialog extends JDialog implements ActionListener {

    /**
     * Der Rückgabewert als zusammengefattser String
     */
    private String retVal = "";

    /**
     * das aktuelle <code>userField</code>, für welches die VF definiert wird.
     */
    private final UserField userField;

    /**
     * Das Panel wird initialisiert, wenn die VF eine Summe oder TWSumme werden
     * soll.
     */
    private OperatorInputPanel operatorInputPanel;

    /**
     * Das Panel wird initialisiert, wenn die VF eine Referenz werden soll.
     */
    private ReferencePanel rp;

    /**
     * wird mit einem Wert der Konstanten belegt <- zur Kennzeichung, welches
     * Panel initialisiert werden soll.
     */
    private final String vfOperator;

    /**
     * Instanz des Panels. Beinzhaltet selbst nur den OK.- und Abbrechen button.
     * Kann je nach übergebenem Operatortyp (SUM, TWSUM) den entsprechenden
     * komplettierten Dialog anzeigen.
     *
     * @param owner
     * @param definitions
     * @param operator Eine der beiden Konstanten <code>UserField.SUM</code>
     *            oder <code>UserField.TWSUM</code>
     * @param userField
     */
    public VfDialog(final Dialog owner, final UserFieldDefinitions definitions, final String operator, final UserField userField) {
        super(owner);
        this.userField = userField;
        vfOperator = operator;
        String title = getResString("accounting_function") + ": " + UserField.getDisplayableFunctionName(vfOperator);
        setTitle(title);
        setModal(true);
        setLocationByPlatform(true);
        init(definitions);
    }

    /**
     *
     */
    private void init(final UserFieldDefinitions definitions) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        if (vfOperator.equals(UserField.ACCOUNTING_FUNCTION_REF)) {
            rp = new ReferencePanel(definitions, userField);
            add(rp, gbc);
        } else {
            operatorInputPanel = new OperatorInputPanel(definitions, vfOperator, userField);
            add(operatorInputPanel, gbc);
        }
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weighty = 0;
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton(getResString("ok"));
        okButton.setActionCommand("ok");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton(getResString("cancel"));
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, gbc);
        pack();
    }

    /**
     * Zeigt der Dialog an.
     *
     * @return Gibt die Verrechungsfunktion als String-Rückgabewert zurück.
     */
    public String showDialog() {
        setVisible(true);
        return retVal;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getActionCommand().equals("ok")) {

            if (vfOperator.equals(UserField.ACCOUNTING_FUNCTION_REF)) {
                retVal = rp.getRetVal();
            } else {
                retVal = operatorInputPanel.getRetVal();
            }
            if (retVal != null) {
                //JOptionPane.showMessageDialog(null, retVal);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, getResString("empty_values"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);

            }

        } else if (e.getActionCommand().equals("cancel")) {
            retVal = "";
            dispose();
        }

    }

}
