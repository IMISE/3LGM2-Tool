package de.imise.tool3lgm.imexport.graphml;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.help.UnsupportedOperationException;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelInstance;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.util.htmlxml.HTMLConverter;
import de.imise.util.image.ImageTools;

public class YEdGraphmlWriter extends GraphmlWriter {

    public YEdGraphmlWriter(final File file, final Szenario szenario, final int layer) throws XMLStreamException, IOException {
        super(file, szenario, layer);
    }

    @Override
    protected String getCreatedByComment() {
        return "Created by Tool3lgm " + Tool3lgmConstants.TOOL_VERSION;
    }

    @Override
    protected void writeXMLSchemaAttributes() throws XMLStreamException {
        writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writeAttribute("xmlns:java", "http://www.yworks.com/xml/yfiles-common/1.0/java");
        writeAttribute("xmlns:sys", "http://www.yworks.com/xml/yfiles-common/markup/primitives/2.0");
        writeAttribute("xmlns:x", "http://www.yworks.com/xml/yfiles-common/markup/2.0");
        writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        writeAttribute("xmlns:y", "http://www.yworks.com/xml/graphml");
        writeAttribute("xmlns:yed", "http://www.yworks.com/xml/yed/3");
        writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml/1.1/ygraphml.xsd");
    }

    private void writeKeyYFilesType(final String attFor, final String attId, final String yFilesType) throws XMLStreamException {
        writeEmptyElement("key", "for", attFor, "id", attId, "yfiles.type", yFilesType);
    }

