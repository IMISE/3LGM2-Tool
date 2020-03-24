package de.imise.tool3lgm.graphtools.consistency.error;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;

/**
 * An error object that holds the model ({@link GDCollection}) and a model element that is
 * the source of the error. Additionally it stores an object to identify the erorr type.
 *
 * @author AXS (20.03.2008)
 */
public abstract class AbstractConsistencyError extends Error {

    /**
     * Suffix, der an den SimpleClassName gehängt wird, um die genaue BEschreibung des Fehlers
     * aus den Error-Resourcen zu laden.
     */
    protected static final String ERROR_TYPE_RESOURCE_KEY_SUFFIX = "_type";

    /**
     * Suffix, der an den SimpleClassName gehängt wird, um die genaue Beschreibung des Fehlers
     * aus den Error-Resourcen zu laden. Diese Beschreibung steht in den allgemeinen Resourcen
     * des Baukastens.
     */
    protected static final String ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX = "_descrip";

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

    public AbstractConsistencyError(final ModelElement me, final Object errorField, final GDCollection gdcoll) {
        this.me = me;
        this.errorField = errorField;
        this.gdcoll = gdcoll;
    }

    @Override
    public String getMessage() {
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
        sb.append(getResString(errClassName + ERROR_DESCRIPTION_RESOURCE_KEY_SUFFIX));
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
     * @return the errorField
     */
    public Object getErrorField() {
        return errorField;
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
                String resourceKey = clazz.getSimpleName();
                resourceKey += ERROR_TYPE_RESOURCE_KEY_SUFFIX;
                type = getResString(resourceKey);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        return type == null ? "ERROR" : type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (errorField == null ? 0 : errorField.hashCode());
        result = prime * result + (gdcoll == null ? 0 : gdcoll.hashCode());
        result = prime * result + (me == null ? 0 : me.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractConsistencyError other = (AbstractConsistencyError) obj;
        if (errorField == null) {
            if (other.errorField != null) {
                return false;
            }
        } else if (!errorField.equals(other.errorField)) {
            return false;
        }
        if (gdcoll == null) {
            if (other.gdcoll != null) {
                return false;
            }
        } else if (!gdcoll.equals(other.gdcoll)) {
            return false;
        }
        if (me == null) {
            if (other.me != null) {
                return false;
            }
        } else if (!me.equals(other.me)) {
            return false;
        }
        return true;
    }

}
