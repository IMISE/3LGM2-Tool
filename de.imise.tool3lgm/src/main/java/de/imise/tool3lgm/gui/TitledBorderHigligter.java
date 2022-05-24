package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_SHOW_VIEW_COMPONENT_TITLES;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import de.imise.tool3lgm.Tool3lgmConstants;

/**
 * @author AXS (23.05.2022)
 */
public interface TitledBorderHigligter {

    public Border titledBorderBaseBorder = BorderFactory.createTitledBorder("").getBorder();

    /**
     * @param titleResKey
     */
    public default void initBorder(String titleResKey) {
        if (this instanceof JComponent) {
            JComponent c = (JComponent) this;
            Border border = c.getBorder();
            if (border instanceof InternalTitledBorder) { //already initialzed
                ((InternalTitledBorder) border).checkTitle();
            } else {
                String title = Tool3lgmConstants.getResString(titleResKey);
                border = new InternalTitledBorder(titledBorderBaseBorder, title);
                c.setBorder(border);
            }
            c.repaint();
        }
    }

    /**
     *
     */
    public default void setHighlight() {
        if (this instanceof JComponent) {
            JComponent c = (JComponent) this;
            TitledBorder titledBorder = (TitledBorder) c.getBorder();
            String borderTitle = titledBorder.getTitle();
            Border baseBorder = titledBorder.getBorder();

            Color lineColor = Color.BLACK;
            int thickness = 1;
            boolean roundedCorners = false;
            Border highlightBaseBorder = null;
            if (baseBorder instanceof EtchedBorder) {
                EtchedBorder etchedBaseBorder = (EtchedBorder) baseBorder;
                int etchType = etchedBaseBorder.getEtchType();
                Color highlightColor = lineColor;
                Color shadowColor = lineColor.brighter();
                highlightBaseBorder = new EtchedBorder(etchType, highlightColor, shadowColor);
            } else if (baseBorder instanceof LineBorder) {
                LineBorder lineBaseBorder = (LineBorder) baseBorder;
                thickness = lineBaseBorder.getThickness();
                roundedCorners = lineBaseBorder.getRoundedCorners();
            }
            if (highlightBaseBorder == null) {
                highlightBaseBorder = new LineBorder(lineColor, thickness, roundedCorners);
            }
            titledBorder = new TitledBorder(highlightBaseBorder, borderTitle);
            titledBorder.setTitleColor(lineColor);
            c.setBorder(titledBorder);
            c.repaint();
        }
    }

    /**
     *
     */
    public default void removeHighlight() {
        if (this instanceof JComponent) {
            JComponent c = (JComponent) this;
            Border border = c.getBorder();
            if (border instanceof TitledBorder) {
                TitledBorder titledBorder = (TitledBorder) border;
                String title = titledBorder.getTitle();
                titledBorder = new TitledBorder(titledBorderBaseBorder, title);
                c.setBorder(titledBorder);
                c.repaint();
            }
        }
    }

    /**
     * @author AXS (23.05.2022)
     */
    public static class TitledBorderHighlighterAdapterPanel extends JPanel implements TitledBorderHigligter {

        /**
         * Create a new buffered JPanel with the specified layout manager
         *
         * @param layout the LayoutManager to use
         */
        public TitledBorderHighlighterAdapterPanel(LayoutManager layout) {
            super(layout);
        }

    }

    /**
     * @author AXS (24.05.2022)
     */
    public static class InternalTitledBorder extends TitledBorder {

        /**
         * The title of the border is stored in this variable even if it is not
         * displayed.
         */
        String title;

        /**
         * @param border
         * @param title
         */
        public InternalTitledBorder(Border border, String title) {
            super(border, title);
            this.title = title;
            checkTitle();
        }

        /**
         * Sets the super title depending on the option if the title should be
         * shown or hidden.
         */
        public void checkTitle() {
            super.title = OPTION_SHOW_VIEW_COMPONENT_TITLES.is() ? title : "";
        }

    }

}
