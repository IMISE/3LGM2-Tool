package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.util.swing.component.tab.TabbedPane;

/**
 * EIn {@link ElementDialogPanel}, das andere {@link ElementDialogPanel} auf Tabs in sich aufnehmen
 * kann.
 *
 * @author AXS
 */
public class TabbedPanel extends ElementDialogPanel implements ChangeListener {

    /**
     * COMMENTME
     */
    private final TabbedPane rf;

    /**
     * @param dialog
     * @param dialogPanels
     */
    public TabbedPanel(final ElementPropertyDialog dialog, final ElementDialogPanel... dialogPanels) {
        super(dialog);

        rf = new TabbedPane();
        rf.addChangeListener(this);

        for (ElementDialogPanel dialogPanel : dialogPanels) {
            rf.addTab(dialogPanel.getName(), dialogPanel);
        }

        setLayout(new GridLayout(1, 1));
        add(rf);

        init();
    }

    public void addTab(final ElementDialogPanel dialogPanel) {
        rf.addTab(dialogPanel.getName(), dialogPanel);
        init();
    }

    @Override
    protected void init() {
        revalidate();
        repaint();
    }

    @Override
    public void update() {
        for (int i = 0; i < rf.getTabCount(); i++) {
            Component c = rf.getComponentAt(i);
            if (c instanceof ElementDialogPanel) {
                ((ElementDialogPanel) c).update();
            }
        }
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        TabbedPane t = (TabbedPane) e.getSource();
        Object o = t.getSelectedComponent();
        if (o instanceof ElementDialogPanel) {
            ((ElementDialogPanel) o).update();
        }
    }
}
