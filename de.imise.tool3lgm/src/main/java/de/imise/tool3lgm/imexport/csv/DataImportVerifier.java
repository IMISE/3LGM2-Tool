package de.imise.tool3lgm.imexport.csv;

import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.FILE_ERROR;
import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.HEADER_MISSING;
import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.HEADER_UNKNOWN_ELEMENT_TYPE;
import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.ID_CONFLICT;
import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.ID_DUPLICATE;
import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.NODE_OR_EDGE_EMPTY_NAME;
import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.UNKNOWN_LINE;
import static de.imise.tool3lgm.imexport.csv.ImportError.ErrorType.USERFIELD_MISSING;
import static de.imise.tool3lgm.imexport.csv.linehandler.ImportLineHandler.COLUMN_INDEX_EDGE_USERFIELD_NAMES_START;
import static de.imise.tool3lgm.imexport.csv.linehandler.ImportLineHandler.COLUMN_INDEX_ELEMENT_TYPE;
import static de.imise.tool3lgm.imexport.csv.linehandler.ImportLineHandler.COLUMN_INDEX_ID;
import static de.imise.tool3lgm.imexport.csv.linehandler.ImportLineHandler.COLUMN_INDEX_NAME;
import static de.imise.tool3lgm.imexport.csv.linehandler.ImportLineHandler.COLUMN_INDEX_NODE_USERFIELD_NAMES_START;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;
import de.imise.tool3lgm.imexport.csv.linehandler.ImportLineHandler;
import de.imise.tool3lgm.imexport.csv.linehandler.line.AbstractImportLine;
import de.imise.tool3lgm.imexport.csv.linehandler.line.EdgeHeaderLine;
import de.imise.tool3lgm.imexport.csv.linehandler.line.EdgeLine;
import de.imise.tool3lgm.imexport.csv.linehandler.line.NodeHeaderLine;
import de.imise.tool3lgm.imexport.csv.linehandler.line.NodeLine;

/**
 * Class to verify a import file. The main result is a
 * {@link ImportErrorConfiguration} which contains all {@link ImportError} for
 * the file.
 *
 * @author AXS
 * @create 06.10.2014
 */
public class DataImportVerifier {

    /** The GDCollection in wich the file should be imported. */
    private final GDCollection gdcoll;

    /** File to import */
    private final File importFile;

    /** The result errror configuration */
    private final ImportErrorConfiguration errors = new ImportErrorConfiguration();

    /**
     * @param gdcoll
     * @param importFile
     */
    public DataImportVerifier(final GDCollection gdcoll, final File importFile) {
        this.gdcoll = gdcoll;
        this.importFile = importFile;
    }

