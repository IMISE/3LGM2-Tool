package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_PAINTING_TOOLBAR;

import java.awt.BorderLayout;
import java.awt.Container;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.newmatrixview.InternalMatrixFrameToolBar;
import de.imise.tool3lgm.graphtools.newmatrixview.MatrixViewInternalFrame;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphAreaChangeListener;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.util.swing.component.UnfloatableToolBar;

/**
 * Diese Klasse kümmert sich darum, je nach Kontext, die Toolbar zu updaten, die für den gerade aktiven Frame angezeigt werden muss. Einerseits
 * kümmert sie sich darum, die richtige Toolbar anzuzeigen (GraphFrame oder MatrixView) und dann darum, die Toolbar je nach Kontext zu updaten
 * (selektierter Layer -> spezielle Elemente zum Zeichnen anbieten).
 *
 * @author AXS (8 Aug 2017)
 */
public class GraphAreaToolbarManager implements LGMChangeListenerSimple, BasicGraphAreaChangeListener {

    /** toolbar with tools for active layer and sliders for zoom, angel and distance (Graph) or MetaPathSelector (Matrix) */
    private UnfloatableToolBar currentToolBar;

    /** Container, an dem die Toolbar an South angezeigt wird */
    private final Container toolbarParent;

    /** der gerade aktive Frame */
    private AbstractInternalFrame currentFrame;

    /**
     * @param toolbarParent
     */
    public GraphAreaToolbarManager(final Container toolbarParent) {
        this.toolbarParent = toolbarParent;
    }

    /**
     * Aktualisiert die Toolbar, wenn sich der aktive Frame geändert hat.
     */
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
                setToolBarVisibility();
            } else if (activeFrame instanceof MatrixViewInternalFrame) {
                MatrixViewInternalFrame matrixFrame = (MatrixViewInternalFrame) activeFrame;
                if (currentToolBar == null || !(currentToolBar instanceof InternalMatrixFrameToolBar)) {
                    removeToolBar();
                    currentToolBar = new InternalMatrixFrameToolBar(matrixFrame);
                    addToolBar();
                }
                InternalMatrixFrameToolBar matrixViewToolBar = (InternalMatrixFrameToolBar) currentToolBar;
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

    /**
     *
     */
    private void removeChangeListener() {
        if (currentFrame != null && currentFrame instanceof InternalGraphFrame) {
            InternalGraphFrame graphFrame = (InternalGraphFrame) currentFrame;
            InputGraphArea inputGraphArea = graphFrame.getInputGraphArea();
            inputGraphArea.removeChangeListener(this);
        }
    }

    /**
     *
     */
    private void addChangeListener() {
        if (currentFrame != null && currentFrame instanceof InternalGraphFrame) {
            InternalGraphFrame graphFrame = (InternalGraphFrame) currentFrame;
            InputGraphArea inputGraphArea = graphFrame.getInputGraphArea();
            inputGraphArea.addChangeListener(this);
        }
    }

    /**
     * @return <code>true</code>, wenn die aktuelle Toolbar eine {@link GraphAreaToolBar} ist
     */
    public final boolean isGraphAreaToolBar() {
        return currentToolBar != null && currentToolBar instanceof GraphAreaToolBar;
    }

    /**
     * @return <code>true</code>, wenn die aktuelle Toolbar eine {@link GraphAreaToolBar} ist
     */
    public final boolean isMatrixViewToolBar() {
        return currentToolBar != null && currentToolBar instanceof InternalMatrixFrameToolBar;
    }

    /**
     * @return
     */
    public GraphAreaToolBar getGraphAreaToolBar() {
        return isGraphAreaToolBar() ? (GraphAreaToolBar) currentToolBar : null;
    }

    /**
     * @return
     */
    public InternalMatrixFrameToolBar getMatrixViewToolBar() {
        return isMatrixViewToolBar() ? (InternalMatrixFrameToolBar) currentToolBar : null;
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
            } else if (visible && parent != toolbarParent) {
                addToolBar();
            }
        }
    }

    /**
     *
     */
    private void removeToolBar() {
        if (currentToolBar != null) {
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
            toolbarParent.add(currentToolBar, BorderLayout.SOUTH);
            toolbarParent.revalidate();
            toolbarParent.repaint();
        }
    }

    ///////////////////////////
    // GraphDocumentListener //
    ///////////////////////////

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        if (currentToolBar instanceof GraphAreaToolBar) {
            ((GraphAreaToolBar) currentToolBar).setLayer(source.getCollection().getActiveLayer());
        }
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
