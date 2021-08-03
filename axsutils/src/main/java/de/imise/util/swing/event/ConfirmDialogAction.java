package de.imise.util.swing.event;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.FocusManager;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * @author HUK (03.08.2021)
 *         enables key-shortcuts in opened dialogs
 */
public class ConfirmDialogAction extends AbstractAction {

    public static final ConfirmDialogAction CONFIRM_ACTION = new ConfirmDialogAction();

    @Override
    public void actionPerformed(final ActionEvent e) {
        FocusManager currentManager = javax.swing.FocusManager.getCurrentManager();
        Window activeWindow = currentManager.getActiveWindow();
        // checks if the current active window implements the ConfirmDialog interface
        // if so, the confirm action is executed
        if (activeWindow instanceof ConfirmDialog) {
            ConfirmDialog confirmDialog = (ConfirmDialog) activeWindow;
            confirmDialog.confirm();
        }
    }

    public static interface ConfirmDialog {

        public void confirm();

        /**
         * registers the CTRL+ENTER key-shortcut
         * used to click on confirm (the ok-button in most cases)
         */
        public default void registerCtrlEnterKey() {
            if (this instanceof JDialog) {
                JDialog dialog = (JDialog) this;
                JRootPane rootPane = dialog.getRootPane();
                InputMap im = rootPane.getInputMap();
                ActionMap am = rootPane.getActionMap();
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK), CONFIRM_ACTION);
                am.put(CONFIRM_ACTION, CONFIRM_ACTION);

                rootPane.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, im);
                rootPane.setActionMap(am);
            }
        }

    }
}
