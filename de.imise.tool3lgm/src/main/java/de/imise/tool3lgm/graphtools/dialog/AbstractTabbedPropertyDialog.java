package de.imise.tool3lgm.graphtools.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.dialog.element.panel.MultiPanelElementDialogPanel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.userfield.definition.UserField;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.panel.PropertyDialogUserFieldPanel;
import de.imise.util.swing.component.tab.ReorderableTabbedPane;

/**
 * @author AXS (15.02.2018)
 */
public abstract class AbstractTabbedPropertyDialog extends AbstractPropertyDialog {

    /** TabbedPane in das alle Panels kommen */
    protected final ReorderableTabbedPane tabbedPane;

    /**
     * @param gdcoll
     */
    public AbstractTabbedPropertyDialog(final GDCollection gdcoll) {
        super(gdcoll);
        tabbedPane = new ReorderableTabbedPane();
    }

    /**
     * @param owner
     * @param gdcoll
     */
    public AbstractTabbedPropertyDialog(final Frame owner, final GDCollection gdcoll) {
        super(owner, gdcoll);
        tabbedPane = new ReorderableTabbedPane();
    }

    /**
     * @param owner
     * @param gdcoll
     */
    public AbstractTabbedPropertyDialog(final Dialog owner, final GDCollection gdcoll) {
        super(owner, gdcoll);
        tabbedPane = new ReorderableTabbedPane();
    }

    /**
     * Fügt dem Dialog einen neuen Tab mit der übergebenen Componente hinzu. Der
     * Titel des Tabs ist der wird über die getName()-Funktion der Componente
     * ermittelt.
     *
     * @param component
     */
    protected void addTab(final Component component) {
        addTab(component.getName(), component);
    }

    /**
     * Fügt dem Dialog einen neuen Tab mit dem übergebenen Titel und der
     * Komponente hinzu.
     *
     * @param title
     * @param component
     */
    public void addTab(final String title, final Component component) {
        addTab(title, null, component);
    }

    /**
     * Fügt dem Dialog einen neuen Tab mit dem übergebenen Titel, Icon und der
     * Komponente hinzu.
     *
     * @param title
     * @param icon
     * @param component
     */
    protected void addTab(final String title, final Icon icon, final Component component) {
        tabbedPane.addTab(title, icon, component);
    }

    /**
     * Removes the tab with the given index.
     *
     * @param tabIndex index of the tab to remove
     */
    protected void removeTab(final int tabIndex) {
        tabbedPane.remove(tabIndex);
    }

    /**
     * Sets the icon of the last tab in the contained tabbed pane.
     *
     * @param icon new icon of the last tab
     */
    public void setLastTabIcon(final Icon icon) {
        setTabIcon(tabbedPane.getTabCount() - 1, icon);
    }

    /**
     * Sets the title of the last tab in the contained tabbed pane.
     *
     * @param title new title of the last tab
     */
    public void setLastTabTitle(final String title) {
        setTabTitle(tabbedPane.getTabCount() - 1, title);
    }

    /**
     * Delegates {@link JTabbedPane#setIAt(int, Icon)} of the contained tabbed
     * pane.
     *
     * @param tabIndex
     * @param icon
     */
    public void setTabIcon(final int tabIndex, final Icon icon) {
        tabbedPane.setIconAt(tabIndex, icon);
    }

    /**
     * Delegates {@link JTabbedPane#setTitleAt(int, String)} of the contained
     * tabbed pane.
     *
     * @param tabIndex
     * @param title
     */
    public void setTabTitle(final int tabIndex, final String title) {
        tabbedPane.setTitleAt(tabIndex, title);
    }

    /**
     * Selects the last tab
     */
    public void selectLastTab() {
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

    }

    /**
     * Selects the first tab with an {@link PropertyDialogUserFieldPanel} which
     * contains the given userfield.
     *
     * @param tabComponentClass
     * @return index of the selected tab
     * @see #selectTab(String, Class)
     */
    public int selectTab(final UserField userField) {
        for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
            Component comp = tabbedPane.getComponent(i);
            if (comp instanceof PropertyDialogUserFieldPanel) {
                if (((PropertyDialogUserFieldPanel) comp).hasUserField(userField)) {
                    tabbedPane.setSelectedIndex(i);
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @return the {@link ElementDialogPanel} in the active tab of the contained
     *         tabbed pane
     */
    public ElementDialogPanel getSelectedElementDialogPanel() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        return selectedComponent instanceof ElementDialogPanel ? (ElementDialogPanel) selectedComponent : null;
    }

    /**
     * Selects the with a panel or subpanel that displays the given MetaPath.
     *
     * @param metaPath
     */
    public final int selectTab(final MetaPath metaPath) {
        Container panel = getPanel(tabbedPane, metaPath);
        while (panel != null) {
            Container parent = panel.getParent();
            if (parent == tabbedPane) {
                tabbedPane.setSelectedComponent(panel);
                return tabbedPane.getSelectedIndex();
            }
            panel = parent;
        }
        return -1;
    }

    /**
     * Searches for a subpanel in the given container that displays the given
     * MetaPath.
     *
     * @param pane
     * @param metaPath
     * @return
     */
    public static AbstractPathConnectionPanel getPanel(final Container pane, final MetaPath metaPath) {
        for (int i = 0; i < pane.getComponentCount(); i++) {
            Component comp = pane.getComponent(i);
            if (comp instanceof AbstractPathConnectionPanel) {
                AbstractPathConnectionPanel panel = (AbstractPathConnectionPanel) comp;
                if (panel.hasMetaPath(metaPath)) {
                    return panel;
                }
            } else if (comp instanceof MultiPanelElementDialogPanel) {
                MultiPanelElementDialogPanel panel = (MultiPanelElementDialogPanel) comp;
                AbstractPathConnectionPanel subpanel = getPanel(panel, metaPath);
                if (subpanel != null) {
                    return subpanel;
                }
            }
        }
        return null;
    }

    /**
     * @return a list of all tab names in the order of the tabs added to the
     *         contained tabbed pane
     */
    public List<String> getTabNames() {
        ImmutableList.Builder<String> tabNames = ImmutableList.builder();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            tabNames.add(title);

        }
        return tabNames.build();
    }

    /**
     * @return delegates {@link JTabbedPane#getTabCount()} of the contained
     *         tabbed pane
     */
    protected int getTabCount() {
        return tabbedPane.getTabCount();
    }

    /**
     * @param index
     * @return delegates {@link JTabbedPane#getTabComponentAt(int)} of the
     *         contained tabbed pane
     */
    protected Component getTabComponentAt(final int index) {
        return tabbedPane.getComponentAt(index);
    }

}
