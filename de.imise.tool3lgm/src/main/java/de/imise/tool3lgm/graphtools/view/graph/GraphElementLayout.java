package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Color;
import java.awt.Font;

import javax.swing.SwingConstants;

import de.imise.tool3lgm.Tool3lgmConstants;

// TODO: Konzept der Klasse GraphElementLayout überarbeiten und toXMLString-Methode verändern und nur GraphElementLayout-Informationen speichern die
// nicht Standard aus Mapping entsprechen

public class GraphElementLayout implements SwingConstants {

    /**
     * Alle Standardformen. Die String-Repräsentation steht als Schlüssel auch in den
     * Ressourcen. Die Position des Enum-Eintrages wird in der XML-Repräsentation
     * des Modells gepsiechert. D.h. wer hier die Reihenfolge ändert, ändert das
     * Layout der Elemente in Modellen, die vorher erstellt wurden.
     */
    public static enum SHAPE {
        rechteck, oval, dreieck, rundeck, rhombus, tonne, wabe, ordner,
    }

    public static final int WHITE = 0;
    public static final int YELLOW = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;
    public static final int GRAY = 4;
    public static final int RED = 5;
    public static final int ORANGE = 6;
    public static final int BLACK = 7;
    public static final int LIGHTRED = 8;
    public static final int LIGHTGREEN = 9;

    public static final Color[] COLORS = new Color[10];
    static {
        COLORS[WHITE] = Color.white;
        COLORS[YELLOW] = Color.yellow;
        COLORS[GREEN] = Color.green;
        COLORS[BLUE] = new Color(100, 100, 255);
        COLORS[GRAY] = Color.lightGray;
        COLORS[RED] = new Color(255, 100, 100);
        COLORS[ORANGE] = Color.orange;
        COLORS[BLACK] = Color.black;
        COLORS[LIGHTRED] = new Color(255, 153, 102);
        COLORS[LIGHTGREEN] = new Color(204, 255, 204);
    }

    public static final String[] COLOR_NAMES = new String[10];
    static {
        COLOR_NAMES[WHITE] = Tool3lgmConstants.getResString("white");
        COLOR_NAMES[YELLOW] = Tool3lgmConstants.getResString("yellow");
        COLOR_NAMES[GREEN] = Tool3lgmConstants.getResString("green");
        COLOR_NAMES[BLUE] = Tool3lgmConstants.getResString("blue");
        COLOR_NAMES[GRAY] = Tool3lgmConstants.getResString("grey");
        COLOR_NAMES[RED] = Tool3lgmConstants.getResString("red");
        COLOR_NAMES[ORANGE] = Tool3lgmConstants.getResString("orange");
        COLOR_NAMES[BLACK] = Tool3lgmConstants.getResString("black");
        COLOR_NAMES[LIGHTRED] = Tool3lgmConstants.getResString("lightred");
        COLOR_NAMES[LIGHTGREEN] = Tool3lgmConstants.getResString("lightgreen");
    }

    public static final int NICHT_TRANSPARENT = 255;
    public static final int HALB_TRANSPARENT = 128;
    public static final int VOLL_TRANSPARENT = 0;

    public static final String[] FONT_NAMES = {
            Tool3lgmConstants.getResString("courier"), Tool3lgmConstants.getResString("helvetica"), Tool3lgmConstants.getResString("luc_sans"), Tool3lgmConstants.getResString("serif"), Tool3lgmConstants.getResString("icon"),
            Tool3lgmConstants.getResString("times")
    };

    public static final int[] FONT_SIZES = {
            10, 12, 14, 18, 24
    };

    public static final String[] FONT_STYLE_NAMES = {
            Tool3lgmConstants.getResString("font_plain"), Tool3lgmConstants.getResString("font_bold"), Tool3lgmConstants.getResString("font_italic")
    };

    public static final int[] FONT_STYLES = {
            Font.PLAIN, Font.BOLD, Font.ITALIC
    };

    public static final GraphElementLayout.SHAPE STANDARD_FORM = SHAPE.rechteck;
    public static final int STANDARD_WIDTH = 90;
    public static final int STANDARD_HEIGHT = 50;
    //	public static final Color  STANDARD_TRACE_COLOR = Color.BLACK;
    public static final Color STANDARD_BORDER_COLOR = Color.BLACK;
    public static final Color STANDARD_NODE_COLOR = COLORS[RED];
    public static final String STANDARD_FONT_NAME = "SansSerif";
    public static final int STANDARD_FONT_STYLE = Font.PLAIN;
    public static final Color STANDARD_FONT_COLOR = Color.BLACK;
    public static final int STANDARD_FONT_SIZE = 12;
    public static final Font STANDARD_FONT = new Font(STANDARD_FONT_NAME, STANDARD_FONT_STYLE, STANDARD_FONT_SIZE);

