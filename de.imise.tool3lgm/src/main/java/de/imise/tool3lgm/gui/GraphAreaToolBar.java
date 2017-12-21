package de.imise.tool3lgm.gui;

import java.util.ArrayList;
import java.util.List;

import javax.help.CSH;
import javax.swing.ButtonGroup;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.GraphAreaOptionSliders.SliderWithTextField;
import de.imise.util.swing.component.UnfloatableToolBar;

public class GraphAreaToolBar extends UnfloatableToolBar {

    private final GraphAreaOptionSliders sliders;

    private final ToolButton buttonSwitchMouseMode;

    private final List<ToolButton> buttonsCreateElement = new ArrayList<>();

    private final ToolButton buttonCreateEdge;

    private InternalGraphFrame frame;

    private final ButtonGroup buttonGroup = new ButtonGroup();

    /** Mappt vom LayerIndex auf die Elementklassen, für die auf der Toolbar ein Button angezeigt werden soll */
    private static final Multimap<Integer, Class<? extends Node>> layerGraphElementClasses = getLayerGraphElementClasses();

    /** irgendeine der grafisch darstellbaren Klassen, die man braucht, um einen Strich auf den Button der Kanten zu malen */
    private static Class<? extends Node> dummyEdgeButtonNodeClass;

    /**
     * @param frame
     */
    public GraphAreaToolBar(final InternalGraphFrame frame) {
        super();
        buttonSwitchMouseMode = ToolButton.createDisableMouseMakesElementsButton();
        buttonCreateEdge = ToolButton.createEdgeButton(dummyEdgeButtonNodeClass);
        buttonGroup.add(buttonSwitchMouseMode);
        buttonGroup.add(buttonCreateEdge);
        sliders = new GraphAreaOptionSliders(frame, 150, 30);
        setFrame(frame);
        CSH.setHelpIDString(this, "GRAPH_TOOLBAR_ansichtswerkzeuge");
    }

    private static Multimap<Integer, Class<? extends Node>> getLayerGraphElementClasses() {
        Multimap<Integer, Class<? extends Node>> layerGraphElementClasses = ArrayListMultimap.create();
        for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_NODES) {
            if (ModelConstants.isNodeType(elementClass)) { //Assoziationsklassen stehn auch in ALL_NODES -> rausfiltern
                if (ModelConstants.isPaintable(elementClass)) { //Knoten muss natürlich zeichenbar sein
                    if (!ModelConstants.isSlaveType(elementClass)) { // Knoten darf kein untergeordnetes Element sein
                        int layer = ModelConstants.layerFor(elementClass);
                        Class<? extends Node> nodeClass = elementClass.asSubclass(Node.class);
                        layerGraphElementClasses.put(layer, nodeClass);
                        if (dummyEdgeButtonNodeClass == null) {
                            dummyEdgeButtonNodeClass = nodeClass;
                        }
                    }
                }
            }
        }
        return layerGraphElementClasses;
    }

    public void setFrame(final InternalGraphFrame frame) {
        this.frame = frame;
        sliders.setFrame(frame);
        Szenario szen = (Szenario) frame.getSzenario();
        setLayer(szen.getCollection().getActiveLayer());
    }

    public void setLayer(final int layer) {
        for (ToolButton button : buttonsCreateElement) {
            buttonGroup.remove(button);
        }
        buttonsCreateElement.clear();
        for (Class<? extends Node> paintableLayerElementClass : layerGraphElementClasses.get(layer)) {
            ToolButton createNodeButton = ToolButton.createNodeButton(paintableLayerElementClass);
            buttonsCreateElement.add(createNodeButton);
            createNodeButton.setFrame(frame);
            buttonGroup.add(createNodeButton);
        }
        buttonCreateEdge.setFrame(frame);
        buttonSwitchMouseMode.setFrame(frame);

        removeAll();
        add(buttonSwitchMouseMode);
        for (ToolButton button : buttonsCreateElement) {
            add(button);
        }
        add(buttonCreateEdge);
        addSeparator();
        addSliders();
        revalidate();
        repaint();

        InputGraphArea area = frame.getInputGraphArea();
        Class<? extends Node> mouseMakesNodeClass = area.getMouseMakesNodeClass();
        boolean mouseMakesEdge = area.isMouseMakesEdge();
        if (mouseMakesNodeClass != null) {
            buttonSwitchMouseMode.setSelected(mouseMakesNodeClass == null && !mouseMakesEdge);
            buttonCreateEdge.setSelected(mouseMakesEdge);
            for (ToolButton createNodeButton : buttonsCreateElement) {
                createNodeButton.setEnabled(createNodeButton.hasNodeClass(mouseMakesNodeClass));
            }
        }
        buttonSwitchMouseMode.setSelected(true);
    }

    private void addSliders() {
        InputGraphArea area = frame.getInputGraphArea();
        addSlider(sliders.getSliderZoom());
        if (area.isMultiView()) {
            addSlider(sliders.getSliderDegree());
            addSlider(sliders.getSliderGap());
        }
        addSlider(sliders.getSliderPageSizeFactor());
    }

    private void addSlider(final SliderWithTextField slider) {
        add(slider.getLabel());
        add(slider);
    }

    public void update() {
        sliders.updateValues();
    }

}