package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PropertyDialogUserFieldPanelChangeListener implements DocumentListener, ActionListener {

    private final PropertyDialogUserFieldPanel panel;

    public PropertyDialogUserFieldPanelChangeListener(final PropertyDialogUserFieldPanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        panel.commit();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        panel.commit();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        panel.commit();
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        panel.commit();
    }

}
