package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.util.swing.component.TabbedPane;

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
     * @param dl
     * @param dialogPanels
     */
    public TabbedPanel(final ElementPropertyDialog dl, final ElementDialogPanel... dialogPanels) {
        super(dl);

        rf = new TabbedPane();
        for (ElementDialogPanel pane : dialogPanels) {
            rf.addTab(pane.getName(), pane);
        }
        rf.addChangeListener(this);

        setLayout(new GridLayout(1, 1));
        add(rf);

        init();
    }

    @Override
    protected void init() {
        revalidate();
        repaint();
    }

    @Override
    protected void showFullDialog() {
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
