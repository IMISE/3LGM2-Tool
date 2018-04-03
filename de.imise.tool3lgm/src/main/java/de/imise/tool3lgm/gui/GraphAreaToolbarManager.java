package de.imise.tool3lgm.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.matrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.matrixview.MatrixViewPathSelectorToolBar;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.GraphDocumentListener;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphAreaChangeListener;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;
import de.imise.util.swing.component.UnfloatableToolBar;

public class GraphAreaToolbarManager implements GraphDocumentListener, BasicGraphAreaChangeListener {

    /** toolbar with tools for active layer and sliders for zoom, angel and distance (Graph) or MetaPathSelector (Matrix) */
    private UnfloatableToolBar currentToolBar;

    /** Container, an dem die Toolbar an South angezeigt wird */
    private final Container toolbarParent;

    private AbstractInternalFrame currentFrame;

    public GraphAreaToolbarManager(final Container toolbarParent) {
        this.toolbarParent = toolbarParent;
    }

    public void updateToolBar() {
        AbstractInternalFrame activeFrame = Static.getActiveFrame();
        if (currentFrame != activeFrame) {
            removeChangeListener();
            if (activeFrame == null) {
                removeToolBar();
            } else if (activeFrame instanceof InternalGraphFrame && activeFrame.getGraphDocument() instanceof Szenario) {
                InternalGraphFrame graphFrame = (InternalGraphFrame) activeFrame;
                if (currentToolBar == null || !(currentToolBar instanceof GraphAreaToolBar)) {
                    removeToolBar();
                    currentToolBar = new GraphAreaToolBar(graphFrame);
                    addToolBar();
                } else {
                    ((GraphAreaToolBar) currentToolBar).setFrame(graphFrame);
                    if (currentToolBar.getParent() == null) {
                        addToolBar();
                    }
                }
                setToolBarVisible(UserProperties.is(BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR));
            } else if (activeFrame instanceof MatrixViewInternalFrame) {
                MatrixViewInternalFrame matrixFrame = (MatrixViewInternalFrame) activeFrame;
                if (currentToolBar == null || !(currentToolBar instanceof MatrixViewPathSelectorToolBar)) {
                    removeToolBar();
                    currentToolBar = new MatrixViewPathSelectorToolBar(matrixFrame);
                    addToolBar();
                }
                MatrixViewPathSelectorToolBar matrixViewToolBar = (MatrixViewPathSelectorToolBar) currentToolBar;
                matrixViewToolBar.setFrame(matrixFrame);
                matrixFrame.setMatrixViewToolBar(matrixViewToolBar);
                if (matrixViewToolBar.getParent() == null) {
                    addToolBar();
                }
            } else {
                removeToolBar();
            }
            currentFrame = activeFrame;
            addChangeListener();
        }
    }

    private void removeChangeListener() {
        if (currentFrame != null && currentFrame instanceof InternalGraphFrame) {
            InternalGraphFrame graphFrame = (InternalGraphFrame) currentFrame;
            InputGraphArea inputGraphArea = graphFrame.getInputGraphArea();
            inputGraphArea.removeChangeListener(this);
        }
    }

    private void addChangeListener() {
        if (currentFrame != null && currentFrame instanceof InternalGraphFrame) {
            InternalGraphFrame graphFrame = (InternalGraphFrame) currentFrame;
            InputGraphArea inputGraphArea = graphFrame.getInputGraphArea();
            inputGraphArea.addChangeListener(this);
        }
    }

    public GraphAreaToolBar getGraphAreaToolBar() {
        return currentToolBar != null && currentToolBar instanceof GraphAreaToolBar ? (GraphAreaToolBar) currentToolBar : null;
    }

    public void setToolBarVisible(final boolean visible) {
        if (currentToolBar != null) {
            Container parent = currentToolBar.getParent();
            if (!visible && parent != null) {
                removeToolBar();
            } else if (visible && parent != toolbarParent) {
                addToolBar();
            }
        }
    }

    private void removeToolBar() {
        if (currentToolBar != null) {
            toolbarParent.remove(currentToolBar);
            toolbarParent.revalidate();
            toolbarParent.repaint();
        }
    }

    private void addToolBar() {
        if (currentToolBar != null) {
            toolbarParent.add(currentToolBar, BorderLayout.SOUTH);
            toolbarParent.revalidate();
            toolbarParent.repaint();
        }
    }

    ///////////////////////////
    // GraphDocumentListener //
    ///////////////////////////

    @Override
    public void dataChanged(final GraphDocument source) {
    }

    @Override
    public void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
    }

    @Override
    public void elementAdded(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public void elementDeleted(final GraphDocument source, final ElementContainer element) {
    }

    @Override
    public void groupOrderChanged(final GraphDocument source) {
    }

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        if (currentToolBar instanceof GraphAreaToolBar) {
            ((GraphAreaToolBar) currentToolBar).setLayer(source.getCollection().getActiveLayer());
        }
    }

    @Override
    public void colorsChanged(final GraphDocument source) {
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
    }

    //////////////////////////////////
    // BasicGraphAreaChangeListener //
    //////////////////////////////////

    @Override
    public void graphAreaChanged() {
        if (currentToolBar instanceof GraphAreaToolBar) {
            ((GraphAreaToolBar) currentToolBar).update();
        }
    }

    @Override
    public void layerViewChanged(final BasicGraphArea source) {
        activeLayerChanged(source.getSzenario());
    }

}
