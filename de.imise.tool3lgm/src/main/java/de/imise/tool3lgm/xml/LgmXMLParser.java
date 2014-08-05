package de.imise.tool3lgm.xml;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.util.Hashtable;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.Konfiguration;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.InterLayerConnectedNodeContainer;
import de.imise.tool3lgm.graphtools.view.container.KonfigurationContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.log.Log;

public class LgmXMLParser extends DefaultHandler {

    public static final int START = 0;
    public static final int IN_GRAPH = 1;
    public static final int IN_DESCRIPTION = 2;
    public static final int IN_MAPPING = 3;
    public static final int IN_ELEMENT = 4;
    public static final int IN_LAYER = 5;
    public static final int IN_LAYOUT = 6;
    public static final int IN_COLOR = 7;
    public static final int IN_FIELD = 8;
    public static final int UNKNOWN = 9;
    public static final int END = 10;
    public static final int IN_CONTAINER = 11;

    private final StringBuilder elementTextBuf = new StringBuilder();

    private int state, file_state, prev_state;
    private int unknown_depth;
    private LGMGraphDocument doc;
    private ElementContainer ec;
    private ModelElement me;
    private GraphElementLayout layout;
    private String value;
    private String color_name;
    private int red, green, blue, alpha;
    private boolean read_mapping = true;
    private Hashtable<String, byte[]> iconTable = null;
    private boolean loadIcons = false;

