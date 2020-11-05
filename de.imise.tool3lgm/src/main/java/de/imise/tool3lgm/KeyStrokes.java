package de.imise.tool3lgm;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import de.imise.tool3lgm.event.ActionLibrary.AnalysisActions;
import de.imise.tool3lgm.event.ActionLibrary.EditActions;
import de.imise.tool3lgm.event.ActionLibrary.FileActions;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.util.collections.CollectionUtils;

public class KeyStrokes {

    private static final Map<Action, KeyStroke> KEYSTROKES = new HashMap<>();

    public static final int MENU_SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    static {
        put(FileActions.ACTION_NEW_MODEL, KeyEvent.VK_N, MENU_SHORTCUT_KEY_MASK);
        put(FileActions.ACTION_OPEN_MODEL, KeyEvent.VK_O, MENU_SHORTCUT_KEY_MASK);
        put(FileActions.ACTION_SAVE_MODEL, KeyEvent.VK_S, MENU_SHORTCUT_KEY_MASK);
        put(GDCommands.MODEL_ACTION_DELETE, KeyEvent.VK_DELETE, 0);
        put(EditActions.ACTION_REDO, KeyEvent.VK_Y, MENU_SHORTCUT_KEY_MASK);
        put(EditActions.ACTION_UNDO, KeyEvent.VK_Z, MENU_SHORTCUT_KEY_MASK);
        put(EditActions.SELECT_ALL, KeyEvent.VK_A, MENU_SHORTCUT_KEY_MASK);
        put(GDCommands.MODEL_ACTION_COPY, KeyEvent.VK_C, MENU_SHORTCUT_KEY_MASK);
        put(GDCommands.MODEL_ACTION_CUT, KeyEvent.VK_X, MENU_SHORTCUT_KEY_MASK);
        put(GDCommands.MODEL_ACTION_PASTE, KeyEvent.VK_V, MENU_SHORTCUT_KEY_MASK);
        put(EditActions.ACTION_SEARCH, KeyEvent.VK_F, MENU_SHORTCUT_KEY_MASK);
        put(AnalysisActions.ACTION_ANALYSIS_OPEN_REPOSITORY, KeyEvent.VK_F7, 0);
        put(AnalysisActions.ACTION_ANALYSIS_OPEN_EDITOR, KeyEvent.VK_F9, 0);
        put(AnalysisActions.ACTION_ANALYSIS_RESET_RESULT, KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK);

    }

    private static void put(final GDCommands command, final int keyCode, final int modifiers) {
        KEYSTROKES.put(command.createAction(), KeyStroke.getKeyStroke(keyCode, modifiers));
    }

    private static void put(final Action action, final int keyCode, final int modifiers) {
        KEYSTROKES.put(action, KeyStroke.getKeyStroke(keyCode, modifiers));
    }

    /**
     * Ermöglicht das Auslösen der in {@link Tool3lgmConstants} festgelegten
     * Aktionen durch die jeweiligen {@link KeyStroke}s im gesamten Tool.
     */
    public static final void registerPublicKeyStrokes(final JComponent component, final KeyStroke... ingnoreStrokes) {
        InputMap im = component.getInputMap();
        ActionMap am = component.getActionMap();
        for (Action action : KEYSTROKES.keySet()) {
            KeyStroke keyStroke = KEYSTROKES.get(action);
            if (!CollectionUtils.arrayContains(ingnoreStrokes, keyStroke)) {
                im.put(KEYSTROKES.get(action), action);
            }
            am.put(action, action);
        }
        component.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
        component.setActionMap(am);
    }

}
