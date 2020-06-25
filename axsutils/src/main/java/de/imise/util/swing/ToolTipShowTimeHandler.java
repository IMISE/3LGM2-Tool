package de.imise.util.swing;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

/**
 * @author AXS (25.06.2020)
 */
public class ToolTipShowTimeHandler extends MouseAdapter {

    private final int oldDismissTime = ToolTipManager.sharedInstance().getDismissDelay();

    private final int currentDismissTime;

    private ToolTipShowTimeHandler(final int currentDismissTime, final Component component) {
        this.currentDismissTime = currentDismissTime;
        component.addMouseListener(this);
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        ToolTipManager.sharedInstance().setDismissDelay(currentDismissTime);
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        ToolTipManager.sharedInstance().setDismissDelay(oldDismissTime);
    }

    /**
     * @param component the component with the given tooltip dismiss time
     * @param dismissTime the dismiss time in milli seconds
     */
    public static final void setDismissTime(final Component component, final int dismissTime) {
        new ToolTipShowTimeHandler(dismissTime, component);
    }

}
