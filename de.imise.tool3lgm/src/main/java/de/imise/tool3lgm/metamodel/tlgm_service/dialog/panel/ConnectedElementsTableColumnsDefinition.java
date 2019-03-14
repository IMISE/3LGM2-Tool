package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel.ConnectedElementsTableColumnsDefinition.SingleColumnDefinition;

/**
 * @author AXS (12 Mar 2019)
 */
public class ConnectedElementsTableColumnsDefinition implements Iterable<SingleColumnDefinition> {

    /**
     * @author AXS (11 Mar 2019)
     */
    public enum ColumnType {
        END_ELEMENT,
        OPTIONAL, //muss in Verbindung mit einem Index initialisiert werden, wobei der Index die Kante im Pfad beschreibt deren Option in der Spalte dagestellt werden soll
        PATH_NAME, //der über einen ResKey geladene Name des Pfades - im Moment nicht umgesetzt -> DAS GEHT GAR NICHT, WEIL IM DIE BLÄTTER DES BAUMES NICHT MEHR DEN GESAMTPFAD KENNEN
        PATH_STEP_START, //Muss man mit Index angeben, wobei damit immer das EndElement des jeweiligen Pfadschrittes gemeint wäre und der Index Pfadlänge - 1 dassselbe wie END_ELEMENT ergeben würde.
        PATH_STEP_END, //Muss man mit Index angeben, wobei damit immer das EndElement des jeweiligen Pfadschrittes gemeint wäre und der Index Pfadlänge - 1 dassselbe wie END_ELEMENT ergeben würde.
        PATH_STEP_EDGE, //dasselbe wie PATH_STEP_END_ELEMENT nur immer die Kante des Pfadschrittes (wenn es ein Elementarpfadschritt ist)
        PATH_STEP_NAME, //dasselbe wie PATH_STEP_EDGE_CLASS nur nur der Name des Pfadschrittes
        PATH_STEP_FULL_NAME; //dasselbe wie PATH_STEP_EDGE_CLASS nur immer der volle Name des Pfadschrittes
    }

    /**
     * @author AXS (11 Mar 2019)
     */
    public static class SingleColumnDefinition {

        private final ColumnType columnType;

        private final int pathStepIndex;

        private final String headerResKeyOrName;

        private final int width;

        public SingleColumnDefinition(final ColumnType columnType, final String headerResKeyOrName, final int width) {
            this(columnType, -1, headerResKeyOrName, width);
        }

        public SingleColumnDefinition(final ColumnType columnType, final int pathStepIndex, final String headerResKeyOrName, final int width) {
            this.columnType = columnType;
            this.pathStepIndex = pathStepIndex;
            this.headerResKeyOrName = headerResKeyOrName;
            this.width = width;
        }

        public ColumnType getColumnType() {
            return columnType;
        }

        public int getPathStepIndex() {
            return pathStepIndex;
        }

        public String getHeaderResKeyOrName() {
            return headerResKeyOrName;
        }

        public int getWidth() {
            return width;
        }

    }

    /**
     *
     */
    private final List<SingleColumnDefinition> columnDefinitions = new ArrayList<>();

    /**
     * @param width
     */
    public void addColumnEndElement(final int width) {
        addColumn(ColumnType.END_ELEMENT, -1, null, width);
    }

    /**
     * @param index
     * @param width
     */
    public void addColumnOptional(final int index, final int width) {
        addColumn(ColumnType.OPTIONAL, index, null, width);
    }

    /**
     * @param index
     * @param headerResKeyOrName
     * @param width
     */
    public void addColumnPathStepName(final int index, final String headerResKeyOrName, final int width) {
        addColumn(ColumnType.PATH_STEP_NAME, index, headerResKeyOrName, width);
    }

    /**
     * @param columnType
     * @param index
     * @param width
     */
    private void addColumn(final ColumnType columnType, final int index, final String headerResKeyOrName, final int width) {
        SingleColumnDefinition columnDefinition = new SingleColumnDefinition(columnType, index, headerResKeyOrName, width);
        columnDefinitions.add(columnDefinition);
    }

    @Override
    public Iterator<SingleColumnDefinition> iterator() {
        return columnDefinitions.iterator();
    }

    /**
     * @return
     */
    public int columnCount() {
        return columnDefinitions.size();
    }

    /**
     * @param index
     * @return
     */
    public SingleColumnDefinition get(final int index) {
        return columnDefinitions.get(index);
    }

}
