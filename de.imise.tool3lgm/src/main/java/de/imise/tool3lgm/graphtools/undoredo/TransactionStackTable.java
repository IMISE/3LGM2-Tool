package de.imise.tool3lgm.graphtools.undoredo;

import java.util.HashMap;

/**
 * Table, der von der ID einer Transaktion auf die Anzahl der gestarteten Untertransaktionen mit derselben ID mappt.
 * Eine Aktion wie "Lösche Aufgabe X aus Gesamtmodell" hat zur Folge, dass in jedem Teilmodell eine Transaktion
 * "Lösche Aufgabe X aus Teilmodell" gestartet wird. Über diese Map kann man für das Löschen aus dem Teilmodell
 * festestellen, dass es als Unteraktion einer anderen Transaktion gestartet wurde und nicht selbst die äußerste
 * Transaktion war.
 *
 * @author AXS (23.10.2017) aus GDCollection extrahiert
 */
public class TransactionStackTable extends HashMap<Integer, Integer> {

    public TransactionStackTable() {
    }

    public int increase(final int pid) {
        Integer transStackInteger = remove(pid);
        int transStackInt = transStackInteger == null ? 1 : transStackInteger.intValue() + 1;
        put(pid, transStackInt);
        return transStackInt;
    }

    public void decrease(final int pid) {
        Integer transStackInt = remove(pid) - 1;
        if (transStackInt > 0) {
            put(pid, transStackInt);
        }
    }

}
