package de.imise.util.swing.component.tab;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTabbedPane;

/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 * To add a tab, use the method addTab(String, Component)
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file type) use
 * the method addTab(String, Component, Icon). Only clicking the 'X' closes the tab.
 */
public class JTabbedPaneWithCloseIconsRight extends JTabbedPane {

    /**
     *
     */
    private Color activeForegroundColor;

    /**
     * @param activeForegroundColor
     */
    public JTabbedPaneWithCloseIconsRight(final Color activeForegroundColor) {
        this.activeForegroundColor = activeForegroundColor;
    }

    @Override
    public void addTab(final String title, final Component component) {
        this.addTab(title, null, component);
        initLastTabComponent();
    }

    @Override
    public void addTab(final String title, final Icon extraIcon, final Component component) {
        super.addTab(title, extraIcon, component);
        initLastTabComponent();
    }

    @Override
    public void addTab(final String title, final Icon extraIcon, final Component component, final String tooltip) {
        super.addTab(title, extraIcon, component, tooltip);
        initLastTabComponent();
    }

    /**
     *
     */
    private void initLastTabComponent() {
        //without this border sometimes a Nullpointer occurs at the
        //start if the tool loads a model via start parameter2911
        setBorder(BorderFactory.createEmptyBorder());
        int lastTabComponentIndex = getTabCount() - 1;
        setTabComponentAt(lastTabComponentIndex, new FlexibleTabPaneTab(this, activeForegroundColor));
    }

    /**
     * @return
     */
    public Color getActiveForegroundColor() {
        return activeForegroundColor;
    }

    /**
     * @param activeForegroundColor
     */
    public void setActiveForegroundColor(final Color activeForegroundColor) {
        this.activeForegroundColor = activeForegroundColor;
    }

}