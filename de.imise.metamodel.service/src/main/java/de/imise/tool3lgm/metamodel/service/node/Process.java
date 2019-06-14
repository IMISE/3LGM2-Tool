/*
 * Created on 26.11.2003
 */
package de.imise.tool3lgm.metamodel.service.node;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.service.edge.Process_Function_Edge;

/**
 * @author AXS Ein Prozess ist ein Node der Kanten zu Aufgaben haelt. Die Reihenfolge der Kanten zu den Aufgaben in der ArrayList connections legt
 *         den Prozessablauf fest.
 */
public final class Process extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addEdgePanel(Process_Function_Edge.class);
        //dialog.addTab(ModelConstants.getDisplayablePluralName(Function.class), new ProzessStructurePanel(dialog, Process_Funtion_Edge.class, Function_ObjectType_Edge.class));
        //		dialog.addTab(getResString("Kommunikationsprozess_p"),new KommProzessPanel(dialog));
        return dialog;
    }

}
