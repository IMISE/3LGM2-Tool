package de.imise.tool3lgm.imexport.graphml;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;
import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;
import de.imise.util.htmlxml.HTMLConverter;

public class YEdGraphmlWriter extends GraphmlWriter {

    public YEdGraphmlWriter(final File file, final Szenario szenario) throws XMLStreamException, IOException {
        super(file, szenario);
    }

    @Override
    protected String getCreatedByComment() {
        return "Created by Tool3lgm " + Tool3lgmConstants.TOOL_VERSION;
    }

    @Override
    protected void writeStartElementGraphml() throws XMLStreamException {
        writeStartElement("graphml");
        writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writeAttribute("xmlns:java", "http://www.yworks.com/xml/yfiles-common/1.0/java");
        writeAttribute("xmlns:sys", "http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0");
        writeAttribute("xmlns:x", "http://www.yworks.com/xml/yfiles-common/markup/2.0");
        writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writeAttribute("xmlns:y", "http://www.yworks.com/xml/graphml");
        writeAttribute("xmlns:yed", "http://www.yworks.com/xml/yed/3");
        writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd");
    }

    @Override
    protected void writeKeys() throws XMLStreamException {
        for (TypeKeys typeKey : TypeKeys.values()) {
            String[] nameParts = typeKey.nameParts();
            if (nameParts.length == 2) { // 2 Teile -> yEd Attribute
                writeKeyYFilesType(nameParts[0], typeKey.getKeyID(), nameParts[1]);
            } else { // 3 Teile -> general Attribute
                writeKeyGeneralType(nameParts[0], typeKey.getKeyID(), nameParts[1], nameParts[2]);
            }
        }
    }

    public enum TypeKeys {
        port_portgraphics,
        port_portgeometry,
        port_portuserdata,
        node_url_string,
        node_description_string,
        node_nodegraphics,
        graphml_resources,
        edge_url_string,
        edge_description_string,
        edge_edgegraphics;

        public String[] nameParts() {
            StringTokenizer st = new StringTokenizer(name(), "_");
            String[] nameParts = new String[st.countTokens()];
            for (int i = 0; i < nameParts.length; i++) {
                nameParts[i] = st.nextToken();
            }
            return nameParts;
        }

        public String getKeyID() {
            return "d" + ordinal();
        }
    }

    @Override
    protected void writeNodeContent(final NodeContainer nc) throws XMLStreamException {
        writeNodeGraphics(nc);
    }

    protected void writeNodeGraphics(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d6">
        //        <y:ShapeNode>
        //          <y:Geometry height="30.0" width="107.0" x="116.5" y="-345.0"/>
        //          <y:Fill color="#CCCCFF" transparent="false"/>
        //          <y:BorderStyle color="#000000" raised="false" type="line" width="1.0"/>
        //          <y:NodeLabel alignment="center" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="18.1328125" horizontalTextPosition="center" iconTextGap="4" modelName="custom" textColor="#000000" verticalTextPosition="bottom" visible="true" width="72.408203125" x="17.2958984375" y="5.93359375">Objekttyp 1<y:LabelModel>
        //              <y:SmartNodeLabelModel distance="4.0"/>
        //            </y:LabelModel>
        //            <y:ModelParameter>
        //              <y:SmartNodeLabelModelParameter labelRatioX="0.0" labelRatioY="0.0" nodeRatioX="0.0" nodeRatioY="0.0" offsetX="0.0" offsetY="0.0" upX="0.0" upY="-1.0"/>
        //            </y:ModelParameter>
        //          </y:NodeLabel>
        //          <y:Shape type="ellipse"/>
        //        </y:ShapeNode>
        //      </data>
        writeStartElementDataKey(TypeKeys.node_nodegraphics.getKeyID()); // start data
        writeStartElement("y:ShapeNode"); // start y:ShapeNode
        writeGeometry(nc);
        writeEmptyElement("y:Fill", "color", getColorString(NodeRenderer.getColor(nc)), "transparent", "false");
        writeEmptyElement("y:BorderStyle", "color", "#000000", "raised", "false", "type", "line", "width", "1.0");
        writeNodeLabel(nc);
        writeEmptyElement("y:Shape", "type", getYGraphmlShapeName(nc));
        writeEndElement(); // end y:ShapeNode
        writeEndElement(); // end data
    }

