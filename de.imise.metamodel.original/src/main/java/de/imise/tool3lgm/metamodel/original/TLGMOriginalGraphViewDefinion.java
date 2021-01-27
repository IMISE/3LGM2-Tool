package de.imise.tool3lgm.metamodel.original;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout.SHAPE;
import de.imise.tool3lgm.metamodel.original.edge.AufAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbAwbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.AwbkAufOrgVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.KommBeziehung;
import de.imise.tool3lgm.metamodel.original.edge.LogspReprVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.ObjReprVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbPdvbkVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PdvbkAwbVerbindung;
import de.imise.tool3lgm.metamodel.original.node.Anwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.Aufgabe;
import de.imise.tool3lgm.metamodel.original.node.Bausteinschnittstelle;
import de.imise.tool3lgm.metamodel.original.node.Benutzungsschnittstelle;
import de.imise.tool3lgm.metamodel.original.node.Datenbanksystem;
import de.imise.tool3lgm.metamodel.original.node.Dokumentensammlung;
import de.imise.tool3lgm.metamodel.original.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.metamodel.original.node.LogischerSpeicher;
import de.imise.tool3lgm.metamodel.original.node.Objekttyp;
import de.imise.tool3lgm.metamodel.original.node.Organisationsplan;
import de.imise.tool3lgm.metamodel.original.node.PhysischerDVBaustein;
import de.imise.tool3lgm.metamodel.original.node.RechAnwendungsbaustein;
import de.imise.util.pair.Pair;

public class TLGMOriginalGraphViewDefinion extends GraphViewDefinition {

    /**
     * @param metaModel
     */
    public TLGMOriginalGraphViewDefinion(final MetaModel metaModel) {
        super(metaModel);
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    protected final Class[] getPaintableNodes() {
        //diese Funtkion wird nur ein einziges Mal aufgerufen, daher ist es ok,
        //dass das Array hier in der Funktion immer wieder neu angelegt wird
        //Die Reihenfolge in dieser Liste legt fest, in welcher Reihenfolge die Elemente in dem gloabeln LayoutEditor angezeigt werden
        Class[] graphViewVisibleNodes = {
                Aufgabe.class, Objekttyp.class, RechAnwendungsbaustein.class, KonAnwendungsbaustein.class, Datenbanksystem.class, Dokumentensammlung.class, Bausteinschnittstelle.class, Benutzungsschnittstelle.class, PhysischerDVBaustein.class
        };
        return graphViewVisibleNodes;
    }

    @Override
    protected final SimpleMetaPath[] getConfigurationPaths() {
        SimpleMetaPath[] configurationPaths = {
                //Testpfad über alle Ebenen hinweg
                //new MetaPath(Aufgabe.class, PhysischerDVBaustein.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, Aufgabe.class, Anwendungsbaustein.class, AufAufOrgVerbindung.class, AwbkAufOrgVerbindung.class, AwbAwbkVerbindung.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, Objekttyp.class, LogischerSpeicher.class, ObjReprVerbindung.class, LogspReprVerbindung.class),
                SimpleMetaPathCreator.createSimpleMetaPath(metaModel, Anwendungsbaustein.class, PhysischerDVBaustein.class, PdvbkAwbVerbindung.class, PdvbPdvbkVerbindung.class),
        };
        return configurationPaths;
    }

    @Override
    public List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> getHidableIfNotConnected() {
        return ImmutableList.of(new Pair<>(Bausteinschnittstelle.class, KommBeziehung.class));
    }

    @Override
    protected void initDefaultElementLayout() {
        setDefaultLayout(Aufgabe.class, SHAPE.rechteck, GraphElementLayout.COLORS[GraphElementLayout.RED]);
        setDefaultLayout(Objekttyp.class, GraphElementLayout.SHAPE.oval, GraphElementLayout.COLORS[GraphElementLayout.BLUE]);
        setDefaultLayout(Anwendungsbaustein.class, GraphElementLayout.SHAPE.rundeck, GraphElementLayout.COLORS[GraphElementLayout.GRAY]);
        setDefaultLayout(RechAnwendungsbaustein.class, GraphElementLayout.SHAPE.rundeck, GraphElementLayout.COLORS[GraphElementLayout.LIGHTRED]);
        setDefaultLayout(KonAnwendungsbaustein.class, GraphElementLayout.SHAPE.rundeck, GraphElementLayout.COLORS[GraphElementLayout.BLUE]);
        setDefaultLayout(Datenbanksystem.class, GraphElementLayout.SHAPE.tonne, GraphElementLayout.COLORS[GraphElementLayout.YELLOW], 20, 20);
        setDefaultLayout(Dokumentensammlung.class, GraphElementLayout.SHAPE.ordner, GraphElementLayout.COLORS[GraphElementLayout.WHITE], 20, 20);
        setDefaultLayout(Organisationsplan.class, GraphElementLayout.SHAPE.wabe, GraphElementLayout.COLORS[GraphElementLayout.ORANGE]);
        setDefaultLayout(Bausteinschnittstelle.class, GraphElementLayout.SHAPE.oval, GraphElementLayout.COLORS[GraphElementLayout.LIGHTGREEN], 15, 15);
        setDefaultLayout(Benutzungsschnittstelle.class, GraphElementLayout.SHAPE.oval, GraphElementLayout.COLORS[GraphElementLayout.ORANGE], 15, 15);
        setDefaultLayout(PhysischerDVBaustein.class, GraphElementLayout.SHAPE.rechteck, GraphElementLayout.COLORS[GraphElementLayout.ORANGE]);
    }

}
