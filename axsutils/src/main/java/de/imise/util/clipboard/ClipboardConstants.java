package de.imise.util.clipboard;

/**
 * Interface, mit allen notwenigen Konstanten für die Klassen dieses Packages.
 * 
 * @author fstephan
 */
interface ClipboardConstants {

    /**
     * Postion des <code>Strings</code> in den <code>String</code> - Inhalten
     * der Zwischenablage, der dem {@link XMLContentParser} übergeben wird.
     */
    int CONTENT_STRING_POSITION = 1;

    /** Identifiziert den Beginn einer neuen Zeile */
    char ROW_TAG = '\n';

    /** Identifiziert den Beginn einer neuen Spalte */
    char COLUMN_TAG = '\t';
}