    /**
     * @param gdcoll
     * @param importFile
     * @return
     */
    public ImportErrorConfiguration getErrors() {
        BufferedReader reader = null;
        try {
            FileInputStream istream = new FileInputStream(importFile);
            reader = new BufferedReader(new InputStreamReader(istream));
            ImportLineHandler lineHandler = new ImportLineHandler(gdcoll.getMetaModel());
            ImportIDConfiguration importIDConfiguration = new ImportIDConfiguration();
            String line = null;
            int row = -1;
            while ((line = reader.readLine()) != null) {
                row++;
                //um im Fehlerfall den richtigen Zeilenindex bestimmen zu können
                //-> für jede Zeile wenigsten einen Platzhalter hinzufügen. Wenn wirklich
                //ein IDString in der Zeile steht, wird das null unten durch diesen
                //ersetzt. Dies ist nur bei NodeLine und EdgeLine der Fall
                importIDConfiguration.addDefaultID();
                if (line.trim().isEmpty()) {
                    continue;
                }
                lineHandler.setLine(line, row);
                if (lineHandler.isNodeLine()) {
                    addNodeLineErrors(lineHandler.getNodeLine(), importIDConfiguration);
                } else if (lineHandler.isEdgeLine()) {
                    addEdgeLineErrors(lineHandler.getEdgeLine(), importIDConfiguration);
                } else if (lineHandler.isNodeHeaderLine()) {
                    addNodeHeaderLineErrors(lineHandler.getNodeHeaderLine());
                } else if (lineHandler.isEdgeHeaderLine()) {
                    addEdgeHeaderLineErrors(lineHandler.getEdgeHeaderLine());
                } else {
                    errors.add(lineHandler.getLine(), 0, UNKNOWN_LINE);
                }

            }
            reader.close();
        } catch (Exception ex) {
            errors.add(null, -1, FILE_ERROR, ex.getLocalizedMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return errors;
    }

    /**
     * Fehler 1: Die Zeile hat keinen Header (also ist die Elementart nicht
     * klar)<br>
     * Fehler 2: Der Name des Knotens ist leer<br>
     * Fehler 3: ID mehrfach in der Datei vergeben<br>
     * Fehler 4: ID wird im Modell bereits für ein Element einer anderen Art
     * verwendet
     *
     * @param nodeLine
     * @param importIDConfiguration
     */
    private void addNodeLineErrors(final NodeLine nodeLine, final ImportIDConfiguration importIDConfiguration) {
        //gefundene Line hat keinen Header
        if (nodeLine.getHeaderLine() == null) {
            errors.add(nodeLine, COLUMN_INDEX_ELEMENT_TYPE, HEADER_MISSING);
        }
        //Node line always needs a valid name
        String name = nodeLine.getName();
        if (name == null || name.isEmpty()) {
            errors.add(nodeLine, COLUMN_INDEX_NAME, NODE_OR_EDGE_EMPTY_NAME);
        }
        addIDError(nodeLine, importIDConfiguration);
    }

    /**
     * Fehler 1: Der Name der Edge ist leer<br>
     * Fehler 2: ID mehrfach in der Datei vergeben<br>
     * Fehler 3: ID wird im Modell bereits für ein Element einer anderen Art
     * verwendet<br>
     * Fehler 4: Das Element, das die StartID angibt, existiert nicht<br>
     * Fehler 5: Das Element, das die StartID angibt, passt nicht zur Edge<br>
     * Fehler 6: Das Element, das die EndID angibt, existiert nicht<br>
     * Fehler 7: Das Element, das die EndID angibt, passt nicht zur Edge
     *
     * @param edgeLine
     * @param importIDConfiguration
     */
    private void addEdgeLineErrors(final EdgeLine edgeLine, final ImportIDConfiguration importIDConfiguration) {
        //Edge lines always needs a valid name
        String name = edgeLine.getName();
        if (name == null || name.isEmpty()) {
            errors.add(edgeLine, COLUMN_INDEX_NAME, NODE_OR_EDGE_EMPTY_NAME);
        }
        addIDError(edgeLine, importIDConfiguration);
        addStartIDError(edgeLine, importIDConfiguration);
        addEndIDError(edgeLine, importIDConfiguration);
    }

    /**
     * Fehler 1: In der HeaderLine ist eine Elementklasse angegeben, die nicht
     * aufgelöst werden kann.<br>
     * Fehler 2: Für die aktuelle Elementklasse müssen alle Userfields definiert
     * sein, deren Namen in der übergebenen HeaderLine stehen.
     *
     * @param nodeHeaderLine
     */
    private void addNodeHeaderLineErrors(final NodeHeaderLine nodeHeaderLine) {
        Class<? extends ModelElement> elementClass = nodeHeaderLine.getElementClass();
        String elementClassName = nodeHeaderLine.getElementType();

        //Header bei dem der Name nicht zu einer gültigen Elementklasse aufgelöst werden konnte
        if (elementClass == null) {
            errors.add(nodeHeaderLine, COLUMN_INDEX_ELEMENT_TYPE, HEADER_UNKNOWN_ELEMENT_TYPE, "\"" + elementClassName + "\"");
        }
        addHeaderUserFieldErrors(nodeHeaderLine, COLUMN_INDEX_NODE_USERFIELD_NAMES_START);
    }

    /**
     * Fehler: Für die aktuelle Elementklasse müssen alle Userfields definiert
     * sein, deren Namen in der übergebenen HeaderLine stehen.
     *
     * @param edgeHeaderLine
     */
    private void addEdgeHeaderLineErrors(final EdgeHeaderLine edgeHeaderLine) {
        addHeaderUserFieldErrors(edgeHeaderLine, COLUMN_INDEX_EDGE_USERFIELD_NAMES_START);
    }

    /**
     * Fehler: Für die aktuelle Elementklasse müssen alle Userfields definiert
     * sein, deren Namen in der übergebenen HeaderLine stehen.
     *
     * @param line
     * @param userFieldColumnStartIndex
     */
    private void addHeaderUserFieldErrors(final AbstractImportLine line, final int userFieldColumnStartIndex) {
        Class<? extends ModelElement> elementClass = line.getElementClass();
        String elementClassName = line.getElementType();
        UserFieldDefinitions definitions = gdcoll.getUserFieldDefinitions();
        List<String> userFieldNames = line.getUserFields();
        for (int i = 0; i < userFieldNames.size(); i++) {
            String userFieldName = userFieldNames.get(i);
            UserField userField = definitions.getUserField(elementClass, userFieldName);
            if (userField == null) {
                int realColumnIndex = userFieldColumnStartIndex + i;
                errors.add(line, realColumnIndex, USERFIELD_MISSING, userFieldName, elementClassName);
            }
        }
    }

    /**
     * Fehler 1: ID mehrfach in der Datei vergeben<br>
     * Fehler 2: ID wird im Modell bereits für ein Element einer anderen Art
     * verwendet.
     *
     * @param line
     * @param importIDConfiguration
     */
    private void addIDError(final AbstractImportLine line, final ImportIDConfiguration importIDConfiguration) {
        String id = line.getID();
        if (!id.isEmpty()) {
            //den DefaultID durch die gefundene ID ersetzen
            int index = importIDConfiguration.indexOf(id);
            importIDConfiguration.setLastID(id);
            Class<? extends ModelElement> elementClass = line.getElementClass();
            importIDConfiguration.put(id, elementClass);
            //die angegebene ID war bereits in der Liste
            if (index >= 0) {
                errors.add(line, COLUMN_INDEX_ID, ID_DUPLICATE, id, "" + index);
            }
            //prüfen, ob im Modell ein Element einer anderen Art vorkommt, das dienselbe ID hat
            ModelElement me = gdcoll.getMainDoc().findElementCoded(id);
            if (me != null && me.getClass() != elementClass) {
                ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
                String displayableName = elementsNameBuilder.getDisplayableName(me.getClass());
                errors.add(line, COLUMN_INDEX_ID, ID_CONFLICT, displayableName, me.getName(), id);
            }
        }
    }

    /**
     * Fehler 1: Das Element, das der StartID angibt, existiert nicht. Fehler 2:
     * Das Element, das der StartID angibt, passt nicht zur Edge.
     *
     * @param edgeLine
     * @param importIDConfiguration
     */
    private void addStartIDError(final EdgeLine edgeLine, final ImportIDConfiguration importIDConfiguration) {
        //TODO
    }

    /**
     * Fehler 1: Das Element, das der EndID angibt, existiert nicht.<br>
     * Fehler 2: Das Element, das der EndID angibt, passt nicht zur Edge.
     *
     * @param edgeLine
     * @param importIDConfiguration
     */
    private void addEndIDError(final EdgeLine edgeLine, final ImportIDConfiguration importIDConfiguration) {
        //TODO
    }

}
