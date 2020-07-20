/**
 *
 */
package de.imise.tool3lgm.userproperties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;

/**
 * @author AXS (20.07.2020)
 */
public abstract class AbstractUserProperties {

    /** Mit diesem Namenspräfix sind alle Properties zu versehen, die nicht gepspeichert werden sollen */
    public static final String TRANSIENT_PROPERTY_NAME_PREFIX = "TRANSIENT_";

    /**
     * Liefert <code>true</code>, wenn der übergebene Property-Name mit dem String beginnt, der angibt, dass diese Property nicht gespeichert werden
     * soll.
     *
     * @param propertyName
     * @return
     */
    protected static final boolean isTransient(final Object propertyName) {
        return propertyName.toString().startsWith(TRANSIENT_PROPERTY_NAME_PREFIX);
    }

    /**
     * Das eigentliche Property-Objekct, das alle Properties aufnimmt
     */
    static Properties properties = new Properties();

    /**
     * Stellt Property-Change-Funktionalität zur Verfügung. <br>
     * Zu der Klasse <code>PropertyChangeSupport</code> werden alle Property-Change-Listener
     * hinzugefügt und in <code>firePorpertyChange()</code> werden alle Listener benachrichtigt.
     */
    private static PropertyChangeSupport changeSupport = new PropertyChangeSupport(UserProperties.class);

    ///////////////////////////////////////////////////
    // Listener hinzufügen/entfernen/benachrichtigen //
    ///////////////////////////////////////////////////

    /**
     * Fügt einen <code>PropertyChangeListener</code> hinzu
     *
     * @param listener
     */
    public static final void addPropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Entfernt einen <code>PropertyChangeListener</code>
     *
     * @param listener
     */
    public static final void removePropertyChangeListener(final PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Sendet an alle PropertyChangeListener das Ereignis, dass sich etwas geändert hat
     *
     * @param property
     * @param oldValue
     * @param newValue
     */
    public static final void firePropertyChange(final Object property, final String oldValue, final String newValue) {
        changeSupport.firePropertyChange(property.toString(), oldValue, newValue);
    }

    /**
     * Prüft, ob das ChangeEvent für das übergebene Property-Objekt ausgelöst wurde
     *
     * @param property
     * @param event
     * @return
     */
    public static final boolean isPropertyChange(final Object property, final PropertyChangeEvent event) {
        return property.toString().equals(event.getPropertyName());
    }

    /**
     * Fügt für die übergebene Property den übergebenen Wert hinzu
     *
     * @param key
     * @param value
     * @return the old value
     */
    protected static Object put(final Object key, final Object value) {
        String newValue = String.valueOf(value);
        Object oldValue = properties.put(key.toString(), newValue);
        firePropertyChange(key, oldValue == null ? null : oldValue.toString(), newValue);
        return oldValue;
    }

}
