/*
 * Created on 26.11.2003
 */
package de.imise.tool3lgm.metamodel.original.node;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.metamodel.original.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.original.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.original.process.ProzessStructurePanel;

/**
 * @author AXS Ein Prozess ist ein Node der Kanten zu Aufgaben haelt. Die Reihenfolge der Kanten zu den Aufgaben in der ArrayList connections legt
 *         den Prozessablauf fest.
 */
public final class Prozess extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        ElementsNameBuilder elementsNameBuilder = getMetaModel().getElementsNameBuilder();
        dialog.addTab(elementsNameBuilder.getDisplayablePluralName(Aufgabe.class), new ProzessStructurePanel(dialog, PrzAufVerbindung.class, AufObjVerbindung.class));
        //		dialog.addTab(elementsNameBuilder.getDisplayablePluralName(Kommunikationsprozess.class), new KommProzessPanel(dialog));
        return dialog;
    }

}
