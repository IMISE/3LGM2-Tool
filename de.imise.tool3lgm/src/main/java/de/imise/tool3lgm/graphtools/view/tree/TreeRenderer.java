package de.imise.tool3lgm.graphtools.view.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.tools.LGMTreeNode;
import de.imise.tool3lgm.userproperties.UserProperties;

public class TreeRenderer extends DefaultTreeCellRenderer {

	static ImageIcon traceIcon_up = Tool3lgmConstants.getIcon("trace2.gif");
	static ImageIcon traceIcon_down = Tool3lgmConstants.getIcon("trace.gif");
	static ImageIcon traceIcon_both = Tool3lgmConstants.getIcon("trace1.gif");
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

	public TreeRenderer(GraphDocument d) {
		doc = d;

		setLeafIcon(null);
		setClosedIcon(null);
		setOpenIcon(null);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		LGMTreeNode node = (LGMTreeNode) value;
		Object userObject = node.getUserObject();

		if (!node.isSelectable()) {
			setTextNonSelectionColor(Color.gray);
		} else if (UserProperties.isUsePropertyColors()) {
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
				GraphElementLayout.SHAPE form = (me.isUnpaintable() ? null : doc.getMapping().getStandardForm(me));
				if (form!=null)
				switch (form) {
				case rechteck:
					if (iconState == LGMTreeNode.SHOW_NORMAL_ICON)
						setIcon(rectIcon);
					else if (iconState == LGMTreeNode.SHOW_ERROR_ICON)
						setIcon(rectErrorIcon);
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
			} else {
				container.checkTreeIcon();
				setIcon(icon);
			}
		}
		return this;
	}

}
