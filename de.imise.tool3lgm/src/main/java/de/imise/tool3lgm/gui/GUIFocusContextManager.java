/**
 *
 */
package de.imise.tool3lgm.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import de.imise.tool3lgm.Static;

/**
 * Class that starts a Thread. These Thread checks if a Component or a sub
 * component of a {@link TitledBorderHigligter} has the focus. If yes then the
 * border highlight will be updated.
 *
 * @author AXS (24.05.2022)
 */
public class GUIFocusContextManager {

    /**  */
    private static Thread focusOwnerChangeListener = null;

    /**  */
    private static TitledBorderHigligter currentFocusedTitledBorderComponent;

    /**  */
    private static Component lastFocusOwner = null;

    /**
     *
     */
    private GUIFocusContextManager() {
    }

    /**
     *
     */
    public static final void start() {
        if (focusOwnerChangeListener == null || !focusOwnerChangeListener.isAlive()) {
            focusOwnerChangeListener = startFocusThread();
        }
    }

    /**
     * @param defaultFocusedComponent
     */
    public static final void setDefaultFocusedComponent(TitledBorderHigligter defaultFocusedComponent) {
        if (currentFocusedTitledBorderComponent == null) {
            currentFocusedTitledBorderComponent = defaultFocusedComponent;
            currentFocusedTitledBorderComponent.setHighlight();
        }
    }

    /**
     *
     */
    private static Thread startFocusThread() {
        Thread focusOwnerChangeListener = new Thread() {

            @Override
            public void run() {
                while (true) {
                    checkFocusChanged();
                    //if we don't sleep here at least 1 ms then the behaviour is not correct (the context will not be changed)
                    //but longer sleeps cause no visible context change if we open a context menu. The focus switch and repaint
                    //of the focus borders must be faster than the opening of the context menu.
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        };

        try {
            focusOwnerChangeListener.setPriority(Thread.MIN_PRIORITY);
        } catch (Exception e) {
            // ignore
        }

        focusOwnerChangeListener.start();
        return focusOwnerChangeListener;
    }

    /**
     *
     */
    private static void checkFocusChanged() {
        //no model is opened
        if (Static.getSelectedGDCollection() == null) {
            if (currentFocusedTitledBorderComponent != null) {
                currentFocusedTitledBorderComponent.removeHighlight();
                currentFocusedTitledBorderComponent = null;
                lastFocusOwner = null;
            }
        } else {
            MainFrame mainFrame = Static.getMainFrame();
            if (mainFrame != null) {
                Component focusOwner = mainFrame.getFocusOwner();
                if (focusOwner != lastFocusOwner) {
                    lastFocusOwner = focusOwner;
                    Component lastFocusOwnerOrParent = focusOwner;
                    while (lastFocusOwnerOrParent != null) {
                        if (lastFocusOwnerOrParent instanceof TitledBorderHigligter) {
                            if (currentFocusedTitledBorderComponent != null) {
                                currentFocusedTitledBorderComponent.removeHighlight();
                            }
                            currentFocusedTitledBorderComponent = (TitledBorderHigligter) lastFocusOwnerOrParent;
                            currentFocusedTitledBorderComponent.setHighlight();
                            mainFrame.revalidate();
                            mainFrame.repaint();
                            break;
                        }
                        lastFocusOwnerOrParent = lastFocusOwnerOrParent.getParent();
                    }
                }
            }
        }

    }

    /**
     * @param c
     */
    public static void setFocus(Object c) {
        if (c instanceof Component) {
            ((Component) c).requestFocus();
            checkFocusChanged();
        }
    }

    /**
     * A PopupMenu that updates the clicked Component to ensure the context
     * knows the correct component.
     *
     * @author AXS (25.05.2022)
     */
    public static class SetFocusToClickedComponentPopupMenu extends JPopupMenu {

        /**
         * @param label
         */
        public SetFocusToClickedComponentPopupMenu(String label) {
            super(label);
        }

        @Override
        public void setLocation(int x, int y) { // This function is called before the popup will become visible
            MainFrame mainFrame = Static.getMainFrame();
            Container contentPane = mainFrame.getContentPane();
            Component componentAt = SwingUtilities.getDeepestComponentAt(contentPane, x, y);
            setFocus(componentAt);
            super.setLocation(x, y);
        }

    }

    /**
     * A {@link MouseListener} that sets the focus to the clicked component.
     */
    public static final MouseListener SET_FOCUS_TO_CLICKED_COMPONENT_MOUSE_LISTENER = new MouseAdapter() {

        @Override
        public void mouseReleased(MouseEvent e) {
            setFocus(e.getSource());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            setFocus(e.getSource());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            setFocus(e.getSource());
        }
    };

    /**
     * @return
     */
    public static Component getLastFocusOwner() {
        return lastFocusOwner;
    }

    /**
     * @return
     */
    public static TitledBorderHigligter getCurrentFocusedTitledBorderComponent() {
        return currentFocusedTitledBorderComponent;
    }
}
