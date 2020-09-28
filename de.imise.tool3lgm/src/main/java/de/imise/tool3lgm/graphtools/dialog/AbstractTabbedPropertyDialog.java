package de.imise.tool3lgm.graphtools.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
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
     * Fügt dem Dialog einen neuen Tab mit der übergebenen Componente hinzu. Der Titel des Tabs ist der
     * wird über die getName()-Funktion der Componente ermittelt.
     *
     * @param component
     */
    protected void addTab(final Component component) {
        addTab(component.getName(), component);
    }

    /**
     * Fügt dem Dialog einen neuen Tab mit dem übergebenen Titel und der Komponente hinzu.
     *
     * @param title
     * @param component
     */
    public void addTab(final String title, final Component component) {
        addTab(title, null, component);
    }

    /**
     * Fügt dem Dialog einen neuen Tab mit dem übergebenen Titel, Icon und der Komponente hinzu.
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
     * Delegates {@link JTabbedPane#setIAt(int, Icon)} of the contained tabbed pane.
     *
     * @param tabIndex
     * @param icon
     */
    public void setTabIcon(final int tabIndex, final Icon icon) {
        tabbedPane.setIconAt(tabIndex, icon);
    }

    /**
     * Delegates {@link JTabbedPane#setTitleAt(int, String)} of the contained tabbed pane.
     *
     * @param tabIndex
     * @param title
     */
    public void setTabTitle(final int tabIndex, final String title) {
        tabbedPane.setTitleAt(tabIndex, title);
    }

    /**
     * Bringt den Tab mit dem angegebenen Titel in den Vordergrund, wenn zusätzlich noch die
     * übergebene Klasse mit der Klasse der Componente in dem Tab zuweisungskompatibel ist. Die
     * Klasse der im Tabpanel enthaltenen Componente muss die gleiche oder eine Unterklasse der
     * übergebenen Klasse sein.
     *
     * @param title Titel des zu selektierenden Tabs. Wird <code>null</code> übergeben, wird der
     *            erstbeste passende Tab herausgesucht
     * @param tabComponentClass Oberklasse der Komponente in dem zu selektierenden Tab
     * @return Index des Tabs, wenn ein Tab der angegebenen Art gefunden und in den Vordergund
     *         geracht werden konnte
     */
    public int selectTab(final String title, final Class<? extends Component> tabComponentClass) {
        for (int i = 0; i < tabbedPane.getComponentCount(); i++) {
            Component comp = tabbedPane.getComponent(i);
            if (title != null && !tabbedPane.getTitleAt(i).equals(title)) {
                continue;
            }
            if (!tabComponentClass.isAssignableFrom(comp.getClass())) {
                continue;
            }
            tabbedPane.setSelectedIndex(i);
            return i;
        }
        return -1;
    }

    /**
     * Selects the first tab of the contained tabbed pane where the component of this tab is
     * an instance of the given class.
     *
     * @param tabComponentClass
     * @return
     * @see #selectTab(String, Class)
     */
    public int selectTab(final Class<? extends Component> tabComponentClass) {
        return selectTab(null, tabComponentClass);
    }

    /**
     * @return the {@link ElementDialogPanel} in the active tab of the contained tabbed pane
     */
    public ElementDialogPanel getSelectedElementDialogPanel() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        return selectedComponent instanceof ElementDialogPanel ? (ElementDialogPanel) selectedComponent : null;
    }

    /**
     * @return a list of all tab names in the order of the tabs added to the contained tabbed pane
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
     * @return delegates {@link JTabbedPane#getTabCount()} of the contained tabbed pane
     */
    protected int getTabCount() {
        return tabbedPane.getTabCount();
    }

    /**
     * @param index
     * @return delegates {@link JTabbedPane#getTabComponentAt(int)} of the contained tabbed pane
     */
    protected Component getTabComponentAt(final int index) {
        return tabbedPane.getComponentAt(index);
    }

}
