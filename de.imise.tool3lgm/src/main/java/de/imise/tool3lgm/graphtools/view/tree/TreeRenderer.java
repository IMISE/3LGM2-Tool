package de.imise.tool3lgm.graphtools.view.tree;

import static de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState.SHOW_ERROR_ICON;
import static de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState.SHOW_NORMAL_ICON;
import static de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty.OPTION_USE_PROPERTY_COLORS;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.Shape;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.util.image.ImageTools;

public class TreeRenderer extends DefaultTreeCellRenderer {

    private enum TreeIcon {
        knot,
        circle,
        triangle,
        roundeck,
        raute,
        tonne,
        wabe,
        element,
        knot_error,
        //        trace,
        //        trace1,
        //        trace2,
        //        error,
        //        warning
        ;

        private ImageIcon icon;

        private ImageIcon get(final int height) {
            if (icon == null || icon.getIconHeight() != height) {
                icon = Tool3lgmConstants.getIcon(name());
                icon = ImageTools.getScaledInstance(icon, height, 10);
            }
            return icon;
        }

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
                        Shape form = me.isPaintable() ? doc.getDefaultElementsLayout().getStandardForm(ec) : null;
                        Dimension preferredSize = getPreferredSize();
                        int iconHeight = preferredSize.height;
                        if (form != null) {
                            switch (form) {
                            case rechteck:
                                if (iconState == SHOW_NORMAL_ICON) {
                                    icon = TreeIcon.knot.get(iconHeight);
                                } else if (iconState == SHOW_ERROR_ICON) {
                                    icon = TreeIcon.knot_error.get(iconHeight);
                                }
                                break;
                            case oval:
                                icon = TreeIcon.circle.get(iconHeight);
                                break;
                            case dreieck:
                                icon = TreeIcon.triangle.get(iconHeight);
                                break;
                            case rundeck:
                                icon = TreeIcon.roundeck.get(iconHeight);
                                ;
                                break;
                            case rhombus:
                                icon = TreeIcon.raute.get(iconHeight);
                                ;
                                break;
                            case tonne:
                                icon = TreeIcon.tonne.get(iconHeight);
                                ;
                                break;
                            case wabe:
                                icon = TreeIcon.wabe.get(iconHeight);
                                ;
                                break;
                            default:
                            }
                        }
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
