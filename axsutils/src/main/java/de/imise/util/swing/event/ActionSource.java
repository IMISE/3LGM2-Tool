package de.imise.util.swing.event;

import java.lang.reflect.Constructor;

import javax.swing.Action;

/**
 * Interface that can preferably be attached to all enum classes (but also to
 * actions themselves) that define any actions or are actions themselves. The
 * interface can return an action via the default function
 * {@link #createAction()} if the ActionSource via {@link #getActionClass()} is
 * an action that can be initialized with the empty constructor or is already an
 * action itself.
 *
 * @author AXS (10.03.2018)
 */
public interface ActionSource {

    public static final String PPP = "...";

    /**
     * Returns the action to this ActionSource if this is already an action
     * itself or if the {@link #getActionClass()} function returns an action
     * class that can be instantiated via the empty constructor.
     *
     * @return
     */
    public default ExtendedAction createAction() {
        if (this instanceof ExtendedAction) {
            return (ExtendedAction) this;
        }
        Class<? extends ExtendedAction> actionClass = getActionClass();
        Class<?> constructorParameterClass = getClass();
        ExtendedAction action = null;
        while (constructorParameterClass != null) {
            try {
                Constructor<? extends ExtendedAction> actionConstructor = actionClass.getConstructor(constructorParameterClass);
                action = actionConstructor.newInstance(this);
                break;
            } catch (Exception e) {
                constructorParameterClass = constructorParameterClass.getSuperclass();
            }
        }
        if (action != null && isInteractiveAction()) {
            action.putValue(Action.NAME, action.getValue(Action.NAME).toString() + PPP);
        }
        return action;
    }

    /**
     * Returns the ActionClass that can be initialized for this ActionSource.
     * ActionSources that return a valid ActionClass here can be initialized via
     * this class.
     */
    public default Class<? extends ExtendedAction> getActionClass() {
        return null;
    }

    /**
     * For all actions that require user interaction (e.g. via a dialog),
     * <code>true</code> should be returned here. These get in menus always 3
     * dots after the displayed name.
     *
     * @return
     */
    public default boolean isInteractiveAction() {
        return false;
    }

    /**
     * @return the Object to identify the icon in the resources
     */
    public default Object getIconIdentifier() {
        return this;
    }
}
