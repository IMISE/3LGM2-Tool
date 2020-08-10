package de.imise.util.swing.component.tab;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 * To add a tab, use the method addTab(String, Component)
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file type) use
 * the method addTab(String, Component, Icon). Only clicking the 'X' closes the tab.
 */
public class JTabbedPaneWithCloseIconsRight extends JTabbedPane {

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
        int lastTabComponentIndex = getTabCount() - 1;
        setTabComponentAt(lastTabComponentIndex, new FlexibleTabPaneTab(this));
    }

}