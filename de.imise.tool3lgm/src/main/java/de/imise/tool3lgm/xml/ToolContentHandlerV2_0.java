/*
 * Created on 25.11.2003
 */
package de.imise.tool3lgm.xml;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.ABKonfiguration;
import de.imise.tool3lgm.graphtools.elements.node.DBKonfiguration;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.KonfigurationContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.Mapping;
import de.imise.tool3lgm.log.Log;

/**
 * Die Variablen sind auf protected Gesetzt, damit man einen neuen ContentHandler
 * von dieser Klasse ableiten kann aber trotzdem noch Zugriff auf alle nötigen
 * Werte hat. Ich denke, bei kleinen Änderungen (hinzukommen oder wegfallen einzelnener
 * Felder im Dokument) muß man keinen ganz neuen ContentHandler schreiben sondern muß
 * nur einen abgeleiteten von diesem bilden. Ich würde aber empfehlen von Zeit zu Zeit
 * einen völlig neuen ContentHandler zu schreiben.
 * erkennt Modell mit 3lgm2_v2_0.dtd Version 2.0
 * 
 * @author Thomas Rudert
 */
public class ToolContentHandlerV2_0 implements ContentHandler {

    /** gänderte Hashcodes (bei copyAndPaste) Schlüssel ist alter HashString, Wert ist neuer HashString */
    protected HashMap<String, String> hashCodes;

    /** Kanten deren hashStrings (start, end) aufgelöst werden müssen (bei copyAndPaste) */
    protected ArrayList<Kante> kanten;

    /** KantenContainer deren computeBorderPoints()-Methode aufgerufen werden muss */
    protected ArrayList<EdgeContainer> kantenContainer;

    /** GDCollection in die die Element geschrieben werden */
    protected GDCollection collection;

    /** GraphDokument der GDCollection */
    protected GraphDocument doc = null;

    /** aktuelles Szenario */
    protected GraphDocument szenario = null;

    /** aktueller LayerContainer */
    protected LayerContainer layer = null;

    /** aktuelles GraphElementLayout */
    protected GraphElementLayout layout = null;

    /** ElementKlasse auf die sich das GraphElementLayout bezieht (StandardLayout (Mapping) */
    protected Class<? extends ModelElement> classType = null;

    /** aktuelles ModellElement */
    protected ModelElement element = null;

    /** aktueller ElementContainer */
    protected ElementContainer container = null;

    /** definiert den Namen eines field-objektes */
    protected String field = null;

    /** Farbeangabe zu einem GraphElementLayout */
    protected Color color = null;

    /** definiert die gültigkeit Farbe (fg_color, bg_color, border_color) */
    protected String colorString = null;

    /** String der in der characters Methode ausgelesen wird (Werte eines Tags) */
    protected StringBuilder elementValue = new StringBuilder();

    /** HashString eines Bitmaps */
    protected String iconKey = null;

    /**
     * ArrayList mit allen Containern die ein Icon besitzen; da die Icons erst zu letzt eingelesen werden, wird den Containern zuerst nur der
     * HashString des Icons mitgeteilt. Nach dem einlesen der Icons müssen diese Container noch das eigentliche Icon aus der Hashmap der Collection
     * laden. Das passiert in der Methode setIcon();
     */
    protected ArrayList<NodeContainer> containerWithIcon = new ArrayList<NodeContainer>();

    /**
     * option beim Kopieren<br>
     * 
     * @see ModelElement.avoidDuplicates()
     */
    protected boolean avoidDuplicates = false;

    /**
	 * 
	 */
    public ToolContentHandlerV2_0(final GDCollection coll) {
        super();
        collection = coll;
    }

