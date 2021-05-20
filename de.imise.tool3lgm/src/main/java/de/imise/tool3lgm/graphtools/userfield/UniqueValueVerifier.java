package de.imise.tool3lgm.graphtools.userfield;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;

/**
 * Wird zur Zeit nicht genutzt. Wenn dann bei der Eingabe von IDs als
 * benutzerdefinierte Eigenschaften. Die Klasse ist nur rudimentär
 * implementiert.
 *
 * @author Ich
 * @create 19.08.2015
 */
public class UniqueValueVerifier implements DocumentListener {

    //    private final UserField userField;
    //
    //    private final ModelElement me;
    //
    //    private final JTextField newValueInputField;
    //
    //    private final JLabel label4Warning;

    public UniqueValueVerifier(final UserField userField, final ModelElement me, final JTextField newValueInputField, final JLabel label4Warning) {
        //        this.userField = userField;
        //        this.me = me;
        //        this.newValueInputField = newValueInputField;
        //        this.label4Warning = label4Warning;
        newValueInputField.getDocument().addDocumentListener(this);
    }

    public UniqueValueVerifier(final UserField userField, final ModelElement me, final JTextField newValueInputField) {
        this(userField, me, newValueInputField, null);
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        try {
            System.err.println(e.getDocument().getText(0, e.getDocument().getLength()));
        } catch (BadLocationException e1) {
            //TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        insertUpdate(e);
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        insertUpdate(e);
    }

}
