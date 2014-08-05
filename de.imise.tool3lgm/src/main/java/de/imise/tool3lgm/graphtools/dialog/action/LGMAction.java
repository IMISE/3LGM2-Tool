/*
 * Created on 16.11.2007
 */
package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 * @author fstephan Abstrakte Klasse, deren abgeleitete Instanzen ausführbare Aktionen
 *         repräsentieren. Diese Aktionen können mittels <code>actionPerformed(ActionEvent e)</code>
 *         oder <code>execute(EventObject eo)</code> ausgeführt werden.
 */
public abstract class LGMAction extends AbstractAction {

    /**
     * 1.Konstruktor
     * 
     * @param s
     * @param icon
     */
    public LGMAction(final String s, final ImageIcon icon) {
        super(s, icon);
    }

    /**
     * 2.Konstruktor
     * 
     * @param typ
     */
    public LGMAction() {
        super();
    }

    /**
     * 3.Konstruktor
     * 
     * @param s
     */
    public LGMAction(final String s) {
        super(s);
    }

    /**
     * Methode soll so überschrieben werden, dass sie die auszuführenden Aktionen dieser
     * <code>LGMAction</code> beinhaltet und auslöst.
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
    public void actionPerformed(final ActionEvent e) {
        execute(e);
    }

}
