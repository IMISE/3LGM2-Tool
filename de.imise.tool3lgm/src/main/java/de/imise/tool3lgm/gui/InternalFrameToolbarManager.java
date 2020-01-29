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
public class InternalFrameToolbarManager implements LGMChangeListenerSimple, BasicGraphAreaChangeListener {

    /** toolbar with tools for active layer and sliders for zoom, angel and distance (Graph) or MetaPathSelector (Matrix) */
    private UnfloatableToolBar currentToolBar;

    /** Container, der die Toolbar für GraphFrames anzeigt */
    private final Container graphFrameToolbarParent;

    /** Container, der die Toolbar für MatrixFrames anzeigt */
    private final Container matrixFrameToolbarParent;

    /** der gerade aktive Frame */
    private AbstractInternalFrame currentFrame;

    /**
     * @param graphFrameToolbarParent
     * @param matrixFrameToolbarParent
     */
    public InternalFrameToolbarManager(final Container graphFrameToolbarParent, final Container matrixFrameToolbarParent) {
        this.graphFrameToolbarParent = graphFrameToolbarParent;
        this.matrixFrameToolbarParent = matrixFrameToolbarParent;
    }

    /**
     * Aktualisiert die Toolbar, wenn sich der aktive Frame geändert hat.
     */
    public void updateToolBar() {
        AbstractInternalFrame activeFrame = Static.getActiveFrame();
        if (currentFrame != activeFrame) {
            removeMeAsGraphFrameChangeListener();
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
        if (currentFrame != null && currentFrame instanceof InternalGraphFrame) {
            InternalGraphFrame graphFrame = (InternalGraphFrame) currentFrame;
            InputGraphArea inputGraphArea = graphFrame.getInputGraphArea();
            inputGraphArea.removeChangeListener(this);
        }
    }

    /**
     *
     */
    private void addMeAsGraphFrameChangeListener() {
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
        if (currentToolBar instanceof GraphAreaToolBar) {
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
