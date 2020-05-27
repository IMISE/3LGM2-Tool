package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR;

import java.awt.BorderLayout;
import java.awt.Container;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphAreaChangeListener;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.matrixview.MatrixViewPaneFrameComponent;
import de.imise.tool3lgm.gui.matrixview.MatrixViewPaneToolbar;
import de.imise.util.swing.component.UnfloatableToolBar;

/**
 * Diese Klasse kümmert sich darum, je nach Kontext, die Toolbar zu updaten, die für den gerade aktiven Frame angezeigt werden muss. Einerseits
 * kümmert sie sich darum, die richtige Toolbar anzuzeigen (GraphFrame oder MatrixView) und dann darum, die Toolbar je nach Kontext zu updaten
 * (selektierter Layer -> spezielle Elemente zum Zeichnen anbieten).
 *
 * @author AXS (8 Aug 2017)
 */
public class ViewPaneToolbarManager implements LGMChangeListenerSimple, BasicGraphAreaChangeListener {

    /** toolbar with tools for active layer and sliders for zoom, angel and distance (Graph) or MetaPathSelector (Matrix) */
    private UnfloatableToolBar currentToolBar;

    /** Container, der die Toolbar für GraphFrames anzeigt */
    private final Container graphFrameToolbarParent;

    /** Container, der die Toolbar für MatrixFrames anzeigt */
    private final Container matrixFrameToolbarParent;

    /** der gerade aktive Frame */
    private ViewPaneFrameComponent currentFrame;

    /**
     * @param graphFrameToolbarParent
     * @param matrixFrameToolbarParent
     */
    public ViewPaneToolbarManager(final Container graphFrameToolbarParent, final Container matrixFrameToolbarParent) {
        this.graphFrameToolbarParent = graphFrameToolbarParent;
        this.matrixFrameToolbarParent = matrixFrameToolbarParent;
    }

    /**
     * Aktualisiert die Toolbar, wenn sich der aktive Frame geändert hat.
     */
    public void updateToolBar() {
        ViewPaneFrameComponent activeFrame = Static.getActiveFrame();
        if (currentFrame != activeFrame) {
            removeMeAsGraphFrameChangeListener();
            if (activeFrame == null) {
                removeToolBar();
            } else if (activeFrame instanceof GraphViewPaneFrameComponent && activeFrame.getGraphDocument() instanceof Szenario) {
                GraphViewPaneFrameComponent graphFrame = (GraphViewPaneFrameComponent) activeFrame;
                if (currentToolBar == null || !(currentToolBar instanceof GraphAreaToolbar)) {
                    removeToolBar();
                    currentToolBar = new GraphAreaToolbar(graphFrame);
                    addToolBar();
                } else {
                    ((GraphAreaToolbar) currentToolBar).setFrame(graphFrame);
                    if (currentToolBar.getParent() == null) {
                        addToolBar();
                    }
                }
                setToolBarVisibility();
            } else if (activeFrame instanceof MatrixViewPaneFrameComponent) {
                MatrixViewPaneFrameComponent matrixFrame = (MatrixViewPaneFrameComponent) activeFrame;
                if (currentToolBar == null || !(currentToolBar instanceof MatrixViewPaneToolbar)) {
                    removeToolBar();
                    currentToolBar = new MatrixViewPaneToolbar(matrixFrame);
                    addToolBar();
                }
                MatrixViewPaneToolbar matrixViewToolBar = (MatrixViewPaneToolbar) currentToolBar;
                matrixViewToolBar.setFrame(matrixFrame);
                matrixFrame.setMatrixViewToolbar(matrixViewToolBar);
                if (matrixViewToolBar.getParent() == null) {
                    addToolBar();
                }
            } else {
                removeToolBar();
            }
            currentFrame = activeFrame;
            addMeAsGraphFrameChangeListener();
        }
        if (currentToolBar != null) {
            currentToolBar.update();
        }
    }

    /**
     *
     */
    private void removeMeAsGraphFrameChangeListener() {
        if (currentFrame != null && currentFrame instanceof GraphViewPaneFrameComponent) {
            GraphViewPaneFrameComponent graphFrame = (GraphViewPaneFrameComponent) currentFrame;
            InputGraphArea inputGraphArea = graphFrame.getInputGraphArea();
            inputGraphArea.removeChangeListener(this);
        }
    }

    /**
     *
     */
    private void addMeAsGraphFrameChangeListener() {
        if (currentFrame != null && currentFrame instanceof GraphViewPaneFrameComponent) {
            GraphViewPaneFrameComponent graphFrame = (GraphViewPaneFrameComponent) currentFrame;
            InputGraphArea inputGraphArea = graphFrame.getInputGraphArea();
            inputGraphArea.addChangeListener(this);
        }
    }

    /**
     * @return <code>true</code>, wenn die aktuelle Toolbar eine {@link GraphAreaToolbar} ist
     */
    public final boolean isGraphAreaToolBar() {
        return currentToolBar != null && currentToolBar instanceof GraphAreaToolbar;
    }

    /**
     * @return <code>true</code>, wenn die aktuelle Toolbar eine {@link GraphAreaToolbar} ist
     */
    public final boolean isMatrixViewToolbar() {
        return currentToolBar != null && currentToolBar instanceof MatrixViewPaneToolbar;
    }

    /**
     * @return
     */
    public GraphAreaToolbar getGraphAreaToolBar() {
        return isGraphAreaToolBar() ? (GraphAreaToolbar) currentToolBar : null;
    }

    /**
     * @return
     */
    public MatrixViewPaneToolbar getMatrixViewToolbar() {
        return isMatrixViewToolbar() ? (MatrixViewPaneToolbar) currentToolBar : null;
    }

    /**
     * @param visible
     */
    public void setToolBarVisibility() {
        if (currentToolBar != null) {
            boolean visible = OPTION_SHOW_PAINTING_TOOLBAR.is();
            Container parent = currentToolBar.getParent();
            if (!visible && parent != null) {
                removeToolBar();
            } else if (visible && parent != getToolbarParent()) {
                addToolBar();
            }
        }
    }

    /**
     *
     */
    private void removeToolBar() {
        if (currentToolBar != null) {
            Container toolbarParent = getToolbarParent();
            toolbarParent.remove(currentToolBar);
            toolbarParent.revalidate();
            toolbarParent.repaint();
        }
    }

    /**
     *
     */
    private void addToolBar() {
        if (currentToolBar != null) {
            Container toolbarParent = getToolbarParent();
            toolbarParent.add(currentToolBar, BorderLayout.SOUTH);
            toolbarParent.revalidate();
            toolbarParent.repaint();
        }
    }

    private Container getToolbarParent() {
        if (currentToolBar instanceof GraphAreaToolbar) {
            return graphFrameToolbarParent;
        }
        return matrixFrameToolbarParent;
    }

    ///////////////////////////
    // GraphDocumentListener //
    ///////////////////////////

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        updateToolBar();
    }

    //////////////////////////////////
    // BasicGraphAreaChangeListener //
    //////////////////////////////////

    @Override
    public void graphAreaChanged() {
        updateToolBar();
    }

}
