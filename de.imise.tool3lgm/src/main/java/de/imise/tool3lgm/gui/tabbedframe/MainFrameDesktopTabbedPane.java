package de.imise.tool3lgm.gui.tabbedframe;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentListener;
import de.imise.tool3lgm.gui.viewpane.ViewPaneFrameComponentParent;
import de.imise.tool3lgm.gui.viewpane.ViewPaneToolbarManager;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPane;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.matrix.MatrixViewPaneFrameComponent;
import de.imise.util.swing.component.tab.TabbedPaneWithCloseIcons;

/**
 * The TabbedPane, which holds all main windows (submodel views and matrix
 * views).
 *
 * @author AXS (27.05.2020)
 */
public class MainFrameDesktopTabbedPane extends TabbedPaneWithCloseIcons implements ViewPaneFrameComponentParent, LGMChangeListenerSimple {

    /**
     * If all tabs are removed no border should be painted. Store the original
     * TabbedPaintUI to reset it if there is at least one tab.
     */
    private TabbedPaneUI defaultTabbedPaneUI;

    /**
     *
     */
    public MainFrameDesktopTabbedPane() {
        super(Tool3lgmConstants.ACTIVE_TAB_FOREGROUND_COLOR);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        updateBorder();
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

    /**
     * If all tabs are removed no border is painted. If there is at least one
     * tab the default border is painted.
     */
    private void updateBorder() {
        if (getTabCount() == 0) {
            defaultTabbedPaneUI = getUI();
            setUI(new BasicTabbedPaneUI() {
                private final Insets borderInsets = new Insets(0, 0, 0, 0);
                @Override
                protected void paintContentBorder(final Graphics g, final int tabPlacement, final int selectedIndex) {
                }
                @Override
                protected Insets getContentBorderInsets(final int tabPlacement) {
                    return borderInsets;
                }
            });
        } else {
            setUI(defaultTabbedPaneUI);
        }

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
        String name = tabComponent.getName();
        ImageIcon icon = tabComponent.getTabIcon();
        String toolTip = tabComponent.getFullName();
        addTab(name, icon, tabComponent, toolTip);
        GraphDocument doc = tabComponent.getGraphDocument();
        doc.addAllTransactionsListener(this);
        setSelected(tabComponent);
        updateBorder();
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
        //super.removeTabAt(index); donn't call setSelectedIndex() so we have to call
        //the viewActivated(...) function explicitly
        setSelectedTabAsActiveView();
        updateBorder();
    }

    @Override
    public void setSelectedIndex(final int index) {
        Component selectedComponent = getSelectedComponent();
        if (selectedComponent instanceof ViewPaneFrameComponent) {
            ViewPaneFrameComponent viewFrame = (ViewPaneFrameComponent) selectedComponent;
            ViewPaneFrameComponentListener mainFrameDesktopPane = Static.getMainFrameDesktopPane();
            mainFrameDesktopPane.viewDeactivated(viewFrame);
        }
        super.setSelectedIndex(index);
        setSelectedTabAsActiveView();
    }

    /**
     * Sets the selected tab in the data model as active view
     */
    private void setSelectedTabAsActiveView() {
        Component selectedComponent = getSelectedComponent();
        if (selectedComponent instanceof ViewPaneFrameComponent) {
            ViewPaneFrameComponent viewFrame = (ViewPaneFrameComponent) selectedComponent;
            ViewPaneFrameComponentListener mainFrameDesktopPane = Static.getMainFrameDesktopPane();
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
