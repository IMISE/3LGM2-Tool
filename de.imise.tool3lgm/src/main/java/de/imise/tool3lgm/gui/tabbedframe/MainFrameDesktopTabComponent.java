package de.imise.tool3lgm.gui.tabbedframe;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.viewpane.ViewPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;

/**
 * @author AXS (27.05.2020)
 */
public abstract class MainFrameDesktopTabComponent extends JPanel implements ViewPaneFrameComponent {

    /** the view to display */
    protected final ViewPane viewPane;

    /**
     * @param viewPane
     */
    public MainFrameDesktopTabComponent(final ViewPane viewPane) {
        this.viewPane = viewPane;
        setLayout(new BorderLayout());
        add(viewPane, BorderLayout.CENTER);
        GraphDocument doc = viewPane.getGraphDocument();
        doc.addAllTransactionsListener(this);
    }

    @Override
    public ViewPane getViewPane() {
        return viewPane;
    }

    @Override
    public String getName() {
        return viewPane.getName();
    }

    @Override
    public String getToolTipText() {
        return viewPane.getFullName();
    }

    @Override
    public void dispose() {
        GraphDocument doc = getGraphDocument();
        doc.removeAllTransactionsListener(this);
    }

    /**
     * @return the icon that should be displayed on the tab
     */
    public abstract ImageIcon getTabIcon();

}
