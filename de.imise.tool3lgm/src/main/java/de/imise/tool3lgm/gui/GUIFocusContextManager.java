/**
 *
 */
package de.imise.tool3lgm.gui;

import java.awt.Component;

import de.imise.tool3lgm.Static;

/**
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
     *
     */
    private static Thread startFocusThread() {
        Thread focusOwnerChangeListener = new Thread() {

            private Component lastFocusOwner = null;

            private TitledBorderHigligter lastTitledBorder = null;

            @Override
            public void run() {
                while (true) {
                    MainFrame mainFrame = Static.getMainFrame();
                    if (mainFrame != null) {
                        Component focusOwner = mainFrame.getFocusOwner();
                        if (focusOwner != lastFocusOwner) {
                            if (lastTitledBorder != null) {
                                lastTitledBorder.removeHighlight();
                            }
                            lastFocusOwner = focusOwner;
                            while (lastFocusOwner != null) {
                                if (lastFocusOwner instanceof TitledBorderHigligter) {
                                    lastTitledBorder = (TitledBorderHigligter) lastFocusOwner;
                                    lastTitledBorder.setHighlight();
                                    break;
                                }
                                lastFocusOwner = lastFocusOwner.getParent();
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
