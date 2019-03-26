package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.GridLayout;
import java.util.EventObject;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.util.swing.SwingUtils;
import de.imise.util.swing.SwingUtils.MinSizedIconButton;

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

    /**
     * Action zum Aufklappen der rechten Seite
     */
    protected LGMAction showAllAction;

    /**
     * Action zum Zuklappen der rechten Seite
     */
    protected LGMAction showPartlyAction;

    /** Mimimale Breite der Buttons zwischen den Bäumen */
    private static final int MIN_ADD_REMOVE_NEW_BUTTON_WIDTH = 30;

    /** Mimimale Höhe der Buttons zwischen den Bäumen */
    private static final int MIN_ADD_REMOVE_NEW_BUTTON_HEIGHT = 50;

    /** Abstand zwischen den Buttons zwischen den Bäumen */
    private static final int ADD_REMOVE_NEW_BUTTON_VGAP = 3;

    public AbstractExpandablePanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog, labelLastEdgeName, simpleMetaPath);
    }

    @Override
    protected final void init() {
        super.init();
        // Aktionen für den button setzen
        showPartlyAction = getShowAction(this, false);
        viewButton = MinSizedIconButton.createLimitedWidthAndHeigthButton(null);
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

    /**
     * Liefert einen Button, der zwischen den Bäumen dargestellt werden kann mit entsprechender Maximalgröße. Das sind in der Regel die
     * Add/Remove/New-Buttons.
     *
     * @param a Action
     * @return
     */
    private JButton createBetweenTreesButton(final Action a) {
        return SwingUtils.MinSizedIconButton.createLimitedWidthButton(a, MIN_ADD_REMOVE_NEW_BUTTON_WIDTH, MIN_ADD_REMOVE_NEW_BUTTON_HEIGHT);
    }

    /**
     * Legt ein Panel an, dass für jede übergebene Action, die nicht <code>null</code> ist, einen Button enthält. Die Buttons stehen übereinander.
     * 
     * @param actions
     * @return
     */
    public JPanel createBetweenTreesButtonPanel(final Action... actions) {
        JPanel buttonpanel = new JPanel();
        GridLayout gridLayout = new GridLayout(actions.length, 1);
        gridLayout.setVgap(ADD_REMOVE_NEW_BUTTON_VGAP);
        buttonpanel.setLayout(gridLayout);
        for (Action a : actions) {
            if (a != null) {
                buttonpanel.add(createBetweenTreesButton(a));
            }
        }
        return buttonpanel;
    }

}
