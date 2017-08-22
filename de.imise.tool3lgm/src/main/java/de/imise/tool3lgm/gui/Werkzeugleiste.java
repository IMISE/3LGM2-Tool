package de.imise.tool3lgm.gui;

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

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;
import de.imise.util.swing.component.UnfloatableToolBar;

public class Werkzeugleiste extends UnfloatableToolBar implements ActionListener, ChangeListener {

    /**
     * COMMENTME
     */
    public JSlider winkel, zoom, abstand;

    /**
     * COMMENTME
     */
    private JLabel zoomlabel, winkellabel, abstandlabel;

    /**
     * COMMENTME
     */
    private JToggleButton button = null;

    /**
     * COMMENTME
     */
    private ToolButton aufgabe, objekttyp, rechAwbaustein, konAwbaustein, dvbaustein, kante;

    /**
     * COMMENTME
     */
    private ButtonGroup bg;

    /**
     * @param f
     */
    public Werkzeugleiste(final ToolInternalFrame f) {
        super();
        if (!(f.getSzenario() instanceof Szenario)) {
            return;
        }

        button = new JToggleButton(Tool3lgmConstants.getIcon("fill.gif"));
        button.setToolTipText(Tool3lgmConstants.getResString("el_mark_bearb"));
        button.setActionCommand("Maus");
        button.addActionListener(this);
        button.setSelected(true);
        button.setPreferredSize(new Dimension(30, 30));
        CSH.setHelpIDString(button, "wl_maus");
        zoomlabel = new JLabel(Tool3lgmConstants.getResString("zoom"));
        zoom = new JSlider(10, 200);
        zoom.setPreferredSize(new Dimension(150, 30));
        winkellabel = new JLabel(Tool3lgmConstants.getResString("winkel"));
        winkel = new JSlider(0, 80);
        winkel.setPreferredSize(new Dimension(150, 30));
        abstandlabel = new JLabel(Tool3lgmConstants.getResString("abstand"));
        double pageSizeFactor = f.getSzenario().getPageSizeFactor();
        abstand = new JSlider(0, new Double(800 * pageSizeFactor).intValue());
        Szenario szen = (Szenario) f.getSzenario();
        if (szen.getViewParameter() != null) {
            zoom.setValue((int) (szen.getViewParameter().zoom * 100));
            winkel.setValue(szen.getViewParameter().degree);
            abstand.setValue(szen.getViewParameter().shift);
        } else {
            InputGraphArea area = f.getInputGraphArea();
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
        aufgabe = new ToolButton(new Aufgabe(), f.getGraphDocument());
        aufgabe.setToolTipText(ModelConstants.getDisplayableName(Aufgabe.class));
        aufgabe.addActionListener(this);
        aufgabe.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        aufgabe.setPreferredSize(new Dimension(30, 30));
        CSH.setHelpIDString(aufgabe, "wl_aufgabe");
        // Objekttyp
        objekttyp = new ToolButton(new Objekttyp(), f.getGraphDocument());
        objekttyp.setToolTipText(ModelConstants.getDisplayableName(Objekttyp.class));
        objekttyp.addActionListener(this);
        objekttyp.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(objekttyp, "wl_objekttyp");
        // RechAnwendungsbaustein
        rechAwbaustein = new ToolButton(new RechAnwendungsbaustein(), f.getGraphDocument());
        rechAwbaustein.setToolTipText(ModelConstants.getDisplayableName(RechAnwendungsbaustein.class));
        rechAwbaustein.addActionListener(this);
        rechAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(rechAwbaustein, "wl_anwendungsbaustein");
        // KonAnwendungsbaustein
        konAwbaustein = new ToolButton(new KonAnwendungsbaustein(), f.getGraphDocument());
        konAwbaustein.setToolTipText(ModelConstants.getDisplayableName(KonAnwendungsbaustein.class));
        konAwbaustein.addActionListener(this);
        konAwbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(konAwbaustein, "wl_anwendungsbaustein");
        // PhysischerDVBaustein
        dvbaustein = new ToolButton(new PhysischerDVBaustein(), f.getGraphDocument());
        dvbaustein.setToolTipText(ModelConstants.getDisplayableName(PhysischerDVBaustein.class));
        dvbaustein.addActionListener(this);
        dvbaustein.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));
        CSH.setHelpIDString(dvbaustein, "wl_datenverarbeitungsbaustein");
        // Edge
        kante = new ToolButton(new AufObjVerbindung(), f.getGraphDocument());
        kante.setToolTipText(ModelConstants.getDisplayableName(Edge.class));
        kante.addActionListener(this);
        kante.setIcon(Tool3lgmConstants.getIcon("dummy1.gif"));

        CSH.setHelpIDString(this, "ansichtswerkzeuge");
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
        if (!(frame instanceof ToolInternalFrame)) {
            return;
        }

        InputGraphArea area = ((ToolInternalFrame) frame).getInputGraphArea();
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
        if (!(frame instanceof ToolInternalFrame)) {
            return;
        }

        InputGraphArea area = ((ToolInternalFrame) frame).getInputGraphArea();
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
        if (!(frame instanceof ToolInternalFrame)) {
            return;
        }

        if (e.getSource() == winkel) {
            InputGraphArea area = ((ToolInternalFrame) frame).getInputGraphArea();
            area.setDegree(winkel.getValue());
        }
        if (e.getSource() == abstand) {
            InputGraphArea area = ((ToolInternalFrame) frame).getInputGraphArea();
            area.setInterLayerSpace(abstand.getValue());
        }
        if (e.getSource() == zoom) {
            InputGraphArea area = ((ToolInternalFrame) frame).getInputGraphArea();
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
        if (frame != null && frame instanceof ToolInternalFrame) {
            area = ((ToolInternalFrame) frame).getInputGraphArea();
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
