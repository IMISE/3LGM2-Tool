package de.imise.tool3lgm.graphtools.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.PathFunctions;
import de.imise.tool3lgm.graphtools.path.metapaths.AbstractMetaPath;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Class that manages to collect and serve all informations about elements
 * which should be highlighted in a special way for a selected element.
 * At the moment the highlight reacts only on single selctions.
 *
 * @author AXS (05.06.2020)
 */
public class SelectionHighlighter implements LGMChangeListenerSimple {

    /**
     * Contains all {@link ElementContainer} which should be additionally highlighted
     * for a selection (but in another way than the selection itself). This can be used
     * to mark/highlight elements which are (better) connectable a selected element.
     */
    private final HashSet<ElementContainer> highlightForSelection;

    /**
     * @param doc the {@link GraphDocument} this highlighter reacts on selection changed events
     */
    public SelectionHighlighter(final GraphDocument doc) {
        highlightForSelection = new HashSet<>();
        if (!(doc instanceof Szenario)) {
            doc.addAllTransactionsListener(this); //add only for the main doc because these listeners are added to the GDCollection
        }
    }

    @Override
    public void selectionChanged(final GraphDocument doc) {
        highlightForSelection.clear();
        if (!doc.isSingleSelection() || doc.isSelectedOnlyBendpoints()) {
            return;
        }
        //only in the selected doc the highlight must be visible ->
        //so don't run the code in all GraphDocuments
        GDCollection gdcoll = doc.getCollection();
        if (gdcoll.getSelectedDoc() != doc) {
            return;
        }
        List<ModelElement> selectedElements = doc.getSelectedElements();
        ModelElement me = selectedElements.get(0);
        Class<? extends ModelElement> meClass = me.getClass();
        MetaModel metaModel = doc.getMetaModel();
        Collection<AbstractMetaPath> bestConnectableMetPath = metaModel.getBestConnectableMetPath(meClass);
        for (AbstractMetaPath metaPath : bestConnectableMetPath) {
            List<ElementContainer> connectedContainer = PathFunctions.getConnectedContainer(me, doc, metaPath);
            highlightForSelection.addAll(connectedContainer);
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