    private final StringBuilder colorBuilder = new StringBuilder("#");

    private void writeGeometry(final NodeContainer nc) throws XMLStreamException {
        double width = nc.getWidth();
        double height = nc.getHeight();
        double x = nc.getX() - width / 2;
        double y = nc.getY() - height / 2;
        writeEmptyElement("y:Geometry", "height", height, "width", width, "x", x, "y", y);
    }

    // oben -> unten, links -> rechts -------> modelPosition, alignment
    // center, center -------> c, center
    // center, left   -------> c, left
    // center, right  -------> c, right
    // top, center -------> t, center
    // top, left -------> t, left
    // top, rigt -------> t, right
    // bottom, center -------> b, center
    // bottom, left -------> b, left
    // bottom, rigt -------> b, right

    private String getAlignment(final NodeContainer nc) {
        int halign = nc.getHalign();
        if (halign == SwingConstants.LEFT) {
            return "left";
        }
        if (halign == SwingConstants.RIGHT) {
            return "right";
        }
        return "center";
    }

    private String getModelPosition(final NodeContainer nc) {
        int halign = nc.getValign();
        if (halign == SwingConstants.TOP) {
            return "t";
        }
        if (halign == SwingConstants.BOTTOM) {
            return "b";
        }
        return "c";
    }

    public int getFontSize(final NodeContainer nc) {
        int fontSize = nc.getFontSize();
        if (fontSize < 0) {
            fontSize = standardLayout.getStandardFont(nc).getSize();
        }
        return fontSize;
    }

    public String getFontStyle(final NodeContainer nc) {
        int fontStyle = nc.getFont().getStyle();
        if (fontStyle == Font.BOLD) {
            return "bold";
        } else if (fontStyle == Font.ITALIC) {
            return "italic";
        } else if (fontStyle == Font.BOLD + Font.ITALIC) {
            return "bolditalic";
        }
        return "plain";
    }

    private final void writeNodeLabel(final NodeContainer nc) throws XMLStreamException {
        writeStartElement("y:NodeLabel"); // start y:NodeLabel
        writeAttribute("alignment", getAlignment(nc)); //################################
        writeAttribute("autoSizePolicy", "node_width");
        writeAttribute("configuration", "CroppingLabel");
        writeAttribute("fontFamily", "Dialog");
        writeAttribute("fontSize", nc.getFont().getSize());
        writeAttribute("fontStyle", getFontStyle(nc));
        writeAttribute("hasBackgroundColor", "false");
        writeAttribute("hasLineColor", "false");
        writeAttribute("height", "0.0");
        writeAttribute("horizontalTextPosition", "center");
        writeAttribute("iconTextGap", "4");
        writeAttribute("modelName", "internal");
        writeAttribute("modelPosition", getModelPosition(nc)); //################################ c, t, b, l, r, tl, tr, bl, br
        writeAttribute("textColor", "#000000");
        writeAttribute("verticalTextPosition", "bottom");
        writeAttribute("visible", "true");
        writeAttribute("width", "0.0");
        writeAttribute("x", "0.0");
        writeAttribute("y", "0.0");
        writeCharacters(nc.getElement().getClearName());
        writeEndElement(); // end y:NodeLabel
    }

