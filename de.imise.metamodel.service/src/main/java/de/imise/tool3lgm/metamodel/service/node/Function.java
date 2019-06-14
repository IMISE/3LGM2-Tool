package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.Function_ObjectType_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_SoftwareProduct_Edge;
import de.imise.tool3lgm.metamodel.service.edge.Function_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.OrganisationalUnit_Use_Edge;
import de.imise.tool3lgm.metamodel.service.edge.SupportLink_Edge;

public final class Function extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Function_ObjectType_Edge.class);

        //das hier sollte das LeafPanel sein, weil die Nutzung irrelevant ist. Sie hat (anders als es mal im Metamodell von 2010 publiziert wurde von mir eine 1..1-Beziehung
        //sowohl hin zu Aufgaben als auch zu Organisationseinheiten bekommen
        dialog.addPathConnectionLeafPanel(Function_Use_Edge.class, OrganisationalUnit_Use_Edge.class);

        dialog.addEdgePanel(Function_SoftwareProduct_Edge.class);

        //hier sollte es wieder das PathConnectionPanel und nicht das Leaf-Panel sein, weil man hier die Nutzungen sehen muss, um mehrere Anwendungssysteme ranzuhängen
        dialog.addPathConnectionPanel(Function_Use_Edge.class, SupportLink_Edge.class);

        return dialog;
    }

}
