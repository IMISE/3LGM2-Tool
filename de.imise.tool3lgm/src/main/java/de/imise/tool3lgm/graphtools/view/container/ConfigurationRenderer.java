package de.imise.tool3lgm.graphtools.view.container;

import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.MEDUIM_STROKE;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.ToolInternalFrame;
import de.imise.tool3lgm.userproperties.UserProperties;

public class ConfigurationRenderer {

    public static Color[] farben = {
            Color.black,
            Color.blue,
            Color.cyan,
            Color.darkGray,
            Color.gray,
            Color.green,
            Color.magenta,
            Color.orange,
            Color.pink,
            Color.red,
            Color.yellow
    };

    public static int colorCounter = 0;

    private static final ConfigurationRenderer renderer = new ConfigurationRenderer();

    public static final void render(final Graphics g, final InterLayerConnectedNodeContainer configurationStart, final GraphDocument doc) {
        renderer.renderInternal(g, configurationStart, doc);
    }

    private void renderInternal(final Graphics g, final InterLayerConnectedNodeContainer configurationStart, final GraphDocument doc) {
        //        if (!isVisible() && !isHighLight()) {
        if (!configurationStart.isShowInterLayerConnections() && !configurationStart.isHighlightInterLayerConnections()) {
            return;
        }

        ToolInternalFrame frame = doc.getFrame();
        InputGraphArea inputGraphArea = frame.getInputGraphArea();
        boolean multiView = inputGraphArea.isMultiViewEnabled();

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
                colorCounter = (colorCounter + 1) % farben.length;
                configurationStart.setInterLayerConnectionColor(farben[colorCounter]);
            }
            Color elem_col = null;
            if (UserProperties.isAssignConfigurationColors()) {
                elem_col = configurationStart.getInterLayerConnectionColor();
            } else {
                elem_col = Color.black;
            }
            g.setColor(elem_col);

            //            NodeContainer kc1 = (NodeContainer) c1C.get(b);
            InterLayerConnectedNodeContainer kc1 = (InterLayerConnectedNodeContainer) configurationStartContainer.get(b);
            if (!kc1.isVisible()) {
                continue;
            }
            //            if (isSelected()) { //das hier war die Konfiguration, die selected war -> erstmal testweise durch highlight ersetzen
            //            if (kc1.isHighlightInterLayerConnections()) {
            //                g.setColor(Color.red);
            //                g.fillRect(kc1.getX() - 10, kc1.getY() - 10, 20, 20);
            //                g.setColor(elem_col);
            //            }
            if (multiView) {
                Graphics2D gc = (Graphics2D) g;
                Stroke s = gc.getStroke();
                if (configurationEndContainer == null) {
                    configurationEndContainer = new ArrayList<>();
                    ModelElement me = configurationStart.getElement();
                    Class<? extends ModelElement> elementClass = me.getClass();
                    GraphViewDefinition graphViewDefinition = ModelConstants.getGraphViewDefinition();
                    MetaPath interLayerMetaPath = graphViewDefinition.getInterLayerMetaPath(elementClass);
                    Set<ModelElement> directConnectedElements = PathFinder.getDirectConnectedElements(me, interLayerMetaPath);
                    for (ModelElement connected : directConnectedElements) {
                        ElementContainer connectedEc = connected.getContainer(doc);
                        if (connectedEc != null) {
                            configurationEndContainer.add(connectedEc);
                        }
                    }
                    int layerOfStartElement = ModelConstants.layerFor(elementClass);
                    int layerOfEndElement = ModelConstants.layerFor(interLayerMetaPath.getEndClass());
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
                        if (configurationStart.isHighlightInterLayerConnections()) {
                            g.setColor(Color.green);
                            gc.setStroke(MEDUIM_STROKE);
                            g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() + x_shift, kc2.getY() + y_shift);
                            gc.setStroke(s);
                            g.setColor(elem_col);
                        }

                        g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() - x_shift, kc2.getY() - y_shift);
                        //                        g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() + (int) x_shift, kc2.getY() + (int) y_shift);
                        //                        if (isSelected()) {
                        //                            g.setColor(Color.red);
                        //                            g.drawLine(kc1.getX(), kc1.getY(), kc2.getX() + (int) x_shift, kc2.getY() + (int) y_shift);
                        //                            g.setColor(elem_col);
                        //                        }
                    }
                }
            }
            //            }
        }
    }
}
