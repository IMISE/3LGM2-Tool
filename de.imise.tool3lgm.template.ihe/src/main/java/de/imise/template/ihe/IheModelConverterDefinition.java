package de.imise.template.ihe;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import de.imise.template.ihe.IheImportMetaModelDefinition.Actor;
import de.imise.template.ihe.IheImportMetaModelDefinition.Domain;
import de.imise.template.ihe.IheImportMetaModelDefinition.IheDomain_Edge;
import de.imise.template.ihe.IheImportMetaModelDefinition.IheIntegrationProfile_Edge;
import de.imise.template.ihe.IheImportMetaModelDefinition.IheTransaction_Edge;
import de.imise.template.ihe.IheImportMetaModelDefinition.IntegrationProfile;
import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmMetaModelContext;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition;
import de.imise.tool3lgm.graphtools.metamodel.ModelConverterDefinition.TargetMetaPathsCreationDefinition.NameSource;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPathCreator;
import de.imise.tool3lgm.metamodel.service.TLGMServiceMetaModel;
import de.imise.tool3lgm.metamodel.service.edge.IheActor_IheInterface_Edge;
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
        return ImmutableMap.of();
    }

    @Override
    public Map<Class<? extends Edge>, Class<? extends Edge>> getSourceEdgeClassesToSwitchedTargetEdgeClasses() {
        //IntegrationProfile_Edge -> IheIntegrationProfile_IheActor_Edge
        //IheDomain_Edge -> IheIntegrationProfile_IheDomain_Edge
        return ImmutableMap.of(IheIntegrationProfile_Edge.class, IheIntegrationProfile_IheActor_Edge.class, IheDomain_Edge.class, IheIntegrationProfile_IheDomain_Edge.class);
    }

    @Override
    public Map<Class<? extends Edge>, TargetMetaPathsCreationDefinition> getSourceEdgeClassesToTargetMetaPaths() {
        Class<? extends MetaModelDefinition> targetMetaModelDefinitionClass = getTargetMetaModelDefinitionClass();
        MetaModelContext serviceMetaModelContext = Tool3lgmMetaModelContext.getMetaModelContextForDefinitionClass(targetMetaModelDefinitionClass);
        MetaModel serviceMetaModel = serviceMetaModelContext.getMetaModel();

        //IHE Actor besitzt IHE Schnittstelle + IHE Schnittstelle (aufrufend) ruft auf ( <- ) IHE Transaction + IHE Transaction wird bereitsgestellt durch ( <- ) IHE Schnittstelle (bereitstellend) + IHE Schnittstelle gehört zu IHE Actor
        SimpleMetaPath actorTransactionActorMetaPath = SimpleMetaPathCreator.createSimpleMetaPath(serviceMetaModel, IheActor.class, IheActor.class, IheActor_IheInterface_Edge.class, IheInvokingInterface_IheTransaction_Edge.class,
                IheProvidingInterface_IheTransaction_Edge.class, IheActor_IheInterface_Edge.class);
        TargetMetaPathsCreationDefinition def = new TargetMetaPathsCreationDefinition(actorTransactionActorMetaPath);
        def.addElementNameCreationPattern(1, NameSource.PATH_STEP_EDGE_NAME); //EndElement der 2.Kante im Pfad ( IheInvokingInterface_IheTransaction_Edge -> EndElement = Transaction) soll den Namen der Ursprungskante bekommen
        return ImmutableMap.of(IheTransaction_Edge.class, def);
    }

    @Override
    public void transform(final GDCollection source, final GDCollection target) {
        //das hier macht aus dem Namen "IHE Schnittstelle (bereistellend) 33" den Namen "ITI-43 Schnittstelle (bereitstellend)", je nachdem mit welcher Transaktion die Schnittstelle verbunden ist
        LGMGraphDocument doc = target.getMainGraphDocument();
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
