package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.help.CSH;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.ToolTipManager;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;
import de.imise.tool3lgm.log.Log;
import de.imise.util.swing.component.UnfloatableToolBar;

// TODO: alles auf die Actions umstellen, die auch das Menü benutzt. Die Actions sollten aber auch gleich mit den Icons klarkommen
public class ToolBar extends UnfloatableToolBar implements ActionListener, MouseListener {
    //	private AbstractInternalFrame frame = null;
    public JButton multi, redo, undo, print, backward, forward;

    private final JToggleButton fach, log, phy;

    private final JButton neu, open, save;

    ///*	private JButton testButton = new JButton(new AbstractAction("Test"){
    //	 public void actionPerformed(ActionEvent e) {
    //	 GraphDocument doc = Tool3lgm.tool.getSelectedDoc();
    //	 if (doc==null)
    //	 return;
    //	 ArrayList aufgaben = doc.getAllModelElements(Aufgabe.class, true, true);
    //	 for (int i=0; i<aufgaben.size(); i++){
    //	 Aufgabe auf = (Aufgabe)aufgaben.get(i);
    //	 ArrayList connnections = auf.getConnections(AufObjVerbindung.class);
    //	 for (int j=0; j<connnections.size(); j++){
    //	 Doppelkante kante = (Doppelkante)connnections.get(j);
    //	 System.err.println(kante.getDirection() + " " + kante.getStart().getClass().getSimpleName() + ": " + kante.getStart().getClearName() + "\t" + kante.getEnd().getClass().getSimpleName() + ": " + kante.getEnd().getClearName());
    //	 }
    //	 }
    //	 }
    //	 });
    //	 */

    int windowIndex = -1;

    ArrayList<AbstractInternalFrame> windowList = new ArrayList<>();

    boolean operatingWindowList = false;

    public ToolBar() {
        super();

        setFloatable(false);

        multi = new JButton(Tool3lgmConstants.getIcon("singelview.gif"));
        multi.setToolTipText("MultiView");
        multi.setActionCommand("multi");
        multi.addActionListener(this);
        CSH.setHelpIDString(multi, "swl_multiview");

        fach = new JToggleButton(Tool3lgmConstants.getLocalizedIcon("domainLayer.gif"));
        fach.setToolTipText(getResString("domain_layer"));
        fach.setActionCommand("fachlich");
        fach.addActionListener(this);
        CSH.setHelpIDString(fach, "swl_fachlich");
        log = new JToggleButton(Tool3lgmConstants.getLocalizedIcon("logicalToolLayer.gif"));
        log.setToolTipText(getResString("logical_tool_layer"));
        log.setActionCommand("logisch");
        log.addActionListener(this);
        CSH.setHelpIDString(log, "swl_logisch");
        phy = new JToggleButton(Tool3lgmConstants.getLocalizedIcon("physicalToolLayer.gif"));
        phy.setToolTipText(getResString("physical_tool_layer"));
        phy.setActionCommand("physisch");
        phy.addActionListener(this);
        CSH.setHelpIDString(phy, "swl_physisch");

        neu = createToolBarButton(ActionLibrary.FileActions.ACTION_NEW_MODEL, "ICON_LARGE_ACTION_NEW_MODEL.gif", "TOOLTIP_ACTION_NEW_MODEL", "ACTION_NEW_MODEL");
        open = createToolBarButton(ActionLibrary.FileActions.ACTION_OPEN_MODEL, "ICON_LARGE_ACTION_OPEN_MODEL.gif", "TOOLTIP_ACTION_OPEN_MODEL", "ACTION_OPEN_MODEL");
        save = createToolBarButton(ActionLibrary.FileActions.ACTION_SAVE_MODEL, "ICON_LARGE_ACTION_SAVE_MODEL.gif", "TOOLTIP_ACTION_SAVE_MODEL", "ACTION_SAVE_MODEL");

        print = new JButton(Tool3lgmConstants.getIcon("print.gif"));
        print.setToolTipText(getResString("modell_drucken"));
        print.setActionCommand("print");
        print.addActionListener(this);

        undo = new JButton(Tool3lgmConstants.getIcon("undo.gif"));
        undo.setToolTipText(getResString("undo"));
        undo.setActionCommand("undo");
        undo.addMouseListener(this);
        undo.addActionListener(this);
        CSH.setHelpIDString(undo, "swl_undo");

        redo = new JButton(Tool3lgmConstants.getIcon("redo.gif"));
        redo.setToolTipText(getResString("redo"));
        redo.setActionCommand("redo");
        redo.addMouseListener(this);
        redo.addActionListener(this);
        CSH.setHelpIDString(redo, "swl_redo");

        addControlButtons();

        backward = new JButton(Tool3lgmConstants.getIcon("arrow_left.gif"));
        backward.setToolTipText(getResString("vf"));
        backward.setActionCommand("backward");
        backward.addActionListener(this);
        backward.setEnabled(false);

        forward = new JButton(Tool3lgmConstants.getIcon("arrow_right.gif"));
        forward.setToolTipText(getResString("nf"));
        forward.setActionCommand("forward");
        forward.addActionListener(this);
        forward.setEnabled(false);

        addButtonsView();

        CSH.setHelpIDString(this, "standardsymbolleiste");

        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(fach);
        bGroup.add(phy);
        bGroup.add(log);
    }

