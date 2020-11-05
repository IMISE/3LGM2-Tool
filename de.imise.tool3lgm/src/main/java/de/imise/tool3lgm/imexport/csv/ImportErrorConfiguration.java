package de.imise.tool3lgm.imexport.csv;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.imise.tool3lgm.imexport.csv.linehandler.line.AbstractImportLine;

/**
 * Collection of {@link ImportError} for a verification
 *
 * @author AXS
 * @create 06.10.2014
 */
public class ImportErrorConfiguration {

    /**
     * List of all {@link ImportError} in the order of their occurance
     */
    private final List<ImportError> errors = new ArrayList<>();

    /**
     * Stores the {@link ImportError.ErrorType} of all already added errors.
     * This is for identifying errors which should not be added twice.
     */
    private final Set<ImportError.ErrorType> addedErrorTypes = new HashSet<>();

    /**
     *
     */
    public ImportErrorConfiguration() {
    }

    /**
     * Adds a new {@link ImportError}
     *
     * @param line The line where the error occurs
     * @param col the colum where the error occurs
     * @param errorType the type of the error
     * @param args String arguments to format the printable error message
     * @return the added error or <code>null</code> if an error with the given
     *         type should not be added again
     */
    public ImportError add(final AbstractImportLine line, final int col, final ImportError.ErrorType errorType, final Object... args) {
        if (errorType.isSingleAddError()) {
            if (addedErrorTypes.contains(errorType)) {
                return null;
            }
            addedErrorTypes.add(errorType);
        }

        ImportError error = createError(line, col, errorType, args);
        errors.add(error);
        return error;
    }

    /**
     * Creates where the error occurs
     *
     * @param line The line where the error occurs
     * @param col the colum where the error occurs
     * @param errorType the type of the error
     * @param args String arguments to format the printable error message
     * @return the created error
     */
    private ImportError createError(final AbstractImportLine line, final int col, final ImportError.ErrorType errorType, final Object... args) {
        int realRow = line != null ? line.getRow() + 1 : -1;
        int realCol = line != null ? col + 1 : -1;
        ImportError importError = ImportError.create(realRow, realCol, errorType, args);
        return importError;
    }

    /**
     * @return <code>true</code> if errors is not empty
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * @return the errors
     */
    public List<ImportError> getErrors() {
        return errors;
    }

}
