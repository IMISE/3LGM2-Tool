package de.imise.tool3lgm.metamodel.tlgm_service.node;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.ApplicationComponent_CommunicationInterface_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.OrganisationalUnit_Use_Edge;
import de.imise.tool3lgm.metamodel.tlgm_service.edge.SupportLink_Edge;

/**
 * @author AXS (26.12.2017)
 */
public abstract class ApplicationComponent extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addPathConnectionPanel(SupportLink_Edge.class, Function_Use_Edge.class);
        dialog.addPathConnectionPanel(SupportLink_Edge.class, OrganisationalUnit_Use_Edge.class);
        //das hier auskommentierte ist Quatsch, da es über diese doofe Unterstützung_Kante = Support_Edge geht. Dieses komische Konstrukt wollten Sebastian und ich eigentlich auch nicht aus dem originalen Servide-Metamodell übernehmen
        //        dialog.addPathConnectionInfoPanel(ApplicationComponent_SupportLink_Edge.class, OrganisationalUnit_SupportLink_Edge.class);
        dialog.addTabbedPanel(ElementsNameBuilder.getDisplayablePluralName(CommunicationInterface.class));
        dialog.addTabbedPanelPathConnectionPanel(InvokingInterface.class, ApplicationComponent_CommunicationInterface_Edge.class);
        dialog.addTabbedPanelPathConnectionPanel(ProvidingInterface.class, ApplicationComponent_CommunicationInterface_Edge.class);
        return dialog;
    }

}
