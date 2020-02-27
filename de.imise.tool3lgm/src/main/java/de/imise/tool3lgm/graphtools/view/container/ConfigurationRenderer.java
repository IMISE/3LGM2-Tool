package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.MEDUIM_STROKE;
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

import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.InternalGraphFrame;

public class ConfigurationRenderer {

    /**
     * {@link GraphElementLayout#STANDARD_COLORS} defines a list of colors. This colors
     * will be used to paint the different configuratons. If there are more configurations
     * in the graph than colors, the counter starts at 0 again.
     */
    public static int colorCounter = 0;

    /**
     * Renders the configuration from the <code>configurationStart</code> to all of its ends.
     *
     * @param g the target graphics objetct where the rendering is done
     * @param configurationStart the container whose configurations should be rendered
     * @param doc the submodel in which the configurations should be shown
     */
    public static final void render(final Graphics g, final InterLayerConnectedNodeContainer configurationStart, final GraphDocument doc) {
        boolean configurationStartIsAnalysisResult = doc.isAnalysisResult(configurationStart);
        if (!configurationStart.isShowInterLayerConnections()) {
            if (!configurationStartIsAnalysisResult) {
                return;
            }
        }
        InternalGraphFrame frame = doc.getFrame();
        InputGraphArea inputGraphArea = frame.getInputGraphArea();
        boolean multiView = inputGraphArea.isMultiView();

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
            if (multiView) {
                Graphics2D gc = (Graphics2D) g;
                Stroke s = gc.getStroke();
                if (configurationEndContainer == null) {
                    configurationEndContainer = new ArrayList<>();
                    ModelElement me = configurationStart.getElement();
                    MetaModel metaModel = me.getMetaModel();
                    GraphViewDefinition graphViewDefinition = metaModel.getGraphViewDefinition();
                    SimpleMetaPath interLayerMetaPath = graphViewDefinition.getInterLayerMetaPath(me);
                    Collection<ModelElement> interLayerConnectedElements = MetaPathFunctions.getConnectedElements(me, interLayerMetaPath);
                    for (ModelElement connected : interLayerConnectedElements) {
                        ElementContainer connectedEc = connected.getContainer(doc);
                        if (connectedEc != null) {
                            configurationEndContainer.add(connectedEc);
                        }
                    }
                    int layerOfStartElement = metaModel.layerFor(me.getClass());
                    int layerOfEndElement = metaModel.layerFor(interLayerMetaPath.getEndClass());
                    int shiftCount = (layerOfStartElement - layerOfEndElement) / 2;
                    LayerContainer layer = doc.getLayer(layerOfEndElement);
                    x_shift = (int) layer.x_shift * shiftCount;
                    y_shift = (int) layer.y_shift * shiftCount;
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
                        if (configurationStartIsAnalysisResult && doc.isAnalysisResult(connectedEc)) {
                            g.setColor(Color.black);
                            gc.setStroke(MEDUIM_STROKE);
                            g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() - x_shift, kc2.getY() - y_shift);
                            gc.setStroke(s);
                            g.setColor(elem_col);
                        } else {
                            g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() - x_shift, kc2.getY() - y_shift);
                        }
                    }
                }
            }
        }
    }

}
