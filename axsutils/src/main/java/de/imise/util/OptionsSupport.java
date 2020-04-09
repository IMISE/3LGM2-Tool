package de.imise.util;

import java.util.Collection;
import java.util.HashSet;

/**
 * Klasse, die Funktionalität bereit stellt, um das den Zustand von boolean-Optionen zu verwalten.
 *
 * @author AXS (03.04.2018)
 */
public class OptionsSupport {

    /**
     * Wenn eine MODEL_OPTION für dieses GraphDocument true ist, dann muss das zugehörige GDCommand in dieser Collection sein.
     */
    private final Collection<Object> trueOptions = new HashSet<>();

    /**
     * Liefert <code>true</code>, wenn die Option mit der übergebenen ID zuvor auf <code>true</code> gesetzt wurde.
     *
     * @param optionId
     * @return
     */
    public boolean isOptionTrue(final Object optionId) {
        return trueOptions.contains(optionId);
    }

    /**
     * @param optionId
     * @param value
     * @return
     */
    public boolean setOption(final Object optionId, final boolean value) {
        if (!value) {
            return trueOptions.remove(optionId);
        }
        boolean oldValue = isOptionTrue(optionId);
        if (!oldValue) {
            trueOptions.add(optionId);
        }
        return oldValue;
    }

    /**
     * Dreht den Wert einer Boolean-Option um.
     *
     * @param optionId
     * @return den neuen Wert der Option
     */
    public boolean switchOption(final Object optionId) {
        return !setOption(optionId, !isOptionTrue(optionId));
    }

}
