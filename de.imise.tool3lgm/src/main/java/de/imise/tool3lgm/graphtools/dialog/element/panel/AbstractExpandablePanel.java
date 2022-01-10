package de.imise.tool3lgm.graphtools.dialog.element.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.EventObject;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.element.DialogActionCommands;
import de.imise.tool3lgm.graphtools.dialog.search.SearchResultView;
import de.imise.tool3lgm.graphtools.dialog.search.TreeSearchOptionsPanel;
import de.imise.tool3lgm.graphtools.path.metapaths.MetaPath;
import de.imise.util.swing.component.MinSizedIconButton;

/**
 * Panel, bei dem man die rechte Seite auf und zuklappen kann. In der Regel sind
 * dort links und rechts Bäume.
 *
 * @author AXS
 * @created 12.06.2017
 */
public abstract class AbstractExpandablePanel extends LGMDragNDropPanel {

    /**
     * Button zu Auf- und Zuklappen der rechten Seite
     */
    private JButton expandOrCollapseViewButton;

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
    private static final int MIN_ADD_REMOVE_NEW_BUTTON_HEIGHT = 45;

    /** Abstand zwischen den Buttons zwischen den Bäumen */
    private static final int ADD_REMOVE_NEW_BUTTON_VGAP = 3;

    /**
     * @param dialog
     * @param titleLabelOption
     * @param westLabelOption
     * @param metaPath
     */
    public AbstractExpandablePanel(final AbstractElementPropertyDialog dialog, final PanelLabelOption titleLabelOption, final PanelLabelOption westLabelOption, final MetaPath metaPath) {
        super(dialog, titleLabelOption, westLabelOption, metaPath);
    }

    @Override
    protected final void init() {
        super.init();
        // Aktionen für den button setzen
        showPartlyAction = getShowAction(this, false);
        expandOrCollapseViewButton = MinSizedIconButton.createLimitedHeightButton(showPartlyAction, MIN_ADD_REMOVE_NEW_BUTTON_WIDTH);
        showAllAction = getShowAction(this, true);
    }

    /**
     * @param full
     */
    public final void showFullDialog(final boolean full) {
        if (full && !dialog.isInfoDialog()) {
            showFullDialog();
        } else {
            showPartlyDialog();
        }
        JButton viewButton = getExpandOrCollapseViewButton();
        if (viewButton != null) {
            viewButton.setAction(full ? showPartlyAction : showAllAction);
        }
        update();
    }

    /**
     * @return
     */
    protected final boolean isRightSideVisible() {
        JButton viewButton = getExpandOrCollapseViewButton();
        return viewButton != null && viewButton.getAction() == showPartlyAction;
    }

    /**
     *
     */
    protected abstract void showFullDialog();

    /**
     *
     */
    protected abstract void showPartlyDialog();

    @Override
    public JButton getExpandOrCollapseViewButton() {
        if (isExpandable()) {
            return expandOrCollapseViewButton;
        }
        return null;
    }

    /**
     * Sets the state, that the panel cannot be expaded by setting the
     * viewButton to <code>null</code>
     */
    protected abstract boolean isExpandable();

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das gesamte oder
     * nur einen Teils des Panels anzeigt.
     *
     * @param panel
     */
    public static final LGMAction getShowAction(final AbstractExpandablePanel panel, final boolean full) {
        return new LGMAction(full ? DialogActionCommands.ACTION_DIALOG_RIGHT_SIDE_SHOW : DialogActionCommands.ACTION_DIALOG_RIGHT_SIDE_HIDE) {
            @Override
            public void execute(final EventObject e) {
                panel.showFullDialog(full);
            }
        };
    }

    /**
     * Liefert einen Button, der zwischen den Bäumen dargestellt werden kann mit
     * entsprechender Maximalgröße. Das sind in der Regel die
     * Add/Remove/New-Buttons.
     *
     * @param a Action
     * @return
     */
    private JButton createBetweenTreesButton(final Action a) {
        return MinSizedIconButton.createLimitedWidthAndHeightButton(a, MIN_ADD_REMOVE_NEW_BUTTON_WIDTH, MIN_ADD_REMOVE_NEW_BUTTON_HEIGHT);
    }

    /**
     * Legt ein Panel an, dass für jede übergebene Action, die nicht
     * <code>null</code> ist, einen Button enthält. Die Buttons stehen
     * übereinander.
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
                JButton button = createBetweenTreesButton(a);
                buttonpanel.add(button);
            }
        }
        return buttonpanel;
    }

    /**
     * @return
     */
    protected static final JPanel createTreeSearchPanel(JLabel label, SearchResultView tree) {
        TreeSearchOptionsPanel rtreeSearchPanel = new TreeSearchOptionsPanel(tree);
        JPanel rsearchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        add(rsearchPanel, label, gbc, 0, 0, 1, 1, new Insets(0, 0, 0, 20));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1d;
        add(rsearchPanel, rtreeSearchPanel.getElementName(), gbc, 1, 0, 1, 1);
        return rsearchPanel;
    }

}
