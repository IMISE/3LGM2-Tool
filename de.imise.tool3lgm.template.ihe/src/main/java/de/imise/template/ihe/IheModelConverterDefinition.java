package de.imise.template.ihe;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import de.imise.template.ihe.IheImportMetaModelDefinition.Actor;
import de.imise.template.ihe.IheImportMetaModelDefinition.Domain;
import de.imise.template.ihe.IheImportMetaModelDefinition.IheDomain_Edge;
import de.imise.template.ihe.IheImportMetaModelDefinition.IheIntegrationProfile_Edge;
import de.imise.template.ihe.IheImportMetaModelDefinition.IheTransaction_Edge;
import de.imise.template.ihe.IheImportMetaModelDefinition.IntegrationProfile;
import de.imise.template.ihe.IheImportMetaModelDefinition.MustBeGroupedWith_Edge;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition.TargetPathsCreationDefinition.NameSource;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeModel;
import de.imise.tool3lgm.graphtools.path.paths.PathResultTreeNode;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.metamodel.service.TLGMServiceMetaModel;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheActor_MustBeGroupedWith_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheCommunicationLink_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheActor_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheIntegrationProfile_IheDomain_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheInvokingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.edge.IheProvidingInterface_IheTransaction_Edge;
import de.imise.tool3lgm.metamodel.service.node.IheActor;
import de.imise.tool3lgm.metamodel.service.node.IheDomain;
import de.imise.tool3lgm.metamodel.service.node.IheIntegrationProfile;
import de.imise.tool3lgm.metamodel.service.node.IheInterface;

/**
 * @author AXS (10 Jun 2019)
 */
public class IheModelConverterDefinition extends ModelConverterDefinition {

    /**
     *
     */
    public IheModelConverterDefinition() {
        super(IheImportMetaModelDefinition.class, TLGMServiceMetaModel.class);
    }

    @Override
    public Map<Class<? extends Node>, Class<? extends Node>> getSourceNodeClassesToTargetNodeClasses() {
        //Actor -> IheActor   Domain -> IheDomain    IntegrationProfile -> IheIntegrationProfile
        return ImmutableMap.of(Actor.class, IheActor.class, Domain.class, IheDomain.class, IntegrationProfile.class, IheIntegrationProfile.class);
    }

    @Override
    public Map<Class<? extends Edge>, Class<? extends Edge>> getSourceEdgeClassesToTargetEdgeClasses() {
        return ImmutableMap.of(MustBeGroupedWith_Edge.class, IheActor_IheActor_MustBeGroupedWith_Edge.class);
    }

    @Override
    public Map<Class<? extends Edge>, Class<? extends Edge>> getSourceEdgeClassesToSwitchedTargetEdgeClasses() {
        //IntegrationProfile_Edge -> IheIntegrationProfile_IheActor_Edge
        //IheDomain_Edge -> IheIntegrationProfile_IheDomain_Edge
        return ImmutableMap.of(IheIntegrationProfile_Edge.class, IheIntegrationProfile_IheActor_Edge.class, IheDomain_Edge.class, IheIntegrationProfile_IheDomain_Edge.class);
    }

    @SuppressWarnings("unchecked")
    private SimpleMetaPath get_Actor_Transaction_Actor_MetaPath() {
        //IHE Actor besitzt IHE Schnittstelle + IHE Schnittstelle (aufrufend) ruft auf ( <- ) IHE Transaction + IHE Transaction wird bereitsgestellt durch ( <- ) IHE Schnittstelle (bereitstellend) + IHE Schnittstelle gehört zu IHE Actor
        SimpleMetaPath actorTransactionActorMetaPath = targetMetaPath(IheActor.class, IheActor.class, IheActor_IheInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class, IheProvidingInterface_IheTransaction_Edge.class,
                IheActor_IheInterface_Edge.class);
        return actorTransactionActorMetaPath;
    }

