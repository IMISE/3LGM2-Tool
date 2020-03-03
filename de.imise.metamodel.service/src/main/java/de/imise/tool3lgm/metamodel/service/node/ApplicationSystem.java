package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_IheActorInstance_Edge;
import de.imise.tool3lgm.metamodel.service.edge.ApplicationSystem_SoftwareProduct_Edge;

/**
 * @author AXS (26.12.2017)
 */
public class ApplicationSystem extends PartableApplicationComponent {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(ApplicationSystem_SoftwareProduct_Edge.class);
        //Den folgenden Pfad unbedingt mit Startelementklasse angeben, damit der Pfad bei der Unterklasse
        //IheActorInstance nicht angelegt werden kann und das Panel, so wie es richtig ist, nicht geaddet wird.
        dialog.addEdgePanel(IheActorInstance.class, ApplicationSystem_IheActorInstance_Edge.class);
        return dialog;
    }

}
