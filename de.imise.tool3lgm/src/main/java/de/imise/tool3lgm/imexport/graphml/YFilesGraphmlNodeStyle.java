package de.imise.tool3lgm.imexport.graphml;

import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.Shape;
import de.imise.util.htmlxml.HTMLConverter;

public class YFilesGraphmlNodeStyle {

    //    <yjs:ShapeNodeStyle fill="#FFCCFFCC" shape="ELLIPSE"/>

    public String fill;

    public String shape;

    public YFilesGraphmlNodeStyle(final NodeContainer nc) {
        Enum<?> shapeType = getYGraphmlShape(nc);
        shape = shapeType == YGraphShape.RECTANGLE ? null : shapeType.name();
        fill = HTMLConverter.getHTMLColor(GraphmlWriter.getColor(nc), true, "#");
    }

    public static YFilesGraphmlNodeStyle createLabelStyle(final NodeContainer nc) {
        return new YFilesGraphmlNodeStyle(nc);
    }

    private static final StringBuilder sb = new StringBuilder();

    public final String getNodeStyleKey() {
        sb.setLength(0);
        if (fill != null) {
            sb.append(fill);
        }
        if (shape != null) {
            sb.append(shape);
        }
        return sb.toString();
    }

    private final Enum<?> getYGraphmlShape(final NodeContainer nc) {
        Shape shape = nc.getForm();
        if (shape == null) {
            shape = nc.getGraphDocument().getDefaultElementsLayout().getStandardForm(nc);
        }
        return getYGraphmlShape(shape);
    }

    private static enum YGraphShape { // in dieser Schreibweise braucht das yEd-Format die shapes. Diese hier gehen in yFiles zwar auch, aber dann wird die Zeichenfarbe (fill-Attribut) ignoriert
        DIAMOND,
        ELLIPSE,
        RECTANGLE,
        TRIANGLE,
        ROUND_RECTANGLE,
        HEXAGON;
    }

    private Enum<?> getYGraphmlShape(final Shape shape) {
        switch (shape) {
        case dreieck:
            return YGraphShape.TRIANGLE;
        case oval:
            return YGraphShape.ELLIPSE;
        case rundeck:
            return YGraphShape.ROUND_RECTANGLE;
        case rhombus:
            return YGraphShape.DIAMOND;
        case wabe:
            return YGraphShape.HEXAGON;
        case tonne:
            return YGraphShape.HEXAGON;
        case ordner:
            return YGraphShape.HEXAGON;
        default:
            return YGraphShape.RECTANGLE;
        }
    }

}
