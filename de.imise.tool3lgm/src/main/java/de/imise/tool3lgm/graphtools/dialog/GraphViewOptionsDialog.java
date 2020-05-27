package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Static.getMainFrame;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.gui.viewpane.graph.GraphAreaOptionSliders;
import de.imise.tool3lgm.gui.viewpane.graph.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.viewpane.graph.GraphAreaOptionSliders.SliderWithTextField;

/**
 * Dialog zum Einstellen von Ebenenabstand, Zoom, Winkel und Größe der Zeichenfläche
 *
 * @author N.N.
 */
public class GraphViewOptionsDialog extends JDialog {

    private GraphAreaOptionSliders sliders;

    double pageSizeFactorBackup;

    private final JPanel labelsPanel = createPanel();

    private final JPanel slidersPanel = createPanel();

    private final JPanel textFieldsPanel = createPanel();

    /**
     *
     */
    public GraphViewOptionsDialog() {
        super(getMainFrame());
    }

    private static final JPanel createPanel() {
        return new JPanel(new GridLayout(4, 1));
    }

    /**
     * @param frame
     */
    public void showDialog(final GraphViewPaneFrameComponent frame) {
        // Dialog nur für Teilmodelle anzeigen, da nur Teilmodelle einen Grafischen View besitzen
        if (!(frame.getGraphDocument() instanceof Szenario)) {
            return;
        }
        if (sliders == null) {
            sliders = new GraphAreaOptionSliders(frame);
        } else {
            sliders.setFrame(frame);
        }

        setTitle(getResString("OPTIONS_GRAPH_VIEW_DIALOG_TITLE"));
        setSize(340, 180);

        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        JPanel panel = new JPanel();

        BorderLayout bl = new BorderLayout();
        panel.setLayout(bl);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(sliders.getSliderZoom());
        add(sliders.getSliderPageSizeFactor());

        if (frame.getInputGraphArea().isMultiView()) {
            add(sliders.getSliderDegree());
            add(sliders.getSliderGap());
        }

        panel.add(labelsPanel, BorderLayout.WEST);
        panel.add(slidersPanel, BorderLayout.CENTER);
        panel.add(textFieldsPanel, BorderLayout.EAST);

        JPanel buttonpanel = new JPanel();
        JButton closeButton = new JButton(new AbstractAction(getResString("close")) {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        });
        buttonpanel.add(closeButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void add(final SliderWithTextField slider) {
        labelsPanel.add(slider.getLabel());
        slidersPanel.add(slider);
        textFieldsPanel.add(slider.getTextField());
    }

}
