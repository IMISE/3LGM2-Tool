/*
 * Created on 25.11.2003
 */
package de.imise.tool3lgm.xml;

import static de.imise.tool3lgm.graphtools.elements.ModelConstants.createElement;
import static de.imise.tool3lgm.graphtools.elements.ModelConstants.getClassForName;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.Edge;
import de.imise.tool3lgm.graphtools.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionFileHandler;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.Mapping;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Prozess;

/**
 * Die Variablen sind auf protected Gesetzt, damit man einen neuen
 * ContentHandler von dieser Klasse ableiten kann aber trotzdem noch Zugriff auf
 * alle nötigen Werte hat. Ich denke, bei kleinen Änderungen (hinzukommen oder
 * wegfallen einzelnener Felder im Dokument) muß man keinen ganz neuen
 * ContentHandler schreiben sondern muß nur einen abgeleiteten von diesem
 * bilden. Ich würde aber empfehlen von Zeit zu Zeit einen völlig neuen
 * ContentHandler zu schreiben.
 * erkennt Modell mit 3lgm2_v3_0.dtd Version 3.0
 *
 * @author Thomas Rudert
 */
public class ToolContentHandlerV3_0 implements ContentHandler {

    private static String lastCopyFileTimeStamp = "";

    private boolean paste = false;

    /** Alle über Copy&Paste eingefügten Elemente. Diese werden am Ende selected(true) gesetzt */
    private List<ElementContainer> pastedElements;

    private final Map<String, BendpointContainer> hashToMainDocBendpointContainer = new HashMap<>();

    private Map<String, BendpointContainer> hashToSzenarioBendpointContainer;

    /**
     * Faktor, um den die Position von per Paste eingefügten Elementen in x und y Richtung nach unten
     * verschoben wird. Mehrmaliges Hintereinandereinfügen erhöht diesen Faktor, so dass die kopierten
     * Elemente immer schräg unter den originalen bzw. zuletzt eingefügten Elementen landen.
     */
    private int copyAndPastePositionShift = 0;

    private final boolean isCopyAndPaste() {
        return copyAndPastePositionShift > 0;
    }

    /**
     * gänderte Hashcodes (bei copyAndPaste) Schlüssel ist alter HashString, Wert ist neuer HashString
     */
    private Map<String, String> oldToNewHashString;

    /** Alle Kanten. Am Ende müssem deren hashStrings (start, end) aufgelöst werden. */
    private final List<Edge> edges = new ArrayList<>();

    /** GDCollection in die die Element geschrieben werden */
    protected GDCollection collection;

    /** Haupt-GraphDokument der GDCollection */
    protected GraphDocument doc = null;

    /** Die Definition der UserFields der GDCollection */
    protected UserFieldDefinitions userFieldDefinitions;

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

    /** wird gesetzt, wenn der aktuelle Container gelont werden soll */
    private ElementContainer tmp_container = null;

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
     * ArrayList mit allen Containern die ein Icon besitzen; da die Icons erst
     * zu letzt eingelesen werden, wird den Containern zuerst nur der HashString
     * des Icons mitgeteilt. Nach dem einlesen der Icons müssen diese Container
     * noch das eigentliche Icon aus der Hashmap der Collection laden. Das
     * passiert in der Methode setIcon();
     */
    protected List<NodeContainer> containerWithIcon = new ArrayList<>();

    /**
     * aktuelles userField bei der Definition der benutzerdefinierten
     * Eigenschaftsfelder
     */
    protected UserField userField;

    /**
     * option beim Kopieren <br>
     *
     * @see ModelElement.avoidDuplicates()
     */
    protected boolean avoidDuplicates = false;

    /**
     * @param coll
     */
    public ToolContentHandlerV3_0(final GDCollection coll) {
        this(coll, false);
    }

