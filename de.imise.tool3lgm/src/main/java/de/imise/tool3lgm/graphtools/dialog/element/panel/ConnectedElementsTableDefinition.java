package de.imise.tool3lgm.graphtools.dialog.element.panel;

import static de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption.LABEL_END_ELEMENT_TYPE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.imise.tool3lgm.graphtools.dialog.element.panel.AbstractPathConnectionPanel.PanelLabelOption;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ConnectedElementsTableDefinition.SingleColumnDefinition;
import de.imise.tool3lgm.graphtools.metamodel.elements.OptionalEdge;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;

/**
 * Data object that represents the definition of column headers of a
 * {@link ConnectedElementsTable}.
 *
 * @author AXS (12 Mar 2019)
 */
public class ConnectedElementsTableDefinition implements Iterable<SingleColumnDefinition> {

    /**
     * Resource key of the name of the table and thus of the tab in which the
     * table is located. If it is not <code>null</code> and is not found in the
     * resources, it is set as value itself. If it is <code>null</code>, the
     * {@link #tablePanelLabelOption} is used to label the panel.
     */
    public String tableResKeyOrName = null;

    /**
     * If the {@link #tableResKeyOrName} is <code>null</code>, this option is
     * used to lable the panel and thus the tab of the panel. Default ist
     * {@link PanelLabelOption#LABEL_END_ELEMENT_TYPE}.
     */
    public PanelLabelOption tablePanelLabelOption = LABEL_END_ELEMENT_TYPE;

    /**
     * Defines which part of the metapath of the panel a column represents
     *
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
        PATH_STEP_FULL_NAME, //dasselbe wie PATH_STEP_EDGE_CLASS nur immer der volle Name des Pfadschrittes
        PATH_STEP_BACKWARD_NAME, //dasselbe wie PATH_STEP_EDGE_CLASS nur nur der Rückwärts-Name des Pfadschrittes
        PATH_STEP_FULL_BACKAWARD_NAME; //dasselbe wie PATH_STEP_EDGE_CLASS nur immer der volle Rückwärts-Name des Pfadschrittes
    }

    /**
     * Definition einer einzelnen Spalte.
     *
     * @author AXS (11 Mar 2019)
     */
    public static class SingleColumnDefinition {

        /**
         * Art der Spalte - also welche genaue Information des in dieser Spalte
         * anzuzeigenden Pfadschrittes dargestellt werden soll (z.B. Name des
         * Endelementes, Name der Kante in einer bestimmten Richtung oder
         * Optionalität einer {@link OptionalEdge} im Pfadschritt oder ...
         */
        private final ColumnType columnType;

        /**
         * Index des Pfadschrittes, von dem eine Eigenschaft in der Spalte
         * dargestellt wird.
         */
        private final int pathStepIndex;

        /**
         * Optionaler Parameter, der den Default-Spatennamen mir einem
         * Resourcenstring überschreibt
         */
        private final String headerResKeyOrName;

        /**
         * preferredWidth der Spalte (muss man auch im Verhältnis zu den anderen
         * Spalten und deren preferredWidth sehen). Kann man setzten, wenn einge
         * Spalten breiter als andere sein sollen. Default ist, dass alle gleich
         * breit sind.
         */
        private final int width;

        /**
         * @param columnType Art der Spalte - also welche genaue Information des
         *            in dieser Spalte anzuzeigenden Pfadschrittes dargestellt
         *            werden soll (z.B. Name des Endelementes, Name der Kante in
         *            einer bestimmten Richtung oder Optionalität einer
         *            {@link OptionalEdge} im Pfadschritt oder ...
         * @param pathStepIndex Index des Pfadschrittes, von dem eine
         *            Eigenschaft in der Spalte dargestellt wird.
         * @param headerResKeyOrName Optionaler Parameter, der den
         *            Default-Spatennamen mir einem Resourcenstring überschreibt
         * @param width preferredWidth der Spalte (muss man auch im Verhältnis
         *            zu den anderen Spalten und deren preferredWidth sehen).
         *            Kann man setzten, wenn einge Spalten breiter als andere
         *            sein sollen. Default ist, dass alle gleich breit sind.
         */
        private SingleColumnDefinition(final ColumnType columnType, final int pathStepIndex, final String headerResKeyOrName, final int width) {
            this.columnType = columnType;
            this.pathStepIndex = pathStepIndex;
            this.headerResKeyOrName = headerResKeyOrName;
            this.width = width;
        }

        /**
         * @return type of the column
         */
        public ColumnType getColumnType() {
            return columnType;
        }

