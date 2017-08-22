package de.imise.tool3lgm.graphtools.matrixview;

import static de.imise.tool3lgm.graphtools.elements.Edge.NOTCONNECTED;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.Node;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.log.Log;

/**
 * Beschreibt und verwaltet die Daten einer Matrixsicht über eine (Teil-) Modell
 *
 * @author Thomas Rudert, AXS (23.10.07)
 */
public class TableModel implements Iterable<TableCell> {

    /**
     * ArrayList mit den ModelElementen für die Spalten. <br>
     */
    private List<ModelElement> colHeader;

    /** ArrayList mit den ModelElementen für die Zeilen */
    private List<ModelElement> rowHeader;

    private Set<TableCell> cellsSet = null;

    /** das zugehörige (Teil-)Modell */
    private final GraphDocument graphDocument;

    /** Zeilenklasse */
    private Class<? extends ModelElement> rowClass;

    /** Spaltenklasse */
    private Class<? extends ModelElement> colClass;

    /** MetaPfad über den Zeilen- und Spaltenklasse verbunden sein sollen */
    private MetaPath metaPath;

    /**
     * Legt fest, ob nur absolte Teilelemente angezeigt werden sollen
     */
    private boolean absolutePartsOnly = false;

    /**
     * Legt ein neues <code>TableModel</code> an, das erstmal gar nichts darstellt, sondern nur das <code>GraphDocument</code> kennt, aus dem nach dem
     * setzen von gültigen Zeilen und Spaltenklassen die Elementverknüpfungen anzeigen soll. Konstruktor
     *
     * @param graphDocument das (Teil-)Modell
     */
    public TableModel(final GraphDocument graphDocument) {
        this(graphDocument, null, null, null, false);
    }

    /**
     * Legt ein neues <code>TableModel</code> an, das in den Zeilen alle Elemente der Klasse <code>rowElementClass</code> und in den Spalten alle
     * Elemente der Klasse <code>colElementClass</code> sowie alle ihre Verbindungen darstellt, wenn die beiden
     * Klassen gültige Elementklassen sind. Sind sie ungültig, wird nichts dargestellt.
     *
     * @param graphDocument das (Teil-)Modell
     * @param rowClass Zeilenklasse
     * @param colClass Spaltenklasse
     * @param metaPath MetaPfad über den Zeilen- und Spaltenklasse verbunden sein sollen
     * @param absolutePartsOnly legt fest, ob in der Matrix nur Elemente auftauchen sollen, die im Gesamtmodell keine Teilelemente besitzen
     */
    public TableModel(final GraphDocument graphDocument, final Class<? extends ModelElement> rowClass, final Class<? extends ModelElement> colClass, final MetaPath metaPath, final boolean absolutePartsOnly) {
        this.graphDocument = graphDocument;
        fillTableModel(rowClass, colClass, metaPath, absolutePartsOnly);
    }

    /**
     * @param rowClass Zeilenklasse
     * @param colClass Spaltenklasse
     * @param metaPath MetaPfad über den Zeilen- und Spaltenklasse verbunden sein sollen
     * @param absolutePartsOnly legt fest, ob in der Matrix nur Elemente auftauchen sollen, die im Gesamtmodell keine Teilelemente besitzen
     */
    public void fillTableModel(final Class<? extends ModelElement> rowClass, final Class<? extends ModelElement> colClass, final MetaPath metaPath, final boolean absolutePartsOnly) {
        this.rowClass = rowClass;
        this.colClass = colClass;
        this.metaPath = metaPath;
        this.absolutePartsOnly = absolutePartsOnly;
        update();
    }

    /**
     * gibt ArrayListe mit den String für die Zeilenüberschriften zurück
     *
     * @return ArrayList mit Strings der Zeilenüberschriften
     */
    public List<ModelElement> getRowHeaders() {
        return rowHeader;
    }

    /**
     * gibt ArrayListe mit den Strings für die Spaltenüberschriften zurück
     *
     * @return ArrayList mit STrings der Spaltenüberschriften
     */
    public List<ModelElement> getColHeaders() {
        return colHeader;
    }

    /**
     * @return {@link Node} für Zeilenelement
     */
    public Node getRowKnot(final int rowIndex) {
        return rowIndex < rowHeader.size() ? (Node) rowHeader.get(rowIndex) : null;
    }

    /**
     * @return {@link Node} für Spaltenelement
     */
    public Node getColKnot(final int colIndex) {
        return colIndex < colHeader.size() ? (Node) colHeader.get(colIndex) : null;
    }

    /**
     * @return
     */
    public int getNumberOfCols() {
        return colHeader.size();
    }

    /**
     * @return
     */
    public int getNumberOfRows() {
        return rowHeader.size();
    }

    @Override
    public Iterator<TableCell> iterator() {
        return cellsSet.iterator();
    }

    /**
     * Aktualisiert das Model der Tabelle
     */
    public void update() {
        if (rowClass != null && colClass != null) {
            rowHeader = graphDocument.getModelItems(rowClass, true, absolutePartsOnly, true);

            colHeader = graphDocument.getModelItems(colClass, true, absolutePartsOnly, true);

            updateAllCellEntries();
        }
    }

    /**
     * Liefert <code>true</code>, wenn gültige Elementklassen und ein gültiger MetaPfad (jeweils ungleich <code>null</code>) gesetzt sind.
     *
     * @return <code>true</code>, wenn gültige Klassen ein gültiger Metapfad gesetzt sind, sonst <code>false</code>
     */
    public boolean isValid() {
        if (rowClass == null || colClass == null || metaPath == null) {
            return false;
        }
        return true;
    }

    /**
     * löscht alle Zelleneinträge aus dem Tabellenmodell und setzt diese mit den aktuelle Überschriften neu
     */
    private void updateAllCellEntries() {

        cellsSet = new HashSet<>(colHeader.size() * rowHeader.size());

        if (metaPath == null) {
            return;
        }

        if (colHeader.size() * rowHeader.size() == 0) {
            return;
        }

        //die Verbindungen in der Matrixsicht werden immer im Gesamtmodell gesucht
        //GraphDocument doc = graphDocument.getCollection().getGraphDocument();

        int connected;
        for (int i = 0; i < rowHeader.size(); i++) {
            for (int j = 0; j < colHeader.size(); j++) {
                try {
                    // connected = PathFinder.isConnected(rowHeader.get(i), colHeader.get(j), metaPath, doc);
                    connected = PathFinder.isConnected(rowHeader.get(i), colHeader.get(j), metaPath);
                    if (connected != NOTCONNECTED) {
                        cellsSet.add(new TableCell(i, j, metaPath.getColor(connected)));
                    }
                } catch (StackOverflowError err) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), err);
                }

            }
        }

    }

    /**
     * @return Returns the MetaPath of the metaPathSelector.
     */
    public MetaPath getMetaPath() {
        return metaPath;
    }

}