    static SAXParserFactory factory = SAXParserFactory.newInstance();
    static SAXParser parser = null;
    static {
        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerParser"), e);
        }
    }

    public LgmXMLParser() {
        unknown_depth = 0;
        file_state = START;
        state = START;
    }

    @Override
    public void startElement(final String namespaceURI, final String localName, String rawName, final Attributes atts) {
        try {
            if (state == UNKNOWN) {
                unknown_depth++;
                return;
            }

            elementTextBuf.setLength(0);

            if (rawName.equals("field")) {
                value = null;
                if (file_state != IN_ELEMENT && file_state != IN_LAYER) {
                    System.out.println("XML Error: Not in element or layer when field begins.");
                    return;
                }
                if (me == null) {
                    System.out.println("XML Error: No element when field begins");
                    return;
                }
                value = atts.getValue("name");
                state = IN_FIELD;
                return;
            }

            if (rawName.equals("element")) {
                if (doc instanceof Szenario) {
                    return;
                }
                if (me != null) {
                    System.out.println("The last element hasn't been taken over and freed!");
                }
                if (atts != null) {
                    value = atts.getValue("class");
                } else {
                    value = "???";
                }
                if (value.equals("KonfOrgKombination")) {
                    value = "AufOrgKombination";
                }
                if (value.equals("Kante")) {
                    value = "Doppelkante";
                }

                String hash = atts.getValue("hash");

                if (doc instanceof Szenario) {
                    me = doc.getCollection().getMainGraphDocument().findElementCoded(hash);
                }
                if (me == null) {
                    try {
                        me = ModelConstants.getClassForName(value).newInstance();
                    } catch (Exception e) {
                        Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                        return;
                    }
                }

                String layer = atts.getValue("layer");
                if (layer != null) {
                    me.setLayer(Integer.parseInt(layer));
                }

                if (hash != null && !hash.equals("")) {
                    me.setHashString(hash);
                }
                if (me instanceof Kante) {
                    ec = new EdgeContainer((Kante) me, doc);
                } else if (me instanceof Konfiguration) {
                    ec = new KonfigurationContainer((Konfiguration) me, doc);
                } else if (ModelConstants.isInterLayerStartClass(me.getClass())) {
                    ec = new InterLayerConnectedNodeContainer((Knoten) me, doc);
                } else {
                    ec = new NodeContainer((Knoten) me, doc);
                }

                state = IN_ELEMENT;
                file_state = IN_ELEMENT;
                return;
            }

            if (rawName.equals("container")) {
                if (ec != null) {
                    System.out.println("The last container hasn't been taken over and freed!");
                }
                ec = null;
                String hash = null;
                if (atts != null) {
                    hash = atts.getValue("hash");
                }
                if (hash == null) {
                    return;
                }
                me = doc.getCollection().getMainGraphDocument().findElementCoded(hash);
                if (me == null) {
                    System.out.println("No Element for container " + hash);
                    return;
                }

                if (me.isUnique() && doc instanceof Szenario) {
                    me = null;
                    ec = null;
                } else if (me instanceof Konfiguration) {
                    ec = new KonfigurationContainer((Konfiguration) me, doc);
                } else if (ModelConstants.isInterLayerStartClass(me.getClass())) {
                    ec = new InterLayerConnectedNodeContainer((Knoten) me, doc);
                } else if (me instanceof Knoten) {
                    ec = new NodeContainer((Knoten) me, doc);
                }

                state = IN_CONTAINER;
                file_state = IN_CONTAINER;
                return;
            }

            if (rawName.equals("layout")) {
                layout = null;
                if (file_state == IN_MAPPING) {
                    if (atts != null) {
                        value = atts.getValue("class");
                    } else {
                        value = "???";
                    }
                    Class<? extends ModelElement> elementClass = ModelConstants.getClassForName(value);
                    if (!ModelElement.class.isAssignableFrom(elementClass)) {
                        System.out.println("XML Error: wrong class \"" + value + "\".");
                    } else if (Knoten.class.isAssignableFrom(elementClass)) {
                        if (doc == null) {
                            System.out.println("Haeh, Doc null????");
                        } else if (doc.getMapping() == null) {
                            System.out.println("Haeh, Mapping null?????");
                        } else {
                            layout = doc.getMapping().getStandardElementLayout(elementClass);
                        }
                    }
                } else if (file_state == IN_CONTAINER) {
                    if (ec == null) {
                        return;
                    }
                    layout = ec.getE3LGMLayout();
                } else if (file_state == IN_ELEMENT) {
                    if (me == null) {
                        return;
                    }
                    if (ec == null) {
                        layout = new GraphElementLayout();
                    } else {
                        layout = ec.getE3LGMLayout();
                    }
                } else if (file_state == IN_LAYER) {
                    layout = ec.get3LGMLayout();
                }
                if (layout == null) {
                }
                state = IN_LAYOUT;
                return;
            }

            if (rawName.equals("nelayout")) {
                layout = null;
                if (file_state == IN_CONTAINER) {
                    if (ec == null) {
                        return;
                    }
                    layout = ec.getNE3LGMLayout();
                } else if (file_state == IN_ELEMENT) {
                    if (me == null) {
                        return;
                    }
                    if (ec == null) {
                        layout = new GraphElementLayout();
                    } else {
                        layout = ec.getNE3LGMLayout();
                    }
                }
                if (layout == null) {
                }
                state = IN_LAYOUT;
                return;
            }

            if (rawName.equals("color")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                value = atts.getValue("name");
                red = 0;
                green = 0;
                blue = 0;
                alpha = 255;
                color_name = value;
                state = IN_COLOR;
                return;
            }

            if (ec == null) {
                me = null;
                file_state = IN_GRAPH;
                state = IN_GRAPH;
                return;
            }
            if (doc.getLayer(me.layerFor()).add(ec) != null) {
                if (me instanceof ABKonfiguration) {
                    doc.getCollection().addABKonf((KonfigurationContainer) ec);
                } else if (me instanceof DBKonfiguration) {
                    doc.getCollection().addDBKonf((KonfigurationContainer) ec);
                }
            }

            if (rawName.equals("line_style")) {
                return;
            }
            if (rawName.equals("x")) {
                return;
            }
            if (rawName.equals("y")) {
                return;
            }
            if (rawName.equals("width")) {
                return;
            }
            if (rawName.equals("height")) {
                return;
            }
            if (rawName.equals("red")) {
                return;
            }
            if (rawName.equals("green")) {
                return;
            }
            if (rawName.equals("blue")) {
                return;
            }
            if (rawName.equals("alpha")) {
                return;
            }
            if (rawName.equals("icon")) {
                return;
            }
            if (rawName.equals("font_family")) {
                return;
            }
            if (rawName.equals("font_style")) {
                return;
            }
            if (rawName.equals("font_size")) {
                return;
            }
            if (rawName.equals("valign")) {
                return;
            }
            if (rawName.equals("halign")) {
                return;
            }

            if (rawName.equals("graph")) {
                if (state != START) {
                    System.out.println("XML Error: Not in start when graph begins.");
                }
                file_state = IN_GRAPH;
                state = IN_GRAPH;
                return;
            }

            if (rawName.equals("description")) {
                if (state != IN_GRAPH) {
                    System.out.println("XML Error: Not in graph when description begins.");
                }
                file_state = IN_DESCRIPTION;
                state = IN_DESCRIPTION;
                return;
            }
            if (!read_mapping && rawName.equals("mapping")) {
                rawName = "--unknown--";
            }

            if (rawName.equals("mapping")) {
                if (file_state != IN_GRAPH) {
                    System.out.println("XML Error: Not in graph when mapping begins.");
                }
                file_state = IN_MAPPING;
                state = IN_MAPPING;
                return;
            }

            if (rawName.equals("layer")) {
                if (ec != null) {
                    System.out.println("The last element hasn't been taken over and freed!");
                }
                try {
                    ec = doc.getLayer(Integer.parseInt(atts.getValue("number")));
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    return;
                }
                file_state = IN_LAYER;
                state = IN_LAYER;
                return;
            }

            // now we have a new unknown element
            prev_state = state;
            state = UNKNOWN;
            unknown_depth = 1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String rawName) {
        try {
            // System.out.println (file_state);

            //			 System.out.println("endElement:");
            //			 System.out.println("namespaceURI: "+namespaceURI);
            //			 System.out.println("localName: "+localName);
            //			 System.out.println("rawName: "+rawName);

            if (state == UNKNOWN) {
                // System.out.println("Ignoring unknown sub-elements.");
                if (unknown_depth > 1) {
                    unknown_depth--;
                } else {
                    state = prev_state;
                    prev_state = START;
                    unknown_depth = 0;
                }
                return;
            }

            if (rawName.equals("element")) {
                if (file_state != IN_ELEMENT) {
                    return;
                }
                if (ec == null) {
                    me = null;
                    file_state = IN_GRAPH;
                    state = IN_GRAPH;
                    return;
                }
                if (doc.getLayer(me.layerFor()).add(ec) != null) {
                    if (me instanceof ABKonfiguration) {
                        doc.getCollection().addABKonf((KonfigurationContainer) ec);
                    } else if (me instanceof DBKonfiguration) {
                        doc.getCollection().addDBKonf((KonfigurationContainer) ec);
                    }
                }

                me = null;
                ec = null;
                file_state = IN_GRAPH;
                state = IN_GRAPH;
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("field")) {
                if (state != IN_FIELD) {
                    return;
                }
                if (me == null) {
                    return;
                }
                if (value == null) {
                    return;
                }
                me.putXMLFieldString(value, elementTextBuf.toString());
                state = file_state;
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("container")) {
                if (file_state != IN_CONTAINER) {
                    return;
                }
                if (ec == null || me == null) {
                    file_state = IN_GRAPH;
                    state = IN_GRAPH;
                    ec = null;
                    me = null;
                    return;
                }

                boolean add = true;
                if (ec instanceof EdgeContainer) {
                    Kante k = (Kante) me;
                    if (k.getStart().isUnique() || k.getEnd().isUnique()) {
                        add = false;
                    }
                }
                if (add) {
                    ec.refreshText();
                    doc.getLayer(ec.getElement().layerFor()).add(ec);
                }

                ec = null;
                me = null;
                file_state = IN_GRAPH;
                state = IN_GRAPH;
                return;
            }

            if (rawName.equals("layout")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                state = file_state;
                layout = null;
                return;
            }

            if (rawName.equals("nelayout")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                state = file_state;
                layout = null;
                return;
            }

            if (rawName.equals("red")) {
                if (state != IN_COLOR) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    red = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for red: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("green")) {
                if (state != IN_COLOR) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    green = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for green: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("blue")) {
                if (state != IN_COLOR) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    blue = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for blue: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("alpha")) {
                if (state != IN_COLOR) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    alpha = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for alpha: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("color")) {
                if (state != IN_COLOR) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                if (color_name.equals("bg_color")) {
                    layout.bg_color = new Color(red, green, blue, alpha);
                } else if (color_name.equals("fg_color")) {
                    layout.fg_color = new Color(red, green, blue, alpha);
                } else if (color_name.equals("border_color")) {
                    layout.border_color = new Color(red, green, blue, alpha);
                }
                state = IN_LAYOUT;

                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("line_style")) {

                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    layout.line_style = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for line_style: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("x")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    layout.x = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for x: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("y")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    layout.y = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for y: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("width")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    layout.width = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for width: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("height")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    layout.height = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for height: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("icon")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    String iconPath = elementTextBuf.toString().trim();
                    layout.icon = iconPath;
                    if (loadIcons) {
                        iconPath = doc.getCollection().loadIcon(new File(iconPath));
                    }
                    ((NodeContainer) ec).setIcon(iconPath, doc.getCollection().getIconTable());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for icon: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("font_family")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                String name = elementTextBuf.toString().trim();
                if (layout.getFont() == null) {
                    layout.setFont(new Font(name, GraphElementLayout.STANDARD_FONT_STYLE, GraphElementLayout.STANDARD_FONT_SIZE));
                } else {
                    layout.setFont(new Font(name, layout.getFont().getStyle(), layout.getFont().getSize()));
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("font_style")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                int style = 0;
                for (int i = 0; i < GraphElementLayout.FONT_STYLE_NAMES.length; i++) {
                    if (elementTextBuf.toString().trim().equals(GraphElementLayout.FONT_STYLE_NAMES[i])) {
                        style = i;
                    }
                }
                if (layout.getFont() == null) {
                    layout.setFont(new Font(GraphElementLayout.STANDARD_FONT_NAME, style, GraphElementLayout.STANDARD_FONT_SIZE));
                } else {
                    layout.setFont(new Font(layout.getFont().getName(), style, layout.getFont().getSize()));
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("font_size")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                int size = Integer.parseInt(elementTextBuf.toString());
                if (layout.getFont() == null) {
                    layout.setFont(new Font(GraphElementLayout.STANDARD_FONT_NAME, GraphElementLayout.STANDARD_FONT_STYLE, size));
                } else {
                    layout.setFont(new Font(layout.getFont().getName(), layout.getFont().getStyle(), size));
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("valign")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    layout.valign = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for valign: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("halign")) {
                if (state != IN_LAYOUT) {
                    return;
                }
                if (layout == null) {
                    return;
                }
                try {
                    layout.halign = Integer.parseInt(elementTextBuf.toString());
                } catch (Exception e) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
                    System.out.println("XML Error: Incorrect value for halign: " + elementTextBuf.toString());
                    return;
                }
                elementTextBuf.setLength(0);
                return;
            }

            if (rawName.equals("graph")) {
                state = START;
                return;
            }

            if (rawName.equals("description")) {
                doc.setDescription(elementTextBuf.toString());
                state = IN_GRAPH;
                file_state = IN_GRAPH;
                return;
            }

            if (rawName.equals("mapping")) {
                if (file_state != IN_MAPPING) {
                    System.out.println("XML Error: Not in mapping when finishing mapping.");
                }
                file_state = IN_GRAPH;
                state = IN_GRAPH;
                return;
            }

            if (rawName.equals("layer")) {
                file_state = IN_GRAPH;
                state = IN_GRAPH;
                ec = null;
                me = null;
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        elementTextBuf.append(ch, start, length);
    }

    /**
     * @param inputBuffer
     * @param _doc
     * @param imageTable
     * @param loadIcons
     * @param _select
     * @return
     */
    public LGMGraphDocument process(final StringReader inputBuffer, final LGMGraphDocument _doc, final Hashtable<String, byte[]> imageTable, final boolean loadIcons, final boolean _select) {
        return process(inputBuffer, _doc, imageTable, loadIcons);
    }

    /**
     * @param inputBuffer
     * @param _doc
     * @param imageTable
     * @param loadIcons
     * @return
     */
    public LGMGraphDocument process(final StringReader inputBuffer, final LGMGraphDocument _doc, final Hashtable<String, byte[]> imageTable, final boolean loadIcons) {
        this.loadIcons = loadIcons;
        iconTable = imageTable;

        if (inputBuffer == null) {
            return _doc;
        }
        if (_doc == null) {
            return null;
        }
        doc = _doc;
        read_mapping = true;

        InputSource src = new InputSource();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(inputBuffer);
            src.setCharacterStream(reader);
            parser.parse(src, this);
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            System.err.println("Parser Exception:\n" + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return doc;
    }

    /**
     * @param raf
     * @param _doc
     * @param iconTable
     * @param loadIcons
     * @param select
     * @return
     */
    public LGMGraphDocument process(final RandomAccessFile raf, final LGMGraphDocument _doc, final Hashtable<String, byte[]> iconTable, final boolean loadIcons, final boolean select) {
        StringBuilder strbuf = new StringBuilder();
        try {
            raf.seek(0);
            while (raf.getFilePointer() < raf.length()) {
                strbuf.append(raf.readLine());
                strbuf.append('\n');
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            System.err.println("Parser Exception:\n" + e.getMessage());
            e.printStackTrace();
        }
        doc = process(new StringReader(strbuf.toString()), _doc, iconTable, loadIcons, select);
        return doc;
    }

    /**
     * @param fileStream
     * @return
     */
    public static boolean isXMLFile(final FileInputStream fileStream) {
        String line = "";
        try {
            fileStream.getChannel().position(0);
            byte[] chars = new byte["<graph>".length()];
            fileStream.read(chars);
            line = new String(chars);
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            return false;
        }
        if (line.startsWith("<?xml") || line.startsWith("<graph>")) {
            return true;
        }
        return false;
    }

    public static GraphDocument loadString(final StringReader strBuffer, final LGMGraphDocument _doc, final Hashtable<String, byte[]> ImageTable, final boolean loadIcons) {
        LgmXMLParser p = new LgmXMLParser();

        GraphDocument retVal = p.process(strBuffer, _doc, ImageTable, loadIcons, true);

        try {
            p.finalize();
        } catch (Throwable e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
        }
        p = null;
        System.gc();
        return retVal;
    }

    public byte[] getIcon(final Object o) {
        byte[] image = iconTable.get(o);
        return image;
    }

    public static boolean isXMLString(final StringBuilder str) {
        return str.indexOf("<?xml") == 0 || str.indexOf("<graph>") == 0;
    }

    public static LGMGraphDocument loadFile(final RandomAccessFile raf, final LGMGraphDocument _doc, final Hashtable<String, byte[]> iconTable, final boolean loadIcons, final boolean select) {
        LgmXMLParser p = new LgmXMLParser();
        return p.process(raf, _doc, iconTable, loadIcons, select);
    }
}
