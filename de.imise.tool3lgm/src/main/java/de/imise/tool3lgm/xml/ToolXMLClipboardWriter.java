package de.imise.tool3lgm.xml;

import static de.imise.tool3lgm.Tool3lgmConstants.CLIPBOARD_PATH;
import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.CopyDependencyResolver.resolveCopyDependencies;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.CopyDependencyResolver.CopyDependencyResolverResultSimple;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.SortedSelection;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.log.Log;

public class ToolXMLClipboardWriter extends ToolXMLWriter {

    final GraphDocument selectedDoc;

    private ToolXMLClipboardWriter(final GraphDocument selectedDoc) throws XMLStreamException, FactoryConfigurationError, IOException {
        super(selectedDoc.getCollection(), CLIPBOARD_PATH);
        this.selectedDoc = selectedDoc;
    }

    public static boolean writeClipboard(final GraphDocument selectedDoc) {
        try {
            ToolXMLClipboardWriter clipboardWriter = new ToolXMLClipboardWriter(selectedDoc);
            clipboardWriter.writeClipboardContent();
            clipboardWriter.finish();
        } catch (Exception e) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), e);
            return false;
        }
        return true;
    }

    /**
     * @param masterTag
     * @return
     * @throws XMLStreamException
     */
    private final void writeClipboardContent() throws XMLStreamException {
        LGMGraphDocument lgmDoc = (LGMGraphDocument) selectedDoc;
        SortedSelection sortedSelection = lgmDoc.getSortedSelection();
        CopyDependencyResolverResultSimple resolvedCopyDependencies = resolveCopyDependencies(sortedSelection);

        writeStartDocument();
        writeStartElement("tool3lgm_clipboard"); //<tool3lgm_clipboard>
        writeAttribute("time", String.valueOf(System.currentTimeMillis()));
        writeUserFieldDefinitions(resolvedCopyDependencies.userFieldNumberFormats, resolvedCopyDependencies.userFields);
        writeStartElement("objects"); //<objects>
        MetaModel metaModel = gdcoll.getMetaModel();
        for (ModelElement me : resolvedCopyDependencies.elements) {
            Class<? extends ModelElement> elementClass = me.getClass();
            if (metaModel.avoidDuplicates(elementClass) && !sortedSelection.contains(me) || !Static.isExpertMode() && metaModel.isPureTemplateElementClass(elementClass)) {
                writeStartElement("avoidDuplicates"); //<avoidDuplicates>
                writeModelElement(me);
                writeEndElement(); //</avoidDuplicates>
            } else {
                writeModelElement(me);
            }
        }
        for (Edge edge : resolvedCopyDependencies.additionalEdges) {
            writeModelElement(edge);
        }
        writeEndElement(); //</objects>
        writeStartElement("szenario"); //<szenario>
        Set<String> iconIDs = new HashSet<>();
        int lastLayer = -1;
        for (ModelElement me : resolvedCopyDependencies.elements) {
            if (me.isUnique()) {
                continue;
            }
            ElementContainer ec = me.getContainer(selectedDoc);
            if (ec == null) {
                continue;
            }
            int layer = me.layerFor();
            if (layer != lastLayer) {
                if (lastLayer >= 0) {
                    writeEndElement(); //</layer>
                }
                writeStartElement("layer"); //<layer>
                writeAttribute("number", layer);
                lastLayer = layer;
            }
            writeElementContainer(ec);
            GraphElementLayout layout = ec.get3LGMLayout();
            if (layout != null) {
                String iconID = layout.getIconID();
                if (iconID != null) {
                    iconIDs.add(iconID);
                }
            }
        }
        if (lastLayer >= 0) {
            writeEndElement(); //</layer>
        }
        writeEndElement(); //</szenario>
        writeStartElement("images"); //<images>
        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        for (String iconID : iconIDs) {
            writeImage(iconID, iconTable.get(iconID));
        }
        writeEndElement(); //</images>
        writeEndElement(); //</tool3lgm_clipboard>
    }

}
