package de.imise.tool3lgm.imexport;

import static de.imise.tool3lgm.imexport.ImportError.ErrorType.FILE_ERROR;
import static de.imise.tool3lgm.imexport.ImportError.ErrorType.HASH_CONFLICT;
import static de.imise.tool3lgm.imexport.ImportError.ErrorType.HASH_DUPLICATE;
import static de.imise.tool3lgm.imexport.ImportError.ErrorType.HEADER_MISSING;
import static de.imise.tool3lgm.imexport.ImportError.ErrorType.HEADER_UNKNOWN_ELEMENT_TYPE;
import static de.imise.tool3lgm.imexport.ImportError.ErrorType.NODE_OR_EDGE_EMPTY_NAME;
import static de.imise.tool3lgm.imexport.ImportError.ErrorType.UNKNOWN_LINE;
import static de.imise.tool3lgm.imexport.ImportError.ErrorType.USERFIELD_MISSING;
import static de.imise.tool3lgm.imexport.linehandler.ImportLineHandler.COLUMN_INDEX_EDGE_USERFIELD_NAMES_START;
import static de.imise.tool3lgm.imexport.linehandler.ImportLineHandler.COLUMN_INDEX_ELEMENT_TYPE;
import static de.imise.tool3lgm.imexport.linehandler.ImportLineHandler.COLUMN_INDEX_HASH;
import static de.imise.tool3lgm.imexport.linehandler.ImportLineHandler.COLUMN_INDEX_NAME;
import static de.imise.tool3lgm.imexport.linehandler.ImportLineHandler.COLUMN_INDEX_NODE_USERFIELD_NAMES_START;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.imexport.linehandler.ImportLineHandler;
import de.imise.tool3lgm.imexport.linehandler.line.AbstractImportLine;
import de.imise.tool3lgm.imexport.linehandler.line.EdgeHeaderLine;
import de.imise.tool3lgm.imexport.linehandler.line.EdgeLine;
import de.imise.tool3lgm.imexport.linehandler.line.NodeHeaderLine;
import de.imise.tool3lgm.imexport.linehandler.line.NodeLine;

