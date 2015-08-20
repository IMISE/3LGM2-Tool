/*
 * Created on 13.01.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JList;

import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model.UserFieldTableModel;
import de.imise.util.Pair;

/**
 * Klasse stellt den Controller für <code>UserFieldTable</code>s dar.
 * <p>
 * Mittels Instanzen dieser Klasse kann die Editierbarkeit der Zellen von Tables kontrolliert werden. <br>
 * Außerdem wird die Möglichkeit geboten, mehrfache Auswahl von Zellen vorzunehemen, sowie die gemeinsame Änderung der Werte aller markierten Zellen.
 * <p>
 * Über die statischen Methoden können vorgefertigte Instanzen dieser Klasse abgerufen werden, die auf Tabels für primäre und sekundäre Kennzahlen,
 * Verteilungsgewicht oder Modelvariablen zugeschnitten sind.
 * 
 * @author fstephan
 */
public abstract class UserFieldTableController {

    /* ************************************** Start: Konstanten ****************************************** */

    // Konstanten für den selectionChangeMode

    private static final int CONTROL_MODE = 1;

    private static final int SHIFT_MODE = 2;

    private static final int DEFAULT_MODE = 0;

    /* ************************************** Ende: Konstanten ****************************************** */

    /* ************************************** Start: Deklaration ****************************************** */

    /**
     * Abbildung einer Zelle auf den Wert ihrer Editierbarkeit: (i,j) -> ist_editierbar(i,j) für alle Zellen eines Tables
     */
    boolean[][] editMatrix;

    /**
     * Abbildung einer Zelle auf ihren Selektionsstatus: (i,j) -> ist_selektiert(i,j) für alle Zellen eines Tables
     */
    boolean[][] initialSelectionMatrix;

    /**
     * Ändert und überwacht {@link #selectionChangeMode} in Abhängigkeit davon, ob Shift oder Strg gedrückt wurde bzw. ein MouseDragging erfolgt ist.
     * Damit wird das korrekte Selektionsverhalten dieses {@link UserFieldTableController}s ermöglicht.
     */
    private SelectionModeHandler selectionModeHandler;

    /**
     * {@link KeyListener}, der das Verhalten des Tables bei Drücken der Pfeiltasten der Entertaste oder der Deletetaste bestimmt.
     */
    private KeySelectionChangeHandler keySelectionHandler;

    /**
     * Listener, der dafür sorgt, dass die Selektion beim Dragging mit der Mouse auch über dem RowHeader fortgesetzt wird.
     */
    private RowHeaderSelectionListener rowHeaderSelectionListener;

    /**
     * Array, das den durch das Keyboard und Mouse-Dragging erzeugten Selektionszustand der Zellen enthält
     */
    private boolean[][] rangeSelectionMatrix;

    /** Array, das den durch Mouse-Clicks erzeugten Selektionszustand der Zellen enthält */
    private boolean[][] singleSelectionMatrix;

    /**
     * Array, das für jede Zelle wiedergibt, ob ihre Selektion durch die Methoden {@link #clearRangeSelections()} und {@link #clearSingleSelections()}
     * aufgehoben werden darf.
     */
    private boolean[][] ignoreClearingMatrix;

    /**
     * Bestimmt das Selektionsverhalten, bei Änderung der Auwahl in einem Table.
     */
    private int selectionChangeMode = DEFAULT_MODE;

    /** Startkoordinaten der Selektion (Zeilenindex,Spaltenindex) */
    private Point anchorPoint;

    /** Endkoordinaten der Selektion (Zeilenindex,Spaltenindex) */
    private Point leadingPoint;

    /** Gibt an, ob die Mehrfachselektion aktiviert ist */
    private boolean isMultipleSelectionEnabled = false;

    /* ************************************** Ende: Deklaration ****************************************** */

