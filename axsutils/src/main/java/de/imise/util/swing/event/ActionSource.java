package de.imise.util.swing.event;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

/**
 * Interface that can preferably be attached to all enum classes that define any
 * ActionIdentifier. This way, the actions can be defined right away, if they
 * only need the ActionSource object for initialization.
 *
 * @author AXS (10.03.2018)
 */
public interface ActionSource {

    /**
     * All actions that should have 3 dots appended to the name in the menu must
     * have this string in the name of their enum at the end. These should be
     * all actions that require user interaction during the execution of the
     * action, i.e. a dialog is opened.
     */
    public static final String INTERACTIVE_ACTION_NAME_POSTFIX = "_PPP";

    /**
     * These 3 dots are appended to the label of all actions whose Identifier
     * toString() method returns a string with "_PPP"
     * ({@link #INTERACTIVE_ACTION_NAME_POSTFIX}) at the end.
     */
    public static final String PPP = "...";

    /**
     * Maps from an ActionSource to the action class to be initialized for that
     * ActionSource.
     */
    static final Map<ActionSource, Class<? extends ExtendedAction>> ACTION_CLASS = new HashMap<>();

    /**
     * @return action for this ActionSource
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
     * Stores for the passed ActionSources which ActionClass should be returned
     * via the {@link #getActionClass()} method (if the default implementation
     * remains).
     *
     * @param actionClass
     * @param actionSources
     */
    public static void put(final Class<? extends ExtendedAction> actionClass, final ActionSource... actionSources) {
        for (ActionSource actionSource : actionSources) {
            ACTION_CLASS.put(actionSource, actionClass);
            String name = actionSource.toString();
            boolean isInteractive = name.endsWith(INTERACTIVE_ACTION_NAME_POSTFIX);
            if (!isInteractive && actionSource instanceof Enum<?>) {
                name = ((Enum<?>) actionSource).name();
                isInteractive = name.endsWith(INTERACTIVE_ACTION_NAME_POSTFIX);
            }
            if (isInteractive) {
                INTERACTIVE_ACTIONS.add(actionSource);
            }
        }
    }

    /**
     * Actions where somehow a dialog is displayed after the call should be
     * added via this put function. The only effect is that they get 3 dots
     * behind their name in the menu. Alternatively, you can add "_PPP" to the
     * end of the ActionSource name.
     *
     * @param actionClass
     * @param actionSources
     */
    public static void putInteractive(final Class<? extends ExtendedAction> actionClass, final ActionSource... actionSources) {
        put(actionClass, actionSources);
        setInteractive(actionSources);
    }

    /**
     * Returns the ActionClass that is initialized for this ActionSource.
     */
    public default Class<? extends ExtendedAction> getActionClass() {
        return ACTION_CLASS.get(this);
    }

    /**
     *
     */
    public static final Set<ActionSource> INTERACTIVE_ACTIONS = new HashSet<>();

    /**
     * @param interactiveActionSources
     */
    public static void setInteractive(final ActionSource... interactiveActionSources) {
        INTERACTIVE_ACTIONS.addAll(Arrays.asList(interactiveActionSources));
    }

    /**
     * For all actions that require user interaction (e.g. via a dialog),
     * <code>true</code> should be returned here.
     *
     * @return
     */
    public default boolean isInteractiveAction() {
        return INTERACTIVE_ACTIONS.contains(this);
    }

    /**
     * @return the Object to identify the icon in the resources
     */
    public default Object getIconIdentifier() {
        return this;
    }
}
