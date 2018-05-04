package de.imise.tool3lgm.xml;

import static de.imise.tool3lgm.Tool3lgmConstants.getFileNameExtensionFilter;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.Tool3lgmConstants.FileFilterType.LGM3_UNZIPPED;
import static de.imise.tool3lgm.Tool3lgmConstants.FileFilterType.LGM3_ZIP;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_LINE_STYLE;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_LINE_THICKNESS;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import com.google.common.base.Strings;
import com.google.common.collect.Table.Cell;

import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserField.Style;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;
import de.imise.tool3lgm.graphtools.view.container.BendpointContainer;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.LayerContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.tool3lgm.gui.InternalGraphFrame;
import de.imise.tool3lgm.log.Log;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.htmlxml.IntendingXMLWriter;
import de.imise.util.io.FileHandler;

/**
 * @author AXS (04.08.2017)
 */
public class ToolXMLWriter extends IntendingXMLWriter {

    /** Modell, das gespeichert werden soll */
    protected final GDCollection gdcoll;

    /** Hashes aller Icons, die von den über diesen Writer exportierten Elementen tatsächlich genutzt werden */
    private final Set<String> usedIconHashes;

    protected ToolXMLWriter(final GDCollection gdcoll, final File file) throws XMLStreamException, FactoryConfigurationError, IOException {
        this(gdcoll, file, null);
    }

    protected ToolXMLWriter(final GDCollection gdcoll, final File file, final String zipEntryName) throws XMLStreamException, FactoryConfigurationError, IOException {
        super(file, zipEntryName);
        this.gdcoll = gdcoll;
        usedIconHashes = gdcoll != null ? new HashSet<>() : null;
    }

    /**
     * @param file
     * @return
     */
    public static final String getZipEntryName(final File file) {
        String fileName = file.getName();
        FileNameExtensionFilter zipFileNameExtensionFilter = getFileNameExtensionFilter(LGM3_ZIP);
        String zipFileNameExtension = zipFileNameExtensionFilter.getExtensions()[0];
        FileNameExtensionFilter unzippedFileNameExtensionFilter = getFileNameExtensionFilter(LGM3_UNZIPPED);
        String unzippedFileNameExtension = unzippedFileNameExtensionFilter.getExtensions()[0];
        String entryName = ensureFileExtension(fileName, unzippedFileNameExtension, zipFileNameExtension);
        return entryName;
    }

    /**
     * @param fileName
     * @param fileExtensionToAppend
     * @param fileExtensionToRemove
     * @return
     */
    private static final String ensureFileExtension(final String fileName, final String fileExtensionToAppend, final String fileExtensionToRemove) {
        String entryName = fileName;
        boolean extensionStartsWithDot = fileExtensionToAppend.startsWith(".");
        String extension = FileHandler.getExtension(entryName, extensionStartsWithDot);
        if (!Strings.isNullOrEmpty(fileExtensionToRemove) && fileExtensionToRemove.equals(extension)) {
            entryName = entryName.substring(0, entryName.length() - extension.length());
        }
        if (!entryName.endsWith(fileExtensionToAppend)) {
            boolean lastCharIsDot = entryName.endsWith(".");
            if (lastCharIsDot) {
                if (extensionStartsWithDot) {
                    entryName = entryName.substring(0, entryName.length() - 1);
                }
            } else if (!extensionStartsWithDot) {
                entryName += ".";
            }
            entryName += fileExtensionToAppend;
        }
        return entryName;

    }

    /**
     * @param gdcoll
     * @param file
     * @return
     */
    public static boolean write(final GDCollection gdcoll, final File file) {
        return write(gdcoll, file, null);
    }

    /**
     * @param gdcoll
     * @param file
     * @param zipEntryName
     * @return
     */
    public static boolean write(final GDCollection gdcoll, final File file, final String zipEntryName) {
        try {
            ToolXMLWriter toolXMLWriter = new ToolXMLWriter(gdcoll, file, zipEntryName);
            toolXMLWriter.writeModel();
            toolXMLWriter.finish();
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
            return false;
        }
        return true;
    }

