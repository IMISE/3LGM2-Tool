package de.imise.tool3lgm.gui;

import java.util.ArrayList;
import java.util.List;

import javax.help.CSH;
import javax.swing.ButtonGroup;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
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

    private int currentLayer = -1;

    /** Mappt vom LayerIndex auf die Elementklassen, für die auf der Toolbar ein Button angezeigt werden soll */
    //    private static final Multimap<Integer, Class<? extends Node>> layerGraphElementClasses = getLayerGraphElementClasses();

    /** irgendeine der grafisch darstellbaren Klassen, die man braucht, um einen Strich auf den Button der Kanten zu malen */
    private static Class<? extends Node> dummyEdgeButtonNodeClass;

    /**
     * @param frame
     */
    public GraphAreaToolBar(final InternalGraphFrame frame) {
        super();
        buttonSwitchMouseMode = ToolButton.createDisableMouseMakesElementsButton();
        MetaModel metaModel = frame.doc.getMetaModel();
        buttonCreateEdge = ToolButton.createEdgeButton(metaModel, dummyEdgeButtonNodeClass);
        buttonGroup.add(buttonSwitchMouseMode);
        buttonGroup.add(buttonCreateEdge);
        sliders = new GraphAreaOptionSliders(frame, 150, 30);
        setFrame(frame);
        CSH.setHelpIDString(this, "GRAPH_TOOLBAR_ansichtswerkzeuge");
    }

    //AXS 28.05.2019: als das Metamodell statisch war, wurde diese Map hier gefüllt. Sollte der Kontext-Switch zu langsam sein, kann man das wieder so oder so
    //ähnlich machen, aber ich habe das erstmal durch die Variante ersetzt, in der die Liste jedes Mal einfach komplett neu aufgebaut wird
    //    private static Multimap<Integer, Class<? extends Node>> getLayerGraphElementClasses() {
    //        Multimap<Integer, Class<? extends Node>> layerGraphElementClasses = ArrayListMultimap.create();
    //        MetaModel selectedMetaModel = Static.getSelectedMetaModel();
    //        for (Class<? extends ModelElement> elementClass : selectedMetaModel.allNodesSet) {
    //            if (MetaModel.isNodeType(elementClass)) { //Assoziationsklassen stehn auch in ALL_NODES -> rausfiltern
    //                if (selectedMetaModel.isPaintable(elementClass)) { //Knoten muss natürlich zeichenbar sein
    //                    if (!selectedMetaModel.isSlaveType(elementClass)) { // Knoten darf kein untergeordnetes Element sein
    //                        int layer = selectedMetaModel.layerFor(elementClass);
    //                        Class<? extends Node> nodeClass = elementClass.asSubclass(Node.class);
    //                        layerGraphElementClasses.put(layer, nodeClass);
    //                        if (dummyEdgeButtonNodeClass == null) {
    //                            dummyEdgeButtonNodeClass = nodeClass;
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        return layerGraphElementClasses;
    //    }

    private List<Class<? extends Node>> getLayerGraphElementClasses(final int layerIndex) {
        MetaModel metaModel = frame.doc.getMetaModel();
        List<Class<? extends Node>> paintableNodeClasses = new ArrayList<>(5);
        for (Class<? extends ModelElement> elementClass : metaModel.allNodesSet) {
            if (MetaModel.isNodeType(elementClass)) { //Assoziationsklassen stehn auch in ALL_NODES -> rausfiltern
                if (metaModel.isPaintable(elementClass)) { //Knoten muss natürlich zeichenbar sein
                    if (!metaModel.isSlaveType(elementClass)) { // Knoten darf kein untergeordnetes Element sein
                        if (layerIndex == metaModel.layerFor(elementClass)) { //layer muss stimmen
                            Class<? extends Node> nodeClass = elementClass.asSubclass(Node.class);
                            paintableNodeClasses.add(nodeClass);
                            if (dummyEdgeButtonNodeClass == null) {
                                dummyEdgeButtonNodeClass = nodeClass;
                            }
                        }
                    }
                }
            }
        }
        return paintableNodeClasses;
    }

    void setFrame(final InternalGraphFrame frame) {
        this.frame = frame;
        sliders.setFrame(frame);
        update();
    }

    private void updateLayer() {
        if (frame == null) {
            return;
        }
        GDCollection gdcoll = frame.getCollection();
        int activeLayer = gdcoll.getActiveLayer();
        if (currentLayer != activeLayer) {
            currentLayer = activeLayer;
            for (ToolButton button : buttonsCreateElement) {
                buttonGroup.remove(button);
            }
            buttonsCreateElement.clear();
            MetaModel metaModel = frame.doc.getMetaModel();
            for (Class<? extends Node> paintableLayerElementClass : getLayerGraphElementClasses(currentLayer)) {
                ToolButton createNodeButton = ToolButton.createNodeButton(metaModel, paintableLayerElementClass);
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

    @Override
    public void update() {
        sliders.updateValues();
        updateLayer();
        revalidate();
        repaint();
    }

}