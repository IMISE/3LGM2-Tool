package de.imise.tool3lgm.xml;

import static de.imise.tool3lgm.Tool3lgmConstants.getFileNameExtensionFilter;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.Tool3lgmConstants.FileFilterType.LGM3_UNZIPPED;
import static de.imise.tool3lgm.Tool3lgmConstants.FileFilterType.LGM3_ZIP;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_LINE_STYLE;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_LINE_THICKNESS;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_TEXT_ALIGNMENT_HTML;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_TEXT_POSITION_HORIZONTAL;
import static de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.STANDARD_TEXT_POSITION_VERTICAL;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Table.Cell;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.consistency.ModelCleaner;
import de.imise.tool3lgm.graphtools.metamodel.elements.Bendpoint;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
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
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewParameter;
import de.imise.tool3lgm.log.Log;
import de.imise.util.Alphabetical;
import de.imise.util.collections.CollectionUtils;
import de.imise.util.htmlxml.IntendingXMLWriter;
import de.imise.util.io.FileHandler;

/**
 * @author AXS (04.08.2017)
 */
public class ToolXMLWriter extends IntendingXMLWriter {

    /** Modell, das gespeichert werden soll */
    protected final GDCollection gdcoll;

    /**
     * IDs aller Icons, die von den über diesen Writer exportierten Elementen
     * tatsächlich genutzt werden
     */
    private final Set<String> usedIconIDs;

    protected ToolXMLWriter(final GDCollection gdcoll, final File file) throws XMLStreamException, FactoryConfigurationError, IOException {
        this(gdcoll, file, null);
    }

