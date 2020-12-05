package de.imise.util.event;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import de.imise.util.OperatingSystem;

/**
 * @author AXS (01.04.2020)
 */
public class InputEvents {

    /**
     * @param mouseEvent the mouse event
     * @param key the key that is to test if it is pressed
     * @return <code>true</code> if the key was pressed during the mouse event
     */
    public static boolean isKeyPressed(final MouseEvent mouseEvent, final KeyEvent key) {
        int modifiers = mouseEvent.getModifiers();
        int keyPressed = modifiers & key.getModifiers();
        return keyPressed != 0;
    }

    /**
     * @param event
     * @return <code>true</code> if the CTRL-Key is pressed on Windows or MacOS
     *         or Linux OR the CMD-Key is pressed on Mac. On MacOS both -
     *         CTRL-Key and CMD-Key return <code>true</code>.
     */
    public static final boolean isOperatingSystemDependentCTRLorCMDdown(final InputEvent event) {
        return isOperatingSystemDependentCTRLorCMDorSHIFTdown(event, false);
    }

    /**
     * @param event
     * @return <code>true</code> if Shift and/or the CTRL-Key is pressed on
     *         Windows or MacOS or Linux OR the CMD-Key is pressed on Mac. On
     *         MacOS both - CTRL-Key and CMD-Key return <code>true</code>.
     */
    public static final boolean isOperatingSystemDependentCTRLorCMDorSHIFTdown(final InputEvent event) {
        return isOperatingSystemDependentCTRLorCMDorSHIFTdown(event, true);
    }

    /**
     * @param event
     * @param checkShift if <code>true</code> the function also returns
     *            <code>true</code> if SHIFT is pressed. If <code>false</code>
     *            it doesn't matter if SIHFT ist pressed.
     * @return <code>true</code> if SHIFT and/or the CTRL-Key is pressed on
     *         Windows or MacOS or Linux OR the CMD-Key is pressed on Mac. On
     *         MacOS both - CTRL-Key and CMD-Key return <code>true</code>.
     */
    private static final boolean isOperatingSystemDependentCTRLorCMDorSHIFTdown(final InputEvent event, final boolean checkShift) {
        int modifiers = event.getModifiers();
        int ctrlPressed = modifiers & InputEvent.CTRL_MASK;
        if (ctrlPressed == 0) {
            boolean isMacOs = OperatingSystem.isMacOs();
            if (isMacOs) {
                ctrlPressed = modifiers & InputEvent.META_MASK;
            }
            if (ctrlPressed == 0 && checkShift) {
                ctrlPressed = modifiers & InputEvent.SHIFT_MASK;
            }
        }
        return ctrlPressed != 0;
    }

}
