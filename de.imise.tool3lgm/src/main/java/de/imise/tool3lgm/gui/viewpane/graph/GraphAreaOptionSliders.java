package de.imise.tool3lgm.gui.viewpane.graph;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.DATA_CHANGED;
import static de.imise.tool3lgm.graphtools.undoredo.TransactionManager.STANDARD_PID;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.BasicGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.util.swing.component.MinMaxNumberTextField3;

public class GraphAreaOptionSliders implements ChangeListener {

    private SliderWithTextField sliderDegree, sliderZoom, sliderGap, sliderPageSizeFactor;

    private GraphViewPaneFrameComponent frame;

    public GraphAreaOptionSliders(final GraphViewPaneFrameComponent frame) {
        this(frame, -1, -1);
    }

    public GraphAreaOptionSliders(final GraphViewPaneFrameComponent frame, final int preferredSizeWidth, final int preferredSizeHeight) {
        this.frame = frame;
        init(preferredSizeWidth, preferredSizeHeight);
        addChangeListener();
    }

    private void init(final int preferredSizeWidth, final int preferredSizeHeight) {
        sliderDegree = new SliderWithTextField(0, 80, preferredSizeWidth, preferredSizeHeight, "winkel");
        sliderZoom = new SliderWithTextField((int) (BasicGraphArea.ZOOM_FACTOR_MINIMUM * 100), (int) (BasicGraphArea.ZOOM_FACTOR_MAXIMUM * 100), preferredSizeWidth, preferredSizeHeight, "zoom");
        sliderGap = new SliderWithTextField(0, getSliderGapMaximum(), preferredSizeWidth, preferredSizeHeight, "abstand");
        sliderPageSizeFactor = new SliderWithTextField(100, 100 * GraphDocument.MAX_PAGE_SIZE_FACTOR, preferredSizeWidth, preferredSizeHeight, "page_zoom");
        updateValues();
    }

    private void addChangeListener() {
        sliderDegree.addChangeListener(this);
        sliderZoom.addChangeListener(this);
        sliderGap.addChangeListener(this);
        sliderPageSizeFactor.addChangeListener(this);
    }

    private void removeChangeListener() {
        sliderDegree.removeChangeListener(this);
        sliderZoom.removeChangeListener(this);
        sliderGap.removeChangeListener(this);
        sliderPageSizeFactor.removeChangeListener(this);
    }

    private int getSliderGapMaximum() {
        //den maximalen Abstand in Anhängigkeit von der Ebenengröße berechnen
        GraphViewPane graphViewPane = frame.getViewPane();
        Szenario szen = graphViewPane.getSzenario();
        double pageSizeFactor = szen.getPageSizeFactor();
        int maxPageSizeFactor = Double.valueOf((GraphDocument.INITIAL_PAGE_HEIGHT + BasicGraphArea.GRAPH_BORDER.top) * pageSizeFactor).intValue();
        return maxPageSizeFactor;
    }

    public void updateValues() {
        if (frame == null) {
            return;
        }
        InputGraphArea area = frame.getInputGraphArea();
        if (area != null) {
            sliderZoom.setValue(Double.valueOf(area.getZoom() * 100d).intValue());
            sliderDegree.setValue(area.getLayerAngle());
            Szenario szen = area.getSzenario();
            double pageSizeFactor = szen.getPageSizeFactor();
            sliderPageSizeFactor.setValue(Double.valueOf(pageSizeFactor * 100d).intValue());
            sliderGap.setMaximum(getSliderGapMaximum());
            sliderGap.setValue(area.getLayerGap());
        }
    }

    public SliderWithTextField getSliderDegree() {
        return sliderDegree;
    }

    public SliderWithTextField getSliderZoom() {
        return sliderZoom;
    }

    public SliderWithTextField getSliderGap() {
        return sliderGap;
    }

    public SliderWithTextField getSliderPageSizeFactor() {
        return sliderPageSizeFactor;
    }

    public void setFrame(final GraphViewPaneFrameComponent frame) {
        //die Listener müssen entfernt werden, weil in updateValues() sonst ein stateChanged() ausgelöst wird und der aktuelle Frame ein falsches Gap bekommt
        removeChangeListener();
        this.frame = frame;
        updateValues();
        addChangeListener();
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        if (frame == null) {
            return;
        }
        if (e.getSource() == sliderDegree) {
            InputGraphArea area = frame.getInputGraphArea();
            area.setLayerAngle(sliderDegree.getValue());
        } else if (e.getSource() == sliderGap) {
            InputGraphArea area = frame.getInputGraphArea();
            area.setLayerGap(sliderGap.getValue());
        } else if (e.getSource() == sliderZoom) {
            InputGraphArea area = frame.getInputGraphArea();
            area.setZoom((double) sliderZoom.getValue() / 100);
        } else if (e.getSource() == sliderPageSizeFactor) {
            GraphDocument doc = frame.getGraphDocument();
            int pageSizeFactor = (int) (doc.getPageSizeFactor() * 100d);
            int sliderValue = sliderPageSizeFactor.getValue();
            // Preventing the destruction of the UNDO-REDO chain. The PageSizeFactor
            // of the doc can differ by a fraction (<0.01) from the value of the slider.
            if (pageSizeFactor != sliderValue) {
                doc.setPageSizeFactor(sliderValue / 100d, true, STANDARD_PID);
            }
        }
    }

    public class SliderWithTextField extends JSlider {

        private final JTextField valueTextField;

        private final JLabel label;

        public SliderWithTextField(final int min, final int max, final int preferredSizeWidth, final int preferredSizeHeight, final String labelResKey) {
            super(min, max);
            if (preferredSizeWidth != -1 && preferredSizeHeight != -1) {
                setPreferredSize(new Dimension(preferredSizeWidth, preferredSizeHeight));
            }
            valueTextField = new MinMaxNumberTextField3(min, max, 0);
            label = Strings.isNullOrEmpty(labelResKey) ? null : new JLabel(getResString(labelResKey));

            addMouseListener(new MouseAdapter() {

                GraphDocument doc = null;

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (doc != null) {
                        //fire data changed to update the undo redo buttons
                        doc.finish_transaction(STANDARD_PID, DATA_CHANGED);
                        doc = null;
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    //start transaction to prevent every single mini slider change
                    // will be logged as a single Undo-Redo command
                    doc = Static.getSelectedDoc();
                    if (doc != null) {
                        doc.start_transaction(STANDARD_PID);
                    }
                }

            });

        }

        public JTextField getTextField() {
            return valueTextField;
        }

        public JLabel getLabel() {
            return label;
        }

        @Override
        public void setValue(final int n) {
            valueTextField.setText(String.valueOf(n));
            super.setValue(n);
        }

    }

}
