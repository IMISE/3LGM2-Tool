package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelInstance;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;

/**
 * @author N.N.
 * @create Very long time ago
 */
public class ToolButton extends JToggleButton {

    private final Node paintedNode;

    private final boolean isEdgeButton;

    private static final Dimension PREFRERRED_SIZE = new Dimension(30, 30);

    private InternalGraphFrame frame;

    /**
     * COMMENTME
     */
    private NodeContainer paintedNodeContainer;

    /**
     * @param metaModel
     * @param paintableElementClass
     * @param isEdgeButton
     */
    private ToolButton(final MetaModelInstance metaModel, final Class<? extends ModelElement> paintableElementClass) {
        isEdgeButton = metaModel == null;
        paintedNode = paintableElementClass == null ? null : (Node) metaModel.createElement(paintableElementClass);
        setPreferredSize(PREFRERRED_SIZE);
    }

    /**
     * @param metaModel
     * @param paintableElementClass
     * @return
     */
    public static ToolButton createNodeButton(final MetaModelInstance metaModel, final Class<? extends Node> paintableElementClass) {
        final ToolButton button = new ToolButton(metaModel, paintableElementClass);
        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (button.frame != null) {
                    InputGraphArea area = button.frame.getInputGraphArea();
                    area.setMouseMakesKnot(paintableElementClass);
                }
            }
        });
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        button.setToolTipText(elementsNameBuilder.getDisplayableName(paintableElementClass));
        button.setIcon("ICON_LARGE_BACKGROUND_NORMAL.gif");
        button.setSelectedIcon("ICON_LARGE_BACKGROUND_SELECTED.gif");
        CSH.setHelpIDString(button, "GRAPH_TOOLBAR_" + paintableElementClass.getSimpleName());
        button.setPreferredSize(PREFRERRED_SIZE);
        button.setSize(PREFRERRED_SIZE);
        return button;
    }

    /**
     * @param metaModel
     * @param dummyPaintableElementClass
     *            irgendeine der grafisch darstellbaren Klassen, die man braucht, um einen Strich auf den Button der Kanten zu malen
     * @return
     */
    public static ToolButton createEdgeButton(final MetaModelInstance metaModel, final Class<? extends Node> dummyPaintableElementClass) {
        ToolButton button = new ToolButton(null, dummyPaintableElementClass);
        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (button.frame != null) {
                    InputGraphArea area = button.frame.getInputGraphArea();
                    area.setMouseMakesTrace(true);
                }
            }
        });
        ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
        button.setToolTipText(elementsNameBuilder.getDisplayableName(Edge.class));
        button.setIcon("ICON_LARGE_BACKGROUND_NORMAL.gif");
        button.setSelectedIcon("ICON_LARGE_BACKGROUND_SELECTED.gif");
        return button;
    }

    public static ToolButton createDisableMouseMakesElementsButton() {
        ToolButton button = new ToolButton(null, null);
        button.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (button.frame != null) {
                    InputGraphArea area = button.frame.getInputGraphArea();
                    area.setMouseMakesKnot(null);
                    area.setMouseMakesTrace(false);
                }
            }
        });
        button.setToolTipText(getResString("GRAPH_TOOLBAR_TOOLTP_SWITCH_MOUSE_MODUS_BUTTON"));
        button.setIcon("ICON_LARGE_ACTION_MOUSE_MODUS_NORMAL.gif");
        button.setSelectedIcon("ICON_LARGE_ACTION_MOUSE_MODUS_SELECTED.gif");
        CSH.setHelpIDString(button, "GRAPH_TOOLBAR_switchMouseMode");
        return button;
    }

    private void setIcon(final String reskey) {
        setIcon(Tool3lgmConstants.getIcon(reskey));
    }

    private void setSelectedIcon(final String reskey) {
        setSelectedIcon(Tool3lgmConstants.getIcon(reskey));
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        GraphDocument gd = Static.getSelectedDoc();
        if (gd == null) {
            return;
        }
        if (paintedNodeContainer != null) {
            paintedNodeContainer.setLocation(getWidth() / 2, getHeight() / 2);
            paintedNodeContainer.paint(g);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return PREFRERRED_SIZE;
    }

    public void setFrame(final InternalGraphFrame frame) {
        if (frame != null && paintedNode != null) {
            GraphDocument doc = frame.getGraphDocument();
            paintedNodeContainer = new NodeContainer(paintedNode, doc);
            if (isEdgeButton) {
                paintedNodeContainer.setSizeForButtons(18, 2);
                paintedNodeContainer.setForm(GraphElementLayout.SHAPE.rechteck);
                paintedNodeContainer.setColor(Color.BLACK);
            } else {
                paintedNodeContainer.setSizeForButtons(18, 14);
            }
        }
        this.frame = frame;
    }

    public boolean isButtonForLayer(final int layer) {
        return paintedNode.layerFor() == layer;
    }

    public boolean hasNodeClass(final Class<? extends Node> nodeClass) {
        return paintedNode.getClass() == nodeClass;
    }
}
