package de.imise.tool3lgm.graphtools.view.graph;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.SwingConstants;

// TODO: Konzept der Klasse GraphElementLayout überarbeiten und
// toXMLString-Methode verändern und nur GraphElementLayout-Informationen
// speichern die
// nicht Standard aus Mapping entsprechen

public class GraphElementLayout implements SwingConstants, Cloneable {

    public static Color[] STANDARD_COLORS = {
            Color.black, Color.blue, Color.cyan, Color.darkGray, Color.gray, Color.green, Color.magenta, Color.orange, Color.pink, Color.red, Color.yellow
    };

    /**  */
    public static final Stroke NORMAL_STROKE = new BasicStroke(1);

    /** 1 px Stroke with 3 px dots and dashes */
    public static final Stroke NORMAL_STROKE_DOTTED = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] {
            3f
    }, 0f);

    /** 1 px Stroke with 10 px dashes */
    public static final Stroke NORMAL_STROKE_DASHED = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[] {
            10f
    }, 0f);

    /**  */
    public static final Stroke MEDUIM_STROKE = new BasicStroke(4);

    /**  */
    public static final Stroke FAT_STROKE = new BasicStroke(7);

    /** The Stroke of the border of not selected layers */
    public static final Stroke LAYER_STROKE_SELECTED = new BasicStroke(3);

    /** Stroke for the border of collapsed elements in the graph */
    public static final Stroke NOT_EXPANDED_BORDER_STROKE = new BasicStroke(4f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1f, new float[] {
            10
    }, 10f);

    /** Stroke for {@link HasPartEdges} in the graph */
    public static final Stroke HAS_PART_EDGES_STROKE = NORMAL_STROKE_DASHED;

    /**
     * Alle Standardformen. Die String-Repräsentation steht als Schlüssel auch
     * in den Ressourcen. Die Position des Enum-Eintrages wird in der
     * XML-Repräsentation des Modells gepsiechert. D.h. wer hier die Reihenfolge
     * ändert, ändert das Layout der Elemente in Modellen, die vorher erstellt
     * wurden.
     */
    public static enum SHAPE {
        rechteck,
        oval,
        dreieck,
        rundeck,
        rhombus,
        tonne,
        wabe,
        ordner,
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

    public static final int LIGHTPURPLE = 10;

    public static final int LIGHTBLUE = 11;

    public static final Color[] COLORS = new Color[12];
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
        COLORS[LIGHTPURPLE] = new Color(229, 203, 255);
        COLORS[LIGHTBLUE] = new Color(0, 204, 255);
    }

    public static final String[] COLOR_NAMES = new String[12];
    static {
        COLOR_NAMES[WHITE] = getResString("white");
        COLOR_NAMES[YELLOW] = getResString("yellow");
        COLOR_NAMES[GREEN] = getResString("green");
        COLOR_NAMES[BLUE] = getResString("blue");
        COLOR_NAMES[GRAY] = getResString("grey");
        COLOR_NAMES[RED] = getResString("red");
        COLOR_NAMES[ORANGE] = getResString("orange");
        COLOR_NAMES[BLACK] = getResString("black");
        COLOR_NAMES[LIGHTRED] = getResString("lightred");
        COLOR_NAMES[LIGHTGREEN] = getResString("lightgreen");
        COLOR_NAMES[LIGHTPURPLE] = getResString("lightpurple");
        COLOR_NAMES[LIGHTBLUE] = getResString("lightblue");
    }

    /** Transparenzwert für nicht transparent (Alpha = 255) */
    public static final int TRANSPARENCY_NONE = 255;

    /** Transparenzwert für halb transparent (Alpha = 128) */
    public static final int TRANSPARENCY_HALF = 128;

    /** Transparenzwert für voll transparent (Alpha = 0) */
    public static final int TRANSPARENCY_FULL = 0;

    public static final String[] FONT_NAMES = {
            getResString("courier"), getResString("helvetica"), getResString("luc_sans"), getResString("serif"), getResString("icon"), getResString("times")
    };

    public static final int[] FONT_SIZES = {
            10, 12, 14, 18, 24
    };

    public static final String[] FONT_STYLE_NAMES = {
            getResString("font_plain"), getResString("font_bold"), getResString("font_italic")
    };

    public static final int[] FONT_STYLES = {
            Font.PLAIN, Font.BOLD, Font.ITALIC
    };

    /**
     * @author AXS (30.01.2020)
     */
    public static enum TextAlignmentHTML {
        LEFT {
            @Override
            public int getSwingConstant() {
                return SwingConstants.LEFT;
            }

        },
        CENTER {
            @Override
            public int getSwingConstant() {
                return SwingConstants.CENTER;
            }

        },
        RIGHT {
            @Override
            public int getSwingConstant() {
                return SwingConstants.RIGHT;
            }
        },
        JUSTIFY {
            @Override
            public int getSwingConstant() {
                return SwingConstants.CENTER;
            }
        };

        /**
         * @return
         */
        public abstract int getSwingConstant();

        /**
         * @return
         */
        public final String getHTMLAlign() {
            return name();
        }

        /**
         * @param swingConstantValue
         * @return
         */
        public static TextAlignmentHTML getValueForSwingConstant(final int swingConstantValue) {
            if (swingConstantValue == LEFT.getSwingConstant()) {
                return LEFT;
            }
            if (swingConstantValue == RIGHT.getSwingConstant()) {
                return RIGHT;
            }
            return CENTER;
        }
    }

    /**
     * @author AXS (30.01.2020)
     */
    public static enum TextPositionHorizontal {
        LEFT {
            @Override
            public int getSwingConstant() {
                return SwingConstants.LEFT;
            }
        },
        CENTER {
            @Override
            public int getSwingConstant() {
                return SwingConstants.CENTER;
            }
        },
        RIGHT {
            @Override
            public int getSwingConstant() {
                return SwingConstants.RIGHT;
            }
        };

        /**
         * @return
         */
        public abstract int getSwingConstant();

        /**
         * @param swingConstantValue
         * @return
         */
        public static TextPositionHorizontal getValueForSwingConstant(final int swingConstantValue) {
            if (swingConstantValue == LEFT.getSwingConstant()) {
                return LEFT;
            }
            if (swingConstantValue == RIGHT.getSwingConstant()) {
                return RIGHT;
            }
            return CENTER;
        }
    }

    /**
     * @author AXS (30.01.2020)
     */
    public static enum TextPositionVertical {
        TOP {
            @Override
            public int getSwingConstant() {
                return SwingConstants.TOP;
            }
        },
        CENTER {
            @Override
            public int getSwingConstant() {
                return SwingConstants.CENTER;
            }
        },
        BOTTOM {
            @Override
            public int getSwingConstant() {
                return SwingConstants.BOTTOM;
            }
        };

        /**
         * @return
         */
        public abstract int getSwingConstant();

        /**
         * @param swingConstantValue
         * @return
         */
        public static TextPositionVertical getValueForSwingConstant(final int swingConstantValue) {
            if (swingConstantValue == TOP.getSwingConstant()) {
                return TOP;
            }
            if (swingConstantValue == BOTTOM.getSwingConstant()) {
                return BOTTOM;
            }
            return CENTER;
        }
    }

    public static final GraphElementLayout.SHAPE STANDARD_FORM = SHAPE.rechteck;
    public static final int STANDARD_WIDTH = 90;
    public static final int STANDARD_HEIGHT = 50;
    public static final int STANDARD_LINE_THICKNESS = 1;
    public static final int STANDARD_LINE_STYLE = 0;
    public static final TextAlignmentHTML STANDARD_TEXT_ALIGNMENT_HTML = TextAlignmentHTML.CENTER;
    public static final TextPositionVertical STANDARD_TEXT_POSITION_VERTICAL = TextPositionVertical.CENTER;
    public static final TextPositionVertical STANDARD_TEXT_POSITION_VERTICAL_WITH_ICON = TextPositionVertical.BOTTOM;
    public static final TextPositionHorizontal STANDARD_TEXT_POSITION_HORIZONTAL = TextPositionHorizontal.CENTER;
    //	public static final Color  STANDARD_TRACE_COLOR = Color.BLACK;
    public static final Color STANDARD_BORDER_COLOR = Color.BLACK;
    public static final Color STANDARD_NODE_COLOR = COLORS[RED];
    public static final String STANDARD_FONT_NAME = "SansSerif";
    public static final int STANDARD_FONT_STYLE = Font.PLAIN;
    public static final Color STANDARD_FONT_COLOR = Color.BLACK;
    public static final int STANDARD_FONT_SIZE = 12;
    public static final Font STANDARD_FONT = new Font(STANDARD_FONT_NAME, STANDARD_FONT_STYLE, STANDARD_FONT_SIZE);

    /**
     * Standardlayout für alle Node und Kanten, die kein spezielles eigenes
     * Layout haben
     */
    public static final GraphElementLayout STANDARD_ELEMENT_LAYOUT = new GraphElementLayout();
    static {
        STANDARD_ELEMENT_LAYOUT.fg_color = STANDARD_FONT_COLOR;
        STANDARD_ELEMENT_LAYOUT.bg_color = STANDARD_NODE_COLOR;
        STANDARD_ELEMENT_LAYOUT.border_color = STANDARD_BORDER_COLOR;
        STANDARD_ELEMENT_LAYOUT.font = STANDARD_FONT;
        STANDARD_ELEMENT_LAYOUT.iconID = null;

        //TODO: den Standard-Linestyle aus den Containern hier her verlegen
        //		STANDARD_ELEMENT_LAYOUT.line_style =
        STANDARD_ELEMENT_LAYOUT.form = STANDARD_FORM;
        STANDARD_ELEMENT_LAYOUT.textPositionHorizontal = STANDARD_TEXT_POSITION_HORIZONTAL;
        STANDARD_ELEMENT_LAYOUT.textPositionVertical = STANDARD_TEXT_POSITION_VERTICAL;
        STANDARD_ELEMENT_LAYOUT.textAlignmentHTML = STANDARD_TEXT_ALIGNMENT_HTML;
        STANDARD_ELEMENT_LAYOUT.width = STANDARD_WIDTH;

    }

    /** Hintergrundfarbe */
    public Color bg_color;

    /** Vordergrund bzw. Schriftfarbe */
    public Color fg_color;

    /** Farbe des Rahmens bei Node bzw. der Linie bei Assoziationen */
    public Color border_color;

    /** Dicke des Rahmnes bei Node bzw. der Linie der Assoziationen */
    public int line_thickness;

    /** Form (momentan nur bei Node genutzt) */
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

    /** ID des Icons */
    private String iconID;

    /** Stil des Rahmens bei Node oder der Linie bei Kanten */
    public int line_style;

    /**
     * Horizontale Position des Labeltextes. Gibt es kein Icon, dann bezieht
     * sich diese Position auf das gesamte Label. Mit Icon bezieht sie sich auf
     * das Icon.
     */
    public TextPositionHorizontal textPositionHorizontal;

    /**
     * Vertikale Position des Labeltextes. Gibt es kein Icon, dann bezieht sich
     * diese Position auf das gesamte Label. Mit Icon bezieht sie sich auf das
     * Icon.
     */
    public TextPositionVertical textPositionVertical;

    /**
     * Ausrichtung des HTML-Textes des Labels.
     */
    public TextAlignmentHTML textAlignmentHTML;

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
        line_thickness = STANDARD_LINE_THICKNESS;
        iconID = null;
        font = null;
        form = null;
        line_style = STANDARD_LINE_STYLE;

        //null bei den folgenden Werten gibt an, dass es nicht explizit gesetzt wurde
        textPositionVertical = STANDARD_TEXT_POSITION_VERTICAL;
        textPositionHorizontal = STANDARD_TEXT_POSITION_HORIZONTAL;
        textAlignmentHTML = STANDARD_TEXT_ALIGNMENT_HTML;
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
        tmp.iconID = iconID == null ? null : new String(iconID);
        tmp.line_style = line_style;
        tmp.textPositionVertical = textPositionVertical;
        tmp.textPositionHorizontal = textPositionHorizontal;
        tmp.textAlignmentHTML = textAlignmentHTML;
        return tmp;
    }

    /**
     * @return the iconID
     */
    public String getIconID() {
        return iconID;
    }

    /**
     * @param iconID of the icon to set
     */
    public void setIconID(final String iconID) {
        if (this.iconID == iconID) {
            return;
        }
        if (iconID == null) {
            textPositionVertical = TextPositionVertical.CENTER;
        } else {
            textPositionVertical = TextPositionVertical.BOTTOM;
        }
        this.iconID = iconID;
    }

    /**
     * @return <code>true</code> if the <code>textAlignmentHTML</code> has the
     *         default value
     */
    public boolean isDefaultTextAlignmentHTML() {
        return textAlignmentHTML == STANDARD_TEXT_ALIGNMENT_HTML;
    }

}
