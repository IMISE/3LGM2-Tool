package de.imise.tool3lgm.event.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeType;
import de.imise.util.swing.event.OptionAction;

/**
 * Eine Action, die für globale Boolean-Optionen ist. Die ganzen Optionen der UserProperties können hiermit behandelt werden.
 *
 * @author AXS 04.12.2017
 */
public abstract class GlobalOptionAction extends StaticAction implements OptionAction {

    private final GDCollectionChangeType changeType;

    public GlobalOptionAction(final Object identifier) {
        this(identifier, GDCollectionChangeType.DATA_CHANGED);
    }

    public GlobalOptionAction(final Object identifier, final GDCollectionChangeType changeType) {
        super(identifier);
        this.changeType = changeType;
    }

    @Override
    protected final void actionPerformed() {
        changeOption();
        distributeDataChanged();
    }

    @Override
    protected final void actionPerformedWithEvent(final ActionEvent e) {
    }

    /** Benachrichtigt das Tool über eine Ändeung der Daten */
    private void distributeDataChanged() {
        distributeOptionChange(changeType);
    }

    /** Benachrichtigt das Tool über das Eintreten des spezifizierten Ereignisses */
    private void distributeOptionChange(final GDCollectionChangeType eventCode) {
        List<GDCollection> collections = Static.getTool().getCollections();
        for (int i = 0; i < collections.size(); i++) {
            GDCollection col = collections.get(i);
            col.distribute(eventCode);
        }
    }

    @Override
    public JCheckBoxMenuItem createMenuItem() {
        return OptionAction.super.createMenuItem();
    }

    public abstract void changeOption();

}
