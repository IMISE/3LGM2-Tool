package de.imise.tool3lgm.event.action;

import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author Ich (23.05.2018)
 */
public class UserPropertyBooleanChangeAction extends GlobalOptionAction {

    /**  */
    private final UserProperties.BooleanProperty booleanProperty;

    /**
     * @param booleanProperty
     */
    public UserPropertyBooleanChangeAction(final UserProperties.BooleanProperty booleanProperty) {
        super(booleanProperty);
        this.booleanProperty = booleanProperty;
    }

    @Override
    public void changeOption() {
        booleanProperty.set(booleanProperty.isNot());
    }

    @Override
    public boolean isSelected() {
        return booleanProperty.is();
    }

}
