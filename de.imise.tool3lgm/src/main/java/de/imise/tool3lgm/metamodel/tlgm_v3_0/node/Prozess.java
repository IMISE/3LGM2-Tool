/*
 * Created on 26.11.2003
 */
package de.imise.tool3lgm.metamodel.tlgm_v3_0.node;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.Node;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.AufObjVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.edge.PrzAufVerbindung;
import de.imise.tool3lgm.metamodel.tlgm_v3_0.process.ProzessStructurePanel;

/**
 * @author AXS Ein Prozess ist ein Node der Kanten zu Aufgaben haelt. Die Reihenfolge der Kanten zu den Aufgaben in der ArrayList connections legt
 *         den Prozessablauf fest.
 */
public final class Prozess extends Node {

    @Override
    public ElementPropertyDialog createPropertyDialog() {
        ElementPropertyDialog dialog = super.createPropertyDialog();
        dialog.addTab(getResString("Aufgabe_p"), new ProzessStructurePanel(dialog, PrzAufVerbindung.class, AufObjVerbindung.class));
        //		dialog.addTab(getResString("Kommunikationsprozess_p"),new KommProzessPanel(dialog));
        return dialog;
    }

}
