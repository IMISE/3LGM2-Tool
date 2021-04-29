package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.util.swing.component.text.NumberTextField;

/**
 * Ein Focus-Listener der nur für Eingabefelder von Kennzahlen gedacht ist. Er
 * sorgt dafür, dass der Wert eines Eingabetextfeldes formatiert angezeigt wird,
 * solange das Eingabefeld nicht den Focus hat und wenn es den Focus bekommt
 * (und damit im Editiermodus ist), immer unformatiert angezeigt wird.
 *
 * @author Ich
 * @create 27.09.2015
 */
public class PropertyDialogUserFieldPanelNumberInputFocusListener implements FocusListener {

    private final PropertyDialogUserFieldPanelChangeListener changeListener;

    private final ModelElement me;

    private final UserField userField;

    public PropertyDialogUserFieldPanelNumberInputFocusListener(final PropertyDialogUserFieldPanelChangeListener changeListener, final ModelElement me, final UserField userField) {
        this.changeListener = changeListener;
        this.me = me;
        this.userField = userField;
    }

    @Override
    public void focusGained(final FocusEvent e) {
        JTextComponent textComponent = (JTextComponent) e.getSource();
        //Value ist unformatiert
        String value = PropertyDialogUserFieldPanel.getUserFieldValue(me, userField, false);
        textComponent.setText(value);
        textComponent.getDocument().addDocumentListener(changeListener);
    }

    @Override
    public void focusLost(final FocusEvent e) {
        NumberTextField textComponent = (NumberTextField) e.getSource();
        textComponent.getDocument().removeDocumentListener(changeListener);
        //Value ist formatiert
        String value = PropertyDialogUserFieldPanel.getUserFieldValue(me, userField, true);
        textComponent.setValue(value);
    }

}
