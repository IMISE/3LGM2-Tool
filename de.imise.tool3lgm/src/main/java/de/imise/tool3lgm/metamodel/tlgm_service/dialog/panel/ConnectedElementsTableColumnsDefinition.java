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

        public SingleColumnDefinition(final ColumnType columnType, final String headerResKeyOrName) {
            this(columnType, -1, headerResKeyOrName);
        }

        public SingleColumnDefinition(final ColumnType columnType, final int pathStepIndex, final String headerResKeyOrName) {
            this.columnType = columnType;
            this.pathStepIndex = pathStepIndex;
            this.headerResKeyOrName = headerResKeyOrName;
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

    }

    /**
     *
     */
    private final List<SingleColumnDefinition> columnDefinitions = new ArrayList<>();

    /**
     *
     */
    public void addColumnEndElement() {
        addColumn(ColumnType.END_ELEMENT, -1, null);
    }

    /**
     * @param index
     */
    public void addColumnOptional(final int index) {
        addColumn(ColumnType.OPTIONAL, index, null);
    }

    /**
     * @param index
     * @param headerResKeyOrName
     */
    public void addColumnPathStepName(final int index, final String headerResKeyOrName) {
        addColumn(ColumnType.PATH_STEP_NAME, index, headerResKeyOrName);
    }

    /**
     * @param columnType
     * @param index
     */
    private void addColumn(final ColumnType columnType, final int index, final String headerResKeyOrName) {
        SingleColumnDefinition columnDefinition = new SingleColumnDefinition(columnType, index, headerResKeyOrName);
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
