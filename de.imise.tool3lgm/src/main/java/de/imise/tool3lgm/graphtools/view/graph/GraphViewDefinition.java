package de.imise.tool3lgm.graphtools.view.graph;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import de.imise.tool3lgm.graphtools.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.TextfeldFach;
import de.imise.tool3lgm.graphtools.elements.TextfeldLog;
import de.imise.tool3lgm.graphtools.elements.TextfeldPhy;

/**
 * @author AXS
 */
public abstract class GraphViewDefinition {

    protected abstract Class<? extends ModelElement>[] getPaintableNodes();

    private final Set<Class<? extends ModelElement>> allPaintableNodes;

    public GraphViewDefinition() {
        ImmutableSet.Builder<Class<? extends ModelElement>> allPaintableNodesSetBuilder = ImmutableSet.<Class<? extends ModelElement>> builder();
        for (Class<? extends ModelElement> paintableNodeClass : getPaintableNodes()) {
            allPaintableNodesSetBuilder.add(paintableNodeClass);
        }
        //Diese Klassen müssen noch hinzugefügt werden, da sie auch dargestellt werden
        allPaintableNodesSetBuilder.add(Knickpunkt.class);
        allPaintableNodesSetBuilder.add(TextfeldFach.class);
        allPaintableNodesSetBuilder.add(TextfeldLog.class);
        allPaintableNodesSetBuilder.add(TextfeldPhy.class);
        allPaintableNodes = allPaintableNodesSetBuilder.build();
    }

    /**
     * Liefert <code>true</code>, wenn die Elementklasse nicht in der Grafik dargestellt wird.
     *
     * @param elementClass
     * @return
     */
    public final boolean isPaintable(final Class<? extends ModelElement> elementClass) {
        return allPaintableNodes.contains(elementClass);
    }

    /**
     * Liefert die Anzahl der nicht dargestellten instanziierbaren Unterklassen von {@link ModelElement}
     *
     * @return
     */
    public final int getPaintableNodesCount() {
        return allPaintableNodes.size();
    }

}
