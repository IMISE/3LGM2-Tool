/*
 * Created on 08.01.2004 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog.element.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMComponentListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMWindowListener;
import de.imise.tool3lgm.graphtools.dialog.element.AbstractElementPropertyDialog;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.swing.SwingUtils;

/**
 * @author Thomas Ist ein TestPanel zur Verallgemeinerung abgeleiteter Panels
 */
public abstract class ElementDialogPanel extends JPanel {

    /** Der Dialog in dem sich dieses Panel befindet */
    protected final AbstractElementPropertyDialog dialog;

    /** Der ElementsNameBuilder des zugehörigen Metamodells */
    protected final ElementsNameBuilder elementsNameBuilder;

    /**
     * Liste mit allen Elementen, die im Panel selektiert sind. Da ein Panel mehrere Bäume enthalten
     * kann, wird sich in dieser Liste sozusagen die Gesamtselektion gemerkt.
     */
    protected List<ElementContainer> highlight = new ArrayList<>(0);

    /**
     * COMMENTME
     */
    protected List<Object> specialInfoOwner = new ArrayList<>(0);

    /**
     * COMMENTME
     */
    protected EventObject lastSelEvent = null;

    /**
     * COMMENTME
     */
    private int correctingSelectionCount = 0;

    /**
     * COMMENTME
     */
    private LGMWindowListener windowListener;

    /**
     * COMMENTME
     */
    private LGMComponentListener componentListener;

    /**
     * COMMENTME
     */
    private LGMAction windowClosedAction;

    /**
     * COMMENTME
     */
    private LGMAction componentShownAction;

    /** All consistency errors that can be fixed in this panel */
    protected Collection<AbstractConsistencyError> consistencyErrors;

    /** ****************************************************************************** */

    /**
     * TODO: die Konstruktoren braucht man sicher nicht mehr, wenn alle Panels mit Kanten oder
     * Pfaden initialisiert werden
     *
     * @param dialog Dialog, der dieses Panel enthält
     */
    public ElementDialogPanel(final AbstractElementPropertyDialog dialog) {
        this(dialog, (String) null);
    }

    /**
     * @param dialog Dialog, der dieses Panel enthält
     * @param name
     */
    public ElementDialogPanel(final AbstractElementPropertyDialog dialog, final String name) {
        this.dialog = dialog;
        setName(name);
        GraphDocument mainDoc = dialog.getMainDoc();
        elementsNameBuilder = mainDoc.getElementsNameBuilder();
        init();
    }

