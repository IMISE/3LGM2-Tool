package de.imise.tool3lgm.imexport.csv;

/**
 * Represents an import error
 *
 * @author AXS
 * @create 06.10.2014
 */
public class ImportError {

    /**
     * The type of the error with a printable representation.
     *
     * @author AXS
     * @create 06.10.2014
     */
    public static enum ErrorType {
        FILE_ERROR(""),

        UNKNOWN_LINE("Row %s ErrorType in row"),

        HEADER_UNKNOWN_ELEMENT_TYPE("Unknown element type %s"),
        //        HEADER_MISSING_NAME_COLUMN("Missing columns 'Name', 'Description' and 'ID_String'"),
        //        HEADER_MISSING_DESCRIPTION_COLUMN("Missing columns 'Description' and 'ID_String'"),
        //        HEADER_MISSING_ID_COLUMN("Missing column 'ID_String'"),
        HEADER_MISSING("Header is missing", true),

        ID_DUPLICATE("Duplicate ID \'%s\' in line %s"),
        ID_CONFLICT("ID conflict. There is an other element with a different element class but the same has string. %s \'%s\' with ID \'%s\'"),
        USERFIELD_MISSING("Missing user field. Define a user field \'%s\' for element class \'%s\'"),

        NODE_OR_EDGE_EMPTY_NAME("An empty elemnt name is not allowed."),

        EDGE_HEADER_MISSING_START_ID_COLUMN("Missing column 'Start_ID'"),
        EDGE_HEADER_MISSING_END_ID_COLUMN("Missing column 'End_ID'"),
        EDGE_START_ID_MISSING("Missing start ID"),
        EDGE_END_ID_MISSING("Missing end ID"),
        EDGE_START_ID_NO_ELEMENT_FOUND("No element found for start ID \'%s\'"),
        EDGE_END_ID_NO_ELEMENT_FOUND("No element found for end ID \'%s\'"),
        EDGE_START_ID_NOT_MATCHING("Element \'%s\' with class \'%s\' doesn't match the egde start class"),
        EDGE_END_ID_NOT_MATCHING("Element \'%s\' with class \'%s\' doesn't match the egde end class"),

        ;

        /** The printable representation of the error */
        private final String errorMessage;

        /** <code>true</code> for errors which should be added only once */
        private final boolean singleAdd;

        /** Number of arguments in the errorMessage */
        private final int argumentsCount;

        /**
         * @param errorMessage
         */
        private ErrorType(final String errorMessage) {
            this(errorMessage, false);
        }

        /**
         * @param errorMessage
         * @param singleAdd
         */
        private ErrorType(final String errorMessage, final boolean singleAdd) {
            this.errorMessage = errorMessage;
            this.singleAdd = singleAdd;
            argumentsCount = countArguments();
        }

        @Override
        public String toString() {
            return errorMessage;
        }

        /**
         * @return <code>true</code> if more than one occurance of this error is
         *         irrelevant
         */
        public boolean isSingleAddError() {
            return singleAdd;
        }

        /**
         * Counts the number of arguments in the errorMessage
         *
         * @return
         */
        private int countArguments() {
            int counter = 0;
            int index = errorMessage.indexOf("%s");
            while (index >= 0) {
                counter++;
                index = errorMessage.indexOf("%s", index + 1);
            }
            return counter;
        }

        /**
         * @return the number of arguments in the errorMessage
         */
        public int getArgumentsCount() {
            return argumentsCount;
        }
    }

    /** The type of this error */
    private final ErrorType errorType;

    /** The message for this error */
    private final String errorString;

    /**
     * @param errorString message for this error
     * @param errorType type of this error
     */
    private ImportError(final String errorString, final ErrorType errorType) {
        this.errorType = errorType;
        this.errorString = errorString;
    }

    /**
     * Creates an import error
     *
     * @param row row number
     * @param col column number
     * @param errorType error type
     * @param args arguments to format the error message
     * @return a new Import error
     */
    public static ImportError create(final int row, final int col, final ErrorType errorType, final Object... args) {
        String errorString = createErrorString(row, col, errorType, args);
        ImportError importError = new ImportError(errorString, errorType);
        return importError;
    }

    /**
     * Formats the error string with the given parameters
     *
     * @param row Row number for the error message. If the row number is lower
     *            than 0 it will be ignored and not added to the message.
     * @param col Column number for the error message. If the column number is
     *            lower than 0 it will be ignored and not added to the message.
     * @param errorType The error type which prescribe the error message
     * @param args Arguments to format the error message
     * @return The formatted error message string
     */
    private static String createErrorString(final int row, final int col, final ErrorType errorType, final Object... args) {
        String details = String.format(errorType.toString(), args);
        String rowString = row >= 0 ? "Row " + row + " " : "";
        String colString = col >= 0 ? "Column " + col + " " : "";
        String errString = rowString + colString + details;
        return errString;
    }

    /**
     * @return the error message string
     */
    public String getErrorString() {
        return errorString;
    }

    /**
     * @return the error type
     */
    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return getErrorString();
    }

}
