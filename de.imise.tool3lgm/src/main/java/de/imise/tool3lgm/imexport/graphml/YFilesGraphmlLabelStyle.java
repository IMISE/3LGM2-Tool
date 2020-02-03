package de.imise.tool3lgm.imexport.graphml;

import java.awt.Font;

import javax.swing.SwingConstants;

import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.HorizontalAlignment;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.VerticalAlignment;

public class YFilesGraphmlLabelStyle {

    public String labelLayout;

    public String mainLabelStyle;

    public String valign;

    public String halign;

    public String wrapping;

    public String textFill;

    public String textSize;

    public String fontSize;

    public String fontStyle;

    public String fontWeight;

    public YFilesGraphmlLabelStyle(final LayerContainer lc) {
        labelLayout = "{x:Static y:InteriorLabelModel.North}";
        mainLabelStyle = null;
        valign = null;
        halign = null;
        wrapping = null;
        textSize = "20";
        fontSize = textSize;
        fontStyle = null;
        fontWeight = null;
    }

    public YFilesGraphmlLabelStyle(final NodeContainer nc) {
        labelLayout = nc.getIconString() == null ? "{x:Static y:InteriorStretchLabelModel.Center}" : "{x:Static y:ExteriorLabelModel.South}";
        if (!nc.hideText()) {
            mainLabelStyle = null;
            VerticalAlignment valignEnum = nc.getValign();
            int valignSwingConstant = valignEnum.getSwingConstant();
            valign = getSwingConstantsAsGraphMLString(valignSwingConstant);
            HorizontalAlignment halignEnum = nc.getHalign();
            int halignSwingConstant = halignEnum.getSwingConstant();
            halign = getSwingConstantsAsGraphMLString(halignSwingConstant);
            wrapping = "WORD";
            textSize = null;
            Font font = nc.getFont();
            fontSize = String.valueOf(font.getSize());
            fontStyle = font.isItalic() ? "ITALIC" : null;
            fontWeight = font.isBold() ? "BOLD" : null;
        } else {
            mainLabelStyle = "{x:Static y:VoidLabelStyle.Instance}";
        }
    }

    public static YFilesGraphmlLabelStyle createLabelStyle(final ElementContainer ec) {
        if (ec instanceof LayerContainer) {
            return new YFilesGraphmlLabelStyle((LayerContainer) ec);
        }
        return new YFilesGraphmlLabelStyle((NodeContainer) ec);
    }

    private static String getSwingConstantsAsGraphMLString(final int position) {
        if (position == SwingConstants.TOP) {
            return "TOP";
        }
        if (position == SwingConstants.RIGHT) {
            return "RIGHT";
        }
        if (position == SwingConstants.BOTTOM) {
            return "BOTTOM";
        }
        if (position == SwingConstants.LEFT) {
            return "LEFT";
        }
        return "CENTER";
    }

    private static final StringBuilder sb = new StringBuilder();

    public final String getLabelStyleKey() {
        sb.setLength(0);
        if (valign != null) {
            sb.append(valign);
        }
        if (halign != null) {
            sb.append(halign);
        }
        if (wrapping != null) {
            sb.append(wrapping);
        }
        if (textFill != null) {
            sb.append(textFill);
        }
        if (textSize != null) {
            sb.append(textSize);
        }
        if (fontSize != null) {
            sb.append(fontSize);
        }
        if (fontStyle != null) {
            sb.append(fontStyle);
        }
        if (fontWeight != null) {
            sb.append(fontWeight);
        }
        return sb.toString();
    }

}
