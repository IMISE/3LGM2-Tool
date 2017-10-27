package de.imise.tool3lgm.imexport.graphml;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.swing.SwingConstants;
import javax.xml.stream.XMLStreamException;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.NodeRenderer;

public class YFilesGraphmlWriter extends GraphmlWriter {

    public YFilesGraphmlWriter(final File file, final Szenario szenario) throws XMLStreamException, IOException {
        super(file, szenario);
    }

    @Override
    protected String getCreatedByComment() {
        return "Created by Tool3lgm " + Tool3lgmConstants.TOOL_VERSION;
    }

    @Override
    protected void writeXMLSchemaAttributes() throws XMLStreamException {
        writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://www.yworks.com/xml/schema/graphml.html/2.0/ygraphml.xsd ");
        writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
        writeAttribute("xmlns:y", "http://www.yworks.com/xml/yfiles-common/3.0");
        writeAttribute("xmlns:x", "http://www.yworks.com/xml/yfiles-common/markup/3.0");
        writeAttribute("xmlns:yjs", "http://www.yworks.com/xml/yfiles-for-html/2.0/xaml");
        writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    }

    private static class KeyAttributes {
        public String id;
        public String attFor;
        public String attName;
        public String attUri;
        public String attType;
        public String staticMember;
    }

    protected void writeKey(final String attFor, final String attId, final String attName, final String attUri, final String attType, final String staticMember) throws XMLStreamException {
        if (Strings.isNullOrEmpty(staticMember)) {
            writeEmptyElement("key", "id", attId, "for", attFor, "attr.name", attName, "y:attr.uri", attUri, "attr.type", attType);
        } else {
            writeStartElement("key", "id", attId, "for", attFor, "attr.name", attName, "y:attr.uri", attUri);
            writeStartElement("default");
            writeEmptyElement("x:Static", "Member", staticMember);
            writeEndElement();
            writeEndElement();
        }
    }

    private void writeKey(final KeyAttributes atts) throws XMLStreamException {
        writeKey(atts.attFor, atts.id, atts.attName, atts.attUri, atts.attType, atts.staticMember);
    }

    public enum TypeKeys {
        //        <key id="d0" for="node" attr.type="string" attr.name="descrMapper"/>
        node_description_string,
        //        <key id="d1" for="node" attr.name="NodeLabels" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeLabels"/>
        node_NodeLabels,
        //        <key id="d2" for="node" attr.name="NodeGeometry" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeGeometry"/>
        node_NodeGeometry,
        //        <key id="d3" for="all" attr.name="UserTags" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/UserTags"/>
        all_UserTags,
        //        <key id="d4" for="node" attr.name="NodeStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeStyle"/>
        node_NodeStyle,
        edge_description_string,
        //        <key id="d5" for="edge" attr.name="EdgeLabels" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeLabels"/>
        edge_EdgeLabels,
        //        <key id="d6" for="edge" attr.name="EdgeGeometry" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeGeometry"/>
        edge_EdgeGeometry,
        //        <key id="d7" for="edge" attr.name="EdgeStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeStyle"/>
        edge_EdgeStyle,
        //        <key id="d8" for="port" attr.name="PortLocationParameter" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/PortLocationParameter">
        //            <default>
        //                <x:Static Member="y:FreeNodePortLocationModel.NodeCenterAnchored"/>
        //            </default>
        //        </key>
        port_PortLocationParameter {
            @Override
            public String getStaticMember() {
                return "y:FreeNodePortLocationModel.NodeCenterAnchored";
            }
        },
        //        <key id="d9" for="port" attr.name="PortStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/PortStyle">
        //            <default>
        //                <x:Static Member="y:VoidPortStyle.Instance"/>
        //            </default>
        //        </key>
        port_PortStyle {
            @Override
            public String getStaticMember() {
                return "y:VoidPortStyle.Instance";
            }
        },
        //        <key id="d10" attr.name="SharedData" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/SharedData"/>
        SharedData;

        public KeyAttributes keyAttributes() {
            StringTokenizer st = new StringTokenizer(name(), "_");
            int tokenCount = st.countTokens();
            KeyAttributes atts = new KeyAttributes();
            atts.id = getKeyID();
            if (tokenCount != 1) {
                atts.attFor = st.nextToken();
            }
            atts.attName = st.nextToken();
            if (tokenCount < 3) {
                atts.attUri = getUri(atts.attName);
            } else {
                atts.attType = st.nextToken();
            }
            atts.staticMember = getStaticMember();
            return atts;
        }

        public String getStaticMember() {
            return null;
        }

        public static final String getUri(final String uriLastPart) {
            return "http://www.yworks.com/xml/yfiles-common/2.0/" + uriLastPart;
        }

        public String getKeyID() {
            return "d" + ordinal();
        }

    }