    /**
     * @param coll
     * @param paste
     */
    public ToolContentHandlerV3_0(final GDCollection coll, final boolean paste) {
        super();
        collection = coll;
        this.paste = paste;
        if (paste) {
            pastedElements = new ArrayList<>();
        }
        doc = collection.getMainGraphDocument();
        szenario = collection.getSelectedDoc();
        userFieldDefinitions = coll.getUserFieldDefinitions();
    }

    @Override
    public void setDocumentLocator(final Locator arg0) {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    /**
     * ruft Methoden doc._relinkTraces(), doc._duplicateAufOrgs(),
     * doc._duplicateABKonfs() auf und setzt doc, container, collection,
     * elementValue auf null; in der naechsten Version (V1_2) sollte diese
     * Funktion ueberschrieben werden, damit die 3 Funktionen nicht mehr
     * aufgerufen werden, da diese nur Fehler korrigert haben, die dann nicht
     * mehr auftreten duerften
     *
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @SuppressWarnings("deprecation")
    @Override
    public void endDocument() throws SAXException {
        doc._cleanContainers();
        doc._removeMultipleTraces();

        for (Szenario szen : collection.getSzenarios()) {
            szen.initKnotContainers();
            szen.initTraceContainers();
            //			collection.getSzenario(i).refreshSpecialInfoTargets();
        }
        doc._refreshSubordinatedElementsInSzenarios();
        doc.deselectAll(true);
        if (paste) {
            for (ElementContainer ec : pastedElements) {
                doc.addToSelection(ec, 0);
            }
        }

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

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        try {
            elementValue.setLength(0);

            if (qName.equals("field")) {
                field = atts.getValue("name");

            } else if (qName.equals("userField")) {
                field = atts.getValue("hash");

            } else if (qName.equals("element")) {
                Class<? extends ModelElement> elementClass = null;
                try {
                    String className = atts.getValue("class");
                    elementClass = getClassForName(className);
                } catch (Exception e) {
                    throw new SAXException("Klasse für Element nicht gefunden!\n Name=" + qName + "\n UserField=" + attsToString(atts));
                }

                if (avoidDuplicates) {
                    element = doc.findElementCoded(atts.getValue("hash"));
                    if (element == null) {
                        element = createElement(elementClass, false);
                    }
                } else {
                    element = createElement(elementClass, false);
                }

                if (element != null) {
                    String hashString = atts.getValue("hash");
                    if (isCopyAndPaste()) {
                        String newHashString = element.getHashString();
                        oldToNewHashString.put(hashString, newHashString);
                    } else {
                        element.setHashString(hashString);
                    }

                    if (element instanceof Edge) {
                        edges.add((Edge) element);
                    }
                }
            } else if (qName.equals("container")) {
                String hashString = atts.getValue("hash");
                if (isCopyAndPaste()) {
                    element = doc.findElementCoded(oldToNewHashString.get(hashString).toString());
                } else {
                    element = doc.findElementCoded(hashString);
                }
                if (element == null) {
                    BendpointContainer bendpointContainer = hashToMainDocBendpointContainer.get(hashString);
                    if (bendpointContainer != null) {
                        element = bendpointContainer.getElement();
                    }
                }

                //				if (element == null)
                //					throw new SAXException("ModelObject für Container nicht gefunden!\n Name=" + qName + "\n UserField=" + attsToString(atts));

                if (element != null) {
                    container = element.getContainer(szenario);
                    if (container == null) {
                        container = element.createContainer(szenario);
                    } else {
                        tmp_container = container;
                        container = container.clone(false, szenario);
                    }

                    if (container == null) {
                        throw new SAXException("Could not find Container for ModelObject!\n Name=" + qName + "\n UserField=" + atts.toString());
                    }
                }
                element = null;

                //            } else if (qName.equals("expanded")) {
                //
                //            } else if (qName.equals("visible")) {
                //
                //            } else if (qName.equals("x")) {
                //
                //            } else if (qName.equals("y")) {
                //
                //            } else if (qName.equals("width")) {
                //
                //            } else if (qName.equals("height")) {
                //
                //            } else if (qName.equals("red")) {
                //
                //            } else if (qName.equals("green")) {
                //
                //            } else if (qName.equals("blue")) {
                //
                //            } else if (qName.equals("alpha")) {
                //
            } else if (qName.equals("color")) {
                colorString = atts.getValue("name");
                color = new Color(0, 0, 0, 255);
                //
                //            } else if (qName.equals("font_family")) {
                //
                //            } else if (qName.equals("font_size")) {
                //
                //            } else if (qName.equals("font_style")) {
                //
                //            } else if (qName.equals("line_style")) {
                //
                //            } else if (qName.equals("form")) {
                //
                //            } else if (qName.equals("icon")) {
                //
                //            } else if (qName.equals("valign")) {
                //
                //            } else if (qName.equals("halign")) {
                //
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
                String value = atts.getValue("number");
                //wieder aktivieren, wenn die ToolContentVersion 3.6 aktiviert wird
                //                int layerIndex = Integer.parseInt(value) / 2; //die alten Layer gingen von 0 bis 4 (mit den 2 Zwischenlayern, die es jetzt nicht mehr gibt)
                int layerIndex = Integer.parseInt(value);
                layer = szenario.getLayer(layerIndex);

            } else if (qName.equals("szenario")) {
                Static.setProgressDialogStatusLabel("labelReadSzenario", atts.getValue("titel") + " ...");

                if (!isCopyAndPaste()) {
                    szenario = collection.createSzenario(atts.getValue("titel"), false, "", atts.getValue("hash"), false);
                }
                hashToSzenarioBendpointContainer = new HashMap<>();

                //            } else if (qName.equals("description")) {
                //
            } else if (qName.equals("mapping")) {
                szenario.setMapping(new Mapping());

            } else if (qName.equals("bitmap")) {
                if (atts.getValue("type").equals("gif/base64")) {
                    iconKey = atts.getValue("hash");
                }

            } else if (qName.equals("images")) {
                Static.setProgressDialogStatusLabel("labelReadIcons");

                //            } else if (qName.equals("header")) {
                //
                //            } else if (qName.equals("title")) {
                //
                //            } else if (qName.equals("version")) {
                //
            } else if (qName.equals("avoidDuplicates")) {
                avoidDuplicates = true;

                //            } else if (qName.equals("userFieldDefinitions")) {
                //
            } else if (qName.equals("userFieldDef")) {
                String elementClass = atts.getValue("elementClass");
                //bei Modellvariablen ist die Elementclass null
                if (elementClass == null) {
                    userField = new UserField(atts.getValue("hash"), collection.getUserFieldDefinitions());
                } else {
                    userField = new UserField(getClassForName(elementClass), atts.getValue("hash"), collection.getUserFieldDefinitions());
                }
            } else if (qName.equals("replacerEntry")) {
                String elementHash = atts.getValue("elementHash");
                String userFieldHash = atts.getValue("userFieldHash");
                String replaceUserFieldHash = atts.getValue("replaceUserFieldHash");
                WeightReplacer replacer = userFieldDefinitions.getWeightReplacer();
                replacer.setReplacement(elementHash, userFieldHash, replaceUserFieldHash);

            } else if (qName.equals("standardWeigthReplacerEntry")) {
                String elementHash = atts.getValue("elementHash");
                String edgeClassName = atts.getValue("edgeClass");
                String replaceUserFieldHash = atts.getValue("replaceUserFieldHash");
                WeightReplacer replacer = userFieldDefinitions.getWeightReplacer();
                Class<? extends Edge> edgeClass = getClassForName(edgeClassName).asSubclass(Edge.class);
                replacer.setUniformDistributionReplacement(elementHash, edgeClass, replaceUserFieldHash);

                //            } else if (qName.equals("userFieldName")) {
                //
                //            } else if (qName.equals("userFieldDescription")) {
                //
                //            } else if (qName.equals("userFieldStyle")) {
                //
                //            } else if (qName.equals("userFieldTreeVis")) {
                //
                //            } else if (qName.equals("userFieldStandardValue")) {
                //
                //            } else if (qName.equals("userFieldInternalAccounting")) {
                //
                //            } else if (qName.equals("userFieldInternalAccountingWeightUserFieldHash")) {
                //
                //            } else if (qName.equals("userFieldFormula")) {
                //
                //            } else if (qName.equals("userFieldFormatHash")) {
                //
                //            } else if (qName.equals("userFieldFormatString")) {
                //
            } else if (qName.equals("modell_3lgm_2")) {
                doc = collection.getMainGraphDocument();

            } else if (qName.equals("tool3lgm_clipboard")) {
                String timeStamp = atts.getValue("time");
                if (timeStamp == null || lastCopyFileTimeStamp.equals(timeStamp)) {
                    copyAndPastePositionShift = collection.increasePasteCounter();
                } else {
                    copyAndPastePositionShift = collection.resetPasteCounter();
                    lastCopyFileTimeStamp = timeStamp;
                }
                doc = Static.getSelectedGDCollection().getMainGraphDocument();
                szenario = collection.getSelectedDoc();
                szenario.clearSelection();
                oldToNewHashString = new HashMap<>();

            } else if (qName.equals("objects")) {
                Static.setProgressDialogStatusLabel("labelReadElements");

            }
        } catch (Exception e) {
            Log.show(Log.ERROR, e);
        }
    }

    @Override
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        //		System.out.println("end: " + qName);
        try {
            if (qName.equals("field")) {
                if (element != null) {
                    //Bei Aufgaben gab es die Felder requirement und note. Sollten Modelle
                    //auftauchen, bei denen diese Felder noxh existieren, dann werden die
                    //Einträge hier einfach an die Beschreibung angehängt. Die Felder
                    //description, requirement und note stehen in genau dieser Reihenfolge
                    //in der Datei.
                    if (field.equals("requirement") || field.equals("note")) {
                        String value = elementValue.toString().trim();
                        if (!value.equals("")) {
                            StringBuilder sb = new StringBuilder(element.getDescription());
                            sb.append("\n\n\n##### ");
                            sb.append(Tool3lgmConstants.getResString(field));
                            sb.append(" #####\n\n");
                            sb.append(value);
                            element.setDescription(sb.toString());
                        }
                        //falls es noch Modelle mit separaten external IDs gibt, werden diese hier in UserFields umgewandelt
                    } else if (field.toLowerCase().startsWith("extid")) {
                        UserField userField = userFieldDefinitions.getUserField(element.getClass(), field);
                        if (userField == null) {
                            userField = new UserField(element.getClass(), UserField.Style.ID, userFieldDefinitions);
                            userFieldDefinitions.add(userField);
                        }
                        element.setUserFieldInputValue(userField, elementValue.toString());
                    } else if (field.equals("layer")) {
                        //Tue nichts. Bei Modellen bis zur 3.5 stand der Layer noch mit im ModelElement. Danach nicht mehr, weil er sich immer
                        //aus den ModelConstants bzw. aus dem beim Einlesen der Elemente gerafde aktiven Layer ergibt.
                        //Weg lassen darf man die Abfrage hier aber auch nicht, weil wenn layer als Attribut angegeben ist und nicht hier ausgewertet
                        //wird, dann kommt es zu einem Fehler, wenn man dieses Attribut gar nicht auswertet
                    } else if (!ToolContentHandlerV3_0_DeprecatedValuesHandler.putDeprecatedXMLFieldString(collection, element, field, elementValue.toString())) {
                        if (!element.putXMLFieldString(field, elementValue.toString())) {
                            throw new SAXException("ModelElement konnte field nicht verarbeiten!\n ModelElement=" + element.getHashString() + "\n field=" + field + "\n Wert=" + elementValue);
                        }
                    }
                }
                field = null;

            } else if (qName.equals("userField")) {
                String val = elementValue.toString();
                //wenn eine Benutzerdefinierte Eigenschaft für ein Element eingelesen werden soll
                if (element != null) {
                    element.setUserFieldInputValue(userFieldDefinitions.getUserField(field), val);
                }
                //Benutzerdefinierte Eigenschaft für das Modell (GDCollection)
                else {
                    collection.setUserFieldInputValue(userFieldDefinitions.getUserField(field), val);
                }
                field = null;

            } else if (qName.equals("element")) {
                if (element != null) {
                    try {
                        if (!avoidDuplicates || element.getContainer(doc) == null) {
                            container = element.createContainer(doc);
                            int layer = ModelConstants.NO_LAYER;
                            try {
                                layer = element.layerFor(); //Knickpunkte werfen hier eine Exception, wenn sie noch keine Kante zugewiesen haben. Daher try-catch
                                LayerContainer layerContainer = doc.getLayer(layer);
                                layerContainer.add(container);
                            } catch (Exception e) {
                                if (container instanceof BendpointContainer) {
                                    hashToMainDocBendpointContainer.put(element.getHashString(), (BendpointContainer) container);
                                } else {
                                    throw new SAXException("ModelElement konnte field nicht verarbeiten!\n ModelElement=" + element.getHashString() + "\n field=" + field + "\n Wert=" + elementValue);
                                }
                            }
                        }
                        if (paste) {
                            pastedElements.add(container);
                        }
                        element = null;
                        container = null;
                    } catch (Exception e) {
                        Log.show(Log.ERROR, e);
                    }
                }

            } else if (qName.equals("container")) {
                if (tmp_container != null) {
                    container = tmp_container;
                    tmp_container = null;
                } else if (container != null) {
                    if (isCopyAndPaste() || !szenario.equals(doc)) {
                        ModelElement me = container.getElement();
                        try {
                            int layer = me.layerFor();
                            LayerContainer layerContainer = szenario.getLayer(layer);
                            layerContainer.add(container);
                        } catch (Exception e) {
                            //mache nichts. Knickpunkte werfen bei layerFor() eine Exception. Sie werden später dem Layer ihrer Kante hinzugefügt
                        }
                    }
                    if (container instanceof BendpointContainer) {
                        hashToSzenarioBendpointContainer.put(container.getHashString(), (BendpointContainer) container);
                    }
                    if (isCopyAndPaste()) {
                        szenario.addSimpleToSelection(container);
                    }
                }
                container = null;

            } else if (qName.equals("expanded")) {
                if (container != null) {
                    container.setExpanded(Boolean.valueOf(elementValue.toString()).booleanValue());
                }

            } else if (qName.equals("visible")) {
                if (container != null) {
                    container.setVisible(Boolean.valueOf(elementValue.toString()).booleanValue());
                }
            } else if (qName.equals("x")) {
                if (layout == null) {
                    return;
                }
                layout.x = Integer.parseInt(elementValue.toString());

                if (isCopyAndPaste()) {
                    layout.x = layout.x + 10 * copyAndPastePositionShift;
                }

            } else if (qName.equals("y")) {
                if (layout == null) {
                    return;
                }
                layout.y = Integer.parseInt(elementValue.toString());

                if (isCopyAndPaste()) {
                    layout.y = layout.y + 10 * copyAndPastePositionShift;
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
                if (layout == null) {
                    return;
                }
                String elementValueString = elementValue.toString();
                int formIndex = Integer.parseInt(elementValueString);
                GraphElementLayout.SHAPE[] shapes = GraphElementLayout.SHAPE.values();
                layout.form = shapes[formIndex];

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
                if (container != null) {
                    if (container.getElement() instanceof Prozess) {
                        container.checkTreeIcon();
                    }
                }
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
                for (BendpointContainer benpointContainer : hashToSzenarioBendpointContainer.values()) {
                    Knickpunkt bendpoint = benpointContainer.getKnickpunktKnoten();
                    String bendpointEdgeHash = bendpoint.getKantenHash();

                    if (isCopyAndPaste()) {
                        bendpointEdgeHash = oldToNewHashString.get(bendpointEdgeHash);
                    }
                    EdgeContainer kc = benpointContainer.getGraphDocument().findEdgeContainerCoded(bendpointEdgeHash);
                    if (kc != null) {
                        bendpoint.addEdge(kc.getEdge());
                        bendpoint.setOwner(kc);
                        kc.setKnickpunkt(benpointContainer, bendpoint.getIndex());
                        int layer = bendpoint.layerFor();
                        BendpointContainer mainDocBendpointContainer = hashToMainDocBendpointContainer.get(bendpoint.getHashString());
                        doc.getLayer(layer).add(mainDocBendpointContainer);
                        szenario.getLayer(layer).add(benpointContainer);
                    }
                }

                szenario = null;

            } else if (qName.equals("mapping")) {

            } else if (qName.equals("description")) {
                if (szenario != null) {
                    szenario.setDescription(elementValue.toString());
                } else if (doc != null) {
                    doc.setDescription(elementValue.toString());
                }

            } else if (qName.equals("bitmap")) {
                if (iconKey != null && !collection.getIconTable().containsKey(iconKey)) {
                    collection.getIconTable().put(iconKey, Base64.decode(elementValue.toString()));
                }

                iconKey = null;

            } else if (qName.equals("images")) {

            } else if (qName.equals("header")) {

            } else if (qName.equals("title")) {

            } else if (qName.equals("version")) {
                GDCollectionFileHandler fileHandler = collection.getFileHandler();
                fileHandler.setFileVersion(elementValue.toString());

            } else if (qName.equals("avoidDuplicates")) {
                avoidDuplicates = false;

            } else if (qName.equals("userFieldDefinitions")) {

            } else if (qName.equals("userFieldDef")) {
                if (userField != null) {
                    collection.getUserFieldDefinitions().add(userField);
                } else {
                    throw new SAXException("Error while parsing definition of userFields: userFiel shouldn't not be equals to null");
                }

                userField = null;

            } else if (qName.equals("userFieldName") || qName.equals("userFieldDescription") || qName.equals("userFieldStyle") || qName.equals("userFieldTreeVis") || qName.equals("userFieldStandardValue") || qName.equals("userFieldInternalAccounting")
                    || qName.equals("userFieldInternalAccountingWeightUserFieldHash") || qName.equals("userFieldFormula") || qName.equals("userFieldFormatString") || qName.equals("userFieldFormatHash")) {
                if (userField == null) {
                    throw new SAXException("Error while parsing definition of userFields: userFiel shouldn't not be equals to null");
                }
                userField.putXMLFieldString(qName, elementValue.toString());
            } else if (qName.equals("modell_3lgm_2")) {
                Static.setProgressDialogStatusLabel("labelReferenceIcons");

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

                Static.setProgressDialogStatusLabel("labelConnectTraces");

                //die HashStrings für das Start- bzw. End-Objekt einer Edge
                // auflösen und die wirklichen Node setzten
                if (isCopyAndPaste()) {
                    for (BendpointContainer bendpointContainer : hashToMainDocBendpointContainer.values()) {
                        Knickpunkt bendpoint = bendpointContainer.getKnickpunktKnoten();
                        bendpoint.putXMLFieldString("kanteHash", oldToNewHashString.get(bendpoint.getKantenHash()));
                    }
                }
                for (Edge edge : edges) {
                    if (isCopyAndPaste()) {
                        edge.putXMLFieldString("start", oldToNewHashString.get(edge.getStartHash()));
                        edge.putXMLFieldString("end", oldToNewHashString.get(edge.getEndHash()));
                    }
                    edge.decodeHashStrings(doc);
                }
            }
        } catch (Exception e) {
            Log.show(Log.ERROR, e);
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

    /**
     * @param atts
     * @return
     */
    private static final String attsToString(final Attributes atts) {
        String retVal = "";
        for (int i = 0; i < atts.getLength(); i++) {
            retVal += " " + atts.getType(i) + " " + atts.getValue(i);
        }
        return retVal;
    }
}
