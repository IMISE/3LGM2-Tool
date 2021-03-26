package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState.SHOW_ERROR_ICON;
import static de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState.SHOW_NORMAL_ICON;
import static de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState.SHOW_WARNING_ICON;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_USE_PROPERTY_COLORS;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.collections4.map.HashedMap;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.Shape;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.util.image.ImageTools;

/**
 * @author AXS (< 01.01.2017)
 */
public class TreeRenderer extends DefaultTreeCellRenderer {

    /**
     * Cache for the tree icons to prevent rescaling to the correct height all
     * the time.
     */
    private static final Map<Shape, ImageIcon> shapeToTreeIcon = new HashedMap<>();

    /**
     * Dummy-Icon to mark shapes with no icon image
     */
    private static final ImageIcon DUMMY_ICON = new ImageIcon();

    /**
     * @param shape
     * @param height
     * @param error
     * @return
     */
    private static ImageIcon getTreeIcon(final Shape shape, final int height, final IconState iconState) {
        ImageIcon icon = null;
        if (shape != null) {
            icon = shapeToTreeIcon.get(shape);
            if (icon == null || icon != DUMMY_ICON && icon.getIconHeight() != height) {
                boolean errorIcon = iconState == SHOW_ERROR_ICON;
                boolean warningIcon = iconState == SHOW_WARNING_ICON;
                String iconName = "TREE_ICON_" + shape.name() + (errorIcon ? "_ERROR" : warningIcon ? "_WARNING" : "");
                icon = Tool3lgmConstants.getIcon(iconName);
                if (icon == null && (errorIcon || warningIcon)) { //at the moment an error icon only exists for rectangle (Shape.rechteck) -> fallback normal icon
                    icon = getTreeIcon(shape, height, SHOW_NORMAL_ICON);
                }
                if (icon == null) { // no icon found for this shape in the resources -> store the dummy
                    icon = DUMMY_ICON;
                } else {
                    icon = ImageTools.getScaledInstance(icon, height, 10);
                }
                shapeToTreeIcon.put(shape, icon);
            }
        }
        return icon == DUMMY_ICON ? null : icon;

    }

    /**
     * Standard text color for all rendered trees
     */
    protected static Color standardColor = Color.BLACK;

    /**
     * If set <code>true</code> by an subclass the text color will not be set
     * again by this renderer.
     */
    protected boolean ignoreColor = false;

    /**
     *
     */
    public TreeRenderer() {
        setLeafIcon(null);
        setClosedIcon(null);
        setOpenIcon(null);
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean sel, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
        if (value instanceof LGMTreeNode) {
            LGMTreeNode<?> node = (LGMTreeNode<?>) value;
            Object userObject = node.getUserObject();

            if (!node.isSelectable()) {
                setTextNonSelectionColor(Color.gray);
            } else if (OPTION_USE_PROPERTY_COLORS.is()) {
                if (!ignoreColor) {
                    setTextNonSelectionColor(node.getSignalColor());
                }
            } else if (!ignoreColor) {
                setTextNonSelectionColor(standardColor);
            }

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            ImageIcon icon = (ImageIcon) getIcon(); //if set then it is an ImageIcon!
            IconState iconState = null;
            if (icon == null && node instanceof IconifiedTreeNode) {
                IconifiedTreeNode<?> iconNode = (IconifiedTreeNode<?>) node;
                icon = iconNode.getIcon();
                iconState = iconNode.getIconState();
            }
            if (icon == null) {
                if (userObject instanceof ElementContainer) {
                    ElementContainer ec = (ElementContainer) userObject;
                    icon = ec.getTreeIcon();
                    if (icon == null) {
                        GraphDocument doc = ec.getGraphDocument();
                        ModelElement me = ec.getElement();
                        Shape shape = me.isPaintable() ? doc.getDefaultElementsLayout().getStandardForm(ec) : null;
                        Dimension preferredSize = getPreferredSize();
                        int iconHeight = preferredSize.height;
                        icon = getTreeIcon(shape, iconHeight, iconState);
                    } else {
                        ec.checkTreeIcon();
                    }
                }
            }
            setIcon(icon);
            return this;
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

}
