package de.imise.tool3lgm.graphtools.dialog.action;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import de.imise.tool3lgm.event.action.StaticAction;

/**
 * Abstrakte Klasse, deren abgeleitete Instanzen ausführbare Aktionen
 * repräsentieren. Diese Aktionen können mittels <code>actionPerformed(ActionEvent e)</code>
 * oder <code>execute(EventObject eo)</code> ausgeführt werden.<br>
 * Anders als die normale Action kann das auslösende Ereignis ein allg. {@link EventObject}
 * sein und nicht nur ein spezielles {@link ActionEvent}. Dadurch können z.B. auch
 * {@link MouseEvent}s als auslösendes Ereignis dienen.
 *
 * @author fstephan (16.11.2007)
 */
public abstract class LGMAction extends StaticAction {

    /**
     * @param identifier
     *            the toString() value of this parameter is the name of resources like the action
     *            and icon name
     */
    public LGMAction(final Object identifier) {
        super(identifier);
    }

    /**
     *
     */
    public LGMAction() {
        this(null);
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