        /**
         * @return the index of the path step this column definition is
         *         referencing
         */
        public int getPathStepIndex() {
            return pathStepIndex;
        }

        /**
         * @return the reskey or name of the column header
         */
        public String getHeaderResKeyOrName() {
            return headerResKeyOrName;
        }

        /**
         * @return the width of the column
         */
        public int getWidth() {
            return width;
        }

    }

    /**
     * Liste alle Einzelspaltendefinitionen. Das ist die Reihenfolge, in der die
     * Tabellenspalten initial erzeugt werden.
     */
    private final List<SingleColumnDefinition> columnDefinitions = new ArrayList<>();

    /**
     * Fügt eine Spalte zur Definition hinzu, die die Endelemente der gefundenen
     * Pfade anzeigt.
     *
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnEndElement(final int width) {
        addColumn(ColumnType.END_ELEMENT, -1, null, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die die Optional-Eigenschaft einer
     * {@link OptionalEdge} anzeigt. Der Spaltenkopf ist der Anzeigename der
     * gemeinsamen Oberklasse aller Endklassen aller übergebenen
     * {@link SimpleMetaPath}
     *
     * @param index Index der {@link OptionalEdge} im Pfad
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnOptional(final int index, final int width) {
        addColumn(ColumnType.OPTIONAL, index, null, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die den Namen des Startelementes
     * des Elementarpfades anzeigt.
     *
     * @param index Index des Elementarpfades im Pfad
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnPathStepStart(final int index, final int width) {
        addColumn(ColumnType.PATH_STEP_START, index, null, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die den Namen der Kante des
     * Elementarpfades anzeigt.
     *
     * @param index Index des Elementarpfades im Pfad
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnPathStepEdge(final int index, final int width) {
        addColumn(ColumnType.PATH_STEP_EDGE, index, null, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die den Namen des Endelementes des
     * Elementarpfades anzeigt.
     *
     * @param index Index des Elementarpfades im Pfad
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnPathStepEnd(final int index, final int width) {
        addColumn(ColumnType.PATH_STEP_END, index, null, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die den Namen des Elementarpfades
     * anzeigt - also nur den Kantennamen in der durch den Pfad festgelegten
     * Vorwärts Richtung. Der Spaltenkopf wird aus allen Kantennamen jeweils
     * durch " / " getrennt gebildet.
     *
     * @param index Index des Elementarpfades im Pfad
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnPathStepName(final int index, final int width) {
        addColumnPathStepName(index, null, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die den Namen des Elementarpfades
     * anzeigt - also nur den Kantennamen in der durch den Pfad festgelegten
     * Vorwärts-Richtung.
     *
     * @param index Index des Elementarpfades im Pfad
     * @param headerResKeyOrName Resorucenkey des anzuzeigenden Spaltenkopfes
     *            dieser Spalte. Wird <code>null</code> übergeben, ist das
     *            dassseleb wie bei {@link #addColumnPathStepName(int, int)}
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnPathStepName(final int index, final String headerResKeyOrName, final int width) {
        addColumn(ColumnType.PATH_STEP_NAME, index, headerResKeyOrName, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die den Namen des Elementarpfades
     * anzeigt - also nur den Kantennamen in der durch den Pfad festgelegten
     * Rückwärts-Richtung. Der Spaltenkopf wird aus allen Kantennamen jeweils
     * durch " / " getrennt gebildet.
     *
     * @param index Index des Elementarpfades im Pfad
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnPathStepBackwardName(final int index, final int width) {
        addColumnPathStepBackwardName(index, null, width);
    }

    /**
     * Fügt eine Spalte zur Definition hinzu, die den Namen des Elementarpfades
     * anzeigt - also nur den Kantennamen in der durch den Pfad festgelegten
     * Rückwärts-Richtung.
     *
     * @param index Index des Elementarpfades im Pfad
     * @param headerResKeyOrName Resorucenkey des anzuzeigenden Spaltenkopfes
     *            dieser Spalte. Wird <code>null</code> übergeben, ist das
     *            dassselbe wie bei
     *            {@link #addColumnPathBackwardStepName(int, int)}.
     * @param width preferredWidth der Spalte (muss man auch im Verhältnis zu
     *            den anderen Spalten und deren preferredWidth sehen). Kann man
     *            setzten, wenn einge Spalten breiter als andere sein sollen.
     *            Default ist, dass alle gleich breit sind.
     */
    public void addColumnPathStepBackwardName(final int index, final String headerResKeyOrName, final int width) {
        addColumn(ColumnType.PATH_STEP_BACKWARD_NAME, index, headerResKeyOrName, width);
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
