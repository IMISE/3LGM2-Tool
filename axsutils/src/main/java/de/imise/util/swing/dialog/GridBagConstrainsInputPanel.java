package de.imise.util.swing.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.imise.util.swing.component.AlphabeticalComboBox;
import de.imise.util.swing.component.MinMaxNumberTextField2;

/**
 * A panel, which can also open in a DIalog, where you can adjust
 * GridBagConstraints before adding them. This panel is used during the
 * development process to adjust the correct values in dialogs with
 * GridBagLayout. When you have the right values, you can disable it again.
 *
 * @author AXS (08.02.2021)
 */
public class GridBagConstrainsInputPanel extends JPanel {

    /**
     * @author AXS (08.02.2021)
     */
    private enum Anchor {
        /**
         * Put the component in the center of its display area.
         */
        CENTER,

        /**
         * Put the component at the top of its display area, centered
         * horizontally.
         */
        NORTH,

        /**
         * Put the component at the top-right corner of its display area.
         */
        NORTHEAST,

        /**
         * Put the component on the right side of its display area, centered
         * vertically.
         */
        EAST,

        /**
         * Put the component at the bottom-right corner of its display area.
         */
        SOUTHEAST,

        /**
         * Put the component at the bottom of its display area, centered
         * horizontally.
         */
        SOUTH,

        /**
         * Put the component at the bottom-left corner of its display area.
         */
        SOUTHWEST,

        /**
         * Put the component on the left side of its display area, centered
         * vertically.
         */
        WEST,

        /**
         * Put the component at the top-left corner of its display area.
         */
        NORTHWEST,

        /**
         * Place the component centered along the edge of its display area
         * associated with the start of a page for the current
         * {@code ComponentOrientation}. Equal to NORTH for horizontal
         * orientations.
         */
        PAGE_START,

        /**
         * Place the component centered along the edge of its display area
         * associated with the end of a page for the current
         * {@code ComponentOrientation}. Equal to SOUTH for horizontal
         * orientations.
         */
        PAGE_END,

        /**
         * Place the component centered along the edge of its display area where
         * lines of text would normally begin for the current
         * {@code ComponentOrientation}. Equal to WEST for horizontal,
         * left-to-right orientations and EAST for horizontal, right-to-left
         * orientations.
         */
        LINE_START,

        /**
         * Place the component centered along the edge of its display area where
         * lines of text would normally end for the current
         * {@code ComponentOrientation}. Equal to EAST for horizontal,
         * left-to-right orientations and WEST for horizontal, right-to-left
         * orientations.
         */
        LINE_END,

        /**
         * Place the component in the corner of its display area where the first
         * line of text on a page would normally begin for the current
         * {@code ComponentOrientation}. Equal to NORTHWEST for horizontal,
         * left-to-right orientations and NORTHEAST for horizontal,
         * right-to-left orientations.
         */
        FIRST_LINE_START,

        /**
         * Place the component in the corner of its display area where the first
         * line of text on a page would normally end for the current
         * {@code ComponentOrientation}. Equal to NORTHEAST for horizontal,
         * left-to-right orientations and NORTHWEST for horizontal,
         * right-to-left orientations.
         */
        FIRST_LINE_END,

        /**
         * Place the component in the corner of its display area where the last
         * line of text on a page would normally start for the current
         * {@code ComponentOrientation}. Equal to SOUTHWEST for horizontal,
         * left-to-right orientations and SOUTHEAST for horizontal,
         * right-to-left orientations.
         */
        LAST_LINE_START,

        /**
         * Place the component in the corner of its display area where the last
         * line of text on a page would normally end for the current
         * {@code ComponentOrientation}. Equal to SOUTHEAST for horizontal,
         * left-to-right orientations and SOUTHWEST for horizontal,
         * right-to-left orientations.
         */
        LAST_LINE_END;

        /**
         * @return
         */
        public int swingConstantsValue() {
            return ordinal() + 10; //see SwingConstants; there you can find the values from 10 to 26
        }

        /**
         * @return
         */
        public static Anchor getBySwingConstantsValue(final int swingConstantsValue) {
            for (Anchor anchor : Anchor.values()) {
                if (anchor.swingConstantsValue() == swingConstantsValue) {
                    return anchor;
                }
            }
            return null;
        }

    }

    /**
     * @author AXS (08.02.2021)
     */
    public enum Fill {
        /**
         * Do not resize the component.
         */
        NONE,

        /**
         * Resize the component both horizontally and vertically.
         */
        BOTH,

        /**
         * Resize the component horizontally but not vertically.
         */
        HORIZONTAL,

        /**
         * Resize the component vertically but not horizontally.
         */
        VERTICAL;

        /**
         * @return
         */
        public int swingConstantsValue() {
            return ordinal();
        }

        /**
         * @return
         */
        public static Fill getBySwingConstantsValue(final int swingConstantsValue) {
            for (Fill fill : Fill.values()) {
                if (fill.swingConstantsValue() == swingConstantsValue) {
                    return fill;
                }
            }
            return null;
        }
    }

    /**
     *
     */
    public static GridBagConstraints gbcCache;

    /**
     *
     */
    private final GridBagConstraints returnGbc;

    private final MinMaxNumberTextField2 gridx;
    private final MinMaxNumberTextField2 gridy;
    private final MinMaxNumberTextField2 gridwidth;
    private final MinMaxNumberTextField2 gridheight;
    private final MinMaxNumberTextField2 weightx;
    private final MinMaxNumberTextField2 weighty;
    private final AlphabeticalComboBox<Enum<?>> anchor;
    private final AlphabeticalComboBox<Enum<?>> fill;
    private final MinMaxNumberTextField2 insetsTop;
    private final MinMaxNumberTextField2 insetsLeft;
    private final MinMaxNumberTextField2 insetsBottom;
    private final MinMaxNumberTextField2 insetsRight;
    private final MinMaxNumberTextField2 ipadx;
    private final MinMaxNumberTextField2 ipady;

