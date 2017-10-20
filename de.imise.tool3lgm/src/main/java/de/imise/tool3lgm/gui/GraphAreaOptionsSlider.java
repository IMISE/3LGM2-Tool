package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.base.Strings;

import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.util.swing.component.MinMaxNumberTextField3;

public class GraphAreaOptionsSlider implements ChangeListener {

    private SliderWithTextField sliderDegree, sliderZoom, sliderGap, sliderPageSizeFactor;

    private InternalGraphFrame frame;

    public GraphAreaOptionsSlider(final InternalGraphFrame frame) {
        this(frame, -1, -1);
    }

    public GraphAreaOptionsSlider(final InternalGraphFrame frame, final int preferredSizeWidth, final int preferredSizeHeight) {
        this.frame = frame;
        init(preferredSizeWidth, preferredSizeHeight);
    }

    private void init(final int preferredSizeWidth, final int preferredSizeHeight) {
        sliderDegree = new SliderWithTextField(0, 80, preferredSizeWidth, preferredSizeHeight, "winkel", this);
        sliderZoom = new SliderWithTextField(10, 200, preferredSizeWidth, preferredSizeHeight, "zoom", this);
        sliderGap = new SliderWithTextField(0, getSliderGapMaximum(), preferredSizeWidth, preferredSizeHeight, "abstand", this);
        sliderPageSizeFactor = new SliderWithTextField(100, 1000, preferredSizeWidth, preferredSizeHeight, "page_zoom", this);
        updateValues();
    }

    private int getSliderGapMaximum() {
        //den maximalen Abstand in Anhängigkeit von der Ebenengröße berechnen
        double pageSizeFactor = frame.getSzenario().getPageSizeFactor();
        int maxPageSizeFactor = new Double(800 * pageSizeFactor).intValue();
        return maxPageSizeFactor;
    }

    public void updateValues() {
        if (frame == null) {
            return;
        }
        InputGraphArea area = frame.getInputGraphArea();
        if (area != null) {
            sliderZoom.setValue(new Double(area.getZoom() * 100d).intValue());
            sliderDegree.setValue(area.getLayerAngle());
            sliderGap.setValue(area.getLayerGap());
            Szenario szen = (Szenario) frame.getSzenario();
            double pageSizeFactor = szen.getPageSizeFactor();
            sliderPageSizeFactor.setValue(new Double(pageSizeFactor * 100d).intValue());
        }
        sliderGap.setMaximum(getSliderGapMaximum());
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

    public void setFrame(final InternalGraphFrame frame) {
        this.frame = frame;
        updateValues();
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
            frame.getGraphDocument().setPageSizeFactor(sliderPageSizeFactor.getValue() / 100d);
        }
    }

    public class SliderWithTextField extends JSlider {

        private final JTextField valueTextField;

        private final JLabel label;

        public SliderWithTextField(final int min, final int max, final int preferredSizeWidth, final int preferredSizeHeight, final String labelResKey, final ChangeListener changeListener) {
            super(min, max);
            if (preferredSizeWidth != -1 && preferredSizeHeight != -1) {
                setPreferredSize(new Dimension(preferredSizeWidth, preferredSizeHeight));
            }
            valueTextField = new MinMaxNumberTextField3(min, max, 0);
            label = Strings.isNullOrEmpty(labelResKey) ? null : new JLabel(getResString(labelResKey));
            addChangeListener(changeListener);
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