    private static final JButton createToolBarButton(final Action action, final String iconName, final String toolTipResKey, final String helpID) {
        JButton button = new JButton(action);
        button.setIcon(Tool3lgmConstants.getIcon(iconName));
        button.setText(null);
        button.setToolTipText(getResString(toolTipResKey));
        if (helpID != null) {
            CSH.setHelpIDString(button, helpID);
        }
        return button;
    }

    private void addButtonsView() {
        removeAll();

        addControlButtons();

        add(multi);

        add(fach);
        add(log);
        add(phy);

        addSeparator();
        add(backward);
        add(forward);

        repaint();
    }

    private void addControlButtons() {
        add(neu);
        add(open);
        add(save);
        //add(print);

        addSeparator();
        undo.setEnabled(false);
        add(undo);

        redo.setEnabled(false);
        add(redo);

        addSeparator();

        //		add(testButton);

    }

    public void checkUndoandRedo() {
        GraphDocument gd = Static.getSelectedDoc();
        if (gd == null) {
            undo.setEnabled(false);
            redo.setEnabled(false);
            return;
        }
        if (gd.getCollection().getTman().isUndoAvailable()) {
            undo.setEnabled(true);
        } else {
            undo.setEnabled(false);
        }
        if (gd.getCollection().getTman().isRedoAvailable()) {
            redo.setEnabled(true);
        } else {
            redo.setEnabled(false);
        }
    }

    public void checkMulti() {
        AbstractInternalFrame frame = Static.getActiveFrame();
        if (frame == null) {
            return;
        }
        if (!(frame instanceof ToolInternalFrame)) {
            return;
        }
        if (multi == null) {
            return;
        }
        if (((ToolInternalFrame) frame).getInputGraphArea().isMultiViewEnabled()) {
            multi.setIcon(Tool3lgmConstants.getIcon("singelview.gif"));
        } else {
            multi.setIcon(Tool3lgmConstants.getIcon("multiview.gif"));
        }
    }

    public void setSaveEnabled(final boolean enabled) {
        save.setEnabled(enabled);
    }

    public void changeMultiSingleView() {

        AbstractInternalFrame frame = Static.getActiveFrame();
        if (!(frame instanceof ToolInternalFrame)) {
            return;
        }
        InputGraphArea area = ((ToolInternalFrame) frame).getInputGraphArea();

        if (area.isMultiViewEnabled()) {
            area.setMultiViewEnabled(false);
            multi.setIcon(Tool3lgmConstants.getIcon("multiview.gif"));
        } else {
            area.setMultiViewEnabled(true);
            multi.setIcon(Tool3lgmConstants.getIcon("singelview.gif"));
        }

        Static.getTool().activeLayerChanged(Static.getSelectedDoc());
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

        if (s.equals("multi")) {
            changeMultiSingleView();
            return;
        }

        GraphDocument doc = Static.getSelectedDoc();
        if (doc == null) {
            return;
        }
        if (s.equals("fachlich")) {
            doc.getCollection().setActiveLayer(4);
            return;
        }
        if (s.equals("logisch")) {
            doc.getCollection().setActiveLayer(2);
            return;
        }
        if (s.equals("physisch")) {
            doc.getCollection().setActiveLayer(0);
            return;
        }
        if (s.equals("redo")) {
            doc.redo();
            updateUndoRedoToolTips();
            return;
        }
        if (s.equals("undo")) {
            doc.undo();
            updateUndoRedoToolTips();
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

    /**
     * @param c
     */
    public void setActiveLayer(final int c) {
        switch (c) {
        case ModelConstants.PHYSICAL_LAYER:
            phy.setSelected(true);
            break;
        case ModelConstants.LOGICAL_LAYER:
            log.setSelected(true);
            break;
        case ModelConstants.DOMAIN_LAYER:
            fach.setSelected(true);
            break;
        default:
            break;
        }

    }

    /**
     *
     */
    public void checkLayer() {
        GraphDocument doc = Static.getSelectedDoc();
        if (doc != null) {
            setActiveLayer(doc.getCollection().getActiveLayer());
        }
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
     *
     */
    private void updateUndoRedoToolTips() {
        GraphDocument doc = Static.getSelectedDoc();

        if (doc == null || !doc.isVerificationMode()) {
            undo.setToolTipText(getResString("undo"));
            redo.setToolTipText(getResString("redo"));
            return;
        }
        TransactionManager tman = doc.getCollection().getTman();
        if (tman == null) {
            undo.setToolTipText(getResString("undo"));
            redo.setToolTipText(getResString("redo"));
            return;
        }
        String queue = tman.getQueue(10);
        queue = queue.replaceAll("\n", "<br>");
        queue = "<html><body>" + queue + "</html></body>";

        undo.setToolTipText(queue);
        redo.setToolTipText(queue);

    }

}
