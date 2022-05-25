/**
 *
 */
package de.imise.tool3lgm.gui;

import java.awt.Component;

import de.imise.tool3lgm.Static;

/**
 * Class that starts a Thread. These Thread checks if a Component or a sub
 * component of a {@link TitledBorderHigligter} has the focus. If yes then the
 * border highlight will be updated.
 *
 * @author AXS (24.05.2022)
 */
public class GUIFocusContextManager {

    /**
     *
     */
    private static Thread focusOwnerChangeListener = null;

    /**
     *
     */
    private static TitledBorderHigligter currentFocusedTitledBorderComponent;

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

            private Component lastFocusOwner = null;

            @Override
            public void run() {
                while (true) {
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
                                        break;
                                    }
                                    lastFocusOwnerOrParent = lastFocusOwnerOrParent.getParent();
                                }
                            }
                        }
                    }
                    try {
                        sleep(200);
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

}