    /**
     * @param gbc
     */
    public GridBagConstrainsInputPanel(final GridBagConstraints gbc) {
        if (gbcCache == null) {
            gbcCache = new GridBagConstraints();
            copy(gbc, gbcCache);
        }
        returnGbc = gbc;

        setLayout(new GridLayout(14, 2));
        gridx = add("gridx", gbcCache.gridx);
        gridy = add("gridy", gbcCache.gridy);
        gridwidth = add("gridwidth", gbcCache.gridwidth);
        gridheight = add("gridheight", gbcCache.gridheight);
        weightx = add("weightx", gbcCache.weightx);
        weighty = add("weighty", gbcCache.weighty);
        anchor = add("anchor", Anchor.values(), Anchor.getBySwingConstantsValue(gbcCache.anchor));
        fill = add("fill", Fill.values(), Fill.getBySwingConstantsValue(gbcCache.fill));
        insetsTop = add("insets.top", gbcCache.insets.top);
        insetsRight = add("insets.right", gbcCache.insets.right);
        insetsBottom = add("insets.bottom", gbcCache.insets.bottom);
        insetsLeft = add("insets.left", gbcCache.insets.left);
        ipadx = add("ipadx", gbcCache.ipadx);
        ipady = add("ipady", gbcCache.ipady);
    }

    /**
     *
     */
    private void setConstraintsWithInputValues() {
        gbcCache.gridx = gridx.intValue();
        gbcCache.gridy = gridy.intValue();
        gbcCache.gridwidth = gridwidth.intValue();
        gbcCache.gridheight = gridheight.intValue();
        gbcCache.weightx = weightx.doubleValue();
        gbcCache.weighty = weighty.doubleValue();
        gbcCache.anchor = ((Anchor) anchor.getSelectedObject()).swingConstantsValue();
        gbcCache.fill = ((Fill) fill.getSelectedObject()).swingConstantsValue();
        gbcCache.insets.top = insetsTop.intValue();
        gbcCache.insets.right = insetsRight.intValue();
        gbcCache.insets.bottom = insetsBottom.intValue();
        gbcCache.insets.left = insetsLeft.intValue();
        gbcCache.ipadx = ipadx.intValue();
        gbcCache.ipady = ipady.intValue();
        copy(gbcCache, returnGbc);
    }

    /**
     * @param s
     */
    private void add(final String s) {
        JLabel label = new JLabel(s);
        add(label);
    }

    /**
     * @param i
     */
    private MinMaxNumberTextField2 add(final int i) {
        MinMaxNumberTextField2 textField = new MinMaxNumberTextField2(0d, 100d, 0);
        textField.setValue(i);
        add(textField);
        return textField;
    }

    /**
     * @param s
     * @param i
     */
    private MinMaxNumberTextField2 add(final String s, final int i) {
        add(s);
        return add(i);
    }

    /**
     * @param d
     */
    private MinMaxNumberTextField2 add(final double d) {
        MinMaxNumberTextField2 textField = new MinMaxNumberTextField2(0d, 1d, 3);
        textField.setValue(d);
        add(textField);
        return textField;
    }

    /**
     * @param s
     * @param d
     */
    private MinMaxNumberTextField2 add(final String s, final double d) {
        add(s);
        return add(d);
    }

    /**
     * @param enumValues
     * @param defaultValue
     * @return
     */
    private AlphabeticalComboBox<Enum<?>> add(final Enum<?>[] enumValues, final Enum<?> defaultValue) {
        AlphabeticalComboBox<Enum<?>> enumBox = new AlphabeticalComboBox<>();
        for (Enum<?> e : enumValues) {
            enumBox.addObject(e);
        }
        enumBox.setSelectedObject(defaultValue);
        add(enumBox);
        return enumBox;
    }

    /**
     * @param s
     * @param enumValues
     * @param defaultValue
     * @return
     */
    private AlphabeticalComboBox<Enum<?>> add(final String s, final Enum<?>[] enumValues, final Enum<?> defaultValue) {
        add(s);
        return add(enumValues, defaultValue);
    }

    /**
     * @param source
     * @param target
     */
    public static void copy(final GridBagConstraints source, final GridBagConstraints target) {
        target.gridx = source.gridx;
        target.gridy = source.gridy;
        target.gridwidth = source.gridwidth;
        target.gridheight = source.gridheight;
        target.weightx = source.weightx;
        target.weighty = source.weighty;
        target.anchor = source.anchor;
        target.fill = source.fill;
        target.insets = source.insets;
        target.ipadx = source.ipadx;
        target.ipady = source.ipady;
    }

    /**
     * @param gbc
     */
    public static void setConstraintsWithDialog(final Component owner, final GridBagConstraints gbc) {
        GridBagConstrainsInputPanel panel = new GridBagConstrainsInputPanel(gbc);

        JOptionPane optionPane = new JOptionPane();
        Object msg[] = {
                panel
        };
        optionPane.setMessage(msg);
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

        JDialog dialog = optionPane.createDialog(owner, "GripdBagConstraints");
        dialog.setVisible(true);
        Object value = optionPane.getValue();
        // Schließen übers Kreuz oder irgendwas unvorhergesehenes
        if (value == null || !(value instanceof Integer)) {
            return;
        }
        // Schließen über einen der Knöpfe Knopf ermitteln
        int i = ((Integer) value).intValue();
        // Schließen oder OK
        if (i == JOptionPane.CLOSED_OPTION || i == JOptionPane.OK_OPTION) {
            panel.setConstraintsWithInputValues();
            // Abbrechen gedrückt
        }

    }

}
