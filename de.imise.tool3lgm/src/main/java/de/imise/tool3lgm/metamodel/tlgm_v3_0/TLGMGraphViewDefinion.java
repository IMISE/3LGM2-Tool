package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import de.imise.tool3lgm.graphtools.view.graph.GraphViewDefinition;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Aufgabe;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Objekttyp;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.RechAnwendungsbaustein;

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
