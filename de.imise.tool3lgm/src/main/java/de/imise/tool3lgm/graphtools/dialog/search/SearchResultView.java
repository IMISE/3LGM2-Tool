package de.imise.tool3lgm.graphtools.dialog.search;

import java.awt.Component;

import de.imise.tool3lgm.graphtools.model.GraphDocument;

/**
 * @author Ich (23.09.2020)
 */
public interface SearchResultView {

    /**
     * @return
     */
    public default Component getResultViewComponent() {
        return (Component) this;
    }

    /**
     * @param doc
     * @param options
     */
    public void showResult(GraphDocument doc, SearchOptions options);

    /**
     * Calls {@link #showResult(GraphDocument, SearchOptions)} with
     * <code>null</code> for the {@link GraphDocument}. This class
     * must know by its own which model it has to search.
     *
     * @param options
     */
    public default void showResult(final SearchOptions options) {
        showResult(null, options);
    }

}
