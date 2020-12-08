/*
 * Created on 16.11.2007
 */
package de.imise.util.swing.event;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.ImageIcon;

/**
 * @author fstephan Abstrakte Klasse, deren abgeleitete Instanzen ausführbare
 *         Aktionen repräsentieren. Diese Aktionen können mittels
 *         <code>actionPerformed(ActionEvent e)</code> oder
 *         <code>execute(EventObject eo)</code> ausgeführt werden.
 */
public abstract class FlexibleEventAction extends ExtendedAction {

    /**
     * 1.Konstruktor
     * 
     * @param s
     * @param icon
     */
    public FlexibleEventAction(String s, ImageIcon icon) {
        super(s, icon);
    }

    /**
     * 2.Konstruktor
     * 
     * @param typ
     */
    public FlexibleEventAction() {
        super();
    }

    /**
     * 3.Konstruktor
     * 
     * @param s
     */
    public FlexibleEventAction(String s) {
        super(s);
    }

    /**
     * Methode soll so überschrieben werden, dass sie die auszuführenden
     * Aktionen dieser <code>LGMAction</code> beinhaltet und auslöst.
     * 
     * @param eo
     */
    public abstract void execute(EventObject eo);

    /**
     * Methode ruft <code>execute(EventObject eo)</code> mit <code>e</code> auf.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.execute(e);
    }

}
