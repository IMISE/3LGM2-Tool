package de.imise.tool3lgm.gui.tabbedframe;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentListener;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentParent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneToolbarManager;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;

/**
 * @author AXS (27.05.2020)
 */
public class MainFrameDesktopTabbedPane extends JTabbedPane implements ViewPaneFrameComponentParent {

    /**
     *
     */
    public MainFrameDesktopTabbedPane() {
    }

    @Override
    public GraphViewPane getGraphViewPane(final GraphDocument doc) {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            Component tab = getTabComponentAt(i);
            if (tab instanceof GraphViewPaneFrameComponent) {
                GraphViewPaneFrameComponent graphViewPaneFrameComponent = (GraphViewPaneFrameComponent) tab;
                GraphViewPane viewPane = graphViewPaneFrameComponent.getViewPane();
                return viewPane;
            }
        }
        return null;
    }

    @Override
    public List<ViewPaneFrameComponent> getViewPaneFrameComponents(final GraphDocument doc) { //doc == null -> return all ViewPaneFrameComponent
        int tabCount = getTabCount();
        List<ViewPaneFrameComponent> viewPaneFrameComponents = new ArrayList<>(doc == null ? tabCount : 3); //more than 1 graph view and 2 matrix views per doc is unlikely
        for (int i = 0; i < tabCount; i++) {
            Component tab = getTabComponentAt(i);
            if (tab instanceof ViewPaneFrameComponent) {
                ViewPaneFrameComponent viewPaneFrameComponent = (ViewPaneFrameComponent) tab;
                if (doc == null || viewPaneFrameComponent.hasGraphDocument(doc)) {
                    viewPaneFrameComponents.add(viewPaneFrameComponent);
                }
            }
        }
        return viewPaneFrameComponents;
    }

    @Override
    public List<ViewPaneFrameComponent> getAllViewPaneFrameComponents() {
        return getViewPaneFrameComponents(null);
    }

    @Override
    public void removeViewPaneFrameComponents(final GraphDocument doc) {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            Component tab = getTabComponentAt(i);
            if (tab instanceof ViewPaneFrameComponent) {
                ViewPaneFrameComponent viewPaneFrameComponent = (ViewPaneFrameComponent) tab;
                if (viewPaneFrameComponent.hasGraphDocument(doc)) {
                    remove(i);
                    viewPaneFrameComponent.dispose();
                }
            }
        }
    }

    @Override
    public boolean isSelected(final ViewPaneFrameComponent viewPaneFrameComponent) {
        Component selectedComponent = getSelectedComponent();
        return selectedComponent == viewPaneFrameComponent;
    }

    @Override
    public void setSelected(final ViewPaneFrameComponent viewPaneFrameComponent) {
        setSelectedComponent((Component) viewPaneFrameComponent);
    }

    /**
     * @param doc
     * @return
     */
    @Override
    public GraphViewPaneFrameComponent createGraphView(final GraphDocument doc) {
        MainFrameDesktopGraphTabComponent graphView = new MainFrameDesktopGraphTabComponent(doc);
        super.add(graphView);
        setSelected(graphView);
        return graphView;
    }

    @Override
    public MatrixViewPaneFrameComponent createMatrixView(final GraphDocument doc, final int titleIndex, final ViewPaneToolbarManager viewPaneToolBarManager) {
        MainFrameDesktopMatrixTabComponent matrixView = new MainFrameDesktopMatrixTabComponent(doc);
        super.add(matrixView);
        setSelected(matrixView);
        return matrixView;
    }

    @Override
    public void addViewPaneFrameComponentListener(final ViewPaneFrameComponentListener listener) {
        // TODO Auto-generated method stub

    }

}
