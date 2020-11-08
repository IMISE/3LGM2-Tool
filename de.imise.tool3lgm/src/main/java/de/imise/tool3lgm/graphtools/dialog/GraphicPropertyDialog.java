package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.userproperties.UserProperties.IntProperty.PROPERTY_INT_RENDER_SETTINGS;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * Dialog for setting the properties for drawing graphics
 *
 * @author Thomas Rudert (30.10.2003)
 */
public class GraphicPropertyDialog extends JDialog {

    /**
     * Bitpattern for Rendering-Hints (standard value: all bits are set to
     * zero<br>
     * bit0: ANTIALIASING<br>
     * bit1: ALPHA_INTERPOLATION<br>
     * bit2: COLOR_RENDERING<br>
     * bit3: RENDERING<br>
     * bit4: DITHERING<br>
     * bit5: FRACTIONALMETRICS<br>
     * bit6: INTERPOLATION<br>
     * bit7: TEXT_ANTIALIASING<br>
     */

    private final JCheckBox[] checkBoxArray = new JCheckBox[8];
    private final String[] checkBoxText = {
            "Antialiasing", "Alpha-Interpolation", "Color-Rendering", "Rendering", "Dithering", "Fractionalmetrics", "Interpolation", "Text-Antialiasing"
    };

    public GraphicPropertyDialog(final Frame owner) {
        super(owner, getResString("graphicProperty"), true);

        setLocationRelativeTo(owner);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

        int renderingHints = PROPERTY_INT_RENDER_SETTINGS.get();

        for (int i = 0; i < checkBoxArray.length; i++) {
            checkBoxArray[i] = new JCheckBox(checkBoxText[i]);
            checkBoxArray[i].setSelected((renderingHints >> i & 1) == 1);
            checkBoxPanel.add(checkBoxArray[i]);
        }
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(checkBoxPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton(getResString("ok"));
        okButton.addActionListener(arg0 -> {
            setProperties();
            dispose();
        });

        JButton applyButton = new JButton(getResString("apply"));
        applyButton.addActionListener(arg0 -> setProperties());

        JButton cancelButton = new JButton(getResString("cancel"));
        cancelButton.addActionListener(arg0 -> dispose());
        buttonPanel.add(okButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    private void setProperties() {
        int bitPattern = 0;
        for (int i = 0; i < checkBoxArray.length; i++) {
            bitPattern |= checkBoxArray[i].isSelected() ? 1 << i : 0;
        }

        PROPERTY_INT_RENDER_SETTINGS.set(bitPattern);
        getParent().repaint();
    }
}
