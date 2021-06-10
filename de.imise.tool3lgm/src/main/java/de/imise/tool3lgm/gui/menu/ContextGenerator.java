package de.imise.tool3lgm.gui.menu;

import static de.imise.tool3lgm.Tool3lgmConstants.TOOLTIP_RESSOURCE_PREFIX;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.action.UserPropertyBooleanChangeAction;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.undoredo.CommandHandler;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.swing.event.ExtendedAction;
import de.imise.util.swing.menu.DynamicPopupMenu;
import de.imise.util.swing.menu.MenuScroller;

/**
 * @author AXS (23.09.2019)
 */
public abstract class ContextGenerator implements ActionListener {

    /**
     *
     */
    public static final String STANDARD_SUBITEMS_INDENTATION = "      ";

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
     * @return PopupMenu that will update the enabled states before become
     *         visible
     */
    public final JPopupMenu createUpdatingPopupMenu() {
        return createUpdatingPopupMenu(null);
    }

    /**
     * @param label
     * @return PopupMenu that will update the enabled states before become
     *         visible
     */
    public final JPopupMenu createUpdatingPopupMenu(final String label) {
        JPopupMenu menu = new JPopupMenu(label);
        menu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                //update the enabled states of the items
                Object source = e.getSource();
                if (source instanceof Container) {
                    Container menu = (Container) e.getSource();
                    checkEnabled(menu);
                    DynamicPopupMenu.removeUselessSeparators(menu);
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                //nothing to do
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {
                //nothing to do
            }
        });
        return menu;
    }

    /**
     * @param resKeyOrString
     * @return
     */
    protected String loadResStringWithoutError(final String resKeyOrString) {
        String string = null;
        GraphDocument doc = getDoc();
        if (doc != null) {
            ElementsNameBuilder elementsNameBuilder = doc.getElementsNameBuilder();
            string = elementsNameBuilder.getResStringWithoutError(resKeyOrString);
        } else {
            string = Tool3lgmConstants.getResStringWithoutError(resKeyOrString);
        }
        return string;
    }

    /**
     * @param item
     * @return
     */
    public final JMenuItem cloneItem(final JMenuItem item) {
        JMenuItem clone = new JMenuItem(item.getText(), item.getIcon());
        clone.setActionCommand(item.getActionCommand());
        clone.setEnabled(item.isEnabled());
        clone.addActionListener(this);
        return clone;
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
        return getItem(JMenuItem.class, resKeyOrString, command, arguments, icon, enabled, toolTip, false);
    }

    /**
     * @param itemType
     * @param resKeyOrString
     * @param command
     * @param arguments
     * @param icon
     * @param enabled
     * @param toolTip
     * @param selected
     * @return
     */
    private final <T extends JMenuItem> T getItem(final Class<T> itemType, final String resKeyOrString, final GDCommands command, final String arguments, final ImageIcon icon, final boolean enabled, String toolTip, final boolean selected) {
        String label = loadResStringWithoutError(resKeyOrString);
        T item = null;
        try {
            item = itemType.newInstance();
        } catch (Exception e) {
            e.printStackTrace(); //only relevant in development mode
        }
        if (item != null) {
            item.setText(label);
            item.setIcon(icon);
            item.addActionListener(this);
            if (arguments == null) {
                item.setActionCommand(command.toString());
            } else {
                item.setActionCommand(command + " " + arguments);
            }
            item.setEnabled(enabled);
            if (Strings.isNullOrEmpty(toolTip)) {
                String toolTipResKey = TOOLTIP_RESSOURCE_PREFIX + resKeyOrString;
                toolTip = Tool3lgmConstants.getResStringWithoutError(toolTipResKey);
                if (toolTipResKey == toolTip) {
                    toolTip = null;
                }
            } else {
                toolTip = Tool3lgmConstants.getResStringWithoutError(toolTip);
            }
            item.setToolTipText(toolTip);
            item.setSelected(selected);
        }
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
        ExtendedAction action = command.createAction();
        if (command.isModelOption()) {
            //            System.err.println(command.name() + " " + action.getClass() + " " + action.isEnabled());
            return new JCheckBoxMenuItem(action); //in this case the action can/should not be null!
        }
        if (action != null) {
            return new JMenuItem(action);
        }
        return getItem(command.name(), command);
    }

    /**
     * @param resKey
     * @param command
     * @return
     */
    protected final JMenuItem getItem(final BooleanProperty booleanProperty) {
        UserPropertyBooleanChangeAction action = booleanProperty.createAction();
        JCheckBoxMenuItem menuItem = action.createMenuItem();
        return menuItem;
    }

    /**
     * @param command
     * @return
     */
    protected static final JMenuItem getItem(final Action action) {
        return new JMenuItem(action);
    }

    /**
     * @param resKeyOrString
     * @param command
     * @param arguments
     * @param selected
     * @return
     */
    protected final JRadioButtonMenuItem getRadioItem(final String resKeyOrString, final boolean selected, final GDCommands command, final Object... arguments) {
        return getRadioItem(resKeyOrString, null, true, null, selected, command, arguments);
    }

    /**
     * @param resKeyOrString
     * @param toolTip
     * @param selected
     * @param command
     * @param arguments
     * @return
     */
    protected final JRadioButtonMenuItem getRadioItem(final String resKeyOrString, final String toolTip, final boolean selected, final GDCommands command, final Object... arguments) {
        return getRadioItem(resKeyOrString, null, true, toolTip, selected, command, arguments);
    }

    /**
     * @param resKeyOrString
     * @param command
     * @param arguments
     * @param icon
     * @param enabled
     * @param toolTip
     * @param selected
     * @return
     */
    protected final JRadioButtonMenuItem getRadioItem(final String resKeyOrString, final ImageIcon icon, final boolean enabled, final String toolTip, final boolean selected, final GDCommands command, final Object... arguments) {
        String argumentsString = CommandHandler.getArgumentsString(arguments);
        return getItem(JRadioButtonMenuItem.class, resKeyOrString, command, argumentsString, icon, enabled, toolTip, selected);
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        GraphDocument doc = getDoc();
        if (doc != null) {
            String actionCommand = e.getActionCommand();
            doc.exec(actionCommand, STANDARD_PID);
        }
    }

    /**
     * @param menu
     * @param item
     */
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

    /**
     * @param gdcoll
     * @return
     */
    protected final JMenuItem getCopyToModelMenu(final GDCollection gdcoll) {
        JMenu menu = new JMenu(gdcoll.getName());
        JMenuItem item = new JMenuItem(getResString("main_model"));
        LGMGraphDocument doc = (LGMGraphDocument) getDoc();
        item.addActionListener(e -> LGMGraphDocument.copySelectedToModel(doc, gdcoll.getMainDoc()));
        menu.add(item);
        for (final Szenario szen : gdcoll.getSzenarios()) {
            item = new JMenuItem(szen.getTitle());
            item.addActionListener(e -> LGMGraphDocument.copySelectedToModel(doc, szen));
            menu.add(item);
        }
        return menu;
    }

    /**
     * @param c
     */
    public static void checkEnabled(final Component c) {
        Component[] components;
        if (c instanceof JMenu) {
            components = ((JMenu) c).getMenuComponents();
        } else if (c instanceof JPopupMenu) {
            components = ((JPopupMenu) c).getComponents();
        } else if (c instanceof JMenuItem) {
            components = new Component[1];
            components[0] = c;
        } else {
            components = new Component[0];
        }

        for (Component comp : components) {
            if (comp instanceof JMenu) { // muss man vor JMenuItem testen, weil JMenu auch ein JMenuItem ist
                checkEnabled(comp);
            } else if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                Action action = item.getAction();
                if (action != null) {
                    boolean enabled = action.isEnabled();
                    item.setEnabled(enabled);
                }
            } else { //JPopupMenu und alles andere
                checkEnabled(comp);
            }
        }
    }

    /**
     * Sets a menu scroller for the given menu with pleasant values for most
     * cases.
     *
     * @param menu
     */
    protected static void setMenuScroller(final JMenu menu) {
        setMenuScroller(menu, 0, 0);
    }

    /**
     * Sets a menu scroller for the given menu with pleasant values for most
     * cases.
     *
     * @param menu
     * @param topFixedCount number of items before the scroller
     * @param bottomFixedCount number of items after the scroller
     */
    protected static void setMenuScroller(final JMenu menu, final int topFixedCount, final int bottomFixedCount) {
        int itemCount = menu.getItemCount();
        int maxSingelItemHeight = 1;
        for (int i = 0; i < itemCount; i++) {
            JMenuItem item = menu.getItem(i);
            Dimension preferredSize = item.getPreferredSize();
            if (preferredSize.height > maxSingelItemHeight) {
                maxSingelItemHeight = preferredSize.height;
            }
        }
        Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = defaultToolkit.getScreenSize();
        int maxItemCount = screenSize.height / maxSingelItemHeight - 3 - topFixedCount - bottomFixedCount;
        if (itemCount > maxItemCount) {
            int interval = 125;
            MenuScroller.setScrollerFor(menu, maxItemCount, interval, topFixedCount, bottomFixedCount);
        }
    }

}
