package de.imise.util.swing.component;

import javax.swing.JToolBar;

/**
 * @author Thomas Rudert
 *         Abstrakte Klasse für alle Werkzeugleisten, die nicht floatable sein sollen.
 */
public abstract class UnfloatableToolBar extends JToolBar {

    public UnfloatableToolBar() {
        setFloatable(false);
    }

    public abstract void update();
}