    @Override
    public Multimap<Class<? extends Edge>, TargetPathsCreationDefinition> getSourceEdgeClassesToTargetMetaPaths() {

        //Das hier auskommentierte wäre die Pfad-Definition zum Anlegen sowohl des Pfades über die Schnittstellen und Transaktionen als auch
        //über die Schnittstellen mit einer Kommnuikationsbeziehung. Das Funktioniert aber erst, wenn man nicht nur SimpleMetaPaths als Paths
        //anlegen kann sondern auch die anderen (ListMetaPaths)
        //
        //        //Start: IheActor  > IheInvokingInterface
        //        SimpleMetaPath actor_to_InvokingInterface_MetaPath = targetMetaPath(IheActor.class, IheInvokingInterface.class, IheActor_IheInterface_Edge.class);
        //        //Middle Part 1: IheInvokingInterface > IheTransaction > IheProvidingInterface
        //        SimpleMetaPath invokingInterface_Transaction_ProvidingInterface_MetaPath = targetMetaPath(IheInvokingInterface.class, IheProvidingInterface.class, IheInvokingInterface_IheTransaction_Edge.class, IheProvidingInterface_IheTransaction_Edge.class);
        //        //Middle Part 2: IheInvokingInterface > IheCommunicationLink > IheProvidingInterface
        //        SimpleMetaPath invokingInterface_CommunicationLink_ProvidingInterface_MetaPath = targetMetaPath(IheInvokingInterface.class, IheProvidingInterface.class, IheCommunicationLink_Edge.class);
        //        //Middle: Part 1 + Part 2
        //        UnionMetaPath invokingInterface_to_ProvidingInterface_MetaPath = new UnionMetaPath(invokingInterface_Transaction_ProvidingInterface_MetaPath, invokingInterface_CommunicationLink_ProvidingInterface_MetaPath);
        //        //End: IheProvidingInterface > IheActor
        //        SimpleMetaPath providingInterface_to_Actor_MetaPath = targetMetaPath(IheProvidingInterface.class, IheActor.class, IheActor_IheInterface_Edge.class);
        //        //Full: Start + Middle + End
        //        SequenceMetaPath actor_to_Actor_MetaPath = new SequenceMetaPath(actor_to_InvokingInterface_MetaPath, invokingInterface_to_ProvidingInterface_MetaPath, providingInterface_to_Actor_MetaPath);

        SimpleMetaPath actor_Transaction_Actor_MetaPath = get_Actor_Transaction_Actor_MetaPath();
        TargetPathsCreationDefinition def1 = new TargetPathsCreationDefinition(actor_Transaction_Actor_MetaPath);
        def1.addElementNameCreationPattern(1, NameSource.PATH_STEP_EDGE_NAME); //EndElement der 2.Kante im Pfad ( IheInvokingInterface_IheTransaction_Edge -> EndElement = Transaction) soll den Namen der Ursprungskante bekommen

        //        //IHE Actor besitzt IHE Schnittstelle + IHE Schnittstelle (aufrufend) ist vrbunden mit IHE Schnittstelle (bereitstellend) + IHE Schnittstelle gehört zu IHE Actor
        //        SimpleMetaPath actorCommunicationLinkActorMetaPath = targetMetaPath(IheActor.class, IheActor.class, IheActor_IheInterface_Edge.class, IheCommunicationLink_Edge.class, IheActor_IheInterface_Edge.class);
        //        TargetMetaPathsCreationDefinition def2 = new TargetMetaPathsCreationDefinition(actorCommunicationLinkActorMetaPath);
        //
        //beide MetaPfade für die IheTransaction_Edge hinzufügen
        //       return ImmutableListMultimap.of(IheTransaction_Edge.class, def1, IheTransaction_Edge.class, def2);
        //Das funktioniert nicht:
        //Problem ist das Joinen um Verlauf der Hintereinanderusführung des Anlegens der beiden Pfade. Das müsste komplett bereitigt werden
        //und dadruch ersetzt werden, dass man die Pfade, die aus derselben Kante entstehen sollen, gleich das im Moment nicht umgesetzte
        //Anlegen von Pararllelen Pfaden erzeugt.
        //Lösung hier (bzw. Workaround): Einfach die IheCommunicationLink_Edge zwischen allen Schnittstellen anlegen, die über Transaktionen
        //verbunden sind. Das sollte in diesem Fall hinhauen.

        return ImmutableListMultimap.of(IheTransaction_Edge.class, def1);
    }

