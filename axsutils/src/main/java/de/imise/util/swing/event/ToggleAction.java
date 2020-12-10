/**
 *
 */
package de.imise.util.swing.event;

import java.awt.event.ActionEvent;

/**
 * Eine Aktion, die mehrere andere Aktionen in sich aufnehmen kann. Dabei hat
 * diese Aktion nach außen immer genau die Eigenschaften der ersten Aktion in
 * der übergebenen Aktionenliste, deren enabled()-Funktion <code>true</code>
 * liefert.<br>
 * Sinnvoller Weise kann man diese Aktion mit 2 anderen Aktionen initialisieren,
 * die beide komplematäre Zustände bei isEnabled() haben, so dass die Aktion
 * zwischen 2 Zuständen hin und her schalten kann.
 *
 * @author AXS
 */
public class ToggleAction extends ExtendedAction {

    /**
     * Liste der Aktionen, von denen die erste, die bei isEnabed() true liefert,
     * die Eigenschaften an diese Aktion übergibt
     */
    protected ExtendedAction[] actions;

    /**
     * @param actions Liste der Aktionen, von denen die erste, die bei
     *            isEnabed() true liefert, die Eigenschaften an diese Aktion
     *            übergibt
     */
    public ToggleAction(final ExtendedAction... actions) {
        //erst einmal alle Werte auf die erste Aktion setzen
        super(actions[0].getText());
        setAllAttributes(actions[0]);
        this.actions = actions;
        //jetzt werden alle Eigenschaften auf die erste Aktione gesetzt, die enabled ist (wenn im Moment keine
        //Aktion enabled ist, dann bleiben die Eigenschaften der ersten Funktion
        isEnabled();
    }

    @Override
    public boolean isEnabled() {
        for (ExtendedAction action : actions) {
            if (action.isEnabled()) {
                setAllAttributes(action);
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        /*
         * Testausgabe im Zusammenhang mit dem Abschaffen der Richtung von
         * Kanten GDCollection gdcoll =
         * Static.getTool().getSelectedGDCollection(); HashSet<Class<? extends
         * ModelElement>> classes = new HashSet<>(); for (ModelElement me :
         * gdcoll.getGraphDocument().getModelItems(Kante.class, true)) {
         * Doppelkante edge = (Doppelkante) me; if (edge.getDirection() ==
         * Doppelkante.BACKWARD || edge.getDirection() == Doppelkante.DOUBLE)
         * classes.add(edge.getClass()); } for (Class<? extends ModelElement>
         * elemClass : classes) { boolean f = false, b = false, d = false; for
         * (ModelElement me :
         * gdcoll.getGraphDocument().getModelItems(elemClass)) { Doppelkante
         * edge = (Doppelkante) me; if (edge.getDirection() ==
         * Doppelkante.FORWARD && !f) { f = true; } else if (edge.getDirection()
         * == Doppelkante.BACKWARD && !b) { b = true; } else if
         * (edge.getDirection() == Doppelkante.DOUBLE && !d) { d = true; } } if
         * (f) System.err.println("F " + elemClass.getSimpleName()); if (b)
         * System.err.println("B " + elemClass.getSimpleName()); if (d)
         * System.err.println("D " + elemClass.getSimpleName()); if (f || b ||
         * d) System.err.println(); }
         */
        for (ExtendedAction action : actions) {
            if (action.isEnabled()) {
                action.actionPerformed(e);
                return;
            }
        }
    }

}
