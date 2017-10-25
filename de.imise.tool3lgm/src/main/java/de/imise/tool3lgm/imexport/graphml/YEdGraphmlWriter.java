package de.imise.tool3lgm.imexport.graphml;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.Szenario;
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
        int width = nc.getWidth();
        int height = nc.getHeight();
        int x = nc.getX() - width / 2;
        int y = nc.getY() - height / 2;
        writeEmptyElement("y:Geometry", "height", height, "width", width, "x", x, "y", y);
    }

    private final void writeNodeLabel(final NodeContainer nc) throws XMLStreamException {
        writeStartElement("y:NodeLabel"); // start y:NodeLabel
        writeAttribute("alignment", "center");
        writeAttribute("autoSizePolicy", "content");
        writeAttribute("fontFamily", "Dialog");
        writeAttribute("fontSize", "12");
        writeAttribute("fontStyle", "plain");
        writeAttribute("hasBackgroundColor", "false");
        writeAttribute("hasLineColor", "false");
        writeAttribute("height", "18.1328125");
        writeAttribute("horizontalTextPosition", "center");
        writeAttribute("iconTextGap", "4");
        writeAttribute("modelName", "custom");
        writeAttribute("textColor", "#000000");
        writeAttribute("verticalTextPosition", "bottom");
        writeAttribute("visible", "true");
        writeAttribute("width", "72.408203125");
        writeAttribute("x", "17.2958984375");
        writeAttribute("y", "5.93359375");
        writeCharacters(nc.getElement().getClearName());
        writeStartElement("y:LabelModel"); // start y:LabelModel
        writeEmptyElement("y:SmartNodeLabelModel", "distance", "4.0");
        writeEndElement(); // end y:LabelModel
        writeStartElement("y:ModelParameter"); // start y:ModelParameter
        writeEmptyElement("y:SmartNodeLabelModelParameter", "labelRatioX", "0.0", "labelRatioY", "0.0", "nodeRatioX", "0.0", "nodeRatioY", "0.0", "offsetX", "0.0", "offsetY", "0.0", "upX", "0.0", "upY", "-1.0");
        writeEndElement(); // end y:ModelParameter
        writeEndElement(); // end y:NodeLabel
    }

    private String getColorString(final Color color) {
        colorBuilder.setLength(1);
        HTMLConverter.appendHTMLColor(colorBuilder, color == null ? Color.black : color);
        return colorBuilder.toString();
    }

}