    @Override
    protected void writeEdgeContent(final EdgeContainer ec) throws XMLStreamException {
        //    <data key="d10">
        //    <y:PolyLineEdge>
        //      <y:Path sx="0.0" sy="0.0" tx="0.0" ty="0.0">
        //        <y:Point x="30.0" y="-510.0"/>
        //      </y:Path>
        //      <y:LineStyle color="#000000" type="line" width="1.0"/>
        //      <y:Arrows source="none" target="delta"/>
        //      <y:EdgeLabel alignment="center" configuration="AutoFlippingLabel" distance="2.0" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" hasText="false" height="4.0" horizontalTextPosition="center" iconTextGap="4" modelName="custom" preferredPlacement="anywhere" ratio="0.5" textColor="#000000" verticalTextPosition="bottom" visible="true" width="4.0" x="88.56015555517583" y="-141.03710755185193">
        //        <y:LabelModel>
        //          <y:SmartEdgeLabelModel autoRotationEnabled="false" defaultAngle="0.0" defaultDistance="10.0"/>
        //        </y:LabelModel>
        //        <y:ModelParameter>
        //          <y:SmartEdgeLabelModelParameter angle="0.0" distance="28.47797277442824" distanceToCenter="false" position="right" ratio="0.048676984301588115" segment="-1"/>
        //        </y:ModelParameter>
        //        <y:PreferredPlacementDescriptor angle="0.0" angleOffsetOnRightSide="0" angleReference="absolute" angleRotationOnRightSide="co" distance="-1.0" frozen="true" placement="anywhere" side="anywhere" sideReference="relative_to_edge_flow"/>
        //      </y:EdgeLabel>
        //      <y:BendStyle smoothed="false"/>
        //    </y:PolyLineEdge>
        //  </data>
        writeEmptyElementDataKey(TypeKeys.edge_url_string.getKeyID());
        writeEmptyElementDataKey(TypeKeys.edge_description_string.getKeyID());
        writeStartElementDataKey(TypeKeys.edge_edgegraphics.getKeyID()); // start data
        writeStartElement("y:PolyLineEdge"); // start y:PolyLineEdge
        writeEdgePath(ec);
        writeEmptyElement("y:LineStyle", "color", getColorString(getEdgeColor(ec)), "type", getEdgeType(ec), "width", "1.0");
        writeEdgeArrows(ec);
        writeEmptyElement("y:BendStyle", "smoothed", "false");

        writeEndElement(); // end y:PolyLineEdge
        writeEndElement(); // end data
    }

    private void writeEdgePath(final EdgeContainer ec) throws XMLStreamException {
        if (ec.getBendpointContainerCount() == 0) {
            writeEmptyElement("y:Path", "sx", "0.0", "sy", "0.0", "tx", "0.0", "ty", "0.0");
        } else {
            writeStartElement("y:Path", "sx", "0.0", "sy", "0.0", "tx", "0.0", "ty", "0.0"); // start y:Path
            for (BendpointContainer bc : ec.iterateBendpointContainers()) {
                writeEmptyElement("y:Point", "x", bc.getX(), "y", bc.getY());
            }
            writeEndElement(); // end y:Path
        }
    }

    private Color getEdgeColor(final EdgeContainer ec) {
        Color color = ec.getColor();
        return color == null ? Color.BLACK : color;
    }

    private String getEdgeType(final EdgeContainer ec) {
        Edge edge = ec.getEdge();
        Class<? extends Edge> edgeClass = edge.getClass();
        String type = ModelConstants.isComposition(edgeClass) || ModelConstants.isPartOfEdge(edgeClass) ? "dashed" : "line";
        return type;
    }

    private void writeEdgeArrows(final EdgeContainer ec) throws XMLStreamException {
        Edge edge = ec.getEdge();
        int direction = edge.getDirection();
        String sourceArrow = direction == Edge.DOUBLE || direction == Edge.BACKWARD ? "delta" : "none";
        String targetArrow = direction == Edge.DOUBLE || direction == Edge.FORWARD ? "delta" : "none";
        writeEmptyElement("y:Arrows", "source", sourceArrow, "target", targetArrow);
    }

    @Override
    protected void writeResources() throws XMLStreamException {
        writeStartElementDataKey(TypeKeys.graphml_resources.getKeyID());
        writeEmptyElement("y:Resources");
        writeEndElement();
    }

    private String getColorString(final Color color) {
        colorBuilder.setLength(1);
        HTMLConverter.appendHTMLColor(colorBuilder, color == null ? Color.black : color);
        return colorBuilder.toString();
    }

}