    private void writeKeyGeneralType(final String attFor, final String attId, final String attName, final String attType) throws XMLStreamException {
        writeEmptyElement("key", "attr.name", attName, "attr.type", attType, "for", attFor, "id", attId);
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

    private enum TypeKeys {
        graph_Description_string,
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
    protected void writeGraphDescription() throws XMLStreamException {
        String description = szenario.getDescription();
        writeCDATAElementDataKey(TypeKeys.graph_Description_string.getKeyID(), description);
    }

    @Override
    protected void writeLayerNodeData(final int layer) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void writeNodeContent(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d5"><![CDATA[Hallo]]></data>
        writeCDATAElementDataKey(TypeKeys.node_description_string.getKeyID(), nc.getElement().getDescription());
        //        <data key="d6">
        //...
        //      </data>
        writeStartElementDataKey(TypeKeys.node_nodegraphics.getKeyID()); // start data
        YGraphShape shape = (YGraphShape) getYGraphmlShape(nc);
        String shapeName = shape.toString();
        writeNode(nc, shapeName, shape.isGenericNode());
        writeEndElement(); // end data
    }

    private void writeNode(final NodeContainer nc, final String shapeName, final boolean genericNode) throws XMLStreamException {
        //        <y:ShapeNode>
        //          <y:Geometry height="30.0" width="107.0" x="116.5" y="-345.0"/>
        //          <y:Fill color="#CCCCFF" transparent="false"/>
        //          <y:BorderStyle color="#000000" raised="false" type="line" width="1.0"/>
        //          <y:NodeLabel alignment="right" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="62.1328125" horizontalTextPosition="center" iconData="1" iconTextGap="4" modelName="internal" modelPosition="c" textColor="#000000" verticalTextPosition="bottom" visible="true" width="187.9140625" x="2.04296875" y="-3.06640625">Physischer DV-Baustein 1 Label</y:NodeLabel>
        //          <y:Shape type="ellipse"/>
        //        </y:ShapeNode>
        //oder
        //        <y:GenericNode configuration="com.yworks.flowchart.dataBase">
        //            <y:Geometry height="40.0" width="60.0" x="120.0" y="-363.0"/>
        //            <y:Fill color="#FFFF00" color2="#FFFF00" transparent="false"/>
        //            <y:BorderStyle color="#000000" type="line" width="1.0"/>
        //          <y:NodeLabel alignment="right" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="62.1328125" horizontalTextPosition="center" iconData="1" iconTextGap="4" modelName="internal" modelPosition="c" textColor="#000000" verticalTextPosition="bottom" visible="true" width="187.9140625" x="2.04296875" y="-3.06640625">Physischer DV-Baustein 1 Label</y:NodeLabel>
        //         </y:GenericNode>
        if (genericNode) {
            writeStartElement("y:GenericNode", "configuration", shapeName); // start y:GenericNode
        } else {
            writeStartElement("y:ShapeNode"); // start y:ShapeNode
        }
        writeNodeGeometry(nc);
        writeNodeColor(nc, genericNode);
        writeNodeBorderStyle(nc, genericNode);
        writeNodeLabel(nc);
        if (!genericNode) {
            writeEmptyElement("y:Shape", "type", shapeName);
        }
        writeEndElement(); // end y:GenericNode oder end ShapeNode
    }

    private void writeNodeGeometry(final NodeContainer nc) throws XMLStreamException {
        double width = nc.getWidth();
        double height = nc.getHeight();
        String x = String.valueOf(nc.getX() - width / 2);
        String y = String.valueOf(nc.getY() - height / 2);
        writeEmptyElement("y:Geometry", "height", String.valueOf(height), "width", String.valueOf(width), "x", x, "y", y);
    }

    private void writeNodeColor(final NodeContainer nc, final boolean genericNode) throws XMLStreamException {
        //          <y:Fill hasColor="false" transparent="false"/>
        //oder
        //          <y:Fill color="#FFFF00" transparent="false"/>
        //oder
        //          <y:Fill color="#FFFF00" color2="#FFFF00" transparent="false"/>
        if (nc.getIcon() == null) {
            String colorString = getColorString(getColor(nc), false);
            writeEmptyElement("y:Fill", "color", colorString, "color2", genericNode ? colorString : null);
        } else {
            writeEmptyElement("y:Fill", "hasColor", "false");
        }
        writeAttribute("transparent", "false");
    }

    private void writeNodeBorderStyle(final NodeContainer nc, final boolean genericNode) throws XMLStreamException {
        //          <y:BorderStyle hasColor="false" raised="false" type="line" width="1.0"/>
        //oder
        //          <y:BorderStyle color="#000000" raised="false" type="line" width="1.0"/>
        //oder
        //          <y:BorderStyle color="#000000" type="line" width="1.0"/>
        writeEmptyElement("y:BorderStyle");
        if (nc.getIcon() != null) {
            writeAttribute("hasColor", "false");
        } else {
            writeAttribute("color", "#000000");
        }
        writeAttributes("raised", genericNode ? null : "false", "type", "line", "width", "1.0");
    }

    // 3LGM oben -> unten, links -> rechts -------> yEd modelPosition, alignment
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
        int valign = nc.getValign();
        if (valign == SwingConstants.TOP) {
            return "t";
        }
        if (valign == SwingConstants.BOTTOM) {
            return "b";
        }
        return "c";
    }

    private String getFontStyle(final NodeContainer nc) {
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
        //          <y:NodeLabel alignment="right" autoSizePolicy="content" fontFamily="Dialog" fontSize="12" fontStyle="plain" hasBackgroundColor="false" hasLineColor="false" height="62.1328125" horizontalTextPosition="center" iconData="1" iconTextGap="4" modelName="internal" modelPosition="c" textColor="#000000" verticalTextPosition="bottom" visible="true" width="187.9140625" x="2.04296875" y="-3.06640625">Physischer DV-Baustein 1 Label</y:NodeLabel>
        boolean hideText = nc.hideText();
        boolean hasIcon = nc.getIcon() != null;
        writeStartElement("y:NodeLabel"); // start y:NodeLabel
        writeAttribute("alignment", getAlignment(nc)); //################################
        writeAttribute("autoSizePolicy", hideText ? "node_size" : hasIcon ? "content" : "node_width");
        writeAttribute("configuration", hasIcon ? null : "CroppingLabel");
        writeAttribute("fontFamily", "Dialog");
        writeAttribute("fontSize", nc.getFont().getSize());
        writeAttribute("fontStyle", getFontStyle(nc));
        writeAttribute("hasBackgroundColor", "false");
        writeAttribute("hasLineColor", "false");
        writeAttribute("height", "0.0");
        writeAttribute("horizontalTextPosition", "center");
        writeAttribute("iconData", getIconResourceID(nc)); // wenn hier null zurück kommt, wird das Attribut nicht geschrieben!
        writeAttribute("iconTextGap", "4");
        writeAttribute("modelName", "internal");
        writeAttribute("modelPosition", getModelPosition(nc)); //################################ c, t, b, l, r, tl, tr, bl, br
        writeAttribute("textColor", "#000000");
        writeAttribute("verticalTextPosition", "bottom");
        writeAttribute("visible", !hideText);
        writeAttribute("width", "0.0");
        writeAttribute("x", "0.0");
        writeAttribute("y", "0.0");
        writeCharacters(getElementName(nc));
        writeEndElement(); // end y:NodeLabel
    }

    private final List<String> usedIconResources = new ArrayList<>();

    private String getIconResourceID(final NodeContainer nc) {
        String iconId = nc.getIconString();
        int resourceID = 0;
        if (iconId != null) {
            resourceID = usedIconResources.indexOf(iconId) + 1;
            if (resourceID < 1) {
                usedIconResources.add(iconId);
                resourceID = usedIconResources.size();
            }
        }
        return resourceID < 1 ? null : String.valueOf(resourceID);
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
        writeEmptyElement("y:LineStyle", "color", getColorString(getEdgeColor(ec), false), "type", getEdgeType(ec), "width", "1.0");
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
                writeEmptyElement("y:Point", "x", String.valueOf(bc.getX()), "y", String.valueOf(bc.getY()));
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
        String type = MetaModelInstance.isComposition(edgeClass) || MetaModelInstance.isHasPartEdge(edgeClass) ? "dashed" : "line";
        return type;
    }

    private void writeEdgeArrows(final EdgeContainer ec) throws XMLStreamException {
        Edge edge = ec.getEdge();
        Class<? extends Edge> edgeClass = edge.getClass();
        MetaModelInstance metaModel = edge.getMetaModel();
        ConnectionState direction = metaModel.isDirectedEdge(edgeClass) ? MetaModelInstance.isDoubleMeaningEdge(edgeClass) ? ((DoubleMeaningEdge) edge).getConnectionState() : ConnectionState.FORWARD : ConnectionState.DOUBLE;
        String sourceArrow = direction == ConnectionState.DOUBLE || direction == ConnectionState.BACKWARD ? "delta" : "none";
        String targetArrow = direction == ConnectionState.DOUBLE || direction == ConnectionState.FORWARD ? "delta" : "none";
        writeEmptyElement("y:Arrows", "source", sourceArrow, "target", targetArrow);
    }

    @Override
    protected void writeResources() throws XMLStreamException, IOException {
        //      <data key="d7">
        //        <y:Resources>
        //          <y:Resource id="1">
        //            <yed:ScaledIcon xScale="1.0" yScale="1.0">
        //              <yed:ImageIcon image="3"/>
        //            </yed:ScaledIcon>
        //          </y:Resource>
        //          <y:Resource id="2">
        //            <yed:ScaledIcon xScale="1.0" yScale="1.0">
        //              <yed:ImageIcon image="4"/>
        //            </yed:ScaledIcon>
        //          </y:Resource>
        //          <y:Resource id="3" type="java.awt.image.BufferedImage">iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAABK0lEQVR42u2YwQ2DMAwAPQsrdIWs&#13;
        //    wAqswCz58egWXSErdIf++0gxrZFJU5qIGIyUSCdBBPRksJ0U/GcAgBr4AJJzzqmBi1ZBEcG2bb0x&#13;
        //    Zlf6vk8XxBu6znlrvTj4O4h/PvIE8ebxcCI2aJ6uSYGk+PEmwZhUCYpF8FSCpeRCySKCUnJqBYtF&#13;
        //    UEKsaATVC9ZXrFWwRrC+4lRBvvrYKhNGjstlC+LicRiuMziHD5Ama8kPN5iRWtpbf5nJ3pOoF7yP&#13;
        //    c8Thmyb1G3cuaMf0QjBROLjT+8W/HVvTNMl8CeIDNEWPM7mpF9T2r0IkWd6C9O1pICrIu0bJUpHD&#13;
        //    agQ1jXMKhq0tVu/w++DnuybJka1tVZDqYE61l2ZRB6nN8TQP29yerY4LLlYz6gU1guMFR3t+FHQK&#13;
        //    YwgAAAAASUVORK5CYII=</y:Resource>
        //          <y:Resource id="4" type="java.awt.image.BufferedImage">iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAiklEQVR42u2WAQrAIAhF/+G69a7g&#13;
        //    FTrLVhvBGBQmNldT+ARR+gwlsRsbHMABPgsAQFVdAPkCEamqBjEHQAhBJBWA7OhpaZul66wiADfw&#13;
        //    EIDe4GsBSII7gAOsA0DFk8DW+Yz+B2A+Ed1NOgsUiWZC7exbr9CsAWw4ZVaEMa2RWZC1rOdtQ9Ma&#13;
        //    eLMND3AwIsiX8uOAAAAAAElFTkSuQmCC</y:Resource>
        //        </y:Resources>
        //      </data>
        writeStartElementDataKey(TypeKeys.graphml_resources.getKeyID()); // start data
        if (usedIconResources.isEmpty()) {
            writeEmptyElement("y:Resources");
        } else {
            writeStartElement("y:Resources"); // start y:Resources
            int imageCount = usedIconResources.size();
            for (int i = 1; i <= imageCount; i++) {
                writeImageScale(i, i + imageCount);
            }
            for (int i = 0; i < imageCount; i++) {
                writeImageData(i, i + 1 + imageCount);
            }
            writeEndElement(); // end y:Resources
        }
        writeEndElement(); // end data
    }

    private void writeImageScale(final int scaleID, final int dataID) throws XMLStreamException {
        //          <y:Resource id="1">
        //            <yed:ScaledIcon xScale="1.0" yScale="1.0">
        //              <yed:ImageIcon image="3"/>
        //            </yed:ScaledIcon>
        //          </y:Resource>
        writeStartElement("y:Resource", "id", String.valueOf(scaleID)); // start y:Resource
        writeStartElement("yed:ScaledIcon", "xScale", "1.0", "yScale", "1.0"); // start yed:ScaledIcon
        writeEmptyElement("yed:ImageIcon", "image", String.valueOf(dataID));
        writeEndElement(); // end yed:ScaledIcon
        writeEndElement(); // end y:Resource
    }

    private void writeImageData(final int usedIconResourceIndex, final int dataID) throws XMLStreamException, IOException {
        //          <y:Resource id="4" type="java.awt.image.BufferedImage">iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAiklEQVR42u2WAQrAIAhF/+G69a7g&#13;
        //    FTrLVhvBGBQmNldT+ARR+gwlsRsbHMABPgsAQFVdAPkCEamqBjEHQAhBJBWA7OhpaZul66wiADfw&#13;
        //    EIDe4GsBSII7gAOsA0DFk8DW+Yz+B2A+Ed1NOgsUiWZC7exbr9CsAWw4ZVaEMa2RWZC1rOdtQ9Ma&#13;
        //    eLMND3AwIsiX8uOAAAAAAElFTkSuQmCC</y:Resource>
        writeStartElement("y:Resource", "id", String.valueOf(dataID), "type", "java.awt.image.BufferedImage"); // start y:Resource
        writeImageDataLines(ImageTools.getBase64EncodedImage(getResourceIconAsImage(usedIconResourceIndex)));
        writeEndElement(); // end y:Resource
    }

    private BufferedImage getResourceIconAsImage(final int usedIconResourceIndex) {
        String iconHashID = usedIconResources.get(usedIconResourceIndex);
        GDCollection gdcoll = szenario.getCollection();
        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        byte[] iconData = iconTable.get(iconHashID);
        ImageIcon icon = new ImageIcon(iconData);
        return ImageTools.toBufferedImage(icon);
    }

    public static final int LINE_LENGTH = 76;

    public static final String LINE_END = "&#13;\n";

    private void writeImageDataLines(final String encodedImage) throws XMLStreamException, IOException {
        for (int i = 0; i * LINE_LENGTH < encodedImage.length(); i++) {
            int start = i * LINE_LENGTH;
            int fullLength = encodedImage.length();
            int end = start + LINE_LENGTH;
            String line = encodedImage.substring(start, end < fullLength ? end : fullLength);
            writeCharacters(line);
            if (line.length() == LINE_LENGTH && (i + 1) * LINE_LENGTH < encodedImage.length()) {
                writeCharactersUnescaped(LINE_END);
            }
        }
    }

    private final StringBuilder colorBuilder = new StringBuilder("#");

    protected String getColorString(final Color color, final boolean alpha) {
        colorBuilder.setLength(1);
        HTMLConverter.appendHTMLColor(colorBuilder, color == null ? Color.black : color, alpha);
        return colorBuilder.toString();
    }

    private static enum YGraphShape { // in dieser Schreibweise braucht das yEd-Format die shapes. Diese hier gehen in yFiles zwar auch, aber dann wird die Zeichenfarbe (fill-Attribut) ignoriert
        rectangle,
        triangle,
        ellipse,
        roundrectangle,
        diamond,
        hexagon,
        com_yworks_flowchart_dataBase,
        com_yworks_flowchart_predefinedProcess;

        @Override
        public String toString() {
            return isGenericNode() ? name().replaceAll("_", ".") : name();
        }

        public boolean isGenericNode() {
            return ordinal() >= com_yworks_flowchart_dataBase.ordinal();
        }
    }

    private final Enum<?> getYGraphmlShape(final NodeContainer nc) {
        GraphElementLayout.SHAPE shape = nc.getForm();
        if (shape == null) {
            shape = nc.getGraphDocument().getMapping().getStandardForm(nc);
        }
        return getYGraphmlShape(shape);
    }

    private Enum<?> getYGraphmlShape(final GraphElementLayout.SHAPE shape) {
        switch (shape) {
        case dreieck:
            return YGraphShape.triangle;
        case oval:
            return YGraphShape.ellipse;
        case rundeck:
            return YGraphShape.roundrectangle;
        case rhombus:
            return YGraphShape.diamond;
        case wabe:
            return YGraphShape.hexagon;
        case tonne:
            return YGraphShape.com_yworks_flowchart_dataBase;
        case ordner:
            return YGraphShape.com_yworks_flowchart_predefinedProcess;
        default:
            return YGraphShape.rectangle;
        }
    }

}