    @Override
    public void transform(final GDCollection source, final GDCollection target) {
        createCommunicationLinks(target); //Kommunikationsbeziehungen zw. Schnittstellen erzeugen, die über eine Transaktion verbunden sind
        renameCommunicationInterfacesWithTransactionNames(source, target); //Schnittstellen mit dem Namen der Transaktion versehen
    }

    /**
     * Diese Funktion erzeugt zwischen allen Schnittstellen, die über eine Transaktion miteinander verbunden sind, auch eine
     * IheCommunicationLink_Edge.
     * Das hier könnte man sich sparen, wenn man parallele Pfade auch aus einem parallelen MetaPfad erzeugen könnte (und nicht
     * nur SimplePaths aus SimpleMetaPaths wie bisher). Das ganze ist schon angedacht, aber noch nicht umgesetzt (siehe
     * getSourceEdgeClassesToTargetMetaPaths(). Das hier ist der Quick-And-Dirty-Workaround.
     *
     * @param target
     */
    private void createCommunicationLinks(final GDCollection target) {
        SimpleMetaPath actor_Transaction_Actor_MetaPath = get_Actor_Transaction_Actor_MetaPath();
        Class<? extends ModelElement> startClass = actor_Transaction_Actor_MetaPath.getStartClass();
        GraphDocument doc = target.getMainDoc();
        List<ModelElement> actors = doc.getModelItems(startClass);
        PathResultTreeModel resultTree = PathFunctions.getResultTree(actors, actor_Transaction_Actor_MetaPath);
        PathResultTreeNode root = resultTree.getRoot();
        for (PathResultTreeNode actorNodes : root.getChildren()) {
            for (PathResultTreeNode invokingInterfaceNodes : actorNodes.getChildren()) {
                ModelElement invokingInterface = invokingInterfaceNodes.getEndElement();
                for (PathResultTreeNode transactionNodes : invokingInterfaceNodes.getChildren()) {
                    for (PathResultTreeNode providingInterfaceNodes : transactionNodes.getChildren()) {
                        ModelElement providingInterface = providingInterfaceNodes.getEndElement();
                        target.link(IheCommunicationLink_Edge.class, invokingInterface, providingInterface, TransactionManager.STANDARD_PID);
                    }
                }
            }
        }
    }

    /**
     * Das hier macht aus dem Namen "IHE Schnittstelle (bereistellend) 33" den Namen "ITI-43 Schnittstelle (bereitstellend)",
     * je nachdem mit welcher Transaktion die Schnittstelle verbunden ist.
     *
     * @param source
     * @param target
     */
    private void renameCommunicationInterfacesWithTransactionNames(final GDCollection source, final GDCollection target) {
        LGMGraphDocument doc = target.getMainDoc();
        for (ModelElement me : doc.getModelItems(IheInterface.class, true)) {
            StringBuilder sb = new StringBuilder();
            for (ModelElement connected : me.getConnectedElements(IheInterface_IheTransaction_Edge.class)) {
                String name = connected.getName();
                if (name.startsWith("ITI-")) {
                    int firstWhiteSpaceIndex = name.indexOf(' ');//ITI-XX vorne extrahieren
                    if (firstWhiteSpaceIndex > 0) {
                        name = name.substring(0, firstWhiteSpaceIndex);
                    }
                }
                sb.append(name);
                sb.append(" ");
            }
            String name = me.getName();
            if (name.startsWith("IHE") && name.length() > 3) {//3 ist die Länge von IHE
                int lastWhiteSpaceIndex = name.lastIndexOf(' '); //die durchnummerierte Zahl des generierten Namens hinten abschneiden
                if (lastWhiteSpaceIndex > 3) {
                    name = name.substring(3, lastWhiteSpaceIndex).trim();
                }
            }
            sb.append(name);
            name = sb.toString();
            me.setName(name);
        }
    }

}
