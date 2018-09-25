package de.imise.tool3lgm.graphtools.metamodel.elements;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.path.MetaPath;

/**
 * Oberklasse für alle Kantenklassen, die ein Element von einem anderen ableiten, also eine Instanz bilden. Das Instanzelement ist immer das
 * Endelement der Kante und das Oberelement immer das Startelement.
 * Der Mechanismus funktioniert folgendermaßen: Anders als bei allen anderen Kanten legt man diese Kante nicht zwischen 2 existierenden Elementen
 * an, sondern man legt diese Kante für ein Element der Startklasse der Kante an. Dadurch wird das Endelement erst neu erzeugt ("instanziiert").
 *
 * @author AXS (25.09.2018)
 */
public abstract class InstanciationEdge extends Edge {

    /**
     * Sammlung aller Pfade, die ausgehend vom Startelement dieser Kante ebenfalls angelegt werden sollen, wenn eine Instanziierung über diese
     * Kantenklasse durchgeführt wird. <br>
     * Jeder der Pfade muss zwingend bei derselben Klasse starten, bei der diese Kante startet.<br>
     * Der Pfad hat nur einen Effekt, wenn seine Startklasse zur Startklasse dieser Kante zuweisungskompatibel ist und er mind. eine
     * {@link InstanciationEdge} enthält. Der hiermit verbundene Mechanismus geht durch die Kantenklassen des Pfades. Ist die aktuelle
     * Kantenklasse keine {@link InstanciationEdge}, dann suche von den aktuellen Elementen ausgehend (am Anfang ist das das Startelement dieser
     * Kante) alle damit über diese Kantenart verbundenen Elemente und nimmt sie für den nächsten Schritt als Startelemente. Sobald im Pfad eine
     * {@link InstanciationEdge} auftaucht, werden alle Elementarten und Kanten der dahinter liegenden Pfadschritte kompeltt neu erzeugt und die
     * entstehenden Elemente immer mit den vorherigen verbunden. Wenn der Pfad mit einer Klasse endet (was er in den meisten Fällen tun wird, damit
     * das ganze sinnvoll ist), die zuweisungskompatibel zur Endklasse dieser Kante ist (also zum durch diese Kante neu erzeugten Element), dann wird
     * die letzte Verbindung bzw. die letzte Kante hin zum EndElementdieser Kante erzeugt und nicht nochmal ein Element der Endelementart angelegt.
     * Damit kann man "Nebenbedingungspfade" für das Startelement gleich mit anlegen, wenn man das Startelement über diese Kante hier intsanziiert.
     */
    private static ImmutableCollection<MetaPath> instanciableMetaPaths = ImmutableList.of();

    public static void addInstanciableMetaPath(final MetaPath... metaPaths) {
        ImmutableList.Builder<MetaPath> listBuilder = ImmutableList.builder();
        for (MetaPath metaPath : instanciableMetaPaths) {
            listBuilder.add(metaPath);
        }
        for (MetaPath metaPath : metaPaths) {
            listBuilder.add(metaPath);
        }
        instanciableMetaPaths = listBuilder.build();
    }

    public Iterable<MetaPath> iterateInstanciableMetaPaths() {
        return instanciableMetaPaths;
    }

}