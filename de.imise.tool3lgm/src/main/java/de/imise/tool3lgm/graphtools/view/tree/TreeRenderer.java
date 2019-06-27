package de.imise.tool3lgm.graphtools.view.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.tree.node.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.userproperties.UserProperties.BooleanProperty;

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

    GraphDocument doc;

    public TreeRenderer(final GraphDocument d) {
        doc = d;

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
            } else if (UserProperties.is(BooleanProperty.OPTION_USE_PROPERTY_COLORS)) {
                setTextNonSelectionColor(node.getSignalColor());
            } else {
                setTextNonSelectionColor(Color.black);
            }

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (userObject instanceof ElementContainer) {
                int iconState = node.getIconState();
                ElementContainer container = (ElementContainer) userObject;
                ImageIcon icon = container.getTreeIcon();
                if (icon == null) {
                    ModelElement me = container.getElement();
                    GraphElementLayout.SHAPE form = me.isPaintable() ? doc.getMapping().getStandardForm(container) : null;
                    if (form != null) {
                        switch (form) {
                        case rechteck:
                            if (iconState == LGMTreeNode.SHOW_NORMAL_ICON) {
                                setIcon(rectIcon);
                            } else if (iconState == LGMTreeNode.SHOW_ERROR_ICON) {
                                setIcon(rectErrorIcon);
                            }
                            break;
                        case oval:
                            setIcon(circleIcon);
                            break;
                        case dreieck:
                            setIcon(triangleIcon);
                            break;
                        case rundeck:
                            setIcon(rundeckIcon);
                            break;
                        case rhombus:
                            setIcon(rhombusIcon);
                            break;
                        case tonne:
                            setIcon(tonneIcon);
                            break;
                        case wabe:
                            setIcon(wabeIcon);
                            break;
                        default:
                            setIcon(null);
                        }
                    }
                } else {
                    container.checkTreeIcon();
                    setIcon(icon);
                }
            }
            return this;
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

}
