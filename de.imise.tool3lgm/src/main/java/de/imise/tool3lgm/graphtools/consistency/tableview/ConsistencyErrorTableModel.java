/**
 *
 */
package de.imise.tool3lgm.graphtools.consistency.tableview;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.DefaultTableModel;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.ModelValidator;
import de.imise.tool3lgm.graphtools.consistency.ModelValidatorDefinition;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MissingPathError;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.util.NamedObjectContainer;

/**
 * @author AXS
 */
public class ConsistencyErrorTableModel extends DefaultTableModel {

    /**
     * @author AXS
     */
    public static enum ColumnNames {
        NUMBER(40),
        ERROR_TYPE(40),
        DESCRIPTION,
        ELEMENT,
        ELEMENT_TYPE,
        CONNECTION_TYPE;

        public final int maxColumnWidth;

        /**
         * Store the toString value because this String is the identifier for
         * the column and it will be compared by object identity (and not
         * equals).
         */
        private String toString;

        private ColumnNames() {
            maxColumnWidth = -1;
        }

        private ColumnNames(final int maxColumnWidth) {
            this.maxColumnWidth = maxColumnWidth;
        }

        public int getMaxColimnWidth() {
            return -1;
        }

        @Override
        public String toString() {
            if (toString == null) {
                toString = getResString("ColumnNames_" + name());
            }
            return toString;
        }

    }

    /**
     *
     */
    public ConsistencyErrorTableModel() {
        ColumnNames[] colIdentifiers = ColumnNames.values();
        setColumnIdentifiers(colIdentifiers);
    }

    /**
     * @param modelValidator
     */
    public void setErrors(final ModelValidator modelValidator) {
        //ignore unfixable missingPath errors. They can be only fixed
        //with other fixable missingPathErrors (if defined)
        Collection<AbstractConsistencyError> errors = modelValidator == null ? new ArrayList<>(0) : modelValidator.getInconsistencies();
        dataVector.clear();
        setRowCount(errors.size());

        int i = 0;
        for (AbstractConsistencyError error : errors) {

            if (error instanceof MissingPathError) {
                ModelValidatorDefinition modelValidatorDefinition = error.getModelValidatorDefinition();
                if (!modelValidatorDefinition.isSolutionExecuteable(error)) {
                    int rowCount = getRowCount();
                    setRowCount(rowCount - 1);
                    continue;
                }
            }

            // Zeilennummer
            setValueAt(i + 1, i, ColumnNames.NUMBER);

            // Fehlertyp
            String errorTypeString = error.getTypeString();
            NamedObjectContainer<AbstractConsistencyError> type = new NamedObjectContainer<>(error, errorTypeString);
            setValueAt(type, i, ColumnNames.ERROR_TYPE);

            ModelElement me = error.getModelElement();

            // Elementtyp
            GDCollection gdcoll = error.getCollection();
            ElementsNameBuilder elementsNameBuilder = gdcoll.getElementsNameBuilder();
            Class<? extends ModelElement> elementClass = me.getClass();
            String displayableClassName = elementsNameBuilder.getDisplayableName(elementClass);
            setValueAt(displayableClassName, i, ColumnNames.ELEMENT_TYPE);

            // Element
            setValueAt(me, i, ColumnNames.ELEMENT);

            // Verbindungsart / Feld
            String errorFieldString = error.getErrorFieldString();
            setValueAt(errorFieldString, i, ColumnNames.CONNECTION_TYPE);

            // Beschreibung + Tooltip
            String errorToolTip = error.getLongMessage();
            String errorDescription = error.getMessage();
            //getToolTip(int row) uses this NamedObjectContainer to get the tooltip for the whole row
            NamedObjectContainer<String> errorDescriptionContainer = new NamedObjectContainer<>(errorToolTip, errorDescription);
            setValueAt(errorDescriptionContainer, i, ColumnNames.DESCRIPTION);

            i++;
        }
    }

    /**
     * @param row
     * @return
     */
    @SuppressWarnings("unchecked")
    public String getToolTip(final int row) {
        int column = ColumnNames.DESCRIPTION.ordinal();
        Object valueAt = getValueAt(row, column);
        NamedObjectContainer<String> descriptionWithToolTip = (NamedObjectContainer<String>) valueAt;
        return descriptionWithToolTip.getFirstItem();
    }

    /**
     * @param aValue
     * @param row
     * @param columnIdentifier
     */
    private void setValueAt(final Object aValue, final int row, final ColumnNames columnIdentifier) {
        int column = columnIdentifier.ordinal();
        setValueAt(aValue, row, column);
    }

}