    private void writeSharedData() throws XMLStreamException {
        //        <data key="d9">
        //            <y:SharedData>
        //                <yjs:DefaultLabelStyle x:Key="1" textFill="BLACK">
        //                    <yjs:DefaultLabelStyle.font>
        //                        <yjs:Font fontSize="12"/>
        //                    </yjs:DefaultLabelStyle.font>
        //                </yjs:DefaultLabelStyle>
        //            </y:SharedData>
        //        </data>
        writeStartElement("data", "key", TypeKeys.SharedData.getKeyID()); // start data
        writeStartElement("y:SharedData"); // start y:SharedData
        writeStartElement("yjs:DefaultLabelStyle", "x:Key", "1", "textFill", "BLACK"); // start yjs:DefaultLabelStyle
        writeStartElement("yjs:DefaultLabelStyle.font"); // start yjs:DefaultLabelStyle.font
        writeEmptyElement("yjs:Font", "fontSize", "12");
        writeEndElement(); // end yjs:DefaultLabelStyle.font
        writeEndElement(); // end yjs:DefaultLabelStyle
        writeEndElement(); // end y:SharedData
        writeEndElement(); // end data
    }

    @Override
    protected void writeKeys() throws XMLStreamException {
        //        <key id="d0" for="node" attr.type="string" attr.name="descrMapper"/>
        //        <key id="d1" for="node" attr.name="NodeLabels" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeLabels"/>
        //        <key id="d2" for="node" attr.name="NodeGeometry" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeGeometry"/>
        //        <key id="d3" for="all" attr.name="UserTags" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/UserTags"/>
        //        <key id="d4" for="node" attr.name="NodeStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeStyle"/>
        //        <key id="d5" for="edge" attr.name="EdgeLabels" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeLabels"/>
        //        <key id="d6" for="edge" attr.name="EdgeGeometry" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeGeometry"/>
        //        <key id="d7" for="edge" attr.name="EdgeStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeStyle"/>
        //        <key id="d8" for="port" attr.name="PortLocationParameter" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/PortLocationParameter">
        //            <default>
        //                <x:Static Member="y:FreeNodePortLocationModel.NodeCenterAnchored"/>
        //            </default>
        //        </key>
        //        <key id="d9" for="port" attr.name="PortStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/PortStyle">
        //            <default>
        //                <x:Static Member="y:VoidPortStyle.Instance"/>
        //            </default>
        //        </key>
        //        <key id="d10" attr.name="SharedData" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/SharedData"/>
        //        <data key="d10">
        //            <y:SharedData>
        //                <yjs:DefaultLabelStyle x:Key="1" textFill="BLACK">
        //                    <yjs:DefaultLabelStyle.font>
        //                        <yjs:Font fontSize="12"/>
        //                    </yjs:DefaultLabelStyle.font>
        //                </yjs:DefaultLabelStyle>
        //            </y:SharedData>
        //        </data>
        for (TypeKeys typeKey : TypeKeys.values()) {
            writeKey(typeKey.keyAttributes());
        }
        writeSharedData();
    }

    @Override
    protected void writeGraphDescription() throws XMLStreamException {
    }

    @Override
    protected void writeNodeContent(final NodeContainer nc) throws XMLStreamException {
        writeNodeDescription(nc);
        writeNodeLabel(nc);
        writeNodeGeometry(nc);
        writeNodeStyle(nc);
        writeNodePorts(nc);
    }

    protected void writeNodeDescription(final NodeContainer nc) throws XMLStreamException {
        ModelElement me = nc.getElement();
        String description = me.getDescription();
        writeCDATAElementDataKey(TypeKeys.node_description_string.getKeyID(), description);
    }

    protected void writeNodeLabel(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d1">
        //            <x:List>
        //                <y:Label LayoutParameter="{x:Static y:InteriorLabelModel.Center}" Style="{y:GraphMLReference 1}" PreferredSize="54.73342969096785,14">
        //                    <y:Label.Text><![CDATA[Aufgabe 1]]></y:Label.Text>
        //                </y:Label>
        //            </x:List>
        //        </data>
        writeStartElementDataKey(TypeKeys.node_NodeLabels.getKeyID()); // start data
        writeStartElement("x:List"); // start x:List
        writeStartElement("y:Label", "LayoutParameter", "{x:Static y:InteriorLabelModel." + getLabelPosition(nc) + "}", "Style", "{y:GraphMLReference 1}");// , "PreferredSize", "0.0,0"); // start y:Label
        writeCDATAElement("y:Label.Text", getElementName(nc));
        writeEndElement(); // end y:Label
        writeEndElement(); // end x:List
        writeEndElement(); // end data
    }

    private String getLabelPosition(final NodeContainer nc) {
        int halign = nc.getHalign();
        int valign = nc.getValign();
        if (halign == SwingConstants.LEFT) {
            if (valign == SwingConstants.TOP) {
                return "NorthWest";
            } else if (valign == SwingConstants.BOTTOM) {
                return "SouthWest";
            } else {
                return "West";
            }
        } else if (halign == SwingConstants.RIGHT) {
            if (valign == SwingConstants.TOP) {
                return "NorthEast";
            } else if (valign == SwingConstants.BOTTOM) {
                return "SouthEast";
            } else {
                return "East";
            }
        } else {
            if (valign == SwingConstants.TOP) {
                return "North";
            } else if (valign == SwingConstants.BOTTOM) {
                return "South";
            } else {
                return "Center";
            }
        }
    }