    /**
     * @param definitions
     * @param file
     * @return
     */
    public static boolean writeUserFieldDefinitions(final UserFieldDefinitions definitions, final File file) {
        try {
            ToolXMLWriter toolXMLWriter = new ToolXMLWriter(null, file);
            toolXMLWriter.writeUserFieldDefinitions(definitions, false);
            toolXMLWriter.finish();
        } catch (Exception e) {
            Log.show(Log.ERROR, "Exception while exporting UserFieldFile", e);
            return false;
        }
        return true;
    }

    /**
     * Schreibt nur einen Teil der Teilmodelle und Elemente eines Modells
     *
     * @param gdcoll
     * @param file
     * @param szenarios
     * @param elements
     * @param userFields
     * @param iconHashes
     * @return
     */
    public static boolean writeExport(final GDCollection gdcoll, final File file, final List<Szenario> szenarios, final Collection<ModelElement> elements, final Iterable<UserField> userFields, final Iterable<String> iconHashes) {
        try {
            ToolXMLWriter toolXMLWriter = new ToolXMLWriter(gdcoll, file);
            toolXMLWriter.writeModel(gdcoll.getName() + " (export)", szenarios, elements, userFields, iconHashes);
            toolXMLWriter.finish();
        } catch (Exception e) {
            Log.show(Log.ERROR, "Exception while exporting UserFieldFile", e);
            return false;
        }
        return true;
    }

    /////////////////////////////////////////////
    // Hauptfunktion zum Schreiben des Modells //
    /////////////////////////////////////////////

    private void writeModel() throws XMLStreamException {
        writeModel(gdcoll.getName(), null, null, null, null);
    }

    /**
     * Schreibt eine Modell in eine XML-Datei.
     *
     * @param name Name des Modells in der Datei
     * @param szenarios Liste der Teilmodelle, die geschrieben werden sollen. Ist diese Liste <code>null</code>, werden alle Teilmodell geschrieben.
     * @param elements Liste der Elemente, die geschrieben werden sollen. Ist diese Liste <code>null</code>, werden alle Elemente geschrieben.
     * @param userFields Alle UserFields, die geschrieben werden sollen. Ist dieses {@link Iterable} <code>null</code>, werden alle UserFields
     *            geschrieben.
     * @param iconHashes HashStrings aller Icons, die geschrieben werden sollen. Ist dieses {@link Iterable} <code>null</code>, werden alle Icons
     *            geschrieben.
     * @throws XMLStreamException
     */
    private void writeModel(final String name, final List<Szenario> szenarios, final Collection<ModelElement> elements, final Iterable<UserField> userFields, final Iterable<String> iconHashes) throws XMLStreamException {
        writeStartDocument();
        writeStartElement("modell_3lgm_2"); //<modell_3lgm_2>
        writeStartElement("header"); //<header>
        writeElement("title", name);
        writeElement("description", gdcoll.getMainGraphDocument().getDescription());
        writeElement("version", gdcoll.getFileVersion());
        writeEndElement(); //</header>
        if (userFields == null) {
            writeUserFieldDefinitions(gdcoll.getUserFieldDefinitions(), true);
        } else {
            writeUserFieldDefinitions(userFields);
        }
        writeStartElement("objects"); //<objects>
        writeStartElement("model"); //<model>
        writeUserFieldValues(gdcoll);
        writeEndElement(); //</model>
        writeModelElements(elements);
        writeEndElement(); //</objects>
        writeSzenarios(szenarios, elements);
        writeStartElement("images"); //<images>
        writeImages(iconHashes == null ? gdcoll.getIconTable().keySet() : iconHashes);
        writeEndElement(); //</images>
        writeEndElement(); //</modell_3lgm_2>
    }

    protected void writeStartDocument() throws XMLStreamException {
        writeStartDocument("UTF-8", "1.0");
        writeComment(ToolXMLParser.getCurrentFileVersionBare());
    }

    //////////////////////////
    // UserFieldDefinitions //
    //////////////////////////

