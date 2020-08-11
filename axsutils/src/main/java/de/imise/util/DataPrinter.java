package de.imise.util;

/**
 * Einfaches Interface um schnell Debug-Ausgaben zu bekommen.
 *
 * @author AXS (26 Jun 2019)
 */
public interface DataPrinter {

    public final boolean DEBUG = true;

    public default boolean isDebug() {
        return DEBUG;
    }

    public default boolean isDebugErrors() {
        return isDebug();
    }

    public default void print(final Object o) {
        if (isDebug()) {
            Sys.outn(0, o);
        }
    }

    public default void print() {
        if (isDebug()) {
            Sys.outn(0, "");
        }
    }

    public default void printe(final Object o) {
        if (isDebugErrors()) {
            Sys.errm(1, 1, o);
        }
    }

    public default void printe() {
        if (isDebugErrors()) {
            Sys.errm(1, 1, "");
        }
    }

}
