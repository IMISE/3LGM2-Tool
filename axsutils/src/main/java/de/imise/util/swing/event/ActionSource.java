package de.imise.util.swing.event;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

/**
 * Interface das vorzugsweise an alle Enum-Klassen gehängt werden kann, die irgendwelche ActionIdentifiert
 * definieren. Damit können gleich die Actions definiert werden, wenn diese zur Initialisierung nur das
 * ActionSource-Object brauchen.
 *
 * @author AXS (10.03.2018)
 */
public interface ActionSource {

    public static final String PPP = "...";

    /**
     * Alle Actions, bei denen an den Namen im Menü noch 3 Punkte angehängt werden sollen, müssen im Namen ihres Enums am Ende diesen String haben.
     * Das sollten alle Actions sein, bei denen eine Interaktion des Nutzers bei der Durchführung der Action nötig ist, also ein Dialog aufgeht.
     */
    public static final String INTERACTIVE_ACTION_NAME_POSTFIX = "_PPP";

    /**
     * Mappt von einer ActionSource auf die Action-Klasse, die für diese ActionSource initialisiert werden soll.
     */
    public static final Map<ActionSource, Class<? extends ExtendedAction>> ACTION_CLASS = new HashMap<>();

    /**
     * Liefert die Action zu dieser ActionSource
     *
     * @return
     */
    public default ExtendedAction createAction() {
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
     * Speichert für die übergebenen ActionSources, welche ActionClass über die Methode {@link #getActionClass()} zurück
     * geliefert werden soll (wenn die default implementierung bestehen bleibt).
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
     * Actions bei denen irgendwie ein Dialog nach dem Aufruf angezeigt wird, sollten über diese put-Funktion geadded werden.
     * Einzige Auswirkung ist, dass sie im Menü 3 Punkte hinter ihren Namen bekommen. Alternativ kann man den Namen der ActionSource am Ende mit
     * "_PPP" versehen.
     *
     * @param actionClass
     * @param actionSources
     */
    public static void putInteractive(final Class<? extends ExtendedAction> actionClass, final ActionSource... actionSources) {
        put(actionClass, actionSources);
        setInteractive(actionSources);
    }

    /**
     * Liefert die ActionKlasse, die für diese ActionSource initialisiert wird.
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
     * Bei allen Actions, die eine Benutzerinteraktion (z.B. über einen Dialog vorraussetzen,
     * sollte hier <code>true</code> zurück kommen.
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
