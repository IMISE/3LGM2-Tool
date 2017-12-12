package de.imise.tool3lgm.event.action;

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
        UserProperties.set(booleanProperty, isSelected());
    }
}
