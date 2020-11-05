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
     * Ligth gree with a touch of blue :)
     */
    private static final Color higlightColor = new Color(0, 250, 150);

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
            ElementContainerTreeNode node = (ElementContainerTreeNode) value;
            ElementContainer ec = node.getUserObject();
            ModelElement me = ec.getElement();
            Collection<AbstractConsistencyError> consistencyErrors = panel.getConsistencyErrors();
            boolean highlight = false;
            for (AbstractConsistencyError consistencyError : consistencyErrors) {
                if (consistencyError instanceof MissingPathError) {
                    MissingPathError missingPathError = (MissingPathError) consistencyError;
                    Collection<ModelElement> missingElements = missingPathError.getMissingElements();
                    if (missingElements.contains(me)) {
                        setFont(higlightFont);
                        highlight = true;
                        break;
                    }
                }
            }
            if (highlight) {
                setFont(higlightFont);
                setTextNonSelectionColor(higlightColor);
                ignoreColor = true;
            } else {
                setFont(standardFont);
                setTextNonSelectionColor(standardColor);
                ignoreColor = false;
            }
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

}
