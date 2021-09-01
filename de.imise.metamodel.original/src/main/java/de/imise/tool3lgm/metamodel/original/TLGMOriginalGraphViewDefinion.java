package de.imise.tool3lgm.metamodel.original;

import static de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.InterLayerLineRenderType.LINE_TYPE_DASHED;
import static de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition.InterLayerLineRenderType.LINE_TYPE_SOLID;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.BLUE;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.LIGHTGREEN;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.LIGHTRED;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.ORANGE;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.RED;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.WHITE;
import static de.imise.tool3lgm.graphtools.view.graph.LayoutColor.YELLOW;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.ordner;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.oval;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.rechteck;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.rundeck;
import static de.imise.tool3lgm.graphtools.view.graph.Shape.tonne;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPathCreator;
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

    @Override
    protected final SimpleMetaPath[] getInterLayerMetaPaths() {
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
    protected InterLayerLineRenderType[] getInterLayerLineRenderTypes() {
        return new InterLayerLineRenderType[] {
                LINE_TYPE_SOLID, LINE_TYPE_DASHED, LINE_TYPE_SOLID
        };
    }

    @Override
    public List<Pair<Class<? extends ModelElement>, Class<? extends Edge>>> getHidableIfNotConnected() {
        return ImmutableList.of(new Pair<>(Bausteinschnittstelle.class, KommBeziehung.class));
    }

    @Override
    protected void initDefaultElementLayout() {
        setDefaultLayout(Aufgabe.class, rechteck, RED);
        setDefaultLayout(Objekttyp.class, oval, BLUE);
        setDefaultLayout(RechAnwendungsbaustein.class, rundeck, LIGHTRED);
        setDefaultLayout(KonAnwendungsbaustein.class, rundeck, BLUE);
        setDefaultLayout(Datenbanksystem.class, tonne, YELLOW, 20, 20);
        setDefaultLayout(Dokumentensammlung.class, ordner, WHITE, 20, 20);
        setDefaultLayout(Bausteinschnittstelle.class, oval, LIGHTGREEN, 16, 16);
        setDefaultLayout(Benutzungsschnittstelle.class, oval, ORANGE, 16, 16);
        setDefaultLayout(PhysischerDVBaustein.class, rechteck, ORANGE);
    }

}
