package de.imise.tool3lgm.graphtools.consistency.error;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;

public abstract class AbstractError extends Error {

    /**
     * Suffix, der an den SimpleClassName gehängt wird, um die genaue BEschreibung des Fehlers
     * aus den Error-Resourcen zu laden.
     */
    protected static final String ERROR_DESCRIPTION_SUFFIX = "_descrip";

    /**
     * Dieser Placeholder kann zum Beispiel in der Beschreibung in den Resourcen verwendet werden,
     * um die Stelle zu markieren, an der ein Wert eingefügt werden soll. Das kann beliebig oft
     * sein, Hauptsache man hält sich bei den übergebenen Werten an die an die Reihenfolge.
     */
    protected static final String ERROR_PLACEHOLDER = "$value";

    /**
     * Element, das zu anderen Elementen zuviele Verbindungen hat
     */
    protected ModelElement me;

    /**
     * Klasse der Verbindungen, deren Instanzanzahl für das Modellelement zu hoch oder zu niedrig
     * ist.
     */
    protected Object errorField;

    /**
     * Das Modell in dem der Fehler auftrat
     */
    protected GDCollection gdcoll;

    public AbstractError(final ModelElement me, final Object errorField, final GDCollection gdcoll) {
        this.me = me;
        this.errorField = errorField;
        this.gdcoll = gdcoll;
    }

    @Override
    public final String getMessage() {
        return getMessageBuilder().toString();
    }

    /**
     * Liefert einen {@link StringBuilder} mit der Fehlermeldung
     *
     * @return
     */
    protected StringBuilder getMessageBuilder() {
        StringBuilder sb = new StringBuilder();
        String errClassName = getClass().getSimpleName();
        sb.append(getResString(errClassName + ERROR_DESCRIPTION_SUFFIX));
        replacePlaceHolder(sb);
        return sb;
    }

    /**
     * Liefert alle Replacements für die Message (muss in ggf. Unterklassen überschrieben werden)
     *
     * @return
     */
    protected String[] getMessageReplaceArguments() {
        return null;
    }

    protected StringBuilder replacePlaceHolder(final StringBuilder messageBuilder) {
        String[] replacements = getMessageReplaceArguments();
        int replacementIndex = 0;
        if (replacements != null && replacements.length > 0) {
            int placeholderStart = messageBuilder.indexOf(ERROR_PLACEHOLDER);
            while (placeholderStart >= 0) {
                int placeholderEnd = placeholderStart + ERROR_PLACEHOLDER.length();
                messageBuilder.replace(placeholderStart, placeholderEnd, replacements[replacementIndex]);
                int offset = placeholderStart + replacements[replacementIndex].length();
                placeholderStart = messageBuilder.indexOf(ERROR_PLACEHOLDER, offset);
                replacementIndex++;
            }
        }
        return messageBuilder;
    }

    /**
     * Liefert einen String, der im ErrorTable in der Spalte "Verbindungstyp / Feld" angezeigt wird.
     *
     * @return
     */
    public abstract String getErrorFieldString();

    /**
     * Liefert das ModelElement mit dem Fehler
     *
     * @return the me
     */
    public final ModelElement getModelElement() {
        return me;
    }

    /**
     * Liefert die {@link GDCollection} mit dem Fehler
     *
     * @return the gdcoll
     */
    public final GDCollection getCollection() {
        return gdcoll;
    }

    /**
     * Liefert einen String, der für jede Art von Fehler eindeutig sein sollte (z.B. "MIN", "MAX" oder "ID").
     * Wird in den Resourcen nichts gefunden, wird "ERROR" zurück gegeben.
     *
     * @return
     */
    public final String getTypeString() {
        Class<?> clazz = getClass();
        String type = null;
        while (type == null && clazz != Object.class) {
            try {
                type = getResString(clazz.getSimpleName());
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        return type == null ? "ERROR" : type;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (super.equals(obj)) {
            return true;
        }
        if (!(obj.getClass() == getClass())) {
            return false;
        }
        AbstractError ce = (AbstractError) obj;
        if (me != ce.me || errorField != ce.errorField) {
            return false;
        }
        return true;
    }

}
