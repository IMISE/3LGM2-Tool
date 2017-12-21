package de.imise.tool3lgm.imexport.graphml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.xml.stream.XMLStreamException;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.xml.Base64;

public class YFilesGraphmlWriter extends GraphmlWriter {

    private final Map<String, String> hashIdToSharedDataKey = new HashMap<>();

    public YFilesGraphmlWriter(final File file, final Szenario szenario, final int layer) throws XMLStreamException, IOException {
        super(file, szenario, layer);
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

        //diese Funktion hier wird garantiert genau 1x pro Schreibvorgang aufgerufen -> sharedDataEntryCount hier zurücksetzen
        hashIdToSharedDataKey.clear();
    }

    private String[] getAttributes(final String... strings) {
        return strings;
    }

    private void writeKey(final YFilesGraphmlWriterDataKeys key) throws XMLStreamException {
        YFilesGraphmlWriterDataKeys.KeyAttributes atts = key.keyAttributes();
        boolean isBoolean = "boolean".equals(atts.attType);
        String[] attributes = getAttributes("id", key.getKeyID(), "for", atts.attFor, "attr.type", atts.attType, "attr.name", atts.attName, "y:attr.uri", atts.attUri);
        if (Strings.isNullOrEmpty(atts.staticMember) && !isBoolean) {
            writeEmptyElement("key", attributes);
        } else {
            writeStartElement("key", attributes); // start key
            writeStartElement("default"); // start default
            if (isBoolean) {
                writeCharacters("true");
            } else {
                writeEmptyElement("x:Static", "Member", atts.staticMember);
            }
            writeEndElement(); // end default
            writeEndElement(); // end key
        }
    }

    @Override
    protected void writeLayerNodeData(final int layer) throws XMLStreamException {
        //        <data key="d0">true</data>
        //        <data key="d3">
        //          <x:List>
        //            <y:Label LayoutParameter="{x:Static y:InteriorLabelModel.North}">
        //              <y:Label.Text><![CDATA[Fachliche Ebene]]></y:Label.Text>
        //              <y:Label.Style>
        //                <yjs:DefaultLabelStyle textFill="BLACK" textSize="20">
        //                  <yjs:DefaultLabelStyle.font>
        //                    <yjs:Font fontSize="20"/>
        //                  </yjs:DefaultLabelStyle.font>
        //                </yjs:DefaultLabelStyle>
        //              </y:Label.Style>
        //            </y:Label>
        //          </x:List>
        //        </data>
        //        <data key="d4">
        //          <y:RectD X="0" Y="0" Width="1024" Height="768"/>
        //        </data>
        //        <data key="d6">
        //          <yjs:CollapsibleNodeStyleDecorator>
        //            <yjs:ShapeNodeStyle fill="#FFF0F0F0"/>
        //          </yjs:CollapsibleNodeStyleDecorator>
        //        </data>
        writeElementDataKey(YFilesGraphmlWriterDataKeys.node_Expanded_boolean.getKeyID(), "true"); // start data - end data
        writeNodeLabel(szenario.getLayer(layer));
        String x = String.valueOf(currentSzenarioLayerOffsetX);
        String y = String.valueOf(currentSzenarioLayerOffsetY);
        String w = String.valueOf(szenario.getPageWidth());
        String h = String.valueOf(szenario.getPageHeight());
        writeNodeGeometry(x, y, w, h);
        writeStartElementDataKey(YFilesGraphmlWriterDataKeys.node_NodeStyle.getKeyID()); // start data
        writeStartElement("yjs:CollapsibleNodeStyleDecorator"); // start yjs:CollapsibleNodeStyleDecorator
        writeEmptyElement("yjs:ShapeNodeStyle", "fill", "#FFF0F0F0");
        writeEndElement(); // end yjs:CollapsibleNodeStyleDecorator
        writeEndElement(); // end data
    }

    private void writeSharedData() throws XMLStreamException {
        //    <data key="d14">
        //        <y:SharedData>
        //            <yjs:DefaultLabelStyle x:Key="1" verticalTextAlignment="TOP" horizontalTextAlignment="LEFT" wrapping="WORD" textFill="BLACK"/>
        //                <yjs:DefaultLabelStyle.font>
        //                    <yjs:Font fontSize="12" fontStyle="ITALIC" fontWeight="BOLD"/>
        //                </yjs:DefaultLabelStyle.font>
        //            </yjs:DefaultLabelStyle>
        //        </y:SharedData>
        //    </data>
        writeStartElement("data", "key", YFilesGraphmlWriterDataKeys.SharedData.getKeyID()); // start data
        writeStartElement("y:SharedData"); // start y:SharedData
        writeSharedDataEntries();
        writeEndElement(); // end y:SharedData
        writeEndElement(); // end data
    }

