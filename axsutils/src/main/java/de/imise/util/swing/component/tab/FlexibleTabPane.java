package de.imise.util.swing.component.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

/**
 * A JTabbedPane which can have a close ('X') icon on each tab. To add a tab,
 * use the method addTab(String, Component) To have an extra icon on each tab
 * (e.g. like in JBuilder, showing the file type) use the method addTab(String,
 * Component, Icon). Only clicking the 'X' closes the tab.
 */
public class FlexibleTabPane extends JTabbedPane {

    /**
     *
     */
    private final Color activeTabForegroundColor;

    /**
     *
     */
    private final int activeTabFontStyle;

    /**
     *
     */
    private final boolean withCloseButtons;

    /**
     *
     */
    public FlexibleTabPane() {
        this(false);
    }

    /**
     * @param tabPlacement
     */
    public FlexibleTabPane(final int tabPlacement) {
        this();
        setTabPlacement(tabPlacement);
    }

    /**
     * @param tabPlacement
     * @param tabLayoutPolicy
     */
    public FlexibleTabPane(final int tabPlacement, final int tabLayoutPolicy) {
        this();
        setTabPlacement(tabPlacement);
        setTabLayoutPolicy(tabLayoutPolicy);
    }

    /**
     * Creates a tab pane with standard foreground color and a plain font.
     *
     * @param withCloseButtons if <code>true</code> there are close buttons on
     *            the right side of the tab, which close the tab via
     *            {@link JTabbedPane#remove(int)} which subclasses can override
     *            to react on this event.
     */
    public FlexibleTabPane(final boolean withCloseButtons) {
        this(UIManager.getColor("TabbedPane.foreground"), Font.PLAIN, withCloseButtons);
    }

    /**
     * @param withCloseButtons if <code>true</code> there are close buttons on
     *            the right side of the tab, which close the tab via
     *            {@link JTabbedPane#remove(int)} which subclasses can override
     *            to react on this event.
     * @param activeTabForegroundColor foregroud color of the active atb
     * @param activeTabFontStyle {@link Font#PLAIN} or {@link Font#BOLD} or
     *            {@link Font#BOLD} or {@link Font#BOLD} + {@link Font#ITALIC}
     */
    public FlexibleTabPane(final Color activeTabForegroundColor, final int activeTabFontStyle) {
        this(activeTabForegroundColor, activeTabFontStyle, false);
    }

    /**
     * @param activeTabForegroundColor foregroud color of the active atb
     * @param activeTabFontStyle {@link Font#PLAIN} or {@link Font#BOLD} or
     *            {@link Font#BOLD} or {@link Font#BOLD} + {@link Font#ITALIC}
     * @param withCloseButtons if <code>true</code> there are close buttons on
     *            the right side of the tab, which close the tab via
     *            {@link JTabbedPane#remove(int)} which subclasses can override
     *            to react on this event.
     */
    public FlexibleTabPane(final Color activeTabForegroundColor, final int activeTabFontStyle, final boolean withCloseButtons) {
        this.withCloseButtons = withCloseButtons;
        this.activeTabForegroundColor = activeTabForegroundColor;
        this.activeTabFontStyle = activeTabFontStyle;
    }

    /**
     * @param tabPlacement see {@link JTabbedPane#setTabPlacement(int)}
     * @param activeTabForegroundColor foregroud color of the active atb
     * @param activeTabFontStyle {@link Font#PLAIN} or {@link Font#BOLD} or
     *            {@link Font#BOLD} or {@link Font#BOLD} + {@link Font#ITALIC}
     * @param withCloseButtons if <code>true</code> there are close buttons on
     *            the right side of the tab, which close the tab via
     *            {@link JTabbedPane#remove(int)} which subclasses can override
     *            to react on this event.
     */
    public FlexibleTabPane(final int tabPlacement, final Color activeTabForegroundColor, final int activeTabFontStyle, final boolean withCloseButtons) {
        super(tabPlacement);
        this.withCloseButtons = withCloseButtons;
        this.activeTabForegroundColor = activeTabForegroundColor;
        this.activeTabFontStyle = activeTabFontStyle;
    }

    /**
     * @param tabPlacement see {@link JTabbedPane#setTabPlacement(int)}
     * @param tabLayoutPolicy {@link JTabbedPane#setTabLayoutPolicy(int)}
     * @param activeTabForegroundColor foregroud color of the active atb
     * @param activeTabFontStyle {@link Font#PLAIN} or {@link Font#BOLD} or
     *            {@link Font#BOLD} or {@link Font#BOLD} + {@link Font#ITALIC}
     * @param withCloseButtons if <code>true</code> there are close buttons on
     *            the right side of the tab, which close the tab via
     *            {@link JTabbedPane#remove(int)} which subclasses can override
     *            to react on this event.
     */
    public FlexibleTabPane(final int tabPlacement, final int tabLayoutPolicy, final Color activeTabForegroundColor, final int activeTabFontStyle, final boolean withCloseButtons) {
        super(tabPlacement, tabLayoutPolicy);
        this.withCloseButtons = withCloseButtons;
        this.activeTabForegroundColor = activeTabForegroundColor;
        this.activeTabFontStyle = activeTabFontStyle;
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
        Icon tabIcon = getIconAt(lastTabComponentIndex);
        setTabComponentAt(lastTabComponentIndex, new FlexibleTabPaneTab(this, tabIcon, activeTabForegroundColor, activeTabFontStyle, withCloseButtons));
    }

    @Override
    public void setIconAt(final int index, final Icon icon) {
        Component tabComponent = getTabComponentAt(index);
        if (!(tabComponent instanceof FlexibleTabPaneTab)) {
            super.setIconAt(index, icon);
            return;
        }
        FlexibleTabPaneTab tab = (FlexibleTabPaneTab) tabComponent;
        tab.setIcon(icon);
    }

}