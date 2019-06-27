package de.imise.tool3lgm.graphtools.newmatrixview;

import static de.imise.tool3lgm.userproperties.UserProperties.is;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS;

import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.MetaPathSelector.MetaPathSelection;
import de.imise.tool3lgm.graphtools.path.meta.AbstractMetaPath;
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
    private List<AbstractMetaPath> metaPaths;

    /** Legt fest, ob nur absolte Teilelemente angezeigt werden sollen */
    private boolean absolutePartsOnly = false;

    /**
     * Hintergrundfarbe für alle {@link TableCell}s
     * private Color tableCellBackgroundColor = Color.white;
     * /**
     * Farben, in denen die Pfade dargestellt werden. Wird nur ein Pfad dargestellt,
     * bekommt er die erste Pfade, werden 2 Pfade dargestellt, bekommen sie die ersten
     * beiden und ihre Kombination die 3. Farbe usw. Mit den 15 Farben lassen sich n=4
     * Pfade und alle ihre Kombinationen gleichzeitig darstellen (2^n - 1)
     */
    public static Color[] pathColors = {
            Color.blue, Color.orange, Color.green, Color.cyan, Color.red, Color.magenta, Color.pink, Color.yellow, Color.black, Color.darkGray, Color.gray, new Color(122, 55, 139), new Color(144, 238, 144), new Color(139, 54, 38), new Color(255, 185, 15)

    };

    /**
     * Legt ein neues <code>TableModel</code> an, das erstmal gar nichts
     * darstellt, sondern nur das <code>GraphDocument</code> kennt, aus dem
     * nach dem setzen von gültigen Zeilen und Spaltenklassen die
     * Elementverknüpfungen anzeigen soll.
     * Konstruktor
     *
     * @param graphDocument
     *            das (Teil-)Modell
     */
    public TableModel(final GraphDocument graphDocument) {
        this.graphDocument = graphDocument;
        fillTableModelIntern(null, null, null, false);
    }

    /**
     * @param metaPathSelection Selektion, die vorgibt, für welche Klassen und Pfade die Tabelle dargestellt werden soll. Dieser Paramter darf nicht
     *            null sein!
     */
    public void fillTableModel(final MetaPathSelection metaPathSelection) {
        fillTableModelIntern(metaPathSelection.class1, metaPathSelection.class2, metaPathSelection.selectedMetaPaths, metaPathSelection.showPartsOnly);
    }

    /**
     * @param rowClass
     *            Zeilenklasse
     * @param colClass
     *            Spaltenklasse
     * @param metaPaths
     *            MetaPfade über den Zeilen- und Spaltenklasse verbunden sein sollen
     * @param absolutePartsOnly
     *            legt fest, ob in der Matrix nur Elemente auftauchen sollen, die im
     *            Gesamtmodell keine Teilelemente besitzen
     */
    private void fillTableModelIntern(final Class<? extends ModelElement> rowClass, final Class<? extends ModelElement> colClass, final List<AbstractMetaPath> metaPaths, final boolean absolutePartsOnly) {
        this.rowClass = rowClass;
        this.colClass = colClass;
        this.metaPaths = metaPaths;
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
    public Node getRowElement(final int rowIndex) {
        return rowIndex < rowHeader.size() ? (Node) rowHeader.get(rowIndex) : null;
    }

    /**
     * @return {@link Node} für Spaltenelement
     */
    public Node getColElement(final int colIndex) {
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
     * Liefert die {@link TableCell} an mit den Koordinaten col und row.
     * ACHTUNG: Da diese Funktion über den Iterator aller Cells läuft, ist sie bei vielen Zellen sehr 'teuer'.
     *
     * @param col
     * @param row
     * @return
     */
    public TableCell getCell(final int col, final int row) {
        for (TableCell cell : cellsSet) {
            if (cell.getColIndex() == col && cell.getRowIndex() == row) {
                return cell;
            }
        }
        return null;
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
     * Liefert <code>true</code>, wenn gültige Elementklassen und gültige MetaPfade
     * (jeweils ungleich <code>null</code>) gesetzt sind.
     *
     * @return <code>true</code>, wenn gültige Klassen und gültige Metapfade gesetzt sind, sonst <code>false</code>
     */
    public boolean isValid() {
        if (rowClass == null || colClass == null || metaPaths == null) {
            return false;
        }
        for (AbstractMetaPath metaPath : metaPaths) {
            if (metaPath == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * löscht alle Zelleneinträge aus dem Tabellenmodell und setzt diese mit den
     * aktuelle überschriften neu
     */
    private void updateAllCellEntries() {
        cellsSet = new HashSet<>(colHeader.size() * rowHeader.size());
        if (metaPaths == null) {
            return;
        }
        if (colHeader.size() * rowHeader.size() == 0) {
            return;
        }
        for (int i = 0; i < rowHeader.size(); i++) {
            for (int j = 0; j < colHeader.size(); j++) {
                try {
                    int connectionBitPattern = -1;
                    for (int k = 0; k < metaPaths.size(); k++) {
                        AbstractMetaPath metaPath = metaPaths.get(k);
                        boolean containsPartOf = metaPath.containsPropertyTransferEdge();
                        boolean connected = false;
                        if (containsPartOf) {
                            connected = MetaPathFunctions.getConnectionState(rowHeader.get(i), colHeader.get(j), metaPath, false, false) != null;
                        } else {
                            connected = MetaPathFunctions.getConnectionState(rowHeader.get(i), colHeader.get(j), metaPath, is(OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARENTS), is(OPTION_ELEMENTS_RECEIVE_PROPERTIES_FROM_PARTS)) != null;
                        }
                        if (connected) {
                            connectionBitPattern += 1 << k;
                        }
                    }
                    if (connectionBitPattern >= 0) {
                        cellsSet.add(new TableCell(i, j, pathColors[connectionBitPattern]));
                    }
                } catch (StackOverflowError err) {
                    Log.show(Log.ERROR, Tool3lgmConstants.getResString("FehlerAllgemein"), err);
                }
            }
        }

    }

    /**
     * @return Returns the MetaPath of the metaPathSelector.
     */
    public List<AbstractMetaPath> getMetaPaths() {
        return metaPaths;
    }

}