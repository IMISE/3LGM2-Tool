package de.imise.tool3lgm.event.action;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import de.imise.tool3lgm.graphtools.model.GDCollectionChangeType;
import de.imise.tool3lgm.userproperties.UserProperties;

public class UserPropertyBooleanChangeAction extends GlobalOptionAction {

    private final UserProperties.BooleanProperty booleanProperty;

    public UserPropertyBooleanChangeAction(final UserProperties.BooleanProperty booleanProperty) {
        super(booleanProperty, UserProperties.is(booleanProperty));
        this.booleanProperty = booleanProperty;
    }

    public UserPropertyBooleanChangeAction(final UserProperties.BooleanProperty booleanProperty, final GDCollectionChangeType changeType) {
        super(booleanProperty, UserProperties.is(booleanProperty), changeType);
        this.booleanProperty = booleanProperty;
    }

    @Override
    protected void changeOption() {
        UserProperties.set(booleanProperty, !UserProperties.is(booleanProperty));
        updateSelection();
    }

    public void updateSelection() {
        setSelected(UserProperties.is(booleanProperty));
    }

    @Override
    public JCheckBoxMenuItem createMenuItem() {
        final JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(this);
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
                updateSelection();
            }

        });
        return checkBoxMenuItem;
    }

}