    /**
     * Standardlayout für alle Knoten und Kanten, die kein spezielles eigenes Layout haben
     */
    public static final GraphElementLayout STANDARD_ELEMENT_LAYOUT = new GraphElementLayout();
    static {
        STANDARD_ELEMENT_LAYOUT.fg_color = STANDARD_FONT_COLOR;
        STANDARD_ELEMENT_LAYOUT.bg_color = STANDARD_NODE_COLOR;
        STANDARD_ELEMENT_LAYOUT.border_color = STANDARD_BORDER_COLOR;
        STANDARD_ELEMENT_LAYOUT.font = STANDARD_FONT;
        STANDARD_ELEMENT_LAYOUT.icon = null;

        //TODO: den Standard-Linestyle aus den Containern hier her verlegen		
        //		STANDARD_ELEMENT_LAYOUT.line_style = 
        STANDARD_ELEMENT_LAYOUT.form = STANDARD_FORM;
        STANDARD_ELEMENT_LAYOUT.halign = CENTER;
        STANDARD_ELEMENT_LAYOUT.valign = CENTER;
        STANDARD_ELEMENT_LAYOUT.width = STANDARD_WIDTH;

    }

    /** Hintergrundfarbe */
    public Color bg_color;

    /** Vordergrund bzw. Schriftfarbe */
    public Color fg_color;

    /** Farbe des Rahmens bei Knoten bzw. der Linie bei Assoziationen */
    public Color border_color;

    /** Dicke des Rahmnes bei Knoten bzw. der Linie der Assoziationen */
    public int line_thickness;

    /** Form (momentan nur bei Knoten genutzt) */
    public GraphElementLayout.SHAPE form;

    /** Schriftart */
    private Font font;

    /**
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * @param font the font to set
     */
    public void setFont(final Font font) {
        this.font = font;
    }

    /** X-Position */
    public int x;

    /** Y-Position */
    public int y;

    /** Breite */
    public int width;

    /** Höhe */
    public int height;

    //TODO:prüfen ob das der Gesamte Pfad zum Icon ist und ob das überhaupt gebraucht wird. EIgentlich müsste hier das Icon rein und nicht ein String
    /** Name des Icons */
    public String icon;

    /** Stil des Rahmens bei Knoten oder der Linie bei Kanten */
    public int line_style;

    /** vertikale Ausrichtung des Labeltextes */
    public int valign;

    /** horizontale Ausrichtung des Labeltextes */
    public int halign;

    /**
	 * 
	 */
    public GraphElementLayout() {
        x = 0;
        y = 0;
        reset();
    }

    /**
	 * 
	 */
    public void reset() {
        width = STANDARD_WIDTH;
        height = STANDARD_HEIGHT;
        bg_color = null; // default: wie im Mapping
        fg_color = null; // default: Color.black
        border_color = null; // default: Color.black
        line_thickness = 1;
        icon = null;
        font = null;
        form = null;
        line_style = 0;
        valign = CENTER;
        halign = CENTER;
    }

    @Override
    public Object clone() {
        GraphElementLayout tmp = new GraphElementLayout();
        if (bg_color != null) {
            tmp.bg_color = new Color(bg_color.getRed(), bg_color.getGreen(), bg_color.getBlue(), bg_color.getAlpha());
        }
        if (fg_color != null) {
            tmp.fg_color = new Color(fg_color.getRed(), fg_color.getGreen(), fg_color.getBlue(), fg_color.getAlpha());
        }
        if (border_color != null) {
            tmp.border_color = new Color(border_color.getRed(), border_color.getGreen(), border_color.getBlue(), border_color.getAlpha());
        }
        tmp.line_thickness = line_thickness;
        tmp.form = form;
        tmp.setFont(font != null ? font.deriveFont(font.getStyle()) : null);
        tmp.x = x;
        tmp.y = y;
        tmp.width = width;
        tmp.height = height;
        tmp.icon = icon == null ? null : new String(icon);
        tmp.line_style = line_style;
        tmp.valign = valign;
        tmp.halign = halign;
        return tmp;
    }