    /**
     * @param definitions
     * @param appendWeightReplacer
     * @throws XMLStreamException
     */
    private void writeUserFieldDefinitions(final UserFieldDefinitions definitions, final boolean appendWeightReplacer) throws XMLStreamException {
        Iterable<UserField> userFields = CollectionUtils.getCommonIterable(definitions.getFormatUserFields(), definitions.getGlobalUserFields(), definitions.getElementClassUserFields());
        writeUserFieldDefinitions(userFields, appendWeightReplacer ? definitions.getWeightReplacer() : null);
    }

    protected void writeUserFieldDefinitions(final Iterable<UserField> userFields) throws XMLStreamException {
        writeUserFieldDefinitions(userFields, null);
    }

    protected void writeUserFieldDefinitions(final Iterable<UserField> userFields, final WeightReplacer weightReplacer) throws XMLStreamException {
        writeStartElement("userFieldDefinitions");
        //Zuerst immer die Formate und dann immer die globalen Varialen rausschreiben
        for (UserField uf : userFields) {
            writeUserField(uf);
        }
        writeUserFieldWeightReplacer(weightReplacer);
        writeEndElement();
    }

    protected void writeUserField(final UserField uf) throws XMLStreamException {
        writeStartElement("userFieldDef");
        //bei Modell-Attributen wird die targetClass nicht als UserField ins
        // Tag geschrieben
        if (!uf.isGlobalOrFormat()) {
            writeAttribute("elementClass", uf.getTargetClass().getSimpleName());
        }
        writeAttribute("hash", uf.getHashCode());
        writeElement("userFieldName", uf.getName());
        writeElement("userFieldDescription", uf.getDescription());
        Style style = uf.getStyle();
        writeElement("userFieldStyle", style.name());
        writeElement("userFieldTreeVis", String.valueOf(uf.isTreeVisibility()));
        for (int i = 0; i < uf.getListValuesCount(); i++) {
            writeElement("userFieldStandardValue", uf.getListValueAt(i));
        }

        if (style == Style.FORMAT) {
            writeElement("userFieldFormatString", uf.getFormatExportString());
        } else if (style == Style.CLASSIFICATION_NUMBER) {
            UserField formatUserField = uf.getFormatUserField();
            if (formatUserField != null) {
                writeElement("userFieldFormatHash", formatUserField.getHashCode());
            }
        } else if (style == Style.CLASSIFICATION_NUMBER_FORMULA) {
            writeElement("userFieldFormula", uf.getFormula());
            UserField formatUserField = uf.getFormatUserField();
            if (formatUserField != null) {
                writeElement("userFieldFormatHash", formatUserField.getHashCode());
            }
        }
        writeEndElement();
    }

    private void writeUserFieldWeightReplacer(final WeightReplacer weightReplacer) throws XMLStreamException {
        if (weightReplacer != null && !weightReplacer.isEmpty()) {
            writeStartElement("weightReplacer");
            if (!weightReplacer.isEmptyReplacer()) {
                writeStartElement("replacer");
                for (Cell<String, String, String> replacerEntry : weightReplacer.getReplacerContent()) {
                    writeEmptyElement("replacerEntry");
                    writeAttribute("elementHash", replacerEntry.getRowKey());
                    writeAttribute("userFieldHash", replacerEntry.getColumnKey());
                    writeAttribute("replaceUserFieldHash", replacerEntry.getValue());
                }
                writeEndElement();
            }
            if (!weightReplacer.isEmptyStandardReplacer()) {
                writeStartElement("standardWeigthReplacer");
                for (Cell<String, Class<? extends Edge>, String> replacerEntry : weightReplacer.getStandardReplacerContent()) {
                    writeEmptyElement("standardWeigthReplacerEntry");
                    writeAttribute("elementHash", replacerEntry.getRowKey());
                    writeAttribute("userFieldHash", replacerEntry.getColumnKey().getSimpleName());
                    writeAttribute("replaceUserFieldHash", replacerEntry.getValue());
                }
                writeEndElement();
            }
            writeEndElement();
        }
    }

    ///////////////////////////////////////////
    // UserField-Values der UserFieldTargets //
    ///////////////////////////////////////////

