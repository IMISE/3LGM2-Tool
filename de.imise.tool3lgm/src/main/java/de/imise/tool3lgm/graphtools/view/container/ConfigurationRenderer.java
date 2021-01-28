package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.InterLayerLineRenderType.LINE_TYPE_SOLID;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_COLORS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ASSIGN_CONFIGURATION_COLORS;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.InterLayerLineRenderType;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;

public class ConfigurationRenderer {

    /**
     * {@link GraphElementLayout#STANDARD_COLORS} defines a list of colors. This
     * colors will be used to paint the different configuratons. If there are
     * more configurations in the graph than colors, the counter starts at 0
     * again.
     */
    public static int colorCounter = 0;

    /**
     * Renders the configuration from the <code>configurationStart</code> to all
     * of its ends.
     *
     * @param g the target graphics objetct where the rendering is done
     * @param configurationStart the container whose configurations should be
     *            rendered
     */
    public static final void render(final Graphics g, final InterLayerConnectedNodeContainer configurationStart) {
        Szenario szen = (Szenario) configurationStart.getGraphDocument(); //wenn das kein Szenario ist, dann sollte dieser Renderer auch nicht aufgerufen werden
        boolean configurationStartIsAnalysisResult = szen.isAnalysisResult(configurationStart);
        if (!configurationStart.isShowInterLayerConnections()) {
            if (!configurationStartIsAnalysisResult) {
                return;
            }
        }

        List<ElementContainer> configurationStartContainer = null;
        List<ElementContainer> configurationEndContainer = null;
        int x_shift = Integer.MAX_VALUE;
        int y_shift = Integer.MAX_VALUE;
        if (!configurationStart.isVisible()) {
            configurationStartContainer = configurationStart.getSurrogateContainer();
        } else {
            configurationStartContainer = new ArrayList<>(1);
            configurationStartContainer.add(configurationStart);
        }

        GraphViewParameter graphViewParameter = Static.getGraphViewParameter(szen); //ACHTUNG DAS HIER PASSIERT BEI JEDEM RENDERN UND IST RELATIV AUFWENDIG, WENN ES VIELE OFFENE FRAMES GIBT -> GGF. OPTIMIEREN!
        for (int b = 0; b < configurationStartContainer.size(); b++) {
            if (configurationStart.getInterLayerConnectionColor() == null) {
                colorCounter = (colorCounter + 1) % STANDARD_COLORS.length;
                configurationStart.setInterLayerConnectionColor(STANDARD_COLORS[colorCounter]);
            }
            Color elem_col = null;
            if (OPTION_ASSIGN_CONFIGURATION_COLORS.is()) {
                elem_col = configurationStart.getInterLayerConnectionColor();
            } else {
                elem_col = Color.black;
            }
            g.setColor(elem_col);

            InterLayerConnectedNodeContainer kc1 = (InterLayerConnectedNodeContainer) configurationStartContainer.get(b);
            if (!kc1.isVisible()) {
                continue;
            }
            if (graphViewParameter.multiView) {
                Graphics2D gc = (Graphics2D) g;
                Stroke oldStroke = gc.getStroke();
                InterLayerLineRenderType interLayerLineRenderType = LINE_TYPE_SOLID;
                if (configurationEndContainer == null) {
                    configurationEndContainer = new ArrayList<>();
                    ModelElement me = configurationStart.getElement();
                    MetaModel metaModel = szen.getMetaModel();
                    GraphViewDefinition graphViewDefinition = metaModel.getGraphViewDefinition();
                    MetaPath interLayerMetaPath = graphViewDefinition.getInterLayerMetaPath(me);
                    Collection<ModelElement> interLayerConnectedElements = interLayerMetaPath.getConnectedElements(me);
                    for (ModelElement connected : interLayerConnectedElements) {
                        ElementContainer connectedEc = connected.getContainer(szen);
                        if (connectedEc != null) {
                            configurationEndContainer.add(connectedEc);
                        }
                    }
                    int layerOfStartElement = metaModel.layerFor(me.getClass());
                    int layerOfEndElement = metaModel.layerFor(interLayerMetaPath.getEndClass());
                    int shiftCount = (layerOfStartElement - layerOfEndElement) / 2;
                    LayerContainer layer = szen.getLayer(layerOfEndElement);
                    x_shift = (int) layer.x_shift * shiftCount;
                    y_shift = (int) layer.y_shift * shiftCount;
                    interLayerLineRenderType = graphViewDefinition.getInterLayerLineRenderType(interLayerMetaPath);
                }
                for (ElementContainer connectedEc : configurationEndContainer) {
                    List<ElementContainer> c2C = null;
                    if (!connectedEc.isVisible()) {
                        c2C = connectedEc.getSurrogateContainer();
                    } else {
                        c2C = ImmutableList.of(connectedEc);
                    }

                    for (int d = 0; d < c2C.size(); d++) {
                        NodeContainer kc2 = (NodeContainer) c2C.get(d);
                        if (!kc2.isVisible()) {
                            continue;
                        }
                        if (configurationStartIsAnalysisResult && szen.isAnalysisResult(connectedEc)) {
                            Color analysisColor = NodeRenderer.getAnalysisColor();
                            g.setColor(analysisColor);
                            Stroke configurationStroke = interLayerLineRenderType.getAnalysisResultStroke();
                            gc.setStroke(configurationStroke);
                            g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() - x_shift, kc2.getY() - y_shift);
                            g.setColor(elem_col);
                        } else {
                            Stroke configurationStroke = interLayerLineRenderType.getStroke();
                            gc.setStroke(configurationStroke);
                            g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() - x_shift, kc2.getY() - y_shift);
                        }
                    }
                }
                gc.setStroke(oldStroke);
            }
        }
    }

}
