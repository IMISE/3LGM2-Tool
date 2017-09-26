package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.help.CSH;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;
import de.imise.util.swing.component.UnfloatableToolBar;

public class InternalGraphFrameToolBar extends UnfloatableToolBar implements ActionListener, ChangeListener {

    /**
     * COMMENTME
     */
    public JSlider winkel, zoom, abstand;

    /**
     * COMMENTME
     */
    private final JLabel zoomlabel, winkellabel, abstandlabel;

    /**
     * COMMENTME
     */
    private JToggleButton button = null;

    /**
     * COMMENTME
     */
    private final ToolButton aufgabe, objekttyp, rechAwbaustein, konAwbaustein, dvbaustein, kante;

    /**
     * COMMENTME
     */
    private ButtonGroup bg;

    private static final Multimap<Integer, Class<? extends ModelElement>> layerGraphElementClasses = getLayerGraphElementClasses();

    /**
     * @param frame
     */
    public InternalGraphFrameToolBar() {
        super();
        button = new JToggleButton(Tool3lgmConstants.getIcon("fill.gif"));
        button.setToolTipText(getResString("el_mark_bearb"));
        button.setActionCommand("Maus");
        button.addActionListener(this);
        button.setSelected(true);
        button.setPreferredSize(new Dimension(30, 30));
        CSH.setHelpIDString(button, "wl_maus");
        zoomlabel = new JLabel(getResString("zoom"));
        zoom = new JSlider(10, 200);
        zoom.setPreferredSize(new Dimension(150, 30));
        winkellabel = new JLabel(getResString("winkel"));
        winkel = new JSlider(0, 80);
        winkel.setPreferredSize(new Dimension(150, 30));
        abstandlabel = new JLabel(getResString("abstand"));
        double pageSizeFactor = frame.getSzenario().getPageSizeFactor();
        abstand = new JSlider(0, new Double(800 * pageSizeFactor).intValue());
        Szenario szen = (Szenario) frame.getSzenario();
        if (szen.getViewParameter() != null) {
            zoom.setValue((int) (szen.getViewParameter().zoom * 100));
            winkel.setValue(szen.getViewParameter().degree);
            abstand.setValue(szen.getViewParameter().shift);
        } else {
            InputGraphArea area = frame.getInputGraphArea();
            if (area != null) {
                zoom.setValue(80);
                winkel.setValue(area.getDegree());
                abstand.setValue(area.getPitchShift());
            } else {
                zoom.setValue(80);
                winkel.setValue(75);
                abstand.setValue(new Double(200 * pageSizeFactor).intValue());
            }
        }
        abstand.setPreferredSize(new Dimension(150, 30));
        abstand.addChangeListener(this);
        winkel.addChangeListener(this);
        zoom.addChangeListener(this);

        // Aufgabe
        aufgabe = new ToolButton(new Aufgabe(), frame.getGraphDocument());
        aufgabe.setToolTipText(ModelConstants.getDisplayableName(Aufgabe.class));
        aufgabe.addActionListener(this);
        aufgabe.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        aufgabe.setPreferredSize(new Dimension(30, 30));
        CSH.setHelpIDString(aufgabe, "wl_aufgabe");
        // Objekttyp
        objekttyp = new ToolButton(new Objekttyp(), frame.getGraphDocument());
        objekttyp.setToolTipText(ModelConstants.getDisplayableName(Objekttyp.class));
        objekttyp.addActionListener(this);
        objekttyp.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(objekttyp, "wl_objekttyp");
        // RechAnwendungsbaustein
        rechAwbaustein = new ToolButton(new RechAnwendungsbaustein(), frame.getGraphDocument());
        rechAwbaustein.setToolTipText(ModelConstants.getDisplayableName(RechAnwendungsbaustein.class));
        rechAwbaustein.addActionListener(this);
        rechAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(rechAwbaustein, "wl_anwendungsbaustein");
        // KonAnwendungsbaustein
        konAwbaustein = new ToolButton(new KonAnwendungsbaustein(), frame.getGraphDocument());
        konAwbaustein.setToolTipText(ModelConstants.getDisplayableName(KonAnwendungsbaustein.class));
        konAwbaustein.addActionListener(this);
        konAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(konAwbaustein, "wl_anwendungsbaustein");
        // PhysischerDVBaustein
        dvbaustein = new ToolButton(new PhysischerDVBaustein(), frame.getGraphDocument());
        dvbaustein.setToolTipText(ModelConstants.getDisplayableName(PhysischerDVBaustein.class));
        dvbaustein.addActionListener(this);
        dvbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(dvbaustein, "wl_datenverarbeitungsbaustein");
        // Edge
        kante = new ToolButton(new AufObjVerbindung(), frame.getGraphDocument());
        kante.setToolTipText(ModelConstants.getDisplayableName(Edge.class));
        kante.addActionListener(this);
        kante.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));

        CSH.setHelpIDString(this, "ansichtswerkzeuge");
    }

    private static Multimap<Integer, Class<? extends ModelElement>> getLayerGraphElementClasses() {
        Multimap<Integer, Class<? extends ModelElement>> layerGraphElementClasses = ArrayListMultimap.create();
        GraphViewDefinition graphViewDefinition = ModelConstants.getGraphViewDefinition();
        for (Class<? extends ModelElement> elementClass : ModelConstants.ALL_ELEMENTS) {
            int layer = ModelConstants.layerFor(elementClass);
            if (graphViewDefinition.isPaintable(elementClass)) {
                layerGraphElementClasses.put(layer, elementClass);
            }
        }
        return layerGraphElementClasses;
    }

    public void setFrame(final InternalGraphFrame frame) {
        if (!(frame.getSzenario() instanceof Szenario)) {
            return;
        }

        double pageSizeFactor = frame.getSzenario().getPageSizeFactor();
        abstand.setMaximum(new Double(800 * pageSizeFactor).intValue());
        Szenario szen = (Szenario) frame.getSzenario();
        if (szen.getViewParameter() != null) {
            zoom.setValue((int) (szen.getViewParameter().zoom * 100));
            winkel.setValue(szen.getViewParameter().degree);
            abstand.setValue(szen.getViewParameter().shift);
        } else {
            InputGraphArea area = frame.getInputGraphArea();
            if (area != null) {
                zoom.setValue(80);
                winkel.setValue(area.getDegree());
                abstand.setValue(area.getPitchShift());
            } else {
                zoom.setValue(80);
                winkel.setValue(75);
                abstand.setValue(new Double(200 * pageSizeFactor).intValue());
            }
        }

    }

    public void addButtonsFachlich() {
        removeAll();
        if (button == null) {
            return;
        }

        // add(but);
        add(button);
        add(aufgabe);
        add(objekttyp);
        add(kante);
        addSeparator();
        bg = new ButtonGroup();
        bg.add(button);
        bg.add(aufgabe);
        bg.add(objekttyp);
        bg.add(kante);
        addSlider();
        revalidate();
        repaint();
    }

    public void addButtonsLogisch() {
        removeAll();
        if (button == null) {
            return;
        }

        // add(but);
        add(button);
        add(rechAwbaustein);
        add(konAwbaustein);
        add(kante);
        addSeparator();
        bg = new ButtonGroup();
        bg.add(button);
        bg.add(rechAwbaustein);
        bg.add(konAwbaustein);
        bg.add(kante);
        addSlider();
        revalidate();
        repaint();
    }

    public void addButtonsPhysisch() {
        removeAll();
        if (button == null) {
            return;
        }

        // add(but);
        add(button);
        add(dvbaustein);
        add(kante);
        addSeparator();
        bg = new ButtonGroup();
        bg.add(button);
        bg.add(dvbaustein);
        bg.add(kante);
        addSlider();
        revalidate();
        repaint();
    }

    private void addSlider() {
        AbstractInternalFrame frame = Static.getActiveFrame();
        if (frame == null) {
            return;
        }
        if (!(frame instanceof InternalGraphFrame)) {
            return;
        }

        InputGraphArea area = ((InternalGraphFrame) frame).getInputGraphArea();
        add(zoomlabel);
        add(zoom);
        if (area.isMultiViewEnabled()) {
            add(winkellabel);
            add(winkel);
            add(abstandlabel);
            add(abstand);
        }
    }

    protected void clearButtons() {
        button.setIcon(Tool3lgmConstants.getIcon("kreis.gif"));
        aufgabe.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        objekttyp.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        rechAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        konAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        dvbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        kante.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
    }

    public void setMausModus() {
        if (button == null) {
            return;
        }

        clearButtons();
        AbstractInternalFrame frame = Static.getActiveFrame();
        if (frame == null) {
            return;
        }
        if (!(frame instanceof InternalGraphFrame)) {
            return;
        }

        InputGraphArea area = ((InternalGraphFrame) frame).getInputGraphArea();
        button.setIcon(Tool3lgmConstants.getIcon("fill.gif"));
        button.setSelected(true);
        area.setMouseMakesKnot(null);
        area.setMouseMakesTrace(false);
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        if (button == null) {
            return;
        }

        AbstractInternalFrame frame = Static.getActiveFrame();
        if (frame == null) {
            return;
        }
        if (!(frame instanceof InternalGraphFrame)) {
            return;
        }

        if (e.getSource() == winkel) {
            InputGraphArea area = ((InternalGraphFrame) frame).getInputGraphArea();
            area.setDegree(winkel.getValue());
        }
        if (e.getSource() == abstand) {
            InputGraphArea area = ((InternalGraphFrame) frame).getInputGraphArea();
            area.setInterLayerSpace(abstand.getValue());
        }
        if (e.getSource() == zoom) {
            InputGraphArea area = ((InternalGraphFrame) frame).getInputGraphArea();
            area.setZoom((double) zoom.getValue() / 100);
        }
    }

    /**
     * @param value
     */
    public void setZoom(final double value) {
        zoom.setValue((int) (value * 100d));
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (button == null) {
            return;
        }

        AbstractInternalFrame frame = Static.getActiveFrame();
        InputGraphArea area = null;
        if (frame != null && frame instanceof InternalGraphFrame) {
            area = ((InternalGraphFrame) frame).getInputGraphArea();
        }
        clearButtons();
        // String str = e.getActionCommand();
        if (e.getSource() == button) {
            setMausModus();
            return;
        }
        if (e.getSource() == aufgabe) {
            aufgabe.setIcon(Tool3lgmConstants.getIcon("dummy.gif"));
            if (area != null) {
                area.setMouseMakesKnot(Aufgabe.class);
            }
        }
        if (e.getSource() == objekttyp) {
            objekttyp.setIcon(Tool3lgmConstants.getIcon("dummy.gif"));
            if (area != null) {
                area.setMouseMakesKnot(Objekttyp.class);
            }
        }
        if (e.getSource() == rechAwbaustein) {
            rechAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy.gif"));
            if (area != null) {
                area.setMouseMakesKnot(RechAnwendungsbaustein.class);
            }
        }
        if (e.getSource() == konAwbaustein) {
            rechAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy.gif"));
            if (area != null) {
                area.setMouseMakesKnot(KonAnwendungsbaustein.class);
            }
        }
        if (e.getSource() == dvbaustein) {
            dvbaustein.setIcon(Tool3lgmConstants.getIcon("dummy.gif"));
            if (area != null) {
                area.setMouseMakesKnot(PhysischerDVBaustein.class);
            }
        }
        if (e.getSource() == kante) {
            kante.setIcon(Tool3lgmConstants.getIcon("dummy.gif"));
            if (area != null) {
                area.setMouseMakesTrace(true);
            }
        }
    }
}
