package de.imise.util.swing.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

public interface ActionSource {

    public static final String PPP = "...";

    public static final Map<ActionSource, Class<? extends Action>> ACTION_CLASS = new HashMap<>();

    public default Action getAction() {
        Class<? extends Action> actionClass = getActionClass();
        Class<?> constructorParameterClass = getClass();
        Action action = null;
        while (constructorParameterClass != null) {
            try {
                action = actionClass.getConstructor(constructorParameterClass).newInstance(this);
                break;
            } catch (Exception e) {
                constructorParameterClass = constructorParameterClass.getSuperclass();
            }
        }
        if (isInteractiveAction()) {
            action.putValue(Action.NAME, action.getValue(Action.NAME).toString() + PPP);
        }
        return action;
    }

    public static void put(final Class<? extends Action> actionClass, final ActionSource... actionSources) {
        for (ActionSource actionSource : actionSources) {
            ACTION_CLASS.put(actionSource, actionClass);
        }
    }

    public default Class<? extends Action> getActionClass() {
        return ACTION_CLASS.get(this);
    }

    public static final Set<ActionSource> INTERACTIVE_ACTIONS = new HashSet<>();

    /**
     * Bei allen Actions, die eine Benutzerinteraktion (z.B. über einen Dialog vorraussetzen,
     * sollte hier <code>true</code> zurück kommen.
     *
     * @return
     */
    public default boolean isInteractiveAction() {
        return INTERACTIVE_ACTIONS.contains(this);
    }

}
