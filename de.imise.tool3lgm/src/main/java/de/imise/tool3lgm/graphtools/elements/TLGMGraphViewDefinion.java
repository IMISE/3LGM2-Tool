package de.imise.tool3lgm.graphtools.elements;

import de.imise.tool3lgm.graphtools.elements.node.Aufgabe;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Objekttyp;
import de.imise.tool3lgm.graphtools.elements.node.PhysischerDVBaustein;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewDefinition;

public class TLGMGraphViewDefinion extends GraphViewDefinition {

    @SuppressWarnings("unchecked")
    @Override
    protected final Class[] getPaintableNodes() {
        //diese Funtkion wird nur ein einziges Mal aufgerufen, daher ist es ok,
        //dass das Array hier in der Funktion immer wieder neu angelegt wird
        Class[] graphViewVisibleNodes = {
                Aufgabe.class,
                Objekttyp.class,
                RechAnwendungsbaustein.class,
                KonAnwendungsbaustein.class,
                Datenbanksystem.class,
                Bausteinschnittstelle.class,
                Benutzungsschnittstelle.class,
                PhysischerDVBaustein.class
        };
        return graphViewVisibleNodes;
    }

}
