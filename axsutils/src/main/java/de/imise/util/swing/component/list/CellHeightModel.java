package de.imise.util.swing.component.list;

import javax.swing.SizeSequence;

/**
 * Dient dem definieren von Zellhöhen für eine {@link ExtendedJList}.
 *
 * @see SizeSequence
 * @author fstephan
 */
public interface CellHeightModel {

    /**
     * Gibt die Höhe der Zelle am Index wieder
     * 
     * @param index Index der Zelle innerhalb der Liste, der dieses
     *            {@link CellHeightModel}<br>
     *            zugeordnet ist
     * @return
     * @throws ArrayIndexOutOfBoundsException
     */
    int getHeight(int index) throws ArrayIndexOutOfBoundsException;

    /**
     * Gibt die Anzahl der Einträge in diesem {@link CellHeightModel} wieder,
     * welche äquivalent zur Anzahl der Zellen in der dazugehörigen List sein
     * sollte.
     *
     * @return
     */
    int getSize();
}
