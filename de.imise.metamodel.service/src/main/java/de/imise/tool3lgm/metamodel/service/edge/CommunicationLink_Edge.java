package de.imise.tool3lgm.metamodel.service.edge;

import static de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality.ZERO_UNLIMITED;

import java.util.Collection;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.ConnectedElementsTableDefinition;
import de.imise.tool3lgm.graphtools.metamodel.EdgeCardinality;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.BooleanAttributeEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.ElementaryMetaPathHandler;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.tool3lgm.metamodel.service.node.CommunicationInterface;
import de.imise.tool3lgm.metamodel.service.node.IheActorInstance;
import de.imise.tool3lgm.metamodel.service.node.InvokingInterface;
import de.imise.tool3lgm.metamodel.service.node.ProvidingInterface;

/**
 * @author AXS (11.01.2017)
 */
public final class CommunicationLink_Edge extends BooleanAttributeEdge {

    public static final Class<? extends ModelElement> STCL = InvokingInterface.class;

    public static final EdgeCardinality SCARD = ZERO_UNLIMITED;

    public static final EdgeCardinality ECARD = ZERO_UNLIMITED;

    public static final Class<? extends ModelElement> ETCL = ProvidingInterface.class;

    public CommunicationLink_Edge() {
        super("CommunicationLinkEdge_executionDepending_Attribute");
    }

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        if (showTransactions()) {
            //da es fast niemals mehr als 1 bis 2 Tansaktionen sind, die man nicht ändern kann, reicht hier eine Liste auf dem DescripPanel
            dialog.addDescripPanel(IheCommunicationLink_CommunicationLink_Edge.class, IheTransaction_IheCommunicationLink_Edge.class);
        }

        dialog.addEdgePanel(Service_CommunicationLink_Edge.class);

        ConnectedElementsTableDefinition tableDefinition = new ConnectedElementsTableDefinition();
        tableDefinition.addColumnPathStepEnd(0, 300);
        tableDefinition.addColumnPathStepName(1, "HEADER_ACCESS_TYPE", 300);
        tableDefinition.addColumnEndElement(300);
        dialog.addTablePanel(tableDefinition, 0, Service_CommunicationLink_Edge.class, Service_ObjectType_Edge.class);

        return dialog;
    }

    private boolean showTransactions() {
        //das Transaktions-Panel nur anzeigen, wenn diese Kommunikationsbeziehung Schnittstellen verbindet,
        //die zu IheActorInstances gehören und nicht nur zu normalen Anwendungssystemen, denn nur bei
        //IheActorInsances können überhaupt Transaktionen verknüpft sein

        MetaModel metaModel = getMetaModel();
        ElementaryMetaPathHandler emph = metaModel.getElementaryMetaPathHandler();
        //this = CommunicationLink_Edge -> StartElement = InvokingInterface
        ElementaryMetaPath communicationLink_StartElement_InvokingInterface = emph.getEdgeToStartElementMetaPath(CommunicationLink_Edge.class);
        //this = CommunicationLink_Edge -> EndElement = ProvidingInterface
        ElementaryMetaPath communicationLink_EndElement_ProvidingInterface = emph.getEdgeToEndElementMetaPath(CommunicationLink_Edge.class);
        //CommunicationInterface -> IheActorInstance
        ElementaryMetaPath communicationInterface_IheActorInstance = emph.getMetaPath(CommunicationInterface.class, ApplicationComponent_CommunicationInterface_Edge.class, Direction.BACKWARD, IheActorInstance.class);
        //Gesamtpfad 1: this = CommunicationLink_Edge -> StartElement = InvokingInterface -> IheActorInstance
        SimpleMetaPath communicationLink_InvokingInterface_IheActorInstance = new SimpleMetaPath(communicationLink_StartElement_InvokingInterface, communicationInterface_IheActorInstance);
        //Gesamtpfad 2: this = CommunicationLink_Edge -> EndElement = ProvidingInterface -> IheActorInstance
        SimpleMetaPath communicationLink_ProvidingInterface_IheActorInstance = new SimpleMetaPath(communicationLink_EndElement_ProvidingInterface, communicationInterface_IheActorInstance);
        //Vereinigungspfad aus Gesamtpfad 1 und Gesamtpfad 2 = Ergebnisselemente sind über beide Einzelpfade verknüpfte IheActorInstances
        UnionMetaPath communicationLink_Interfaces_IheActorInstances = new UnionMetaPath(communicationLink_InvokingInterface_IheActorInstance, communicationLink_ProvidingInterface_IheActorInstance);
        //Verknüpfte Elemente holen (multiple ist true, falls beide Schnittstellen zu derselben IheActorInstance gehören
        Collection<ModelElement> actorInstancesOfCommunicationLink = MetaPathFunctions.getConnectedElements(this, communicationLink_Interfaces_IheActorInstances, true);
        //über das InvokingInterface muss die verbundene IheActorInstance gefunden worden sein und genauso über das ProvidingInterface
        // => es müssen 2 IheActorInstances gefunden worden sein, damit der TransactionTab angezeigt wird
        return actorInstancesOfCommunicationLink.size() == 2;
    }

}