    /**
     * Schreibt das übergebene userFieldTarget in die XML-Datei
     *
     * @param userFieldTarget
     * @throws XMLStreamException
     */
    private void writeUserFieldValues(final UserFieldTarget userFieldTarget) throws XMLStreamException {
        for (UserField keyUserField : userFieldTarget.getUserFieldInputValueKeys()) {
            //Hier muss geprüft werden, ob das rauszuschreibende userfield null ist, denn darf es nicht rausgeschrieben werden.
            if (keyUserField != null) {
                writeStartElement("userField"); //<userField>
                writeAttribute("hash", keyUserField.getHashCode());
                writeCharacters(userFieldTarget.getUserFieldInputValue(keyUserField));
                writeEndElement(); //</userField>
            }
        }
    }

    ///////////////////
    // ModelElements //
    ///////////////////

    /**
     * Schreibt die übergebenen ModellElemente in die XML-Datei oder alle ModellElemente der Collection, wenn die übergebenen Elemente
     * <code>null</code> sind.
     *
     * @param elements Elemente, die rausgeschrieben werden sollen. Ist dieses Object <code>null</code>, dann werden alle Elemente des
     *            Gesamtmodells rausgeschrieben
     * @throws XMLStreamException
     */
    private void writeModelElements(final Iterable<ModelElement> elements) throws XMLStreamException {
        if (elements != null) {
            for (ModelElement me : elements) {
                writeModelElement(me);
            }
        } else {
            LGMGraphDocument doc = gdcoll.getMainGraphDocument();
            for (LayerContainer lc : doc.getLayers()) {
                for (NodeContainer kc : lc.getNodeContainer()) {
                    writeModelElement(kc.getElement());
                }
                doc.sortKanten();
                for (EdgeContainer kc : lc.getEdgeContainer()) {
                    writeModelElement(kc.getElement());
                }
                for (BendpointContainer kc : lc.getBendpointContainers()) {
                    writeModelElement(kc.getElement());
                }
            }
        }
    }

    /**
     * Schreibt das übergebene Modellelement in die XML-Datei
     *
     * @param me
     * @throws XMLStreamException
     */
    public void writeModelElement(final ModelElement me) throws XMLStreamException {
        writeStartElement("element"); //<element>
        writeAttribute("class", me.getClass().getSimpleName());
        writeAttribute("hash", me.getHashString());
        writeModelElementField("name", me.getName());
        writeModelElementField("description", me.getDescription());
        String associatedSzenHashString = me.getAssociatedDoc();
        if (!Strings.isNullOrEmpty(associatedSzenHashString)) {
            writeModelElementField("assoc_szen", associatedSzenHashString);
        }
        writeUserFieldValues(me);

        if (me instanceof Edge) {
            Edge edge = (Edge) me;
            writeModelElementField("start", edge.getStart().getHashString());
            writeModelElementField("end", edge.getEnd().getHashString());
            writeModelElementField("state", edge.getDirectionName());
        } else if (me instanceof Knickpunkt) {
            Knickpunkt bendpoint = (Knickpunkt) me;
            EdgeContainer edgeContainer = bendpoint.getOwner();
            writeModelElementField("kantenHash", edgeContainer.getHashString());
            writeModelElementField("index", edgeContainer.getIndexOfKnickpunkt(bendpoint));
        }
        writeEndElement(); //</element>
    }

    private void writeModelElementField(final String nameAttribute, final String text) throws XMLStreamException {
        writeStartElement("field"); //<field>
        writeAttribute("name", nameAttribute);
        writeCharacters(getValidString(text));
        writeEndElement(); //</field>
    }

    private void writeModelElementField(final String nameAttribute, final int intValue) throws XMLStreamException {
        writeModelElementField(nameAttribute, String.valueOf(intValue));
    }

    /////////////////////////////
    // Szenarios = Teilmodelle //
    /////////////////////////////

