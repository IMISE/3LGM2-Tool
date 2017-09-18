package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.gui.ToolInternalFrame;
import de.imise.tool3lgm.gui.Werkzeugleiste;

/**
 * Dialog zum Einstellen von Ebenenabstand, Zoom, Winkel und Größe der Zeichenfläche
 *
 * @author N.N.
 */
public class EinstellungDialog extends JDialog implements ChangeListener, ActionListener {

    /**
     * COMMENTME
     */
    private JTextField textzoom, textwinkel, textabstand;

    /**
     * COMMENTME
     */
    private JTextField textPageSizeFactor;

    /**
     * COMMENTME
     */
    private JSlider pageSizeFactorSlider;

    /**
     * COMMENTME
     */
    private double pageSizeFactorBackup = 1d;

    /**
     * COMMENTME
     */
    private ToolInternalFrame frame;

    /**
     * COMMENTME
     */
    private JSlider zoom, winkel, abstand;

    /**
     * 
     */
    public EinstellungDialog() {
        super();
    }

    /**
     * @param f
     */
    public void showDialog(final ToolInternalFrame f) {
        // Dialog nur für Teilmodelle anzeigen, da nur Teilmodelle einen Grafischen View besitzen
        if (!(f.getGraphDocument() instanceof Szenario)) {
            return;
        }

        frame = f;
        Werkzeugleiste leiste = (Werkzeugleiste) frame.getToolBar();
        setTitle(getResString("settings") + getResString("3points"));
        setSize(340, 180);

        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel();

        BorderLayout bl = new BorderLayout();
        panel.setLayout(bl);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new GridLayout(4, 1));
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1));
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new GridLayout(4, 1));

        JLabel text = new JLabel(getResString("zoom"));
        zoom = new JSlider(leiste.zoom.getMinimum(), leiste.zoom.getMaximum());
        zoom.setValue(leiste.zoom.getValue());
        zoom.addChangeListener(this);
        textzoom = new JTextField(4);
        textzoom.setText(new Integer(zoom.getValue()).toString());
        textzoom.setEditable(false);
        westPanel.add(text);
        centerPanel.add(zoom);
        eastPanel.add(textzoom);

        JLabel textz = new JLabel(getResString("page_zoom"));
        pageSizeFactorBackup = frame.getGraphDocument().getPageSizeFactor();
        pageSizeFactorSlider = new JSlider(100, 1000);
        pageSizeFactorSlider.setValue((int) (pageSizeFactorBackup * 100));
        pageSizeFactorSlider.addChangeListener(this);
        textPageSizeFactor = new JTextField(4);
        textPageSizeFactor.setText(new Double(pageSizeFactorSlider.getValue() / 100d).toString());
        textPageSizeFactor.setEditable(false);
        westPanel.add(textz);
        centerPanel.add(pageSizeFactorSlider);
        eastPanel.add(textPageSizeFactor);

        JLabel textw = new JLabel(getResString("winkel"));
        winkel = new JSlider(leiste.winkel.getMinimum(), leiste.winkel.getMaximum());
        winkel.setValue(leiste.winkel.getValue());
        winkel.addChangeListener(this);
        textwinkel = new JTextField(4);
        JLabel texta = new JLabel(getResString("abstand"));
        textwinkel.setText(new Integer(winkel.getValue()).toString());
        textwinkel.setEditable(false);

        abstand = new JSlider(leiste.abstand.getMinimum(), leiste.abstand.getMaximum());
        abstand.setValue(leiste.abstand.getValue());
        abstand.addChangeListener(this);
        textabstand = new JTextField(4);
        textabstand.setText(new Integer(abstand.getValue()).toString());
        textabstand.setEditable(false);
        if (frame.getInputGraphArea().isMultiViewEnabled()) {
            westPanel.add(textw);
            centerPanel.add(winkel);
            eastPanel.add(textwinkel);
            westPanel.add(texta);
            centerPanel.add(abstand);
            eastPanel.add(textabstand);
        }

        panel.add(westPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(eastPanel, BorderLayout.EAST);

        JPanel buttonpanel = new JPanel();
        JButton b = new JButton(getResString("close"));
        b.addActionListener(this);
        buttonpanel.add(b);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        textzoom.setText(new Integer(zoom.getValue()).toString());
        textwinkel.setText(new Integer(winkel.getValue()).toString());
        textabstand.setText(new Integer(abstand.getValue()).toString());
        frame.getGraphDocument().setPageSizeFactor(pageSizeFactorSlider.getValue() / 100d);

        InputGraphArea area = frame.getInputGraphArea();
        area.setZoom((double) Integer.parseInt(textzoom.getText()) / 100);
        zoom.setValue(Integer.parseInt(textzoom.getText()));
        Werkzeugleiste leiste = (Werkzeugleiste) frame.getToolBar();
        leiste.zoom.setValue(Integer.parseInt(textzoom.getText()));
        if (frame.getInputGraphArea().isMultiViewEnabled()) {
            area.setDegree(Integer.parseInt(textwinkel.getText()));
            area.setInterLayerSpace(Integer.parseInt(textabstand.getText()));
            winkel.setValue(Integer.parseInt(textwinkel.getText()));
            abstand.setValue(Integer.parseInt(textabstand.getText()));
            leiste.winkel.setValue(Integer.parseInt(textwinkel.getText()));
            leiste.abstand.setValue(Integer.parseInt(textabstand.getText()));
        }
        textPageSizeFactor.setText(new Double(frame.getGraphDocument().getPageSizeFactor()).toString());
        leiste.abstand.setMaximum(new Double(800 * frame.getGraphDocument().getPageSizeFactor()).intValue());
        abstand.setMaximum(leiste.abstand.getMaximum());
        // in der Einzelansicht den Viewpoint korrekt aktualisieren
        if (!area.isMultiViewEnabled()) {
            area.setInterLayerSpace(0);
        }
        frame.layoutChanged(frame.getGraphDocument());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String str = e.getActionCommand();
        if (str.equals(getResString("close"))) {
            frame.getGraphDocument().setPageSizeFactor(pageSizeFactorBackup, pageSizeFactorSlider.getValue() / 100d, true, TransactionManager.STANDARD_PID);
            dispose();
        }
    }
}