    private void writeSharedDataEntries() throws XMLStreamException {
        for (int layer : ModelConstants.VISIBLE_LAYERS) {
            LayerContainer lc = szenario.getLayer(layer);
            writeLabelStyleAsSharedData(lc);
            for (NodeContainer nc : lc.getKnoten()) {
                writeLabelStyleAsSharedData(nc);
                if (!writeIconAsSharedData(nc)) {
                    writeNodeStyleAsSharedData(nc);
                }
            }
            for (EdgeContainer ec : lc.getKanten()) {
                writeEdgeStyleAsSharedData(ec);
            }
        }
    }

    private void writeLabelStyleAsSharedData(final ElementContainer ec) throws XMLStreamException {
        //            <yjs:DefaultLabelStyle x:Key="1" verticalTextAlignment="TOP" horizontalTextAlignment="LEFT" wrapping="WORD" textFill="BLACK"/>
        //                <yjs:DefaultLabelStyle.font>
        //                    <yjs:Font fontSize="12" fontStyle="ITALIC" fontWeight="BOLD"/>
        //                </yjs:DefaultLabelStyle.font>
        //            </yjs:DefaultLabelStyle>
        YFilesGraphmlLabelStyle style = YFilesGraphmlLabelStyle.createLabelStyle(ec);
        //nur bei den ausgeblendeten Labels ist dieser mainLabelStyle != null -> diese brauchen nicht in sharedData geschrieben
        // werden, da sie keine Referenz sein können, da diese Refrenz auch immer über den mainLabelStyle abgebildet wird
        if (style.mainLabelStyle != null) {
            return;
        }
        String styleKey = style.getLabelStyleKey();
        String sharedDataStyleKey = getNewSharedDataStyleKey(styleKey);
        if (sharedDataStyleKey != null) {
            writeStartElement("yjs:DefaultLabelStyle", "x:Key", sharedDataStyleKey, "verticalTextAlignment", style.valign, "horizontalTextAlignment", style.halign, "wrapping", style.wrapping, "textFill", "BLACK", "textSize", style.textSize); // start yjs:DefaultLabelStyle
            writeStartElement("yjs:DefaultLabelStyle.font"); // start yjs:DefaultLabelStyle.font
            writeEmptyElement("yjs:Font", "fontSize", style.fontSize, "fontStyle", style.fontStyle, "fontWeight", style.fontWeight);
            writeEndElement(); // end yjs:DefaultLabelStyle.font
            writeEndElement(); // end yjs:DefaultLabelStyle
        }
    }

    private void writeNodeStyleAsSharedData(final NodeContainer nc) throws XMLStreamException {
        YFilesGraphmlNodeStyle style = new YFilesGraphmlNodeStyle(nc);
        String styleKey = style.getNodeStyleKey();
        String sharedDataStyleKey = getNewSharedDataStyleKey(styleKey);
        if (sharedDataStyleKey != null) {
            writeEmptyElement("yjs:ShapeNodeStyle", "x:Key", sharedDataStyleKey, "fill", style.fill, "shape", style.shape);
        }
    }

    private void writeEdgeStyleAsSharedData(final EdgeContainer ec) throws XMLStreamException {
        //        <data key="d7">
        //            <yjs:PolylineEdgeStyle smoothingLength="0" targetArrow="TRIANGLE"/>
        //        </data>
        YFilesGraphmlEdgeStyle style = new YFilesGraphmlEdgeStyle(ec);
        String styleKey = style.getEdgeStyleKey();
        String sharedDataStyleKey = getNewSharedDataStyleKey(styleKey);
        if (sharedDataStyleKey != null) {
            String startTag = "yjs:PolylineEdgeStyle";
            if (style.isDashed) {
                writeStartElement(startTag); // start yjs:PolylineEdgeStyle
            } else {
                writeEmptyElement(startTag);
            }
            writeAttributes("x:Key", sharedDataStyleKey, "smoothingLength", style.smoothingLength, "sourceArrow", style.sourceArrow, "targetArrow", style.targetArrow);
            if (style.isDashed) {
                //          <yjs:PolylineEdgeStyle.stroke>
                //            <yjs:Stroke fill="BLACK" dashStyle="Dash" thickness="1"/>
                //          </yjs:PolylineEdgeStyle.stroke>
                writeStartElement("yjs:PolylineEdgeStyle.stroke"); // start yjs:PolylineEdgeStyle.stroke
                writeEmptyElement("yjs:Stroke", "fill", style.strokeFill, "dashStyle", style.strokeDashStyle, "thickness", style.strokeThickness);
                writeEndElement(); // end yjs:PolylineEdgeStyle.stroke
                writeEndElement(); // end yjs:PolylineEdgeStyle
            }
        }
    }