    /**
     * @param szenarios Liste der Szenarios, die rausgeschrieben werden sollen. Ist die Liste <code>null</code>, werden alle Szenarios rausgeschrieben
     * @param elements ist diese Collection nicht null, werden nur die Elemente der Szenarios rausgeschrieben, die sich in der Collection befinden
     * @throws XMLStreamException
     */
    private void writeSzenarios(final List<Szenario> szenarios, final Collection<ModelElement> elements) throws XMLStreamException {
        for (Szenario szen : gdcoll.getSzenarios()) {
            if (szenarios != null && !szenarios.contains(szen)) {
                continue;
            }
            writeStartElement("szenario"); //<szenario>
            writeAttribute("hash", szen.getHashString());
            writeAttribute("titel", szen.getTitle());
            writeElement("description", szen.getDescription());
            // Informationen über Ansicht speichern
            InternalGraphFrame frame = szen.getFrame();
            if (frame != null) {
                writeStartElement("view"); //<view>
                writeElement("selected", frame.isSelected());
                Point viewPosition = frame.getScrollPane().getViewport().getViewPosition();
                writeElement("x", viewPosition.x);
                writeElement("y", viewPosition.y);
                InputGraphArea inputGraphArea = frame.getInputGraphArea();
                writeElement("zoom", inputGraphArea.getZoom());
                writeElement("degree", inputGraphArea.getLayerAngle());
                writeElement("shift", inputGraphArea.getMultiViewLayerGap());
                writeElement("pageSizeFactor", szen.getPageSizeFactor());
                writeElement("activeLayer", gdcoll.getActiveLayer());
                writeElement("multiView", inputGraphArea.isMultiView());
                writeEndElement(); //</view>
            }
            writeStartElement("mapping"); //"<mapping>"
            ElementsLayoutDefinition mapping = szen.getMapping();
            for (Class<? extends ModelElement> elementClass : mapping.getElementClassesWithStandardLayout()) {
                GraphElementLayout standardElementLayout = mapping.getStandardElementLayout(elementClass);
                writeGraphElementLayout(elementClass, standardElementLayout, true);
            }
            writeEndElement(); //"</mapping>"
            writeLayerContainer(szen, elements);
            writeEndElement(); //</szenario>
        }
    }

    //////////////////////////////////
    // Layer- und Element-Container //
    //////////////////////////////////

    /**
     * Schreibt die LayerContainer mit ihren enthaltenen ElementContainern der Layer in die XML-Datei
     *
     * @param szen Teilmodell das geschrieben werden soll
     * @param elements ist diese Collection nicht <code>null</code>, werden nur die darin enthaltenen Elemente rausgeschrieben
     * @throws XMLStreamException
     */
    private void writeLayerContainer(final Szenario szen, final Collection<ModelElement> elements) throws XMLStreamException {
        for (LayerContainer lc : szen.getLayers()) {
            writeStartElement("layer"); //<layer>
            writeAttribute("number", lc.getLayerNumber());
            writeGraphElementLayout(null, lc.get3LGMLayout(), true);
            for (NodeContainer kc : lc.getNodeContainer()) {
                if (elements == null || elements.contains(kc.getElement())) {
                    writeElementContainer(kc);
                }
            }
            for (EdgeContainer kc : lc.getEdgeContainer()) {
                if (elements == null || elements.contains(kc.getElement())) {
                    writeElementContainer(kc);
                }
            }
            for (BendpointContainer kc : lc.getBendpointContainers()) {
                if (elements == null || elements.contains(kc.getElement())) {
                    writeElementContainer(kc);
                }
            }
            writeEndElement(); //</layer>
        }
    }

    protected void writeElementContainer(final ElementContainer ec) throws XMLStreamException {
        writeStartElement("container"); //<container>
        writeAttribute("hash", ec.getHashString());
        if (!(ec instanceof EdgeContainer)) {
            writeElement("expanded", ec.isExpanded());
            writeElement("visible", ec.isVisible());
        }
        GraphElementLayout expandedLayout = ec.getE3LGMLayout();
        if (expandedLayout != null) {
            writeGraphElementLayout(ModelElement.class, expandedLayout, true);
        }
        GraphElementLayout nonExpandedLayout = ec.getNE3LGMLayout();
        if (nonExpandedLayout != null) {
            writeGraphElementLayout(ModelElement.class, nonExpandedLayout, false);
        }
        writeEndElement(); //</container>
    }

