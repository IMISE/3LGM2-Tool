package de.imise.tool3lgm.graphtools.metamodel.elements;

/**
 * Oberklasse für alle Kantenklassen, die ein Element von einem anderen ableiten, also eine Instanz bilden. Das Instanzelement ist immer das
 * Endelement der Kante und das Oberelement immer das Startelement.
 * Der Mechanismus funktioniert folgendermaßen: Anders als bei allen anderen Kanten legt man diese Kante nicht zwischen 2 existierenden Elementen
 * an, sondern man legt diese Kante für ein Element der Startklasse der Kante an. Dadurch wird das Endelement erst neu erzeugt ("instanziiert").
 *
 * @author AXS (25.09.2018)
 */
public abstract class InstanciationEdge extends Edge {

}