    private boolean writeIconAsSharedData(final NodeContainer nc) throws XMLStreamException {
        String styleKey = nc.getIconString();
        //das Element hat kein Icon
        if (styleKey == null) {
            return false;
        }
        String sharedDataKey = getNewSharedDataStyleKey(styleKey);
        if (sharedDataKey != null) {
            Map<String, byte[]> iconTable = szenario.getCollection().getIconTable();
            byte[] icon = iconTable.get(styleKey);
            writeEmptyElement("yjs:ImageNodeStyle", "x:Key", sharedDataKey, "image", "data:image/png;base64," + Base64.encode(icon));
        }
        return true;
    }

    public String getNewSharedDataStyleKey(final String styleKey) {
        String sharedDataStyleKey = hashIdToSharedDataKey.get(styleKey);
        //dieses Layout wurde schon in die SharedData Section geschrieben
        if (sharedDataStyleKey != null) {
            return null;
        }
        sharedDataStyleKey = String.valueOf(hashIdToSharedDataKey.size());
        hashIdToSharedDataKey.put(styleKey, sharedDataStyleKey);
        return sharedDataStyleKey;
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
        for (YFilesGraphmlWriterDataKeys typeKey : YFilesGraphmlWriterDataKeys.values()) {
            writeKey(typeKey);
        }
        writeSharedData();
    }

    @Override
    protected void writeGraphDescription() throws XMLStreamException {
    }

    @Override
    protected void writeNodeContent(final NodeContainer nc) throws XMLStreamException {
        writeElementDescription(nc);
        writeElementTlgmId(nc);
        writeNodeLabel(nc);
        writeNodeGeometry(nc);
        writeNodeStyle(nc);
        writeNodePorts(nc);
    }

    protected void writeElementDescription(final ElementContainer ec) throws XMLStreamException {
        ModelElement me = ec.getElement();
        String description = me.getDescription();
        if (!Strings.isNullOrEmpty(description)) {
            YFilesGraphmlWriterDataKeys key = ec instanceof EdgeContainer ? YFilesGraphmlWriterDataKeys.edge_description_string : YFilesGraphmlWriterDataKeys.node_description_string;
            writeCDATAElementDataKey(key.getKeyID(), me.getDescription());
        }
    }

    protected final void writeElementTlgmId(final ElementContainer ec) throws XMLStreamException {
        YFilesGraphmlWriterDataKeys key = ec instanceof EdgeContainer ? YFilesGraphmlWriterDataKeys.edge_tlgmid_string : YFilesGraphmlWriterDataKeys.node_tlgmid_string;
        writeElementDataKey(key.getKeyID(), ec.getHashString());
    }

    private void writeNodeLabel(final ElementContainer ec) throws XMLStreamException {
        //unsichtbares Label
        //        <data key="d1">
        //            <x:List>
        //                <y:Label LayoutParameter="{x:Static y:InteriorStretchLabelModel.Center}" Style="{x:Static y:VoidLabelStyle.Instance}">
        //                    <y:Label.Text><![CDATA[Aufgabe 1]]></y:Label.Text>
        //                </y:Label>
        //            </x:List>
        //        </data>
        //oder sichtbares Label
        //    <data key="d1">
        //        <x:List>
        //            <y:Label LayoutParameter="{x:Static y:InteriorStretchLabelModel.Center}">
        //                <y:Label.Text><![CDATA[Aufgabe 1]]></y:Label.Text>
        //                <y:Label.Style>
        //                    <yjs:DefaultLabelStyle verticalTextAlignment="TOP" horizontalTextAlignment="LEFT" wrapping="WORD" textFill="BLACK"/>
        //                        <yjs:DefaultLabelStyle.font>
        //                            <yjs:Font fontSize="12" fontStyle="ITALIC" fontWeight="BOLD"/>
        //                        </yjs:DefaultLabelStyle.font>
        //                    </yjs:DefaultLabelStyle>
        //                </y:Label.Style>
        //            </y:Label>
        //        </x:List>
        //    </data>
        //oder Label mit Icon
        //        <data key="d1">
        //            <x:List>
        //                <y:Label LayoutParameter="{x:Static y:ExteriorLabelModel.South}">
        //                  ... alles wie oben ...
        //                </y:Label>
        //            </x:List>
        //        </data>
        //oder beim Label des LayerKnotens
        //      <data key="d2">
        //          <x:List>
        //              <y:Label LayoutParameter="{x:Static y:InteriorLabelModel.North}" Style="{y:GraphMLReference 4}" PreferredSize="148.9838161374421,22">
        //                  <y:Label.Text><![CDATA[Fachliche Ebene]]></y:Label.Text>
        //                  <y:Label.Style>
        //                      <yjs:DefaultLabelStyle textFill="BLACK" textSize="20">
        //                          <yjs:DefaultLabelStyle.font>
        //                              <yjs:Font fontSize="20"/>
        //                          </yjs:DefaultLabelStyle.font>
        //                      </yjs:DefaultLabelStyle>
        //                   </y:Label.Style>
        //                </y:Label>
        //            </x:List>
        //        </data>
        YFilesGraphmlLabelStyle labelStyle = YFilesGraphmlLabelStyle.createLabelStyle(ec);
        writeStartElementDataKey(YFilesGraphmlWriterDataKeys.node_NodeLabels.getKeyID()); // start data
        writeStartElement("x:List"); // start x:List
        //wenn der labelStyle selbst kein MainLabelStyle hatte -> Referenz auf die SharedData eintragen
        if (labelStyle.mainLabelStyle == null) {
            String labelStyleKey = labelStyle.getLabelStyleKey();
            String sharedDataLabelStyleKey = hashIdToSharedDataKey.get(labelStyleKey);
            labelStyle.mainLabelStyle = "{y:GraphMLReference " + sharedDataLabelStyleKey + "}";
        }
        writeStartElement("y:Label", "LayoutParameter", labelStyle.labelLayout, "Style", labelStyle.mainLabelStyle); // start y:Label
        writeCDATAElement("y:Label.Text", getElementName(ec));
        writeEndElement(); // end y:Label
        writeEndElement(); // end x:List
        writeEndElement(); // end data
    }

