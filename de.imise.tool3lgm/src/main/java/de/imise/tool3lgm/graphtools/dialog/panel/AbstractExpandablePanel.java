package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Dimension;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;

/**
 * Panel, bei dem man die rechte Seite auf und zuklappen kann. In der Regel sind dort links und rechts Bäume.
 *
 * @author AXS
 * @created 12.06.2017
 */
public abstract class AbstractExpandablePanel extends LGMDragNDropPanel {

    /**
     * Button zu Auf- und Zuklappen der rechten Seite
     */
    protected JButton viewButton;

    private class IconButton extends JButton {

        @Override
        public Dimension getPreferredSize() {
            Action action = getAction();
            if (action != null) {
                Icon icon = (Icon) action.getValue(AbstractAction.SMALL_ICON);
                if (icon != null) {
                    return new Dimension(icon.getIconWidth(), icon.getIconHeight());
                }
            }
            return super.getPreferredSize();
        }

        @Override
        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

    }

    /**
     * Action zum Aufklappen der rechten Seite
     */
    protected LGMAction showAllAction;

    /**
     * Action zum Zuklappen der rechten Seite
     */
    protected LGMAction showPartlyAction;

    public AbstractExpandablePanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog, labelLastEdgeName, simpleMetaPath);
    }

    @Override
    protected final void init() {
        super.init();
        // Aktionen für den button setzen
        showPartlyAction = getShowAction(this, false);
        viewButton = new IconButton();
        showAllAction = getShowAction(this, true);
    }

    public final void showFullDialog(final boolean full) {
        if (full) {
            showFullDialog();
        } else {
            showPartlyDialog();
        }
        viewButton.setAction(full ? showPartlyAction : showAllAction);
        update();
    }

    protected final boolean isRightSideVisible() {
        return viewButton.getAction() == showPartlyAction;
    }

    protected abstract void showFullDialog();

    protected abstract void showPartlyDialog();

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das gesamte oder nur einen Teils des Panels anzeigt.
     *
     * @param panel
     */
    public static final LGMAction getShowAction(final AbstractExpandablePanel panel, final boolean full) {
        return new LGMAction("", Tool3lgmConstants.getIcon(full ? "zu.gif" : "auf.gif")) {
            @Override
            public void execute(final EventObject e) {
                panel.showFullDialog(full);
            }
        };
    }

}