/**
 * Class to verify a import file. The main result is a {@link ImportErrorConfiguration} which contains all {@link ImportError} for the file.
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

    public DataImportVerifier(final GDCollection gdcoll, final File importFile) {
        super();
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
            ImportLineHandler lineHandler = new ImportLineHandler();
            ImportHashConfiguration importHashConfiguration = new ImportHashConfiguration();
            String line = null;
            int row = -1;
            while ((line = reader.readLine()) != null) {
                row++;
                //um im Fehlerfall den richtigen Zeilenindex bestimmen zu können
                //-> für jede Zeile wenigsten einen Platzhalter hinzufügen. Wenn wirklich
                //ein HashString in der Zeile steht, wird das null unten durch diesen
                //ersetzt. Dies ist nur bei NodeLine und EdgeLine der Fall
                importHashConfiguration.addDefaultHash();
                if (line.trim().isEmpty()) {
                    continue;
                }
                lineHandler.setLine(line, row);
                if (lineHandler.isNodeLine()) {
                    addNodeLineErrors(lineHandler.getNodeLine(), importHashConfiguration);
                } else if (lineHandler.isEdgeLine()) {
                    addEdgeLineErrors(lineHandler.getEdgeLine(), importHashConfiguration);
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
     * Fehler 1: Die Zeile hat keinen Header (also ist die Elementart nicht klar)
     * Fehler 2: Der Name des Knotens ist leer
     * Fehler 3: Hash mehrfach in der Datei vergeben
     * Fehler 4: Hash wird im Modell bereits für ein Element einer anderen Art verwendet
     * 
     * @param nodeLine
     * @param importHashConfiguration
     */
    private void addNodeLineErrors(final NodeLine nodeLine, final ImportHashConfiguration importHashConfiguration) {
        //gefundene Line hat keinen Header
        if (nodeLine.getHeaderLine() == null) {
            errors.add(nodeLine, COLUMN_INDEX_ELEMENT_TYPE, HEADER_MISSING);
        }
        //Node line always needs a valid name 
        String name = nodeLine.getName();
        if (name == null || name.isEmpty()) {
            errors.add(nodeLine, COLUMN_INDEX_NAME, NODE_OR_EDGE_EMPTY_NAME);
        }
        addHashError(nodeLine, importHashConfiguration);
    }

    /**
     * Fehler 1: Der Name der Kante ist leer
     * Fehler 2: Hash mehrfach in der Datei vergeben
     * Fehler 3: Hash wird im Modell bereits für ein Element einer anderen Art verwendet
     * Fehler 4: Das Element, das der Starthash angibt, existiert nicht
     * Fehler 5: Das Element, das der Starthash angibt, passt nicht zur Kante
     * Fehler 6: Das Element, das der Endhash angibt, existiert nicht
     * Fehler 7: Das Element, das der Endhash angibt, passt nicht zur Kante
     * 
     * @param edgeLine
     * @param importHashConfiguration
     */
    private void addEdgeLineErrors(final EdgeLine edgeLine, final ImportHashConfiguration importHashConfiguration) {
        //Edge lines always needs a valid name 
        String name = edgeLine.getName();
        if (name == null || name.isEmpty()) {
            errors.add(edgeLine, COLUMN_INDEX_NAME, NODE_OR_EDGE_EMPTY_NAME);
        }
        addHashError(edgeLine, importHashConfiguration);
        addStartHashError(edgeLine, importHashConfiguration);
        addEndHashError(edgeLine, importHashConfiguration);
    }

    /**
     * Fehler 1: In der HeaderLine ist eine Elementklasse angegeben, die nicht aufgelöst werden kann.
     * Fehler 2: Für die aktuelle Elementklasse müssen alle Userfields definiert sein, deren Namen in der übergebenen HeaderLine stehen.
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
     * Fehler: Für die aktuelle Elementklasse müssen alle Userfields definiert sein, deren Namen in der übergebenen HeaderLine stehen.
     * 
     * @param edgeHeaderLine
     */
    private void addEdgeHeaderLineErrors(final EdgeHeaderLine edgeHeaderLine) {
        addHeaderUserFieldErrors(edgeHeaderLine, COLUMN_INDEX_EDGE_USERFIELD_NAMES_START);
    }

    /**
     * Fehler: Für die aktuelle Elementklasse müssen alle Userfields definiert sein, deren Namen in der übergebenen HeaderLine stehen.
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
     * Fehler 1: Hash mehrfach in der Datei vergeben
     * Fehler 2: Hash wird im Modell bereits für ein Element einer anderen Art verwendet.
     * 
     * @param line
     * @param importHashConfiguration
     */
    private void addHashError(final AbstractImportLine line, final ImportHashConfiguration importHashConfiguration) {
        String hash = line.getHash();
        if (!hash.isEmpty()) {
            //den DefaultHash durch den gefundenen Hash ersetzen
            int index = importHashConfiguration.indexOf(hash);
            importHashConfiguration.setLastHash(hash);
            Class<? extends ModelElement> elementClass = line.getElementClass();
            importHashConfiguration.put(hash, elementClass);
            //der angegebene Hash war bereits in der Liste
            if (index >= 0) {
                errors.add(line, COLUMN_INDEX_HASH, HASH_DUPLICATE, hash, "" + index);
            }
            //prüfen, ob im Modell ein Element einer anderen Art vorkommt, das denselben Hash hat
            ModelElement me = gdcoll.getMainGraphDocument().findElementCoded(hash);
            if (me != null && me.getClass() != elementClass) {
                String displayableName = ModelConstants.getDisplayableName(me.getClass());
                errors.add(line, COLUMN_INDEX_HASH, HASH_CONFLICT, displayableName, me.getName(), hash);
            }
        }
    }

    /**
     * Fehler 1: Das Element, das der Starthash angibt, existiert nicht.
     * Fehler 2: Das Element, das der Starthash angibt, passt nicht zur Kante.
     * 
     * @param edgeLine
     * @param importHashConfiguration
     */
    private void addStartHashError(final EdgeLine edgeLine, final ImportHashConfiguration importHashConfiguration) {
        //TODO
    }

    /**
     * Fehler 1: Das Element, das der Endhash angibt, existiert nicht.
     * Fehler 2: Das Element, das der Endhash angibt, passt nicht zur Kante.
     * 
     * @param edgeLine
     * @param importHashConfiguration
     */
    private void addEndHashError(final EdgeLine edgeLine, final ImportHashConfiguration importHashConfiguration) {
        //TODO
    }

}
