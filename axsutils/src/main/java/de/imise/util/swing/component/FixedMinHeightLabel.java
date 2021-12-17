package de.imise.util.swing.component;

import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;

/**
 * {@link GridBagConstraints} scales the labels even if they should not be
 * scaled because fill = NONE and both weights are set to 0. Hence this
 * detour...
 *
 * @author AXS (17.12.2021)
 */
public class FixedMinHeightLabel extends JLabel {

    /**
     *
     */
    public FixedMinHeightLabel() {
    }

    /**
     * @param s
     */
    public FixedMinHeightLabel(String s) {
        super(s);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension maximumSize = super.getMaximumSize();
        maximumSize.height = getFixHeight();
        return maximumSize;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        preferredSize.height = getFixHeight();
        return preferredSize;
    }

    @Override
    public Dimension getSize() {
        Dimension size = super.getSize();
        size.height = getFixHeight();
        return size;
    }

    /**
     * @return
     */
    private int getFixHeight() {
        return getMinimumSize().height;
    }

}
