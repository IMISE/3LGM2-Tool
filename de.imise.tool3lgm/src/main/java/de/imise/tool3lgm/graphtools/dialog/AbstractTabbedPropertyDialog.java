package de.imise.tool3lgm.graphtools.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.util.swing.component.TabbedPane;

public abstract class AbstractTabbedPropertyDialog extends AbstractPropertyDialog {

    /** TabbedPane in das alle Panels kommen */
    private final TabbedPane tab;

    public AbstractTabbedPropertyDialog(final GDCollection gdcoll) {
        super(gdcoll);
        tab = new TabbedPane();
    }

    public AbstractTabbedPropertyDialog(final Frame owner, final GDCollection gdcoll) {
        super(owner, gdcoll);
        tab = new TabbedPane();
    }

    public AbstractTabbedPropertyDialog(final Dialog owner, final GDCollection gdcoll) {
        super(owner, gdcoll);
        tab = new TabbedPane();
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

    protected JComponent getTabComponent() {
        return tab;
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
        tab.addTab(title, icon, component);
    }

    public void setLastTabIcon(final Icon icon) {
        setTabIcon(tab.getTabCount() - 1, icon);
    }

    public void setLastTabTitle(final String title) {
        setTabTitle(tab.getTabCount() - 1, title);
    }

    public void setTabIcon(final int tabIndex, final Icon icon) {
        tab.setIconAt(tabIndex, icon);
    }

    public void setTabTitle(final int tabIndex, final String title) {
        tab.setTitleAt(tabIndex, title);
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
        for (int i = 0; i < tab.getComponentCount(); i++) {
            Component comp = tab.getComponent(i);
            if (title != null && !tab.getTitleAt(i).equals(title)) {
                continue;
            }
            if (!tabComponentClass.isAssignableFrom(comp.getClass())) {
                continue;
            }
            tab.setSelectedIndex(i);
            return i;
        }
        return -1;
    }

    /**
     * @param tabComponentClass
     * @return
     * @see #selectTab(String, Class)
     */
    public int selectTab(final Class<? extends Component> tabComponentClass) {
        return selectTab(null, tabComponentClass);
    }

    public ElementDialogPanel getSelectedElementDialogPanel() {
        Component selectedComponent = tab.getSelectedComponent();
        return selectedComponent instanceof ElementDialogPanel ? (ElementDialogPanel) selectedComponent : null;
    }

    protected int getTabCount() {
        return tab.getTabCount();
    }

    protected Component getTabComponentAt(final int index) {
        return tab.getTabComponentAt(index);
    }

}