    /**
     *
     */
    protected void init() {
        setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
        highlight = new ArrayList<>();

        windowClosedAction = LGMActionLibrary.getWindowClosedAction(this);
        componentShownAction = LGMActionLibrary.getComponentShownAction(this);

        windowListener = new LGMWindowListener(null, windowClosedAction, null, null, null, null, null);
        componentListener = new LGMComponentListener(null, null, null, componentShownAction);

        dialog.addWindowListener(windowListener);
        addComponentListener(componentListener);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     *
     */
    public abstract void update();

    /**
     *
     */
    public void commit() {
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    // ----------------------------------------------------------------------------------------------------------------------------------
    /**
     * @param con
     * @param c
     * @param gbc
     * @param x
     * @param y
     * @param w
     * @param h
     */
    protected final void add(final Container con, final Component c, final GridBagConstraints gbc, final int x, final int y, final int w, final int h) {
        //der Static import funktioniert nicht mit der add-Funktion, weil das mit den add-Funktionen aus Container kollidiert
        SwingUtils.add(con, c, gbc, x, y, w, h);
    }

    /**
     * @return a button that the dialog will show beside the OK,Cancel, TakeOver buttons
     */
    public JButton getPanelButton() {
        return null;//Subclasses that will show e.g. a view button beside the OK,Cancel, TakeOver buttons can return the button here
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * @param tree
     */
    protected void expandTree(final JTree tree) {
        for (int n = 0; n < tree.getRowCount(); n++) {
            tree.expandRow(n);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     *
     */
    public void removeHighLightsAndSpecialInfos() {
        removeHighLights();
        removeSpecialInfos();
    }

    /**
     *
     */
    public void removeHighLights() {
        for (int b = 0; b < highlight.size(); b++) {
            ElementContainer ec = highlight.get(b);
            if (ec != null) {
                ec.setHighLight(false);
            }
        }
        highlight.clear();
    }

    /**
     *
     */
    public void removeSpecialInfos() {
        for (int i = 0; i < specialInfoOwner.size(); i++) {
            // TODO:AXS: das hier geht für LGMProcessSteps jetzt auf jeden Fall schief, weil sie
            // keine ElementContainer mehr sind

            ElementContainer ec = (ElementContainer) specialInfoOwner.get(i);
            if (ec != null) {
                // ec.deleteSpecialInfoFromMyTargets();
            }
        }
        specialInfoOwner.clear();
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    public int getTransactionID() {
        return dialog.getTransactionID();
    }

    /**
     *
     */
    public void clearHighlight() {
        highlight.clear();
    }

    /**
     * @param ec
     */
    public void addHighlight(final ElementContainer ec) {
        highlight.add(ec);
    }

    /**
     * @param list
     */
    public void setHighlightVector(final List<ElementContainer> list) {
        highlight = list;
    }

    /**
     * @return dialog
     */
    public final AbstractElementPropertyDialog getDialog() {
        return dialog;
    }

    /**
     * @return correctingSelectionCount
     */
    public int getCorrectingSelectionCount() {
        return correctingSelectionCount;
    }

    /**
     * @return lastSelEvent
     */
    public EventObject getLastSelEvent() {
        return lastSelEvent;
    }

    /**
     * @param eventObject
     */
    public void setLastSelEvent(final EventObject eventObject) {
        lastSelEvent = eventObject;
    }

    /**
     * @param correctedCount
     */
    public void setCorrectingSelectionCount(final int correctedCount) {
        correctingSelectionCount = correctedCount;
    }

    /**
     *
     */
    public void increaseCorrectingSelectionCount() {
        correctingSelectionCount++;
    }

    /**
     *
     */
    public void decreaseCorrectingSelectionCount() {
        correctingSelectionCount--;
    }

    /**
     * @return modelElement
     */
    public ModelElement getModelElement() {
        return dialog.getModelElement();
    }

    /**
     * @return
     */
    public Class<? extends ModelElement> getModelElementClass() {
        ModelElement me = getModelElement();
        return me.getClass();
    }

    /**
     * Das Hauptdokument des Modells. In den Dialogen werden immer alle Verbindungen angezeigt, die
     * in diesem Dokument vorkommen.
     *
     * @return
     */
    public LGMGraphDocument getMainDoc() {
        GDCollection gdcoll = getCollection();
        LGMGraphDocument mainDoc = gdcoll.getMainDoc();
        return mainDoc;
    }

    /**
     * @return
     */
    public LGMGraphDocument getSelectedDoc() {
        GDCollection gdcoll = getCollection();
        return gdcoll.getSelectedDoc();
    }

    /**
     * @return
     */
    public GDCollection getCollection() {
        ModelElement me = getModelElement();
        GDCollection gdcoll = me.getCollection();
        return gdcoll;
    }

    /**
     * @return
     */
    public final MetaModel getMetaModel() {
        return elementsNameBuilder.getMetaModel();
    }

    /**
     * @param consistencyError
     */
    public boolean addConsistencyError(final AbstractConsistencyError consistencyError) {
        if (this instanceof DisplayAndFixConsistencyErrorPanel) {
            DisplayAndFixConsistencyErrorPanel panel = (DisplayAndFixConsistencyErrorPanel) this;
            ElementDialogPanel responsiblePanelForConsistencyError = panel.getResponsiblePanelForConsistencyError(consistencyError);
            if (responsiblePanelForConsistencyError != null) {
                addConsistencyError(responsiblePanelForConsistencyError, consistencyError);
                addConsistencyError(this, consistencyError);
                return true;
            }
        }
        return false;
    }

    /**
     * @param panel
     * @param consistencyError
     */
    private static void addConsistencyError(final ElementDialogPanel panel, final AbstractConsistencyError consistencyError) {
        if (panel.consistencyErrors == null) {
            panel.consistencyErrors = new ArrayList<>();
        }
        panel.consistencyErrors.add(consistencyError);
    }

    /**
     *
     */
    public void clearConsistencyErrors() {
        if (consistencyErrors != null) {
            consistencyErrors.clear();
        }
    }

    /**
     * @return the consistency errors this panel or subpanels are displaying
     */
    public Collection<AbstractConsistencyError> getConsistencyErrors() {
        return consistencyErrors;
    }

    /**
     * @return
     */
    public final boolean hasConsistencyErrors() {
        Collection<AbstractConsistencyError> consistencyErrors = getConsistencyErrors(); //don't replace the function by direct access of this.consistencyErrors
        return consistencyErrors != null && !consistencyErrors.isEmpty();
    }

    // -------------------------------------------------------------------------------- -/

}
