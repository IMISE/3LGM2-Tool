package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.common.base.Strings;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.WeightReplacer;
import de.imise.util.NamedObjectContainer;

public class WeightReplaceTableModel extends AbstractTableModel {

    public WeightReplaceTableModel(final GraphDocument doc) {
        super(doc);
    }

    public WeightReplaceTableModel(final GraphDocument doc, final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        super(doc);
        setData(elementClass, edgeClass);
    }

    /**
     * Setzt Verteilungsgewichtsersetzungsgewichte
     *
     * @param elementClass
     * @param edgeClass
     */
    public void setData(final Class<? extends ModelElement> elementClass, final Class<? extends Edge> edgeClass) {
        //Elementklassen in den Zeilen
        List<ModelElement> allRowElements = doc.getModelItems(elementClass, false, true);
        //Alle KennzahluserFields in den Spalten
        List<UserField> allColumnElements = definitions.getUserFields(edgeClass, UserField.Style.CLASSIFICATION_NUMBER_STYLES);
        //Platzhalter für die Gleichverteilung
        allColumnElements.add(0, null);

        //falls man Filter wollte (z.B. nur Blattelemente oder so etwas, müsste man das hier tun
        //Analgo zu den anderen Panels
        List<ModelElement> rowElements = new ArrayList<>();
        List<UserField> columnElements = new ArrayList<>();
        Object[][] data = new Object[allRowElements.size()][allColumnElements.size()];

        // temp_data, rowElements, columnElements erstellen
        for (ModelElement re : allRowElements) {
            for (UserField ce : allColumnElements) {
                int columnIndex = columnElements.indexOf(ce);
                if (columnIndex == -1) {
                    columnIndex = columnElements.size();
                    columnElements.add(ce);
                }

                int rowIndex = rowElements.indexOf(re);
                if (rowIndex == -1) {
                    rowIndex = rowElements.size();
                    rowElements.add(re);
                }

                WeightReplacer weightReplacer = definitions.getWeightReplacer();
                String replaceUserFieldHash;
                //im Falle des Platzhalters für das nicht wirklich vorhandene UserFieldWeight für die Gleichverteilung
                if (ce == null) {
                    replaceUserFieldHash = weightReplacer.getUniformDistributionReplacement(re.getHashString(), edgeClass);
                } else { //ansonsten einfach den genauen Ersetzungshash suchen
                    replaceUserFieldHash = weightReplacer.getReplacement(re.getHashString(), ce.getHashCode());
                }
                //wenn ein Ersetzungshash gefunden wurde
                if (!Strings.isNullOrEmpty(replaceUserFieldHash)) {
                    UserField replaceUserField = definitions.getUserField(replaceUserFieldHash);
                    data[rowIndex][columnIndex] = getValueContainer(replaceUserField);
                } else {
                    data[rowIndex][columnIndex] = getBlankValueContainer(ce);
                }
            }
        }

        // RowHeader aufbauen
        Vector<NamedObjectContainer<?>> rowIdentifiers = new Vector<>(rowElements.size());
        for (int i = 0; i < rowElements.size(); i++) {
            ModelElement me = rowElements.get(i);
            rowIdentifiers.add(new NamedObjectContainer<>(me, me.getName()));
        }

        // ColumnHeader aufbauen
        Vector<NamedObjectContainer<?>> columnIdentifiers = new Vector<>(columnElements.size());
        for (int j = 0; j < columnElements.size(); j++) {
            UserField userField = columnElements.get(j);
            //wenn das UserField null ist, dann ist das der Platzhalter für die Gleichverteilung
            columnIdentifiers.add(getValueContainer(userField));
        }

        // Daten setzen
        this.setDataVector(data, columnIdentifiers, rowIdentifiers);
    }

    @Override
    public final NamedObjectContainer<UserField> getContainerForNewValue(final Object value, final int row, final int col) {
        @SuppressWarnings("unchecked")
        NamedObjectContainer<UserField> newValue = (NamedObjectContainer<UserField>) value;
        return newValue;
    }

    @Override
    public void clearValueAt(final int row, final int col) {
        @SuppressWarnings("unchecked")
        NamedObjectContainer<UserField> columnIndentifier = (NamedObjectContainer<UserField>) columnIdentifiers.get(col);
        UserField userField = columnIndentifier.getObject();
        NamedObjectContainer<UserField> blankContainer = getBlankValueContainer(userField);
        setValueAt(blankContainer, row, col);
    }

    /**
     * Liefert einen Container, für die Gleichverteilung.
     *
     * @return
     */
    public static NamedObjectContainer<UserField> getUniformlyDistributedValueContainer() {
        return NamedObjectContainer.of(null, Tool3lgmConstants.getResString("uniformly_distributed"));
    }

    /**
     * Liefert <code>true</code>, wenn der übergebene Container eine Gleichverteilung repräsentiert (also
     * das Object im Container <code>null</code> ist).
     *
     * @param value
     * @return
     */
    public static boolean isUniformlyDistributedValueContainer(final NamedObjectContainer<UserField> value) {
        return value != null && value.getObject() == null;
    }

    /**
     * Liefert einen Container, bei dem als String der Name des übergebenen UserFields gesetzt ist.
     * Ist das übergebene UserField <code>null</code>, kommt en Container für die Gleichverteilung zurück.
     *
     * @param userField
     * @return
     */
    public static NamedObjectContainer<UserField> getValueContainer(final UserField userField) {
        if (userField == null) {
            return getUniformlyDistributedValueContainer();
        }
        return NamedObjectContainer.of(userField, userField.getName());
    }

    /**
     * Liefert einen Container für das übergebene UserField, dessen String ein Leerzeichen ist.
     *
     * @param userField
     * @return
     */
    public static NamedObjectContainer<UserField> getBlankValueContainer(final UserField userField) {
        return NamedObjectContainer.of(userField, " ");
    }

}