    /**
     * @param color
     * @param col_name
     * @return
     */
    private static String getColorXMLString(final Color color, final String col_name) {
        if (color == null) {
            return "";
        }
        return "<color" + (col_name.length() > 0 ? " name=\"" + col_name + "\"" : "") + ">" // Name
                + "<red>" + color.getRed() + "</red>" // Red
                + "<green>" + color.getGreen() + "</green>" // Green
                + "<blue>" + color.getBlue() + "</blue>" // Blue
                + (color.getAlpha() != 255 ? "<alpha>" + color.getAlpha() + "</alpha>" : "") // Alpha
                + "</color>";
    }

    /**
     * @param forCopy
     * @param expanded
     * @return
     */
    public final String getXMLString(final boolean forCopy, final boolean expanded) {
        return getXMLString(null, forCopy, expanded);
    }

    private final String getXMLString(final Class<?> elementClass, final boolean forCopy, final boolean expanded) {
        return "<" + (expanded ? "" : "ne") + "layout" + (elementClass != null ? " class=\"" + elementClass.getSimpleName() + "\"" : "") + ">" + getColorXMLString(bg_color, "bg_color") + getColorXMLString(fg_color, "fg_color")
                + getColorXMLString(border_color, "border_color") + (line_thickness != 1 ? "<line_thickness>" + line_thickness + "</line_thickness>" : "") + (line_style != 0 ? "<line_style>" + line_style + "</line_style>" : "")
                + (form != null ? "<form>" + form.ordinal() + "</form>" : "") + (font != null ? "<font_family>" + font.getName() + "</font_family>" : "") + (font != null ? "<font_style>" + font.getStyle() + "</font_style>" : "")
                + (font != null ? "<font_size>" + font.getSize() + "</font_size>" : "") + "<x>" + (forCopy ? x + 10 : x) + "</x>" + "<y>" + (forCopy ? y + 10 : y) + "</y>" + (width != -1 ? "<width>" + width + "</width>" : "")
                + (height != -1 ? "<height>" + height + "</height>" : "") + (icon != null ? "<icon>" + icon + "</icon>" : "") + "<valign>" + valign + "</valign>" + "<halign>" + halign + "</halign>" + "</" + (expanded ? "" : "ne") + "layout>";
    }

    /**
     * @author Thomas Rudert
     * @return String der vollstaendige XML-Tag zu diesem Objekt
     */
    public String toXMLString(final boolean expanded) {
        return toXMLString(null, expanded);
    }

    /**
     * @author Thomas Rudert
     * @param type, Standardlayout zu diesem Objekttyp
     * @return String der vollstaendige XML-Tag zu diesem Objekt
     */
    public String toXMLString(final Class<?> elementClass, final boolean expanded) {
        StringBuilder xmlString = new StringBuilder("<" + (expanded ? "" : "ne") + "layout");
        //ElementClass ist nur für Layer null
        if (elementClass != null) {
            xmlString.append(" class=\"" + elementClass.getSimpleName() + "\"");
        }

        xmlString.append(">" + getColorXMLString(bg_color, "bg_color") + getColorXMLString(fg_color, "fg_color") + getColorXMLString(border_color, "border_color"));

        if (line_thickness != 1) {
            xmlString.append("<line_thickness>" + line_thickness + "</line_thickness>");
        }
        if (line_style != 0) {
            xmlString.append("<line_style>" + line_style + "</line_style>");
        }
        if (form != null) {
            xmlString.append("<form>" + form.ordinal() + "</form>");
        }
        if (font != null) {
            xmlString.append("<font_family>" + font.getName() + "</font_family>");
            xmlString.append("<font_style>" + font.getStyle() + "</font_style>");
            xmlString.append("<font_size>" + font.getSize() + "</font_size>");
        }
        xmlString.append("<x>" + x + "</x>");
        xmlString.append("<y>" + y + "</y>");
        if (width != -1) {
            xmlString.append("<width>" + width + "</width>");
        }
        if (height != -1) {
            xmlString.append("<height>" + height + "</height>");
        }
        if (icon != null) {
            xmlString.append("<icon>" + icon + "</icon>");
        }
        xmlString.append("<valign>" + valign + "</valign>");
        xmlString.append("<halign>" + halign + "</halign>");

        xmlString.append("</" + (expanded ? "" : "ne") + "layout>");

        return xmlString.toString();
    }

}
