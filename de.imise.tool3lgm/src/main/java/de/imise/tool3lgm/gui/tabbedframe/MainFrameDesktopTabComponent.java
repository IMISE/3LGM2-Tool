package de.imise.tool3lgm.gui.tabbedframe;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener;
import de.imise.tool3lgm.gui.viewpane.ViewPane;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;

/**
 * @author AXS (27.05.2020)
 */
public abstract class MainFrameDesktopTabComponent extends JPanel implements ViewPaneFrameComponent, LGMChangeListener {

    /** the view to display */
    protected final ViewPane viewPane;

    /**
     * @param viewPane
     */
    public MainFrameDesktopTabComponent(final ViewPane viewPane) {
        this.viewPane = viewPane;
        setLayout(new BorderLayout());
        add(viewPane, BorderLayout.CENTER);
        GraphDocument doc = getGraphDocument();
        doc.addAllTransactionsListener(this);
    }

    @Override
    public ViewPane getViewPane() {
        return viewPane;
    }

    @Override
    public final void modelOrSzenarioNameChanged(final GraphDocument source) {
        updateTitle();
    }

    /**
     * Sets the frame title
     */
    public final void updateTitle() {
        String fullName = viewPane.getFullName();
        setName(fullName);
    }

    @Override
    public void szenarioRemoved(final GraphDocument source) {
        if (source == getGraphDocument()) {
            //closeTab? oder macht das der Parent?
        }
    }

    @Override
    public void dispose() {
        GraphDocument doc = getGraphDocument();
        doc.removeAllTransactionsListener(this);
    }

}
