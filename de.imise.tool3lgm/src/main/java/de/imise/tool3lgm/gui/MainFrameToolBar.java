package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.help.CSH;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.ToolTipManager;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.event.action.StaticAction;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.util.swing.component.UnfloatableToolBar;

public class MainFrameToolBar extends UnfloatableToolBar implements MouseListener, LGMChangeListenerSimple, Tool3lgmChangeListener {

    private GraphDocument doc = null;

    private final JButton undo, redo;

    public MainFrameToolBar() {
        JButton switchView = new ToolbarButton(ActionLibrary.ViewActions.ACTION_GRAPH_SWITCH_ONE_LAYER_AND_THREE_LAYER_PERSPECTIVE);

        JButton fach = new ToolbarButton(ActionLibrary.ViewActions.ACTION_ACTIVATE_DOMAIN_LAYER);
        JButton log = new ToolbarButton(ActionLibrary.ViewActions.ACTION_ACTIVATE_LOGICAL_LAYER);
        JButton phy = new ToolbarButton(ActionLibrary.ViewActions.ACTION_ACTIVATE_PHYSICAL_LAYER);

        JButton neu = new ToolbarButton(ActionLibrary.FileActions.ACTION_NEW_MODEL);
        JButton open = new ToolbarButton(ActionLibrary.FileActions.ACTION_OPEN_MODEL);
        JButton save = new ToolbarButton(ActionLibrary.FileActions.ACTION_SAVE_MODEL);

        undo = new ToolbarButton(ActionLibrary.EditActions.ACTION_UNDO);
        undo.addMouseListener(this);
        redo = new ToolbarButton(ActionLibrary.EditActions.ACTION_REDO);
        redo.addMouseListener(this);

        JButton backward = new ToolbarButton(LastAndNextViewManager.ACTION_GOTO_PREVIOUS_VIEW);
        JButton forward = new ToolbarButton(LastAndNextViewManager.ACTION_GOTO_NEXT_VIEW);

        CSH.setHelpIDString(this, "standardsymbolleiste");

        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(fach);
        bGroup.add(phy);
        bGroup.add(log);

        add(neu);
        add(open);
        add(save);
        addSeparator();
        add(undo);
        add(redo);
        addSeparator();
        add(switchView);
        add(fach);
        add(log);
        add(phy);
        addSeparator();
        add(backward);
        add(forward);

        addAsToolChangeListener();

    }

    @Override
    public void model_change_changed(final GraphDocument source) {
        LGMGraphDocument selectedDoc = Static.getSelectedDoc();
        if (doc != selectedDoc) {
            if (doc != null) {
                doc.removeClosedTransactionsListener(this);
            }
            doc = selectedDoc;
            if (doc != null) {
                doc.addClosedTransactionsListener(this);
            }
        }
        update();
    }

    @Override
    public void update() {
        //Alle Knöpfe aktualisieren
        for (Component component : getComponents()) {
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                Action action = button.getAction();
                if (action != null) {
                    button.setEnabled(action.isEnabled());
                    button.setText("");
                }
            }
        }
        revalidate();
        repaint();
    }

    @Override
    public void changed() {
        update();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mousePressed(final MouseEvent e) {
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    private static int toolTipDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();

    @Override
    public void mouseEntered(final MouseEvent e) {
        if (e.getSource() == undo || e.getSource() == redo) {
            toolTipDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
            ToolTipManager.sharedInstance().setDismissDelay(100000);
            updateUndoRedoToolTips();
        }
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        ToolTipManager.sharedInstance().setDismissDelay(toolTipDismissDelay);
    }

    /**
     * Aktualisiert die Tooltips des Undo- und Redo-Knopfes. Wenn man den Verification-Modus einschaltet, wird
     * der Undo-Stack als Tooltip angezeigt.
     */
    private void updateUndoRedoToolTips() {
        GraphDocument doc = Static.getSelectedDoc();
        //die Tooltips auf den Undo-Redo-Buttons sollen den Queue anzeigen, wenn das aktuelle doc auf verfificationMode gestellt wurde oder der globale Code-Schalter an ist
        boolean showQueueAsToolTip = doc != null && doc.getCollection().getTman() != null && doc.isVerificationMode();
        if (showQueueAsToolTip) {
            TransactionManager tman = doc.getCollection().getTman();
            String queue = tman.getQueue(10);
            queue = queue.replaceAll("\n", "<br>");
            queue = "<html><body>" + queue + "</html></body>";
            undo.setToolTipText(queue);
            redo.setToolTipText(queue);
        } else {
            String identifier = undo.getAction().getValue(StaticAction.IDENTIFIER_KEY).toString();
            undo.setToolTipText(getResString(StaticAction.TOOLTIP_RESSOURCE_PREFIX + identifier));

            identifier = redo.getAction().getValue(StaticAction.IDENTIFIER_KEY).toString();
            redo.setToolTipText(getResString(StaticAction.TOOLTIP_RESSOURCE_PREFIX + identifier));
        }
    }

    private final class ToolbarButton extends JButton {

        public ToolbarButton(final Action a) {
            super(a);
            setText(null);
            //Diesen value muss es bei diesen Actions immer geben
            CSH.setHelpIDString(this, a.getValue(StaticAction.IDENTIFIER_KEY).toString());
        }

        @Override
        public void setText(final String arg0) {
            //das muss sein, weil bei einer im Menü und in der Toolbar gleichzeitig genutzen tool3lgm.util.swing.event.ToggleAction
            //beim Aufblättern des Menüs der Text neu gesetzt wird und somit auch in der Toolbar erscheint
            super.setText(null);
        }

    }

}
