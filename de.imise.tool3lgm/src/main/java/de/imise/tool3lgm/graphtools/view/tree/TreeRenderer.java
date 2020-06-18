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
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode;
import de.imise.tool3lgm.graphtools.view.tree.node.IconifiedTreeNode.IconState;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.util.image.ImageTools;

public class TreeRenderer extends DefaultTreeCellRenderer {

    static ImageIcon edgeIcon_up = Tool3lgmConstants.getIcon("trace2.gif");
    static ImageIcon edgeIcon_down = Tool3lgmConstants.getIcon("trace.gif");
    static ImageIcon edgeIcon_both = Tool3lgmConstants.getIcon("trace1.gif");
    static ImageIcon rectIcon = Tool3lgmConstants.getIcon("knot.gif");
    static ImageIcon circleIcon = Tool3lgmConstants.getIcon("circle.gif");
    static ImageIcon triangleIcon = Tool3lgmConstants.getIcon("triangle.gif");
    static ImageIcon rundeckIcon = Tool3lgmConstants.getIcon("roundeck.gif");
    static ImageIcon rhombusIcon = Tool3lgmConstants.getIcon("raute.gif");
    static ImageIcon tonneIcon = Tool3lgmConstants.getIcon("tonne.gif");
    static ImageIcon wabeIcon = Tool3lgmConstants.getIcon("wabe.gif");
    static ImageIcon dummyIcon = Tool3lgmConstants.getIcon("element.gif");
    static ImageIcon rectErrorIcon = Tool3lgmConstants.getIcon("knot_error.gif");
    //	static ImageIcon errorIcon = Tool3lgmConstants.getIcon("error.gif");
    //	static ImageIcon warningIcon = Tool3lgmConstants.getIcon("warning.gif");

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
            LGMTreeNode node = (LGMTreeNode) value;
            Object userObject = node.getUserObject();

            if (!node.isSelectable()) {
                setTextNonSelectionColor(Color.gray);
            } else if (OPTION_USE_PROPERTY_COLORS.is()) {
                setTextNonSelectionColor(node.getSignalColor());
            } else {
                setTextNonSelectionColor(Color.black);
            }

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            ImageIcon icon = null;
            IconState iconState = null;
            if (node instanceof IconifiedTreeNode) {
                IconifiedTreeNode iconNode = (IconifiedTreeNode) node;
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
                        GraphElementLayout.SHAPE form = me.isPaintable() ? doc.getMapping().getStandardForm(ec) : null;
                        if (form != null) {
                            switch (form) {
                            case rechteck:
                                if (iconState == SHOW_NORMAL_ICON) {
                                    icon = rectIcon;
                                } else if (iconState == SHOW_ERROR_ICON) {
                                    setIcon(rectErrorIcon);
                                }
                                break;
                            case oval:
                                icon = circleIcon;
                                break;
                            case dreieck:
                                icon = triangleIcon;
                                break;
                            case rundeck:
                                icon = rundeckIcon;
                                break;
                            case rhombus:
                                icon = rhombusIcon;
                                break;
                            case tonne:
                                icon = tonneIcon;
                                break;
                            case wabe:
                                icon = wabeIcon;
                                break;
                            default:
                            }
                        }
                    } else {
                        ec.checkTreeIcon();
                    }
                }
            }
            if (icon != null) {
                Dimension preferredSize = getPreferredSize();
                icon = ImageTools.getScaledInstance(icon, preferredSize.height, 10);
            }
            setIcon(icon);
            return this;
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

}
