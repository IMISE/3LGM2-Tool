package de.imise.tool3lgm.gui;

import static de.imise.tool3lgm.Tool3lgmConstants.getToolTipResString;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.help.CSH;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.ToolTipManager;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmChangeListener;
import de.imise.tool3lgm.event.ActionLibrary;
import de.imise.tool3lgm.event.action.StaticAction;
import de.imise.tool3lgm.graphtools.model.GDCommands;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListenerSimple;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.util.swing.component.DropDownButtonSource;
import de.imise.util.swing.component.UnfloatableToolBar;
import de.imise.util.swing.event.ActionSource;
import de.imise.util.swing.event.ExtendedAction;

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

        JButton showConf = new ToolbarButton(ActionLibrary.ContextActions.MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_ON, true);
        JButton hideConf = new ToolbarButton(ActionLibrary.ContextActions.MODEL_ACTION_SET_INTERLAYER_CONNECTIONS_VISIBILITY_OFF, true);

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
        addSeparator();
        add(showConf);
        add(hideConf);
        addSeparator();

        // Definition of the buttons and the action group in every button. null values create a separator in the buttons list
        //and the DropDownButtonSource.DEFAULT_ACTION_INDICATOR defines the NEXT ActionSource as default for the button
        ActionSource[][] alignmentAndPositionActions = {
                {
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_LEFT, //text left aligned
                        DropDownButtonSource.NEXT_ACTION_DEFAULT_INDICATOR,
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_CENTER, //text center aligned
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_RIGHT, //text right aligned
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_ALIGNMENT_HTML_JUSTIFY, //text justified
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_LEFT, //text position left
                        DropDownButtonSource.NEXT_ACTION_DEFAULT_INDICATOR,
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_CENTER, //text position center
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_HORIZONTAL_RIGHT, //text position right
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_TOP, //text position top
                        DropDownButtonSource.NEXT_ACTION_DEFAULT_INDICATOR,
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_CENTER, //text position center
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TEXT_POSITION_VERTICAL_BOTTOM, //text position bottom
                }, { //Separator
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_LEFT, //elements position left
                        DropDownButtonSource.NEXT_ACTION_DEFAULT_INDICATOR,
                        GDCommands.MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_CENTER, //elements psoition center
                        GDCommands.MODEL_ACTION_SET_ELEMENTS_POSITION_HORIZONTAL_RIGHT, //elements position right
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_TOP, //elements position top
                        DropDownButtonSource.NEXT_ACTION_DEFAULT_INDICATOR,
                        GDCommands.MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_CENTER, //elements position center
                        GDCommands.MODEL_ACTION_SET_ELEMENTS_POSITION_VERTICAL_BOTTOM, //elements position bottom
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENTS_ALIGNMENT_GRID,
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH, //elements get same width
                        GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_HEIGTH, //elements get same height
                        DropDownButtonSource.NEXT_ACTION_DEFAULT_INDICATOR,
                        GDCommands.MODEL_ACTION_SET_ELEMENT_ALIGNMENT_SIZE_WIDTH_AND_HEIGTH, //elements get same width and height
                }, { //Separator
                }, {
                        GDCommands.MODEL_ACTION_MOVE_ORDER_TO_FIRST_POSITION, //text position top
                        GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_UP, //text position one up
                        GDCommands.MODEL_ACTION_MOVE_ORDER_ONE_POSITION_DOWN, //text position one down
                        DropDownButtonSource.NEXT_ACTION_DEFAULT_INDICATOR,
                        GDCommands.MODEL_ACTION_MOVE_ORDER_TO_LAST_POSITION, //text position last
                }, { //Separator
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENT_FONT, //set text font
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENT_COLOR, //set element color
                }, {
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_NONE, //set element transparency to non-transparent
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_HALF, //set element transparency to half-transparent
                        GDCommands.MODEL_ACTION_SET_ELEMENT_TRANSPARENCY_FULL, //set element transparency to full-transparent
                }, {}, {
                        GDCommands.MODEL_ACTION_ADOPT_SAME_FONT, //set selected elements' font to last selected
                        GDCommands.MODEL_ACTION_ADOPT_SAME_TRANSPARENCY, //set selected elements' transparency to last selected
                        GDCommands.MODEL_ACTION_ADOPT_SAME_COLOR, //set selected elements' color to last selected
                        GDCommands.MODEL_ACTION_ADOPT_SAME_ALL //set selected elements' style to last selected
                }
        };

        //bisheriger Code = alle Buttons nebeneinander (ohne DropdownButtons)
        //        addSeparator();
        //        int i = 0;
        //        for (ActionSource[] alignmentActions : alignmentAndPositionActions) {
        //            if (i++ > 0) {
        //                addSeparator();
        //            }
        //            for (ActionSource actionSource : alignmentActions) {
        //                add(new ToolbarButton(actionSource, true));
        //            }
        //        }

        //neuer Code = alle Funktionen über DropdownButtons zur Verfügung stellen
        for (ActionSource[] alignmentActions : alignmentAndPositionActions) {
            addDropDownButton(alignmentActions);
        }

        addAsToolChangeListener();
    }

    /**
     * @param actionSources
     */
    private void addDropDownButton(final ActionSource[] actionSources) {
        if (actionSources.length == 0) {
            addSeparator();
            return;
        }
        DropDownButtonSource dropDownButtonSource = new DropDownButtonSource(actionSources);
        JButton button = dropDownButtonSource.getButtonComponent();
        add(button);

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
     * Aktualisiert die ToolTips des Undo- und Redo-Knopfes. Wenn man den
     * Verification-Modus einschaltet, wird der Undo-Stack als Tooltip
     * angezeigt.
     */
    private void updateUndoRedoToolTips() {
        GraphDocument selectedDoc = Static.getSelectedDoc();
        //die ToolTips auf den Undo-Redo-Buttons sollen den Queue anzeigen, wenn das aktuelle doc auf verfificationMode gestellt wurde oder der globale Code-Schalter an ist
        boolean showQueueAsToolTip = selectedDoc != null && selectedDoc.getCollection().getTman() != null && selectedDoc.isVerificationMode();
        if (showQueueAsToolTip) {
            TransactionManager tman = selectedDoc.getCollection().getTman();
            String queue = tman.getQueue(10);
            queue = queue.replaceAll("\n", "<br>");
            queue = "<html><body>" + queue + "</html></body>";
            undo.setToolTipText(queue);
            redo.setToolTipText(queue);
        } else {
            setStaticResourceToolTip(undo);
            setStaticResourceToolTip(redo);
        }
    }

    /**
     * @param button
     */
    private void setStaticResourceToolTip(final JButton button) {
        Action action = button.getAction();
        Object toolTipReskey = action.getValue(StaticAction.IDENTIFIER_KEY);
        String toolTipText = getToolTipResString(toolTipReskey);
        button.setToolTipText(toolTipText);
    }

    private final class ToolbarButton extends JButton {

        public ToolbarButton(final Action a, final boolean setSmallIcon) {
            super(a);
            setText(null);
            //Diesen value muss es bei diesen Actions immer geben
            CSH.setHelpIDString(this, a.getValue(StaticAction.IDENTIFIER_KEY).toString());
            //ModelActions (Text Alignment + Postion) bekommen kleinere Buttons
            if (setSmallIcon && a instanceof ExtendedAction) {
                ExtendedAction ea = (ExtendedAction) a;
                Icon smallIcon = ea.getSmallIcon();
                setIcon(smallIcon);
            }
        }

        public ToolbarButton(final Action a) {
            this(a, false);
        }

        public ToolbarButton(final ActionSource actionSource, final boolean smallIcon) {
            this((Action) actionSource.createAction(), smallIcon);
        }

        @Override
        public void setText(final String arg0) {
            //das muss sein, weil bei einer im Menü und in der Toolbar gleichzeitig genutzen tool3lgm.util.swing.event.ToggleAction
            //beim Aufblättern des Menüs der Text neu gesetzt wird und somit auch in der Toolbar erscheint
            super.setText(null);
        }

    }

}