    /**
     * Konstruktor Zugriff von Außen nicht möglich. Verwende stattdessen eine der getNew...() Methoden
     * 
     * @param editMatrix Gibt zu jeder Zelle
     * @param initialSelectionMatrix
     */
    private UserFieldTableController(final boolean[][] editMatrix, final boolean[][] initialSelectionMatrix) {
        this.editMatrix = editMatrix;
        this.initialSelectionMatrix = initialSelectionMatrix;
        int m = initialSelectionMatrix.length;
        int n = initialSelectionMatrix[0].length;
        rangeSelectionMatrix = new boolean[m][n];
        singleSelectionMatrix = new boolean[m][n];
        ignoreClearingMatrix = new boolean[m][n];
    }

    /**
     * Methode ist so zu überschreiben, dass zurückgegeben wird, ob die Zelle an der Position (row,col) editierbar ist, oder nicht.
     * 
     * @param row
     * @param col
     * @return
     */
    abstract boolean isEditable(int row, int col);

    /**
     * Methode gibt wieder, ob die Zelle an der Position (<code>row</code>,<code>col</code>) ausgewählt ist.
     * 
     * @param row
     * @param col
     * @return
     */
    boolean isCellSelected(final int row, final int col) {
        if (row < 0 || col < 0) {
            return false;
        }
        try {
            return rangeSelectionMatrix[row][col] || singleSelectionMatrix[row][col];
        } catch (NullPointerException npe) {
            return initialSelectionMatrix[row][col];
        }
    }

    /**
     * Gibt wieder, ob <code>p</code> der Ausgangspunkt der aktuellen Selektion ist
     * 
     * @param p
     * @return <code>p.equals(anchorPoint)</code>
     */
    boolean isAnchorPoint(final Point p) {
        return p.equals(anchorPoint);
    }

    /**
     * Gibt wieder, ob <code>p</code> der Punkt ist, bis zu dem die aktuelle Selektion reicht.
     * 
     * @param p
     * @return <code>p.equals(leadingPoint)</code>
     */
    boolean isLeadingPoint(final Point p) {
        return p.equals(leadingPoint);
    }

    /**
     * Gibt den Ausgangspunkt der aktuellen Selektion wieder
     * 
     * @return {@link #anchorPoint}
     */
    Point getAnchorSelectionPoint() {
        return anchorPoint;
    }

    /**
     * Gibt den Punkt wieder, bis zu dem die aktuelle Selektion reicht.
     * 
     * @return
     */
    Point getLeadSelectionPoint() {
        return leadingPoint;
    }