    private void writeNodeGeometry(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d2">
        //            <y:RectD X="0" Y="0" Width="70" Height="50"/>
        //        </data>
        double width = nc.getWidth();
        double height = nc.getHeight();
        String x = String.valueOf(nc.getX() - width / 2);
        String y = String.valueOf(nc.getY() - height / 2);
        writeStartElementDataKey(TypeKeys.node_NodeGeometry.getKeyID()); //start data
        writeEmptyElement("y:RectD", "X", x, "Y", y, "Width", String.valueOf(width), "Height", String.valueOf(height));
        writeEndElement(); // end data
    }

    private void writeNodeStyle(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d4">
        //            <yjs:ShapeNodeStyle fill="#FF6868FF" shape="ELLIPSE"/>
        //        </data>
        Enum<?> shape = getYGraphmlShapeName(nc);
        String shapeName = shape == YGraphShape.RECTANGLE ? null : shape.name();
        writeStartElementDataKey(TypeKeys.node_NodeStyle.getKeyID()); //start data
        writeEmptyElement("yjs:ShapeNodeStyle", "fill", getColorString(NodeRenderer.getColor(nc), true), "shape", shapeName);
        writeEndElement(); // end data
    }

    private void writeNodePorts(final NodeContainer nc) throws XMLStreamException {
        //        <port name="p0"/>
        ModelElement me = nc.getElement();
        for (int i = 0; i < me.getEdgesCount(); i++) {
            Edge edge = me.getEdge(i);
            ElementContainer ec = edge.getContainer(szenario);
            if (ec != null /* && ec.isVisible() */) { // das visible darf nicht abgefragt werden!
                writeEmptyElement("port", "name", "p" + i);
            }
        }
    }

    @Override
    protected void writeEdgeContent(final EdgeContainer ec) throws XMLStreamException {
        Edge edge = ec.getEdge();
        writeEdgePorts(edge);
        writeEdgeBendpoints(ec);
        writeEdgeStyle(edge);
    }

    private void writeEdgePorts(final Edge edge) throws XMLStreamException {
        //an das von der super-Klasse geschriebene <edge>-Tag noch die Parameter sourceport und targetport anhängen
        ModelElement start = edge.getStart();
        ModelElement end = edge.getEnd();
        int sourcePortIndex = start.getEdgeIndex(edge);
        int targetPortIndex = end.getEdgeIndex(edge);
        writeAttributes("sourceport", "p" + sourcePortIndex, "targetport", "p" + targetPortIndex);
    }

    private void writeEdgeBendpoints(final EdgeContainer ec) throws XMLStreamException {
        //        <data key="d6">
        //            <x:List>
        //                <y:Bend Location="135,25"/>
        //            </x:List>
        //        </data>
        if (ec.getBendpointContainerCount() > 0) {
            writeStartElementDataKey(TypeKeys.edge_EdgeGeometry.getKeyID()); // start data
            writeStartElement("x:List"); // start x:List
            for (BendpointContainer bc : ec.iterateBendpointContainers()) {
                writeEmptyElement("y:Bend", "Location", bc.getX() + "," + bc.getY());
            }
            writeEndElement(); // end x:List
            writeEndElement(); // end data
        }
    }

    private void writeEdgeStyle(final Edge edge) throws XMLStreamException {
        //        <data key="d7">
        //            <yjs:PolylineEdgeStyle smoothingLength="0" targetArrow="TRIANGLE"/>
        //        </data>
        writeStartElementDataKey(TypeKeys.edge_EdgeStyle.getKeyID()); // start data
        int direction = edge.getDirection();
        String sourceArrow = direction == Edge.DOUBLE || direction == Edge.BACKWARD ? "TRIANGLE" : null;
        String targetArrow = direction == Edge.DOUBLE || direction == Edge.FORWARD ? "TRIANGLE" : null;
        writeEmptyElement("yjs:PolylineEdgeStyle", "smoothingLength", "0", "sourceArrow", sourceArrow, "targetArrow", targetArrow);
        writeEndElement(); // end data
    }

    @Override
    protected void writeResources() throws XMLStreamException {
    }

    private static enum YGraphShape { // in dieser Schreibweise braucht das yEd-Format die shapes. Diese hier gehen in yFiles zwar auch, aber dann wird die Zeichenfarbe (fill-Attribut) ignoriert
        DIAMOND,
        ELLIPSE,
        RECTANGLE,
        TRIANGLE,
        RoundRectangle,
        HEXAGON;
    }

    @Override
    protected Enum<?> getYGraphmlShapeName(final GraphElementLayout.SHAPE shape) {
        switch (shape) {
        case dreieck:
            return YGraphShape.TRIANGLE;
        case oval:
            return YGraphShape.ELLIPSE;
        case rundeck:
            return YGraphShape.RoundRectangle; // aus irgendeinem dummen Grund will yFiles4HTML überall die deprecated Großschreibweise haben, ausser beim ROUND_RECT
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
