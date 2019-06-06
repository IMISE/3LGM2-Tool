package de.imise.tool3lgm.imexport.linehandler;

import static de.imise.tool3lgm.imexport.linehandler.LineParser.EMPTY_STRING;

import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.imexport.DisplayableNameHandler;
import de.imise.tool3lgm.imexport.linehandler.line.AbstractImportLine;
import de.imise.tool3lgm.imexport.linehandler.line.EdgeHeaderLine;
import de.imise.tool3lgm.imexport.linehandler.line.EdgeLine;
import de.imise.tool3lgm.imexport.linehandler.line.NodeHeaderLine;
import de.imise.tool3lgm.imexport.linehandler.line.NodeLine;

/**
 * Creates {@link NodeLine}, {@link EdgeLine} {@link NodeHeaderLine} and {@link EdgeHeaderLine} from an input line string.
 *
 * @author
 * @create 06.10.2014
 */
public class ImportLineHandler {

    /** Column index for the element type */
    public static final int COLUMN_INDEX_ELEMENT_TYPE = 0;

    /** Column index for the name */
    public static final int COLUMN_INDEX_NAME = 1;

    /** Column index for the description */
    public static final int COLUMN_INDEX_DESCRIPTION = 2;

    /** Column index for the hash */
    public static final int COLUMN_INDEX_HASH = 3;

    /** Column index for the first user filed column in node lines */
    public static final int COLUMN_INDEX_NODE_USERFIELD_NAMES_START = 4;

    /** Column index for the hash of the start element in egde lines */
    public static final int COLUMN_INDEX_EDGE_START_HASH = 4;

    /** Column index for the hash of the end element in egde lines */
    public static final int COLUMN_INDEX_EDGE_END_HASH = 5;

    /** Column index for the first user filed column in edge lines */
    public static final int COLUMN_INDEX_EDGE_USERFIELD_NAMES_START = 6;

    /** Handler for resolving the value of an element type column to a model element class (subclass og {@link ModelElement}) */
    private final DisplayableNameHandler nameHandler;

    /** The last found header line ({@link NodeHeaderLine} or {@link EdgeHeaderLine}) */
    private AbstractImportLine lastHeader;

    /** Current line */
    private AbstractImportLine line;

    /** Line Tokenizer */
    private final LineParser parser = new LineParser();

    /**
     * @param metaModel
     */
    public ImportLineHandler(final MetaModel metaModel) {
        nameHandler = new DisplayableNameHandler(metaModel);
    }

    /**
     * Sets the current line.
     *
     * @param lineString
     * @param row
     */
    public void setLine(final String lineString, final int row) {
        parser.setLine(lineString);
        String elementType = parser.getColumn(COLUMN_INDEX_ELEMENT_TYPE);
        String name = parser.getColumn(COLUMN_INDEX_NAME);
        String description = parser.getColumn(COLUMN_INDEX_DESCRIPTION);
        String hash = parser.getColumn(COLUMN_INDEX_HASH);
        //Header line?
        if (!EMPTY_STRING.equals(elementType)) {
            //resolve the element type to a element class
            Class<? extends ModelElement> elementClass = nameHandler.getElementClass(elementType);
            //element class is a subclass of Edge
            if (MetaModel.isEdgeType(elementClass)) {
                String startHash = parser.getColumn(COLUMN_INDEX_EDGE_START_HASH);
                String endHash = parser.getColumn(COLUMN_INDEX_EDGE_END_HASH);
                List<String> userFieldNames = parser.getColumns(COLUMN_INDEX_EDGE_USERFIELD_NAMES_START);
                line = new EdgeHeaderLine(elementClass, elementType, name, description, hash, startHash, endHash, userFieldNames, row);
                //element class not found or element class is a subclass of Node
            } else if (elementClass == null || MetaModel.isNodeType(elementClass)) {
                List<String> userFieldNames = parser.getColumns(COLUMN_INDEX_NODE_USERFIELD_NAMES_START);
                line = new NodeHeaderLine(elementClass, elementType, name, description, hash, userFieldNames, row);
            }
            //store the header as lastHeader
            lastHeader = line;
            // not a header line
        } else {
            //the last found header is a EdgeLineHeader
            if (lastHeader != null && lastHeader instanceof EdgeHeaderLine) {
                String startHash = parser.getColumn(COLUMN_INDEX_EDGE_START_HASH);
                String endHash = parser.getColumn(COLUMN_INDEX_EDGE_END_HASH);
                List<String> userFields = parser.getColumns(COLUMN_INDEX_EDGE_USERFIELD_NAMES_START);
                line = new EdgeLine((EdgeHeaderLine) lastHeader, name, description, hash, startHash, endHash, userFields, row);
                //no header or a NodeLineHeader is set as thelast header
            } else if (lastHeader == null || lastHeader instanceof NodeHeaderLine) {
                List<String> userFields = parser.getColumns(COLUMN_INDEX_NODE_USERFIELD_NAMES_START);
                line = new NodeLine((NodeHeaderLine) lastHeader, name, description, hash, userFields, row);
            }
        }
    }

    /**
     * @return current line
     */
    public AbstractImportLine getLine() {
        return line;
    }

    /**
     * @return <code>true</code> if this line can be interpreted as a {@link NodeLine}, {@link EdgeLine}, {@link NodeHeaderLine} or
     *         {@link EdgeHeaderLine} otherwise <code>false</code>
     */
    public boolean isLine() {
        return line != null;
    }

    /**
     * @return <code>true</code> if the current line represents a {@link EdgeHeaderLine}
     */
    public boolean isEdgeHeaderLine() {
        return isLine() && line instanceof EdgeHeaderLine;
    }

    /**
     * @return <code>true</code> if the current line represents a {@link EdgeLine}
     */
    public boolean isEdgeLine() {
        return isLine() && line instanceof EdgeLine;
    }

    /**
     * @return <code>true</code> if the current line represents a {@link NodeHeaderLine}
     */
    public boolean isNodeHeaderLine() {
        return isLine() && line instanceof NodeHeaderLine;
    }

    /**
     * @return <code>true</code> if the current line represents a {@link NodeLine}
     */
    public boolean isNodeLine() {
        return isLine() && line instanceof NodeLine;
    }

    /**
     * @return <code>true</code> if the current line is a header or at least one line before the current lien was a header
     */
    public boolean hasHeader() {
        return lastHeader != null;
    }

    /**
     * @return return <code>null</code> if the current line is not a {@link NodeLine}. If it is a NodeLine it will be returned.
     */
    public NodeLine getNodeLine() {
        if (isNodeLine()) {
            return (NodeLine) line;
        }
        return null;
    }

    /**
     * @return return <code>null</code> if the current line is not a {@link EdgeLine}. If it is a EdgeLine it will be returned.
     */
    public EdgeLine getEdgeLine() {
        if (isEdgeLine()) {
            return (EdgeLine) line;
        }
        return null;
    }

    /**
     * @return return <code>null</code> if the current line is not a {@link NodeHeaderLine}. If it is a NodeHeaderLine it will be returned.
     */
    public NodeHeaderLine getNodeHeaderLine() {
        if (isNodeHeaderLine()) {
            return (NodeHeaderLine) line;
        }
        return null;
    }

    /**
     * @return return <code>null</code> if the current line is not a {@link EdgeHeaderLine}. If it is a EdgeHeaderLine it will be returned.
     */
    public EdgeHeaderLine getEdgeHeaderLine() {
        if (isEdgeHeaderLine()) {
            return (EdgeHeaderLine) line;
        }
        return null;
    }

}
