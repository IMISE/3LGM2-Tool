/**
 *
 */
package de.imise.tool3lgm.graphtools.view.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Collection;

import javax.swing.JTree;

import de.imise.tool3lgm.graphtools.consistency.error.type.AbstractConsistencyError;
import de.imise.tool3lgm.graphtools.consistency.error.type.MissingPathError;
import de.imise.tool3lgm.graphtools.dialog.element.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.node.ElementContainerTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;

/**
 * @author Ich (29.10.2020)
 */
public class PanelTreeRenderer extends TreeRenderer {

    /**
     *
     */
    private final ElementDialogPanel panel;

    /**
     * Standard tree font
     */
    private static Font standardFont;

    /**
     * Standard tree font in bold
     */
    private static Font higlightFont;

    /**
     * Light green with a touch of blue :)
     */
    private static final Color errorSolutionHighlightColor = new Color(0, 250, 150);

    /**
     * @param panel
     */
    public PanelTreeRenderer(final ElementDialogPanel panel) {
        this.panel = panel;
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        if (value instanceof ElementContainerTreeNode) {
            if (higlightFont == null) {
                standardFont = tree.getFont();
                higlightFont = standardFont.deriveFont(Font.BOLD);
                standardColor = tree.getForeground();
            }

            if (highlightAsErrorSolution(value)) {
                setFont(higlightFont);
                setTextNonSelectionColor(errorSolutionHighlightColor);
                ignoreColor = true;
            } else {
                setFont(standardFont);
                setTextNonSelectionColor(standardColor);
                ignoreColor = false;
            }
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

    /**
     * @param value
     * @return <code>true</code> if the passed object is a {@link LGMTreeNode}
     *         where a {@link MissingPathError} is set, where the element of the
     *         TreeNode is an element to correct the error.
     */
    private boolean highlightAsErrorSolution(final Object value) {
        ElementContainerTreeNode node = (ElementContainerTreeNode) value;
        AbstractConsistencyError consistencyError = node.getConsistencyError();
        if (consistencyError instanceof MissingPathError) {
            MissingPathError missingPathError = (MissingPathError) consistencyError;
            ElementContainer ec = node.getUserObject();
            ModelElement me = ec.getElement();
            Collection<ModelElement> missingElements = missingPathError.getMissingElements();
            if (missingElements.contains(me)) {
                return true;
            }
        }
        return false;
    }

}
