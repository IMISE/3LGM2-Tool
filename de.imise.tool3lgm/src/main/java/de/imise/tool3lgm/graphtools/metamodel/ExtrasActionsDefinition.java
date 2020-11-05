package de.imise.tool3lgm.graphtools.metamodel;

import javax.swing.Action;

/**
 * Klasse, über die für ein Metamodell Actions im Reiter Extras hinzugefügt
 * werden können. Um {@link NullPointerException}s zu verhindern, wird wenn für
 * ein Metamodell keine solche Klasse angegeben ist, immer eine Instanz dieser
 * Kasse hier zurück gegeben, die aber keine Actions zurück liefert.
 *
 * @author AXS (5 Jun 2018)
 */
public class ExtrasActionsDefinition {

    /**
     * Liefert die Actions, die zum Extras-Menü hinzugefügt werden.
     *
     * @return
     */
    public Action[] getActions() {
        return new Action[0];
    }

    /**
     * Liefert suer soezielle Actions, die man am besten im Extras-Menü im
     * Untermenü Plugin versteckt
     *
     * @return
     */
    public Action[] getPluginActions() {
        return new Action[0];
    }

    /**
     * Liefert die Actions dieser Definition.
     *
     * @param pluginActions wenn <code>true</code> werden die PluginAction
     *            zurück gegeben, sonst die allgemeinen.
     * @return
     */
    public final Action[] getActions(final boolean pluginActions) {
        return pluginActions ? getPluginActions() : getActions();
    }
}
