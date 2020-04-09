package de.imise.util.swing.event;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public interface OptionAction {

    public default JCheckBoxMenuItem createMenuItem() {
        if (this instanceof Action) {
            final JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem((Action) this);
            checkBoxMenuItem.addAncestorListener(new AncestorListener() {
                @Override
                public void ancestorRemoved(final AncestorEvent event) {
                }
                @Override
                public void ancestorMoved(final AncestorEvent event) {
                }
                @Override
                public void ancestorAdded(final AncestorEvent event) {
                    //diese Funktion wird beim Anzeigen des MenuItems ausgelöst. Dabei muss der Selektionszustand
                    //des Items noch einmal geprüft werden, falls die zu grunde liegende Property woanders als über
                    //dieses Item geändert wurde
                    //updateSelection();
                    checkBoxMenuItem.setEnabled(isEnabled());
                    checkBoxMenuItem.setSelected(isSelected());
                }
            });
            return checkBoxMenuItem;
        }
        return null;
    }

    public boolean isSelected();

    public boolean isEnabled();

}
