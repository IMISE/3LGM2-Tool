package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.userproperties.UserProperties;

public class UserPropertyBooleanChangeAction extends GlobalOptionAction {

    private final UserProperties.BooleanProperty booleanProperty;

    public UserPropertyBooleanChangeAction(final UserProperties.BooleanProperty booleanProperty) {
        super(booleanProperty);
        this.booleanProperty = booleanProperty;
    }

    @Override
    public void changeOption() {
        UserProperties.set(booleanProperty, !UserProperties.is(booleanProperty));
    }

    @Override
    public boolean isSelected() {
        return UserProperties.is(booleanProperty);
    }

}