    ////////////
    // Layout //
    ////////////

    /**
     * @param elementClass Elementklasse, für die das Layout geschrieben werden soll (wenn null, dann für Layer)
     * @param layout Layout zu diesr Elementklasse
     * @param expanded wenn <code>true</code>, wird das normale Layout geschrieben, wenn <code>false</code> das zusammengeklappte Layout
     * @throws XMLStreamException
     */
    private void writeGraphElementLayout(final Class<?> elementClass, final GraphElementLayout layout, final boolean expanded) throws XMLStreamException {
        writeStartElement(expanded ? "layout" : "nelayout"); //<layout> oder <nelayout>
        //ElementClass ist nur für Layer null
        if (elementClass != null) {
            writeAttribute("class", elementClass.getSimpleName());
        }
        writeGraphElementLayoutColor(layout.bg_color, "bg_color");
        writeGraphElementLayoutColor(layout.fg_color, "fg_color");
        writeGraphElementLayoutColor(layout.border_color, "border_color");

        if (layout.line_thickness != STANDARD_LINE_THICKNESS) {
            writeElement("line_thickness", layout.line_thickness);
        }
        if (layout.line_style != STANDARD_LINE_STYLE) {
            writeElement("line_style", layout.line_style);
        }
        if (layout.form != null/* && layout.form != STANDARD_FORM */) {
            writeElement("form", layout.form.ordinal());
        }
        Font font = layout.getFont();
        if (font != null) {
            writeElement("font_family", font.getName());
            writeElement("font_style", font.getStyle());
            writeElement("font_size", font.getSize());
        }
        writeElement("x", layout.x);
        writeElement("y", layout.y);
        if (layout.width != -1) {
            writeElement("width", layout.width);
        }
        if (layout.height != -1) {
            writeElement("height", layout.height);
        }
        if (layout.icon != null) {
            usedIconHashes.add(layout.icon);
            writeElement("icon", layout.icon);
        }
        //        if (layout.valign != STANDARD_VALIGN) {
        writeElement("valign", layout.valign);
        //        }
        //        if (layout.halign != STANDARD_HALIGN) {
        writeElement("halign", layout.halign);
        //        }
        writeEndElement(); //</layout> oder </nelayout>
    }

    /**
     * @param color
     * @param colorName
     * @return
     */
    private void writeGraphElementLayoutColor(final Color color, final String colorName) throws XMLStreamException {
        if (color != null) {
            writeStartElement("color"); //<color>
            if (!Strings.isNullOrEmpty(colorName)) {
                writeAttribute("name", colorName);
            }
            writeElement("red", color.getRed());
            writeElement("green", color.getGreen());
            writeElement("blue", color.getBlue());
            int alpha = color.getAlpha();
            if (alpha != 255) {
                writeElement("alpha", alpha);
            }
            writeEndElement(); //</color>
        }
    }

    ///////////
    // ICONS //
    ///////////

    /**
     * Schreibt alle Icons in die XML-Datei, deren HashString in den übergebenen iconHashStrings vorkommen
     *
     * @param iconHashStrings
     * @throws XMLStreamException
     */
    private void writeImages(final Iterable<String> iconHashStrings) throws XMLStreamException {
        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        for (String iconHashString : iconHashStrings) {
            //nur die Icons in den XML-Stream schreiben, die von den exportierten Elementen auch genutzt werden
            if (usedIconHashes.contains(iconHashString)) {
                writeImage(iconHashString, iconTable.get(iconHashString));
            }
        }
    }

    protected void writeImage(final String iconHashString, final byte[] icon) throws XMLStreamException {
        writeStartElement("bitmap"); //<bitmap>
        writeAttribute("type", "gif/base64");
        writeAttribute("hash", iconHashString);
        writeCharacters(Base64.encode(icon));
        writeEndElement(); //</bitmap>
    }

}
