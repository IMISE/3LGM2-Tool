package de.imise.tool3lgm.metamodel.tlgm_v3_0;

import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewDefinition;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.node.Anwendungsbaustein;
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

    @Override
    protected final MetaPath[] getConfigurationPaths() {
        MetaPath[] configurationPaths = {
                //Testpfad über alle Ebenen hinweg
                //new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class),
                new MetaPath(Aufgabe.class, Anwendungsbaustein.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class),
                new MetaPath(Anwendungsbaustein.class, PhysischerDVBaustein.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class),
        };
        return configurationPaths;
    }

}
