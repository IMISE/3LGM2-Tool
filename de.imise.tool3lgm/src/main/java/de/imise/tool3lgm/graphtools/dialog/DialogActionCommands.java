package de.imise.tool3lgm.graphtools.dialog;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.util.swing.event.ActionSource;

/**
 * Action command identifier for dialog actions with icons.
 * Dialog action without icons but only text should be
 * {@link LGMAction}s which are initialised with the text
 * to be displayed.
 *
 * @author AXS (15.02.2020)
 */
public enum DialogActionCommands implements ActionSource {

    ACTION_DIALOG_CONNECT_ELEMENT,
    ACTION_DIALOG_DISCONNECT_ELEMENT,
    ACTION_DIALOG_MOVE_CONNECTION_STEP_UP,
    ACTION_DIALOG_MOVE_CONNECTION_STEP_DOWN,
    ACTION_DIALOG_NEW_ELEMENT, // Neues Element anlegen
    ACTION_DIALOG_RIGHT_SIDE_HIDE, // Rechte Seite verbergen
    ACTION_DIALOG_RIGHT_SIDE_SHOW, //Rechte Seite einblenden

}
