package de.imise.tool3lgm.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.log.Log;

public class ToolXMLClipboardWriter extends ToolXMLWriter {

    final GraphDocument selectedDoc;

    private ToolXMLClipboardWriter(final GraphDocument selectedDoc) throws XMLStreamException, FactoryConfigurationError, IOException {
        super(selectedDoc.getCollection(), new File(Tool3lgmConstants.getClipboardPath()), false);
        this.selectedDoc = selectedDoc;
    }

    public static boolean writeClipboard(final GraphDocument selectedDoc) {
        try {
            ToolXMLClipboardWriter clipboardWriter = new ToolXMLClipboardWriter(selectedDoc);
            clipboardWriter.writeClipboardContent();
            clipboardWriter.finish();
        } catch (Exception e) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
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
        List<ModelElement> copyElements = new ArrayList<>();
        Set<UserField> userFields = new HashSet<>();
        LGMGraphDocument lgmDoc = (LGMGraphDocument) selectedDoc;
        List<ElementContainer> sortedSelection = lgmDoc.getSortedSelection();
        gdcoll.resolveCopyDependencies(sortedSelection, copyElements, userFields);

        writeStartDocument();
        writeStartElement("tool3lgm_clipboard"); //<tool3lgm_clipboard>
        writeUserFieldDefinitions(userFields);
        writeStartElement("userFieldDefinitions"); //<userFieldDefinitions>
        for (UserField uf : userFields) {
            writeUserField(uf);
        }
        writeEndElement(); //</userFieldDefinitions>
        writeStartElement("objects"); //<objects>
        for (ModelElement me : copyElements) {
            if (me.avoidDuplicates()) {
                writeStartElement("avoidDuplicates"); //<avoidDuplicates>
                writeModelElement(me);
                writeEndElement(); //</avoidDuplicates>
            } else {
                writeModelElement(me);
            }
        }
        writeEndElement(); //</objects>
        writeStartElement("szenario"); //<szenario>
        HashSet<String> icons = new HashSet<>();
        for (ModelElement me : copyElements) {
            if (me.isUnique()) {
                continue;
            }
            ElementContainer ec = me.getContainer(selectedDoc);
            if (ec == null) {
                continue;
            }
            writeElementContainer(ec);
            GraphElementLayout layout = ec.get3LGMLayout();
            if (layout != null && layout.icon != null) {
                icons.add(layout.icon);
            }
        }
        writeEndElement(); //</szenario>
        writeStartElement("images"); //<images>
        Map<String, byte[]> iconTable = gdcoll.getIconTable();
        for (String iconHashString : icons) {
            writeImage(iconHashString, iconTable.get(iconHashString));
        }
        writeEndElement(); //</images>
        writeEndElement(); //</tool3lgm_clipboard>
    }
}
