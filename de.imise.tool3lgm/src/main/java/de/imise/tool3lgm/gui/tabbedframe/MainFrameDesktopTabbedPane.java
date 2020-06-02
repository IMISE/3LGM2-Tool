package de.imise.tool3lgm.gui.tabbedframe;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentListener;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentParent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneToolbarManager;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;
import de.imise.util.swing.component.JTabbedPaneWithCloseIcons;

/**
 * @author AXS (27.05.2020)
 */
public class MainFrameDesktopTabbedPane extends JTabbedPaneWithCloseIcons implements ViewPaneFrameComponentParent, LGMChangeListenerSimple {

    /**
     *
     */
    public MainFrameDesktopTabbedPane() {
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    @Override
    public GraphViewPane getGraphViewPane(final GraphDocument doc) {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            Component tab = getComponentAt(i);
            if (tab instanceof GraphViewPaneFrameComponent) {
                GraphViewPaneFrameComponent graphViewPaneFrameComponent = (GraphViewPaneFrameComponent) tab;
                if (graphViewPaneFrameComponent.hasGraphDocument(doc)) {
                    GraphViewPane viewPane = graphViewPaneFrameComponent.getViewPane();
                    return viewPane;
                }
            }
        }
        return null;
    }

    @Override
    public List<ViewPaneFrameComponent> getViewPaneFrameComponents(final GraphDocument doc) { //doc == null -> return all ViewPaneFrameComponent
        int tabCount = getTabCount();
        List<ViewPaneFrameComponent> viewPaneFrameComponents = new ArrayList<>(doc == null ? tabCount : 3); //more than 1 graph view and 2 matrix views per doc is unlikely
        for (int i = 0; i < tabCount; i++) {
            Component tab = getComponentAt(i);
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
            Component tab = getComponentAt(i);
            if (tab instanceof ViewPaneFrameComponent) {
                ViewPaneFrameComponent viewPaneFrameComponent = (ViewPaneFrameComponent) tab;
                if (viewPaneFrameComponent.hasGraphDocument(doc)) {
                    removeTabAt(i);
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
     * @param tabComponent
     */
    private void addView(final MainFrameDesktopTabComponent tabComponent) {
        addTab(tabComponent.getName(), null, tabComponent, tabComponent.getFullName());
        GraphDocument doc = tabComponent.getGraphDocument();
        doc.addAllTransactionsListener(this);
        setSelected(tabComponent);
    }

    @Override
    public GraphViewPaneFrameComponent createGraphView(final GraphDocument doc) {
        MainFrameDesktopGraphTabComponent graphView = new MainFrameDesktopGraphTabComponent(doc);
        addView(graphView);
        return graphView;
    }

    @Override
    public MatrixViewPaneFrameComponent createMatrixView(final GraphDocument doc, final int titleIndex, final ViewPaneToolbarManager viewPaneToolBarManager) {
        MainFrameDesktopMatrixTabComponent matrixView = new MainFrameDesktopMatrixTabComponent(doc, viewPaneToolBarManager, titleIndex);
        addView(matrixView);
        return matrixView;
    }

    @Override
    public void removeTabAt(final int index) {
        Component selectedComponent = getSelectedComponent();
        ViewPaneFrameComponent viewFrame = null;
        ViewPaneFrameComponentListener mainFrameDesktopPane = Static.getMainFrameDesktopPane();
        if (selectedComponent instanceof ViewPaneFrameComponent) {
            viewFrame = (ViewPaneFrameComponent) selectedComponent;
            mainFrameDesktopPane.viewDeactivated(viewFrame);
            mainFrameDesktopPane.viewClosing(viewFrame);
            GraphDocument doc = viewFrame.getGraphDocument();
            doc.addAllTransactionsListener(this); //this must be added to the same doc as views for this doc are opened, because closing a view always removes one listener!
        }
        super.removeTabAt(index);
        if (viewFrame != null) {
            mainFrameDesktopPane.viewClosed(viewFrame);
            viewFrame.dispose(); //remove the InputGraphArea as MouseListener of the MainFrame
        }
    }

    @Override
    public void setSelectedIndex(final int index) {
        Component selectedComponent = getSelectedComponent();
        ViewPaneFrameComponentListener mainFrameDesktopPane = Static.getMainFrameDesktopPane();
        if (selectedComponent instanceof ViewPaneFrameComponent) {
            ViewPaneFrameComponent viewFrame = (ViewPaneFrameComponent) selectedComponent;
            mainFrameDesktopPane.viewDeactivated(viewFrame);
        }
        super.setSelectedIndex(index);
        selectedComponent = getSelectedComponent();
        if (selectedComponent instanceof ViewPaneFrameComponent) {
            ViewPaneFrameComponent viewFrame = (ViewPaneFrameComponent) selectedComponent;
            mainFrameDesktopPane.viewActivated(viewFrame);
        }
    }

    @Override
    public void modelOrSzenarioNameChanged(final GraphDocument source) {
        for (int i = getTabCount() - 1; i >= 0; i--) {
            Component tab = getComponentAt(i);
            if (tab instanceof ViewPaneFrameComponent) {
                ViewPaneFrameComponent viewPaneFrameComponent = (ViewPaneFrameComponent) tab;
                if (viewPaneFrameComponent.hasGraphDocument(source)) {
                    String name = viewPaneFrameComponent.getName();
                    setTitleAt(i, name);
                }
            }
        }
    }

}