    protected ToolXMLWriter(final GDCollection gdcoll, final File file, final String zipEntryName) throws XMLStreamException, FactoryConfigurationError, IOException {
        super(file, zipEntryName);
        this.gdcoll = gdcoll;
        usedIconIDs = gdcoll != null ? new HashSet<>() : null;
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
            ModelCleaner.cleanModel(gdcoll);
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
     * @param iconIDs
     * @return
     */
    public static boolean writeExport(final GDCollection gdcoll, final File file, final List<Szenario> szenarios, final Collection<ModelElement> elements, final Iterable<UserField> userFields, final Iterable<String> iconIDs) {
        try {
            ToolXMLWriter toolXMLWriter = new ToolXMLWriter(gdcoll, file);
            toolXMLWriter.writeModel(gdcoll.getName() + " (export)", szenarios, elements, userFields, iconIDs);
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
     * @param szenarios Liste der Teilmodelle, die geschrieben werden sollen.
     *            Ist diese Liste <code>null</code>, werden alle Teilmodell
     *            geschrieben.
     * @param elements Liste der Elemente, die geschrieben werden sollen. Ist
     *            diese Liste <code>null</code>, werden alle Elemente
     *            geschrieben.
     * @param userFields Alle UserFields, die geschrieben werden sollen. Ist
     *            dieses {@link Iterable} <code>null</code>, werden alle
     *            UserFields geschrieben.
     * @param iconIDs IDs aller Icons, die geschrieben werden sollen. Ist dieses
     *            {@link Iterable} <code>null</code>, werden alle Icons
     *            geschrieben.
     * @throws XMLStreamException
     */
    private void writeModel(final String name, final List<Szenario> szenarios, final Collection<ModelElement> elements, final Iterable<UserField> userFields, final Iterable<String> iconIDs) throws XMLStreamException {
        writeStartDocument();
        writeStartElement("modell_3lgm_2"); //<modell_3lgm_2>
        writeStartElement("header"); //<header>
        writeElement("title", name);
        writeElement("description", gdcoll.getMainDoc().getDescription());
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
        writeImages(iconIDs == null ? gdcoll.getIconTable().keySet() : iconIDs);
        writeEndElement(); //</images>
        writeEndElement(); //</modell_3lgm_2>
    }

    protected void writeStartDocument() throws XMLStreamException {
        writeStartDocument("UTF-8", "1.0");
        writeComment(ToolXMLParser.getCurrentFileVersionBare(gdcoll));
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
        writeAttribute("hash", uf.getID());
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
                writeElement("userFieldFormatHash", formatUserField.getID());
            }
        } else if (style == Style.CLASSIFICATION_NUMBER_FORMULA) {
            writeElement("userFieldFormula", uf.getFormula());
            UserField formatUserField = uf.getFormatUserField();
            if (formatUserField != null) {
                writeElement("userFieldFormatHash", formatUserField.getID());
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
                writeAttribute("hash", keyUserField.getID());
                writeCharacters(userFieldTarget.getUserFieldInputValue(keyUserField));
                writeEndElement(); //</userField>
            }
        }
    }

    ///////////////////
    // ModelElements //
    ///////////////////

    /**
     * Schreibt die übergebenen ModellElemente in die XML-Datei oder alle
     * ModellElemente der Collection, wenn die übergebenen Elemente
     * <code>null</code> sind.
     *
     * @param elements Elemente, die rausgeschrieben werden sollen. Ist dieses
     *            Object <code>null</code>, dann werden alle Elemente des
     *            Gesamtmodells rausgeschrieben
     * @throws XMLStreamException
     */
    private void writeModelElements(final Iterable<ModelElement> elements) throws XMLStreamException {
        if (elements != null) {
            for (ModelElement me : elements) {
                writeModelElement(me);
            }
        } else {
            LGMGraphDocument doc = gdcoll.getMainDoc();
            //layer 0 is Physical Tool Layer  -> Layer 4 is Logical Layer -> Iterate backward
            List<LayerContainer> layers = doc.getLayers();
            //Nodes
            for (int i = layers.size() - 1; i >= 0; i--) {
                LayerContainer lc = layers.get(i);
                List<NodeContainer> nodeContainersAlphabetical = lc.getNodeContainersAlphabetical();
                writeModelElementsInternal(nodeContainersAlphabetical);
            }
            //Edges
            doc.sortEdgeContainers();
            for (int i = layers.size() - 1; i >= 0; i--) {
                LayerContainer lc = layers.get(i);
                Iterable<EdgeContainer> edgeContainers = lc.getEdgeContainers();
                writeModelElementsInternal(edgeContainers);
            }
            //Bendpoints
            for (int i = layers.size() - 1; i >= 0; i--) {
                LayerContainer lc = layers.get(i);
                Iterable<BendpointContainer> bendpointContainers = lc.getBendpointContainers();
                writeModelElementsInternal(bendpointContainers);
            }
        }
    }

    /**
     * Schreibt die Modellelemente der übergebenen ElementContainer in die
     * XML-Datei
     *
     * @param elements
     * @throws XMLStreamException
     */
    private void writeModelElementsInternal(final Iterable<? extends ElementContainer> elements) throws XMLStreamException {
        for (ElementContainer ec : elements) {
            ModelElement me = ec.getElement();
            writeModelElement(me);
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
        writeAttribute("hash", me.getID());
        writeModelElementField("name", me.getName());
        writeModelElementFieldIfNotNullOrEmpty("description", me.getDescription());
        String associatedSzenID = me.getAssociatedSzenID();
        if (!Strings.isNullOrEmpty(associatedSzenID)) {
            writeModelElementField("assoc_szen", associatedSzenID);
        }
        writeUserFieldValues(me);

        if (me instanceof Edge) {
            Edge edge = (Edge) me;
            writeModelElementField("start", edge.getStart().getID());
            writeModelElementField("end", edge.getEnd().getID());
            if (me instanceof DoubleMeaningEdge) {
                writeModelElementField("state", ((DoubleMeaningEdge) edge).getConnectionStateName());
            }
            if (me instanceof OptionalEdge) {
                OptionalEdge optionalEdge = (OptionalEdge) me;
                if (optionalEdge.isOptional()) {
                    writeModelElementField("optional", Boolean.TRUE.toString());
                }
            }
        } else if (me instanceof Bendpoint) {
            Bendpoint bendpoint = (Bendpoint) me;
            EdgeContainer edgeContainer = bendpoint.getOwner();
            writeModelElementField("kantenHash", edgeContainer.getID());
            writeModelElementField("index", edgeContainer.getIndexOfBendpoint(bendpoint));
        }
        writeEndElement(); //</element>
    }

    /**
     * @param nameAttribute
     * @param text
     * @throws XMLStreamException
     */
    private void writeModelElementField(final String nameAttribute, final String text) throws XMLStreamException {
        writeModelElementField(nameAttribute, text, true);
    }

    /**
     * @param nameAttribute
     * @param text
     * @throws XMLStreamException
     */
    private void writeModelElementFieldIfNotNullOrEmpty(final String nameAttribute, final String text) throws XMLStreamException {
        writeModelElementField(nameAttribute, text, false);
    }

    /**
     * @param nameAttribute
     * @param text
     * @param writeIfNullOrEmpty only if <code>true</code> the tag will be
     *            written if the text is {@link NullPointerException} or empty
     * @throws XMLStreamException
     */
    private void writeModelElementField(final String nameAttribute, final String text, final boolean writeIfNullOrEmpty) throws XMLStreamException {
        if (writeIfNullOrEmpty || !Strings.isNullOrEmpty(text)) {
            writeStartElement("field"); //<field>
            writeAttribute("name", nameAttribute);
            writeCharacters(getValidString(text));
            writeEndElement(); //</field>
        }
    }

    /**
     * @param nameAttribute
     * @param intValue
     * @throws XMLStreamException
     */
    private void writeModelElementField(final String nameAttribute, final int intValue) throws XMLStreamException {
        writeModelElementField(nameAttribute, String.valueOf(intValue));
    }

    /////////////////////////////
    // Szenarios = Teilmodelle //
    /////////////////////////////

    /**
     * @param mapping
     * @return a alphabetical sortet list of all element classes with standard
     *         layout
     */
    private List<Class<? extends ModelElement>> getElementClassesWithStandardLayout(final ElementsLayoutDefinition mapping) {
        Iterable<Class<? extends ModelElement>> elementClassesWithStandardLayout = mapping.getElementClassesWithStandardLayout();
        ArrayList<Class<? extends ModelElement>> elementClassesWithStandardLayoutAlphabetical = Lists.newArrayList(elementClassesWithStandardLayout);
        //to ensure that the order of element classes is the same independently of the systems locale we always sort here with english locale
        Comparator<Object> comparator = Alphabetical.getComparator(Locale.ENGLISH);
        Collections.sort(elementClassesWithStandardLayoutAlphabetical, comparator);
        return elementClassesWithStandardLayoutAlphabetical;
    }

    /**
     * @param szenarios Liste der Szenarios, die rausgeschrieben werden sollen.
     *            Ist die Liste <code>null</code>, werden alle Szenarios
     *            rausgeschrieben
     * @param elements ist diese Collection nicht null, werden nur die Elemente
     *            der Szenarios rausgeschrieben, die sich in der Collection
     *            befinden
     * @throws XMLStreamException
     */
    private void writeSzenarios(final List<Szenario> szenarios, final Collection<ModelElement> elements) throws XMLStreamException {
        GraphDocument selectedDoc = gdcoll.getSelectedDoc();
        for (Szenario szen : gdcoll.getSzenarios()) {
            if (szenarios != null && !szenarios.contains(szen)) {
                continue;
            }
            writeStartElement("szenario"); //<szenario>
            writeAttribute("hash", szen.getID());
            writeAttribute("titel", szen.getTitle());
            writeElement("description", szen.getDescription());
            // Informationen über Ansicht speichern
            GraphViewParameter graphViewParameter = Static.getGraphViewParameter(szen);
            writeStartElement("view"); //<view>
            writeElement("selected", selectedDoc == szen);
            writeElement("x", graphViewParameter.viewPositionX);
            writeElement("y", graphViewParameter.viewPositionY);
            writeElement("zoom", graphViewParameter.zoom);
            writeElement("degree", graphViewParameter.layerAngle);
            writeElement("shift", graphViewParameter.layerGap);
            writeElement("pageSizeFactor", szen.getPageSizeFactor());
            writeElement("activeLayer", gdcoll.getActiveLayer());
            writeElement("multiView", graphViewParameter.multiView);
            writeEndElement(); //</view>
            writeStartElement("mapping"); //"<mapping>"
            ElementsLayoutDefinition mapping = szen.getMapping();
            for (Class<? extends ModelElement> elementClass : getElementClassesWithStandardLayout(mapping)) {
                GraphElementLayout standardElementLayout = mapping.getStandardElementLayout(elementClass);
                writeDefaultGraphElementLayout(elementClass, standardElementLayout);
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
     * Schreibt die LayerContainer mit ihren enthaltenen ElementContainern der
     * Layer in die XML-Datei
     *
     * @param szen Teilmodell das geschrieben werden soll
     * @param elements ist diese Collection nicht <code>null</code>, werden nur
     *            die darin enthaltenen Elemente rausgeschrieben
     * @throws XMLStreamException
     */
    private void writeLayerContainer(final Szenario szen, final Collection<ModelElement> elements) throws XMLStreamException {
        for (LayerContainer lc : szen.getLayers()) {
            writeStartElement("layer"); //<layer>
            writeAttribute("number", lc.getLayerNumber());
            writeLayerLayout(lc.get3LGMLayout());
            for (NodeContainer kc : lc.getGraphNodeContainers()) {
                if (elements == null || elements.contains(kc.getElement())) {
                    writeElementContainer(kc);
                }
            }
            for (EdgeContainer kc : lc.getEdgeContainers()) {
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

    /**
     * @param ec
     * @throws XMLStreamException
     */
    protected void writeElementContainer(final ElementContainer ec) throws XMLStreamException {
        writeStartElement("container"); //<container>
        writeAttribute("hash", ec.getID());
        if (!(ec instanceof EdgeContainer)) {
            //write only if not default true
            writeElementIfFalse("expanded", ec.isExpanded());
            writeElementIfFalse("visible", ec.isVisible());
        }
        GraphElementLayout expandedLayout = ec.getE3LGMLayout();
        ModelElement me = ec.getElement();
        Class<? extends ModelElement> elementClass = me.getClass();
        GraphDocument doc = ec.getGraphDocument();
        ElementsLayoutDefinition layoutDefinition = doc.getMapping();
        GraphElementLayout defaultElementLayout = layoutDefinition.getStandardElementLayout(elementClass);
        if (expandedLayout != null) {
            writeGraphElementLayout(expandedLayout, defaultElementLayout, true);
        }
        GraphElementLayout nonExpandedLayout = ec.getNE3LGMLayout();
        if (nonExpandedLayout != null) {
            writeGraphElementLayout(nonExpandedLayout, defaultElementLayout, false);
        }
        writeEndElement(); //</container>
    }

    ////////////
    // Layout //
    ////////////

    /**
     * @param elementClass
     * @param layout
     * @throws XMLStreamException
     */
    private void writeDefaultGraphElementLayout(final Class<?> elementClass, final GraphElementLayout layout) throws XMLStreamException {
        writeGraphElementLayout(elementClass, layout, null, true);
    }

    /**
     * @param elementClass Elementklasse, für die das Layout geschrieben werden
     *            soll (wenn null, dann für Layer)
     * @param layout Layout zu diesr Elementklasse
     * @param expanded wenn <code>true</code>, wird das normale Layout
     *            geschrieben, wenn <code>false</code> das zusammengeklappte
     *            Layout
     * @throws XMLStreamException
     */
    private void writeGraphElementLayout(final GraphElementLayout layout, final GraphElementLayout defaultLayout, final boolean expanded) throws XMLStreamException {
        writeGraphElementLayout(null, layout, defaultLayout, expanded);
    }

    /**
     * @param elementClass Elementklasse, für die das Layout geschrieben werden
     *            soll (wenn null, dann für Layer)
     * @param layout Layout zu diesr Elementklasse
     * @param expanded wenn <code>true</code>, wird das normale Layout
     *            geschrieben, wenn <code>false</code> das zusammengeklappte
     *            Layout
     * @throws XMLStreamException
     */
    private void writeLayerLayout(final GraphElementLayout layout) throws XMLStreamException {
        writeGraphElementLayout(null, layout, null, true);
    }

    /**
     * @param elementClass Elementklasse, für die das Layout geschrieben werden
     *            soll (wenn null, dann für Layer)
     * @param layout Layout zu diesr Elementklasse
     * @param expanded wenn <code>true</code>, wird das normale Layout
     *            geschrieben, wenn <code>false</code> das zusammengeklappte
     *            Layout
     * @param defaultLayout if this is <code>null</code> this indictates that
     *            the layout parameter is an default layout for a type of
     *            elements in the mapping section of a submodel. In this case
     *            the elementClassParameter should never be <code>null</code>.
     *            If the defaultLayout parameter it is not <code>null</code> but
     *            different to the layout parameter this indicated that the
     *            layout parameter is the layout of a single element
     * @throws XMLStreamException
     */
    private void writeGraphElementLayout(final Class<?> elementClass, final GraphElementLayout layout, final GraphElementLayout defaultLayout, final boolean expanded) throws XMLStreamException {
        writeStartElement(expanded ? "layout" : "nelayout"); //<layout> oder <nelayout>

        boolean isDefaultElementClassLayout = elementClass != null && defaultLayout == null;
        boolean isLayerLayout = elementClass == null && defaultLayout == null;
        boolean writeFullLayout = isDefaultElementClassLayout || isLayerLayout;

        if (isDefaultElementClassLayout) { //in this case elementClass should be never null!
            writeAttribute("class", elementClass.getSimpleName());
        }
        if (writeFullLayout || defaultLayout.bg_color != layout.bg_color) {
            writeGraphElementLayoutColor(layout.bg_color, "bg_color");
        }
        if (writeFullLayout || defaultLayout.fg_color != layout.fg_color) {
            writeGraphElementLayoutColor(layout.fg_color, "fg_color");
        }
        if (writeFullLayout || defaultLayout.border_color != layout.border_color) {
            writeGraphElementLayoutColor(layout.border_color, "border_color");
        }

        if (isDefaultElementClassLayout && layout.line_thickness != STANDARD_LINE_THICKNESS) {
            writeElement("line_thickness", layout.line_thickness);
        }
        if (defaultLayout == null && layout.line_style != STANDARD_LINE_STYLE) {
            writeElement("line_style", layout.line_style);
        }
        if (layout.form != null) { //only the default element layouts have a form != null (at the moment)
            writeElement("form", layout.form.ordinal());
        }
        Font font = layout.getFont();
        if (isDefaultElementClassLayout && font != null || !isDefaultElementClassLayout && font != null && !font.equals(defaultLayout.getFont())) {
            writeElement("font_family", font.getName());
            writeElement("font_style", font.getStyle());
            writeElement("font_size", font.getSize());
        }
        if (layout.x != 0 || layout.y != 0) {
            writeElement("x", layout.x);
            writeElement("y", layout.y);
        }
        //        if (layout.width != -1 || !writeFullLayout && layout.width != defaultLayout.width) {
        //        if (layout.height != -1 || !writeFullLayout && layout.height != defaultLayout.height) {
        if (isDefaultElementClassLayout || !isLayerLayout && (layout.width != defaultLayout.width || layout.height != defaultLayout.height)) {
            writeElement("width", layout.width);
            writeElement("height", layout.height);
        }
        String icon = layout.getIconID();
        if (icon != null) {
            usedIconIDs.add(icon);
            writeElement("icon", icon);
        }
        if (layout.textPositionHorizontal != STANDARD_TEXT_POSITION_HORIZONTAL) {
            writeElement("halign", layout.textPositionHorizontal.name());
        }
        if (layout.textPositionVertical != STANDARD_TEXT_POSITION_VERTICAL) {
            writeElement("valign", layout.textPositionVertical.name());
        }
        if (layout.textAlignmentHTML != STANDARD_TEXT_ALIGNMENT_HTML) {
            writeElement("htmlalign", layout.textAlignmentHTML.name());
        }
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
     * Schreibt alle Icons in die XML-Datei, deren ID in den übergebenen iconIDs
     * vorkommen
     *
     * @param iconIDs
     * @throws XMLStreamException
     */
    private void writeImages(final Iterable<String> iconIDs) throws XMLStreamException {
        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        for (String iconID : iconIDs) {
            //nur die Icons in den XML-Stream schreiben, die von den exportierten Elementen auch genutzt werden
            if (usedIconIDs.contains(iconID)) {
                writeImage(iconID, iconTable.get(iconID));
            }
        }
    }

    /**
     * @param iconID
     * @param icon
     * @throws XMLStreamException
     */
    protected void writeImage(final String iconID, final byte[] icon) throws XMLStreamException {
        writeStartElement("bitmap"); //<bitmap>
        writeAttribute("type", "gif/base64");
        writeAttribute("hash", iconID);
        writeCharacters(Base64.encode(icon));
        writeEndElement(); //</bitmap>
    }

}