    private void writeNodeGeometry(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d2">
        //            <y:RectD X="0" Y="0" Width="70" Height="50"/>
        //        </data>
        Icon icon = nc.getIcon();
        double width = icon == null ? nc.getWidth() : icon.getIconWidth();
        double height = icon == null ? nc.getHeight() : icon.getIconHeight();
        String x = String.valueOf(nc.getX() - width / 2 + currentSzenarioLayerElementsOffsetX);
        String y = String.valueOf(nc.getY() - height / 2 + currentSzenarioLayerElementsOffsetY);
        String w = String.valueOf(width);
        String h = String.valueOf(height);
        writeNodeGeometry(x, y, w, h);
    }

    private void writeNodeGeometry(final String x, final String y, final String w, final String h) throws XMLStreamException {
        writeStartElementDataKey(YFilesGraphmlWriterDataKeys.node_NodeGeometry.getKeyID()); //start data
        writeEmptyElement("y:RectD", "X", x, "Y", y, "Width", w, "Height", h);
        writeEndElement(); // end data
    }

    private void writeNodeStyle(final NodeContainer nc) throws XMLStreamException {
        //        <data key="d4">
        //            <yjs:ShapeNodeStyle fill="#FF6868FF" shape="ELLIPSE"/>
        //        </data>
        writeStartElementDataKey(YFilesGraphmlWriterDataKeys.node_NodeStyle.getKeyID()); //start data
        String iconHash = nc.getIconString();
        String styleKey = iconHash != null ? iconHash : new YFilesGraphmlNodeStyle(nc).getNodeStyleKey();
        writeSharedDataReference(styleKey);
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
        writeElementDescription(ec);
        writeElementTlgmId(ec);
        writeEdgeBendpoints(ec);
        writeEdgeStyle(ec);
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
            writeStartElementDataKey(YFilesGraphmlWriterDataKeys.edge_EdgeGeometry.getKeyID()); // start data
            writeStartElement("x:List"); // start x:List
            for (BendpointContainer bc : ec.iterateBendpointContainers()) {
                String x = String.valueOf(bc.getX() + currentSzenarioLayerElementsOffsetX);
                String y = String.valueOf(bc.getY() + currentSzenarioLayerElementsOffsetY);
                writeEmptyElement("y:Bend", "Location", x + "," + y);
            }
            writeEndElement(); // end x:List
            writeEndElement(); // end data
        }
    }

    private void writeEdgeStyle(final EdgeContainer ec) throws XMLStreamException {
        //        <data key="d7">
        //            <yjs:PolylineEdgeStyle smoothingLength="0" targetArrow="TRIANGLE"/>
        //        </data>
        YFilesGraphmlEdgeStyle style = new YFilesGraphmlEdgeStyle(ec);
        writeStartElementDataKey(YFilesGraphmlWriterDataKeys.edge_EdgeStyle.getKeyID()); // start data
        writeSharedDataReference(style.getEdgeStyleKey());
        writeEndElement(); // end data
    }

    @Override
    protected void writeResources() throws XMLStreamException {
    }

    protected void writeSharedDataReference(final String styleKey) throws XMLStreamException {
        String sharedDataKey = hashIdToSharedDataKey.get(styleKey);
        writeEmptyElement("y:GraphMLReference", "ResourceKey", sharedDataKey);
    }

}
