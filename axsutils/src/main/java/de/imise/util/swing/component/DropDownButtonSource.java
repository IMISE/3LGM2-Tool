package de.imise.util.swing.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;

import de.imise.util.swing.event.ActionSource;
import de.imise.util.swing.event.ExtendedAction;
import de.imise.util.swing.menu.DynamicPopupMenu;

/**
 * @author AXS (31.01.2021)
 */
public class DropDownButtonSource implements ActionListener {

    /**
     *
     */
    private final JButton buttonComponent;

    /**
     *
     */
    private final boolean smallIcons;

    /**
     * @param actionSources
     */
    public DropDownButtonSource(final ActionSource... actionSources) {
        this(true, actionSources);
    }

    /**
     * @param smallIcons
     * @param actionSources The action sources whcih can create the button
     *            actions. If there is a <code>null</code> value in the list, so
     *            a separator will be added
     */
    public DropDownButtonSource(final boolean smallIcons, final ActionSource... actionSources) {
        JPopupMenu popupMenu = createDropDownMenu(actionSources);
        ExtendedAction action = actionSources[0].createAction();
        this.smallIcons = smallIcons;
        Icon icon = smallIcons ? action.getSmallIcon() : action.getLargeIcon();
        buttonComponent = DropDownButtonFactory.createDropDownButton(icon, popupMenu);
        buttonComponent.setAction(action);
        buttonComponent.setIcon(icon);
    }

    /**
     * @param actionSources The action sources whcih can create the button
     *            actions. If there is a <code>null</code> value in the list, so
     *            a separator will be added
     * @return
     */
    private JPopupMenu createDropDownMenu(final ActionSource[] actionSources) {
        if (actionSources.length < 2) {
            return null;
        }
        JPopupMenu popupMenu = new DynamicPopupMenu();
        for (ActionSource actionSource : actionSources) {
            if (actionSource == null) {
                popupMenu.addSeparator();
            } else {
                Action action = actionSource.createAction();
                JMenuItem popupMenuItem = popupMenu.add(action);
                popupMenuItem.addActionListener(this);
            }
        }
        return popupMenu;
    }

    /**
     * @return the buttonComponent
     */
    public final JButton getButtonComponent() {
        return buttonComponent;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        JMenuItem source = (JMenuItem) e.getSource();
        ExtendedAction action = (ExtendedAction) source.getAction();
        buttonComponent.setAction(action);
        Icon icon = smallIcons ? action.getSmallIcon() : action.getLargeIcon();
        buttonComponent.setIcon(icon);
        buttonComponent.setDisabledIcon(ImageUtilities.createDisabledIcon(icon));
    }

}
