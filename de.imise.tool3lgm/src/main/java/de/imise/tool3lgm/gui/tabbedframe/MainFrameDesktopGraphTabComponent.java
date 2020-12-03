package de.imise.tool3lgm.gui.tabbedframe;

import javax.swing.ImageIcon;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;

/**
 * @author AXS (27.05.2020)
 */
public class MainFrameDesktopGraphTabComponent extends MainFrameDesktopTabComponent implements GraphViewPaneFrameComponent {

    /**
     * the real graph display and interaction component
     */
    private final InputGraphArea area;

    /**
     * @param doc
     */
    public MainFrameDesktopGraphTabComponent(final GraphDocument doc) {
        super(new GraphViewPane(doc));
        GraphViewPane graphViewPane = getViewPane();
        area = graphViewPane.getInputGraphArea();
    }

    @Override
    public GraphViewPane getViewPane() {
        return (GraphViewPane) super.getViewPane();
    }

    @Override
    public InputGraphArea getInputGraphArea() {
        return area;
    }

    @Override
    public void dispose() {
        super.dispose();
        area.dispose();
    }

    @Override
    public ImageIcon getTabIcon() {
        return Tool3lgmConstants.TOOL_ICON_13;
    }

}