    @Override
    public void setDocumentLocator(final Locator arg0) {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    /**
     * ruft Methoden doc._relinkTraces(), doc._duplicateAufOrgs(), doc._duplicateABKonfs() auf und setzt doc, container, collection, elementValue auf
     * null;
     * in der naechsten Version (V1_2) sollte diese Funktion ueberschrieben werden, damit die 3 Funktionen nicht mehr aufgerufen werden, da diese nur
     * Fehler korrigert haben, die dann nicht mehr auftreten duerften
     * 
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void endDocument() throws SAXException {
        doc._cleanContainers();
        doc._removeMultipleTraces();

        if (copyAndPaste > 0) {
            for (EdgeContainer kc : kantenContainer) {
                kc.computeBorderPoints();
            }

        } else {
            for (int i = 0; i < collection.getNumberOfSzenarios(); i++) {
                collection.getSzenario(i).initKnotContainers();
                collection.getSzenario(i).initTraceContainers();
                //				collection.getSzenario(i).refreshSpecialInfoTargets();
            }
        }
        doc._refreshSubordinatedElementsInSzenarios();

        doc = null;
        containerWithIcon = null;
        collection = null;
        elementValue = null;
    }

    @Override
    public void startPrefixMapping(final String arg0, final String arg1) throws SAXException {
    }

    @Override
    public void endPrefixMapping(final String arg0) throws SAXException {
    }

    int copyAndPaste = 0;

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            elementValue.setLength(0);

            //		System.out.println("start: " + qName + (atts.getValue("hash") != null ? " hash="+atts.getValue("hash") : "") + (atts.getValue("name") != null ? " name="+atts.getValue("name") : ""));		

            if (qName.equals("field")) {
                field = atts.getValue("name");

            } else if (qName.equals("element")) {
                Class<? extends ModelElement> elementClass = ModelConstants.getClassForName(atts.getValue("class"));
                if (elementClass == null) {
                    //				throw new SAXException("Klasse für Element nicht gefunden!\n Name=" + qName + "\n UserField=" + attsToString(atts));
                    return;
                }
                if (avoidDuplicates) {
                    if ((element = doc.findElementCoded(atts.getValue("hash"))) == null) {
                        element = ModelConstants.createElement(elementClass, false);
                    }
                } else {
                    element = ModelConstants.createElement(elementClass, false);
                }

                if (element == null) {
                    XMLInformationMessenger.showElementNoLongerSupportedMessage(collection, elementClass, true);
                    //				ToolXMLParser.addDeprecatedElementHashes(atts.getValue("hash"));
                } else {
                    if (copyAndPaste > 0) {

                        hashCodes.put(atts.getValue("hash"), element.getHashString());
                        if (element instanceof Kante) {
                            kanten.add((Kante) element);
                        }
                    } else {
                        element.setHashString(atts.getValue("hash"));
                    }
                }
            } else if (qName.equals("avoidDuplicates")) {
                avoidDuplicates = true;

            } else if (qName.equals("container")) {
                if (copyAndPaste > 0) {
                    element = doc.findElementCoded(hashCodes.get(atts.getValue("hash")).toString());
                } else {
                    element = doc.findElementCoded(atts.getValue("hash"));
                }

                //			if (element == null)
                //				throw new SAXException("ModelObject für Container nicht gefunden!\n Name=" + qName + "\n UserField=" + attsToString(atts));

                if (element != null) {
                    container = element.createContainer(szenario);

                    if (container == null) {
                        throw new SAXException("Container für ModelObject nicht gefunden!\n Name=" + qName + "\n UserField=" + atts.toString());
                    }

                    if (copyAndPaste > 0 && container instanceof EdgeContainer) {
                        kantenContainer.add((EdgeContainer) container);
                    }
                }
                element = null;

            } else if (qName.equals("expanded")) {

            } else if (qName.equals("visible")) {

            } else if (qName.equals("x")) {

            } else if (qName.equals("y")) {

            } else if (qName.equals("width")) {

            } else if (qName.equals("height")) {

            } else if (qName.equals("red")) {

            } else if (qName.equals("green")) {

            } else if (qName.equals("blue")) {

            } else if (qName.equals("alpha")) {

            } else if (qName.equals("color")) {
                colorString = atts.getValue("name");
                color = new Color(0, 0, 0, 255);

            } else if (qName.equals("font_family")) {

            } else if (qName.equals("font_size")) {

            } else if (qName.equals("font_style")) {

            } else if (qName.equals("line_style")) {

            } else if (qName.equals("form")) {

            } else if (qName.equals("icon")) {

            } else if (qName.equals("valign")) {

            } else if (qName.equals("halign")) {

            } else if (qName.equals("layout")) {
                if (container == null && element != null) {
                    container = element.getContainer(doc);
                }

                if (container != null) {
                    layout = container.getE3LGMLayout();
                    if (layout == null) {
                        layout = new GraphElementLayout();
                        container.setE3LGMLayout(layout);
                    }
                } else if (layer != null) {
                    layout = layer.get3LGMLayout();
                    if (layout == null) {
                        layout = new GraphElementLayout();
                        container.set3LGMLayout(layout);
                    }
                } else if ((classType = ModelConstants.getClassForName(atts.getValue("class"))) != null) {
                    layout = szenario.getMapping().getStandardElementLayout(classType);
                }
            } else if (qName.equals("nelayout")) {
                if (container != null) {
                    layout = container.getNE3LGMLayout();
                    if (layout == null) {
                        layout = new GraphElementLayout();
                        container.setNE3LGMLayout(layout);
                    }
                }
            } else if (qName.equals("layer")) {
                layer = szenario.getLayer(Integer.parseInt(atts.getValue("number")));

            } else if (qName.equals("szenario")) {
                Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelReadSzenario") + atts.getValue("titel") + " ...");

                if (copyAndPaste <= 0) {
                    szenario = collection.createSzenario(atts.getValue("titel"), false, "", atts.getValue("hash"), false);
                }

            } else if (qName.equals("description")) {

            } else if (qName.equals("mapping")) {
                szenario.setMapping(new Mapping());

            } else if (qName.equals("bitmap")) {
                if (atts.getValue("type").equals("gif/base64")) {
                    iconKey = atts.getValue("hash");
                }

            } else if (qName.equals("images")) {
                Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelReadIcons"));

            } else if (qName.equals("header")) {

            } else if (qName.equals("title")) {

            } else if (qName.equals("version")) {

            } else if (qName.equals("modell_3lgm_2")) {
                doc = collection.getMainGraphDocument();
                collection.setCopyAndPaste(0);
                copyAndPaste = collection.getCopyAndPaste();

            } else if (qName.equals("tool3lgm_clipboard")) {
                doc = Tool3lgm.tool.getSelectedDoc().getCollection().getMainGraphDocument();
                copyAndPaste = collection.getCopyAndPaste();
                collection.setCopyAndPaste(copyAndPaste + 1);
                copyAndPaste = collection.getCopyAndPaste();
                szenario = collection.getSelectedDoc();
                szenario.clearSelection();
                hashCodes = new HashMap<String, String>();
                kanten = new ArrayList<Kante>();
                kantenContainer = new ArrayList<EdgeContainer>();

            } else if (qName.equals("objects")) {
                Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelReadElements"));

            }
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            e.printStackTrace();
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        //		System.out.println("end: " + qName);
        try {
            if (qName.equals("field")) {
                if (element != null && !element.putXMLFieldString(field, elementValue.toString())) {
                    throw new SAXException("ModelElement konnte field nicht verarbeiten!\n ModelElement=" + element.getHashString() + "\n field=" + field + "\n Wert=" + elementValue);
                }

                field = null;

            } else if (qName.equals("element")) {
                if (element != null && (!avoidDuplicates || element.getContainer(doc) == null)) {
                    container = element.createContainer(doc);
                    if (element.layerFor() < 0 || element.layerFor() >= ModelConstants.LAYERS.length) {
                        throw new SAXException("ModelElement hat ungueltige Ebenenangabe! hash=" + element.getHashString() + "layerFor=" + element.layerFor());
                    }
                    if (doc.getLayer(element.layerFor()).add(container) != null) {
                        if (element instanceof ABKonfiguration) {
                            collection.addABKonf((KonfigurationContainer) container);
                        } else if (element instanceof DBKonfiguration) {
                            collection.addDBKonf((KonfigurationContainer) container);
                        }
                    }
                }
                element = null;
                container = null;

            } else if (qName.equals("avoidDuplicates")) {
                avoidDuplicates = false;

            } else if (qName.equals("container")) {
                if (container != null && (copyAndPaste > 0 || !szenario.equals(doc))) {
                    szenario.getLayer(container.getElement().layerFor()).add(container);
                    //				container.refreshText();
                }

                if (copyAndPaste > 0) {
                    szenario.addSimpleToSelection(container);
                }

                container = null;

            } else if (qName.equals("expanded")) {
                if (container == null) {
                    return;
                }
                container.setExpanded(Boolean.valueOf(elementValue.toString()).booleanValue());

            } else if (qName.equals("visible")) {
                if (container == null) {
                    return;
                }
                container.setVisible(Boolean.valueOf(elementValue.toString()).booleanValue());

            } else if (qName.equals("x")) {
                if (layout == null) {
                    return;
                }
                layout.x = Integer.parseInt(elementValue.toString());

                if (copyAndPaste > 0) {
                    layout.x = layout.x + 10 * copyAndPaste;
                }

            } else if (qName.equals("y")) {
                if (layout == null) {
                    return;
                }
                layout.y = Integer.parseInt(elementValue.toString());

                if (copyAndPaste > 0) {
                    layout.y = layout.y + 10 * copyAndPaste;
                }

            } else if (qName.equals("width")) {
                if (layout == null) {
                    return;
                }
                layout.width = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("height")) {
                if (layout == null) {
                    return;
                }
                layout.height = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("red")) {
                color = new Color(Integer.parseInt(elementValue.toString()), color.getGreen(), color.getBlue(), color.getAlpha());

            } else if (qName.equals("green")) {
                color = new Color(color.getRed(), Integer.parseInt(elementValue.toString()), color.getBlue(), color.getAlpha());

            } else if (qName.equals("blue")) {
                color = new Color(color.getRed(), color.getGreen(), Integer.parseInt(elementValue.toString()), color.getAlpha());

            } else if (qName.equals("alpha")) {
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), Integer.parseInt(elementValue.toString()));

            } else if (qName.equals("color")) {
                if (layout == null) {
                    return;
                }
                if (colorString.equals("bg_color")) {
                    layout.bg_color = color;
                } else if (colorString.equals("fg_color")) {
                    layout.fg_color = color;
                } else if (colorString.equals("border_color")) {
                    layout.border_color = color;
                } else {
                    System.out.println("Flasche Color-Angabe");
                }
                color = null;

            } else if (qName.equals("form")) {
                layout.form = GraphElementLayout.SHAPE.values()[Integer.parseInt(elementValue.toString())];

            } else if (qName.equals("font_family")) {
                if (layout == null) {
                    return;
                }
                String name = elementValue.toString().trim();
                if (layout.getFont() == null) {
                    layout.setFont(new Font(name, GraphElementLayout.STANDARD_FONT_STYLE, GraphElementLayout.STANDARD_FONT_SIZE));
                } else {
                    layout.setFont(new Font(name, layout.getFont().getStyle(), layout.getFont().getSize()));
                }

            } else if (qName.equals("font_size")) {
                if (layout == null) {
                    return;
                }
                int size = Integer.parseInt(elementValue.toString());
                if (layout.getFont() == null) {
                    layout.setFont(new Font(GraphElementLayout.STANDARD_FONT_NAME, GraphElementLayout.STANDARD_FONT_STYLE, size));
                } else {
                    layout.setFont(new Font(layout.getFont().getName(), layout.getFont().getStyle(), size));
                }

            } else if (qName.equals("font_style")) {
                if (layout == null) {
                    return;
                }
                int style = Integer.parseInt(elementValue.toString());
                if (layout.getFont() == null) {
                    layout.setFont(new Font(GraphElementLayout.STANDARD_FONT_NAME, style, GraphElementLayout.STANDARD_FONT_SIZE));
                } else {
                    layout.setFont(new Font(layout.getFont().getName(), style, layout.getFont().getSize()));
                }

            } else if (qName.equals("line_style")) {
                if (layout == null) {
                    return;
                }
                layout.line_style = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("line_thickness")) {
                if (layout == null) {
                    return;
                }
                layout.line_thickness = Integer.parseInt(elementValue.toString());

            } else if (qName.equals("valign")) {
                if (layout == null) {
                    return;
                }
                if (layout != null) {
                    layout.valign = Integer.parseInt(elementValue.toString());
                }

            } else if (qName.equals("halign")) {
                if (layout == null) {
                    return;
                }
                if (layout != null) {
                    layout.halign = Integer.parseInt(elementValue.toString());
                }

            } else if (qName.equals("layout")) {
                layout = null;
                classType = null;

            } else if (qName.equals("nelayout")) {
                layout = null;
                classType = null;

            } else if (qName.equals("icon")) {
                layout.icon = elementValue.toString();
                containerWithIcon.add((NodeContainer) container);

            } else if (qName.equals("layer")) {
                layer = null;

            } else if (qName.equals("szenario")) {
                szenario = null;

            } else if (qName.equals("mapping")) {

            } else if (qName.equals("description")) {
                if (szenario != null) {
                    szenario.setDescription(elementValue.toString());
                } else if (doc != null) {
                    doc.setDescription(elementValue.toString());
                }

            } else if (qName.equals("bitmap")) {
                if (iconKey != null && !collection.getIconTable().contains(iconKey)) {
                    collection.getIconTable().put(iconKey, Base64.decode(elementValue.toString()));
                }

                iconKey = null;

            } else if (qName.equals("images")) {

            } else if (qName.equals("header")) {

            } else if (qName.equals("title")) {

            } else if (qName.equals("version")) {
                collection.setFileVersion(elementValue.toString());

            } else if (qName.equals("modell_3lgm_2")) {
                de.imise.tool3lgm.Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelReferenceIcons"));

                /* Icons in den Container einlesen */
                for (NodeContainer kc : containerWithIcon) {
                    kc.setIcon();
                }

            } else if (qName.equals("tool3lgm_clipboard")) {
                /* Icons in den Container einlesen */
                for (NodeContainer kc : containerWithIcon) {
                    kc.setIcon();
                }

            } else if (qName.equals("objects")) {

                Tool3lgm.tool.setProgressDialogStatusLabel(Tool3lgmConstants.getResString("labelConnectTraces"));

                /* die HashStrings für das Start- bzw. End-Objekt einer Kante auflösen und die wirklichen Knoten setzten */
                if (copyAndPaste > 0) {
                    Kante kante;
                    for (int i = 0; i < kanten.size(); i++) {
                        kante = kanten.get(i);
                        kante.putXMLFieldString("start", hashCodes.get(kante.getStartHash()));
                        kante.putXMLFieldString("end", hashCodes.get(kante.getEndHash()));
                        kante.decodeHashStrings(doc);
                    }
                } else {
                    for (int i = 0; i < ModelConstants.LAYERS.length; i++) {
                        for (EdgeContainer kc : doc.getLayer(ModelConstants.LAYERS[i]).getKanten()) {
                            kc.getEdge().decodeHashStrings(doc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
            e.printStackTrace();
        }
    }

    @Override
    public void characters(final char[] arg0, final int arg1, final int arg2) throws SAXException {
        elementValue.append(String.valueOf(arg0, arg1, arg2));
    }

    @Override
    public void ignorableWhitespace(final char[] arg0, final int arg1, final int arg2) throws SAXException {
    }

    @Override
    public void processingInstruction(final String arg0, final String arg1) throws SAXException {

    }

    @Override
    public void skippedEntity(final String arg0) throws SAXException {
    }

}
