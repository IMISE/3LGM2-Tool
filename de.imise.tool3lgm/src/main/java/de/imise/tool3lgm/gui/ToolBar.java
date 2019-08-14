package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.help.CSH;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.ToolTipManager;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.event.action.StaticAction;
import de.imise.tool3lgm.graphtools.model.GDCollectionChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.component.UnfloatableToolBar;

// TODO:alle Buttons auf Actions umstellen (so wie zum Teil schon geschehen)
public class ToolBar extends UnfloatableToolBar implements ActionListener, MouseListener, GDCollectionChangeListenerSimple {

    private GraphDocument doc = null;

    private int windowIndex = -1;

    private final JButton forward, backward, undo, redo;

    private final List<AbstractInternalFrame> windowList = new ArrayList<>();

    private boolean operatingWindowList = false;

    public ToolBar() {
        super();

        setFloatable(false);

        JButton switchView = new ToolbarButton(ActionLibrary.ViewActions.ACTION_GRAPH_SWITCH_ONE_LAYER_AND_THREE_LAYER_PERSPECTIVE);

        JButton fach = new ToolbarButton(ActionLibrary.ViewActions.ACTION_ACTIVATE_DOMAIN_LAYER);
        JButton log = new ToolbarButton(ActionLibrary.ViewActions.ACTION_ACTIVATE_LOGICAL_TOOL_LAYER);
        JButton phy = new ToolbarButton(ActionLibrary.ViewActions.ACTION_ACTIVATE_PHYSICAL_TOOL_LAYER);

        JButton neu = new ToolbarButton(ActionLibrary.FileActions.ACTION_NEW_MODEL);
        JButton open = new ToolbarButton(ActionLibrary.FileActions.ACTION_OPEN_MODEL);
        JButton save = new ToolbarButton(ActionLibrary.FileActions.ACTION_SAVE_MODEL);

        undo = new ToolbarButton(ActionLibrary.EditActions.ACTION_UNDO);
        undo.addMouseListener(this);
        redo = new ToolbarButton(ActionLibrary.EditActions.ACTION_REDO);
        redo.addMouseListener(this);

        backward = new JButton(Tool3lgmConstants.getIcon("arrow_left.gif"));
        backward.setToolTipText(Tool3lgmConstants.getResString("vf"));
        backward.setActionCommand("backward");
        backward.addActionListener(this);
        backward.setEnabled(false);

        forward = new JButton(Tool3lgmConstants.getIcon("arrow_right.gif"));
        forward.setToolTipText(Tool3lgmConstants.getResString("nf"));
        forward.setActionCommand("forward");
        forward.addActionListener(this);
        forward.setEnabled(false);

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

    }

    public void selectedDocChanged() {
        if (doc != null) {
            doc.removeGDCollectionChangeListener(this);
        }
        doc = Static.getSelectedDoc();
        if (doc != null) {
            doc.addGDCollectionChangeListener(this);
        }
        updateButtons();
    }

    private void updateButtons() {
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
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("forward")) {
            operatingWindowList = true;
            AbstractInternalFrame f = getNextWindow();
            if (f != null) {
                try {
                    f.setSelected(true);
                } catch (Exception ex) {
                    Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
                }
            }
            operatingWindowList = false;
            return;
        }
        if (s.equals("backward")) {
            operatingWindowList = true;
            AbstractInternalFrame f = getPreviousWindow();
            if (f != null) {
                try {
                    f.setSelected(true);
                } catch (Exception ex) {
                    Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
                }
            }
            operatingWindowList = false;
            return;
        }
    }

    public void addWindow(final AbstractInternalFrame frame) {
        if (operatingWindowList) {
            return;
        }
        if (frame == null) {
            return;
        }
        if (windowIndex < 0 || windowList.get(windowIndex) != frame) {
            for (int i = windowList.size() - 1; i > windowIndex; i--) {
                windowList.remove(i);
            }
            windowIndex++;
            if (windowIndex >= windowList.size()) {
                windowList.add(frame);
            } else {
                windowList.add(windowIndex, frame);
            }
        }
        if (windowIndex > 0) {
            backward.setEnabled(true);
        }
        if (windowIndex >= windowList.size()) {
            forward.setEnabled(false);
        }
    }

    public void removeWindow(final AbstractInternalFrame frame) {
        if (operatingWindowList) {
            return;
        }
        int index = windowList.indexOf(frame);
        while (index >= 0) {
            windowList.remove(index);
            if (windowIndex >= index) {
                windowIndex--;
            }
            index = windowList.indexOf(frame);
        }
        if (windowIndex < -1) {
            windowIndex = -1;
        }
        if (windowIndex >= windowList.size()) {
            windowIndex = windowList.size() - 1;
        }

        if (windowIndex <= 0) {
            backward.setEnabled(false);
        }
        if (windowIndex >= windowList.size() - 1) {
            forward.setEnabled(false);
        }
    }

    public AbstractInternalFrame getNextWindow() {
        if (windowIndex < 0 || windowIndex >= windowList.size() - 1) {
            return null;
        }
        AbstractInternalFrame retVal = windowList.get(windowIndex + 1);
        if (windowIndex < windowList.size() - 1) {
            windowIndex++;
        }
        if (windowIndex >= windowList.size() - 1) {
            forward.setEnabled(false);
        }
        if (windowIndex >= 0) {
            backward.setEnabled(true);
        }
        return retVal;
    }

    public AbstractInternalFrame getPreviousWindow() {
        if (windowIndex <= 0 || windowIndex > windowList.size()) {
            return null;
        }
        AbstractInternalFrame retVal = windowList.get(windowIndex - 1);
        if (windowIndex >= 0) {
            windowIndex--;
        }

        if (windowIndex <= 0) {
            backward.setEnabled(false);
        }
        if (windowIndex < windowList.size()) {
            forward.setEnabled(true);
        }
        return retVal;
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

    @Override
    public void changed() {
        updateButtons();
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
