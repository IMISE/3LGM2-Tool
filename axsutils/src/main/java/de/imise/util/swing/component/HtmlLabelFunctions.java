package de.imise.util.swing.component;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * @author AXS (24.02.2020)
 */
public class HtmlLabelFunctions {

    /**
     * A label to render the html text and derive the sizes
     */
    private static JLabel renderDummy = new JLabel();

    /**
     * Returns the minimum width in order to render the given htmltext as html string with the given
     * font and size. The returned height of the rerun dimension is the preferred height if the label
     * would be rendered with the given width.
     *
     * @param htmlLabel the label with the html text and the designated size
     * @return
     */
    public static HtmlLabelDimension getHtmlLabelDimension(final Font font, final String htmlTtext, final int width) {
        HtmlLabelDimension htmlLabelDimension = new HtmlLabelDimension();
        renderDummy.setText(htmlTtext);
        renderDummy.setFont(font);
        renderDummy.setSize(width, 10); // the height is irrelevant
        Dimension preferredSize = renderDummy.getPreferredSize();
        View view = (View) renderDummy.getClientProperty(BasicHTML.propertyKey);
        if (view != null) {
            view.setSize(width, 10);

            float w = view.getMinimumSpan(View.X_AXIS);
            float h = view.getPreferredSpan(View.Y_AXIS);
            htmlLabelDimension.minWidth = (int) Math.ceil(w);
            htmlLabelDimension.preferredHeight = (int) Math.ceil(h);
            htmlLabelDimension.lineHeight = preferredSize.height;
        }
        return htmlLabelDimension;
    }

    /**
     * Data structure to return all relevant informations.
     *
     * @author AXS (24.02.2020)
     */
    public static class HtmlLabelDimension {

        /**
         * The minimal width
         */
        public int minWidth = -1;

        /**
         * The preferred height
         */
        public int preferredHeight = -1;

        /**
         * The height of one line
         */
        public int lineHeight = -1;

        @Override
        public String toString() {
            return getClass().getSimpleName() + ": minWidth=" + minWidth + " preferredHeight=" + preferredHeight + " lineHeight=" + lineHeight;
        }

    }

}
