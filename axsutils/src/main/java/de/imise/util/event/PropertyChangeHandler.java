package de.imise.util.event;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class PropertyChangeHandler {

    /**
     * Stellt Property-Change-Funktionalität zur Verfügung. <br>
     * Zu der Klasse <code>PropertyChangeSupport</code> werden alle Property-Change-Listener
     * hinzugefügt und in <code>firePorpertyChange()</code> werden alle Listener benachrichtigt.
     */
    protected PropertyChangeSupport changeSupport = null;

    public PropertyChangeHandler() {
        super();
    }

    public PropertyChangeHandler(final Object sourceBean) {
        super();
        changeSupport = new PropertyChangeSupport(sourceBean);
    }

    //Listener hinzufügen/entfernen/benachrichtigen

    /**
     * Fügt einen <code>PropertyChangeListener</code> hinzu
     *
     * @param listener
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        if (changeSupport == null) {
            changeSupport = new PropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Entfernt einen <code>PropertyChangeListener</code>
     *
     * @param listener
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        if (changeSupport != null) {
            changeSupport.removePropertyChangeListener(listener);
        }
    }

    /**
     * Sendet an alle <code>PropertyChangeListener</code> ein <code>PropertyChangeEvent</code>
     * mit dem Namen <code>propertyName</code>.
     *
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        if (changeSupport != null) {
            changeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Sendet an alle <code>PropertyChangeListener</code> ein <code>PropertyChangeEvent</code>
     * mit dem Namen <code>propertyName</code>.
     *
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    protected void firePropertyChange(final Enum<?> propertyName, final Object oldValue, final Object newValue) {
        firePropertyChange(propertyName.name(), oldValue, newValue);
    }

    //Ende Listener hinzufügen/entfernen/benachrichtugen

}
