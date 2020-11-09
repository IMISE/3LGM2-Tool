package de.imise.tool3lgm.graphtools.dialog.element.panel;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.graphtools.dialog.element.ElementPropertyDialog;
import de.imise.util.swing.component.tab.ReorderableTabbedPane;

/**
 * EIn {@link ElementDialogPanel}, das andere {@link ElementDialogPanel} auf
 * Tabs in sich aufnehmen kann.
 *
 * @author AXS
 */
public class TabbedPanel extends ElementDialogPanel implements ChangeListener {

    /**
     * COMMENTME
     */
    private final ReorderableTabbedPane tabbedPane;

    /**
     * @param dialog
     * @param dialogPanels
     */
    public TabbedPanel(final ElementPropertyDialog dialog, final ElementDialogPanel... dialogPanels) {
        super(dialog);

        tabbedPane = new ReorderableTabbedPane();
        tabbedPane.addChangeListener(this);

        for (ElementDialogPanel dialogPanel : dialogPanels) {
            tabbedPane.addTab(dialogPanel.getName(), dialogPanel);
        }

        setLayout(new GridLayout(1, 1));
        add(tabbedPane);

        init();
    }

    public void addTab(final ElementDialogPanel dialogPanel) {
        tabbedPane.addTab(dialogPanel.getName(), dialogPanel);
        init();
    }

    @Override
    protected void init() {
        revalidate();
        repaint();
    }

    @Override
    public void update() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component c = tabbedPane.getComponentAt(i);
            if (c instanceof ElementDialogPanel) {
                ((ElementDialogPanel) c).update();
            }
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        ReorderableTabbedPane t = (ReorderableTabbedPane) e.getSource();
        Object o = t.getSelectedComponent();
        if (o instanceof ElementDialogPanel) {
            ((ElementDialogPanel) o).update();
        }
    }

    @Override
    public Collection<JComponent> getToolTipTargets() {
        Collection<JComponent> toolTipTargets = new ArrayList<>();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component tabComponent = tabbedPane.getComponentAt(i);
            if (tabComponent instanceof ElementDialogPanel) {
                ElementDialogPanel panel = (ElementDialogPanel) tabComponent;
                Collection<JComponent> panelToolTipTargets = panel.getToolTipTargets();
                toolTipTargets.addAll(panelToolTipTargets);
            }
        }
        return toolTipTargets;
    }

}
