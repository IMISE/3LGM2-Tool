package de.imise.tool3lgm.gui;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import de.imise.tool3lgm.event.ActionIdentifier;
import de.imise.tool3lgm.event.action.GraphDocumentAction;

/**
 * @author AXS (29.01.2020)
 */
public class LastAndNextViewManager {

    /** List of all InternalFrames in the order they were active */
    private static final List<AbstractInternalFrame> windowList = new ArrayList<>();

    /** if true blocks the circular update */
    private static boolean operatingWindowList = false;

    private static int windowIndex = -1;

    /**
     *
     */
    private LastAndNextViewManager() {
        // no instances allowd
    }

    /** Activates the view which was active before the actual view became activated */
    public static final Action ACTION_GOTO_PREVIOUS_VIEW = new GraphDocumentAction(ActionIdentifier.ACTION_GOTO_PREVIOUS_VIEW) {
        @Override
        protected void actionPerformed() {
            operatingWindowList = true;
            AbstractInternalFrame f = getPreviousWindow();
            if (f != null) {
                try {
                    f.setSelected(true);
                } catch (Exception ex) {
                    //do nothing because it's not critical
                    //Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
                }
            }
            operatingWindowList = false;
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && windowIndex > 0;
        }

    };

    /** Activates the view which was active before the last ACTION_GOTO_PREVIOUS_VIEW was performed */
    public static final Action ACTION_GOTO_NEXT_VIEW = new GraphDocumentAction(ActionIdentifier.ACTION_GOTO_NEXT_VIEW) {
        @Override
        protected void actionPerformed() {
            operatingWindowList = true;
            AbstractInternalFrame f = getNextWindow();
            if (f != null) {
                try {
                    f.setSelected(true);
                } catch (Exception ex) {
                    //do nothing because it's not critical
                    //Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
                }
            }
            operatingWindowList = false;
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && windowIndex < windowList.size() - 1;
        }

    };

    /**
     * @param frame
     */
    public static void addWindow(final AbstractInternalFrame frame) {
        if (operatingWindowList) {
            return;
        }
        if (frame == null) {
            return;
        }
        if (windowIndex < 0 || windowList.get(windowIndex) != frame) {
            for (int i = windowList.size() - 1; i > windowIndex; i--) {
                windowList.remove(i);
            }
            windowIndex++;
            if (windowIndex >= windowList.size()) {
                windowList.add(frame);
            } else {
                windowList.add(windowIndex, frame);
            }
        }
    }

    /**
     * @param frame
     */
    public static void removeWindow(final AbstractInternalFrame frame) {
        if (operatingWindowList) {
            return;
        }
        int index = windowList.indexOf(frame);
        while (index >= 0) {
            windowList.remove(index);
            if (windowIndex >= index) {
                windowIndex--;
            }
            index = windowList.indexOf(frame);
        }
        if (windowIndex < -1) {
            windowIndex = -1;
        }
        if (windowIndex >= windowList.size()) {
            windowIndex = windowList.size() - 1;
        }

    }

    /**
     *
     */
    public static void selectLastFrame() {
        AbstractInternalFrame lastFrame = getNextWindow();
        if (lastFrame == null) {
            lastFrame = getPreviousWindow();
        }
        if (lastFrame != null) {
            try {
                lastFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
            }
        }
    }

    /**
     * @return
     */
    private static AbstractInternalFrame getNextWindow() {
        if (windowIndex < 0 || windowIndex >= windowList.size() - 1) {
            return null;
        }
        AbstractInternalFrame retVal = windowList.get(windowIndex + 1);
        if (windowIndex < windowList.size() - 1) {
            windowIndex++;
        }
        return retVal;
    }

    /**
     * @return
     */
    private static AbstractInternalFrame getPreviousWindow() {
        if (windowIndex <= 0 || windowIndex > windowList.size()) {
            return null;
        }
        AbstractInternalFrame retVal = windowList.get(windowIndex - 1);
        if (windowIndex >= 0) {
            windowIndex--;
        }
        return retVal;
    }

}
