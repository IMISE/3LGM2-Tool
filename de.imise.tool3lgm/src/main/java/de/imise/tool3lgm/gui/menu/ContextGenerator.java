package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;

/**
 * @author AXS (23.09.2019)
 */
public abstract class ContextGenerator implements ActionListener {

    /**
     * COMMENTME
     */
    private boolean controlled = false;

    /**
     *
     */
    public ContextGenerator() {
        setControlled(false);
    }

    /**
     * @param b
     */
    public final void setControlled(final boolean b) {
        controlled = b;
    }

    /**
     * @return
     */
    public final boolean isControlled() {
        return controlled;
    }

    /**
     * @param resKeyOrString
     * @param command
     * @param arguments
     * @param icon
     * @param enabled
     * @param toolTip
     * @return
     */
    protected final JMenuItem getItem(final String resKeyOrString, final GDCommands command, final String arguments, final ImageIcon icon, final boolean enabled, final String toolTip) {
        String label = null;
        label = Tool3lgmConstants.getResStringWithoutError(resKeyOrString);
        JMenuItem item = new JMenuItem(label, icon);
        item.addActionListener(this);
        if (arguments == null) {
            item.setActionCommand(command.toString());
        } else {
            item.setActionCommand(command + " " + arguments);
        }
        item.setEnabled(enabled);
        item.setToolTipText(toolTip);
        return item;
    }

    /**
     * @param resKeyOrString
     * @param command
     * @param arguments
     * @param icon
     * @return
     */
    protected final JMenuItem getItem(final String resKeyOrString, final GDCommands command, final String arguments, final ImageIcon icon) {
        return getItem(resKeyOrString, command, arguments, icon, true, null);
    }

    /**
     * @param resKey
     * @param command
     * @param arguments
     * @return
     */
    protected final JMenuItem getItem(final String resKey, final GDCommands command, final String arguments) {
        return getItem(resKey, command, arguments, null);
    }

    /**
     * @param resKey
     * @param command
     * @return
     */
    protected final JMenuItem getItem(final String resKey, final GDCommands command) {
        return getItem(resKey, command, null);
    }

    /**
     * @param command
     * @return
     */
    protected final JMenuItem getItem(final GDCommands command) {
        if (command.isModelOption()) {
            return new JCheckBoxMenuItem(command.createAction());
        }
        return getItem(command.name(), command);
    }

    /**
     * @param command
     * @return
     */
    protected final JMenuItem getItem(final Action action) {
        return new JMenuItem(action);
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        GraphDocument doc = getDoc();
        if (doc != null) {
            doc.exec(e.getActionCommand(), STANDARD_PID);
        }
    }

    protected void addMenuItem(final JPopupMenu menu, final JMenuItem item) {
        menu.add(item);
        Action action = item.getAction();
        item.setEnabled(action == null || action.isEnabled());
    }

    /**
     * @return
     */
    protected abstract GraphDocument getDoc();

    /**
     * @return
     */
    public abstract JPopupMenu getLayerContextMenu();

    /**
     * @param source
     * @return
     */
    public abstract JPopupMenu getNodeContextMenu(final Component source);

}
