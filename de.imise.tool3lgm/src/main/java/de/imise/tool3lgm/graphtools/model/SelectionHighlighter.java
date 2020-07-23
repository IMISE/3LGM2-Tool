package de.imise.tool3lgm.graphtools.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Class that manages to collect and serve all informations about elements
 * which should be highlighted in a special way for a selected element.
 * At the moment the highlight reacts only on single selections.
 *
 * @author AXS (05.06.2020)
 */
public class SelectionHighlighter implements LGMChangeListenerSimple {

    /**
     * Contains all {@link ElementContainer} which should be additionally highlighted
     * for a selection (but in another way than the selection itself). This can be used
     * to mark/highlight elements which are (better) connectable a selected element.
     */
    private static final HashSet<ElementContainer> highlightForSelection = new HashSet<>();

    /**
     * @param doc the {@link GraphDocument} this highlighter reacts on selection changed events
     */
    public SelectionHighlighter(final GraphDocument doc) {
        //Should not be added to Template Models because when selecting elements that were created
        //from a template element, this element is always also selected in the Template Browser
        //and this selection then removes the highlight in the model.
        if (doc.getModelCategory() == ModelCategory.REGULAR) {
            if (!(doc instanceof Szenario)) {
                doc.addAllTransactionsListener(this); //add only for the main doc because these listeners are added to the GDCollection
            }
        }
    }

    /**
     *
     */
    private void clearHighlight() {
        for (ElementContainer ec : highlightForSelection) {
            ec.setHighLight(false);
        }
        highlightForSelection.clear();
    }

    @Override
    public void selectionChanged(final GraphDocument doc) {
        clearHighlight();
        //only in the selected doc the highlight must be visible ->
        //so don't run the code in all GraphDocuments
        GDCollection gdcoll = doc.getCollection();
        if (gdcoll.getSelectedDoc() != doc) {
            return;
        }
        if (!doc.isSingleSelection() || doc.isSelectedOnlyBendpoints()) {
            return;
        }
        List<ModelElement> selectedElements = doc.getSelectedElements();
        ModelElement me = selectedElements.get(0);
        Class<? extends ModelElement> meClass = me.getClass();
        MetaModel metaModel = doc.getMetaModel();
        Collection<AbstractMetaPath> bestConnectableMetPath = metaModel.getBestConnectableMetPath(meClass);
        for (AbstractMetaPath metaPath : bestConnectableMetPath) {
            List<ElementContainer> connectedContainer = PathFunctions.getConnectedContainer(me, doc, metaPath);
            addHighlight(connectedContainer);
        }
    }

    /**
     * @param containerToHighlight
     */
    private void addHighlight(final List<ElementContainer> containerToHighlight) {
        highlightForSelection.addAll(containerToHighlight);
        for (ElementContainer ec : containerToHighlight) {
            ec.setHighLight(true);
        }
    }

    /**
     * @param ec {@link ElementContainer} that should be checked if it is highlighted
     * @return <code>true</code> if the given {@link ElementContainer} is/shloud be highlighted
     */
    public boolean isHighlighted(final ElementContainer ec) {
        return highlightForSelection.contains(ec);
    }

}
