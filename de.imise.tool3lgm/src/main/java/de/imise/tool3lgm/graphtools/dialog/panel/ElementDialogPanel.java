/*
 * Created on 08.01.2004 To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMComponentListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMWindowListener;
import de.imise.tool3lgm.graphtools.metamodel.MetaModelInstance;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.TreeRenderer;
import de.imise.util.swing.SwingUtils;

/**
 * @author Thomas Ist ein TestPanel zur Verallgemeinerung abgeleiteter Panels
 */
public abstract class ElementDialogPanel extends JPanel {

    /**
     * GraphDocument, für das das Panel geöffnet wurde. Über das Panel neu angelegte Elemente sind
     * automatisch in diesem GraphDocument enthalten.
     */
    protected GraphDocument doc;

    /**
     * Das Hauptdokument des Modells. In den Dialogen werden immer alle Verbindungen angezeigt, die
     * in diesem Dokument vorkommen.
     */
    protected GraphDocument mainDoc;

    /** Der Dialog in dem sich dieses Panel befindet */
    protected final ElementPropertyDialog dialog;

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
     * Der Renderer der Bäume
     */
    protected TreeRenderer treeRenderer;

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

    /** ****************************************************************************** */

    /**
     * TODO: die Konstruktoren braucht man sicher nicht mehr, wenn alle Panels mit Kanten oder
     * Pfaden initialisiert werden
     *
     * @param dialog Dialog, der dieses Panel enthält
     */
    public ElementDialogPanel(final ElementPropertyDialog dialog) {
        this(dialog, (String) null);
    }

    /**
     * @param dialog Dialog, der dieses Panel enthält
     * @param name
     */
    public ElementDialogPanel(final ElementPropertyDialog dialog, final String name) {
        super();
        this.dialog = dialog;
        setName(name);
        doc = dialog.getGraphDocument();
        mainDoc = doc.getCollection().getMainGraphDocument();
        treeRenderer = new TreeRenderer(doc);
        elementsNameBuilder = doc.getElementsNameBuilder();
        init();
    }

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

    public abstract void update();

    public void commit() {
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    // ----------------------------------------------------------------------------------------------------------------------------------
    protected final void add(final Container con, final Component c, final GridBagConstraints gbc, final int x, final int y, final int w, final int h) {
        //der Static import funktioniert nicht mit der add-Funktion, weil das mit den add-Funktionen aus Container kollidiert
        SwingUtils.add(con, c, gbc, x, y, w, h);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    protected void expandTree(final JTree tree) {
        for (int n = 0; n < tree.getRowCount(); n++) {
            tree.expandRow(n);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    public void removeHighLightsAndSpecialInfos() {
        removeHighLights();
        removeSpecialInfos();
    }

    public void removeHighLights() {
        for (int b = 0; b < highlight.size(); b++) {
            ElementContainer ec = highlight.get(b);
            if (ec != null) {
                ec.setHighLight(false);
            }
        }
        highlight.clear();
    }

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

    /**
     * @return doc
     */
    public GraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * @return Das selektierte {@link GraphDocument} der {@link GDCollection} des {@link GraphDocument} des Dialoges.
     */
    public GraphDocument getSelectedGraphDocument() {
        return doc.getCollection().getSelectedDoc();
    }

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

    public void setHighlightVector(final List<ElementContainer> list) {
        highlight = list;
    }

    /**
     * @return dialog
     */
    public final ElementPropertyDialog getDialog() {
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

    public void setLastSelEvent(final EventObject eo) {
        lastSelEvent = eo;
    }

    public void setCorrectingSelectionCount(final int correctedCount) {
        correctingSelectionCount = correctedCount;
    }

    /**
     * @return modelElement
     */
    public final ModelElement getModelElement() {
        return dialog.getModelElement();
    }

    public final MetaModelInstance getMetaModel() {
        return mainDoc.getMetaModel();
    }

    /**
     * @return
     */
    public final ElementsNameBuilder getElementNameBuilder() {
        return mainDoc.getElementsNameBuilder();
    }

    // -------------------------------------------------------------------------------- -/

}
