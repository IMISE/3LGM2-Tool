package de.imise.tool3lgm.graphtools.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.swing.component.TabbedPane;

public abstract class AbstractTabbedPropertyDialog extends AbstractPropertyDialog {

    /** TabbedPane in das alle Panels kommen */
    private final TabbedPane tabbedPane;

    public AbstractTabbedPropertyDialog(final GDCollection gdcoll) {
        super(gdcoll);
        tabbedPane = new TabbedPane();
    }

    public AbstractTabbedPropertyDialog(final Frame owner, final GDCollection gdcoll) {
        super(owner, gdcoll);
        tabbedPane = new TabbedPane();
    }

    public AbstractTabbedPropertyDialog(final Dialog owner, final GDCollection gdcoll) {
        super(owner, gdcoll);
        tabbedPane = new TabbedPane();
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
        return tabbedPane;
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
     * Entfernt den Tab mit dem übergebenen Index
     * 
     * @param tabIndex
     */
    protected void removeTab(final int tabIndex) {
        tabbedPane.remove(tabIndex);
    }

    public void setLastTabIcon(final Icon icon) {
        setTabIcon(tabbedPane.getTabCount() - 1, icon);
    }

    public void setLastTabTitle(final String title) {
        setTabTitle(tabbedPane.getTabCount() - 1, title);
    }

    public void setTabIcon(final int tabIndex, final Icon icon) {
        tabbedPane.setIconAt(tabIndex, icon);
    }

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
     * @param tabComponentClass
     * @return
     * @see #selectTab(String, Class)
     */
    public int selectTab(final Class<? extends Component> tabComponentClass) {
        return selectTab(null, tabComponentClass);
    }

    public ElementDialogPanel getSelectedElementDialogPanel() {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        return selectedComponent instanceof ElementDialogPanel ? (ElementDialogPanel) selectedComponent : null;
    }

    public List<String> getTabNames() {
        ImmutableList.Builder<String> tabNames = ImmutableList.builder();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            tabNames.add(title);

        }
        return tabNames.build();
    }

    protected int getTabCount() {
        return tabbedPane.getTabCount();
    }

    protected Component getTabComponentAt(final int index) {
        return tabbedPane.getComponentAt(index);
    }

}
