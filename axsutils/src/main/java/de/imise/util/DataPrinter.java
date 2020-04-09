package de.imise.util;

import java.io.PrintStream;
import java.util.Iterator;

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

    public default void print(final Iterator<?> it, final boolean err) {
        if (isDebug()) {
            @SuppressWarnings("resource")
            PrintStream stream = err ? System.err : System.out;
            while (it.hasNext()) {
                Object next = it.next();
                stream.print(next);
                stream.print("\t");
            }
            stream.println();
        }
    }

    public default void print(final Object o, final boolean err) {
        if (isDebug()) {
            @SuppressWarnings("resource")
            PrintStream stream = err ? System.err : System.out;
            stream.println(String.valueOf(o));
        }
    }

    public default void print(final boolean err) { // da der err-Printer und der out-Printer nichr synchron laufen, muss auch das hier mit dem richtigen Parameter versehen sein
        if (isDebug()) {
            @SuppressWarnings("resource")
            PrintStream stream = err ? System.err : System.out;
            stream.println();
        }
    }

    public default void print(final Iterator<?> it) {
        print(it, false);
    }

    public default void print(final Object o) {
        print(o, false);
    }

    public default void print() {
        print(false);
    }

    public default void printe(final Iterator<?> it) {
        print(it, true);
    }

    public default void printe(final Object o) {
        print(o, true);
    }

    public default void printe() {
        print(true);
    }

}