    /**
     * Aktiviert die Mehrfachauswahl im Table
     * 
     * @param table
     */
    void enableMultipleSelection(final UserFieldTable table) {

        disableMultipleSelection(table);

        int m = initialSelectionMatrix.length;
        int n = initialSelectionMatrix[0].length;
        rangeSelectionMatrix = new boolean[m][n];
        singleSelectionMatrix = new boolean[m][n];
        ignoreClearingMatrix = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rangeSelectionMatrix[i][j] = initialSelectionMatrix[i][j];
                singleSelectionMatrix[i][j] = initialSelectionMatrix[i][j];
                ignoreClearingMatrix[i][j] = initialSelectionMatrix[i][j];
            }
        }

        selectionModeHandler = new SelectionModeHandler(table);
        table.addKeyListener(selectionModeHandler);
        table.addMouseListener(selectionModeHandler);
        table.addMouseMotionListener(selectionModeHandler);

        keySelectionHandler = new KeySelectionChangeHandler(table);
        table.addKeyListener(keySelectionHandler);

        JList rowHeader = table.getRowHeaderView();
        if (rowHeader != null) {
            rowHeaderSelectionListener = new RowHeaderSelectionListener(table, rowHeader);
            table.addMouseMotionListener(rowHeaderSelectionListener);
        }

        isMultipleSelectionEnabled = true;
    }

    /**
     * Deaktiviert die Mehrfachauswahl im Table
     * 
     * @param table
     */
    void disableMultipleSelection(final UserFieldTable table) {

        table.removeKeyListener(selectionModeHandler);
        table.removeMouseListener(selectionModeHandler);
        table.removeMouseMotionListener(selectionModeHandler);
        table.removeKeyListener(keySelectionHandler);

        if (rowHeaderSelectionListener != null) {
            table.removeMouseMotionListener(rowHeaderSelectionListener);
        }

        isMultipleSelectionEnabled = false;

        selectionChangeMode = DEFAULT_MODE;
    }

    /**
     * Benachrichtigt den Controller, dass im Table eine andere Zelle ausgewählt wurde und löst ein Aktualisieren der Selektion aus.
     * 
     * @param row neuer Zeilenindex
     * @param col neuer Spaltenindex
     */
    void changeSelection(final int row, final int col) {

        switch (selectionChangeMode) {

        // Erweitere Selektion
        case SHIFT_MODE:
            shiftModeSelectionChange(row, col);
            break;

        // Halte bisherige Selektionen fest und beginne eine Neue
        case CONTROL_MODE:
            controlModeSelectionChange(row, col);
            break;

        /*
         * Lösche alle bisherigen Selektionen und setzte nur die eben angeklickte Zelle als selektiert.
         */
        case DEFAULT_MODE:
            defaultModeSelectionChange(row, col);
            break;

        }
    }

    /**
     * Löscht alle Selektionen.
     */
    void clearSelection() {
        clearAllSelections();
        anchorPoint = new Point(-1, -1);
        leadingPoint = new Point(-1, -1);
    }

    void selectAll() {
        clearAllSelections();
        for (int i = 0; i < rangeSelectionMatrix.length; i++) {
            for (int j = 0; j < rangeSelectionMatrix[0].length; j++) {
                rangeSelectionMatrix[i][j] = true;
            }
        }
    }

    /**
     * Gibt zurück, ob mehr als eine Zelle markiert ist.
     * 
     * @return
     */
    boolean hasMultipleSelection() {

        int selectionCount = 0;

        for (int i = 0; i < rangeSelectionMatrix.length; i++) {
            for (int j = 0; j < rangeSelectionMatrix[0].length; j++) {
                if (rangeSelectionMatrix[i][j] == true || singleSelectionMatrix[i][j] == true) {
                    selectionCount++;
                }
                if (selectionCount >= 2) {
                    return true;
                }
            }
        }

        return false;
    }

    /** Gibt wieder, ob die Mehrfachselektion aktiviert ist */
    boolean isMultipleSelectionEnabled() {
        return isMultipleSelectionEnabled;
    }

    /**
     * Gibt, wenn die aktuelle Selektion zusammenhängend ist, ein Punkte-Paar wieder. Der erste Punkt repräsentiert dabei die markierte Zelle mit dem
     * kleinsten Zeilen- und Spaltenindex und der zweite Punkt die mit dem größten Zeilen- und Spaltenindex. <br>
     * Ist die Selektion nicht zusammenhängend, wird <code>null</code> zurückgegeben.
     */
    Pair<Point, Point> getCoherentlySelection() {

        if (!isMultipleSelectionEnabled) {
            return new Pair<Point, Point>(leadingPoint, leadingPoint);
        }

        // Größte und kleinste Zeilen- und Spaltenindizes der selektierten Zellen bestimmen
        int m = rangeSelectionMatrix.length, n = rangeSelectionMatrix[0].length;
        int minRow = m, maxRow = -1, minCol = n, maxCol = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (isCellSelected(i, j)) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        if (maxRow == -1) {
            return null;
        }

        // Prüfe, ob sich nicht selektierte Zellen innerhalb der minimalen und maximalen Indizes befinden
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                if (!isCellSelected(i, j)) {
                    return null;
                }
            }
        }

        return new Pair<Point, Point>(new Point(minRow, minCol), new Point(maxRow, maxCol));
    }

    /**
     * Erweitert Selektion vom {@link #anchorPoint} bis zum gerade gewählten Punkt (<code>row</code>,<code>col</code>).
     * 
     * @param row
     * @param col
     */
    private void shiftModeSelectionChange(final int row, final int col) {
        leadingPoint = new Point(row, col);

        int x1, x2, y1, y2;

        if (anchorPoint.x <= row) {
            x1 = anchorPoint.x;
            x2 = row;
        } else {
            x2 = anchorPoint.x;
            x1 = row;
        }

        if (anchorPoint.y <= col) {
            y1 = anchorPoint.y;
            y2 = col;
        } else {
            y2 = anchorPoint.y;
            y1 = col;
        }

        clearRangeSelections();

        int xMax = rangeSelectionMatrix.length - 1;
        int yMax = rangeSelectionMatrix[0].length - 1;
        if (x1 >= 0 && x1 <= xMax && x2 >= 0 && x2 <= xMax) {
            if (y1 >= 0 && y1 <= yMax && y2 >= 0 && y2 <= yMax) {
                for (int i = x1; i <= x2; i++) {
                    for (int j = y1; j <= y2; j++) {
                        rangeSelectionMatrix[i][j] = true;
                    }
                }
            }
        }

    }

    /**
     * Hält bisherige Selektionen fest und beginnt eine neue, indem der {@link #anchorPoint} auf den gerade ausgewählten Punkt (<code>row</code>,
     * <code>col</code>) gesetzt wird.
     * 
     * @param row
     * @param col
     */
    private void controlModeSelectionChange(final int row, final int col) {

        // Bisherige Selektionen merken
        for (int i = 0; i < ignoreClearingMatrix.length; i++) {
            for (int j = 0; j < ignoreClearingMatrix[0].length; j++) {
                ignoreClearingMatrix[i][j] = rangeSelectionMatrix[i][j] || singleSelectionMatrix[i][j];
            }
        }

        anchorPoint = new Point(row, col);
        leadingPoint = new Point(row, col);

        if (singleSelectionMatrix[row][col] == true || rangeSelectionMatrix[row][col] == true) {
            singleSelectionMatrix[row][col] = false;
            rangeSelectionMatrix[row][col] = false;
        } else {
            singleSelectionMatrix[row][col] = true;
        }
    }

    /**
     * Aktualisiert die Selektionen im Table, falls werder die Shift- noch die Strg- Taste gedrückt wurde.
     * 
     * @param row
     * @param col
     */
    private void defaultModeSelectionChange(final int row, final int col) {
        anchorPoint = new Point(row, col);
        leadingPoint = new Point(row, col);
        clearAllSelections();
        if (leadingPoint != null) {
            singleSelectionMatrix[leadingPoint.x][leadingPoint.y] = true;
        }
    }

    /**
     * Löscht alle durch Mouseclicks erzeugten Selektionen, bis auf die, die durch {@link #ignoreClearingMatrix} davor geschütz sind.
     */
    private void clearSingleSelections() {
        for (int i = 0; i < singleSelectionMatrix.length; i++) {
            for (int j = 0; j < singleSelectionMatrix[0].length; j++) {
                if (ignoreClearingMatrix[i][j] == false) {
                    singleSelectionMatrix[i][j] = false;
                }
            }
        }
    }

    /**
     * Löscht alle durch das Keyboard und MouseDragging erzeugten Selektionen, bis auf die, die durch {@link #ignoreClearingMatrix} davor geschütz
     * sind.
     */
    private void clearRangeSelections() {
        for (int i = 0; i < rangeSelectionMatrix.length; i++) {
            for (int j = 0; j < rangeSelectionMatrix[0].length; j++) {
                if (ignoreClearingMatrix[i][j] == false) {
                    rangeSelectionMatrix[i][j] = false;
                }
            }
        }
    }

    /**
     * Löscht alle Selektionen, behält aber {@link #anchorPoint} und {@link #leadingPoint} bei.
     */
    private void clearAllSelections() {
        ignoreClearingMatrix = new boolean[ignoreClearingMatrix.length][ignoreClearingMatrix[0].length];
        clearSingleSelections();
        clearRangeSelections();
    }

    /**
     * Deaktiviert die Mehrfachselektion.
     * 
     * @param table
     */
    void removeFrom(final UserFieldTable table) {
        disableMultipleSelection(table);
    }

    /**
     * Zelle editierbar gdw. editMatrix für diese Zelle = true
     * 
     * @param editMatrix
     * @return
     */
    public static UserFieldTableController getNewUserFieldTableController(final boolean[][] editMatrix) {
        return new UserFieldTableController(editMatrix, new boolean[editMatrix.length][editMatrix[0].length]) {

            @Override
            public boolean isEditable(final int row, final int col) {
                return editMatrix[row][col];
            }
        };
    }

    /* ************************** Beginn: statische Methode ********************************************* */

    /**
     * Methode gibt eine <code>TableEditCondition</code> für Tables, die Kennzahlen enthalten und <code>uftm</code> als Model besitzen zurück.
     * 
     * @param uftm
     */
    public static UserFieldTableController getNewClassificationNumberTableController(final UserFieldTableModel uftm) {

        /*
         * Eigenschaften: - erste Zeile NICHT editierbar - erste Spalte NICHT editierbar - unabhängig von ModelValue
         */
        return getNewUserFieldTableController(uftm, false, false, false);
    }

    /**
     * Methode gibt eine <code>TableEditCondition</code> für Tables, die Verteilungsgewichte enthalten und <code>uftm</code> als Model besitzen
     * zurück.
     * 
     * @param uftm
     */
    public static UserFieldTableController getNewDistributionWeightTableController(final UserFieldTableModel uftm) {

        /*
         * Eigenschaften: - erste Zeile NICHT editierbar - erste Spalte NICHT editierbar - abhängig von ModelValue
         */
        return getNewUserFieldTableController(uftm, false, false, true);
    }

    /**
     * Methode gibt eine <code>TableEditCondition</code> für Tables, die berechnete Kennzahlen enthalten und <code>uftm</code> als Model besitzen
     * zurück.
     * 
     * @param uftm
     */
    public static UserFieldTableController getNewClassificationNumberFormulaTableController(final UserFieldTableModel uftm) {

        if (uftm.getRowCount() == 0 || uftm.getColumnCount() == 0) {
            return null;
        }

        /*
         * Eigenschaften: - keine Zelle ist editierbar
         */
        return new UserFieldTableController(new boolean[1][1], new boolean[uftm.getRowCount()][uftm.getColumnCount()]) {
            /*
             * (non-Javadoc)
             * @see tool3lgm.graphtools.userfield.dialog.valueinput.table.UserFieldTableController#isEditable(int, int)
             */
            @Override
            public boolean isEditable(final int row, final int col) {
                return false;
            }
        };
    }

    /**
     * Methode gibt eine <code>TableEditCondition</code> für Tables, die ModelVariablen enthalten und <code>uftm</code> als Model besitzen zurück.
     * 
     * @param uftm
     */
    public static UserFieldTableController getNewModelVariableTableController(final UserFieldTableModel uftm) {

        /*
         * Eigenschaften: - erste Zeile NICHT editierbar - erste Spalte editierbar - unabhängig von ModelValue
         */
        return getNewUserFieldTableController(uftm, false, false, false);
    }

    /**
     * Methode gibt in Abhängigkeit der übergebenen Parameter eine neue Instanz dieser Klasse wieder: <code>uftm</code> ist das zum Table gehörige
     * Model <code>firstRowEditable</code> gibt an, ob die oberste Reihe editierbar sein soll <code>firstColumnEditable</code> gibt an, ob die linke
     * Spalte editierbar sein soll <code>dependsOnModelValue</code> gibt an, ob die Editierbarkeit der Zellen zusätzlich noch vom ModelValue der
     * Zellen abhängen soll
     * 
     * @param uftm
     * @param firstRowEditable
     * @param firstColumnEditable
     * @param dependsOnModelValue
     */
    private static UserFieldTableController getNewUserFieldTableController(final UserFieldTableModel uftm, final boolean firstRowEditable, final boolean firstColumnEditable, final boolean dependsOnModelValue) {

        if (uftm.getRowCount() == 0 || uftm.getColumnCount() == 0) {
            return null;
        }

        // x,y-Verschiebung der editMatrix
        final int xshift;
        final int yshift;

        // Verschiebung um 1 nach rechts, falls RowIdentifiers existieren
        if (uftm.hasRowIdentifiers()) {
            xshift = 1;
        } else {
            xshift = 0;
        }

        // Verschiebung um 1 nach unten, falls ColumnIdentifiers existieren
        if (uftm.hasColumnIdentifiers()) {
            yshift = 1;
        } else {
            yshift = 0;
        }

        boolean[][] editMatrix = new boolean[uftm.getRowCount() + yshift][uftm.getColumnCount() + xshift];

        // vorher Alles auf editierbar setzen
        for (int i = 0; i < editMatrix.length; i++) {
            for (int j = 0; j < editMatrix[0].length; j++) {
                editMatrix[i][j] = true;
            }
        }

        if (firstRowEditable == false) {// Oberste Reihe nicht editierbar
            for (int j = 0; j < editMatrix[0].length; j++) {
                editMatrix[0][j] = false;
            }
        }

        if (firstColumnEditable == false) {// Linke Spalte nicht editierbar
            for (int i = 0; i < editMatrix.length; i++) {
                editMatrix[i][0] = false;
            }
        }

        // SelectionMatrix
        boolean selectionMatrix[][] = new boolean[uftm.getRowCount()][uftm.getColumnCount()];

        return new UserFieldTableController(editMatrix, selectionMatrix) {

            @Override
            public boolean isEditable(final int row, final int col) {
                if (dependsOnModelValue == true) {// Zellen ohne ModelValue nicht editierbar
                    return editMatrix[row + yshift][col + xshift] && uftm.isCellEditable(row, col);
                }
                // Zelleneditierbarkeit hängt nur von editMatrix ab
                return editMatrix[row + yshift][col + xshift];
            }
        };
    }

    /* ************************** Ende: statische Methode ********************************************* */

    /* ************************* Beginn: Unterklassen ******************************************** */

    /**
     * Ändert und überwacht {@link #selectionChangeMode} in Abhängigkeit davon, ob Shift oder Strg gedrückt wurde bzw. ein MouseDragging erfolgt ist. <br>
     * Beim Drücken von Strg- und Pfeiltasten erfolgt ein Springen der Selektion an den jeweiligen Rand des {@link #table}s. <br>
     * MouseDragging wird als gedrücktes Shift interpretiert.
     * <p>
     * Außerdem
     * 
     * @author fstephan
     */
    private class SelectionModeHandler extends MouseAdapter implements KeyListener {

        /** zu überwachender Table */
        private final UserFieldTable table;

        /** gibt an, ob gerade ein Dragging der Mouse erfolgt */
        private boolean isDragging;

        /**
         * Konstruktor
         * 
         * @param table zu überwachender Table
         */
        public SelectionModeHandler(final UserFieldTable table) {
            this.table = table;
        }

        /**
         * Hier erfolgt das Setzen des {@link #selectionChangeMode}s beim Drücken von Shift oder Strg.
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(final KeyEvent e) {

            if (isDragging == true) {
                return;
            }

            if (e.isControlDown()) { // Strg gedrückt
                if (e.isShiftDown()) { // Strg & Shift gedrückt
                    selectionChangeMode = SHIFT_MODE;
                } else {
                    // nur Strg gedrückt;
                    // hier wird der DEFAULT_MODE gewählt, damit bei gedrückter Strg-Taste
                    // das Springen zu den Tabellenrändern möglich ist, ohne dabei die Zellen
                    // zwischen dem aktuellen Index und dem Tabellenrand zu markieren
                    selectionChangeMode = DEFAULT_MODE;
                }
                // tatsächliche Indices der markierten Zelle im Table
                int row = table.getSelectionModel().getAnchorSelectionIndex();
                int col = table.getColumnModel().getSelectionModel().getAnchorSelectionIndex();

                switch (e.getKeyCode()) { // Springe an die Ränder des Tables, wenn Pfeiltaste gedrückt wird
                // Springe nach ganz unten
                case KeyEvent.VK_DOWN:
                    table.changeSelection(table.getRowCount() - 1, col, false, false);
                    break;
                // Springe nach ganz oben
                case KeyEvent.VK_UP:
                    table.changeSelection(0, col, false, false);
                    break;
                // Springe nach ganz links
                case KeyEvent.VK_LEFT:
                    table.changeSelection(row, 0, false, false);
                    break;
                // Springe nach ganz rechts
                case KeyEvent.VK_RIGHT:
                    table.changeSelection(row, table.getColumnCount() - 1, false, false);
                    break;
                }
                // Strg & Shift gedrückt
                if (e.isShiftDown()) {
                    selectionChangeMode = SHIFT_MODE;
                } else {
                    // nur Strg gedrückt;
                    // das möglicherweise eingetretene Springen zu den Rändern des Tables wurde bereits abgearbeitet
                    // --> damit darf jetzt auf CONTROL_MODE gewechselt werden um mehrfache Selektion zu ermöglichen
                    selectionChangeMode = CONTROL_MODE;
                }
            } else { // Strg nicht gedrückt
                if (e.isShiftDown()) {
                    selectionChangeMode = SHIFT_MODE;
                }
            }
        }

        /**
         * Hier erfolgt das Setzen des {@link #selectionChangeMode}s beim Lösen von Shift oder Strg.
         * 
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        @Override
        public void keyReleased(final KeyEvent e) {
            setSelectionChangeMode(e);
        }

        /**
         * Setzt den Selektionsmodus je nach gedrückten Shift- und Strg-Knöpfen.
         * 
         * @param e
         */
        private final void setSelectionChangeMode(final InputEvent e) {
            if (e.isControlDown()) {
                if (e.isShiftDown()) {
                    selectionChangeMode = DEFAULT_MODE;
                } else {
                    selectionChangeMode = CONTROL_MODE;
                }
            } else {
                if (e.isShiftDown()) {
                    selectionChangeMode = SHIFT_MODE;
                } else {
                    selectionChangeMode = DEFAULT_MODE;
                }
            }
        }

        @Override
        public void keyTyped(final KeyEvent e) {
        }

        /**
         * Hier erfolgt das Setzen des {@link #selectionChangeMode}s beim Dragging der Mouse.
         * 
         * @see java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseDragged(final MouseEvent e) {
            isDragging = true;
            selectionChangeMode = SHIFT_MODE;
        }

        /**
         * Hier erfolgt das Setzen des {@link #selectionChangeMode}s beim Lösen der Mouse-Taste.
         * 
         * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseReleased(final MouseEvent e) {
            isDragging = false;
            setSelectionChangeMode(e);
        }
    }

    /**
     * {@link KeyListener}, der das Verhalten des Tables bei Drücken der Pfeiltasten der Entertaste oder der Deletetaste bestimmt.
     * <p>
     * Sind mehrere Zellen markiert, wird nach Drücken von Enter die Markierung auf den {@link #leadingPoint} der letzten Selektion gesetzt. <br>
     * Ist nur eine Zelle markiert, wird nach Drücken von Enter die Markierung um Eins nach unten verschoben, bzw. in die nächste Spalte, falls das
     * Spaltendende überschritten wurde.
     * <p>
     * Wird eine der Pfeiltasten gedrückt, wandert die Markierung vom {@link #leadingPoint} aus um Eins in die jeweilige Richtung.
     * <p>
     * Beim Drücken der Delete-Taste wird sofort der Inhalt aller markierten Zellen gelöscht, ohne dabei ein Editieren der Lead-Zelle zu starten.
     * 
     * @author fstephan
     */
    private class KeySelectionChangeHandler extends KeyAdapter {

        /** zu überwachender Table */
        private final UserFieldTable table;

        /**
         * Konstruktor
         * 
         * @param table zu überwachender Table
         */
        public KeySelectionChangeHandler(final UserFieldTable table) {
            this.table = table;
        }

        /**
         * Ändert die Markierung im {@link #table} in Abhänigigkeit der gedrückten Taste und der Anzahl selektierter Zellen.
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        @Override
        public void keyPressed(final KeyEvent e) {

            // ////////////////////////////////////////////////
            // Nach dem hier die Markierung geändert wurde, //
            // erfolgt ein erneute Änderung durch den //
            // standardmäßigen KeyListener eines JTables! //
            // Deshalb muss z.B. bei mehrfacher Selektion //
            // und gedrückter Entertaste, der leadingPoint //
            // um Eins nach oben gesetzt werden, weil der //
            // KeyListener des JTables danach die //
            // Markierung um Eins nach unten setzt. //
            // Damit wird erreicht, dass die Markierung auf //
            // dem leadingPoint bleibt. //
            // ////////////////////////////////////////////////

            if (isArrowOrEnterPressed(e)) {// Enter oder Pfeiltaste wurde gedrückt

                if (isEnterPressed(e) && hasMultipleSelection()) {
                    leadingPoint.translate(-1, 0);
                }

                // einfache Selektion + Enter oder Pfeiltaste -> setze Markierung auf leadingPoint
                table.getSelectionModel().setSelectionInterval(leadingPoint.x, leadingPoint.x);
                table.getColumnModel().getSelectionModel().setSelectionInterval(leadingPoint.y, leadingPoint.y);

                return;
            } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
                selectAll();
            }
        }

        /**
         * Übernimmt Delete sofort auf alle selektierten Zellen.
         * 
         * @param e
         * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
         */
        @Override
        public void keyReleased(final KeyEvent e) {

            int row = table.getEditingRow();
            int col = table.getEditingColumn();

            if (row != -1 && col != -1) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (table.getCellEditor(row, col).getCellEditorValue().toString().equals(UserFieldTableCell.EDITOR_EMPTY_STRING)) {
                        table.stopEditing();
                    }
                }
                table.setRowSelectionInterval(row, row);
                table.setColumnSelectionInterval(col, col);
            }
        }

        /**
         * Gibt wieder ob das {@link KeyEvent} <code>e</code> durch Drücken der Enter- oder einer der Pfeiltasten ausgelöst wurde.
         * 
         * @param e
         * @return
         */
        private boolean isArrowOrEnterPressed(final KeyEvent e) {
            return isArrowPressed(e) || isEnterPressed(e);
        }

        /**
         * Gibt wieder ob das {@link KeyEvent} <code>e</code> durch Drücken einer der Pfeiltasten ausgelöst wurde.
         * 
         * @param e
         * @return
         */
        private boolean isArrowPressed(final KeyEvent e) {
            int k = e.getKeyCode();
            return k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN || k == KeyEvent.VK_LEFT || k == KeyEvent.VK_RIGHT;
        }

        /**
         * Gibt wieder ob das {@link KeyEvent} <code>e</code> durch Drücken der Entertaste ausgelöst wurde.
         * 
         * @param e
         * @return
         */
        private boolean isEnterPressed(final KeyEvent e) {
            return e.getKeyCode() == KeyEvent.VK_ENTER;
        }
    }

    /**
     * Listener, der die Selektion im <code>table</code> auch dann der Mouse folgen lässt, wenn sich der Cursor über dem <code>rowHeader</code>
     * befindet.
     * 
     * @author fstephan
     */
    private class RowHeaderSelectionListener implements MouseMotionListener {

        private final UserFieldTable table;
        private final JList rowHeader;

        /**
         * Konstruktor
         * 
         * @param table
         * @param rowHeader
         */
        public RowHeaderSelectionListener(final UserFieldTable table, final JList rowHeader) {
            this.table = table;
            this.rowHeader = rowHeader;
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            if (e.getSource() != table) {
                return;
            }

            int index = rowHeader.locationToIndex(e.getPoint());
            if (leadingPoint != null) {
                table.changeSelection(index, leadingPoint.y, true, true);
            }
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
        }

    }

    /* ************************* Ende: Unterklassen ******************************************** */

}
